package me.mrkirby153.plugins.ThePlague.arena;

import me.mrkirby153.plugins.ThePlague.ThePlague;
import me.mrkirby153.plugins.ThePlague.utils.ChatHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.*;
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
        // Loop through every block and save it to a file
        ChatHelper.sendAdminMessage("Saving arena " + this.name + " to file. May cause lag!");
        File arenaDataFolder = new File(ThePlague.instance().getDataFolder().getAbsolutePath() + File.separator + "data" + File.separator + name);
        if (!arenaDataFolder.exists())
            arenaDataFolder.mkdirs();
        File arenaBlocksFile = new File(arenaDataFolder.getAbsolutePath() + File.separator + name + ".arena-blocks");
        Writer writer;
        try {
            writer = new PrintWriter(arenaBlocksFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            ChatHelper.sendAdminMessage("Could not open file " + arenaBlocksFile.getAbsolutePath() + " [" + e.getMessage() + "]");
            return;
        }
        float startTime = System.currentTimeMillis();
        int count = 0;
        int minX, maxX, minY, maxY, minZ, maxZ;
        ArrayList<Location> blocks = new ArrayList<Location>();
        if (l1.getBlockX() < l2.getBlockX()) {
            minX = l1.getBlockX();
            maxX = l2.getBlockX();
        } else {
            minX = l2.getBlockX();
            maxX = l1.getBlockX();
        }
        if (l1.getBlockY() < l2.getBlockY()) {
            minY = l1.getBlockY();
            maxY = l2.getBlockY();
        } else {
            minY = l2.getBlockY();
            maxY = l1.getBlockY();
        }
        if (l1.getBlockZ() < l2.getBlockZ()) {
            minZ = l1.getBlockZ();
            maxZ = l2.getBlockZ();
        } else {
            minZ = l2.getBlockZ();
            maxZ = l1.getBlockZ();
        }
        System.out.println(minZ + "," + maxZ);
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Location l = new Location(world, x, y, z);
                    Block b = l.getBlock();
                    try {
                        writer.write(String.format("%s,%s,%s,%s:%s,%s", world.getName(), x, y, z, b.getType(), (int) b.getData()) + "\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    count++;
                }
            }
        }
        try {
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        float totalTime = (endTime - startTime) / 1000;
        ChatHelper.sendAdminMessage(String.format("Saved %s blocks in %s seconds!", count, totalTime));

    }

    public void loadArenaFromFile() {
        File arenaDataFolder = new File(ThePlague.instance().getDataFolder().getAbsolutePath() + File.separator + "data" + File.separator + name);
        if (!arenaDataFolder.exists())
            arenaDataFolder.mkdirs();
        File arenaBlocksFile = new File(arenaDataFolder.getAbsolutePath() + File.separator + name + ".arena-blocks");
        ChatHelper.sendAdminMessage("Loading Arena From File...");
        try {
            BufferedReader br = new BufferedReader(new FileReader(arenaBlocksFile));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length != 2)
                    continue;
                String[] coords = parts[0].split(",");
                String[] block = parts[1].split(",");
                if (coords.length != 4 || block.length != 2)
                    continue;
                World w;
                if (Bukkit.getWorld(coords[0]) != null)
                    w = Bukkit.getWorld(coords[0]);
                else
                    continue;
                int x = Integer.parseInt(coords[1]);
                int y = Integer.parseInt(coords[2]);
                int z = Integer.parseInt(coords[3]);

                Material type = Material.valueOf(block[0]);
                if (type == null)
                    continue;
                Location l = new Location(w, x, y, z);
                l.getBlock().setType(type);
                l.getBlock().setData((byte) Integer.parseInt(block[1]));
            }
            br.close();
            ChatHelper.sendAdminMessage("Done!");
        } catch (IOException e) {
            e.printStackTrace();
        }
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
