package me.mrkirby153.plugins.ThePlague.signs;

import org.bukkit.scheduler.BukkitRunnable;

public class SignUpdater extends BukkitRunnable {

    public void run() {
        Signs.updateAllSigns();
    }
}
