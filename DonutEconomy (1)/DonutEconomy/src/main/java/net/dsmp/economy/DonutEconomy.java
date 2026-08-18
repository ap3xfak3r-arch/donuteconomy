package net.dsmp.economy;

import net.dsmp.economy.auction.AuctionManager;
import net.dsmp.economy.commands.*;
import net.dsmp.economy.economy.EconomyManager;
import net.dsmp.economy.economy.PlaytimeManager;
import net.dsmp.economy.listeners.GUIClickListener;
import net.dsmp.economy.listeners.PlayerConnectionListener;
import net.dsmp.economy.listeners.ShopPriceChatListener;
import net.dsmp.economy.scoreboard.ScoreboardManager;
import net.dsmp.economy.shop.ShopManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class DonutEconomy extends JavaPlugin {

    private EconomyManager economyManager;
    private PlaytimeManager playtimeManager;
    private ShopManager shopManager;
    private AuctionManager auctionManager;
    private ScoreboardManager scoreboardManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getDataFolder().mkdirs();

        economyManager = new EconomyManager(this);
        playtimeManager = new PlaytimeManager(this);
        shopManager = new ShopManager(this);
        auctionManager = new AuctionManager(this);
        scoreboardManager = new ScoreboardManager(this);

        ShopPriceChatListener priceChat = new ShopPriceChatListener(this);

        getServer().getPluginManager().registerEvents(new PlayerConnectionListener(this), this);
        getServer().getPluginManager().registerEvents(new GUIClickListener(this, priceChat), this);
        getServer().getPluginManager().registerEvents(priceChat, this);

        getCommand("balance").setExecutor(new BalanceCommand(this));
        getCommand("pay").setExecutor(new PayCommand(this));
        getCommand("sell").setExecutor(new SellCommand(this));
        getCommand("shop").setExecutor(new ShopCommand(this, false));
        getCommand("shopadmin").setExecutor(new ShopCommand(this, true));
        getCommand("auction").setExecutor(new AuctionCommand(this));

        scoreboardManager.start();

        // Tick playtime for everyone online, once per second
        getServer().getScheduler().runTaskTimer(this, () -> {
            for (Player player : getServer().getOnlinePlayers()) {
                playtimeManager.addSecond(player.getUniqueId());
            }
        }, 20L, 20L);

        // Autosave every 5 minutes
        getServer().getScheduler().runTaskTimer(this, () -> {
            economyManager.save();
            playtimeManager.save();
        }, 6000L, 6000L);

        getLogger().info("DonutEconomy enabled!");
    }

    @Override
    public void onDisable() {
        if (economyManager != null) economyManager.save();
        if (playtimeManager != null) playtimeManager.save();
        if (shopManager != null) shopManager.save();
        if (auctionManager != null) auctionManager.save();
        getLogger().info("DonutEconomy disabled!");
    }

    public EconomyManager getEconomyManager() { return economyManager; }
    public PlaytimeManager getPlaytimeManager() { return playtimeManager; }
    public ShopManager getShopManager() { return shopManager; }
    public AuctionManager getAuctionManager() { return auctionManager; }
    public ScoreboardManager getScoreboardManager() { return scoreboardManager; }
}
