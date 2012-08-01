package main.java.hultberg.sur.Hultberg.commands;

import main.java.hultberg.sur.Hultberg.Hultberg;
import main.java.hultberg.sur.Hultberg.handlers.CommandHandler;
import main.java.hultberg.sur.Hultberg.handlers.Log;
import main.java.hultberg.sur.Hultberg.handlers.users.UserHandler;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class Kick extends CommandHandler {

    private UserHandler userHandler;
    private Hultberg plugin;
	private Log ch;

    public Kick(Hultberg instance) {
        super(instance);
        this.plugin = instance;
        setStatus(5);
        this.userHandler = instance.getUserHandler();
        this.ch = instance.getLog();
    }

    @Override
    public boolean onPlayerCommand(Player p, Command command, String label, String[] args) {
        if (args.length >= 1) {
        	Player v = plugin.playerMatch(args[0]);
        	if (this.userHandler.getUserStatus(p) >= this.userHandler.getUserStatus(v)) {
        		String kick = "";
        		if (args.length > 1) {
        			for (int i = 1; i <= args.length - 1; i++) {
        				kick += args[i] + " ";
        			}
        			Location vl = v.getLocation();
        			kick = kick.substring(0, kick.length() - 1);
        			//logHandler.addLine(this.userHandler.getUID(p), this.userHandler.getUID(v), ActionID.KICK, 0, kick);
        			this.plugin.broadcastAll(this.userHandler.getNameColor(v) + ChatColor.YELLOW + " kicked for : " + ChatColor.WHITE + kick);
        			this.plugin.broadcastAll(ChatColor.YELLOW + "Kicked by : " + this.userHandler.getNameColor(p));
        			v.kickPlayer(kick);
        			v.getWorld().strikeLightningEffect(vl);
        			this.ch.KickAndBanLogAddLine(p, v.getName(), kick, 1);
        		} else {
        			Location vl = v.getLocation();
        			//logHandler.addLine(this.userHandler.getUID(p), this.userHandler.getUID(v), ActionID.KICK, 0, "NULL");
                    this.plugin.broadcastAll(this.userHandler.getNameColor(v) + ChatColor.YELLOW + " kicked for : " + ChatColor.WHITE + "No reason given, we do not need one.");
                    this.plugin.broadcastAll(ChatColor.YELLOW + "Kicked by: " + this.userHandler.getNameColor(p));
                    v.kickPlayer("No reason given, we do not need one.");
                    v.getWorld().strikeLightningEffect(vl);
                    this.ch.KickAndBanLogAddLine(p, v.getName(), "No reason given, we do not need one.", 1);
                }
                if(kick == ""){
                	//plugin.getIRC().getIRCConnection().doPrivmsg("#craftit.logg", v.getName() + " ble kicket av " + p.getName() + "." + kick);
                } else {
                	//plugin.getIRC().getIRCConnection().doPrivmsg("#craftit.logg", v.getName() + " ble kicket av " + p.getName() + " for " + kick);
                }
                    
                } else {
                    p.sendMessage(ChatColor.RED + "You do not have permission to kick this user.");
                }
            }
        return true;
    }
}