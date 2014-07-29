package me.mrkirby153.plugins.ThePlague.listeners;


import me.mrkirby153.plugins.ThePlague.arena.ArenaUtils;
import me.mrkirby153.plugins.ThePlague.arena.Arenas;
import me.mrkirby153.plugins.ThePlague.signs.ArenaSign;
import me.mrkirby153.plugins.ThePlague.signs.Signs;
import me.mrkirby153.plugins.ThePlague.utils.ChatHelper;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.Random;

public class ArenaListener implements Listener {

    @EventHandler
    public void blockBreak(BlockBreakEvent event) {
        if (ArenaUtils.isProtectedLobby(event.getBlock().getLocation()))
            event.setCancelled(Arenas.findCreatorByName(event.getPlayer().getName()) == null);
        if (ArenaUtils.isProtected(event.getBlock().getLocation()) && Arenas.getCurrentArena(event.getPlayer()) == null)
            event.setCancelled(Arenas.findCreatorByName(event.getPlayer().getName()) == null);
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent event) {
        if (ArenaUtils.isProtectedLobby(event.getBlock().getLocation()))
            event.setCancelled(Arenas.findCreatorByName(event.getPlayer().getName()) == null);
        if (ArenaUtils.isProtected(event.getBlock().getLocation()) && Arenas.getCurrentArena(event.getPlayer()) == null)
            event.setCancelled(Arenas.findCreatorByName(event.getPlayer().getName()) == null);
    }


    @EventHandler
    public void bucketFill(PlayerBucketFillEvent event) {
        if (ArenaUtils.isProtectedLobby(event.getBlockClicked().getLocation())) {
            event.setCancelled(Arenas.findCreatorByName(event.getPlayer().getName()) == null);
        }
        if (ArenaUtils.isProtected(event.getBlockClicked().getLocation()) && Arenas.getCurrentArena(event.getPlayer()) == null)
            event.setCancelled(Arenas.findCreatorByName(event.getPlayer().getName()) == null);
    }

    @EventHandler
    public void bucketEmpty(PlayerBucketEmptyEvent event) {
        if (ArenaUtils.isProtectedLobby(event.getBlockClicked().getLocation())) {
            event.setCancelled(Arenas.findCreatorByName(event.getPlayer().getName()) == null);
        }
    }

    @EventHandler
    public void onSignClick(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            if (event.getClickedBlock() == null)
                return;
            if (!event.getClickedBlock().getType().toString().contains("SIGN"))
                return;
            ArenaSign a = Signs.findSignFromLocation(event.getClickedBlock().getLocation());
            if (a == null)
                return;
            if (Arenas.getCurrentArena(event.getPlayer()) != null) {
                ChatHelper.sendToPlayer(event.getPlayer(), ChatColor.RED + "You are already in an arena! Leave with /theplague leave");
                return;
            }
            a.getFor().join(event.getPlayer());
        }
    }

    @EventHandler
    @SuppressWarnings("deprecation")
    public void onExplode(EntityExplodeEvent event) {
        for (final Block block : event.blockList()) {
            if(!ArenaUtils.isProtected(block.getLocation())){
                return;
            }
            Random r = new Random();
            FallingBlock fb = block.getWorld().spawnFallingBlock(block.getLocation(), block.getType(), block.getData());
            fb.setVelocity(new Vector(0, r.nextInt(3), 0));
            fb.setDropItem(false);
        }
    }
}
