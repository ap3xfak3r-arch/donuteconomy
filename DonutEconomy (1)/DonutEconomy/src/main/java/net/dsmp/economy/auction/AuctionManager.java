package net.dsmp.economy.auction;

import net.dsmp.economy.DonutEconomy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class AuctionManager {

    private final DonutEconomy plugin;
    private final File file;
    private FileConfiguration config;
    private final Map<UUID, AuctionListing> listings = new LinkedHashMap<>();

    public AuctionManager(DonutEconomy plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "auctions.yml");
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
        listings.clear();
        if (config.isConfigurationSection("listings")) {
            for (String key : config.getConfigurationSection("listings").getKeys(false)) {
                try {
                    UUID id = UUID.fromString(key);
                    String path = "listings." + key + ".";
                    UUID seller = UUID.fromString(config.getString(path + "seller"));
                    String sellerName = config.getString(path + "sellerName");
                    ItemStack item = config.getItemStack(path + "item");
                    double price = config.getDouble(path + "price");
                    long listedAt = config.getLong(path + "listedAt");
                    if (item != null) {
                        listings.put(id, new AuctionListing(id, seller, sellerName, item, price, listedAt));
                    }
                } catch (Exception ignored) {
                }
            }
        }
    }

    public void save() {
        config.set("listings", null);
        for (AuctionListing listing : listings.values()) {
            String path = "listings." + listing.getId() + ".";
            config.set(path + "seller", listing.getSeller().toString());
            config.set(path + "sellerName", listing.getSellerName());
            config.set(path + "item", listing.getItem());
            config.set(path + "price", listing.getPrice());
            config.set(path + "listedAt", listing.getListedAt());
        }
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Collection<AuctionListing> getListings() { return listings.values(); }

    public AuctionListing get(UUID id) { return listings.get(id); }

    public UUID list(Player seller, ItemStack item, double price) {
        UUID id = UUID.randomUUID();
        AuctionListing listing = new AuctionListing(id, seller.getUniqueId(), seller.getName(),
                item.clone(), price, System.currentTimeMillis());
        listings.put(id, listing);
        save();
        return id;
    }

    public void remove(UUID id) {
        listings.remove(id);
        save();
    }
}
