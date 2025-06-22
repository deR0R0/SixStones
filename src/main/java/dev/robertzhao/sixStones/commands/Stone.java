package dev.robertzhao.sixStones.commands;

import dev.robertzhao.sixStones.buffs.*;
import dev.robertzhao.sixStones.tracker.PlayerTrackerHandler;
import dev.robertzhao.sixStones.tracker.Tracker;
import dev.robertzhao.sixStones.tracker.UpdateTrackerHandler;
import dev.robertzhao.sixStones.utils.BuffHandler;
import dev.robertzhao.sixStones.utils.DataHandler;
import dev.robertzhao.sixStones.utils.Tuple;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Stone implements CommandExecutor, TabExecutor {
    private JavaPlugin plugin;
    private HashMap<String, ItemStack> nameToBuff;

    public Stone(JavaPlugin plugin) {
        this.plugin = plugin;
        nameToBuff = BuffHandler.getAllBuffsCodeName(plugin);
    }

    /**********************************************CHECK INVENTORY HAS EMPTY SLOT**********************************************/

    private boolean checkInventoryHasEmptySlot(Inventory inventory) {
        for(ItemStack item : inventory.getContents()) {
            if(item == null || item.getType().isAir()) {
                return true; // found an empty slot
            }
        }

        return false; // no empty slots found
    }

    /**********************************************TRANSFER STONE**********************************************/

    private String transferStone(Player sender, Player target, ItemStack item) {
        try {
            // check for the stone
            if (!sender.getInventory().contains(item)) {
                return ChatColor.RED + "You do not have this stone in your inventory.";
            }

            // check whether the target has a empty spot in their inventory
            if (!checkInventoryHasEmptySlot(target.getInventory())) {
                return ChatColor.RED + "The target player does not have an empty slot in their inventory.";
            }

            // transfer the stone
            sender.getInventory().remove(item);
            target.getInventory().addItem(item);
            BuffHandler.soundAndTitle(target, item);

            // update the tracker if the target player has a tracker
            PlayerTrackerHandler.getInstance().update();
            UpdateTrackerHandler.getInstance().update(target);

            return ChatColor.GREEN + "Successfully transferred " + item.getItemMeta().getDisplayName() + ChatColor.RESET + ChatColor.GREEN + " to " + target.getName() + ".";
        } catch (Exception e) {
            Bukkit.getLogger().severe("[SixStones] An error occurred while transferring the stone: " + e.getMessage());
            return ChatColor.RED + "Internal Error. Please contact server owner.";
        }
    }


    /**********************************************DROP STONE**********************************************/

    private int getYOfAirWithBlockBelow(World world, int x, int z) {
        int worldMaxY = world.getMaxHeight();
        int worldMinY = world.getMinHeight();

        // start from the world height, and decrease height until the block below is solid
        for(int y = worldMaxY; y > worldMinY; y--) {
            Material block = world.getBlockData(x, y, z).getMaterial();
            Material below = world.getBlockData(x, y - 1, z).getMaterial();

            if(block.isAir() && !below.isAir()) {
                return y; // found an air block with a solid block below it
            }
        }

        return 0; // no suitable air block found, just spawn it at 0 :(
    }

    private void spawnChestWithItem(ItemStack item, Location loc) {
        // spawn a chest at the location
        loc.getBlock().setType(Material.CHEST);
        Chest chest = (Chest) loc.getBlock().getState();
        chest.getInventory().setItem(13, item); // place the item in the middle of the chest
    }

    // this method is for dropping the stone within the world borders
    private Tuple<String, Location> dropStone(ItemStack item) {
        // just call the other method lol
        Tuple<String, Location> response = dropStone(item, (int) Bukkit.getWorlds().getFirst().getWorldBorder().getSize() / 2);
        return response;
    }

    // this method is for dropping the stone within user-specified borders
    private Tuple<String, Location> dropStone(ItemStack item, int border) {
        // get world size
        int worldSize = border;

        // randomly generate a location within the world borders
        int x = (int) (Math.random() * worldSize);
        int z = (int) (Math.random() * worldSize);
        int y = getYOfAirWithBlockBelow(Bukkit.getWorlds().getFirst(), x, z);

        // spawn a chest at the location
        Location loc = new Location(Bukkit.getWorlds().getFirst(), x, y, z);
        spawnChestWithItem(item, loc);

        // return a success message
        return new Tuple(ChatColor.GREEN + "Successfully Dropped " + item.getItemMeta().getDisplayName() + ChatColor.RESET + ChatColor.GREEN + " at " + x + ", " + y + ", " + z + ".", loc);
    }

    // this method is for dropping the stone within user-specific borders with restrictions
    private Tuple<String, Location> dropStone(ItemStack item, int border, int exclude) { // exclude coords will be: exclude (x) X exclude (z)
        // while loop to find a valid location
        int x, y, z;
        do {
            x = (int) (Math.random() * border);
            z = (int) (Math.random() * border);
            y = getYOfAirWithBlockBelow(Bukkit.getWorlds().getFirst(), x, z);
        } while (x <= exclude || z <= exclude); // keep generating until we find a valid location

        // spawn a chest at location
        Location location = new Location(Bukkit.getWorlds().getFirst(), x, y, z);
        spawnChestWithItem(item, location);

        // return success message
        return new Tuple(ChatColor.GREEN + "Successfully Dropped " + item.getItemMeta().getDisplayName() + ChatColor.RESET + ChatColor.GREEN + " at " + x + ", " + y + ", " + z + ".", location);
    }


    /**********************************************RECOVER STONE**********************************************/
    private String recoverStone(Player player, String stoneName) {
        // check for the stone presence on the server
        if(BuffHandler.checkForStonePresence(plugin, nameToBuff.get(stoneName))) {
            return ChatColor.RED + "A player has this stone in their inventory. You cannot recover it.";
        }

        // no presence? give the stone to the player

        // check for empty slot in the player's inventory
        if(!checkInventoryHasEmptySlot(player.getInventory())) {
            return ChatColor.RED + player.getName() + " does not have an empty slot in their inventory.";
        }

        // now, just give the item to the player
        player.getInventory().addItem(nameToBuff.get(stoneName));
        BuffHandler.soundAndTitle(player, nameToBuff.get(stoneName));

        return ChatColor.GREEN + "Successfully recovered " + nameToBuff.get(stoneName).getItemMeta().getDisplayName() + ChatColor.RESET + ChatColor.GREEN + " and added it to player's inventory.";
    }


    /**********************************************LOCATE STONE**********************************************/
    private String locateStone(Player player, String stoneName) {
        // check if the player already has a compass
        ItemStack compass = new Tracker(this.plugin).getItem();

        // has the compass? set it to the stone they're referencing
        if(player.getInventory().contains(compass)) {
            // they already have the compass? set the thing they're tracking!
            DataHandler.getInstance().getConfig().set("trackers." + player.getUniqueId(), stoneName);
            DataHandler.getInstance().save();

            // manually update their compass target
            UpdateTrackerHandler.getInstance().update(player);

            // return the response
            return ChatColor.GREEN + "Compass Now Tracking: " + nameToBuff.get(stoneName).getItemMeta().getDisplayName() + ChatColor.RESET + ChatColor.GREEN + ".";
        }

        // doesn't have the compass? give it to them
        if(!checkInventoryHasEmptySlot(player.getInventory())) {
            return ChatColor.RED + "You do not have an empty slot in your inventory to receive the compass.";
        }

        // give the compass to the player
        DataHandler.getInstance().getConfig().set("trackers." + player.getUniqueId(), stoneName);
        DataHandler.getInstance().save();
        player.getInventory().addItem(compass);
        UpdateTrackerHandler.getInstance().update(player);

        return ChatColor.GREEN + "You have received a tracker. Currently tracking: " + nameToBuff.get(stoneName).getItemMeta().getDisplayName() + ChatColor.RESET + ChatColor.GREEN + ".";
    }



    /**********************************************COMMAND HANDLER**********************************************/

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        // check for player
        if(!(commandSender instanceof Player)) {
            Bukkit.getLogger().info("[SixStones] This command can only be run by players.");
            return true;
        }

        Player player = (Player) commandSender;

        // check for correct arg length
        if(args.length <= 1) {
            player.sendMessage(ChatColor.GOLD + "[SixStones]" + ChatColor.RED + " Incorrect Usage. Correct Usage: /stone <stone> <locate / drop / recover / give>");
            return true;
        }

        // check for correct stone
        if(!nameToBuff.containsKey(args[0])) {
            player.sendMessage(ChatColor.GOLD + "[SixStones] " + ChatColor.RED + args[0] + " is not a valid stone.");
            return true;
        }

        // determine the action the player is doing
        switch(args[1]) {
            case "give" -> {
                // check for correct amount of args
                if(args.length < 3) {
                    player.sendMessage(ChatColor.GOLD + "[SixStones] " + ChatColor.RED + "Incorrect Usage. Correct Usage: /stone <stone> give <player>");
                    return true;
                }

                // call the method and get the response
                String response = transferStone(player, Bukkit.getPlayer(args[2]), nameToBuff.get(args[0]));
                player.sendMessage(ChatColor.GOLD + "[SixStones] " + response);

                if(response.contains("Successfully")) {
                    Bukkit.getPlayer(args[2]).sendMessage(ChatColor.GOLD + "[SixStones] " + ChatColor.GREEN + "You have received a " + nameToBuff.get(args[0]).getItemMeta().getDisplayName() + ChatColor.RESET + ChatColor.GREEN + " from " + player.getName() + ".");
                }

                return true;
            }
            case "drop" -> {
                // check for correct amount of args
                if(args.length != 2 && args.length != 3 && args.length != 4) { // 2 args = world border, 3 args = specify include , 4 args = specify include and exclude
                    player.sendMessage(ChatColor.GOLD + "[SixStones] " + ChatColor.RED + "Incorrect Usage. Correct Usage: /stone <stone> drop");
                    return true;
                }

                // choose correct method to call based on amount of args
                Tuple<String, Location> response = null;
                switch(args.length) {
                    case 2 -> { // /stone <stone> drop
                        response = dropStone(nameToBuff.get(args[0]));
                    }
                    case 3 -> { // /stone <stone> drop [include]
                        try {
                            response = dropStone(nameToBuff.get(args[0]), Integer.parseInt(args[2]));
                        } catch (NumberFormatException e) {
                            player.sendMessage(ChatColor.GOLD + "[SixStones] " + ChatColor.RED + "Invalid number format for include. Please enter a valid number.");
                            return true;
                        }
                    }
                    case 4 -> {
                        try {
                            response = dropStone(nameToBuff.get(args[0]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
                        } catch (NumberFormatException e) {
                            player.sendMessage(ChatColor.GOLD + "[SixStones] " + ChatColor.RED + "Invalid number format for include or exclude. Please enter valid numbers.");
                            return true;
                        }
                    }
                }

                // send the response to the player
                if(response.x != null) {
                    player.sendMessage(ChatColor.GOLD + "[SixStones] " + response.x);
                } else {
                    player.sendMessage(ChatColor.GOLD + "[SixStones] " + ChatColor.RED + "An error occurred while dropping the stone. Please contact server owner.");
                    return true;
                }

                // based on the location, we want to give an approximate location to the players

                // get the location of the chest
                int x, y, z = 0;
                x = (int) response.y.getX();
                y = (int) response.y.getY();
                z = (int) response.y.getZ();

                // generate a random numbers between -75 and 75, which will be the offset x and z (no offset y because we want it to be on the surface)
                int offsetX = (int) (Math.random() * 150) - 75; // random number between -75 and 75
                int offsetZ = (int) (Math.random() * 150) - 75; // random number between -75 and 75

                // send the title to players
                String buffItemName = nameToBuff.get(args[0]).getItemMeta().getDisplayName();

                player.sendTitle(buffItemName.substring(0, buffItemName.lastIndexOf("§ka")) + "Has Spawned §ka", ChatColor.GRAY + "Approximate Location: " + (x + offsetX) + ", " + y + ", " + (z + offsetZ), 20, 60, 20);

                return true;
            }
            case "recover" -> {
                // check for correct amount of args
                if(args.length != 3) {
                    player.sendMessage(ChatColor.GOLD + "[SixStones] " + ChatColor.RED + "Incorrect Usage. Correct Usage: /stone <stone> recover <player>");
                    return true;
                }

                // attempt to get the player
                Player targetPlayer = Bukkit.getPlayer(args[2]);
                if(targetPlayer == null) {
                    player.sendMessage(ChatColor.GOLD + "[SixStones] " + ChatColor.RED + "Player " + args[2] + " is not online.");
                    return true;
                }

                // player online and exists, call the method and get the response
                String response = recoverStone(targetPlayer, args[0]);
                player.sendMessage(ChatColor.GOLD + "[SixStones] " + response);

                return true;
            }
            case "locate" -> {
                // check for correct amount of args
                if(args.length != 2) {
                    player.sendMessage(ChatColor.GOLD + "[SixStones] " + ChatColor.RED + "Incorrect Usage. Correct Usage: /stone <stone> locate");
                    return true;
                }

                // call the method and get the response
                String response = locateStone(player, args[0]);
                player.sendMessage(ChatColor.GOLD + "[SixStones] " + response);

                return true;
            }
            default -> {
                player.sendMessage(ChatColor.GOLD + "[SixStones] " + ChatColor.RED + "Incorrect Usage. Correct Usage: /stone <stone> <locate / drop / recover / give>");
                return true;
            }
        }

        //return true;
    }


    /**********************************************TAB COMPLETION HANDLER**********************************************/

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        switch(args.length) {
            case 1 -> {
                // return all the buff names
                return nameToBuff.keySet().stream().toList();
            }
            case 2 -> {
                // return the possible actions
                ArrayList<String> result = new ArrayList<>();
                result.add("locate");
                result.add("give");
                if(commandSender.hasPermission("sixstones.drop")) {
                    result.add("drop");
                }
                if(commandSender.hasPermission("sixstones.recover")) {
                    result.add("recover");
                }
                return result.stream().toList();
            }
            case 3 -> {
                // figure out what the player is trying to do
                switch(args[1]) {
                    case "locate", "give", "recover" -> {
                        // create an arraylist of all online players
                        ArrayList<String> players = new ArrayList<>();

                        // add all online player names to the list
                        for(Player player: Bukkit.getOnlinePlayers()) {
                            players.add(player.getName());
                        }

                        // return the list of players
                        return players.stream().toList();
                    }
                    case "drop" -> {
                        return List.of("<include>");
                    }
                }
            }
            case 4 -> {
                // only case where there is 4 args is:
                // /stone <stone> drop [include] [exclude]
                return List.of("<exclude>");
            }
        }

        return null;
    }
}
