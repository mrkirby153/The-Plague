package me.mrkirby153.plugins.ThePlague;

import org.bukkit.plugin.java.JavaPlugin;

public class ThePlague extends JavaPlugin {
    public static ThePlague instance;

    public static ThePlague instance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
    }

    @Override
    public void onDisable() {

    }
}
