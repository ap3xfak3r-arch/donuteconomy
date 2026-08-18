package net.dsmp.economy.economy;

import net.dsmp.economy.DonutEconomy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlaytimeManager {

    private final DonutEconomy plugin;
    private final File file;
    private FileConfiguration config;
    private final Map<UUID, Long> playtimeSeconds = new HashMap<>();

    public PlaytimeManager(DonutEconomy plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "playtime.yml");
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
        playtimeSeconds.clear();
        if (config.isConfigurationSection("playtime")) {
            for (String key : config.getConfigurationSection("playtime").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    playtimeSeconds.put(uuid, config.getLong("playtime." + key));
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
    }

    public void save() {
        for (Map.Entry<UUID, Long> entry : playtimeSeconds.entrySet()) {
            config.set("playtime." + entry.getKey().toString(), entry.getValue());
        }
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long getSeconds(UUID uuid) {
        return playtimeSeconds.getOrDefault(uuid, 0L);
    }

    public void addSecond(UUID uuid) {
        playtimeSeconds.put(uuid, getSeconds(uuid) + 1);
    }

    public String formatted(UUID uuid) {
        long total = getSeconds(uuid);
        long h = total / 3600;
        long m = (total % 3600) / 60;
        return h + "h " + m + "m";
    }
}
