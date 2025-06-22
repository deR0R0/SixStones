package dev.robertzhao.sixStones.buffs;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class ItemBuff { // object-oriented programming :) other plugins dont use oop :(
    private final JavaPlugin plugin;

    public ItemBuff(JavaPlugin plugin) {
        // create the key used to identify the correct item
        this.plugin = plugin;
    }

    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    public abstract ItemStack get();
}
