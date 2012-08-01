package main.java.hultberg.sur.Hultberg.commands;

import main.java.hultberg.sur.Hultberg.Hultberg;
import main.java.hultberg.sur.Hultberg.handlers.CommandHandler;
import main.java.hultberg.sur.Hultberg.handlers.Log;
import main.java.hultberg.sur.Hultberg.handlers.users.BanHandler;
import main.java.hultberg.sur.Hultberg.handlers.users.UserHandler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class Ban extends CommandHandler {

    private UserHandler userHandler;
    private Hultberg plugin;
	private BanHandler bans;
	private Log ch;

    public Ban(Hultberg instance) {
        super(instance);
        this.plugin = instance;
        setStatus(5);
        this.userHandler = instance.getUserHandler();
        this.bans = instance.getBanHandler();
        this.ch = instance.getLog();
    }

    @Override
    public boolean onPlayerCommand(Player p, Command command, String label, String[] args) {
    	if (args.length >= 1) {
    		// Make a ban.
    		Player v;
    		if(this.bans.isBanned(args[0])){
    			p.sendMessage("§cBruker er bannet. (§a"+args[0]+"§c)");
    		} else {
    			v = Bukkit.getPlayer(args[0]);
    			String reason = "";
        		if (args.length > 1) {
        			for (int i = 1; i <= args.length - 1; i++) {
        				reason += args[i] + " ";
        			}
        			reason = reason.substring(0, reason.length() - 1);
        			plugin.broadcastAll(this.userHandler.getNameColor(args[0]) + ChatColor.DARK_GREEN + " banned for: " + ChatColor.WHITE + reason);
        			plugin.broadcastAll(ChatColor.DARK_GREEN + "Banned by: "+this.userHandler.getNameColor(p));
        			if(Bukkit.getServer().getPlayer(args[0]) != null){
        				v.kickPlayer(reason);
        			}
        		} else {
        			plugin.broadcastAll(this.userHandler.getNameColor(args[0]) + ChatColor.DARK_GREEN + " banned for: " + ChatColor.WHITE + "No reason given.");
        			plugin.broadcastAll(ChatColor.DARK_GREEN + "Banned by: "+this.userHandler.getNameColor(p));
        			if(Bukkit.getServer().getPlayer(args[0]) != null){
        				v.kickPlayer(reason);
        			}
        		}
    			this.bans.setBan(this.userHandler.getUID(args[0]), this.userHandler.getUID(p), reason);
    			this.ch.KickAndBanLogAddLine(p, args[0], reason, 2);
    		}
        }
		return true;
    }
}