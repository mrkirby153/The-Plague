package me.mrkirby153.plugins.ThePlague.command.commands;

import me.mrkirby153.plugins.ThePlague.ThePlague;
import me.mrkirby153.plugins.ThePlague.command.Command;
import me.mrkirby153.plugins.ThePlague.command.Help;
import me.mrkirby153.plugins.ThePlague.utils.MessageHelper;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class GeneralCommands {

    @Command(name = "help", description = "Shows this help message", executeLevel = 2, permission = "theplague.help")
    public void help(CommandSender sender, String[] args) {
        if (args.length == 0) {
            Help.showHelp(sender, 1);
        } else {
            if (this.isNumeric(args[0])) {
                Help.showHelp(sender, Integer.parseInt(args[0]));
            } else {
                Help.showHelp(sender, 1);
            }
        }
    }

    @Command(name = "msg-update", description = "Updates the message file", executeLevel = 2, permission = "theplague.admin.msgUpdate")
    public void updateCommand(CommandSender sender, String[] args) {
        // Do an unsafe copy (discard user's changes)
        if (args.length == 0) {
            File msgFile = new File(ThePlague.instance().getDataFolder(), "messages.yml");
            msgFile.delete();
            ThePlague.instance().saveDefaultMessageFile();
            ThePlague.instance().reloadMessages();
            MessageHelper.sendMessage(sender, "commands.msg-update.msgFileReset");
        }
        if(args.length == 1){
            if(args[0].equalsIgnoreCase("safe")){
                YamlConfiguration jarConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(ThePlague.instance().getResource("messages.yml")));
                YamlConfiguration currentConfig = YamlConfiguration.loadConfiguration(new File(ThePlague.instance().getDataFolder(), "messages.yml"));
                int count = 0;
                for (String string : jarConfig.getKeys(true)) {
                    if(currentConfig.get(string) == null){
                        currentConfig.set(string, jarConfig.get(string));
                        count++;
                    }
                }
                try{
                    currentConfig.save(new File(ThePlague.instance().getDataFolder(), "messages.yml"));
                    ThePlague.instance().reloadMessages();
                }catch(IOException e){
                    MessageHelper.sendMessage(sender, "commands.msg-update-safe.err");
                }
                MessageHelper.sendMessage(sender, "commands.msg-update-safe.success", count);
            } else {
                MessageHelper.sendMessage(sender, "commands.msg-update.help");
            }
        }
    }

    @Command(name="msg-reload", description = "Reloads the message file from disk. Useful if you made changes", executeLevel = 2, permission = "theplague.admin.reload")
    public void reload(CommandSender sender, String[] args){
        ThePlague.instance().reloadMessages();
        MessageHelper.sendMessage(sender, "commands.msg-reload");
    }

    private boolean isNumeric(String number) {
        try {
            double n = Double.parseDouble(number);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    @Command(name="msg-test", description = "Displays the message in the given path", executeLevel = 2, permission = "theplague.admin.msg-test")
    public void msgTest(CommandSender sender, String[] args){
        if(args.length == 0){
            MessageHelper.sendMessage(sender, "commands.msg-test.invalidArgs");
            return;
        }
        MessageHelper.sendMessage(sender, args[0]);
    }
}
