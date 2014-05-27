package me.mrkirby153.plugins.ThePlague.listeners;

import me.mrkirby153.plugins.ThePlague.arena.ArenaUtils;
import me.mrkirby153.plugins.ThePlague.arena.Arenas;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class ArenaListener implements Listener {

    @EventHandler
    public void blockBreak(BlockBreakEvent event) {
        if (ArenaUtils.isProtected(event.getBlock().getLocation())) {
            event.setCancelled(Arenas.findCreatorByName(event.getPlayer().getName()) == null);
        }
    }
}
