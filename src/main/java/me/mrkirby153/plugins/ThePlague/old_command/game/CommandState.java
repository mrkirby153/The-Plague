package me.mrkirby153.plugins.ThePlague.old_command.game;

import me.mrkirby153.plugins.ThePlague.arena.Arena;
import me.mrkirby153.plugins.ThePlague.arena.ArenaState;
import me.mrkirby153.plugins.ThePlague.arena.ArenaUtils;
import me.mrkirby153.plugins.ThePlague.arena.Arenas;
import me.mrkirby153.plugins.ThePlague.old_command.BaseCommand;
import me.mrkirby153.plugins.ThePlague.utils.ChatHelper;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CommandState extends BaseCommand {
    public CommandState() {
        super("force", "Forces the arena into the given state", "theplague.admin.state");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            ChatHelper.send(sender, ChatColor.BLUE + "Acceptable states are: " + ChatColor.GRAY + getStates());
            return;
        }
        if (args.length == 1) {
            ChatHelper.send(sender, ChatColor.GOLD + "Please provide an arena name!");
            return;
        }
        if (args.length == 2) {
            String state = args[0];
            state = state.toUpperCase().replace(" ", "_");
            String arena = args[1];

            Arena a = Arenas.findByName(arena);
            if (a == null) {
                ChatHelper.send(sender, ChatColor.RED + "There is no arena by the name " + arena);
                return;
            }
            if (!getStates().contains(state)) {
                ChatHelper.send(sender, ChatColor.BLUE + "Acceptable states are: " + ChatColor.GRAY + getStates());
            }

            update(ArenaState.valueOf(state), Arenas.findByName(arena));
            ChatHelper.sendAdminMessage(sender.getName() + " has forced arena " + arena + " into state " + state);
            ChatHelper.send(sender, ChatColor.RED + arena + " is now in state " + ChatColor.GOLD + state);
            return;
        }
        ChatHelper.send(sender, ChatColor.RED + "Invalid args!");
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
