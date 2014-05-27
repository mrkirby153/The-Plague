package me.mrkirby153.plugins.ThePlague.signs;

import org.bukkit.Location;

import java.util.ArrayList;

public class Signs {
    //TODO: Add saving and loading to file
    private static ArrayList<ArenaSign> arenaSigns = new ArrayList<ArenaSign>();

    public static void addSign(ArenaSign arenaSign){
        arenaSigns.add(arenaSign);
    }

    public static void removeSign(ArenaSign sign){
        arenaSigns.remove(sign);
    }

    public static ArenaSign findSignFromLocation(Location location){
        for(ArenaSign as : arenaSigns){
            if(as.getLocation() == location)
                return as;
        }
        return null;
    }

    public static void updateAllSigns(){
        updateArenaSigns();
    }

    private static void updateArenaSigns(){
        for(ArenaSign as : arenaSigns){
            as.update();
        }
    }
}
