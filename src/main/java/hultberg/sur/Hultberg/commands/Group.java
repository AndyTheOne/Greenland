package main.java.hultberg.sur.Hultberg.commands;

import main.java.hultberg.sur.Hultberg.Hultberg;
import main.java.hultberg.sur.Hultberg.handlers.CommandHandler;
import main.java.hultberg.sur.Hultberg.handlers.groups.GroupHandler;
import main.java.hultberg.sur.Hultberg.handlers.users.UserHandler;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class Group extends CommandHandler {

	private GroupHandler groupHandler;
	private UserHandler userHandler;

	public Group(Hultberg instance) {
        super(instance);
        setStatus(1);
        //this.wh = instance.getWarnHandler();
        this.userHandler = instance.getUserHandler();
        this.groupHandler = instance.getGroupHandler();
    }

    @Override
    public boolean onPlayerCommand(Player p, Command command, String label, String[] args) {
    	if (args.length == 0){
    		p.sendMessage("§6=============={ §9Group System §6}==============");
        	p.sendMessage("§fAvabilble commands:");
        	p.sendMessage("§7/§6group new [name]§f - Make a new group.");
        	p.sendMessage("§7/§6group leave§f - Leave the group you are a member of.");
        	p.sendMessage("§7/§6group kick §7[§fusername§7]§f - Kick a user from the group.");
        	p.sendMessage("§7/§6group inv §7[§fusername§7]§f - Invite a user to your group.");
        	p.sendMessage("§7/§6group accept §7[§finvite ID§7]§f - Accept an invite.");
        	p.sendMessage("§7/§6group deny §7[§finvite ID§7]§f - Deny an invite.");
        	p.sendMessage("§7/§6group invs§f - List your invites.");
        	p.sendMessage("§7/§6group info§f - Info about your group.");
        	p.sendMessage("§7/§6gh§f - Group chat.");
        	p.sendMessage("§7/§6group§f - Show this info.");
        	p.sendMessage("Type /group 2 for page two.");
    		return true;
    	}
    	if (args.length >= 1 && args[0].equalsIgnoreCase("new")) {
    		// New group
    		String name = "";
    		if (args.length > 1) {
    			for (int i = 1; i <= args.length - 1; i++) {
    				name += args[i] + " ";
    			}
    			name = name.substring(0, name.length() - 1);
    			this.groupHandler.newGroup(name, p);
    		} else {
    			p.sendMessage(ChatColor.RED + "You must enter a name of the group.");
    		}
    		return true;
    	} else if (args.length == 1 && args[0].equalsIgnoreCase("leave")) {
    		// Leave
    		this.groupHandler.leaveGroup(p);
    		return true;
    	} else if (args.length >= 1 && args[0].equalsIgnoreCase("cop")) {
    		// Change ownership
    		this.groupHandler.changeGroupOwnership(args[1], p);
    		return true;
    	} else if (args.length >= 1 && args[0].equalsIgnoreCase("kick")) {
    		// Kick a user
    		this.groupHandler.kickUser(args[1], p);
    		return true;
    	} else if (args.length >= 1 && args[0].equalsIgnoreCase("accept")) {
    		// Accept a invite
    		int inviteID = Integer.parseInt(args[1]);
    		this.groupHandler.acceptInv(p, inviteID);
    		return true;
    	} else if (args.length >= 1 && args[0].equalsIgnoreCase("deny")) {
    		// Deny a invite
    		int inviteID = Integer.parseInt(args[1]);
    		this.groupHandler.denyInv(p, inviteID);
    		return true;
    	} else if (args.length >= 1 && args[0].equalsIgnoreCase("inv")) {
    		// Invite a user
    		this.groupHandler.sendInviteToUser(p, args[1], this.userHandler.getGID(p));
    		return true;
    	} else if (args.length == 1 && args[0].equalsIgnoreCase("invs")) {
    		// List invites.
    		this.groupHandler.listInvites(p);
    		return true;
    	} else if (args.length == 1 && args[0].equalsIgnoreCase("settings")) {
    		// List settings.
    		if(args[1].equals("")){
    			// Show settings.
    		} else if(args[1].equals("ownerInvOnly")){
    			if(args[2].equals("")){
    				// WRONG!
    			} else {
    				// Change the value.
    			}
    		}
    		return true;
    	} else if (args.length == 1 && args[0].equalsIgnoreCase("info")) {
    		// List group info.
    		this.groupHandler.getGroupInfo(this.userHandler.getGID(p), p);
    		return true;
    	} else if (args.length == 1 && args[0].equalsIgnoreCase("2")) {
    		// List group info.
    		p.sendMessage("§6=============={ §9Group System page 2 §6}==============");
        	p.sendMessage("§fAvabilble commands:");
        	p.sendMessage("§7/§6group cop §7[§fusername§7]§f - Change ownership of a group.");
        	p.sendMessage("§7/§6group settings [option] [value]§f - Change settings of group.");
    		return true;
    	}
		return false;
    }
}
