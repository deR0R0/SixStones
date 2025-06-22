package dev.robertzhao.sixStones.commands;

import dev.robertzhao.sixStones.utils.BuffHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.LinkedList;

public class End implements CommandExecutor {
    private final JavaPlugin plugin;

    public End(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        // check for player
        if(!(commandSender instanceof Player)) {
            Bukkit.getLogger().info("[SixStones] This command can only be executed by a player!");
            return true;
        }

        // get all the buffs
        LinkedList<ItemStack> allBuffs = BuffHandler.getAllBuffs(this.plugin);

        Player player = (Player) commandSender;
        // iterate through the linkedlist of buffs and check if player DOESN"T have the buff (faster)
        for(ItemStack buff : allBuffs) {
            // check if the player DOESN'T have the buff
            if(!player.getInventory().contains(buff)) {
                player.sendMessage(ChatColor.YELLOW + "" + ChatColor.ITALIC + "You attempt to snap, but you're too weak! Come back to this command when you have all 6 stones!");
                return true;
            }
        }

        // got to here? got all the buffs, end the game by banning players

        // online players
        for(Player p : Bukkit.getOnlinePlayers()) {
            p.ban(ChatColor.GOLD + player.getName() + " possesses all the power in this world and has SNAPPED, consequently banning you from the server. \n" + ChatColor.BLUE + "Thank you for playing!", (Date) null, null);
        }

        // offline players
        for(OfflinePlayer p : Bukkit.getOfflinePlayers()) {
            p.ban(ChatColor.GOLD + player.getName() + " possesses all the power in this world and has SNAPPED, consequently banning you from the server. \n" + ChatColor.BLUE + "Thank you for playing!", (Date) null, null);
        }

        return true;
    }
}
