package me.mrkirby153.plugins.ThePlague.arena.lobby;

import me.mrkirby153.plugins.ThePlague.ThePlague;
import me.mrkirby153.plugins.ThePlague.arena.Arena;
import me.mrkirby153.plugins.ThePlague.arena.ArenaUtils;
import me.mrkirby153.plugins.ThePlague.arena.Arenas;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;

public class Lobby {

    private Arena forArena;

    private Location pt1;
    private Location pt2;

    private Location spawn;
    private ArrayList<Player> currentPlayers = new ArrayList<Player>();

    public Lobby(String forArena, Location pt1, Location pt2, Location spawn) {
        if(Arenas.findByName(forArena) == null)
            return;
        this.forArena = Arenas.findByName(forArena);
        this.pt1 = pt1;
        this.pt2 = pt2;
        this.spawn = spawn;
    }

    public Lobby(String forArena, Location pt1, Location pt2) {
        this.forArena = Arenas.findByName(forArena);
        this.pt1 = pt1;
        this.pt2 = pt2;
    }

    public void setSpawn(Location spawn){
        this.spawn = spawn;
    }


    @SuppressWarnings("unchecked")
    public void saveToFile() {
        try {
            File lobbyFile = new File(ThePlague.instance().getDataFolder().getAbsolutePath() + File.separator + "data" + File.separator + "lobbies.json");
            if (!lobbyFile.exists())
                lobbyFile.createNewFile();
            BufferedReader br = new BufferedReader(new FileReader(lobbyFile));
            JSONObject jsonObject;
            JSONParser parser = new JSONParser();
            if (br.readLine() != null)
                jsonObject = (JSONObject) parser.parse(new FileReader(lobbyFile));
            else
                jsonObject = new JSONObject();
            JSONObject newLobby = new JSONObject();
            JSONObject pt1 = new JSONObject();
            pt1.put("world", this.pt1.getWorld().getName());
            pt1.put("z", this.pt1.getBlockZ());
            pt1.put("y", this.pt1.getBlockY());
            pt1.put("x", this.pt1.getBlockX());
            JSONObject pt2 = new JSONObject();
            pt2.put("world", this.pt2.getWorld().getName());
            pt2.put("z", this.pt2.getBlockZ());
            pt2.put("y", this.pt2.getBlockY());
            pt2.put("x", this.pt2.getBlockX());

            JSONObject jsonSpawn = new JSONObject();
            if(this.spawn != null) {
                jsonSpawn.put("world", this.spawn.getWorld().getName());
                jsonSpawn.put("yaw", this.spawn.getYaw());
                jsonSpawn.put("pitch", this.spawn.getPitch());
                jsonSpawn.put("z", this.spawn.getBlockZ());
                jsonSpawn.put("y", this.spawn.getBlockY());
                jsonSpawn.put("x", this.spawn.getBlockX());
                newLobby.put("spawn", jsonSpawn);
            }
            newLobby.put("pt1", pt1);
            newLobby.put("pt2", pt2);
            newLobby.put("for", forArena.getName());
            jsonObject.put(forArena.getName(), newLobby);
            FileWriter file = new FileWriter(lobbyFile);
            file.write(ArenaUtils.formatJson(jsonObject.toJSONString()));
            file.flush();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    public Location getPt1() {
        return pt1;
    }

    public Location getPt2() {
        return pt2;
    }

    public Arena getForArena(){
        return this.forArena;
    }

    public void update() {
        saveToFile();
    }

    public Location getSpawn() {
        return spawn;
    }
}
