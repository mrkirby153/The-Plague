package me.mrkirby153.plugins.ThePlague.arena;

import me.mrkirby153.plugins.ThePlague.ThePlague;
import me.mrkirby153.plugins.ThePlague.arena.players.ArenaCreator;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;

public class Arenas {

    public static ArrayList<ArenaCreator> creators = new ArrayList<ArenaCreator>();

    public static void activateCreator(Player player) {
        if (findCreatorByName(player.getName()) == null) {
            ArenaCreator ac = new ArenaCreator(player);
            ThePlague.instance().getServer().getPluginManager().registerEvents(ac, ThePlague.instance());
            if(!creators.contains(ac))
                creators.add(ac);
        }
    }

    public static void deactivateCreator(Player player){
        if(findCreatorByName(player.getName()) != null){
            ArenaCreator ac = findCreatorByName(player.getName());
            HandlerList.unregisterAll(ac);
            if(creators.contains(ac))
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
}
