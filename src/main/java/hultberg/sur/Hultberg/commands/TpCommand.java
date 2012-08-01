package main.java.hultberg.sur.Hultberg.commands;

import main.java.hultberg.sur.Hultberg.Hultberg;
import main.java.hultberg.sur.Hultberg.handlers.CommandHandler;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class TpCommand extends CommandHandler {

    public TpCommand(Hultberg instance) {
        super(instance);
        setStatus(5);
    }

    @Override
    public boolean onPlayerCommand(Player p, Command command, String label, String[] args) {
    	if (args.length == 1) {
            Player v = this.plugin.playerMatch(args[0]);
            	if (v != null) {
            		if(p.getWorld().getName() == v.getWorld().getName()){
            			p.teleport(v.getLocation());
            			p.sendMessage(ChatColor.GREEN + "You where teleported to " + this.userHandler.getNameColor(v) + ChatColor.GREEN + ".");
            		} else {
            			p.sendMessage(this.userHandler.getNameColor(v.getPlayer()) + ChatColor.RED + " is not in the same world as you.");
            		}
            	} else {
            		p.sendMessage(ChatColor.RED + "Can't find user.");
            	}
        } else {
            return false;
        }
        return true;
    }
}
