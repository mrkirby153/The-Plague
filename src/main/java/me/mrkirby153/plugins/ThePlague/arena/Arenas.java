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

    public static ArrayList<ArenaCreator> creators = new ArrayList<ArenaCreator>();
    public static ArrayList<Arena> arenas = new ArrayList<Arena>();
    public static ArrayList<Lobby> lobbies = new ArrayList<Lobby>();


    private static ItemStack creationStick;

    public static void activateCreator(Player player) {
        if (findCreatorByName(player.getName()) == null) {
            ArenaCreator ac = new ArenaCreator(player);
            ThePlague.instance().getServer().getPluginManager().registerEvents(ac, ThePlague.instance());
            if (!creators.contains(ac))
                creators.add(ac);
        }
    }

    public static void deactivateCreator(Player player) {
        if (findCreatorByName(player.getName()) != null) {
            ArenaCreator ac = findCreatorByName(player.getName());
            HandlerList.unregisterAll(ac);
            if (creators.contains(ac))
                creators.remove(ac);
        }
    }

    public static ArenaCreator findCreatorByName(String name) {
        for (ArenaCreator ac : creators) {
            if (ac.getName().equalsIgnoreCase(name))
                return ac;
        }
        return null;
    }

    public static void registerArena(Arena arena) {
        arenas.add(arena);
    }

    public static void registerLobby(Lobby lobby) {
        lobbies.add(lobby);
    }

    public static void unregisterLobby(Lobby lobby) {
        lobbies.remove(lobby);
    }

    public static void unregisterArena(Arena arena) {
        arenas.remove(arena);
    }

    public static Arena findByName(String name) {
        for (Arena a : arenas) {
            if (a.getName().equalsIgnoreCase(name))
                return a;
        }
        return null;
    }

    public static Lobby findLobbyForArena(Arena arena) {
        for (Lobby l : lobbies) {
            if (l.getForArena() == arena)
                return l;
        }
        return null;
    }

    public static void giveCreationStick(Player p) {
        if(creationStick == null)
            constructCreateStick();
        p.getInventory().addItem(creationStick);
    }

    public static void takeCreationStick(Player player){
        if(creationStick == null)
            constructCreateStick();
        player.getInventory().remove(creationStick);
    }

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

    public static ItemStack creationStick() {
        if(creationStick == null)
            constructCreateStick();
        return creationStick;
    }
}
