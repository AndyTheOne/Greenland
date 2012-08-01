package main.java.hultberg.sur.Hultberg.commands;

import main.java.hultberg.sur.Hultberg.Hultberg;
import main.java.hultberg.sur.Hultberg.handlers.CommandHandler;
import main.java.hultberg.sur.Hultberg.handlers.groups.GroupHandler;
import main.java.hultberg.sur.Hultberg.handlers.users.UserHandler;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class GroupChat extends CommandHandler {

	private GroupHandler groupHandler;
	private UserHandler userHandler;

	public GroupChat(Hultberg instance) {
        super(instance);
        setStatus(1);
        //this.wh = instance.getWarnHandler();
        this.userHandler = instance.getUserHandler();
        this.groupHandler = instance.getGroupHandler();
    }

	@Override
	public boolean onPlayerCommand(Player p, Command command, String label, String[] args) {
		if(this.groupHandler.hasGroup(p)){
    		String msg = "";
    		for (int i = 0; i <= args.length - 1; i++) {
    			msg += args[i] + " ";
    		}
    		msg = msg.substring(0, msg.length() - 1);
    		plugin.broadcastGroup("§7(§f"+this.groupHandler.getGroupName(this.userHandler.getGID(p))+"§7) §a"+p.getName()+": "+msg, this.userHandler.getGID(p));
    		return true;
		}
		return false;
	}

}
