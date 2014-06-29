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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

public class ThePlague extends JavaPlugin {
    public static ThePlague instance;

    private FileConfiguration messages = null;
    private File messageFile = null;

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
        saveDefaultMessageFile();
    }

    @Override
    public void onDisable() {

    }

    public void reloadMessages(){
        if(messageFile == null){
            messageFile = new File(getDataFolder(), "messages.yml");
        }
        messages = YamlConfiguration.loadConfiguration(messageFile);

        InputStream defMessageStream = this.getResource("messages.yml");
        if(messageFile != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(messageFile);
            messages.setDefaults(defConfig);
        }
    }

    public FileConfiguration getMessages(){
        if(messageFile == null)
            reloadMessages();
        return messages;
    }

    public void saveMessages(){
        if(messages == null || messageFile == null) {
            getLogger().severe("Could not save messages file!");
            return;
        }
        try{
            getMessages().save(messageFile);
        } catch (IOException e){
            getLogger().log(Level.SEVERE, "Could not save messages to "+messageFile, e);
        }
    }

    public void saveDefaultMessageFile(){
        if(!new File(getDataFolder(), "messages.yml").exists()){
            this.saveResource("messages.yml", false);
        }
        reloadMessages();
    }
}
