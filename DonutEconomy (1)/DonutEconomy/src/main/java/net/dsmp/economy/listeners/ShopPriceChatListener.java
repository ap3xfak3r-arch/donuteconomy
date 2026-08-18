package net.dsmp.economy.listeners;

import net.dsmp.economy.DonutEconomy;
import net.dsmp.economy.shop.ShopItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShopPriceChatListener implements Listener {

    private final DonutEconomy plugin;
    private final Map<UUID, Integer> pendingSlot = new HashMap<>();
    private final Map<UUID, ItemStack> pendingStack = new HashMap<>();

    public ShopPriceChatListener(DonutEconomy plugin) {
        this.plugin = plugin;
    }

    public void awaitPrice(UUID admin, int slot, ItemStack stack) {
        pendingSlot.put(admin, slot);
        pendingStack.put(admin, stack);
    }

    public boolean isPending(UUID uuid) {
        return pendingSlot.containsKey(uuid);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (!pendingSlot.containsKey(uuid)) return;
        event.setCancelled(true);

        String message = event.getMessage().trim();
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (message.equalsIgnoreCase("cancel")) {
                pendingSlot.remove(uuid);
                pendingStack.remove(uuid);
                event.getPlayer().sendMessage(ChatColor.RED + "Cancelled.");
                return;
            }
            String[] parts = message.split(" ");
            if (parts.length != 2) {
                event.getPlayer().sendMessage(ChatColor.RED +
                        "Please type two numbers: <buyPrice> <sellPrice> (0 to disable either). Or type 'cancel'.");
                return;
            }
            double buy, sell;
            try {
                buy = Double.parseDouble(parts[0]);
                sell = Double.parseDouble(parts[1]);
            } catch (NumberFormatException e) {
                event.getPlayer().sendMessage(ChatColor.RED + "Invalid numbers. Try again or type 'cancel'.");
                return;
            }

            int slot = pendingSlot.remove(uuid);
            ItemStack stack = pendingStack.remove(uuid);

            String name = (stack.hasItemMeta() && stack.getItemMeta().hasDisplayName())
                    ? stack.getItemMeta().getDisplayName() : "";

            ShopItem item = new ShopItem(slot, stack.getType(), stack.getAmount(), buy, sell, name);
            plugin.getShopManager().setItem(slot, item);

            event.getPlayer().sendMessage(ChatColor.GREEN + "Shop slot updated!");
            event.getPlayer().openInventory(plugin.getShopManager().buildEditInventory());
        });
    }
}
