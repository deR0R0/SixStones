package dev.robertzhao.sixStones.utils;

import dev.robertzhao.sixStones.buffs.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.LinkedList;

public class BuffCheckers {
    private final JavaPlugin plugin;
    private final LinkedList<ItemStack> items;

    private final HashMap<ItemStack, PotionEffect> buffToEffect = new HashMap<>();

    public BuffCheckers(JavaPlugin plugin) {
        this.plugin = plugin;
        items = BuffHandler.getAllBuffs(this.plugin);
        setBuffToEffect();
        createChecker();
    }

    private void setBuffToEffect() {
        buffToEffect.put(new DiamondBuff(this.plugin).get(), new PotionEffect(PotionEffectType.HEALTH_BOOST, 40, 2 )); // duration in ticks, 40 ticks = 2 seconds. health boost 2, 5 hearts
        buffToEffect.put(new NetheriteBuff(this.plugin).get(), new PotionEffect(PotionEffectType.STRENGTH, 40, 1 )); // strength 2
        buffToEffect.put(new IronBuff(this.plugin).get(), new PotionEffect(PotionEffectType.RESISTANCE, 40, 1 )); // resistance 2
        buffToEffect.put(new GoldBuff(this.plugin).get(), new PotionEffect(PotionEffectType.HASTE, 40, 1)); // haste 2
        buffToEffect.put(new RedstoneBuff(this.plugin).get(), new PotionEffect(PotionEffectType.SPEED, 40, 0)); // speed 1
        buffToEffect.put(new EmeraldBuff(this.plugin).get(), new PotionEffect(PotionEffectType.REGENERATION, 40, 1)); // regen 2
    }

    private void createChecker() {
        // create a bukkit repeating task that runs every 20 ticks (1 second)
        new BukkitRunnable() {
            @Override
            public void run() {
                // for each player online, check if they have the buff items
                for(Player player : Bukkit.getOnlinePlayers()) {
                    for(ItemStack item : items) {
                        if(player.getInventory().contains(item) || player.getInventory().getItemInOffHand().equals(item)) {
                            player.addPotionEffect(buffToEffect.get(item));
                            // check for the extra effect applied by the redstone buff
                            if(item.getType() == Material.REDSTONE) {
                                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 40, 0 )); // jump boost 1
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(this.plugin, 0L, 20L);
    }
}
