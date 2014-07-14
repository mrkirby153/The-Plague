package me.mrkirby153.plugins.ThePlague.arena;

import me.mrkirby153.plugins.ThePlague.ThePlague;
import me.mrkirby153.plugins.ThePlague.arena.lobby.Lobby;
import me.mrkirby153.plugins.ThePlague.utils.Cuboid;
import me.mrkirby153.plugins.ThePlague.utils.MessageHelper;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArenaUtils {
    private static String dataPath = ThePlague.instance().getDataFolder().getAbsolutePath() + File.separator + "data" + File.separator;

    private static JSONParser parser = new JSONParser();

    @SuppressWarnings("deprecation")
    public static void saveBlocksToFile(Arena arena) {
        // Loop through every block and save it to a file
        Location l1 = arena.getPt1();
        Location l2 = arena.getPt2();
        World world = arena.getPt1().getWorld();
        MessageHelper.sendAdminMessage("arena.savingArena", arena.getName());
        File data = new File(ThePlague.instance().getDataFolder().getAbsolutePath() + File.separator + "data" + File.separator + arena.getName());
        if (!data.exists())
            data.mkdirs();
        File arenaBlocksFile = new File(dataPath + arena.getName() + File.separator + arena.getName() + ".arena-blocks");
        Writer writer;
        try {
            writer = new PrintWriter(arenaBlocksFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            MessageHelper.sendMessage("arena.saveError", arenaBlocksFile.getAbsolutePath(), e.getMessage());
            return;
        }
        float startTime = System.currentTimeMillis();
        int count = 0;
        Cuboid cuboid = new Cuboid(l1, l2);
        ArrayList<Location> locations = cuboid.getContainingLocation();
        for (Location l : locations) {
            Block b = l.getBlock();
            try {
                writer.write(String.format("%s,%s,%s,%s:%s,%s", world.getName(), l.getBlockX(), l.getBlockY(), l.getBlockZ(), b.getType(), (int) b.getData()) + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            count++;
        }
        try {
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        float totalTime = (endTime - startTime) / 1000;
        MessageHelper.sendAdminMessage("arena.saveComplete", count, totalTime);
    }

    @SuppressWarnings("deprecation")
    public static void loadBlocksFromFile(final Arena arena) {
        File arenaBlocksFile = new File(dataPath + arena.getName() + File.separator + arena.getName() + ".arena-blocks");
        final ArenaState prevState = arena.getState();
        arena.setState(ArenaState.RESETTING);
        if (!arenaBlocksFile.exists())
            return;
        try {
            BufferedReader br = new BufferedReader(new FileReader(arenaBlocksFile));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length != 2)
                    continue;
                String[] coords = parts[0].split(",");
                final String[] block = parts[1].split(",");
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

                final Material type = Material.valueOf(block[0]);
                if (type == null)
                    continue;
                final Location l = new Location(w, x, y, z);
                if (l.getBlock().equals(Material.AIR)) {
                    l.getBlock().getWorld().playEffect(l, Effect.STEP_SOUND, l.getBlock().getType());
                    l.getBlock().setType(Material.AIR);
                    continue;
                }
                if (l.getBlock().getType().equals(type) && l.getBlock().getData() == (byte) Integer.parseInt(block[1])) {
                    continue;
                }
                l.getBlock().setType(type);
                l.getBlock().setData((byte) Integer.parseInt(block[1]));
                l.getBlock().getWorld().playEffect(l.getBlock().getLocation(), Effect.STEP_SOUND, l.getBlock().getType());
            }
            arena.setState(prevState);
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String formatJson(String json) {
        StringBuilder sb = new StringBuilder();
        char newline = '\n';
        int indent = 0; //current indent level
        int factor = 2; //how many spaces per indent
        char[] data = json.toCharArray();
        for (int j = 0; j < data.length; j++) {
            sb.append(data[j]);
            if (data[j] == '{') {
                sb.append(newline);
                for (int w = factor * ++indent; w > 0; w--) {
                    sb.append(' ');
                }
            } else if (data[j] == '}') {
                sb.delete(sb.length() - 1, sb.length());
                sb.append(newline);
                for (int w = factor * --indent; w > 0; w--) {
                    sb.append(' ');
                }
                sb.append('}');
            } else if (data[j] == ',') {
                sb.append(newline);
                for (int w = factor * indent; w > 0; w--) {
                    sb.append(' ');
                }
            } else if (data[j] == ':') {
                sb.append(' ');
            }
        }
        return sb.toString();
    }

    public static boolean isProtected(Location location) {
        return isProtected(location.toVector());
    }

    public static boolean isProtected(Vector vector) {
        ArrayList<Arena> arenas = Arenas.arenas;
        ArrayList<Lobby> lobbies = Arenas.lobbies;
        for (Arena a : arenas) {
            Cuboid cuboid = new Cuboid(a.getPt1(), a.getPt2());
            if (cuboid.isInCuboid(vector)) {
                return true;
            }
        }
        for (Lobby l : lobbies) {
            Cuboid cuboid = new Cuboid(l.getPt1(), l.getPt2());
            if (cuboid.isInCuboid(vector)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isProtectedLobby(Location loc) {
        Vector vector = loc.toVector();
        for (Lobby l : Arenas.lobbies) {
            Cuboid cuboid = new Cuboid(l.getPt1(), l.getPt2());
            if (cuboid.isInCuboid(vector)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isProtectedArena(Location loc) {
        Vector vector = loc.toVector();
        ArrayList<Arena> arenas = Arenas.arenas;
        for (Arena a : arenas) {
            Cuboid cuboid = new Cuboid(a.getPt1(), a.getPt2());
            if (cuboid.isInCuboid(vector)) {
                return true;
            }
        }
        return false;
    }

    public static ArrayList<String> getAllArenas() {
        File dataFolder = new File(ThePlague.instance().getDataFolder(), "data");
        if(dataFolder == null)
            return new ArrayList<String>();
        if(dataFolder.listFiles() == null)
            return new ArrayList<String>();
        List<File> allFiles = Arrays.asList(dataFolder.listFiles());
        ArrayList<String> folders = new ArrayList<String>();
        for (File f : allFiles) {
            if (f.isDirectory()) {
                folders.add(f.getName());
            }
        }
        return folders;
    }

    public static void loadAllArenas() {
        ArrayList<String> arenaNames = getAllArenas();
        for (String s : arenaNames) {
            File arenaFolder = new File(ThePlague.instance().getDataFolder(), "data" + File.separator + s);
            File dataFile = new File(arenaFolder, "data.json");
            JSONObject jsonFile;
            try {
                jsonFile = (JSONObject) parser.parse(new FileReader(dataFile));
            } catch (Exception e) {
                continue;
            }
            JSONObject arena = (JSONObject) jsonFile.get("arena");
            JSONObject lobby = (JSONObject) jsonFile.get("lobby");
            Location lobbyPt1 = null, lobbyPt2 = null, arenaPt1 = null, arenaPt2 = null;
            if (lobby != null) {
                lobbyPt1 = stringToLocaiton((String) lobby.get("pt1"));
                lobbyPt2 = stringToLocaiton((String) lobby.get("pt2"));
            }
            if (arena != null) {
                arenaPt1 = stringToLocaiton((String) arena.get("pt1"));
                arenaPt2 = stringToLocaiton((String) arena.get("pt2"));
            }
            Arena a = new Arena(s, arenaPt1, arenaPt2, arenaPt1.getWorld());
            a.setState(ArenaState.valueOf((String) jsonFile.get("currentStatus")));
            a.runTaskTimer(ThePlague.instance(), 20L, 20L);
            Arenas.registerArena(a);
            Lobby l;
            Location lobbySpawn = stringToLocaiton((String) lobby.get("spawn"));
            if (lobbySpawn != null)
                l = new Lobby(s, lobbyPt1, lobbyPt2, lobbySpawn);
            else
                l = new Lobby(s, lobbyPt1, lobbyPt2);
            Arenas.registerLobby(l);
            //TODO: Add flag support
        }
    }

    @SuppressWarnings("unchecked")
    public static void saveArena(String name) {
        Arena a = Arenas.findByName(name);
        File arenaDirectory = new File(ThePlague.instance().getDataFolder(), "data" + File.separator + name);
        if (!arenaDirectory.exists()) {
            arenaDirectory.mkdirs();
        }
        File dataFile = new File(arenaDirectory, "data.json");
        if (!dataFile.exists())
            try {
                dataFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        JSONObject jsonFile = null;
        try {
            jsonFile = (JSONObject) parser.parse(new FileReader(dataFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (jsonFile == null)
            return;
        Lobby l = Arenas.findLobbyForArena(a);
        jsonFile.put("name", name);
        jsonFile.put("currentStatus", a.getState());
        JSONObject arena = new JSONObject();
        JSONObject lobby = new JSONObject();
        arena.put("pt1", locationToString(a.getPt1()));
        arena.put("pt2", locationToString(a.getPt2()));

        lobby.put("pt1", locationToString(l.getPt1()));
        lobby.put("pt2", locationToString(l.getPt2()));

        //TODO: Add flag support
        jsonFile.put("flags", new JSONArray());

        try {
            FileWriter fr = new FileWriter(dataFile);
            fr.write(formatJson(jsonFile.toJSONString()));
            fr.flush();
            fr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Location stringToLocaiton(String location) {
        String[] parts = location.split(",");
        if (parts.length < 4) {
            return null;
        }
        World w = Bukkit.getWorld(parts[0]);
        if (w == null)
            return null;
        return new Location(w, Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
    }

    private static String locationToString(Location location) {
        return String.format("%s,%s,%s,%s", location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }
}
