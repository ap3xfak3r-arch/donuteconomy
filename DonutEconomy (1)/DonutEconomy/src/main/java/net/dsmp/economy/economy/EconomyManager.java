package net.dsmp.economy.economy;

import net.dsmp.economy.DonutEconomy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EconomyManager {

    private final DonutEconomy plugin;
    private final File file;
    private FileConfiguration config;
    private final Map<UUID, Double> balances = new HashMap<>();
    private final double startingBalance;

    public EconomyManager(DonutEconomy plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "economy.yml");
        this.startingBalance = plugin.getConfig().getDouble("starting-balance", 500.0);
        load();
    }

    public void load() {
        if (!file.exists()) {
            plugin.getDataFolder().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
        balances.clear();
        if (config.isConfigurationSection("balances")) {
            for (String key : config.getConfigurationSection("balances").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    balances.put(uuid, config.getDouble("balances." + key));
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
    }

    public void save() {
        for (Map.Entry<UUID, Double> entry : balances.entrySet()) {
            config.set("balances." + entry.getKey().toString(), entry.getValue());
        }
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double getBalance(UUID uuid) {
        return balances.getOrDefault(uuid, startingBalance);
    }

    public double getBalance(Player player) {
        return getBalance(player.getUniqueId());
    }

    public void setBalance(UUID uuid, double amount) {
        balances.put(uuid, Math.max(0, round(amount)));
    }

    public boolean has(UUID uuid, double amount) {
        return getBalance(uuid) >= amount;
    }

    public void deposit(UUID uuid, double amount) {
        setBalance(uuid, getBalance(uuid) + amount);
    }

    public boolean withdraw(UUID uuid, double amount) {
        if (!has(uuid, amount)) return false;
        setBalance(uuid, getBalance(uuid) - amount);
        return true;
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
