package net.dsmp.economy.commands;

import net.dsmp.economy.DonutEconomy;
import net.dsmp.economy.auction.AuctionGUI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AuctionCommand implements CommandExecutor {

    private final DonutEconomy plugin;

    public AuctionCommand(DonutEconomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }
        Player player = (Player) sender;

        if (args.length == 0) {
            player.openInventory(new AuctionGUI(plugin).build(0));
            return true;
        }

        if (args[0].equalsIgnoreCase("sell")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /auction sell <price>");
                return true;
            }
            double price;
            try {
                price = Double.parseDouble(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Invalid price.");
                return true;
            }
            if (price <= 0) {
                sender.sendMessage(ChatColor.RED + "Price must be positive.");
                return true;
            }
            ItemStack hand = player.getInventory().getItemInMainHand();
            if (hand == null || hand.getType().isAir()) {
                sender.sendMessage(ChatColor.RED + "You must hold an item to list.");
                return true;
            }
            plugin.getAuctionManager().list(player, hand, price);
            player.getInventory().setItemInMainHand(null);
            sender.sendMessage(ChatColor.GREEN + "Listed " + hand.getAmount() + "x " + hand.getType().name() +
                    " for $" + String.format("%,.2f", price));
            return true;
        }

        sender.sendMessage(ChatColor.RED + "Usage: /auction [sell <price>]");
        return true;
    }
}
