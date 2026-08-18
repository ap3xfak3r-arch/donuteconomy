package net.dsmp.economy.commands;

import net.dsmp.economy.DonutEconomy;
import net.dsmp.economy.shop.ShopItem;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class SellCommand implements CommandExecutor {

    private final DonutEconomy plugin;

    public SellCommand(DonutEconomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }
        Player player = (Player) sender;
        PlayerInventory inv = player.getInventory();

        if (args.length == 0 || args[0].equalsIgnoreCase("hand")) {
            ItemStack hand = inv.getItemInMainHand();
            if (hand == null || hand.getType().isAir()) {
                sender.sendMessage(ChatColor.RED + "You aren't holding anything.");
                return true;
            }
            ShopItem shopItem = plugin.getShopManager().findSellable(hand.getType());
            if (shopItem == null) {
                sender.sendMessage(ChatColor.RED + "This item can't be sold.");
                return true;
            }
            int amount = hand.getAmount();
            double total = (shopItem.getSellPrice() / shopItem.getAmount()) * amount;
            inv.setItemInMainHand(null);
            plugin.getEconomyManager().deposit(player.getUniqueId(), total);
            plugin.getEconomyManager().save();
            sender.sendMessage(ChatColor.GREEN + "Sold " + amount + "x " + hand.getType().name() +
                    " for $" + String.format("%,.2f", total));
            return true;
        }

        if (args[0].equalsIgnoreCase("all")) {
            double total = 0;
            int count = 0;
            ItemStack[] contents = inv.getStorageContents();
            for (int i = 0; i < contents.length; i++) {
                ItemStack stack = contents[i];
                if (stack == null || stack.getType().isAir()) continue;
                ShopItem shopItem = plugin.getShopManager().findSellable(stack.getType());
                if (shopItem == null) continue;
                double price = (shopItem.getSellPrice() / shopItem.getAmount()) * stack.getAmount();
                total += price;
                count += stack.getAmount();
                contents[i] = null;
            }
            inv.setStorageContents(contents);
            if (count == 0) {
                sender.sendMessage(ChatColor.RED + "You have nothing sellable in your inventory.");
                return true;
            }
            plugin.getEconomyManager().deposit(player.getUniqueId(), total);
            plugin.getEconomyManager().save();
            sender.sendMessage(ChatColor.GREEN + "Sold " + count + " items for $" + String.format("%,.2f", total));
            return true;
        }

        sender.sendMessage(ChatColor.RED + "Usage: /sell <hand|all>");
        return true;
    }
}
