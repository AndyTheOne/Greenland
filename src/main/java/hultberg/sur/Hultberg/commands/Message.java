package main.java.hultberg.sur.Hultberg.commands;

import main.java.hultberg.sur.Hultberg.Hultberg;
import main.java.hultberg.sur.Hultberg.handlers.CommandHandler;
import main.java.hultberg.sur.Hultberg.handlers.users.UserHandler;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

/**
 * Author: Xstasy
 * Date: 30.09.11
 * Time: 00:20
 */
public class Message extends CommandHandler {
     @SuppressWarnings("unused")
	private UserHandler userHandler;

    public Message(Hultberg instance) {
        super(instance);
        setStatus(0);
        this.userHandler = instance.getUserHandler();
    }

    @Override
    public boolean onPlayerCommand(Player p, Command command, String label, String[] args) {
    	if(args.length >= 2) {
            String message = "";
            for(int i = 1; i < args.length; i++) {
                message = message + args[i] + " ";
            }
            message = message.substring(0, message.length()-1);
            Player v = plugin.playerMatch(args[0]);
            if(v != null) {
                p.sendMessage(ChatColor.AQUA + "<= " + p.getName() + ": " + message);
                v.sendMessage(ChatColor.AQUA + "=> " + p.getName() + ": " + message);
            } else {
                p.sendMessage(ChatColor.RED + "The user is offline or dosen't exist.");
            }
        }
        return true;
    }
}
