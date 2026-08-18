package net.dsmp.economy.shop;

import net.dsmp.economy.DonutEconomy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopManager {

    public static final int SIZE = 54;

    private final DonutEconomy plugin;
    private final File file;
    private FileConfiguration config;
    private final Map<Integer, ShopItem> items = new HashMap<>();

    public ShopManager(DonutEconomy plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "shop.yml");
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
        items.clear();
        if (config.isConfigurationSection("items")) {
            for (String key : config.getConfigurationSection("items").getKeys(false)) {
                try {
                    int slot = Integer.parseInt(key);
                    String path = "items." + key + ".";
                    Material mat = Material.matchMaterial(config.getString(path + "material", "STONE"));
                    int amount = config.getInt(path + "amount", 1);
                    double buy = config.getDouble(path + "buy", -1);
                    double sell = config.getDouble(path + "sell", -1);
                    String name = config.getString(path + "name", "");
                    if (mat != null) {
                        items.put(slot, new ShopItem(slot, mat, amount, buy, sell, name));
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
    }

    public void save() {
        config.set("items", null);
        for (ShopItem item : items.values()) {
            String path = "items." + item.getSlot() + ".";
            config.set(path + "material", item.getMaterial().name());
            config.set(path + "amount", item.getAmount());
            config.set(path + "buy", item.getBuyPrice());
            config.set(path + "sell", item.getSellPrice());
            config.set(path + "name", item.getDisplayName());
        }
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<Integer, ShopItem> getItems() {
        return items;
    }

    public ShopItem getItem(int slot) {
        return items.get(slot);
    }

    public void setItem(int slot, ShopItem item) {
        items.put(slot, item);
        save();
    }

    public void removeItem(int slot) {
        items.remove(slot);
        save();
    }

    public ShopItem findSellable(Material material) {
        for (ShopItem item : items.values()) {
            if (item.getMaterial() == material && item.getSellPrice() > 0) {
                return item;
            }
        }
        return null;
    }

    public Inventory buildViewInventory() {
        Inventory inv = plugin.getServer().createInventory(new ShopHolder(), SIZE,
                ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Shop");
        for (ShopItem item : items.values()) {
            inv.setItem(item.getSlot(), buildDisplayStack(item));
        }
        return inv;
    }

    public Inventory buildEditInventory() {
        Inventory inv = plugin.getServer().createInventory(new ShopEditHolder(), SIZE,
                ChatColor.DARK_RED + "" + ChatColor.BOLD + "Shop Editor");
        for (int slot = 0; slot < SIZE; slot++) {
            ShopItem item = items.get(slot);
            if (item != null) {
                inv.setItem(slot, buildDisplayStack(item));
            } else {
                inv.setItem(slot, buildPlaceholder());
            }
        }
        return inv;
    }

    private ItemStack buildPlaceholder() {
        ItemStack stack = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Empty Slot");
        meta.setLore(Arrays.asList(
                ChatColor.GRAY + "Hold an item and click",
                ChatColor.GRAY + "to add it to the shop."
        ));
        stack.setItemMeta(meta);
        return stack;
    }

    private ItemStack buildDisplayStack(ShopItem item) {
        ItemStack stack = new ItemStack(item.getMaterial(), item.getAmount());
        ItemMeta meta = stack.getItemMeta();
        if (!item.getDisplayName().isEmpty()) {
            meta.setDisplayName(ChatColor.YELLOW + item.getDisplayName());
        }
        List<String> lore = new ArrayList<>();
        if (item.getBuyPrice() > 0) {
            lore.add(ChatColor.GREEN + "Buy: $" + String.format("%,.2f", item.getBuyPrice()) + " (left-click)");
        }
        if (item.getSellPrice() > 0) {
            lore.add(ChatColor.RED + "Sell: $" + String.format("%,.2f", item.getSellPrice()) + " (right-click)");
        }
        meta.setLore(lore);
        stack.setItemMeta(meta);
        return stack;
    }
}
