package me.mrkirby153.plugins.ThePlague.command;

import me.mrkirby153.plugins.ThePlague.ThePlague;
import org.bukkit.command.CommandSender;

public abstract class BaseCommand {
    private String permissionRequired = "%all%";
    private String commandName;
    private String commandDescription;

    public final String notPlayer = "You must be a player to preform this command!";
    public ThePlague plugin = ThePlague.instance();

    public BaseCommand() throws Exception {
    }

    public BaseCommand(String commandName, String commanDescription, String permissionRequired){
        this.commandName = commandName;
        this.commandDescription = commanDescription;
        this.permissionRequired = permissionRequired;
    }

    public String commandName(){
        return this.commandName;
    }

    public String getPermissionRequired(){
        return this.permissionRequired;
    }

    public String getCommandDescription(){
        return this.commandDescription;
    }

    public abstract void execute(CommandSender sender, String[] args);
}
