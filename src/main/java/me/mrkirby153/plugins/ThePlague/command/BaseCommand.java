package me.mrkirby153.plugins.ThePlague.command;

import me.mrkirby153.plugins.ThePlague.ThePlague;
import org.bukkit.command.CommandSender;

import java.util.Map;

public abstract class BaseCommand {
    private String permissionRequired = "%all%";
    private String commandName;
    private String commandDescription;
    public String longDescription;

    public final String notPlayer = "You must be a player to preform this command!";
    public ThePlague plugin = ThePlague.instance();

    public BaseCommand() {
    }

    public BaseCommand(String commandName, String commanDescription, String permissionRequired) {
        this.commandName = commandName;
        this.commandDescription = commanDescription;
        this.permissionRequired = permissionRequired;
    }

    public BaseCommand(String commandName, String commandDescription) {
        this.commandName = commandName;
        this.commandDescription = commandDescription;
    }

    public String commandName() {
        return this.commandName;
    }

    public String getPermissionRequired() {
        return this.permissionRequired;
    }

    public String getCommandDescription() {
        return this.commandDescription;
    }

    public String getLongDescription() {
        if (longDescription != null)
            if (longDescription.length() > 0)
                return this.longDescription;
        return this.commandDescription;
    }

    public abstract void execute(CommandSender sender, String[] args);

    public String getAliases() {
        String aliases;
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, BaseCommand> e : Commands.getAliases().entrySet()) {
            if (e.getValue() == this) {
                sb.append(e.getKey() + ",");
            }
        }
        aliases = sb.toString();
        return (aliases == null || aliases.length() <= 0) ? "None" : aliases.substring(0, aliases.length() - 1);
    }
}
