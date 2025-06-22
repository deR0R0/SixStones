package dev.robertzhao.sixStones.events;

import dev.robertzhao.sixStones.utils.BuffHandler;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedList;

public class OnDeath implements Listener {
    private final JavaPlugin plugin;
    private final LinkedList<ItemStack> allBuffs;

    public OnDeath(JavaPlugin plugin) {
        this.plugin = plugin;
        // Register the event listener
        org.bukkit.Bukkit.getPluginManager().registerEvents(this, plugin);
        allBuffs = BuffHandler.getAllBuffs(this.plugin);
    }

    private void spawnFireworks(Location location) {
        NamespacedKey key = new NamespacedKey(plugin, "harmless_fireworks");
        Firework deathFirework = (Firework) location.getWorld().spawn(location.add(0, 3, 0), Firework.class);

        // add the key to the firework to prevent it from doing damage later
        deathFirework.getPersistentDataContainer().set(key, PersistentDataType.STRING, "harmless_fireworks");

        // meta for the custom firework
        FireworkMeta fireworkMeta = deathFirework.getFireworkMeta();
        FireworkEffect fireworkEffect = FireworkEffect.builder()
                .withColor(Color.ORANGE)
                .withFade(Color.BLUE)
                .with(FireworkEffect.Type.CREEPER)
                .withFlicker()
                .withTrail()
                .build();

        fireworkMeta.addEffect(fireworkEffect);
        fireworkMeta.setPower(2); // set the power of the firework
        deathFirework.setFireworkMeta(fireworkMeta);

    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Location deathLocation = player.getLocation();

        /******************************REMOVING THE BUFFS AND PLACING IN CHEST******************************/

        // check for the buff item in the inventory
        LinkedList<ItemStack> playerBuffs = new LinkedList<>();
        for(ItemStack buff : allBuffs) {
            if(player.getInventory().contains(buff)) {
                playerBuffs.add(buff);
                player.getInventory().removeItem(buff); // remove from inventory to prevent duplication
                event.getDrops().remove(buff); // remove from death drops to prevent duplication
            }
        }

        if(playerBuffs.isEmpty()) {
            return;
        }

        // check if the death cause is the void, if it is, we set the death location to the same x and z coords but y = 0
        if(event.getEntity().getLastDamageCause() != null && event.getEntity().getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.VOID) {
            deathLocation.setY(0);
        }

        // create a chest at the death location
        deathLocation.getBlock().setType(Material.CHEST);
        Block deathBlock = deathLocation.getBlock();

        // add the buffs items that once belonged to the player into the chest
        // we do this to prevent the loss of the buff when the player commits death on cactus/void/whatever other stuff
        if(deathBlock.getState() instanceof Chest chest) {
            Inventory chestInv = chest.getInventory();
            for(ItemStack item : playerBuffs) {
                chestInv.addItem(item);
            }
        }

        // spawn fireworks at the death location
        spawnFireworks(deathLocation);
    }
}
