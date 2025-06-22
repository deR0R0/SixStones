package dev.robertzhao.sixStones;

import dev.robertzhao.sixStones.commands.End;
import dev.robertzhao.sixStones.commands.GiveAllStones;
import dev.robertzhao.sixStones.commands.Stone;
import dev.robertzhao.sixStones.events.*;
import dev.robertzhao.sixStones.tracker.PlayerTrackerHandler;
import dev.robertzhao.sixStones.tracker.UpdateTrackerHandler;
import dev.robertzhao.sixStones.utils.BuffCheckers;
import dev.robertzhao.sixStones.utils.DataHandler;
import org.bukkit.plugin.java.JavaPlugin;

public final class SixStones extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("SixStones has been enabled!");

        // load the data
        DataHandler.setInstance(new DataHandler(this)); // what even is this??
        DataHandler.getInstance().load();

        // create buff checker
        new BuffCheckers(this);

        // creates handlers
        new ItemDrop(this);
        new InventoryClick(this);
        new OnDeath(this);
        new OnPlayerJoin(this);
        new OnBlockPlace(this);
        new OnItemUse(this);
        new PlayerTrackerHandler(this);
        new UpdateTrackerHandler(this);
        new OnFireworkDamage(this);

        // create command executors
        getCommand("giveallstones").setExecutor(new GiveAllStones(this));
        getCommand("end").setExecutor(new End(this));
        getCommand("stone").setExecutor(new Stone(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("SixStones has been disabled!");
    }
}
