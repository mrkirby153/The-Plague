package me.mrkirby153.plugins.ThePlague.arena;

import me.mrkirby153.plugins.ThePlague.ThePlague;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Arena {
    private String name;
    private Location l1;
    private Location l2;
    private World world;
    private ArrayList<Player> playersInGame = new ArrayList<Player>();

    private ArrayList<Location> uninfedtedSpawnLocations = new ArrayList<Location>();

    public Arena(String name, Location pt1, Location pt2, World world) {
        this.name = name;
        this.l1 = pt1;
        this.l2 = pt2;
        this.world = world;
    }

    public Arena(String name, Location pt1, Location pt2, String world) {
        this.name = name;
        this.l1 = pt1;
        this.l2 = pt2;
        this.world = Bukkit.getWorld(world);
    }

    public void saveArenaToFile() {
        ArenaUtils.saveBlocksToFile(this);
    }

    public void loadArenaFromFile() {
        File arenaDataFolder = new File(ThePlague.instance().getDataFolder().getAbsolutePath() + File.separator + "data" + File.separator + name);

    }

    // Save respawn data
    public void saveRespawnData() {
        JSONObject spawns = new JSONObject();
        JSONArray uninfected = new JSONArray();
        for (Location l : uninfedtedSpawnLocations) {
            String serialized = l.getWorld().getName() + ":" + l.getBlockX() + ":" + l.getBlockY() + ":" + l.getBlockZ() + ":" +
                    l.getPitch() + ":" + l.getYaw();
            uninfected.add(serialized);
        }
        spawns.put("uninfected", uninfected);
        // Write to file
        try {
            FileWriter file = new FileWriter(ThePlague.instance().getDataFolder().getAbsolutePath() + File.separator + "data" + File.separator + this.name + File.separator + name + ".spawns");
            file.write(spawns.toJSONString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void addUninfectedSpawn(Location location) {
        uninfedtedSpawnLocations.add(location);
        saveRespawnData();
    }

    public Location removeUninfectedSpawn(int id) {
        return uninfedtedSpawnLocations.remove(id);
    }

    public Location randomUninfectedSpawn() {
        Random r = new Random();
        return uninfedtedSpawnLocations.get(r.nextInt(uninfedtedSpawnLocations.size()));
    }

    public String getName() {
        return name;
    }

    public Location getPt1() {
        return l1;
    }

    public Location getPt2() {
        return l2;
    }
}
