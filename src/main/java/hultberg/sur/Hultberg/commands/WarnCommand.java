package main.java.hultberg.sur.Hultberg.commands;

import main.java.hultberg.sur.Hultberg.Hultberg;
import main.java.hultberg.sur.Hultberg.handlers.CommandHandler;
import main.java.hultberg.sur.Hultberg.handlers.users.WarningsHandler;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class WarnCommand extends CommandHandler {

	private WarningsHandler wh;

	public WarnCommand(Hultberg instance) {
        super(instance);
        setStatus(5);
        this.wh = instance.getWarnHandler();
        this.userHandler = instance.getUserHandler();
    }

    @Override
    public boolean onPlayerCommand(Player p, Command command, String label, String[] args) {        
        if (args.length >= 1) {
            if (this.userHandler.getUserStatus(p) >= 5) {
                if (args.length > 1) {
                    String kick = "";
                    for (int i = 1; i <= args.length - 1; i++) {
                        kick += args[i] + " ";
                    }
                    if(this.wh.giveWarn(p, args[0], kick)){
                    	return true;
                    }
                    return true;
                }
            }
        }
    	return false;
    }
}
