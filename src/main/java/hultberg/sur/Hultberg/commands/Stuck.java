package main.java.hultberg.sur.Hultberg.commands;

import main.java.hultberg.sur.Hultberg.Hultberg;
import main.java.hultberg.sur.Hultberg.handlers.CommandHandler;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class Stuck extends CommandHandler {

    public Stuck(Hultberg instance) {
        super(instance);
        setStatus(0);
    }

    public boolean onPlayerCommand(Player p, Command command, String label, String[] args) {
        Block b = p.getWorld().getHighestBlockAt(p.getLocation().getBlockX(), p.getLocation().getBlockZ());
        Location l = new Location(p.getWorld(), b.getX() + 0.5, b.getY(), b.getZ() + 0.5, p.getLocation().getYaw(), p.getLocation().getPitch());
        p.teleport(l);
        return true;
    }
}