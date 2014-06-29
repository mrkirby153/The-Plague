package me.mrkirby153.plugins.ThePlague.utils;

import me.mrkirby153.plugins.ThePlague.ThePlague;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class MessageHelper {

    private static ThePlague instance = ThePlague.instance();
    private static FileConfiguration messages = instance.getMessages();

    public static void sendMessage(CommandSender sender, String path, Object... args) {
        if (sender instanceof Player)
            sender.sendMessage(replaceArgs(getMessage(path), args));
        else
            sender.sendMessage(ChatColor.stripColor(replaceArgs(getMessage(path), args)));
    }

    public static void sendMessage(Player player, String path, Object... args) {
        player.sendMessage(replaceArgs(getMessage(path), args));
    }

    @SuppressWarnings("deprecation")
    public static void sendMessage(String playerName, String path, Object... args) {
        Bukkit.getPlayerExact(playerName).sendMessage(replaceArgs(getMessage(path), args));
    }

    private static String getMessage(String path) {
        String prefix;
        if (messages.getString("chatPrefix") == null || messages.getString("chatPrefix").equalsIgnoreCase("null"))
            prefix = "[" + ChatColor.RED + "ThePlague" + ChatColor.WHITE + "] ";
        else
            prefix = ChatColor.translateAlternateColorCodes((char) 38, messages.getString("chatPrefix")) + " ";
        String message;
        if (messages.getString(path) == null || messages.getString(path).equalsIgnoreCase("null")) {
            message = path;
        } else {
            message = ChatColor.translateAlternateColorCodes((char) 38, messages.getString(path));
        }
        return prefix + message;
    }

    public static String getMessageWithoutPrefix(String path) {
        String message;
        if (messages.getString(path) == null || messages.getString(path).equalsIgnoreCase("null")) {
            message = path;
        } else {
            message = ChatColor.translateAlternateColorCodes((char) 38, messages.getString(path));
        }
        return message;
    }

    public static String getMessageFormattedWithoutPrefix(String path, Object... args) {
        return replaceArgs(getMessage(path), args);
    }


    private static String replaceArgs(String message, Object... args) {
        String formattedMessage = message;
        for (int i = 0; i < args.length; i++) {
            String replaceChars = "%" + (i + 1);
//            System.out.println(replaceChars);
            formattedMessage = formattedMessage.replace(replaceChars, args[i].toString());
        }
        return formattedMessage;
    }

    public static void sendAdminMessage(String path, Object... args) {
        String message = ChatColor.GRAY + "" + ChatColor.ITALIC + getMessageFormattedWithoutPrefix(path, args);
        Bukkit.broadcast(message, "theplague.admin.messages");

    }
}
