package me.mrkirby153.plugins.ThePlague.signs;

import me.mrkirby153.plugins.ThePlague.ThePlague;
import me.mrkirby153.plugins.ThePlague.arena.Arena;
import me.mrkirby153.plugins.ThePlague.arena.Arenas;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.*;
import java.util.ArrayList;

public class Signs {
    /** A list of all the signs */
    private static ArrayList<ArenaSign> arenaSigns = new ArrayList<ArenaSign>();

    /**
     * Adds a sign
     * @param arenaSign The Arena Sign
     */
    public static void addSign(ArenaSign arenaSign) {
        arenaSigns.add(arenaSign);
        saveSignsToFile();
    }

    /**
     * Removes a sign
     * @param sign The arena sign to remove
     */
    public static void removeSign(ArenaSign sign) {
        arenaSigns.remove(sign);
        saveSignsToFile();
    }

    /**
     * Gets the sign at the given location
     * @param location The location
     * @return The sign at the location given
     */
    public static ArenaSign findSignFromLocation(Location location) {
        for (ArenaSign as : arenaSigns) {
            if (as.getLocation().equals(location))
                return as;
        }
        return null;
    }

    /**
     * Updates all the signs
     */
    public static void updateAllSigns() {
        updateArenaSigns();
    }

    /**
     * Updates all the signs
     */
    private static void updateArenaSigns() {
        for (ArenaSign as : arenaSigns) {
            as.update();
        }
    }

    /**
     * Writes the sign list to the file
     */
    public static void saveSignsToFile() {
        //TODO: Move to new filesystem
        try {
            File file = new File(ThePlague.instance().getDataFolder().getAbsolutePath() + File.separator + "data" + File.separator + "signs.arena-signs");
            if (!file.exists()) {
                file.createNewFile();
            }
            Writer writer = new FileWriter(file);
            for (ArenaSign s : arenaSigns) {
                Location l = s.getLocation();
                Arena forArena = s.getFor();
                writer.write(String.format("%s:%s:%s:%s:%s", forArena.getName(), l.getBlockX(), l.getBlockY(), l.getBlockZ(), l.getWorld().getName()) + "\n");
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the sign list form a file
     */
    public static void loadSignsFromFile() {
        //TODO: Move to new filesystem
        try {
            File file = new File(ThePlague.instance().getDataFolder().getAbsolutePath() + File.separator + "data" + File.separator + "signs.arena-signs");
            if (!file.exists())
                return;
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                String[] split = line.split(":");
                if (split.length != 5)
                    continue;
                String forArena = split[0];
                int x = Integer.parseInt(split[1]);
                int y = Integer.parseInt(split[2]);
                int z = Integer.parseInt(split[3]);
                World w;
                if ((w = Bukkit.getWorld(split[4])) == null)
                    continue;
                Location l = new Location(w, x, y, z);
                Arena arena;
                if((arena = Arenas.findByName(forArena)) == null)
                    continue;
                arenaSigns.add(new ArenaSign(l, arena));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
