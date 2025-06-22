package dev.robertzhao.sixStones.utils;

import dev.robertzhao.sixStones.buffs.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class BuffHandler {
    // Class Used to handle all the buff objects, so i dont have to repeat code :(
    public static LinkedList<ItemStack> getAllBuffs(JavaPlugin plugin) {
        LinkedList<ItemStack> allBuffs = new LinkedList<>();

        // add all the buffs to the linked list
        allBuffs.add(new DiamondBuff(plugin).get());
        allBuffs.add(new EmeraldBuff(plugin).get());
        allBuffs.add(new GoldBuff(plugin).get());
        allBuffs.add(new IronBuff(plugin).get());
        allBuffs.add(new NetheriteBuff(plugin).get());
        allBuffs.add(new RedstoneBuff(plugin).get());

        return allBuffs;
    }

    public static ItemStack[] getAllBuffsArray(JavaPlugin plugin) {
        LinkedList<ItemStack> allBuffs = getAllBuffs(plugin);
        return allBuffs.toArray(new ItemStack[0]);
    }
    
    public static HashMap<String, ItemStack> getAllBuffsCodeName(JavaPlugin plugin) {
        HashMap<String, ItemStack> nameToBuff = new HashMap<>();

        nameToBuff.put("dazzling_diamond", new DiamondBuff(plugin).get());
        nameToBuff.put("enigmatic_emerald", new EmeraldBuff(plugin).get());
        nameToBuff.put("gleaming_gold", new GoldBuff(plugin).get());
        nameToBuff.put("important_iron", new IronBuff(plugin).get());
        nameToBuff.put("notable_netherite", new NetheriteBuff(plugin).get());
        nameToBuff.put("rapid_redstone", new RedstoneBuff(plugin).get());

        return nameToBuff;
    }

    public static HashMap<ItemStack, String> getCodeNameByBuff(JavaPlugin plugin) {
        HashMap<ItemStack, String> buffToName = new HashMap<>();
        HashMap<String, ItemStack> nameToBuff = getAllBuffsCodeName(plugin);

        for (String name : nameToBuff.keySet()) {
            buffToName.put(nameToBuff.get(name), name);
        }

        return buffToName;
    }

    private static boolean checkFirstTime(JavaPlugin plugin, Player player, ItemStack buff) {
        // check if the player has a section first
        if(!DataHandler.getInstance().getConfig().isConfigurationSection("playSound." + player.getUniqueId())) {
            // doesn't have a section, create one and set all sounds to false
            DataHandler.getInstance().getConfig().createSection("playSound." + player.getUniqueId());
            for(String codename : BuffHandler.getAllBuffsCodeName(plugin).keySet()) {
                DataHandler.getInstance().getConfig().set("playSound." + player.getUniqueId() + "." + codename, false);
            }
        }

        // has a section? check for the specific item sound thing
        HashMap<ItemStack, String> buffToName = BuffHandler.getCodeNameByBuff(plugin);
        String codename = buffToName.get(buff);
        return !DataHandler.getInstance().getConfig().getBoolean("playSound." + player.getUniqueId() + "." + codename);
    }

    public static void soundAndTitle(Player player, ItemStack buff) {
        // play the sound
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1, 1);

        // show a title to the player
        String displayName = buff.getItemMeta().getDisplayName();
        String lore = "";
        try {
            lore = buff.getItemMeta().getLore().get(0);
        } catch(Exception e) {
            Bukkit.getLogger().severe("[SixStones] Error getting lore for buff: " + displayName + ". Please check the item meta.");
            return; // skip if there's an error
        }
        player.sendTitle(ChatColor.WHITE + "Acquired " + displayName, ChatColor.GRAY + lore, 20, 60, 20);

        // send a message to the player
        player.sendMessage(ChatColor.LIGHT_PURPLE + "You feel a surge of power as you acquire " + displayName + ChatColor.LIGHT_PURPLE + ". But... you feel like something is tracking you now...");
        player.sendMessage(ChatColor.GRAY + "Players are now able to track your location. Be careful!");
    }

    public static void playSoundIfInitial(JavaPlugin plugin, Player player, ItemStack buff) {
        // check if the player has already had the sound played before
        if(!checkFirstTime(plugin, player, buff)) {
            return; // dont play the sound
        }

        // save the data
        DataHandler.getInstance().getConfig().set("playSound." + player.getUniqueId() + "." + BuffHandler.getCodeNameByBuff(plugin).get(buff), true);
        DataHandler.getInstance().save();

        soundAndTitle(player, buff);
    }

    public static boolean checkForStonePresence(JavaPlugin plugin, ItemStack buff) {
        // using the list of players we've saved in the data handler, check if any of them have the buff
        List<String> playerUUIDs = DataHandler.getInstance().getConfig().getStringList("players");
        for (String uuid : playerUUIDs) {
            Player player = Bukkit.getPlayer(UUID.fromString(uuid));

            // offline or doesn't exist
            if(player == null) {
                continue;
            }

            // check if the player has the buff in their inventory
            if(player.getInventory().contains(buff) || player.getInventory().getItemInMainHand().equals(buff)) {
                return true;
            }
        }

        // nothing found: return false
        return false;
    }
}
