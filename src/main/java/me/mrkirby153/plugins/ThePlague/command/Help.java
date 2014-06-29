package me.mrkirby153.plugins.ThePlague.command;

import me.mrkirby153.plugins.ThePlague.utils.MessageHelper;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class Help {
    private static ArrayList<String> commands;

    public static void showHelp(CommandSender sender, int page) {
        if (sender instanceof Player)
            showHelpPlayer((Player) sender, page);
        else
            showHelpConsole(sender);

    }

    public static void compileList() {
        ArrayList<Method> commandMethods = new ArrayList<Method>();
        ArrayList<Method> subCommandMethods = new ArrayList<Method>();
        if (commands == null)
            commands = new ArrayList<String>();

        for (Class c : Commands.getHandlers()) {
            for (Method m : c.getMethods()) {
                if (m.isAnnotationPresent(Command.class))
                    commandMethods.add(m);
                if (m.isAnnotationPresent(SubCommand.class))
                    subCommandMethods.add(m);
            }
        }
        for (Method m : commandMethods) {
            Command annotation = m.getAnnotation(Command.class);
            commands.add(ChatColor.GREEN + "  - /theplague " + ChatColor.GOLD + annotation.name() + ChatColor.GREEN + " - " + ChatColor.LIGHT_PURPLE + annotation.description());
            if (annotation.hasChildren()) {
                for (Method sbMethod : subCommandMethods) {
                    SubCommand sbAnn = sbMethod.getAnnotation(SubCommand.class);
                    if (sbAnn.superCommand().equalsIgnoreCase(annotation.name())) {
                        commands.add(ChatColor.GREEN + "  - /theplague " + ChatColor.GOLD + annotation.name() + " " + ChatColor.DARK_RED + sbAnn.commandName() + ChatColor.GREEN + " - " + ChatColor.LIGHT_PURPLE + sbAnn.description());
                    }
                }
            }
        }
    }

    public static void showHelpPlayer(Player player, int page) {
        // Compile a list of all commands/sub-commands
        if (commands == null)
            compileList();
        double totalPages = Math.ceil(commands.size() / 12D);
        //TODO: Finish display of help following CommandHelp.java
        //TODO: Add message.yml support
        if (page > totalPages) {
            page = (int) totalPages;
        }
//        player.sendMessage("----- ThePlague (" + page + "/" + (int) totalPages + " ) -----");
        player.sendMessage(MessageHelper.getMessageFormattedWithoutPrefix("commands.helpPlayer.header", page, (int) totalPages));
        for (int i = (page - 1) * 12; i < ((page - 1) * 12) + 12; i++) {
            if (i >= commands.size())
                break;
            player.sendMessage(commands.get(i));
        }
        player.sendMessage(MessageHelper.getMessageWithoutPrefix("commands.helpPlayer.footer"));

    }

    public static void showHelpConsole(CommandSender sender) {
        ArrayList<Method> commandMethods = new ArrayList<Method>();
        ArrayList<Method> subCommandMethods = new ArrayList<Method>();
        for (Class c : Commands.getHandlers()) {
            for (Method m : c.getMethods()) {
                if (m.isAnnotationPresent(Command.class))
                    commandMethods.add(m);
                if (m.isAnnotationPresent(SubCommand.class))
                    subCommandMethods.add(m);
            }
        }
        //TODO: Add messages.yml support
//        sender.sendMessage("----- ThePlague Help -----");
        sender.sendMessage(MessageHelper.getMessageWithoutPrefix("commands.helpConsole.header"));
        for (Method m : commandMethods) {
            Command annotation = m.getAnnotation(Command.class);
            sender.sendMessage("  - /theplague " + annotation.name() + " - " + annotation.description());
            if (annotation.hasChildren()) {
                for (Method sbMethod : subCommandMethods) {
                    SubCommand sbAnn = sbMethod.getAnnotation(SubCommand.class);
                    if (sbAnn.superCommand().equalsIgnoreCase(annotation.name())) {
                        sender.sendMessage("  - /theplague " + annotation.name() + " " + sbAnn.commandName() + " - " + sbAnn.description());
                    }
                }
            }
        }
//        sender.sendMessage("--------------------------");
        sender.sendMessage(MessageHelper.getMessageWithoutPrefix("commnads.helpConsole.footer"));
    }

}
