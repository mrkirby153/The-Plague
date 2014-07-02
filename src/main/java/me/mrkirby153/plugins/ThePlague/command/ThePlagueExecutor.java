package me.mrkirby153.plugins.ThePlague.command;

import me.mrkirby153.plugins.ThePlague.utils.MessageHelper;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

public class ThePlagueExecutor implements CommandExecutor {


    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String s, String[] args) {
        if (args == null || args.length < 1) {
            Help.showHelp(sender, 1);
            return true;
        }
        // Get the command name
        String commandName = args[0];
        try {
            // Check if the command name has a valid class and method
            Method method = Commands.getExecutorMethod(commandName);
            Class mClass = Commands.getExecutorClass(commandName);
            if (method != null || mClass != null) {
                args = removeFirstArg(args);
                if (method.isAnnotationPresent(Command.class)) {
                    Command annotation = method.getAnnotation(Command.class);
                    boolean isConsole = !(sender instanceof Player);
                    boolean hasSubCommand = annotation.hasChildren();
                    Method subCommand = null;
                    if (hasSubCommand) {
                        // Search for child command
                        ArrayList<Class> handlers = Commands.getHandlers();
                        for (Class c : handlers) {
                            for (Method m : c.getMethods()) {
                                if (m.isAnnotationPresent(SubCommand.class)) {
                                    SubCommand subAnn = m.getAnnotation(SubCommand.class);
                                    if (subAnn.superCommand().equalsIgnoreCase(commandName)) {
                                        if (args.length >= 1) {
                                            if (subAnn.commandName().equalsIgnoreCase(args[0])) {
                                                subCommand = m;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    String permissionRequired = annotation.permission();
                    if (isConsole && annotation.executeLevel() == 0) {
                        MessageHelper.sendMessage(sender, "command.notPlayer");
                        return true;
                    }
                    if (!isConsole && annotation.executeLevel() == 1) {
                        MessageHelper.sendMessage(sender, "command.notConsole");
                        return true;
                    }
                    if (hasSubCommand && args.length > 0) {
                        args = removeFirstArg(args);
                        if (subCommand != null) {
                            SubCommand subAnn = subCommand.getAnnotation(SubCommand.class);
                            String permission = subAnn.subPermission();
                            if (!sender.hasPermission(permission)) {
                                MessageHelper.sendMessage(sender, "command.noPermission");
                                return true;
                            }
                            if (Modifier.isStatic(subCommand.getModifiers())) {
                                subCommand.invoke(null, sender, args);
                                return true;
                            } else {
                                Class c = subCommand.getDeclaringClass();
                                subCommand.invoke(c.newInstance(), sender, args);
                                return true;
                            }
                        }
                    }
                    if (!sender.hasPermission(permissionRequired)) {
                        MessageHelper.sendMessage(sender, "command.noPermission");
                        return true;
                    }
                    if (Modifier.isStatic(method.getModifiers())) {
                        method.invoke(null, sender, args);
                    } else {
                        method.invoke(mClass.newInstance(), sender, args);
                    }
                    return true;
                }
            }
        } catch (Exception e) {
            MessageHelper.sendMessage(sender, "command.errorOccured");
            e.printStackTrace();
            return true;
        }
        MessageHelper.sendMessage(sender, "command.unknownCommand", commandName);
        return true;
    }

    private String[] removeFirstArg(String[] array) {
        String[] newArray = new String[array.length - 1];
        System.arraycopy(array, 1, newArray, 0, array.length - 1);
        return newArray;
    }


}
