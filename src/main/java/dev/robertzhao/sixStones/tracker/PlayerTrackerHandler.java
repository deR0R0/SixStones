/*
    This class is responsible for handling the tracking of the players with the buff in their inventory.
*/

package dev.robertzhao.sixStones.tracker;

import dev.robertzhao.sixStones.utils.BuffHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.LinkedList;

public class PlayerTrackerHandler {
    private static PlayerTrackerHandler INSTANCE;
    private final JavaPlugin plugin;
    private final ItemStack[] buffs;
    private static HashMap<ItemStack, Location> buffLastLocation = new HashMap<>();

    public PlayerTrackerHandler(JavaPlugin plugin) {
        INSTANCE = this;
        this.plugin = plugin;
        this.buffs = BuffHandler.getAllBuffsArray(plugin);
        update();
        createRunnable();
    }

    public static HashMap<ItemStack, Location> getBuffLastLocation() {
        return buffLastLocation;
    }

    public void update() {
        // clear list
        buffLastLocation.clear();

        // for each player online, check if they have the buff items
        for(Player player : Bukkit.getOnlinePlayers()) {
            for(ItemStack buff : buffs) {
                // if the player has the item, update their location
                if(player.getInventory().contains(buff)) {
                    buffLastLocation.put(buff, player.getLocation());
                }
            }
        }
    }

    private void createRunnable() {
        new BukkitRunnable() {
            @Override
            public void run() {
                update();
            }
        }.runTaskTimer(this.plugin, 0L, 200L); // player position updates every 10 seconds
    }

    public static PlayerTrackerHandler getInstance() {
        return INSTANCE;
    }
}
