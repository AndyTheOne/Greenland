package main.java.hultberg.sur.Hultberg.commands;

import main.java.hultberg.sur.Hultberg.Hultberg;
import main.java.hultberg.sur.Hultberg.handlers.CommandHandler;
import main.java.hultberg.sur.Hultberg.handlers.users.UserHandler;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class AddUser extends CommandHandler {

    @SuppressWarnings("unused")
	private UserHandler userHandler;

    public AddUser(Hultberg instance) {
        super(instance);
        setStatus(5);
    }

    @Override
    public boolean onPlayerCommand(Player p, Command command, String label, String[] args) {
            if(args.length == 1) {
                Player v = plugin.playerMatch(args[0]);
                String name = v.getName();
                if(v != null) {
                	plugin.setUserStatus(name, 1);
                	p.sendMessage(ChatColor.DARK_GREEN + "User " + ChatColor.WHITE + v.getName() + ChatColor.DARK_GREEN + " is now added.");
                    v.sendMessage(ChatColor.DARK_GREEN + "You have now building permit.");
                    
                    plugin.broadcastAll("§c[!] §fWelcome, §a" + v.getName() + "§f to Greenland PvP & PvE server!");
                }
            } else {
            	p.sendMessage(ChatColor.RED + "Please type in a username.");
            	p.sendMessage(ChatColor.WHITE + "Usage: " + ChatColor.GRAY + "/" + ChatColor.GOLD + "add" + ChatColor.GRAY + " [" + ChatColor.GOLD + "username" + ChatColor.GRAY + "]");
            }
        return true;
    }
}
