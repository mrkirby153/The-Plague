package me.mrkirby153.plugins.ThePlague.utils;

import me.mrkirby153.plugins.ThePlague.ThePlague;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;

public class InventorySaver {

    public static void savePlayerInventory(Player player) {
        File file = new File(ThePlague.instance().getDataFolder(), "players" + File.separator + player.getUniqueId().toString() + ".yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            file.delete();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileConfiguration cfgFile = YamlConfiguration.loadConfiguration(file);
        ItemStack[] inventory = player.getInventory().getContents();
        for (int i = 0; i < inventory.length; i++) {
            cfgFile.set("inventory." + Integer.toString(i), inventory[i]);
        }
        ItemStack[] armour = player.getInventory().getArmorContents();
        for (int i = 0; i < armour.length; i++) {
            cfgFile.set("armour." + Integer.toString(i), armour[i]);
        }
        cfgFile.set("msc.food", player.getFoodLevel());
        cfgFile.set("msc.xpLevels", player.getLevel());
        cfgFile.set("msc.xp", player.getExp());
        try {
            cfgFile.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadPlayerInventory(Player player) {
        ItemStack[] item = player.getInventory().getContents();
        ItemStack[] inventory = new ItemStack[item.length];
        for (int i = 0; i < item.length; i++) {
            inventory[i] = item[i];
        }
        ItemStack[] arm = player.getInventory().getArmorContents();
        ItemStack[] currentArmour = new ItemStack[arm.length];
        for (int i = 0; i < arm.length; i++) {
            currentArmour[i] = arm[i];
        }
        player.getInventory().setArmorContents(new ItemStack[4]);
        player.getInventory().clear();
        File file = new File(ThePlague.instance().getDataFolder(), "players" + File.separator + player.getUniqueId().toString() + ".yml");
        if (!file.exists())
            return;
        FileConfiguration cfgFile = YamlConfiguration.loadConfiguration(file);
        for (String s : cfgFile.getConfigurationSection("inventory").getKeys(false)) {
            player.getInventory().setItem(Integer.parseInt(s), (ItemStack) cfgFile.get("inventory." + s));
        }
        String[] keys = cfgFile.getConfigurationSection("armour").getKeys(false).toArray(new String[0]);
        ItemStack[] armour = new ItemStack[keys.length];
        for (int i = 0; i < keys.length; i++) {
            armour[i] = (ItemStack) cfgFile.get("armour." + keys[i]);
        }
        player.getInventory().setArmorContents(armour);
        player.setFoodLevel(cfgFile.getInt("msc.food"));
        player.setLevel(cfgFile.getInt("msc.food"));
        player.setExp(Float.parseFloat(cfgFile.getString("msc.xp")));
        player.updateInventory();
        // Drop existing item on the ground
        for (ItemStack i : inventory) {
            if (i != null)
                player.getWorld().dropItemNaturally(player.getLocation(), i);
        }
        for (ItemStack i : currentArmour) {
            if (i != null)
                player.getWorld().dropItemNaturally(player.getLocation(), i);
        }
    }

    public static boolean isInventorySaved(Player player) {
        File file = new File(ThePlague.instance().getDataFolder(), "players" + File.separator + player.getUniqueId().toString() + ".yml");
        return file.exists();
    }
}
