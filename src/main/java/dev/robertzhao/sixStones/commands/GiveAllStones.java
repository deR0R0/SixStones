package dev.robertzhao.sixStones.commands;

import dev.robertzhao.sixStones.buffs.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class GiveAllStones implements CommandExecutor {
    private final JavaPlugin plugin;

    public GiveAllStones(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        // check if the command sender is a player
        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage("This command can only be run by a player.");
            return true;
        }

        Player sender = (Player) commandSender;

        // give player the items
        sender.getInventory().addItem(new DiamondBuff(this.plugin).get());
        sender.getInventory().addItem(new EmeraldBuff(this.plugin).get());
        sender.getInventory().addItem(new GoldBuff(this.plugin).get());
        sender.getInventory().addItem(new IronBuff(this.plugin).get());
        sender.getInventory().addItem(new NetheriteBuff(this.plugin).get());
        sender.getInventory().addItem(new RedstoneBuff(this.plugin).get());

        return true;
    }
}
