package dev.robertzhao.sixStones.utils;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class DataHandler {
    // instance of the DataHandler
    private static DataHandler dataHandler;

    private File file;
    private YamlConfiguration config;
    private JavaPlugin plugin;

    // constructing the constructor :D
    public DataHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    // load load load load the stupid config
    public void load() {
        this.file = new File(plugin.getDataFolder(), "data.yml");

        // check for the file
        if(!file.exists()) {
            plugin.saveResource("data.yml", false);
        }

        // set the config for the config
        this.config = new YamlConfiguration();
        this.config.options().parseComments(false);

        // load the config
        try {
            this.config.load(file);
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to load data.yml: " + e.getMessage());
        }
    }

    // wowow save config
    public void save() {
        try {
            config.save(this.file);
        } catch (Exception err) {
            plugin.getLogger().severe("Failed to save data.yml: " + err.getMessage());
        }
    }

    // get the config
    public YamlConfiguration getConfig() {
        return config;
    }

    // set instance
    public static void setInstance(DataHandler dh) {
        dataHandler = dh;
    }

    // get the instance
    public static DataHandler getInstance() {
        return dataHandler;
    }
}
