package dev.robertzhao.sixStones.events;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class OnFireworkDamage implements Listener {
    private final JavaPlugin plugin;
    private final NamespacedKey key;

    public OnFireworkDamage(JavaPlugin plugin) {
        this.plugin = plugin;
        this.key = new NamespacedKey(plugin, "harmless_fireworks");
        // Register the event listener
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onFireworkDamage(EntityDamageByEntityEvent event) {
        // check for firework entitty
        if(event.getDamager() instanceof Firework) {
            Firework firework = (Firework) event.getDamager();
            // check if the firework has the key
            if(firework.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                // cancel the damage
                event.setCancelled(true);
            }
        }
    }
}
