package main.java.hultberg.sur.Hultberg.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import main.java.hultberg.sur.Hultberg.Hultberg;
import main.java.hultberg.sur.Hultberg.handlers.CommandHandler;
import main.java.hultberg.sur.Hultberg.handlers.users.UserHandler;

public class ModerateUser extends CommandHandler {
	
	private UserHandler userHandler;

    public ModerateUser(Hultberg instance) {
        super(instance);
        setStatus(10);
        this.userHandler = instance.getUserHandler();
    }

	@Override
	public boolean onPlayerCommand(Player p, Command command, String label,
			String[] args) {
		// TODO Auto-generated method stub
		Player v = plugin.playerMatch(args[1]);
        String name = v.getName();
		
		if(args[0].equalsIgnoreCase("stab")){
			if(args.length == 2) {
	            if(v != null) {
	            	plugin.setUserStatus(name, 10);
	            	plugin.broadcastAll(ChatColor.DARK_GREEN + "Userstatus to " + ChatColor.WHITE + name + ChatColor.DARK_GREEN + " was changed to " + ChatColor.WHITE + "stab " + ChatColor.DARK_GREEN + "by " + ChatColor.WHITE + this.userHandler.getNameColor(p));
	            	PermissionAttachment pa = p.addAttachment(plugin);
	                pa.setPermission("worldedit.*", true);
	            }
	        } else {
	            	p.sendMessage(ChatColor.RED + "Plaese type in a username.");
	        }
		} else if(args[0].equalsIgnoreCase("mod")){
			if(args.length == 2) {
				if(v != null) {
                	plugin.setUserStatus(name, 5);
                	plugin.broadcastAll(ChatColor.DARK_GREEN + "Userstatus to " + ChatColor.WHITE + name + ChatColor.DARK_GREEN + " was changed to " + ChatColor.WHITE + "mod " + ChatColor.DARK_GREEN + "by " + ChatColor.WHITE + this.userHandler.getNameColor(p));
                	PermissionAttachment pa = p.addAttachment(plugin);
                	pa.setPermission("worldedit.fixwater", true);
                	pa.setPermission("worldedit.fixlava", true);
                	pa.setPermission("worldedit.drain", true);
				}
            } else {
            	p.sendMessage(ChatColor.RED + "Please type in a username.");
            }
		} else if(args[0].equalsIgnoreCase("user")){
			if(args.length == 2) {
                
                if(v != null) {
                	plugin.setUserStatus(name, 1);
                	plugin.broadcastAll(ChatColor.DARK_GREEN + "Userstatus to " + ChatColor.WHITE + name + ChatColor.DARK_GREEN + " was changed to " + ChatColor.WHITE + "user " + ChatColor.DARK_GREEN + "by " + ChatColor.WHITE + this.userHandler.getNameColor(p));
                }
            } else {
            	p.sendMessage(ChatColor.RED + "Please type in a username.");
            }
		} else if(args[0].equalsIgnoreCase("guest")){
			if(args.length == 2) {
                if(v != null) {
                	plugin.setUserStatus(name, 0);
                }
            } else {
            	p.sendMessage(ChatColor.RED + "Please type in a username.");
            }
		} else {
			p.sendMessage("/mod [stab/mod/user/guest] [user]");
		}
		
		return true;
	}
}
