package me.mrkirby153.plugins.ThePlague.listeners;

import me.mrkirby153.plugins.ThePlague.arena.Arenas;
import me.mrkirby153.plugins.ThePlague.signs.ArenaSign;
import me.mrkirby153.plugins.ThePlague.signs.Signs;
import me.mrkirby153.plugins.ThePlague.utils.ChatHelper;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class CreationListener implements Listener {

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Location l = event.getBlock().getLocation();
        if (event.getLine(0).equalsIgnoreCase("[ThePlague]") || event.getLine(0).equalsIgnoreCase("[Plague]")) {
            if (event.getLine(1).equalsIgnoreCase("arena")) {
                String arenaName = event.getLine(2);
                if (Arenas.findByName(arenaName) == null) {
                    ChatHelper.sendToPlayer(event.getPlayer(), ChatColor.GOLD + "There is no arena by the name of " + arenaName + "!");
                    return;
                }
                ArenaSign arenaSign = new ArenaSign(l, Arenas.findByName(arenaName));
                Signs.addSign(arenaSign);
            }
        }
    }
}
