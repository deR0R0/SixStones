/*
    Comments in DiamondBuff/ItemBuff.java. This is basically the same as diamondbuff
*/

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

public class NetheriteBuff extends ItemBuff {
    private final NamespacedKey key;

    public NetheriteBuff(JavaPlugin plugin) {
        super(plugin);
        key = new NamespacedKey(plugin, "netherite_buff");
    }

    public ItemStack get() {
        ItemStack item = new ItemStack(Material.NETHERITE_INGOT);
        ItemMeta meta = item.getItemMeta();

        // create the metadata (name, lore, and namespacekey) for the item
        meta.setDisplayName(ChatColor.DARK_GRAY + "" + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.DARK_GRAY + " Notable Netherite " + ChatColor.MAGIC + "a");
        meta.setLore(List.of(ChatColor.GRAY + "Grants you Strength (II)"));
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "Netherite");

        // add the cool glint effect
        meta.addEnchant(Enchantment.INFINITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        // set metadata
        item.setItemMeta(meta);

        // return the item with the metadata
        return item;
    }
}
