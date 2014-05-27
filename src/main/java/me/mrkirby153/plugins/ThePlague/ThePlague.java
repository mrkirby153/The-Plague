package me.mrkirby153.plugins.ThePlague;

import me.mrkirby153.plugins.ThePlague.arena.ArenaUtils;
import me.mrkirby153.plugins.ThePlague.command.CmdExecutor;
import me.mrkirby153.plugins.ThePlague.command.Commands;
import me.mrkirby153.plugins.ThePlague.command.arena.CommandCreate;
import me.mrkirby153.plugins.ThePlague.command.arena.CommandSelect;
import me.mrkirby153.plugins.ThePlague.command.game.CommandJoin;
import me.mrkirby153.plugins.ThePlague.command.general.CommandHelp;
import me.mrkirby153.plugins.ThePlague.listeners.ArenaListener;
import org.bukkit.plugin.java.JavaPlugin;

public class ThePlague extends JavaPlugin {
    public static ThePlague instance;

    public static ThePlague instance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        getCommand("theplague").setExecutor(new CmdExecutor());

        getServer().getPluginManager().registerEvents(new ArenaListener(), this);

        ArenaUtils.loadAllArenas();
        ArenaUtils.loadAllLobbies();
        Commands.registerComamnd(new CommandHelp());
        Commands.registerComamnd(new CommandCreate());
        Commands.registerComamnd(new CommandSelect());
        Commands.registerComamnd(new CommandJoin());
    }

    @Override
    public void onDisable() {

    }
}
