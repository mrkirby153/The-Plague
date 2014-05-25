package me.mrkirby153.plugins.ThePlague.command.arena;

import me.mrkirby153.plugins.ThePlague.arena.Arena;
import me.mrkirby153.plugins.ThePlague.arena.Arenas;
import me.mrkirby153.plugins.ThePlague.arena.players.ArenaCreator;
import me.mrkirby153.plugins.ThePlague.command.BaseCommand;
import me.mrkirby153.plugins.ThePlague.utils.ChatHelper;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandCreate extends BaseCommand {
    public CommandCreate() {
        super("create", "The command for creating arenas", "theplague.admin.create");
        this.longDescription = "This command is used to create arenas";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(notPlayer);
                return;
            }
            Player p = (Player) sender;
            if (Arenas.findCreatorByName(p.getName()) != null) {
                Arenas.deactivateCreator(p);
                ChatHelper.sendToPlayer(p, ChatColor.GREEN + "Deactivated creation mode!");
            } else {
                Arenas.activateCreator(p);
                ChatHelper.sendToPlayer(p, ChatColor.GREEN + "Activated creation mode!");
            }
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("spawn-uninfected")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(notPlayer);
                    return;
                }
                Player p = (Player) sender;
                if (Arenas.findCreatorByName(p.getName()) == null) {
                    ChatHelper.sendToPlayer(p, ChatColor.DARK_RED + "You must be in arena creation mode first!");
                    return;
                }
                ArenaCreator ac = Arenas.findCreatorByName(p.getName());
                Arena test = new Arena("test", ac.getPt1(), ac.getPt2(), ac.getPt1().getWorld());
                test.saveArenaToFile();
                test.addUninfectedSpawn(p.getLocation());
                test.saveRespawnData();
            }
            if(args[0].equalsIgnoreCase("select")){
                if (!(sender instanceof Player)) {
                    sender.sendMessage(notPlayer);
                    return;
                }
                Player p = (Player) sender;
                ArenaCreator creator = Arenas.findCreatorByName(p.getName());
                if (creator == null) {
                    ChatHelper.sendToPlayer(p, ChatColor.RED + "You must be in arena create mode to do that!");
                    return;
                }
                creator.setSelectedArena(null);
                ChatHelper.sendToPlayer(p, ChatColor.GREEN + "Cleared current selection!");
            }
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("select")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(notPlayer);
                    return;
                }
                Player p = (Player) sender;
                Arena arena = Arenas.findByName(args[1]);
                if (arena == null) {
                    ChatHelper.sendToPlayer(p, ChatColor.RED + "There is no such arena with name '" + args[1] + "'!");
                    return;
                }
                ArenaCreator creator = Arenas.findCreatorByName(p.getName());
                if (creator == null) {
                    ChatHelper.sendToPlayer(p, ChatColor.RED + "You must be in arena create mode to do that!");
                    return;
                }
                creator.setSelectedArena(arena);
                ChatHelper.sendToPlayer(p, ChatColor.GREEN + "Selected arena " + arena.getName() + "!");
            }
        }
    }
}
