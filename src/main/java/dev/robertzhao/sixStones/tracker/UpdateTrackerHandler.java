/*
    The purpose of this class is to handle updating the compasses the players have in their inventories.
*/

package dev.robertzhao.sixStones.tracker;

import dev.robertzhao.sixStones.utils.BuffHandler;
import dev.robertzhao.sixStones.utils.DataHandler;
import dev.robertzhao.sixStones.utils.Tuple;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.LinkedList;

public class UpdateTrackerHandler {
    private static UpdateTrackerHandler instance;
    private static final HashMap<Integer, Tuple<Integer, Integer>> stoneToOffset = new HashMap<>();
    private final JavaPlugin plugin;

    public UpdateTrackerHandler(JavaPlugin plugin) {
        this.plugin = plugin;
        instance = this;
        createStoneToOffsetMap();
        createRunnable();
    }

    private void createStoneToOffsetMap() {
        // Initialize the stone to offset map
        stoneToOffset.put(1, new Tuple<>(90, 45)); // 1 Stone = 90 Blocks ± 45 blocks
        stoneToOffset.put(2, new Tuple<>(70, 35)); // 2 Stones = 70 Blocks ± 35 blocks
        stoneToOffset.put(3, new Tuple<>(50, 25)); // 3 Stones = 50 Blocks ± 25 blocks
        stoneToOffset.put(4, new Tuple<>(30, 15)); // 4 Stones = 30 Blocks ± 15 blocks
        stoneToOffset.put(5, new Tuple<>(10, 5));   // 5 Stones = 10 Blocks ± 5 blocks
    }

    private Location getRoundedLocation(ItemStack buffItem) {
        // get all the location of the buff items
        HashMap<ItemStack, Location> buffLocations = PlayerTrackerHandler.getBuffLastLocation();

        // get the location of the buff item
        Location buffLocation = buffLocations.get(buffItem);
        if(buffLocation == null) {
            return null; // if the location is null, return null
        }

        // check for duplicate locations. duplicate location will be treated as
        // the player having the buff item in their inventory. this will result
        // in the approx. location be more accurate.
        LinkedList<Location> sameLoc = new LinkedList<>();
        for (Location loc : buffLocations.values()) {
            if(buffLocation.getBlockX() == buffLocation.getBlockX() && buffLocation.getBlockZ() == buffLocation.getBlockZ()) {
                sameLoc.add(loc); // should only add one location if a player has one buff item.
            }
        }

        // based on how many buff items are in the same location, we can determine
        // what the offset should be.

        // get the offset based on the number of buff items in the same location
        int buffCount = sameLoc.size();
        Tuple<Integer, Integer> offset = stoneToOffset.getOrDefault(buffCount, new Tuple<>(0, 0));

        // randomly generate an offset based on the offset values
        // we dont need to generate a y value because the y value will be obfuscated.
        int offsetX = (int) (Math.random() * offset.x - offset.y);
        int offsetZ = (int) (Math.random() * offset.x - offset.y);

        // create a new location with the offset
        Location approxLocation = new Location(buffLocation.getWorld(), buffLocation.getX()+offsetX, buffLocation.getY(), buffLocation.getZ()+offsetZ);

        return approxLocation;
    }

    public void update(Player player) {
        // doesn't have the tracker item, skip
        if (!player.getInventory().contains(new Tracker(plugin).getItem())) {
            return;
        }

        // has the item? check for what buff item they're tracking
        if (!DataHandler.getInstance().getConfig().isString("trackers." + player.getUniqueId())) {
            // set the initial tracker item
            DataHandler.getInstance().getConfig().set("trackers." + player.getUniqueId(), "none"); // Player: "none", tracking no one
            DataHandler.getInstance().save();
        }

        // get the buff item they're tracking
        ItemStack item;
        try {
            item = BuffHandler.getAllBuffsCodeName(plugin).get(DataHandler.getInstance().getConfig().getString("trackers." + player.getUniqueId()));
        } catch (Exception e) {
            plugin.getLogger().severe(e.getMessage());
            // set it to none because its probs a corruption
            DataHandler.getInstance().getConfig().set("trackers." + player.getUniqueId(), "none");
            DataHandler.getInstance().save();
            return;
        }

        // item exists? set the compass target to the last known location of the buff item
        if (item != null) {
            try {
                Location location = getRoundedLocation(item);

                // if the location is null, set it to the player's location
                player.setCompassTarget(player.getLocation());

                // set action bar message
                if (location != null) {
                    player.setCompassTarget(location);
                    if(player.getInventory().getItemInMainHand().equals(new Tracker(plugin).getItem()) || player.getInventory().getItemInOffHand().equals(new Tracker(plugin).getItem())) {
                        // get the world and its name
                        // also get the color of the world
                        World world = location.getWorld();
                        String worldName = "Overworld";
                        ChatColor worldColor = ChatColor.GREEN;
                        if (world != null && world.getName().equals("world_nether")) {
                            worldColor = ChatColor.RED; // Nether
                            worldName = "Nether";
                        } else if (world != null && world.getName().equals("world_the_end")) {
                            worldColor = ChatColor.DARK_PURPLE; // End
                            worldName = "End";
                        }
                        // send the action bar message
                        player.sendActionBar(ChatColor.GOLD + "Tracking: " + item.getItemMeta().getDisplayName() + ChatColor.GRAY + " at " + ChatColor.YELLOW + location.getBlockX() + ", " + ChatColor.MAGIC + location.getBlockY() + ChatColor.RESET + ChatColor.YELLOW + ", " + location.getBlockZ() + ChatColor.GRAY + " in the " + worldColor + worldName);
                    }
                } else {
                    if(player.getInventory().getItemInMainHand().equals(new Tracker(plugin).getItem()) || player.getInventory().getItemInOffHand().equals(new Tracker(plugin).getItem())) {
                        player.sendActionBar(ChatColor.GOLD + "No location found for tracking item.");
                    }
                }
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to set compass target for player " + player.getName() + ": " + e.getMessage());
            }
        }
    }

    private void createRunnable() {
        new BukkitRunnable() {
            public void run() {
                // for each player online
                for (Player player : Bukkit.getOnlinePlayers()) {
                    update(player);
                }
            }
        }.runTaskTimer(this.plugin, 0L, 20L); // update every second
    }

    public static UpdateTrackerHandler getInstance() {
        return instance;
    }
}
