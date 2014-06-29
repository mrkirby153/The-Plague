package me.mrkirby153.plugins.ThePlague.old_command.game;

import me.mrkirby153.plugins.ThePlague.arena.Arena;
import me.mrkirby153.plugins.ThePlague.arena.Arenas;
import me.mrkirby153.plugins.ThePlague.old_command.BaseCommand;
import me.mrkirby153.plugins.ThePlague.utils.ChatHelper;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandJoin extends BaseCommand {

    public CommandJoin(){
        super("join", "The old_command used to join an arena");
    }
    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length == 0){
            ChatHelper.send(sender, ChatColor.GOLD + "----- Arenas -----");
            for(Arena a : Arenas.arenas){
                ChatHelper.send(sender, String.format("   §6 - §b %s §9in state §d%s", a.getName(), "NULL"));
            }
        }
        if(args.length == 1){
            if(!(sender instanceof Player)){
                sender.sendMessage(notPlayer);
                return;
            }
            Player p = (Player) sender;
            String arenaName = args[0];
            if(Arenas.findByName(arenaName) == null){
                ChatHelper.sendToPlayer(p, String.format("§4The arena §6\"%s\"§4 does not exist!", arenaName));
                return;
            }
            Arenas.findByName(arenaName).join(p);
        }
    }
}
