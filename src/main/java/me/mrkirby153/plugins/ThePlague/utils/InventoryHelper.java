package me.mrkirby153.plugins.ThePlague.utils;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class InventoryHelper {
    private static HashMap<String, ItemStack[]> playerItems = new HashMap<String, ItemStack[]>();
    private static HashMap<String, ItemStack[]> playerArmour = new HashMap<String, ItemStack[]>();

    private static HashMap<String, Integer> playerHunger = new HashMap<String, Integer>();
    private static HashMap<String, GameMode> playerGameMode = new HashMap<String, GameMode>();
    private static HashMap<String, Float> playerXp = new HashMap<String, Float>();
    private static HashMap<String, Integer> playerXpLvl = new HashMap<String, Integer>();

    /**
     * Loads the player data from the hashmaps
     * @param player The player object
     */
    public static void load(Player player) {
        ItemStack[] inventory = playerItems.remove(player.getName());
        if (inventory != null)
            player.getInventory().setContents(inventory);
        else
            player.getInventory().clear();
        ItemStack[] armour = playerArmour.get(player.getName());
        if (armour != null)
            player.getInventory().setArmorContents(playerArmour.get(player.getName()));
        else
            player.getInventory().setArmorContents(new ItemStack[4]);
        player.updateInventory();
        if(playerXpLvl.get(player.getName()) != null){
            player.setLevel(playerXpLvl.remove(player.getName()));
        } else {
            player.setLevel(0);
        }
        if(playerXp.get(player.getName()) != null){
            player.setExp(playerXp.remove(player.getName()));
        } else {
            player.setExp(0);
        }
        if(playerHunger.get(player.getName()) != null){
            player.setFoodLevel(playerHunger.remove(player.getName()));
        } else {
            player.setFoodLevel(10);
        }
        if(player.getGameMode() != playerGameMode.get(player.getName()))
            player.setGameMode(playerGameMode.remove(player.getName()));

    }


    /**
     * Saves the player data
     * @param player The player object to save
     */
    public static void save(Player player) {
        ItemStack[] original = player.getInventory().getContents();
        ItemStack[] copy = new ItemStack[original.length];
        for (int i = 0; i < original.length; i++) {
            if (original[i] != null) {
                copy[i] = new ItemStack(original[i]);
            }
        }
        playerItems.put(player.getName(), copy);
        ItemStack[] armourOrig = player.getInventory().getArmorContents();
        ItemStack[] armourCopy = new ItemStack[armourOrig.length];
        for (int i = 0; i < armourOrig.length; i++) {
            if (armourOrig[i] != null)
                armourCopy[i] = new ItemStack(armourOrig[i]);
        }
        playerArmour.put(player.getName(), armourCopy);
        player.getInventory().setArmorContents(new ItemStack[4]);
        player.getInventory().clear();
        player.updateInventory();
        playerXpLvl.put(player.getName(), player.getLevel());
        playerXp.put(player.getName(), player.getExp());
        player.setLevel(0);
        player.setExp(0);
        playerHunger.put(player.getName(), player.getFoodLevel());
        playerGameMode.put(player.getName(), player.getGameMode());
    }
}
