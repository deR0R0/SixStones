package dev.robertzhao.sixStones.events;

import dev.robertzhao.sixStones.tracker.PlayerTrackerHandler;
import dev.robertzhao.sixStones.utils.DataHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class OnPlayerJoin implements Listener {
    private final JavaPlugin plugin;

    public OnPlayerJoin(JavaPlugin plugin) {
        this.plugin = plugin;
        // Register the event listener
        org.bukkit.Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    // this is used for player tracking! (for the recover command)
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // check if the player already in there
        if(DataHandler.getInstance().getConfig().getStringList("players").contains(event.getPlayer().getUniqueId().toString())) {
            return;
        }

        // add them to the list
        List<String> playerUUIDs = DataHandler.getInstance().getConfig().getStringList("players");
        playerUUIDs.add(event.getPlayer().getUniqueId().toString());
        DataHandler.getInstance().getConfig().set("players", playerUUIDs);
        DataHandler.getInstance().save();

        // unrelated thing, but we need to update the tracker for the player
        PlayerTrackerHandler.getInstance().update();
    }
}
