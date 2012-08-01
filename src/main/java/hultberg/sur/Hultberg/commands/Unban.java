package main.java.hultberg.sur.Hultberg.commands;

import main.java.hultberg.sur.Hultberg.Hultberg;
import main.java.hultberg.sur.Hultberg.handlers.CommandHandler;
import main.java.hultberg.sur.Hultberg.handlers.users.BanHandler;
import main.java.hultberg.sur.Hultberg.handlers.users.UserHandler;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class Unban extends CommandHandler {

    private UserHandler userHandler;
    private Hultberg plugin;
	private BanHandler bans;

    public Unban(Hultberg instance) {
        super(instance);
        this.plugin = instance;
        setStatus(5);
        this.userHandler = instance.getUserHandler();
        this.bans = instance.getBanHandler();
    }

    @Override
    public boolean onPlayerCommand(Player p, Command command, String label, String[] args) {
		// Make a ban.
    	if(!this.userHandler.userExists(args[0])) {
    		p.sendMessage("§cUser not found. (§a"+args[0]+"§c)");
    	} else if(!this.bans.isBanned(args[0])){
			p.sendMessage("§cUser is not banned. (§a"+args[0]+"§c)");
		} else {
    		plugin.broadcastAll(ChatColor.GREEN + "User "+ChatColor.WHITE+args[0]+ChatColor.GREEN + " is no longer banned.");
			this.bans.unban(this.userHandler.getUID(args[0]));
		}
		return true;
    }
}
