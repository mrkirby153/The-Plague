package me.mrkirby153.plugins.ThePlague.command.arena;

import me.mrkirby153.plugins.ThePlague.arena.ArenaUtils;
import me.mrkirby153.plugins.ThePlague.arena.Arenas;
import me.mrkirby153.plugins.ThePlague.command.BaseCommand;
import me.mrkirby153.plugins.ThePlague.command.Commands;
import me.mrkirby153.plugins.ThePlague.utils.ChatHelper;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CommandRestore extends BaseCommand {
    public CommandRestore(){
        super("restore", "Loads an arena from a file", "theplague.admin.restore");
        Commands.registerAlias(this, "r");
        Commands.registerAlias(this, "load");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 1) {
            String arena = args[0];
            if (Arenas.findByName(arena) == null) {
                ChatHelper.send(sender, ChatColor.RED + "That arena does not exist!");
                return;
            }
            ArenaUtils.loadBlocksFromFile(Arenas.findByName(arena));
            ChatHelper.send(sender, ChatColor.GOLD+"Restoring blocks...");
            return;
        }
        ChatHelper.send(sender, ChatColor.RED + "Invalid args!");
    }
}
