package net.dsmp.economy.listeners;

import net.dsmp.economy.DonutEconomy;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener implements Listener {

    private final DonutEconomy plugin;

    public PlayerConnectionListener(DonutEconomy plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        plugin.getScoreboardManager().update(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getEconomyManager().save();
        plugin.getPlaytimeManager().save();
    }
}
