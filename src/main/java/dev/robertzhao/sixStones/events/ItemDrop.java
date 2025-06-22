package dev.robertzhao.sixStones.events;

import dev.robertzhao.sixStones.utils.BuffHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedList;

public class ItemDrop implements Listener {
    private final JavaPlugin plugin;
    private final LinkedList<ItemStack> allBuffs;

    public ItemDrop(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        allBuffs = BuffHandler.getAllBuffs(plugin);
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();

        // check for the type of itembuff
        if(allBuffs.contains(item)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.ITALIC + "" + ChatColor.YELLOW + "A mysterious force prevents you from dropping " + item.getItemMeta().getDisplayName() + ChatColor.RESET + ChatColor.YELLOW + "!");
            event.getPlayer().sendMessage(ChatColor.GRAY + "You cannot drop items that grant buffs!");
            event.getPlayer().sendMessage(ChatColor.GRAY + "Tip! Give buffs to other players via the command: /stone <stone> give <player>");
        }
    }
}
