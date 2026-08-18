package net.dsmp.economy.auction;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class AuctionHolder implements InventoryHolder {
    private Inventory inventory;
    private final int page;

    public AuctionHolder(int page) {
        this.page = page;
    }

    public int getPage() { return page; }

    @Override
    public Inventory getInventory() { return inventory; }

    public void setInventory(Inventory inventory) { this.inventory = inventory; }
}
