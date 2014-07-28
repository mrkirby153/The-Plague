package me.mrkirby153.plugins.ThePlague.utils;

import me.mrkirby153.plugins.ThePlague.ThePlague;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class MessageHelper {

    /** An instanace of the main plugin */
    private static ThePlague instance = ThePlague.instance();
    /** The message file */
    private static FileConfiguration messages = instance.getMessages();

    /**
     * Sends a message
     * @param sender The sender to send the message to
     * @param path The path in the configuration where the message is located
     * @param args An array of arguments for the message
     */
    public static void sendMessage(CommandSender sender, String path, Object... args) {
        if (sender instanceof Player)
            sender.sendMessage(replaceArgs(getMessage(path), args));
        else
            sender.sendMessage(ChatColor.stripColor(replaceArgs(getMessage(path), args)));
    }

    /**
     * Sends a message
     * @param player The player to send to
     * @param path The path in the configuration where the message is located
     * @param args An array of arguments for the message
     */
    public static void sendMessage(Player player, String path, Object... args) {
        player.sendMessage(replaceArgs(getMessage(path), args));
    }

    /**
     * Sends a message
     * @param playerName THe playername to send to
     * @param path The path in the configuration where the message is located
     * @param args An array of arguments for the message
     */
    @SuppressWarnings("deprecation")
    public static void sendMessage(String playerName, String path, Object... args) {
        Bukkit.getPlayerExact(playerName).sendMessage(replaceArgs(getMessage(path), args));
    }

    /**
     * Gets the raw message from the configuration file with the prefix
     * @param path The path in the configuration where the message is located
     * @return The message
     */
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

    /**
     * Gets the raw message from the configuration file <b>without</b> the prefix
     * @param path The path in the configuration where the message is located
     * @return The message <b>without</b> the prefix
     */
    public static String getMessageWithoutPrefix(String path) {
        String message;
        if (messages.getString(path) == null || messages.getString(path).equalsIgnoreCase("null")) {
            message = path;
        } else {
            message = ChatColor.translateAlternateColorCodes((char) 38, messages.getString(path));
        }
        return message;
    }

    /**
     * Gets a formatted message without the prefix
     * @param path The path in the configuration where the message is located
     * @param args An array of arguments for the message
     * @return The Message
     */
    public static String getMessageFormattedWithoutPrefix(String path, Object... args) {
        return replaceArgs(getMessageWithoutPrefix(path), args);
    }


    /**
     * Replace the arguments
     * @param message The message
     * @param args The arguments
     * @return The message with the replaced arguments
     */
    private static String replaceArgs(String message, Object... args) {
        String formattedMessage = message;
        for (int i = 0; i < args.length; i++) {
            String replaceChars = "%" + (i + 1);
//            System.out.println(replaceChars);
            formattedMessage = formattedMessage.replace(replaceChars, args[i].toString());
        }
        return formattedMessage;
    }

    /**
     * Sends a formatted message to all the admins
     * @param path The path in the configuration where the message is located
     * @param args The arguments
     */
    public static void sendAdminMessage(String path, Object... args) {
        String message = ChatColor.GRAY + "" + ChatColor.ITALIC + getMessageFormattedWithoutPrefix(path, args);
        Bukkit.broadcast(message, "theplague.admin.messages");

    }
}
