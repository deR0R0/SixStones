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

public class GoldBuff extends ItemBuff{
    private final NamespacedKey key;

    public GoldBuff(JavaPlugin plugin) {
        super(plugin);
        key = new NamespacedKey(plugin, "gold_buff");
    }

    public ItemStack get() {
        ItemStack item = new ItemStack(Material.GOLD_INGOT);
        ItemMeta meta = item.getItemMeta();

        // create the metadata (name, lore, and namespacekey) for the item
        meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.GOLD + " Gleaming Gold " + ChatColor.MAGIC + "a");
        meta.setLore(List.of(ChatColor.GRAY + "Grants you Haste (I)"));
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "Gold");

        // add the cool glint effect
        meta.addEnchant(Enchantment.INFINITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        // set metadata
        item.setItemMeta(meta);

        // return the item with the metadata
        return item;
    }
}
