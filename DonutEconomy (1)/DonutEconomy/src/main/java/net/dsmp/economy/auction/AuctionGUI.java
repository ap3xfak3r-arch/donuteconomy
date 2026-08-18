package net.dsmp.economy.auction;

import net.dsmp.economy.DonutEconomy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class AuctionGUI {

    public static final int PAGE_SIZE = 45; // slots 0-44 hold listings, 45-53 are controls

    private final DonutEconomy plugin;

    public AuctionGUI(DonutEconomy plugin) {
        this.plugin = plugin;
    }

    public Inventory build(int page) {
        List<AuctionListing> all = new ArrayList<>(plugin.getAuctionManager().getListings());
        int totalPages = Math.max(1, (int) Math.ceil(all.size() / (double) PAGE_SIZE));
        page = Math.max(0, Math.min(page, totalPages - 1));

        Inventory inv = plugin.getServer().createInventory(new AuctionHolder(page), 54,
                ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Auction House - Page " + (page + 1));

        int start = page * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, all.size());
        for (int i = start; i < end; i++) {
            AuctionListing listing = all.get(i);
            inv.setItem(i - start, buildDisplayStack(listing));
        }

        if (page > 0) inv.setItem(45, navItem(Material.ARROW, ChatColor.YELLOW + "Previous Page"));
        if (page < totalPages - 1) inv.setItem(53, navItem(Material.ARROW, ChatColor.YELLOW + "Next Page"));
        inv.setItem(49, navItem(Material.BARRIER, ChatColor.RED + "Close"));

        return inv;
    }

    private ItemStack buildDisplayStack(AuctionListing listing) {
        ItemStack stack = listing.getItem().clone();
        ItemMeta meta = stack.getItemMeta();
        List<String> lore = new ArrayList<>();
        if (meta.hasLore() && meta.getLore() != null) {
            lore.addAll(meta.getLore());
        }
        lore.add(" ");
        lore.add(ChatColor.GRAY + "Seller: " + ChatColor.WHITE + listing.getSellerName());
        lore.add(ChatColor.GRAY + "Price: " + ChatColor.GREEN + "$" + String.format("%,.2f", listing.getPrice()));
        lore.add(ChatColor.YELLOW + "Left-click to buy");
        lore.add(ChatColor.RED + "Right-click to cancel (owner only)");
        meta.setLore(lore);
        stack.setItemMeta(meta);
        return stack;
    }

    private ItemStack navItem(Material mat, String name) {
        ItemStack stack = new ItemStack(mat);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(name);
        stack.setItemMeta(meta);
        return stack;
    }
}
