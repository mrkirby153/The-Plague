package me.mrkirby153.plugins.ThePlague.arena.players;

import me.mrkirby153.plugins.ThePlague.arena.Arena;
import org.bukkit.entity.Player;

public class ArenaPlayer {
    private Player player;

    private Arena joinedArena;

    private double infectionLevel;

    public ArenaPlayer(Player player, Arena joinedArena) {
        this.player = player;
        this.joinedArena = joinedArena;
        this.infectionLevel = 0;
    }

    public void infect(float amount) {
        if (amount > 100)
            amount = 100;
        infectionLevel += amount;
        if (infectionLevel > 100)
            infectionLevel = 100;
    }

    public void disinfect(float amount) {
        if (amount > 100)
            amount = 100;
        infectionLevel -= amount;
        if (infectionLevel < 0)
            infectionLevel = 0;
    }

    public void setInfectionAmount(float amount){
        if(amount < 0)
            amount = 0;
        if(amount > 100)
            amount = 10;
        infectionLevel = amount;
    }

    public Player getPlayer() {
        return this.player;
    }

    public double getInfectionAmount() {
        return this.infectionLevel;
    }

    public Arena currentArena(){
        return this.currentArena();
    }
}
