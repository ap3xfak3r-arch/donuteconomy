package net.dsmp.economy.listeners;

import net.dsmp.economy.DonutEconomy;
import net.dsmp.economy.auction.AuctionGUI;
import net.dsmp.economy.auction.AuctionHolder;
import net.dsmp.economy.auction.AuctionListing;
import net.dsmp.economy.shop.ShopEditHolder;
import net.dsmp.economy.shop.ShopHolder;
import net.dsmp.economy.shop.ShopItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GUIClickListener implements Listener {

    private final DonutEconomy plugin;
    private final ShopPriceChatListener priceChat;

    public GUIClickListener(DonutEconomy plugin, ShopPriceChatListener priceChat) {
        this.plugin = plugin;
        this.priceChat = priceChat;
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        Object holder = event.getView().getTopInventory().getHolder();
        if (holder instanceof ShopHolder || holder instanceof ShopEditHolder || holder instanceof AuctionHolder) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory top = event.getView().getTopInventory();
        if (top.getHolder() instanceof ShopHolder) {
            handleShopView(event);
        } else if (top.getHolder() instanceof ShopEditHolder) {
            handleShopEdit(event);
        } else if (top.getHolder() instanceof AuctionHolder) {
            handleAuction(event, (AuctionHolder) top.getHolder());
        }
    }

    private void handleShopView(InventoryClickEvent event) {
        event.setCancelled(true);
        if (event.getClickedInventory() == null || !(event.getClickedInventory().getHolder() instanceof ShopHolder)) return;

        Player player = (Player) event.getWhoClicked();
        ShopItem item = plugin.getShopManager().getItem(event.getSlot());
        if (item == null) return;

        if (event.isLeftClick()) {
            if (item.getBuyPrice() <= 0) {
                player.sendMessage(ChatColor.RED + "This item isn't for sale.");
                return;
            }
            if (!plugin.getEconomyManager().withdraw(player.getUniqueId(), item.getBuyPrice())) {
                player.sendMessage(ChatColor.RED + "You can't afford this.");
                return;
            }
            ItemStack give = new ItemStack(item.getMaterial(), item.getAmount());
            for (ItemStack leftover : player.getInventory().addItem(give).values()) {
                player.getWorld().dropItem(player.getLocation(), leftover);
            }
            plugin.getEconomyManager().save();
            player.sendMessage(ChatColor.GREEN + "Bought " + item.getAmount() + "x " +
                    item.getMaterial().name() + " for $" + String.format("%,.2f", item.getBuyPrice()));
        } else if (event.isRightClick()) {
            if (item.getSellPrice() <= 0) {
                player.sendMessage(ChatColor.RED + "This item can't be sold here.");
                return;
            }
            int have = countMaterial(player, item.getMaterial());
            if (have < item.getAmount()) {
                player.sendMessage(ChatColor.RED + "You need " + item.getAmount() + "x " + item.getMaterial().name());
                return;
            }
            removeMaterial(player, item.getMaterial(), item.getAmount());
            plugin.getEconomyManager().deposit(player.getUniqueId(), item.getSellPrice());
            plugin.getEconomyManager().save();
            player.sendMessage(ChatColor.GREEN + "Sold " + item.getAmount() + "x " +
                    item.getMaterial().name() + " for $" + String.format("%,.2f", item.getSellPrice()));
        }
    }

    private void handleShopEdit(InventoryClickEvent event) {
        event.setCancelled(true);
        if (event.getClickedInventory() == null || !(event.getClickedInventory().getHolder() instanceof ShopEditHolder)) return;

        Player player = (Player) event.getWhoClicked();
        if (!player.hasPermission("donuteconomy.admin")) return;

        int slot = event.getSlot();
        ShopItem existing = plugin.getShopManager().getItem(slot);

        if (existing == null) {
            ItemStack hand = player.getInventory().getItemInMainHand();
            if (hand == null || hand.getType().isAir()) {
                player.sendMessage(ChatColor.RED + "Hold an item in your hand first.");
                return;
            }
            priceChat.awaitPrice(player.getUniqueId(), slot, hand.clone());
            player.closeInventory();
            player.sendMessage(ChatColor.YELLOW + "Type in chat: <buyPrice> <sellPrice>  (0 to disable either, or 'cancel')");
        } else {
            if (event.isLeftClick()) {
                plugin.getShopManager().removeItem(slot);
                player.sendMessage(ChatColor.RED + "Removed item from slot " + slot + ".");
                player.openInventory(plugin.getShopManager().buildEditInventory());
            } else if (event.isRightClick()) {
                ItemStack stack = new ItemStack(existing.getMaterial(), existing.getAmount());
                priceChat.awaitPrice(player.getUniqueId(), slot, stack);
                player.closeInventory();
                player.sendMessage(ChatColor.YELLOW + "Type in chat: <buyPrice> <sellPrice>  (0 to disable either, or 'cancel')");
            }
        }
    }

    private void handleAuction(InventoryClickEvent event, AuctionHolder holder) {
        event.setCancelled(true);
        if (event.getClickedInventory() == null || !(event.getClickedInventory().getHolder() instanceof AuctionHolder)) return;

        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();

        if (slot == 45 && holder.getPage() > 0) {
            player.openInventory(new AuctionGUI(plugin).build(holder.getPage() - 1));
            return;
        }
        if (slot == 53) {
            player.openInventory(new AuctionGUI(plugin).build(holder.getPage() + 1));
            return;
        }
        if (slot == 49) {
            player.closeInventory();
            return;
        }
        if (slot >= AuctionGUI.PAGE_SIZE) return;

        List<AuctionListing> all = new ArrayList<>(plugin.getAuctionManager().getListings());
        int index = holder.getPage() * AuctionGUI.PAGE_SIZE + slot;
        if (index >= all.size()) return;
        AuctionListing listing = all.get(index);

        if (event.isRightClick() && listing.getSeller().equals(player.getUniqueId())) {
            plugin.getAuctionManager().remove(listing.getId());
            for (ItemStack leftover : player.getInventory().addItem(listing.getItem()).values()) {
                player.getWorld().dropItem(player.getLocation(), leftover);
            }
            player.sendMessage(ChatColor.YELLOW + "Listing cancelled and item returned.");
            player.openInventory(new AuctionGUI(plugin).build(holder.getPage()));
            return;
        }

        if (event.isLeftClick()) {
            if (listing.getSeller().equals(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "You can't buy your own listing. Right-click to cancel it.");
                return;
            }
            if (!plugin.getEconomyManager().withdraw(player.getUniqueId(), listing.getPrice())) {
                player.sendMessage(ChatColor.RED + "You can't afford this.");
                return;
            }
            plugin.getEconomyManager().deposit(listing.getSeller(), listing.getPrice());
            plugin.getEconomyManager().save();
            plugin.getAuctionManager().remove(listing.getId());
            for (ItemStack leftover : player.getInventory().addItem(listing.getItem()).values()) {
                player.getWorld().dropItem(player.getLocation(), leftover);
            }
            player.sendMessage(ChatColor.GREEN + "Bought item for $" + String.format("%,.2f", listing.getPrice()));
            player.openInventory(new AuctionGUI(plugin).build(holder.getPage()));
        }
    }

    private int countMaterial(Player player, Material material) {
        int count = 0;
        for (ItemStack stack : player.getInventory().getStorageContents()) {
            if (stack != null && stack.getType() == material) count += stack.getAmount();
        }
        return count;
    }

    private void removeMaterial(Player player, Material material, int amount) {
        ItemStack[] contents = player.getInventory().getStorageContents();
        for (int i = 0; i < contents.length && amount > 0; i++) {
            ItemStack stack = contents[i];
            if (stack != null && stack.getType() == material) {
                int take = Math.min(amount, stack.getAmount());
                stack.setAmount(stack.getAmount() - take);
                if (stack.getAmount() <= 0) contents[i] = null;
                amount -= take;
            }
        }
        player.getInventory().setStorageContents(contents);
    }
}
