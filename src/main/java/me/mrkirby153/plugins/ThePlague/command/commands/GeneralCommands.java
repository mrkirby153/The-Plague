package me.mrkirby153.plugins.ThePlague.command.commands;

import me.mrkirby153.plugins.ThePlague.command.Command;
import me.mrkirby153.plugins.ThePlague.command.Help;
import org.bukkit.command.CommandSender;

public class GeneralCommands {

    @Command(name = "help", description = "Shows this help message", executeLevel = 2, permission = "theplague.help")
    public void help(CommandSender sender, String[] args){
        if(args.length == 0){
            Help.showHelp(sender, 1);
        } else {
            if(this.isNumeric(args[0])){
                Help.showHelp(sender, Integer.parseInt(args[0]));
            } else {
                Help.showHelp(sender, 1);
            }
        }
    }

    private boolean isNumeric(String number) {
        try {
            double n = Double.parseDouble(number);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
