package me.mrkirby153.plugins.ThePlague.command.arena;

import me.mrkirby153.plugins.ThePlague.arena.Arena;
import me.mrkirby153.plugins.ThePlague.arena.Arenas;
import me.mrkirby153.plugins.ThePlague.arena.players.ArenaCreator;
import me.mrkirby153.plugins.ThePlague.command.BaseCommand;
import me.mrkirby153.plugins.ThePlague.utils.ChatHelper;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSelect extends BaseCommand {

    public CommandSelect() {
        super("select", "Selects an arena", "theplague.admin.select");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(notPlayer);
                return;
            }
            Player p = (Player) sender;
            ArenaCreator creator = Arenas.findCreatorByName(p.getName());
            if (creator == null) {
                ChatHelper.sendToPlayer(p, ChatColor.RED + "You must be in arena creation mode to do that!");
                return;
            }
            creator.setSelectedArena(null);
            ChatHelper.sendToPlayer(p, ChatColor.DARK_RED + "Deselected arena!");
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                ChatHelper.send(sender, ChatColor.GOLD + "----- Arenas -----");
                for(Arena a : Arenas.arenas){
                    ChatHelper.send(sender, String.format("   §6 - §b %s §9in state §d%s", a.getName(), "NULL"));
                }
                if(sender instanceof Player){
                    Player p = (Player) sender;
                    ArenaCreator creator = Arenas.findCreatorByName(p.getName());
                    if(creator != null)
                        if(creator.getSelectedArena() != null)
                            ChatHelper.send(sender, String.format("§1Selected: §4%s", creator.getSelectedArena().getName()));
                }
                return;
            }
            if (!(sender instanceof Player)) {
                sender.sendMessage(notPlayer);
                return;
            }
            Player p = (Player) sender;
            ArenaCreator creator = Arenas.findCreatorByName(p.getName());
            if (creator == null) {
                ChatHelper.sendToPlayer(p, ChatColor.RED + "You must be in arena creation mode to do that!");
                return;
            }
            Arena a = Arenas.findByName(args[0]);
            if (a == null) {
                ChatHelper.sendToPlayer(p, ChatColor.RED + "That arena does not exist!");
                return;
            }
            creator.setSelectedArena(a);
            ChatHelper.sendToPlayer(p, ChatColor.GREEN + "Successfully selected arena " + a.getName() + "!");
        }
    }
}
