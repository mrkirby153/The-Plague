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

    public void start() {
        this.secondsLeft = 20;
        setState(ArenaState.RUNNING);
        active = true;
    }

    public void run() {
        switch (this.state) {
            case WAITING:
                lobbyScoreboard();
                break;
            case RUNNING:
                gameScoreboard();
                break;
        }
        if (!active)
            return;
        // Update scoreboards

        if (this.secondsLeft >= 0) {
            if (playersInGame.size() == 0) {
                System.out.println("No players in game");
                setState(ArenaState.WAITING);
                ArenaUtils.loadBlocksFromFile(this);
                active = false;
                return;
            }
        } else {
            if (this.state == ArenaState.RUNNING) {
                setState(ArenaState.ENDED);
                this.secondsLeft = 5;
                return;
            }
            if (this.state == ArenaState.ENDED) {
                setState(ArenaState.WAITING);
                ArenaUtils.loadBlocksFromFile(this);
                active = false;
                return;
            }
        }

        /*if (infectedPlayers.size() == playersInGame.size() - 1) {
            ArrayList<String> players = (ArrayList<String>) playersInGame.clone();
            for(String playerName : infectedPlayers){
                players.remove(playerName);
            }
            this.winner = players.get(0);
            setState(ArenaState.ENDED);
        }*/
        System.out.println(secondsLeft);
        secondsLeft--;
    }

    @SuppressWarnings("deprecation")
    private void gameScoreboard() {

    }

    private void lobbyScoreboard() {
        Scoreboard board = manager.getNewScoreboard();
        Objective o = board.registerNewObjective("game_score", "dummy");
        o.setDisplayName("§b§lThePlague");
        o.setDisplaySlot(DisplaySlot.SIDEBAR);
        Score playerHeader = o.getScore(ChatColor.YELLOW + "Players:");
        playerHeader.setScore(5);
        Score playerCount = o.getScore("  " + ChatColor.GOLD + this.playersInGame.size() + "/--");
        playerCount.setScore(4);
        Score spacer1 = o.getScore(" ");
        spacer1.setScore(3);
        Score mapHeader = o.getScore(ChatColor.LIGHT_PURPLE + "Arena: ");
        mapHeader.setScore(2);
        Score map = o.getScore("   " + ChatColor.GREEN + this.getName());
        map.setScore(1);
        for (String playerName : playersInGame) {
            Bukkit.getPlayerExact(playerName).setScoreboard(board);
        }
    }

    public int playerCount() {
        return this.playersInGame.size();
    }

    public ArrayList<String> getPlayers() {
        return this.playersInGame;
    }

    public void leave(Player p) {
        for (String players : this.playersInGame) {
            ChatHelper.sendToPlayer(Bukkit.getPlayerExact(players), ChatColor.GOLD + players + " has left!");
            Bukkit.getPlayerExact(players).playSound(Bukkit.getPlayerExact(players).getLocation(), Sound.NOTE_BASS, 100F, 0.2F);
        }
        this.playersInGame.remove(p.getName());
        p.setScoreboard(manager.getNewScoreboard());
    }
}
