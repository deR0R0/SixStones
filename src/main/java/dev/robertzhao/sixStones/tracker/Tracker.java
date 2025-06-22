package dev.robertzhao.sixStones.tracker;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class Tracker {
    private final JavaPlugin plugin;
    private final NamespacedKey key;

    // constrcutor
    public Tracker(JavaPlugin plugin) {
        this.plugin = plugin;
        this.key = new NamespacedKey(plugin, "tracker"); // create the namespaced key for the tracker
    }

    // get item
    public ItemStack getItem() {
        ItemStack compass = new ItemStack(Material.COMPASS);

        // set meta data of the compass
        ItemMeta meta = compass.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_PURPLE + "" + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.DARK_PURPLE + "Tracker" + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.DARK_PURPLE);
        meta.setLore(List.of(ChatColor.GRAY + "Right Click to rotate through buffs to track"));
        meta.getPersistentDataContainer().set(Tracker.this.key, PersistentDataType.STRING, "tracker");

        // add the enchant effect to the compass
        meta.addEnchant(Enchantment.INFINITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        // set the item meta
        compass.setItemMeta(meta);

        // return the item
        return compass;
    }
}
