package me.mrkirby153.plugins.ThePlague.command.commands;

import me.mrkirby153.plugins.ThePlague.arena.Arena;
import me.mrkirby153.plugins.ThePlague.arena.ArenaUtils;
import me.mrkirby153.plugins.ThePlague.arena.Arenas;
import me.mrkirby153.plugins.ThePlague.arena.lobby.Lobby;
import me.mrkirby153.plugins.ThePlague.arena.players.ArenaCreator;
import me.mrkirby153.plugins.ThePlague.command.Command;
import me.mrkirby153.plugins.ThePlague.command.SubCommand;
import me.mrkirby153.plugins.ThePlague.utils.MessageHelper;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArenaCommands {

    @Command(name = "create", permission = "theplague.admin.create", description = "Enter or leave arena creation mode", hasChildren = true)
    public void toggleCreateMode(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        if (Arenas.findCreatorByName(p.getName()) != null) {
            Arenas.deactivateCreator(p);
            Arenas.takeCreationStick(p);
            MessageHelper.sendMessage(p, "arena.create.deactivate");
        } else {
            Arenas.activateCreator(p);
            Arenas.giveCreationStick(p);
            MessageHelper.sendMessage(p, "arena.create.activate");
        }
    }

    @Command(name = "select", permission = "theplague.admin.select", description = "Sets your currently active arena (the one you make changes to)")
    public void select(CommandSender sender, String[] args) {
        if (args.length < 1) {
            MessageHelper.sendMessage(sender, "commands.help.select");
            return;
        }
        Player p = (Player) sender;
        if (Arenas.findCreatorByName(p.getName()) == null) {
            MessageHelper.sendMessage(sender, "arena.create.notCreating");
            return;
        }
        Arena a = Arenas.findByName(args[0]);
        if (a == null) {
            MessageHelper.sendMessage(sender, "arena.create.invalidArena", a.getName());
            return;
        }
        Arenas.findCreatorByName(p.getName()).setSelectedArena(a);
        MessageHelper.sendMessage(sender, "arena.create.selectSuccess", a.getName());
    }

    @SubCommand(superCommand = "create", commandName = "spawn-uninfected", description = "Sets the spawn for an uninfected person", subPermission = "theplague.admin.create.uninfectedSpawn")
    public void setSpawnUninfected(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        if (Arenas.findCreatorByName(p.getName()) == null) {
            MessageHelper.sendMessage(sender, "arena.create.notCreating");
            return;
        }
        ArenaCreator ac = Arenas.findCreatorByName(p.getName());
        if (ac.getSelectedArena() == null) {
            MessageHelper.sendMessage(sender, "arena.create.invalidSelectedArena");
            return;
        }
        Arena a = ac.getSelectedArena();
        a.addUninfectedSpawn(p.getLocation());
        a.saveRespawnData();
    }

    @SubCommand(superCommand = "create", commandName = "lobby", description = "Creates the lobby for your selected arena using your selected points", subPermission = "theplague.admin.create.lobby")
    public void createLobby(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        ArenaCreator ac = Arenas.findCreatorByName(p.getName());
        if (ac == null) {
            MessageHelper.sendMessage(sender, "arena.create.notCreating");
            return;
        }
        Arena selectedArena = ac.getSelectedArena();
        if (selectedArena == null) {
            MessageHelper.sendMessage(sender, "arena.create.invalidSelectedArena");
            return;
        }
        Location pt1 = ac.getPt1();
        Location pt2 = ac.getPt2();
        if (pt1 == null || pt2 == null) {
            MessageHelper.sendMessage(sender, "arena.create.invalidSelection");
            return;
        }
        Lobby lobby = new Lobby(selectedArena.getName(), pt1, pt2);
        Arenas.registerLobby(lobby);
        lobby.saveToFile();
        MessageHelper.sendMessage(sender, "arena.create.lobbySuccess");

    }

    @SubCommand(superCommand = "create", commandName = "spawn-lobby", description = "Sets the lobby spawn for the selected arena to your location", subPermission = "theplague.admin.create.lobbySpawn")
    public void createLobbySpawn(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        ArenaCreator ac = Arenas.findCreatorByName(p.getName());
        if (ac == null) {
            MessageHelper.sendMessage(sender, "arena.create.notCreating");
            return;
        }
        Arena selectedArena = ac.getSelectedArena();
        if (selectedArena == null) {
            MessageHelper.sendMessage(sender, "arena.create.invalidSelectedArena");
            return;
        }
        if (Arenas.findLobbyForArena(selectedArena) == null) {
            MessageHelper.sendMessage(sender, "arena.create.noLobby", selectedArena.getName());
            return;
        }
        Location currentLoc = p.getLocation();
        Lobby lobby = Arenas.findLobbyForArena(selectedArena);
        lobby.setSpawn(currentLoc);
        lobby.update();
        MessageHelper.sendMessage(sender, "arena.create.lobbySpawnSuccess");
    }

    @SubCommand(superCommand = "create", commandName = "arena", description = "Creates a new arena", subPermission = "theplague.admin.create.arena")
    public void createArena(CommandSender sender, String[] args) {
        if (args.length < 1) {
            MessageHelper.sendMessage(sender, "commands.help.create.arena");
            return;
        }
        Player p = (Player) sender;
        ArenaCreator creator = Arenas.findCreatorByName(p.getName());
        if (creator == null) {
            MessageHelper.sendMessage(sender, "arena.create.notCreating");
            return;
        }
        String arenaName = args[0];
        if (Arenas.findByName(arenaName) != null) {
            MessageHelper.sendMessage(sender, "arena.create.alreadyExists");
            return;
        }
        Location pt1 = creator.getPt1();
        Location pt2 = creator.getPt2();
        if (pt1 == null || pt2 == null) {
            MessageHelper.sendMessage(sender, "arena.create.invalidSelection");
            return;
        }
        if (!pt1.getWorld().getName().equalsIgnoreCase(pt2.getWorld().getName())) {
            MessageHelper.sendMessage(sender, "arena.create.invalidWorlds");
            return;
        }
        Arena arena = new Arena(arenaName, pt1, pt2, pt1.getWorld());
        Arenas.registerArena(arena);
        if (!ArenaUtils.saveBlocksToFile(arena)) {
            MessageHelper.sendMessage(sender, "arena.create.err.saveBlocks");
            return;
        }
        if (!ArenaUtils.saveArena(arena.getName())) {
            MessageHelper.sendMessage(sender, "arena.create.err.saveArena");
            return;
        }
        creator.setSelectedArena(arena);
        creator.setPt1(null);
        creator.setPt2(null);
        MessageHelper.sendMessage(sender, "arena.create.successArena");

    }

    @Command(name = "resave", aliases = {"save"}, permission = "theplague.admin.resave", description = "Re-saves the given arena to a file", executeLevel = 2)
    public void resave(CommandSender sender, String[] args) {
        if (args.length < 1) {
            MessageHelper.sendMessage(sender, "commands.help.resave");
            return;
        }
        Arena a = Arenas.findByName(args[0]);
        if (a == null) {
            MessageHelper.sendMessage(sender, "arena.save.invalidArena");
            return;
        }
        ArenaUtils.saveBlocksToFile(a);
        MessageHelper.sendMessage(sender, "arena.save.success", a.getName());
    }

    @Command(name = "restore", aliases = {"r", "load"}, permission = "theplague.admin.restore", description = "Loads the arena from a file", executeLevel = 2)
    public void load(CommandSender sender, String[] args) {
        if (args.length < 1) {
            MessageHelper.sendMessage(sender, "commands.help.restore");
            return;
        }
        Arena a = Arenas.findByName(args[0]);
        if (a == null) {
            MessageHelper.sendMessage(sender, "arena.restore.invalidArena");
            return;
        }
        ArenaUtils.loadBlocksFromFile(a);
        MessageHelper.sendMessage(sender, "arena.restore.success");
    }

}
