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

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArenaUtils {
    private static String dataPath = ThePlague.instance().getDataFolder().getAbsolutePath() + File.separator + "data" + File.separator;

    private static JSONParser parser = new JSONParser();

    /**
     * Saves the arena blocks to a file
     *
     * @param arena The arena to save blocks for
     * @return True if the save was successful
     */
    @SuppressWarnings("deprecation")
    public static boolean saveBlocksToFile(Arena arena) {
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
            return false;
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
            return false;
        }

        long endTime = System.currentTimeMillis();
        float totalTime = (endTime - startTime) / 1000;
        MessageHelper.sendAdminMessage("arena.saveComplete", count, totalTime);
        return true;
    }

    /**
     * Loads the blocks from file
     *
     * @param arena The arena
     */
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


    /**
     * Makes json data more human-readable
     *
     * @param json The json to format
     * @return The formatted json
     */
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

    /**
     * Checks if the provided location is protected
     *
     * @param location The location
     * @return True if the location is protected
     */
    public static boolean isProtected(Location location) {
        return isProtected(location.toVector());
    }

    /**
     * Checks if the provided vector is protected
     *
     * @param vector The vector
     * @return True if the vector is protected
     */
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

    /**
     * Checks if the location is a lobby and is protected
     *
     * @param loc The location to check
     * @return True if the location is in a lobby and protected
     */
    public static boolean isProtectedLobby(Location loc) {
        Vector vector = loc.toVector();
        for (Lobby l : Arenas.lobbies) {
            if(l == null)
                continue;
            Cuboid cuboid = new Cuboid(l.getPt1(), l.getPt2());
            if (cuboid.isInCuboid(vector)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the location is an arena and protected
     *
     * @param loc The location to check
     * @return True if the location is in an arena and protected
     */
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

    /**
     * Gets a list of all the arenas from file
     *
     * @return A list of all the arenas in a file.
     */
    public static ArrayList<String> getAllArenas() {
        File dataFolder = new File(ThePlague.instance().getDataFolder(), "data");
        if (dataFolder == null) {
            System.out.println("File " + dataFolder.getAbsolutePath() + " is null");
            return new ArrayList<String>();
        }
        if (dataFolder.listFiles() == null) {
            System.out.println("File " + dataFolder.getAbsolutePath() + " is empty");
            return new ArrayList<String>();
        }
        List<File> allFiles = Arrays.asList(dataFolder.listFiles());
        ArrayList<String> folders = new ArrayList<String>();
        for (File f : allFiles) {
            System.out.println("Processing file " + f.getAbsolutePath());
            if (f.isDirectory()) {
                System.out.println("Arena found! " + f.getAbsolutePath());
                folders.add(f.getName());
            }
        }
        return folders;
    }

    /**
     * Loads all the arenas from file
     */
    public static void loadAllArenas() {
        ArrayList<String> arenaNames = getAllArenas();
        ThePlague.instance().getLogger().info("Begin loading of arenas...");
        for (String s : arenaNames) {
            ThePlague.instance().getLogger().info("Loading arena " + s);
            File arenaFolder = new File(ThePlague.instance().getDataFolder(), "data" + File.separator + s);
            File dataFile = new File(arenaFolder, "data.json");
            JSONObject jsonFile;
            try {
                jsonFile = (JSONObject) parser.parse(new FileReader(dataFile));
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            JSONObject arena = (JSONObject) jsonFile.get("arena");
            JSONObject lobby = (JSONObject) jsonFile.get("lobby");
            Location lobbyPt1 = null, lobbyPt2 = null, arenaPt1 = null, arenaPt2 = null, lobbySpawn = null;
            if (lobby != null && !lobby.isEmpty()) {
                lobbyPt1 = stringToLocaiton((String) lobby.get("pt1"));
                lobbyPt2 = stringToLocaiton((String) lobby.get("pt2"));
                lobbySpawn = stringToLocaiton((String) lobby.get("spawn"));
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
            if (lobbySpawn != null)
                l = new Lobby(s, lobbyPt1, lobbyPt2, lobbySpawn);
            else
                l = new Lobby(s, lobbyPt1, lobbyPt2);
            Arenas.registerLobby(l);
            //TODO: Add flag support
            System.out.println("Loaded arena " + s + "!");
        }
        ThePlague.instance().getLogger().info("End loading of arenas");
    }

    /**
     * Saves arena data to a file
     *
     * @param name The arena name
     * @return true if the save was successful
     */
    @SuppressWarnings("unchecked")
    public static boolean saveArena(String name) {
        Arena a = Arenas.findByName(name);
        if (a == null) {
            MessageHelper.sendAdminMessage("theplague.create.noArenaFound", name);
            return false;
        }
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
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            if (br.readLine() != null)
                jsonFile = (JSONObject) parser.parse(new FileReader(dataFile));
            else
                jsonFile = new JSONObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Lobby l = Arenas.findLobbyForArena(a);
        jsonFile.put("name", name);
        jsonFile.put("currentStatus", a.getState().toString());
        JSONObject arena = new JSONObject();
        JSONObject lobby = new JSONObject();
        arena.put("pt1", locationToString(a.getPt1()));
        arena.put("pt2", locationToString(a.getPt2()));
        if (l != null) {
            lobby.put("pt1", locationToString(l.getPt1()));
            lobby.put("pt2", locationToString(l.getPt2()));
        }
        jsonFile.put("arena", arena);
        jsonFile.put("lobby", lobby);

        //TODO: Add flag support
        jsonFile.put("flags", new JSONArray());
        try {
            FileWriter fr = new FileWriter(dataFile);
            fr.write(formatJson(jsonFile.toJSONString()));
            fr.flush();
            fr.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Converts a string to a location
     *
     * @param location The string in the format world:x:y:z
     * @return A location of the string provided
     */
    private static Location stringToLocaiton(String location) {
        if (location.isEmpty() || location == null)
            return null;
        String[] parts = location.split("-");
        if (parts.length < 4) {
            return null;
        }
        World w = Bukkit.getWorld(parts[0]);
        if (w == null)
            return null;
        return new Location(w, Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
    }

    /**
     * Converts a location to a string
     *
     * @param location The location to convert
     * @return A string with the location
     */
    private static String locationToString(Location location) {
        return String.format("%s-%s-%s-%s", location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }
}
