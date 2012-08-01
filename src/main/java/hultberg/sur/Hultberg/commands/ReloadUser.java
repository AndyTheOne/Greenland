package main.java.hultberg.sur.Hultberg.commands;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import main.java.hultberg.sur.Hultberg.Hultberg;
import main.java.hultberg.sur.Hultberg.handlers.CommandHandler;

public class ReloadUser extends CommandHandler {
	
	@SuppressWarnings("unused")
	private Hultberg plugin;
	
	public ReloadUser(Hultberg instance) {
        super(instance);
        this.plugin = instance;
        setStatus(0);
        this.userHandler = instance.getUserHandler();
        //this.banHandler = instance.getBanHandler();
    }

    @Override
    public boolean onPlayerCommand(Player p, Command command, String label, String[] args) {
    	this.userHandler.reloadUser(p);
		return false;
    }
}
