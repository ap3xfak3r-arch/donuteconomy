package net.dsmp.economy.scoreboard;

import net.dsmp.economy.DonutEconomy;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardManager {

    private final DonutEconomy plugin;

    public ScoreboardManager(DonutEconomy plugin) {
        this.plugin = plugin;
    }

    public void start() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    update(player);
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public void update(Player player) {
        Scoreboard board = player.getScoreboard();
        if (board == null || board == plugin.getServer().getScoreboardManager().getMainScoreboard()) {
            board = plugin.getServer().getScoreboardManager().getNewScoreboard();
            player.setScoreboard(board);
        }

        Objective obj = board.getObjective("dsmp");
        String title = ChatColor.GOLD + "" + ChatColor.BOLD + player.getName();
        if (obj == null) {
            obj = board.registerNewObjective("dsmp", Criteria.DUMMY, title);
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        } else {
            obj.setDisplayName(title);
        }

        for (String entry : board.getEntries()) {
            board.resetScores(entry);
        }

        double balance = plugin.getEconomyManager().getBalance(player.getUniqueId());
        String playtime = plugin.getPlaytimeManager().formatted(player.getUniqueId());

        String topLine = ChatColor.DARK_GRAY + "-----------------";
        String moneyLine = ChatColor.YELLOW + "Money: " + ChatColor.GREEN + "$" + String.format("%,.2f", balance);
        String playtimeLine = ChatColor.YELLOW + "Playtime: " + ChatColor.AQUA + playtime;
        String bottomLine = ChatColor.DARK_GRAY + " ";

        obj.getScore(topLine).setScore(4);
        obj.getScore(moneyLine).setScore(3);
        obj.getScore(playtimeLine).setScore(2);
        obj.getScore(bottomLine).setScore(1);
    }
}
