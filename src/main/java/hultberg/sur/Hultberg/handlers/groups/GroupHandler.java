package main.java.hultberg.sur.Hultberg.handlers.groups;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import main.java.hultberg.sur.Hultberg.Hultberg;
import main.java.hultberg.sur.Hultberg.handlers.users.UserHandler;
import main.java.hultberg.sur.Hultberg.utils.MySQLHandler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class GroupHandler {
	
	private MySQLHandler sqlHandler;
	private Hultberg plugin;
	private Connection conn;
	private PreparedStatement getGroupPS;
	private UserHandler userHandler;
	private PreparedStatement getGroupMembers;
	@SuppressWarnings("unused")
	private PreparedStatement getGroupInvBySender;
	private PreparedStatement getGroupInvByInvited;
	private PreparedStatement getInviteByID;
	private PreparedStatement getUserInvites;
	private PreparedStatement getUserInvitesCount;
	private PreparedStatement getMembers;

	public GroupHandler(Hultberg instance) {
        this.plugin = instance;
        this.sqlHandler = instance.getSqlHandler();
        this.userHandler = instance.getUserHandler();
    }

    public boolean initialize() {
        this.conn = this.sqlHandler.getConnection();
        try {
        	this.getGroupPS = this.conn.prepareStatement("SELECT * FROM groups WHERE `gid` = ?");
        	this.getGroupInvByInvited = this.conn.prepareStatement("SELECT * FROM `group_invs` WHERE `inved_uid` = ? AND to_gid = ?");
        	this.getGroupInvBySender = this.conn.prepareStatement("SELECT * FROM `group_invs` WHERE `sender_uid` = ? AND to_gid = ?");
        	this.getUserInvites = this.conn.prepareStatement("SELECT * FROM `group_invs` WHERE `inved_uid` = ?");
        	this.getInviteByID = this.conn.prepareStatement("SELECT * FROM `group_invs` WHERE `invid` = ?");
        	this.getMembers = this.conn.prepareStatement("SELECT * FROM users WHERE groupID = ?");
        	this.getGroupMembers = this.conn.prepareStatement("SELECT COUNT(*) AS rowCount FROM `users` WHERE `groupID` = ?");
        	this.getUserInvitesCount = this.conn.prepareStatement("SELECT COUNT(*) AS rowCount FROM `group_invs` WHERE `inved_uid` = ?");
        } catch (SQLException e) {
            Hultberg.log.log(Level.SEVERE, "[Hultberg] Feil initialisering av prepared statements i UserHandler: ", e);
            return false;
        }
        return true;
    }
    
    public String getGroupName(int gid){
    	String name = null;
    	if(gid == 0){
    		name = "None";
    	} else {
            try {
                this.getGroupPS.setInt(1, gid);
                ResultSet rs = this.getGroupPS.executeQuery();

                while (rs.next()) {
                    name = rs.getString(3);
                }
            } catch (SQLException e) {
                Hultberg.log.log(Level.SEVERE, "[Hultberg] MySQL Error: " + Thread.currentThread().getStackTrace()[0].getMethodName(), e);
            }
    	}
        return name;
    }
    
    public int getGroupIdByInvId(int invid){
    	int gid = 0;
    	try {
    		this.getInviteByID.setInt(1, invid);
            ResultSet rs = this.getInviteByID.executeQuery();

            while (rs.next()) {
                gid = rs.getInt(4);
            }
        } catch (SQLException e) {
            Hultberg.log.log(Level.SEVERE, "[Hultberg] MySQL Error: " + Thread.currentThread().getStackTrace()[0].getMethodName(), e);
        }
    	return gid;
    }
    
    public void newGroup(String name, Player owner){
    	// In group?
    	if(this.hasGroup(owner)){
    		owner.sendMessage(ChatColor.GRAY + "You are already in a group.");
    		owner.sendMessage(ChatColor.GRAY + "To leave it, type "+ChatColor.WHITE+"/group leave"+ChatColor.GRAY+".");
    	} else { // ok
    		
    		// Create
    		int new_GID = sqlHandler.insert("INSERT INTO `groups`(`owner_uid`, `name`, `noInv`) VALUES ('"+this.userHandler.getUID(owner)+"','"+name+"', '0')");
    		
    		// Did create go ok?
    		if(new_GID == 0){ // failed
    			owner.sendMessage(ChatColor.RED + "Can't create group, contact a mod/admin about the problem!");
    		} else { // created!
    			// Update owner group.
    			if(sqlHandler.update("UPDATE users SET groupID = '"+new_GID+"' WHERE id = '"+this.userHandler.getUID(owner)+"'")){
    				// Okay, group changed. Let's tell user.
    				owner.sendMessage(ChatColor.GRAY + "Group was created!");
    				owner.sendMessage(ChatColor.GRAY + "Invite user's to the group with "+ChatColor.WHITE+"/group inv [username]"+ChatColor.GRAY+"!");
    				this.userHandler.reloadUser(owner);
    			} else {
    				owner.sendMessage(ChatColor.RED + "Can't change your group, contact a mod/admin about the problem!");
    			}
    		}    		
    	}
    }
    
    public void sendInviteToUser(Player from, String to, int gid){
    	if(!this.hasGroup(from)){
    		from.sendMessage("§cYou are not member of any group. Type §6/reloaduser§c if you think this is an error.");
    	} else {
    		if(Bukkit.getServer().getPlayer(to) != null){
    			/*
    			 * Online, with Player.
    			 */
    			Player target = plugin.playerMatch(to);
    			if(this.canInvite(from, gid)){
    				if(this.isInvited(target, gid)){
    					from.sendMessage(ChatColor.BLUE + target.getName() + ChatColor.RED + " has been invited to the group already.");
    				} else {
    					int inv_id = sqlHandler.insert("INSERT INTO `group_invs`(`inved_uid`, `sender_uid`, `to_gid`, `accepted`) VALUES ('"+this.userHandler.getUID(target)+"','"+this.userHandler.getUID(from)+"','"+gid+"','0')");
    					from.sendMessage(ChatColor.GRAY + "You have invited "+target.getName()+" to the group.");
    					target.sendMessage(ChatColor.GRAY + "You got an invite to join "+ChatColor.GREEN+this.getGroupName(gid)+ChatColor.GRAY+" by "+ChatColor.WHITE+from.getName()+ChatColor.GRAY+".");
    					target.sendMessage(ChatColor.GRAY + "Type "+ChatColor.WHITE+"/group accept "+inv_id+ChatColor.GRAY+" to join.");
    				}
    			} else {
    				from.sendMessage(ChatColor.RED + "Only owner of the group can invite people.");
    			}
    		} else {
    			/*
    			 * Offline, with string.
    			 */
    			if(this.userHandler.userExists(to)){
    				if(this.canInvite(from, gid)){
        				if(this.isInvited(to, gid)){
        					from.sendMessage(ChatColor.BLUE + to + ChatColor.RED + " has been invited to the group already.");
        				} else {
        					@SuppressWarnings("unused")
							int inv_id = sqlHandler.insert("INSERT INTO `group_invs`(`inved_uid`, `sender_uid`, `to_gid`, `accepted`) VALUES ('"+this.userHandler.getUID(to)+"','"+this.userHandler.getUID(from)+"','"+gid+"','0')");
        					from.sendMessage(ChatColor.GRAY + "You have invited "+to+" to the group.");
        				}
        			} else {
        				from.sendMessage(ChatColor.RED + "Only owner of the group can invite people.");
        			}
    			} else {
    				from.sendMessage("§c404, player not found.");
    			}
    		}
    	}
    }
    
    public void getGroupInfo(int groupID, Player asker){
    	if(this.hasGroup(asker)){
    		// Har gruppe. :D
    		asker.sendMessage("§6=========== §9Group info§6 ===========");
    		asker.sendMessage(ChatColor.DARK_GREEN + "Name: §f"+this.getGroupName(groupID));
    		asker.sendMessage(ChatColor.DARK_GREEN + "Owner: §f"+this.userHandler.getNameColorRaw(this.userHandler.getNameFromUID(this.getGroupOwner(groupID))));
    		getMembers(asker, groupID);
    	}
    }
    
    public void getMembers(Player asker, int groupID){
    	try{
    		this.getMembers.setInt(1, groupID);
    		ResultSet rs = this.getMembers.executeQuery();
        
    		String members = ChatColor.DARK_GREEN + "Members: §f";
    		
    		while (rs.next()) {
    			members += rs.getString("name") + "§7,§f ";
    		}
    		
    		members = members.substring(0, members.length() - 2) + "§7.§f";
    		asker.sendMessage(members);
    	} catch (SQLException e) {
            Hultberg.log.log(Level.SEVERE, "[Hultberg] Feil i GroupHandler: ", e);
            return;
        }
    }

	// THIS COMMAND SHALL NOT BE CALLED FROM A COMMAND. THIS FUNCTION FORCE DELETE A GROUP AND DO NOT RESET THE USERS/MEMBERS GROUPID!
    public boolean deleteGroup(int gid){
    	if(gid != 0){
    		if(sqlHandler.update("DELETE FROM groups WHERE gid = '"+gid+"'")){
    			// Delete INVS to.
    			if(sqlHandler.update("DELETE FROM `group_invs` WHERE `to_gid` = '"+gid+"'")){
        			return true;       			
        		} else {
        			return false;
        		}
    		} else {
    			return false;
    		}
    	} else {
    		return false;
    	}
    }
    
    public void leaveGroup(Player user){
    	// In group?
    	if(this.hasGroup(user) && (this.getGroupOwner(this.userHandler.getGID(user)) != this.userHandler.getUID(user))){
    		this.userHandler.setGroup(user, 0);
    		this.userHandler.reloadUser(user);
    		user.sendMessage(ChatColor.GRAY + "You have left the group.");
    		plugin.broadcastGroup(ChatColor.GRAY + "("+ ChatColor.WHITE + this.getGroupName(this.userHandler.getGID(user)) + ChatColor.GRAY + ") §9"+user.getName()+"§c has left the group.", this.userHandler.getGID(user));
    	} else if(this.getMembersInt(this.userHandler.getGID(user)) == 1){
    		// There is only one member left. Thats the owner, let he/her leave the group and delete it.
    		if(this.deleteGroup(this.userHandler.getGID(user))){
    			this.userHandler.setGroup(user, 0);
        		user.sendMessage(ChatColor.GRAY + "Since you are the last member, the group gets deleted.");
        		user.sendMessage(ChatColor.GRAY + "You have left the group.");
        		this.userHandler.reloadUser(user);
    		} else {
    			user.sendMessage(ChatColor.RED + "Can't delete the group, contact a mod/admin about the problem!");
    		}    		
    	} else if(this.getGroupOwner(this.userHandler.getGID(user)) == this.userHandler.getUID(user)){
    		user.sendMessage(ChatColor.GRAY + "You are the owner of this group, to leave it you must give the ownership to another member.");
    		user.sendMessage(ChatColor.GRAY + "Type "+ChatColor.WHITE+"/group cop [username]"+ChatColor.GRAY+" to make that user new owner of group.");
    	} else { // ok
    		user.sendMessage(ChatColor.GRAY + "You are not a member of any group.");
    	}
    }
    
    public void kickUser(String target, Player p){
    	if(this.getGroupOwner(this.userHandler.getGID(p)) == this.userHandler.getUID(p)){
    		if(Bukkit.getServer().getPlayer(target) != null){
        		// target is online
    			Player target_p = plugin.playerMatch(target);
    			if(this.userHandler.getGID(target_p) == this.userHandler.getGID(p)){
    				this.userHandler.setGroup(target_p, 0);
        			p.sendMessage(ChatColor.GRAY + "You kicked "+ChatColor.WHITE+target_p.getName()+ChatColor.GRAY+" out of the group.");
        			target_p.sendMessage(ChatColor.RED+"You was kicked out of the group by §9"+p.getName()+"§c.");
        			plugin.broadcastGroup(ChatColor.GRAY + "("+ ChatColor.WHITE + this.getGroupName(this.userHandler.getGID(p)) + ChatColor.GRAY + ") §9"+target_p.getName()+"§c was kicked out of the group by §9"+p.getName()+"§c.", this.userHandler.getGID(p));
        			this.userHandler.reloadUser(target_p);
    			} else {
    				p.sendMessage(ChatColor.WHITE + target_p.getName() + ChatColor.GRAY + " is not a member of your group.");
    			}
        	} else {
        		// target is offline, do it with a string.
        		if(this.userHandler.userExists(target)){
        			if(this.userHandler.getGID(target) == this.userHandler.getGID(p)){
        				this.userHandler.setGroup(target, 0);
        				p.sendMessage(ChatColor.GRAY + "You kicked "+ChatColor.WHITE+target+ChatColor.GRAY+" out of the group.");
        				plugin.broadcastGroup(ChatColor.GRAY + "("+ ChatColor.WHITE + this.getGroupName(this.userHandler.getGID(p)) + ChatColor.GRAY + ") §9"+target+"§c was kicked out of the group by §9"+p.getName()+"§c.", this.userHandler.getGID(p));
        			} else {
        				p.sendMessage(ChatColor.WHITE + target + ChatColor.GRAY + " is not a member of your group.");
        			}
        		} else {
        			p.sendMessage(ChatColor.RED + "404, player not found.");
        		}
        	}
    	} else {
    		// Sender of command is not owner. :o
    		p.sendMessage(ChatColor.GRAY + "You are not the owner of this group.");
    	}
    }
    
    public void changeGroupOwnership(String newOwnerString, Player oldOwner){    	
    	if(Bukkit.getServer().getPlayer(newOwnerString) != null){
    		/*
    		 * User is online!
    		 */
    		Player newOwner = plugin.playerMatch(newOwnerString);
    		if(this.getGroupOwner(this.userHandler.getGID(oldOwner)) == this.userHandler.getUID(oldOwner)){
        		if(this.userHandler.getGID(newOwner) == this.userHandler.getGID(oldOwner)){
            		// Okay, user sending command is owner of this group.
            		// Set new owner.
            		if(sqlHandler.update("UPDATE `groups` SET `owner_uid` = '"+this.userHandler.getUID(newOwner)+"' WHERE gid = '"+this.userHandler.getGID(oldOwner)+"'")){
            			// changed
            			oldOwner.sendMessage(ChatColor.WHITE + newOwner.getName() + ChatColor.GRAY + " is now the new owner.");
            			newOwner.sendMessage(ChatColor.GRAY + "You are the new owner of "+ChatColor.WHITE+this.getGroupName(this.userHandler.getGID(newOwner))+ChatColor.GRAY+".");
            			this.userHandler.reloadUser(newOwner);
            			this.userHandler.reloadUser(oldOwner);
            			plugin.broadcastGroup(ChatColor.GRAY + "("+ ChatColor.WHITE + this.getGroupName(this.userHandler.getGID(oldOwner)) + ChatColor.GRAY + ") §9"+newOwner.getName()+"§c is the new owner of this group.", this.userHandler.getGID(oldOwner));
            		} else {
            			// nope
            			oldOwner.sendMessage(ChatColor.RED + "Can't change group owner, contact a mod/admin about the problem!");
            		}
        		} else {
        			oldOwner.sendMessage(ChatColor.WHITE + newOwner.getName() + ChatColor.GRAY + " is not a member of your group.");
        		}
        	} else {
        		// Sender of command is not owner. :o
        		oldOwner.sendMessage(ChatColor.GRAY + "You are not the owner of this group.");
        	}
    	} else {
    		/*
    		 * Offline is in DEV!
    		 */
    		if(this.userHandler.userExists(newOwnerString)){
    			oldOwner.sendMessage(ChatColor.WHITE + newOwnerString + ChatColor.GRAY + " is offline. Change ownership function can only change if "+ChatColor.WHITE + newOwnerString + ChatColor.GRAY + " is online.");
    			oldOwner.sendMessage(ChatColor.GRAY + "Or, you can kick members and then leave the group. §f/group kick [member]" + ChatColor.GRAY + ".");
    		} else {
    			oldOwner.sendMessage("§c404, player not found.");
    		}
    	}
    	
    }
    
    public int getMembersInt(int gid){
    	int n = 0;
    	if (gid == 0) {
    		// HMM
    	} else {
    		try {
    			getGroupMembers.setInt(1, gid);
    			ResultSet rs = getGroupMembers.executeQuery();
    			if (rs.next()) {
    				n = rs.getInt("rowCount");
    				rs.beforeFirst();
    			}
    		} catch (SQLException e) {
    			Hultberg.log.log(Level.SEVERE, "[Hultberg] Feil i GroupHandler: ", e);
    		}
    	}
    	return n;
    }
    
    public boolean hasGroup(Player uid){
    	if(this.userHandler.getGID(uid) == 0){
    		return false;
    	} else {
    		return true;
    	}
    }
    
    public int getGroupOwner(int gid){
    	int owner = 0;
    	if(gid == 0){
    		owner = 0;
    	} else {
            try {
                this.getGroupPS.setInt(1, gid);
                ResultSet rs = this.getGroupPS.executeQuery();

                while (rs.next()) {
                    owner = rs.getInt(2);
                }
            } catch (SQLException e) {
                Hultberg.log.log(Level.SEVERE, "[Hultberg] MySQL Error: " + Thread.currentThread().getStackTrace()[0].getMethodName(), e);
            }
    	}
        return owner;
    }
    
    public boolean isInvited(Player p, int gid){
    	boolean isInvited = false;
    	
    	try {
    		this.getGroupInvByInvited.setInt(1, this.userHandler.getUID(p));
            this.getGroupInvByInvited.setInt(2, gid);
            ResultSet rs = this.getGroupInvByInvited.executeQuery();

            while (rs.next()) {
                isInvited = true;
            }
        } catch (SQLException e) {
            Hultberg.log.log(Level.SEVERE, "[Hultberg] MySQL Error: " + Thread.currentThread().getStackTrace()[0].getMethodName(), e);
        }
    	
		return isInvited;
    }
    
    
    public boolean isInvited(String to, int gid) {
    	boolean isInvited = false;
    	
    	try {
    		this.getGroupInvByInvited.setInt(1, this.userHandler.getUID(to));
            this.getGroupInvByInvited.setInt(2, gid);
            ResultSet rs = this.getGroupInvByInvited.executeQuery();

            while (rs.next()) {
                isInvited = true;
            }
        } catch (SQLException e) {
            Hultberg.log.log(Level.SEVERE, "[Hultberg] MySQL Error: " + Thread.currentThread().getStackTrace()[0].getMethodName(), e);
        }
    	
		return isInvited;
	}
    
    public boolean validInvite(Player to, int invid) {
    	boolean isInvited = false;
    	
    	try {
    		this.getInviteByID.setInt(1, invid);
            ResultSet rs = this.getInviteByID.executeQuery();

            while (rs.next()) {
                isInvited = true;
            }
        } catch (SQLException e) {
            Hultberg.log.log(Level.SEVERE, "[Hultberg] MySQL Error: " + Thread.currentThread().getStackTrace()[0].getMethodName(), e);
        }
    	
		return isInvited;
	}
    
    public void acceptInv(Player p, int invid){
    	if(this.hasGroup(p)){
    		p.sendMessage(ChatColor.RED + "You are already in a group, type §6/group leave§c to leave it.");
    	} else {
    		// No group.
    		if(this.validInvite(p, invid)){
    			int groupID = this.getGroupIdByInvId(invid);
    			if(sqlHandler.update("DELETE FROM `group_invs` WHERE invid = '"+invid+"'")){
    				this.userHandler.setGroup(p, groupID);
    				this.userHandler.reloadUser(p);
    				p.sendMessage(ChatColor.GRAY + "You accpeted the invite, welcome to the group!");
    				plugin.broadcastGroup("§7(§f"+this.getGroupName(groupID)+"§7) §a"+p.getName()+" has joined the group.", groupID);
    			} else {
    				
    			}
    		} else {
    			p.sendMessage(ChatColor.RED + "That is not a valid invitation id.");
    		}
    	}
    }
    
    public void denyInv(Player p, int invid){
		if(this.validInvite(p, invid)){
			if(sqlHandler.update("DELETE FROM `group_invs` WHERE invid = '"+invid+"'")){
				p.sendMessage(ChatColor.GRAY + "You denied the invite.");
			}
		} else {
			p.sendMessage(ChatColor.RED + "That is not a valid invitation id.");
		}
    }
    
    public boolean hasAccepted(Player p, int gid){
    	boolean accepted = false;
    	
    	try {
            this.getGroupInvByInvited.setInt(1, this.userHandler.getUID(p));
            this.getGroupInvByInvited.setInt(2, gid);
            ResultSet rs = this.getGroupInvByInvited.executeQuery();

            while (rs.next()) {
            	if(rs.getInt(5) == 1){
            		accepted = true;
            	} else {
            		accepted = false;
            	}
            }
        } catch (SQLException e) {
            Hultberg.log.log(Level.SEVERE, "[Hultberg] MySQL Error: " + Thread.currentThread().getStackTrace()[0].getMethodName(), e);
        }
    	
		return accepted;
    }
    
    public boolean canInvite(Player p, int gid){
    	boolean canInvite = true;
    	int noInv = 0;
    	try {
            this.getGroupPS.setInt(1, gid);
            ResultSet rs = this.getGroupPS.executeQuery();

            while (rs.next()) {
                noInv = rs.getInt(4);
                if(noInv == 0){
                	canInvite = true;
                } else if(noInv == 1 && this.getGroupOwner(gid) == this.userHandler.getUID(p)){
                	canInvite = true;
                } else if(noInv == 1 && this.getGroupOwner(gid) != this.userHandler.getUID(p)){
                	canInvite = false;
                }
            }
        } catch (SQLException e) {
            Hultberg.log.log(Level.SEVERE, "[Hultberg] MySQL Error: " + Thread.currentThread().getStackTrace()[0].getMethodName(), e);
        }
    	return canInvite;
    }
    
    public boolean isOwner(Player uid){
    	if(this.userHandler.getGID(uid) == 0){
    		return false;
    	} else {
    		return true;
    	}
    }
    
    public boolean isSameGroup(Player uid1, Player uid2){
    	if(uid1 == null || uid2 == null){
    		return false;
    	} else {
    		if(this.userHandler.getGID(uid1) != 0 || this.userHandler.getGID(uid2) != 0){
    			if(this.userHandler.getGID(uid1) == this.userHandler.getGID(uid2)){
    				return true;
    			} else {
    				return false;
    			}
    		}
    	}
		return false;
    }
    
    public void listInvites(Player p){
    	try {
            //this.plugin.getServer().broadcastMessage("Player: " + p);
            //int uid = this.userHandler.getUID(p);
            //this.plugin.getServer().broadcastMessage("UID: " + uid);
        	
        	this.getUserInvites.setInt(1, this.userHandler.getUID(p));
        	ResultSet rs = getUserInvites.executeQuery();

        	p.sendMessage(ChatColor.GOLD + "======== §aYour invites:");
        	int rowCount = getUsersGroupInvitesCount(p);
        	
        	if (rowCount == 0) {
        		p.sendMessage(ChatColor.WHITE + "No invites found.");
        		return;
        	}

        	@SuppressWarnings("unused")
			int i = 0;
        	while (rs.next()) {
        		i++;
        		p.sendMessage(ChatColor.DARK_GREEN + "Invite to group "+ChatColor.WHITE + this.getGroupName(rs.getInt(4)));
				p.sendMessage(ChatColor.DARK_GREEN + "Sent by: "+ ChatColor.WHITE + this.userHandler.getNameFromUID(rs.getInt(3)) + ChatColor.DARK_GREEN + ".");
				p.sendMessage(ChatColor.GREEN + "Accept, type: §6/group accpet "+rs.getInt(1)+"§a. §cDeny, type: §6/group deny "+rs.getInt(1)+"§c.");
				p.sendMessage(ChatColor.GOLD + "--------------------");
        	}
    	} catch (SQLException e) {
            Hultberg.log.log(Level.SEVERE, "[Hultberg] Feil i GroupHandler: ", e);
            return;
        }
    }

	public int getUsersGroupInvitesCount(Player p) {
		int n = 0;
    	if (p == null) {
    		// HMM
    		return 0;
    	} else {
    		try {
    			this.getUserInvitesCount.setInt(1, this.userHandler.getUID(p));
    			ResultSet rs = getUserInvitesCount.executeQuery();
    			if (rs.next()) {
    				n = rs.getInt("rowCount");
    				rs.beforeFirst();
    			}
    		} catch (SQLException e) {
    			Hultberg.log.log(Level.SEVERE, "[Hultberg] Feil i GroupHandler: ", e);
    		}
    	}
    	return n;
	}
	
	public void settings(){
	}
    
}
