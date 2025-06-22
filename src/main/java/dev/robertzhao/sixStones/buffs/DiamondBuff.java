package dev.robertzhao.sixStones.buffs;

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

public class DiamondBuff extends ItemBuff {
    private final NamespacedKey key;

    public DiamondBuff(JavaPlugin plugin) {
        super(plugin); // call superclass constructor to set the plugin
        this.key = new NamespacedKey(getPlugin(), "diamond_buff"); // create the namespaced key for the diamond buff
    }

    public ItemStack get() {
        ItemStack diamond = new ItemStack(Material.DIAMOND);
        ItemMeta meta = diamond.getItemMeta();

        // create the metadata (name, lore, and namespacekey) for the diamond item
        meta.setDisplayName(ChatColor.AQUA + "" + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.AQUA + " Dazzling Diamond " + ChatColor.MAGIC + "a");
        meta.setLore(List.of(ChatColor.GRAY + "Grants you Health Boost (5 hearts)"));
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "Diamond");

        // add the cool glint effect
        meta.addEnchant(Enchantment.INFINITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        // set metadata
        diamond.setItemMeta(meta);

        // return the item with the metadata
        return diamond;
    }
}
