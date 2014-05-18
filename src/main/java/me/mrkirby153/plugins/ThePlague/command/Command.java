package me.mrkirby153.plugins.ThePlague.command;

import me.mrkirby153.plugins.ThePlague.ThePlague;

import java.util.ArrayList;
import java.util.HashMap;

public class Command {

    private static ArrayList<BaseCommand> commands = new ArrayList<BaseCommand>();
    private static HashMap<String, BaseCommand> aliases = new HashMap<String, BaseCommand>();

    public static void registerComamnd(BaseCommand command){
        if(!commands.contains(command)){
            commands.add(command);
        } else {
            ThePlague.instance().getLogger().warning("Attempted to register "+command.commandName()+" when it is already registered!");
        }
    }

    public static BaseCommand findByString(String name){
        for(BaseCommand cmd : commands){
            if(cmd.commandName().equalsIgnoreCase(name)){
                    return cmd;
            }
        }
        return null;
    }

    public static void registerAlias(BaseCommand cmd, String alias){
        aliases.put(alias, cmd);
    }

    public static HashMap<String, BaseCommand> getAliases(){
        return aliases;
    }

    public static ArrayList<BaseCommand> commandList(){
        return commands;
    }


}
