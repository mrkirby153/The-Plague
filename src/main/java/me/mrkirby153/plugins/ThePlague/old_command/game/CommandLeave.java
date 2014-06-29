package me.mrkirby153.plugins.ThePlague.old_command.game;

import me.mrkirby153.plugins.ThePlague.arena.Arena;
import me.mrkirby153.plugins.ThePlague.arena.Arenas;
import me.mrkirby153.plugins.ThePlague.old_command.BaseCommand;
import me.mrkirby153.plugins.ThePlague.utils.ChatHelper;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandLeave extends BaseCommand {
    public CommandLeave() {
        super("leave", "Leaves your current arena");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(notPlayer);
            return;
        }
        Player p = (Player) sender;
        Arena a = Arenas.getCurrentArena(p);
        if (a == null) {
            ChatHelper.sendToPlayer(p, ChatColor.RED + "You are not in an arena!");
            return;
        }
        a.leave(p);
    }
}
