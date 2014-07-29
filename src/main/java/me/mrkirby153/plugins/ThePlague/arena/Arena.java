package me.mrkirby153.plugins.ThePlague.arena;

import me.mrkirby153.plugins.ThePlague.ThePlague;
import me.mrkirby153.plugins.ThePlague.utils.ChatHelper;
import me.mrkirby153.plugins.ThePlague.utils.InventoryHelper;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Arena extends BukkitRunnable {
    /* Fields related to the storing of arena data*/
    private String name;
    private Location l1;
    private Location l2;
    private World world;
    private ArrayList<String> playersInGame = new ArrayList<String>();
    private ArrayList<String> infectedPlayers = new ArrayList<String>();
    private HashMap<Flag, String> defaultFlags = new HashMap<Flag, String>();
    private HashMap<Flag, String> runtimeFlags = new HashMap<Flag, String>();
    private HashMap<Flag, String> flagList = new HashMap<Flag, String>();

    /* Fields related to the temporary storage of data related to the arena */
    private ArrayList<Location> uninfedtedSpawnLocations = new ArrayList<Location>();
    private Location infectedSpawn;
    private ArenaState state = ArenaState.DISABLED;
    private int secondsLeft = 0;
    private String winner = "Nobody";

    private boolean active = false;
    private ScoreboardManager manager = Bukkit.getScoreboardManager();

    /**
     * Creates a new arena
     *
     * @param name  The arena name
     * @param pt1   The first point of the arena
     * @param pt2   The second point of the arena
     * @param world The world that the arena is in
     */
    public Arena(String name, Location pt1, Location pt2, World world) {
        this.name = name;
        this.l1 = pt1;
        this.l2 = pt2;
        this.world = world;
    }

    /**
     * Creates a new arena
     *
     * @param name  The arena name
     * @param pt1   The first point of the arena
     * @param pt2   The second point of the arena
     * @param world The world name that the arena is in
     */
    public Arena(String name, Location pt1, Location pt2, String world) {
        this.name = name;
        this.l1 = pt1;
        this.l2 = pt2;
        this.world = Bukkit.getWorld(world);
    }

    /**
     * Saves respawn data to a file.
     */
    @Deprecated
    public void saveRespawnData() {
        //TODO: Rewrite respawn data to correspond with new filesystem
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

    /**
     * Adds a location where the unifected/survivors spawn
     *
     * @param location The location where the player can spawn at
     */
    public void addUninfectedSpawn(Location location) {
        uninfedtedSpawnLocations.add(location);
        saveRespawnData();
    }

    /**
     * Loads the respawn data from the disk.
     */
    @Deprecated
    public void loadRespawnLocations() {
        //TODO: Rewrite respawn data to correspond with new filesystem
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

    /**
     * Gets the arena name
     *
     * @return The arena's name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the first point of the arena
     *
     * @return The first point of the arena
     */
    public Location getPt1() {
        return l1;
    }

    /**
     * Gets the second point of the arena
     *
     * @return The second point of the arena
     */
    public Location getPt2() {
        return l2;
    }

    /**
     * Joins a player into the arena and teleports the to the lobby
     *
     * @param p The player to be joined
     */
    @SuppressWarnings("deprecation")
    public void join(Player p) {
        if (Arenas.findLobbyForArena(this) != null) {
            p.teleport(Arenas.findLobbyForArena(this).getSpawn());
            if (!this.playersInGame.contains(p.getName()))
                this.playersInGame.add(p.getName());
            InventoryHelper.save(p);
            for (String players : this.playersInGame) {
                // TODO: Move to messaging system
                ChatHelper.sendToPlayer(Bukkit.getPlayerExact(players), ChatColor.GOLD + players + " has joined!");
                Bukkit.getPlayerExact(players).playSound(Bukkit.getPlayerExact(players).getLocation(), Sound.NOTE_PLING, 1F, 0.12F);
            }
        } else {
            // TODO: Move to messaging system
            ChatHelper.sendToPlayer(p, ChatColor.RED + "That arena is missing a lobby");
        }
    }

    /**
     * Gets the arena's current state
     *
     * @return The state the arena is in
     */
    public ArenaState getState() {
        return state;
    }

    /**
     * Sets the arena's state
     *
     * @param state The state that the arena will be in
     */
    public void setState(ArenaState state) {
        this.state = state;
    }

    /**
     * Gets the winner
     *
     * @return The winner
     */
    public String getWinner() {
        return winner;
    }

    /**
     * Starts the countdown to start the game
     */
    public void start() {
        this.secondsLeft = 20;
        setState(ArenaState.RUNNING);
        active = true;
    }

    /**
     * Handle all the runtime properties of the arena.
     */
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
        secondsLeft--;
    }

    /**
     * Updates the scoreboard for the players ingame
     */
    @SuppressWarnings("deprecation")
    private void gameScoreboard() {
        //TODO: Add game scoreboard
    }

    /**
     * Updates the scoreboard for the players in the lobby
     */
    @SuppressWarnings("depercation")
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

    /**
     * Gets the total amount of players in the game
     *
     * @return The amount of players in game
     */
    public int playerCount() {
        return this.playersInGame.size();
    }

    /**
     * Gets the players in the game
     *
     * @return An arrray list of the playernames ingame.
     */
    public ArrayList<String> getPlayers() {
        return this.playersInGame;
    }

    /**
     * Returns the player to his pervious location
     *
     * @param p The player leaving
     */
    @SuppressWarnings("deprecation")
    public void leave(Player p) {
        for (String players : this.playersInGame) {
            // TODO: Move to new messaging system.
            ChatHelper.sendToPlayer(Bukkit.getPlayerExact(players), ChatColor.GOLD + players + " has left!");
            Bukkit.getPlayerExact(players).playSound(Bukkit.getPlayerExact(players).getLocation(), Sound.NOTE_BASS, 100F, 0.2F);
        }
        this.playersInGame.remove(p.getName());
        p.setScoreboard(manager.getNewScoreboard());
        //TODO: Teleport the player back to his previous location
    }

    /**
     * Sets a flag for the arena.
     *
     * @param flagName The flag to be set
     * @param value    The flag's value
     */
    public void setDefaultFlag(Flag flagName, String value) {
        String currentValue = defaultFlags.get(flagName);
        if (currentValue == null || currentValue.isEmpty()) {
            defaultFlags.put(flagName, value);
            return;
        }
        defaultFlags.remove(flagName);
        defaultFlags.put(flagName, value);
    }

    public void removeDefaultFlag(Flag flagName){
        defaultFlags.remove(flagName);
    }

    /**
     * Gets a flag's value
     *
     * @param flag The flag name
     * @return The flag's value
     */
    public String getFlagValue(Flag flag) {
        return flags.get(flag);
    }

    public HashMap<Flag, String> getFlags(){
        updateFlags();
        return flagList;
    }

    private void updateFlags(){
        if(flagList == null)
            flagList = new HashMap<Flag, String>();
        Set<Flag> defaultFlagKeys = defaultFlags.keySet();
        Set<Flag> runtimeFlagKeys = runtimeFlags.keySet();
        for(Flag f : defaultFlagKeys){
            flagList.put(f, defaultFlags.get(f));
        }
        for(Flag f : runtimeFlagKeys){
            flagList.put(f, runtimeFlags.get(f));
        }
    }
}
