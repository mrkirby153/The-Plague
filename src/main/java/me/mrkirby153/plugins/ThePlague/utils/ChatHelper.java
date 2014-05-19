package me.mrkirby153.plugins.ThePlague.utils;

import me.mrkirby153.plugins.ThePlague.ThePlague;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class ChatHelper {

    private static ThePlague plugin = ThePlague.instance();

    public static void sendToPlayer(Player p, String message) {
        if (p == null)
            return;
        String msg = ChatColor.WHITE + "[" + ChatColor.DARK_RED + "%s" + ChatColor.WHITE + "] %s";
        p.sendMessage(String.format(msg, plugin.getName(), message));
    }

    public static void sendToPlayer(String playerName, String message) {
        @SuppressWarnings("deprecation")
        Player p = Bukkit.getPlayerExact(playerName);
        if (p != null)
            sendToPlayer(p, message);
    }

    @SuppressWarnings("unused")
    public static void sendToConsole(Level level, String message) {
        plugin.getLogger().log(level, ChatColor.stripColor(message));
    }

    public static void sendToConsole(String message) {
        plugin.getLogger().info(ChatColor.stripColor(message));
    }

    public static void send(CommandSender sender, String message) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            sendToPlayer(p, message);
        } else {
            sendToConsole(message);
        }
    }

    public static void sendAdminMessage(String message) {
        Bukkit.broadcast(ChatColor.ITALIC + "" + ChatColor.GRAY + "[ThePlague] " + message, "theplague.admin.messages");
    }
}