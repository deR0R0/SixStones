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

public class EmeraldBuff extends ItemBuff{
    private final NamespacedKey key;

    public EmeraldBuff(JavaPlugin plugin) {
        super(plugin);
        key = new NamespacedKey(plugin, "emerald_buff");
    }

    public ItemStack get() {
        ItemStack item = new ItemStack(Material.EMERALD);
        ItemMeta meta = item.getItemMeta();

        // create the metadata (name, lore, and namespacekey) for the item
        meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.GREEN + " Enigmatic Emerald " + ChatColor.MAGIC + "a");
        meta.setLore(List.of(ChatColor.GRAY + "Grants you Regeneration (I)"));
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "Emerald");

        // add the cool glint effect
        meta.addEnchant(Enchantment.INFINITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        // set metadata
        item.setItemMeta(meta);

        // return the item with the metadata
        return item;
    }
}
