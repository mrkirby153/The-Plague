package me.mrkirby153.plugins.ThePlague.command.arena;

import me.mrkirby153.plugins.ThePlague.arena.Arena;
import me.mrkirby153.plugins.ThePlague.arena.ArenaUtils;
import me.mrkirby153.plugins.ThePlague.arena.Arenas;
import me.mrkirby153.plugins.ThePlague.command.BaseCommand;
import me.mrkirby153.plugins.ThePlague.command.Commands;
import me.mrkirby153.plugins.ThePlague.utils.ChatHelper;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CommandReSave extends BaseCommand {
    public CommandReSave(){
        super("resave", "Re-saves an arena to the file.", "theplague.admin.resave");
        this.longDescription = "Re-saves all blocks in the arena into a file. Helpful if you made changes to the arena";
        Commands.registerAlias(this, "save");
    }
    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length == 0){
            ChatHelper.send(sender, ChatColor.RED+"You must specify an arena first!");
        }
        Arena a = Arenas.findByName(args[0]);
        if(a == null) {
            ChatHelper.send(sender, ChatColor.RED + "That arena does not exist!");
            return;
        }
        ArenaUtils.saveBlocksToFile(a);
        ChatHelper.send(sender, ChatColor.GREEN+"Updated arena");
    }
}
