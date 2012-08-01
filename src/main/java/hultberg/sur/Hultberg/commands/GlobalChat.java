package main.java.hultberg.sur.Hultberg.commands;

import main.java.hultberg.sur.Hultberg.Hultberg;
import main.java.hultberg.sur.Hultberg.handlers.CommandHandler;
import main.java.hultberg.sur.Hultberg.handlers.Log;
import main.java.hultberg.sur.Hultberg.handlers.users.UserHandler;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class GlobalChat extends CommandHandler {

	private UserHandler userHandler;
	private Log ch;

	public GlobalChat(Hultberg instance) {
        super(instance);
        setStatus(0);
        //this.wh = instance.getWarnHandler();
        this.userHandler = instance.getUserHandler();
        this.ch = instance.getLog();
    }

	@Override
	public boolean onPlayerCommand(Player p, Command command, String label, String[] args) {
    	String msg = "";
    	for (int i = 0; i <= args.length - 1; i++) {
    		msg += args[i] + " ";
    	}
    	msg = msg.substring(0, msg.length() - 1);
    	if(this.userHandler.getUserStatus(p.getPlayer()) == 10){
    		plugin.broadcastAll("§7[§aGlobal§7] "+this.userHandler.getNameColor(p.getPlayer().getName()) + ChatColor.GOLD + ": " + ChatColor.WHITE + msg);
        } else if(this.userHandler.getUserStatus(p.getPlayer()) == 5){
        	plugin.broadcastAll("§7[§aGlobal§7] "+this.userHandler.getNameColor(p.getPlayer().getName()) + ChatColor.BLUE + ": " + ChatColor.WHITE + msg);
        } else if(this.userHandler.getUserStatus(p.getPlayer()) == 1){
        	plugin.broadcastAll("§7[§aGlobal§7] "+this.userHandler.getNameColor(p.getPlayer().getName()) + ": " + ChatColor.WHITE +  msg);
        } else if(this.userHandler.getUserStatus(p.getPlayer()) == 0){
        	plugin.broadcastAll("§7[§aGlobal§7] "+this.userHandler.getNameColor(p.getPlayer().getName()) + ChatColor.GRAY + ": " + ChatColor.WHITE + msg);
        }
    	
    	this.ch.ChatLogAddLine(p, "(SHOUT) "+msg);
    	
    	return true;
	}

}
