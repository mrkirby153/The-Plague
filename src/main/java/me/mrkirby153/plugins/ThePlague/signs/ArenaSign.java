package me.mrkirby153.plugins.ThePlague.signs;

import me.mrkirby153.plugins.ThePlague.arena.Arena;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public class ArenaSign {

    private Location location;
    private Arena forArena;

    public ArenaSign(Location location, Arena forArena) {
        this.location = location;
        this.forArena = forArena;
    }

    /**
     * Gets the location for the sign
     * @return The location
     */
    public Location getLocation() {
        return this.location;
    }

    /**
     * Updates the sign
     */
    public void update() {
        Block b = location.getBlock();
        if (!b.getType().toString().contains("SIGN")) {
            return;
        }
        Sign sign = (Sign) b.getState();
        switch (forArena.getState()) {
            case DISABLED:
                sign.setLine(0, ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "[Join]");
                sign.setLine(1, ChatColor.DARK_GRAY + forArena.getName());
                sign.setLine(2, ChatColor.DARK_GRAY + "is disabled");
                sign.setLine(3, "");
                break;
            case WAITING:
                sign.setLine(0, ChatColor.GREEN + "[Join]");
                sign.setLine(1, String.format("§b%s§0/§1%s", forArena.playerCount(), "--"));
                sign.setLine(2, ChatColor.GREEN + forArena.getName());
                sign.setLine(3, ChatColor.UNDERLINE + "Join");
                break;
            case FULL:
                sign.setLine(0, ChatColor.RED + "" + ChatColor.BOLD + "[FULL]");
                sign.setLine(1, String.format("§b%s§0/§1%s", "--", "--"));
                sign.setLine(2, ChatColor.GREEN + forArena.getName());
                sign.setLine(3, "");
                break;
            case STARTING:
                sign.setLine(0, ChatColor.BLUE + "[Starting]");
                sign.setLine(1, String.format("§n%sm %ss", "00", "00"));
                sign.setLine(2, ChatColor.GOLD + "Click to");
                sign.setLine(3, ChatColor.GOLD + "join!");
                break;

            case RUNNING:
                sign.setLine(0, String.format("§e[%s]", forArena.getName()));
                sign.setLine(1, String.format("§n%sm %ss", "00", "00"));
                sign.setLine(3, ChatColor.GOLD + "Click to spectate");
                break;
            case ENDED:
                sign.setLine(0, ChatColor.RED + "█████████");
                sign.setLine(1, ChatColor.GOLD + forArena.getWinner());
                sign.setLine(2, ChatColor.GOLD + "has won!");
                sign.setLine(3, ChatColor.RED + "█████████");
                break;
            case RESETTING:
                sign.setLine(0, ChatColor.RED + "[Resetting]");
                sign.setLine(1, "");
                sign.setLine(2, ChatColor.WHITE + "Please wait...");
                sign.setLine(3, "");
                break;
            default:
                sign.setLine(0, "Unknown");
                sign.setLine(1, "State!");
                break;
        }
        sign.update();
    }

    /**
     * Gets what arena the sign is for
     * @return The arena
     */
    public Arena getFor() {
        return forArena;
    }
}
