package main.java.hultberg.sur.Hultberg.commands;

import main.java.hultberg.sur.Hultberg.Hultberg;
import main.java.hultberg.sur.Hultberg.handlers.CommandHandler;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class SpawnCommand extends CommandHandler {

    public SpawnCommand(Hultberg instance) {
        super(instance);
        setStatus(0);
    }

    @Override
    public boolean onPlayerCommand(Player p, Command command, String label, String[] args) {
    	Location spawn = p.getWorld().getSpawnLocation();
    	p.teleport(spawn);
    	p.sendMessage(ChatColor.GREEN + "Welcome to spawn!");
    	return true;
    }
}