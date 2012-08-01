package main.java.hultberg.sur.Hultberg.commands;

import main.java.hultberg.sur.Hultberg.Hultberg;
import main.java.hultberg.sur.Hultberg.handlers.CommandHandler;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class Trade extends CommandHandler {

    public Trade(Hultberg instance) {
        super(instance);
        setStatus(1);
        this.userHandler = instance.getUserHandler();
    }

    @Override
    public boolean onPlayerCommand(Player p, Command command, String label, String[] args) {
    	String msg = "";
		for (int i = 0; i <= args.length - 1; i++) {
			msg += args[i] + " ";
		}
		msg = msg.substring(0, msg.length() - 1);
		plugin.broadcastTrade(ChatColor.WHITE + "" + p.getName() + ChatColor.WHITE + ": " + ChatColor.WHITE + msg);
		return true;
    }
}