package me.mrkirby153.plugins.ThePlague.command.commands;

import me.mrkirby153.plugins.ThePlague.arena.Arena;
import me.mrkirby153.plugins.ThePlague.arena.ArenaState;
import me.mrkirby153.plugins.ThePlague.arena.ArenaUtils;
import me.mrkirby153.plugins.ThePlague.arena.Arenas;
import me.mrkirby153.plugins.ThePlague.command.Command;
import me.mrkirby153.plugins.ThePlague.utils.MessageHelper;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GameCommands {
    //TODO: Finish commands

    @Command(name = "join", description = "Use to join an arena", permission = "theplague.general.join")
    public void joinCommand(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        if (args.length == 0) {
            //TODO: Add fancy GUI for joining games
            sender.sendMessage(MessageHelper.getMessageWithoutPrefix("game.arenaListHeader"));
            for (Arena a : Arenas.arenas) {
                ChatColor color;
                switch (a.getState()) {
                    case DISABLED:
                        color = ChatColor.GRAY;
                        break;
                    case RUNNING:
                        color = ChatColor.RED;
                        break;
                    case WAITING:
                        color = ChatColor.GREEN;
                        break;
                    default:
                        color = ChatColor.DARK_GREEN;
                }
                sender.sendMessage(color + MessageHelper.getMessageFormattedWithoutPrefix("game.arenaListEntry", a.getName(), a.getState()));
            }
            sender.sendMessage(MessageHelper.getMessageWithoutPrefix("game.arenaListFooter"));
        }
        if(args.length == 1){
            String arena = args[0];
            if(Arenas.findByName(arena) == null){
                MessageHelper.sendMessage(sender, "game.invalidArena", arena);
                return;
            }
            Arenas.findByName(arena).join(p);
        }
    }

    @Command(name = "leave", description = "Use to leave a game you are currently in", permission = "theplague.general.leave")
    public void leaveGame(CommandSender sender, String[] args){
        Player p = (Player) sender;
        Arena a = Arenas.getCurrentArena(p);
        if(a == null){
            MessageHelper.sendMessage(sender, "game.notInArena");
            return;
        }
        a.leave(p);
    }

    @Command(name = "state", description = "Set the state of a specified arena", permission = "theplague.general.state", executeLevel = 2)
    public void state(CommandSender sender, String[] args){
        if(args.length == 0){
            MessageHelper.sendMessage(sender, "game.states", getStates());
        }
        if(args.length == 1){
            MessageHelper.sendMessage(sender, "game.missingName");
            return;
        }
        if(args.length == 2){
            String state = args[0];
            state = state.toUpperCase().replace(" ", "_");
            String arena = args[1];

            Arena a = Arenas.findByName(arena);
            if(a == null){
                MessageHelper.sendMessage(sender, "game.invalidArena", a.getName());
            }
            if(!getStates().contains(state)){
                MessageHelper.sendMessage(sender, "game.invalidState");
                MessageHelper.sendMessage(sender, "game.states", getStates());
            }
            update(ArenaState.valueOf(state), a);
            MessageHelper.sendAdminMessage("game.newState", a.getName(), state);
            MessageHelper.sendMessage(sender, "game.stateSwitchSuccess");
            return;
        }
        MessageHelper.sendMessage(sender, "command.invalidArgs");
    }

    private String getStates() {
        Class c = ArenaState.class;
        Object[] enums = c.getEnumConstants();
        StringBuilder sb = new StringBuilder();
        for (Object o : enums) {
            sb.append(o + ", ");
        }

        if (sb.toString().isEmpty())
            return "None";
        return sb.toString().substring(0, sb.toString().length() - 2);
    }

    private void update(ArenaState state, Arena arena) {
        arena.setState(state);
        switch (state) {
            case RESETTING:
                ArenaUtils.loadBlocksFromFile(arena);
                break;
            case STARTING:
                arena.start();
                break;
        }
    }
}
