/*
 * This file is made by Thypthon. And the idea is by Insane by Xstasy and Jcfk
 */

package main.java.hultberg.sur.Hultberg.commands;

import main.java.hultberg.sur.Hultberg.Hultberg;
import main.java.hultberg.sur.Hultberg.handlers.CommandHandler;
import main.java.hultberg.sur.Hultberg.handlers.users.WarningsHandler;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class Low extends CommandHandler {

	private WarningsHandler wh;

	public Low(Hultberg instance) {
        super(instance);
        setStatus(5);
        this.wh = instance.getWarnHandler();
        this.userHandler = instance.getUserHandler();
    }

    @Override
    public boolean onPlayerCommand(Player p, Command command, String label, String[] args) {
        if(args.length == 0){
        	p.sendMessage(ChatColor.RED + "Please write a username");
        	// The user wants to see his own warns.
        } else {
        	this.wh.listWarnsOthers(p, args[0]);
        	return true;
        }
		return false;
    }
}