package net.dsmp.economy.shop;

import org.bukkit.Material;

public class ShopItem {
    private final int slot;
    private final Material material;
    private final int amount;
    private double buyPrice;  // <= 0 means not buyable
    private double sellPrice; // <= 0 means not sellable
    private final String displayName;

    public ShopItem(int slot, Material material, int amount, double buyPrice, double sellPrice, String displayName) {
        this.slot = slot;
        this.material = material;
        this.amount = amount;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.displayName = displayName == null ? "" : displayName;
    }

    public int getSlot() { return slot; }
    public Material getMaterial() { return material; }
    public int getAmount() { return amount; }
    public double getBuyPrice() { return buyPrice; }
    public double getSellPrice() { return sellPrice; }
    public String getDisplayName() { return displayName; }
    public void setBuyPrice(double buyPrice) { this.buyPrice = buyPrice; }
    public void setSellPrice(double sellPrice) { this.sellPrice = sellPrice; }
}
