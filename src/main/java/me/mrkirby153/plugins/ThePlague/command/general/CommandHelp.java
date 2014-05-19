package me.mrkirby153.plugins.ThePlague.command.general;

import me.mrkirby153.plugins.ThePlague.command.BaseCommand;
import me.mrkirby153.plugins.ThePlague.command.Commands;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class CommandHelp extends BaseCommand {

    public CommandHelp() {
        super("help", "Shows this help message");
        this.longDescription = "Shows a list of all the commands or a detailed description of what a command does.";
        Commands.registerAlias(this, "?");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        int page = 1;
        if (args.length == 1)
            if (this.isNumeric(args[0]))
                page = Integer.parseInt(args[0]);
            else {
                showDetailedHelp(sender, args[0]);
                return;
            }
        if (sender instanceof Player)
            showHelpPlayer((Player) sender, page);
        else
            showHelpConsole();

    }

    private void showHelpPlayer(Player player, int page) {
        ArrayList<BaseCommand> cmds = Commands.commandList();
        double totalCmdPgs = Math.ceil(page / 12D);
        if (page > totalCmdPgs)
            page = (int) totalCmdPgs;
        player.sendMessage(ChatColor.BLUE + "------ ThePlague (" + page + "/" + (int) totalCmdPgs + ") ------");
        for (int i = (page - 1) * 12; i < ((page - 1) * 12) + 12; i++) {
            if (i >= cmds.size())
                break;
            BaseCommand cmd = cmds.get(i);
            player.sendMessage(ChatColor.GREEN + " - " + ChatColor.LIGHT_PURPLE + "/theplague " + cmd.commandName() + ChatColor.GREEN + " : " + ChatColor.AQUA + cmd.getCommandDescription());
        }
        player.sendMessage(ChatColor.GOLD + "Type /theplague help <command> for more information about a command");
    }

    private void showHelpConsole() {
        plugin.getLogger().info("----- ThePlague Help -----");
        for (BaseCommand cmd : Commands.commandList()) {
            plugin.getLogger().info(" - /ThePlague " + cmd.commandName() + " : " + cmd.getCommandDescription());
        }
        plugin.getLogger().info("Type /theplague help <command> for more information about a command");
    }

    private void showDetailedHelp(CommandSender sender, String commandName) {
        BaseCommand cmd = Commands.findByName(commandName);
        if (cmd != null) {
            sender.sendMessage(ChatColor.GOLD + "----- " + WordUtils.capitalizeFully(cmd.commandName()) + " -----");
            sender.sendMessage(ChatColor.GREEN + "   " + cmd.getLongDescription());
            sender.sendMessage(ChatColor.DARK_GREEN + "Aliases: " + cmd.getAliases());
            sender.sendMessage(ChatColor.GOLD + "-----------");
        } else {
            if (Commands.getAliases().containsKey(commandName)) {
                cmd = Commands.getAliases().get(commandName);
                sender.sendMessage(ChatColor.GOLD + "----- " + WordUtils.capitalizeFully(cmd.commandName()) + " -----");
                sender.sendMessage(ChatColor.GREEN + "     " + cmd.getLongDescription());
                sender.sendMessage(ChatColor.DARK_GREEN + "Aliases: " + cmd.getAliases());
                sender.sendMessage(ChatColor.GOLD + "-----------");
            }
        }
    }

    private boolean isNumeric(String number) {
        try {
            double n = Double.parseDouble(number);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
