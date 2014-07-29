package me.mrkirby153.plugins.ThePlague.listeners;

import me.mrkirby153.plugins.ThePlague.utils.InventorySaver;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class GeneralListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onLogin(PlayerLoginEvent event) {
        Player p = event.getPlayer();
        if (InventorySaver.isInventorySaved(p)) {
            //TODO: Move to new messaging system
            p.sendMessage(ChatColor.BOLD + "" + ChatColor.GOLD + "Don't panic! Your items have been saved!");
            p.sendMessage(ChatColor.GOLD+"Type /theplague inventory to get them back");
        }
    }
}
