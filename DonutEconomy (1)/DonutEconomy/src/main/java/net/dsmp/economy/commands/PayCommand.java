package net.dsmp.economy.commands;

import net.dsmp.economy.DonutEconomy;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PayCommand implements CommandExecutor {

    private final DonutEconomy plugin;

    public PayCommand(DonutEconomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }
        Player player = (Player) sender;
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /pay <player> <amount>");
            return true;
        }
        OfflinePlayer target = plugin.getServer().getOfflinePlayer(args[0]);
        if (target.getUniqueId().equals(player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You can't pay yourself.");
            return true;
        }
        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Invalid amount.");
            return true;
        }
        if (amount <= 0) {
            sender.sendMessage(ChatColor.RED + "Amount must be positive.");
            return true;
        }
        if (!plugin.getEconomyManager().withdraw(player.getUniqueId(), amount)) {
            sender.sendMessage(ChatColor.RED + "You don't have enough money.");
            return true;
        }
        plugin.getEconomyManager().deposit(target.getUniqueId(), amount);
        plugin.getEconomyManager().save();
        sender.sendMessage(ChatColor.GREEN + "You paid " + ChatColor.YELLOW + target.getName() +
                ChatColor.GREEN + " $" + String.format("%,.2f", amount));
        if (target.isOnline()) {
            ((Player) target).sendMessage(ChatColor.GREEN + "You received $" + String.format("%,.2f", amount) +
                    " from " + player.getName());
        }
        return true;
    }
}
