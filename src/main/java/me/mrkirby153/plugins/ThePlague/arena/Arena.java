package me.mrkirby153.plugins.ThePlague.arena;

import me.mrkirby153.plugins.ThePlague.ThePlague;
import me.mrkirby153.plugins.ThePlague.utils.ChatHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class Arena {
    private String name;
    private Location l1;
    private Location l2;
    private World world;
    private ArrayList<String> playersInGame = new ArrayList<String>();

    private ArrayList<Location> uninfedtedSpawnLocations = new ArrayList<Location>();
    private Location infectedSpawn;
    private ArenaState state = ArenaState.DISABLED;

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

    public void loadRespawnLocations() {
        try {
            File spawns = new File(ThePlague.instance().getDataFolder().getAbsolutePath() + File.separator + "data" + File.separator + this.name + File.separator + name + ".spawns");
            if (!spawns.exists())
                return;
            BufferedReader br = new BufferedReader(new FileReader(spawns));
            if (br.readLine() == null)
                return;
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(new FileReader(spawns));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void join(Player p) {
        //TODO: Save inventory
        if (Arenas.findLobbyForArena(this) != null) {
            p.teleport(Arenas.findLobbyForArena(this).getSpawn());
            this.playersInGame.add(p.getName());
        } else {
            ChatHelper.sendToPlayer(p, ChatColor.RED + "That arena is missing a lobby");
        }
    }

    public ArenaState getState() {
        return state;
    }

    public void setState(ArenaState state) {
        this.state = state;
    }

    public void gameEnd(String winner){

    }

    public String getWinner(){
        return "Nobody";
    }
}
