package me.mrkirby153.plugins.ThePlague.utils;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class Cuboid {

    private Location pt1;
    private Location pt2;

    public Cuboid(Location pt1, Location pt2) {
        this.pt1 = pt1;
        this.pt2 = pt2;
    }

    public boolean isInCuboid(Location location) {
       return isInCuboid(location.toVector());
    }

    public boolean isInCuboid(Vector vector){
        return (vector.isInAABB(Vector.getMinimum(pt1.toVector(), pt2.toVector()), Vector.getMaximum(pt1.toVector(), pt2.toVector())));
    }

    public Location getPt1() {
        return this.pt1;
    }

    public Location getPt2() {
        return this.pt2;
    }
    
    public ArrayList<Location> getContainingLocation(){
        int minX, maxX, minY, maxY, minZ, maxZ;
        ArrayList<Location> locations = new ArrayList<Location>();
        minX = pt1.getBlockX();
        maxX = pt2.getBlockX();

        minY = pt1.getBlockY();
        maxY = pt2.getBlockY();

        minZ = pt1.getBlockZ();
        maxZ = pt2.getBlockZ();
        if (pt1.getBlockX() > pt2.getBlockX()) {
            minX = pt2.getBlockX();
            maxX = pt1.getBlockX();
        }
        if (pt1.getBlockY() > pt2.getBlockY()) {
            minY = pt2.getBlockY();
            maxY = pt1.getBlockY();
        }
        if (pt1.getBlockZ() > pt2.getBlockZ()) {
            minZ = pt2.getBlockZ();
            maxZ = pt1.getBlockZ();
        }
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Location l = new Location(pt1.getWorld(), x, y, z);
                    locations.add(l);
                }
            }
        }
        return locations;
    }
}
