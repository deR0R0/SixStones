package dev.robertzhao.sixStones.events;

import dev.robertzhao.sixStones.tracker.Tracker;
import dev.robertzhao.sixStones.utils.BuffHandler;
import dev.robertzhao.sixStones.utils.DataHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class OnItemUse implements Listener {
    private final JavaPlugin plugin;

    public OnItemUse(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onItemUse(PlayerInteractEvent event) {
        // on item use of the compass

        // check for the right click
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) {
            return; // right click is the action we want to handle
        }

        // check for the compass and meta
        if(event.getItem() == null || !event.getItem().equals(new Tracker(this.plugin).getItem())) {
            return; // not a compass
        }

        // cycle through the stuff

        // get the current tracker for the player
        String curr = DataHandler.getInstance().getConfig().getString("trackers." + event.getPlayer().getUniqueId());

        // if the current tracker is null, set it to the first one
        if (curr == null) {
            curr = "dazzling_diamond"; // default to the first stone
        }

        // get the list of trackers
        String[] trackers = BuffHandler.getAllBuffsCodeName(this.plugin).keySet().toArray(new String[0]);

        // if the current tracker is the last one, set it to the first one
        if(curr == trackers[trackers.length - 1]) {
            DataHandler.getInstance().getConfig().set("trackers." + event.getPlayer().getUniqueId(), "dazzling_diamond"); // reset to the first stone
        } else {
            // otherwise, set it to the next one
            for (int i = 0; i < trackers.length; i++) {
                if (trackers[i].equals(curr)) {
                    DataHandler.getInstance().getConfig().set("trackers." + event.getPlayer().getUniqueId(), trackers[i+1]); // set to the next stone
                    break;
                }
            }
        }

        DataHandler.getInstance().save(); // save the config

        event.getPlayer().sendMessage(ChatColor.GOLD + "[SixStones] " + ChatColor.GREEN + "Now Tracking: " + ChatColor.YELLOW + BuffHandler.getAllBuffsCodeName(this.plugin).get(DataHandler.getInstance().getConfig().getString("trackers." + event.getPlayer().getUniqueId())).getItemMeta().getDisplayName());
    }
}
