package me.mrkirby153.plugins.ThePlague;

import me.mrkirby153.plugins.ThePlague.arena.ArenaUtils;
import me.mrkirby153.plugins.ThePlague.command.commands.ArenaCommands;
import me.mrkirby153.plugins.ThePlague.command.Commands;
import me.mrkirby153.plugins.ThePlague.command.commands.GeneralCommands;
import me.mrkirby153.plugins.ThePlague.command.ThePlagueExecutor;
import me.mrkirby153.plugins.ThePlague.listeners.ArenaListener;
import me.mrkirby153.plugins.ThePlague.listeners.CreationListener;
import me.mrkirby153.plugins.ThePlague.signs.SignUpdater;
import me.mrkirby153.plugins.ThePlague.signs.Signs;
import org.bukkit.plugin.java.JavaPlugin;

public class ThePlague extends JavaPlugin {
    public static ThePlague instance;

    public static ThePlague instance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        getCommand("theplague").setExecutor(new ThePlagueExecutor());

        Commands.registerNewHandler(GeneralCommands.class);
        Commands.registerNewHandler(ArenaCommands.class);
        getServer().getPluginManager().registerEvents(new ArenaListener(), this);
        getServer().getPluginManager().registerEvents(new CreationListener(), this);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new SignUpdater(), 10L, 10L);

        ArenaUtils.loadAllArenas();
        ArenaUtils.loadAllLobbies();
        Signs.loadSignsFromFile();
    }

    @Override
    public void onDisable() {

    }
}
