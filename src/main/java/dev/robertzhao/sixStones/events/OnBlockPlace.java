package dev.robertzhao.sixStones.events;

import dev.robertzhao.sixStones.buffs.RedstoneBuff;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class OnBlockPlace implements Listener {
    private final JavaPlugin plugin;

    public OnBlockPlace(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        // cancel the placing of the redstone buff item :(
        ItemStack item = event.getItemInHand();
        if(new RedstoneBuff(plugin).get().equals(item)) {
            event.setCancelled(true);
        }
    }
}
