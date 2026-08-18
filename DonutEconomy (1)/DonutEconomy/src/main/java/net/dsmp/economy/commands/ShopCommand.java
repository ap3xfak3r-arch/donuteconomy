package net.dsmp.economy.commands;

import net.dsmp.economy.DonutEconomy;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShopCommand implements CommandExecutor {

    private final DonutEconomy plugin;
    private final boolean editMode;

    public ShopCommand(DonutEconomy plugin, boolean editMode) {
        this.plugin = plugin;
        this.editMode = editMode;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }
        Player player = (Player) sender;
        if (editMode) {
            if (!player.hasPermission("donuteconomy.admin")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission.");
                return true;
            }
            player.openInventory(plugin.getShopManager().buildEditInventory());
        } else {
            player.openInventory(plugin.getShopManager().buildViewInventory());
        }
        return true;
    }
}
