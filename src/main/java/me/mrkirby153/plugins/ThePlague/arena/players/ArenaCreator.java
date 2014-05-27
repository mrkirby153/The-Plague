package me.mrkirby153.plugins.ThePlague.arena.players;

import me.mrkirby153.plugins.ThePlague.arena.Arena;
import me.mrkirby153.plugins.ThePlague.arena.Arenas;
import me.mrkirby153.plugins.ThePlague.utils.ChatHelper;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class ArenaCreator implements Listener {
    private Player player;

    private Location pt1;
    private Location pt2;

    private Arena selectedArena;

    public ArenaCreator(Player player) {
        this.player = player;
    }

    public void setPt1(Location pt1) {
        this.pt1 = pt1;
    }

    public void setPt2(Location pt2) {
        this.pt2 = pt2;
    }

    public Location getPt1() {
        return this.pt1;
    }

    public Location getPt2() {
        return this.pt2;
    }

    public String getName() {
        return this.player.getName();
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setSelectedArena(Arena arena) {
        this.selectedArena = arena;
    }

    public Arena getSelectedArena() {
        return this.selectedArena;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!event.getPlayer().isSneaking()) {
            if(!event.getItem().equals(Arenas.creationStick()))
                return;
            if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                event.setCancelled(true);
                Location l = event.getClickedBlock().getLocation();
                setPt1(l);
                ChatHelper.sendToPlayer(event.getPlayer(), ChatColor.GOLD + "Point 1 set!");
                return;
            }
            if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                event.setCancelled(true);
                Location l = event.getClickedBlock().getLocation();
                setPt2(l);
                ChatHelper.sendToPlayer(event.getPlayer(), ChatColor.GOLD + "Point 2 set!");
            }
        }
    }

}
