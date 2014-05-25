package me.mrkirby153.plugins.ThePlague.arena;

import me.mrkirby153.plugins.ThePlague.ThePlague;
import me.mrkirby153.plugins.ThePlague.utils.ChatHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;

public class ArenaUtils {
    private static String dataPath = ThePlague.instance.getDataFolder().getAbsolutePath() + File.separator + "data" + File.separator;

    private static JSONParser parser = new JSONParser();
    public static void saveBlocksToFile(Arena arena) {
        // Loop through every block and save it to a file
        Location l1 = arena.getPt1();
        Location l2 = arena.getPt2();
        World world = arena.getPt1().getWorld();
        ChatHelper.sendAdminMessage("Saving arena " + arena.getName() + " to file. May cause lag!");
        File data = new File(ThePlague.instance().getDataFolder().getAbsolutePath() + File.separator + "data");
        if (!data.exists())
            data.mkdirs();
        File arenaBlocksFile = new File(dataPath + arena.getName() + File.separator + ".arena-blocks");
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

    public static void loadBlocksFromFile(Arena arena) {
        File arenaBlocksFile = new File(dataPath + arena.getName() + File.separator + ".arena-blocks");
        if(!arenaBlocksFile.exists())
            return;
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

    @SuppressWarnings("unchecked")
    public static void addArena(Arena arena){
        try {
            File arenas = new File(dataPath + "arenas.json");
            if (!arenas.exists())
                arenas.createNewFile();
            // Load json from file.
            JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(arenas));
            System.out.println(jsonObject.toJSONString());
            JSONObject newArena = new JSONObject();
            JSONObject pt1 = new JSONObject();
            pt1.put("x", arena.getPt1().getBlockX());
            pt1.put("y",  arena.getPt1().getBlockY());
            pt1.put("z", arena.getPt1().getBlockZ());
            pt1.put("pitch", arena.getPt1().getPitch());
            pt1.put("yaw", arena.getPt1().getYaw());
            newArena.put("pt1",pt1);
            JSONObject pt2 = new JSONObject();
            pt2.put("x", arena.getPt2().getBlockX());
            pt2.put("y", arena.getPt2().getBlockY());
            pt2.put("z", arena.getPt2().getBlockZ());
            pt2.put("pitch", arena.getPt2().getPitch());
            pt2.put("yaw", arena.getPt2().getYaw());
            newArena.put("pt2", pt2);
            newArena.put("world", arena.getPt1().getWorld());
            jsonObject.put(arena.getName(), newArena);
            FileWriter file = new FileWriter(arenas);
            file.write(jsonObject.toJSONString());
            file.flush();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
