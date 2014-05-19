package me.mrkirby153.plugins.ThePlague.command;

import me.mrkirby153.plugins.ThePlague.utils.ChatHelper;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class CmdExecutor implements CommandExecutor {


    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length >= 1) {
            String commandName = args[0];
            ArrayList<String> cmdArgsArray = new ArrayList<String>(Arrays.asList(args));
            if (cmdArgsArray.contains(commandName))
                cmdArgsArray.remove(commandName);
            String cmdArgs[] = cmdArgsArray.toArray(new String[0]);
            BaseCommand cmd = Commands.findByName(commandName);
            if (cmd != null) {
                String permRequired = cmd.getPermissionRequired();
                if (permRequired.equalsIgnoreCase("%all%") || sender.isOp()) {
                    cmd.execute(sender, cmdArgs);
                    return true;
                }
                if (sender.hasPermission(permRequired)) {
                    cmd.execute(sender, cmdArgs);
                    return true;
                } else {
                    ChatHelper.send(sender, String.format(ChatColor.RED + "This command requires permission node '" + ChatColor.BLUE + "%s" + ChatColor.RED + "'", permRequired));
                    return true;
                }
            } else {
                HashMap<String, BaseCommand> aliases = Commands.getAliases();
                if (aliases.containsKey(commandName)) {
                    cmd = aliases.get(commandName);
                    if (cmd != null) {
                        String permRequired = cmd.getPermissionRequired();
                        if (permRequired.equalsIgnoreCase("%all%") || sender.isOp()) {
                            cmd.execute(sender, cmdArgs);
                            return true;
                        }
                        if (sender.hasPermission(permRequired)) {
                            cmd.execute(sender, cmdArgs);
                            return true;
                        } else {
                            ChatHelper.send(sender, String.format(ChatColor.RED + "This command requires permission node '" + ChatColor.BLUE + "%s" + ChatColor.RED + "'", permRequired));
                            return true;
                        }
                    }
                }
            }
        }
        ChatHelper.send(sender, ChatColor.DARK_RED+"Unknown Argument! Type /theplague help for a list of commands");
        return true;
    }
}
