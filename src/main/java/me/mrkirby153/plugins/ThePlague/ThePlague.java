package me.mrkirby153.plugins.ThePlague;

import me.mrkirby153.plugins.ThePlague.command.arena.CommandCreate;
import me.mrkirby153.plugins.ThePlague.command.CmdExecutor;
import me.mrkirby153.plugins.ThePlague.command.Commands;
import me.mrkirby153.plugins.ThePlague.command.general.CommandHelp;
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

        Commands.registerComamnd(new CommandHelp());
        Commands.registerComamnd(new CommandCreate());
    }

    @Override
    public void onDisable() {

    }
}
