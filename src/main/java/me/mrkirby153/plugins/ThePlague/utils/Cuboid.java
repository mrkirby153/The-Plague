package me.mrkirby153.plugins.ThePlague.utils;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class Cuboid {

    /** The first point of the cuboid */
    private Location pt1;
    /** The second point of the cuboid */
    private Location pt2;

    /**
     * Creates a new cuboid
     * @param pt1 The first point of the cuboid
     * @param pt2 The second point of the cuboid
     */
    public Cuboid(Location pt1, Location pt2) {
        this.pt1 = pt1;
        this.pt2 = pt2;
    }

    /**
     * Checks if a location is in a cuboid
     * @param location The location to check
     * @return True if the location is in a cuboid
     */
    public boolean isInCuboid(Location location) {
       return isInCuboid(location.toVector());
    }

    /**
     * Checks if a vector is in the cuboid
     * @param vector The vector to check
     * @return True if the vector is in a cuboid
     */
    public boolean isInCuboid(Vector vector){
        return (vector.isInAABB(Vector.getMinimum(pt1.toVector(), pt2.toVector()), Vector.getMaximum(pt1.toVector(), pt2.toVector())));
    }

    /**
     * Gets the first point of the cuboid
     * @return The first point of the cuboid
     */
    public Location getPt1() {
        return this.pt1;
    }

    /**
     * Gets the second point of the cuboid
     * @return The second point of the cuboid
     */
    public Location getPt2() {
        return this.pt2;
    }

    /**
     * Get a list of all the blocks in the cuboid
     * @return A list of all the blocks in a cuboid
     */
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
