package net.dsmp.economy.commands;

import net.dsmp.economy.DonutEconomy;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BalanceCommand implements CommandExecutor {

    private final DonutEconomy plugin;

    public BalanceCommand(DonutEconomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Console must specify a player: /balance <player>");
                return true;
            }
            Player player = (Player) sender;
            double bal = plugin.getEconomyManager().getBalance(player.getUniqueId());
            sender.sendMessage(ChatColor.YELLOW + "Your balance: " + ChatColor.GREEN + "$" + String.format("%,.2f", bal));
            return true;
        }
        OfflinePlayer target = plugin.getServer().getOfflinePlayer(args[0]);
        double bal = plugin.getEconomyManager().getBalance(target.getUniqueId());
        sender.sendMessage(ChatColor.YELLOW + target.getName() + "'s balance: " + ChatColor.GREEN + "$" + String.format("%,.2f", bal));
        return true;
    }
}
