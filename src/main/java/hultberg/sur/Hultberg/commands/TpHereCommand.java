package main.java.hultberg.sur.Hultberg.commands;

import main.java.hultberg.sur.Hultberg.Hultberg;
import main.java.hultberg.sur.Hultberg.handlers.CommandHandler;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class TpHereCommand extends CommandHandler {

    public TpHereCommand(Hultberg instance) {
        super(instance);
        setStatus(5);
    }

    @Override
    public boolean onPlayerCommand(Player p, Command command, String label, String[] args) {
    	if (args.length == 1) {
            Player v = this.plugin.playerMatch(args[0]);
            if (v != null) {
                v.teleport(p.getLocation());
                p.sendMessage(ChatColor.GREEN + "You teleported " + this.userHandler.getNameColor(v) + ChatColor.GREEN + " to you.");
                v.sendMessage(ChatColor.GREEN + "You have been teleported to " + this.userHandler.getNameColor(p) + ChatColor.GREEN + ".");
            } else {
                p.sendMessage(ChatColor.RED + "Can't find user.");
            }
        } else {
            return false;
        }
        return true;
    }
}
