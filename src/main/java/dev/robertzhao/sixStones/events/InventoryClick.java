/*
    Cover your eyes... this code is totally not readable.
*/

package dev.robertzhao.sixStones.events;

import dev.robertzhao.sixStones.utils.BuffHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedList;

public class InventoryClick implements Listener {
    private final JavaPlugin plugin;
    private LinkedList<ItemStack> allBuffs;

    public InventoryClick(JavaPlugin plugin) {
        this.plugin = plugin;
        // Register the event listener
        Bukkit.getPluginManager().registerEvents(this, plugin);
        allBuffs = BuffHandler.getAllBuffs(plugin);
    }



    /**********************************************EVENT HANDLER**********************************************/

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // check for player
        if(!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        // check for the inventory crafting thing
        if(event.getView().getTopInventory().getType() == InventoryType.CRAFTING) {
            return; // ignore crafting inventories
        }

        // chekc for numbr keys
        if(event.getClick() == ClickType.NUMBER_KEY && event.getAction() == InventoryAction.HOTBAR_SWAP) {
            Inventory topInventory = event.getView().getTopInventory();
            ItemStack itemBeingSwapped = event.getWhoClicked().getInventory().getItem(event.getHotbarButton()); // create a custom itemstack for the item being swapped because spigot is stupid

            // check if the inventory the item is getting swapped to is not a player's inventory
            // and check if the item being swapped is a buff item
            if((topInventory.getType() != InventoryType.PLAYER) && (allBuffs.contains(itemBeingSwapped))) {
                event.setCancelled(true);
                event.getWhoClicked().sendMessage(ChatColor.RESET + "" + ChatColor.YELLOW + "A mysterious force prevents you from placing " + itemBeingSwapped.getItemMeta().getDisplayName() + ChatColor.RESET + ChatColor.YELLOW + " into this container!\n");
                event.getWhoClicked().sendMessage(ChatColor.GRAY + "You cannot use number keys to move buff items into containers!");
                return;
            }
        }

        // check whether the player is PLACING the item to prevent placing in non-player inventories
        if((event.getClickedInventory() != null) && (!event.getCursor().isEmpty()) && (event.getAction() == InventoryAction.PLACE_ALL || event.getAction() == InventoryAction.PLACE_SOME || event.getAction() == InventoryAction.PLACE_ONE) && event.getClickedInventory().getType() != InventoryType.PLAYER) {
            Bukkit.getLogger().info("[SixStones] Player " + event.getWhoClicked().getName() + " tried to place a buff item in a non-player inventory.");
            event.setCancelled(true);
            event.getWhoClicked().sendMessage(ChatColor.RESET + "" + ChatColor.YELLOW + "A mysterious force prevents you from placing " + event.getCursor().getItemMeta().getDisplayName() + ChatColor.RESET + ChatColor.YELLOW + " into this container!\n");
            event.getWhoClicked().sendMessage(ChatColor.GRAY + "You cannot place buff items into containers other than your inventory or offhand!");
        }

        // check for if the item is being clicked into the player's inventory
        if((!event.getCursor().isEmpty()) && (event.getAction() == InventoryAction.PLACE_ALL || event.getAction() == InventoryAction.PLACE_SOME || event.getAction() == InventoryAction.PLACE_ONE) && event.getClickedInventory().getType() == InventoryType.PLAYER) {
            // play the sound effect and stuff
            BuffHandler.playSoundIfInitial(plugin, (Player) event.getWhoClicked(), event.getCursor());
            return;
        }

        // check for whetehr the player is shift clicking
        if((event.getCurrentItem() != null) && (!event.getCurrentItem().isEmpty()) && (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) && (allBuffs.contains(event.getCurrentItem()))) {
            // determine the destination inventory
            Inventory clicked = event.getClickedInventory();
            Inventory top = event.getView().getTopInventory();
            Inventory bottom = event.getView().getBottomInventory();

            Inventory destination = (clicked.equals(top)) ? bottom : top;

            // if the destination is the player's inventory or offhand, allow the action and playt he sound effect n stuff
            if(destination.equals(bottom)) {
                BuffHandler.playSoundIfInitial(plugin, (Player) event.getWhoClicked(), event.getCurrentItem());
                return;
            }

            // otherwise, cancel the event and send a message to the player
            Bukkit.getLogger().info("[SixStones] Player " + event.getWhoClicked().getName() + " tried to shift click a buff item.");
            event.setCancelled(true);
            event.getWhoClicked().sendMessage(ChatColor.RESET + "" + ChatColor.YELLOW + "A mysterious force prevents you from moving " + event.getCurrentItem().getItemMeta().getDisplayName() + ChatColor.RESET + ChatColor.YELLOW + " into this container!\n");
            event.getWhoClicked().sendMessage(ChatColor.GRAY + "You cannot place buff items into containers other than your inventory or offhand!");
        }
    }

}
