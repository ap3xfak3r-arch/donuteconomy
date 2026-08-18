package net.dsmp.economy.auction;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class AuctionListing {
    private final UUID id;
    private final UUID seller;
    private final String sellerName;
    private final ItemStack item;
    private final double price;
    private final long listedAt;

    public AuctionListing(UUID id, UUID seller, String sellerName, ItemStack item, double price, long listedAt) {
        this.id = id;
        this.seller = seller;
        this.sellerName = sellerName;
        this.item = item;
        this.price = price;
        this.listedAt = listedAt;
    }

    public UUID getId() { return id; }
    public UUID getSeller() { return seller; }
    public String getSellerName() { return sellerName; }
    public ItemStack getItem() { return item; }
    public double getPrice() { return price; }
    public long getListedAt() { return listedAt; }
}
