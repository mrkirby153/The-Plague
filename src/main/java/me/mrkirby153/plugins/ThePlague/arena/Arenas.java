package me.mrkirby153.plugins.ThePlague.arena;

import me.mrkirby153.plugins.ThePlague.ThePlague;
import me.mrkirby153.plugins.ThePlague.arena.lobby.Lobby;
import me.mrkirby153.plugins.ThePlague.arena.players.ArenaCreator;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Arenas {

    /**
     * A list of all the creators
     */
    public static ArrayList<ArenaCreator> creators = new ArrayList<ArenaCreator>();
    /**
     * A list of all the arenas
     */
    public static ArrayList<Arena> arenas = new ArrayList<Arena>();
    /**
     * A list of all the lobbies
     */
    public static ArrayList<Lobby> lobbies = new ArrayList<Lobby>();

    /**
     * The creation stick
     */
    private static ItemStack creationStick;

    /**
     * Puts the player into creation mode
     *
     * @param player The player who is to be put into creation mode
     */
    public static void activateCreator(Player player) {
        if (findCreatorByName(player.getName()) == null) {
            ArenaCreator ac = new ArenaCreator(player);
            ThePlague.instance().getServer().getPluginManager().registerEvents(ac, ThePlague.instance());
            if (!creators.contains(ac))
                creators.add(ac);
        }
    }

    /**
     * Removes the player from creation mode
     *
     * @param player The player who is to be removed from creation mode
     */
    public static void deactivateCreator(Player player) {
        if (findCreatorByName(player.getName()) != null) {
            ArenaCreator ac = findCreatorByName(player.getName());
            HandlerList.unregisterAll(ac);
            if (creators.contains(ac))
                creators.remove(ac);
        }
    }

    /**
     * Gets the creator by its name
     *
     * @param name The creators name
     * @return The creator
     */
    public static ArenaCreator findCreatorByName(String name) {
        for (ArenaCreator ac : creators) {
            if (ac.getName().equalsIgnoreCase(name))
                return ac;
        }
        return null;
    }

    /**
     * Adds an arena as a valid arena
     *
     * @param arena The arena
     */
    public static void registerArena(Arena arena) {
        arenas.add(arena);
    }

    /**
     * Adds a lobby as a valid lobby
     *
     * @param lobby The lobby
     */
    public static void registerLobby(Lobby lobby) {
        lobbies.add(lobby);
    }

    /**
     * Removes a lobby as a valid lobby
     *
     * @param lobby The lobby
     */
    public static void unregisterLobby(Lobby lobby) {
        lobbies.remove(lobby);
    }

    /**
     * Removes an arena as a valid arena
     *
     * @param arena The arena
     */
    public static void unregisterArena(Arena arena) {
        arenas.remove(arena);
    }

    /**
     * Finds an arena by its name
     * @param name The arena name
     * @return The arena
     */
    public static Arena findByName(String name) {
        for (Arena a : arenas) {
            if (a.getName().equalsIgnoreCase(name))
                return a;
        }
        return null;
    }

    /**
     * Finds the lobby for the given arena
     * @param arena The arena
     * @return The given arena's lobby
     */
    public static Lobby findLobbyForArena(Arena arena) {
        for (Lobby l : lobbies) {
            if (l.getForArena() == arena)
                return l;
        }
        return null;
    }

    /**
     * Gives the player the creation wand
     * @param p The player
     */
    public static void giveCreationStick(Player p) {
        if (creationStick == null)
            constructCreateStick();
        p.getInventory().addItem(creationStick);
    }

    /**
     * Removes the creation wand from the player
     * @param player The player
     */
    public static void takeCreationStick(Player player) {
        if (creationStick == null)
            constructCreateStick();
        player.getInventory().remove(creationStick);
    }

    /**
     * Creates the creation stick
     */
    private static void constructCreateStick() {
        creationStick = new ItemStack(Material.STICK);
        ItemMeta im = creationStick.getItemMeta();
        im.setDisplayName(ChatColor.GOLD + "[ThePlague] Creation Stick");
        List<String> lore = new ArrayList<String>();
        lore.add(ChatColor.GREEN + "Tutorial: ");
        lore.add(ChatColor.RED + "Right Click with this item in hand to select point 1");
        lore.add(ChatColor.RED + "Left Click with this item in hand to select point 2");
        im.setLore(lore);
        creationStick.setItemMeta(im);
    }

    /**
     * Gets the creation stick item
     * @return The creation stick
     */
    public static ItemStack creationStick() {
        if (creationStick == null)
            constructCreateStick();
        return creationStick;
    }

    /**
     * Gets the arena the player is in
     * @param p The player
     * @return The arena the given player is in
     */
    public static Arena getCurrentArena(Player p) {
        for (Arena a : arenas) {
            if (a.getPlayers().contains(p.getName()))
                return a;
        }
        return null;
    }
}
