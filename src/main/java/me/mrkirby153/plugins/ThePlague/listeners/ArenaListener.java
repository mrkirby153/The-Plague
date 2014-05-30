package me.mrkirby153.plugins.ThePlague.listeners;


import me.mrkirby153.plugins.ThePlague.ThePlague;
import me.mrkirby153.plugins.ThePlague.arena.ArenaUtils;
import me.mrkirby153.plugins.ThePlague.arena.Arenas;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ArenaListener implements Listener {

    @EventHandler
    public void blockBreak(BlockBreakEvent event){
        if(ArenaUtils.isProtectedLobby(event.getBlock().getLocation()))
            event.setCancelled(Arenas.findCreatorByName(event.getPlayer().getName()) == null);
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent event){
        if(ArenaUtils.isProtectedLobby(event.getBlock().getLocation()))
            event.setCancelled(Arenas.findCreatorByName(event.getPlayer().getName()) == null);
    }


    @EventHandler
    public void bucketFill(PlayerBucketFillEvent event){
        if(ArenaUtils.isProtectedLobby(event.getBlockClicked().getLocation())) {
            event.setCancelled(Arenas.findCreatorByName(event.getPlayer().getName()) == null);
        }
    }

    @EventHandler
    public void bucketEmpty(PlayerBucketEmptyEvent event){
        if(ArenaUtils.isProtectedLobby(event.getBlockClicked().getLocation())) {
            event.setCancelled(Arenas.findCreatorByName(event.getPlayer().getName()) == null);
        }
    }

    @EventHandler
    public void itemFrameInteract(PlayerInteractEntityEvent event){
        if(event.getRightClicked() instanceof ItemFrame){
            if(ArenaUtils.isProtected(event.getRightClicked().getLocation())) {
                event.setCancelled(Arenas.findCreatorByName(event.getPlayer().getName()) == null);
            }
        }
    }

    @EventHandler
    public void onSignClick(PlayerInteractEvent event){
        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)){
            if(!event.getClickedBlock().getType().toString().contains("SIGN"))
                return;
            ArenaSign a = Signs.findSignFromLocation(event.getClickedBlock().getLocation());
            if(a == null)
                return;
            if(Arenas.getCurrentArena(event.getPlayer()) != null){
                ChatHelper.sendToPlayer(event.getPlayer(), ChatColor.RED+"You are already in an arena! Leave with /theplague leave");
                return;
            }
            a.getFor().join(event.getPlayer());
        }
    }

    /*@EventHandler
    @SuppressWarnings("deprecation")
    public void explode(EntityExplodeEvent event){
        int offset = 5;
        int inc = 5;
        for(final Block b : event.blockList()){
            if(!ArenaUtils.isProtected(b.getLocation()) && ArenaUtils.isProtected(event.getLocation())){
                System.out.println("Ding!");
                new BukkitRunnable(){
                    public void run() {
                        Location l = b.getLocation();
                        l.getBlock().setType(b.getType());
                    }
                }.runTaskLater(ThePlague.instance(), 20L);
            }
        }
    }*/
    @EventHandler
    @SuppressWarnings("deprecation")
    public void onExplode(EntityExplodeEvent event) {
        for (final Block block : event.blockList()) {
            final Material blockType = block.getType();
            final byte data = block.getData();
            if (ArenaUtils.isProtected(block.getLocation())) {
                if (block.getType() == Material.TNT)
                    continue;
                new BukkitRunnable() {
                    public void run() {
                        block.setType(blockType);
                        block.setData(data);
                        block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getType());
                    }
                }.runTaskLater(ThePlague.instance(), 60);
            } else if (ArenaUtils.isProtected(event.getLocation())) {

                if (block.getType() == Material.TNT)
                    continue;
                new BukkitRunnable() {
                    public void run() {
                        block.setType(blockType);
                        block.setData(data);
                        block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getType());
                    }
                }.runTaskLater(ThePlague.instance(), 40);
            }
        }
    }
}
