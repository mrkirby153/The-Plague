package me.mrkirby153.plugins.ThePlague.command.arena;

import me.mrkirby153.plugins.ThePlague.arena.Arena;
import me.mrkirby153.plugins.ThePlague.arena.ArenaUtils;
import me.mrkirby153.plugins.ThePlague.arena.Arenas;
import me.mrkirby153.plugins.ThePlague.arena.players.ArenaCreator;
import me.mrkirby153.plugins.ThePlague.command.BaseCommand;
import me.mrkirby153.plugins.ThePlague.utils.ChatHelper;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("arena")) {
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
                String arenaName = args[1];
                if (Arenas.findByName(arenaName) != null) {
                    ChatHelper.sendToPlayer(p, ChatColor.RED + "That arena already exists!");
                    return;
                }
                Location pt1 = creator.getPt1();
                Location pt2 = creator.getPt2();
                if (pt1 == null || pt2 == null) {
                    ChatHelper.sendToPlayer(p, ChatColor.RED + "You must select two points first!");
                    return;
                }
                Arena arena = new Arena(arenaName, pt1, pt2, p.getWorld());
                ArenaUtils.saveBlocksToFile(arena);
                ArenaUtils.addArena(arena);
                creator.setSelectedArena(arena);
                creator.setPt2(null);
                creator.setPt1(null);
                ChatHelper.sendToPlayer(p, ChatColor.GREEN + "Created and selected arena " + arenaName);
            }
        }
    }
}
