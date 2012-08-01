package main.java.hultberg.sur.Hultberg.listeners;

import java.util.List;
import java.util.logging.Level;

import main.java.hultberg.sur.Hultberg.Adminstick;
import main.java.hultberg.sur.Hultberg.Hultberg;
import main.java.hultberg.sur.Hultberg.LogClock;
import main.java.hultberg.sur.Hultberg.PosBook;
import main.java.hultberg.sur.Hultberg.handlers.Log;
import main.java.hultberg.sur.Hultberg.handlers.blocks.BlockLog;
import main.java.hultberg.sur.Hultberg.handlers.blocks.BlockProtect;
import main.java.hultberg.sur.Hultberg.handlers.groups.GroupHandler;
import main.java.hultberg.sur.Hultberg.handlers.users.BanHandler;
import main.java.hultberg.sur.Hultberg.handlers.users.UserHandler;
import main.java.hultberg.sur.Hultberg.handlers.users.WarningsHandler;
import main.java.hultberg.sur.Hultberg.sql.sqlConnector;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;

public class PlayerListener implements Listener {
	
	private Hultberg plugin;
	private UserHandler userHandler;
	@SuppressWarnings("unused")
	private sqlConnector sqlConnector;
	@SuppressWarnings("unused")
	private GroupHandler groupHandler;
	private BlockProtect bp;
	private Log ch;
	private BanHandler bh;
	private BlockLog bl;
	private WarningsHandler wh;

	public PlayerListener(Hultberg instance) {
        this.plugin = instance;
        this.userHandler = instance.getUserHandler();
        this.groupHandler = instance.getGroupHandler();
        this.sqlConnector = instance.getSqlConnector();
        this.bp = instance.getBlockProtectHandler();
        this.ch = instance.getLog();
        this.bh = instance.getBanHandler();
        this.bl = instance.getBlockLogHandler();
        this.wh = instance.getWarnHandler();
    }
	
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent e){
		plugin.sqlConnection();
		Player p = e.getPlayer();
		
		// USER BANNED?!
        if(this.bh.isBanned(p)){
        	// Player is banned, then kick the player with the note.
        	e.disallow(Result.KICK_BANNED, "Banned: "+this.bh.getReason(p));
        	// Send a notice to log.
        	Hultberg.log.log(Level.SEVERE, "Player "+p+" is banned for "+this.bh.getReason(p)+" by "+this.userHandler.getNameFromUID(this.bh.getBanner(p.getName()))+".");
        } else {
        	Hultberg.log.log(Level.SEVERE, p.getName()+" is not banned.");
        }
        
        
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        
        this.userHandler.reloadUser(p);
        this.userHandler.login(p);
        
        PermissionAttachment pa = p.addAttachment(plugin);
        if(this.userHandler.getUserStatus(p) == 10){
        	// Set permissions!
        	pa.setPermission("worldedit.*", true);
        } else if(this.userHandler.getUserStatus(p) == 5){
        	// Set permissions!
        	pa.setPermission("worldedit.navigation.*", true);
        	pa.setPermission("worldedit.fixwater", true);
        	pa.setPermission("worldedit.fixlava", true);
        	pa.setPermission("worldedit.drain", true);
        }
        
        p.sendMessage(ChatColor.GOLD + "================{ " + ChatColor.BLUE + "Greenland PvP & PvE" + ChatColor.GOLD + "}================");
        if(this.userHandler.getUserStatus(p) == 0){
        	p.sendMessage(ChatColor.GOLD + "§a[§c!INFO!§a]§f Type /reg to see info about the server.");
        } else {
        	
        }
        if(this.wh.userWarnsOnline(p) == 0){
        	// NO warns.
        } else {
        	p.sendMessage(ChatColor.DARK_GREEN + "You have "
        				  + ChatColor.WHITE + this.wh.userWarnsOnline(p)
        				  + ChatColor.DARK_GREEN + " warnings.");
        }
        e.setJoinMessage(this.userHandler.getNameColor(p) + ChatColor.GREEN + " logged in.");
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        this.userHandler.logout(e.getPlayer());
        e.setQuitMessage(this.userHandler.getNameColor(p) + ChatColor.RED + " logged out.");
	}
	
	@EventHandler
	public void onPlayerChat(PlayerChatEvent e) {		
        Player p = e.getPlayer();
        String msg = e.getMessage();
        
        if(msg.equals("u00a74u00a75u00a73u00a74v:1")){
        	e.setCancelled(true);
        	return;
        } else {
        	e.setCancelled(true);
            List<Entity> near = p.getNearbyEntities(128,32,128);
    		near.add(p);
    		for (Entity entity : near) {
    			if (entity instanceof Player) {
    				if(this.userHandler.getUserStatus(e.getPlayer()) == 10){
    					((Player)entity).sendMessage(this.userHandler.getNameColor(e.getPlayer()) + ChatColor.GOLD + ": " + ChatColor.WHITE + e.getMessage());
    		        } else if(this.userHandler.getUserStatus(e.getPlayer()) == 5){
    		        	((Player)entity).sendMessage(this.userHandler.getNameColor(e.getPlayer()) + ChatColor.BLUE + ": " + ChatColor.WHITE + e.getMessage());
    		        } else if(this.userHandler.getUserStatus(e.getPlayer()) == 1){
    		        	((Player)entity).sendMessage(this.userHandler.getNameColor(e.getPlayer()) + ": " + ChatColor.WHITE +  e.getMessage());
    		        } else if(this.userHandler.getUserStatus(e.getPlayer()) == 0){
    		        	((Player)entity).sendMessage(this.userHandler.getNameColor(e.getPlayer()) + ChatColor.GRAY + ": " + ChatColor.WHITE + e.getMessage());
    		        }
    			}
    		}
    		
    		this.ch.ChatLogAddLine(p, msg);
        }
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event){
		Player player = event.getPlayer();
        @SuppressWarnings("unused")
		String name = player.getName();
		Location l = event.getTo();
		
        Double x = l.getX();
        Double y = l.getY();
        Double z = l.getZ();
        Float yaw = player.getLocation().getYaw();
        Float pitch = player.getLocation().getPitch();
        if (player.getWorld().getName().equals("world")) {
            if (x <= -2000) {
                Location tp = new Location(player.getWorld(), -2000 + 5, y, z,
                        yaw, pitch);
                player.sendMessage(ChatColor.RED
                        + "You can't cross the border.");
                player.teleport(tp);
                event.setCancelled(true);
            }
            if (x >= 2000) {
                Location tp = new Location(player.getWorld(), 2000 - 5, y, z,
                        yaw, pitch);
                player.sendMessage(ChatColor.RED
                        + "You can't cross the border.");
                player.teleport(tp);
                event.setCancelled(true);
            }
            if (z <= -2000) {
                Location tp = new Location(player.getWorld(), x, y, -2000 + 5,
                        yaw, pitch);
                player.sendMessage(ChatColor.RED
                        + "You can't cross the border.");
                player.teleport(tp);
                event.setCancelled(true);
            }
            if (z >= 2000) {
                Location tp = new Location(player.getWorld(), x, y, 2000 - 5,
                        yaw, pitch);
                player.sendMessage(ChatColor.RED
                        + "You can't cross the border.");
                player.teleport(tp);
                event.setCancelled(true);

            }
        }
	}
	
	@EventHandler
    public void onPlayerKick(PlayerKickEvent e) {
        if (e.getReason().equals("You moved too quickly :( (Hacking?)")) {
        	e.setCancelled(true);
        	return;
        }
        if ((e.getReason().toLowerCase().contains("flying")) || (e.getReason().toLowerCase().contains("floating"))) {
            e.setCancelled(true);
            // WARN ADMINS ABOUT FLYMOD
    		plugin.broadcastAdmin(ChatColor.RED + "User " + ChatColor.BLUE + e.getPlayer().getName() + ChatColor.RED + " is being detected for flymod.");
        }
        e.setLeaveMessage(null);
               
    }
	
    
    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent e){
    	Player p = e.getPlayer();
        if (!this.userHandler.canBuild(p)) {
            e.setCancelled(true);
        } else {
            e.setCancelled(false);
        }
    }
	
	 @EventHandler
	 public void onPlayerInteract(PlayerInteractEvent e) {
		 Player p = e.getPlayer();
		 @SuppressWarnings("unused")
		Material b = e.getMaterial();
	    	
	     // Adminstick
	     new Adminstick(e, plugin);

	     // LogClock - Blokklogg via klokka.
	     new LogClock(e, plugin);
	     
	     // PosBook - /protect postioner.
	     new PosBook(e, plugin);
	     
	     // Chest Protection
	        if (e.getAction() != Action.LEFT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_BLOCK) {
	            return;
	        }
	        if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
	        	return;
	        } else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
	        	Block block = e.getClickedBlock();
	        	int uid = this.userHandler.getUID(p);
	        	if(block.getType() == Material.CHEST){
	        		int owner = bp.getOwner(block);
	                
	                Location l = block.getLocation();
	                @SuppressWarnings("unused")
					Block s = block.getWorld().getBlockAt((int)Math.floor(l.getX()), (int)Math.floor(l.getY()) - 1, (int)Math.floor(l.getZ()));
	                if(block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST){
	                	BlockState state = block.getState();
	                	if(state instanceof Sign){
	                		Sign sign = (Sign)state;
	                		sign.getLine(0).contains("[Public]");
	                		return;
	                	}
	                }         
	                
	                if ((owner == uid) || (this.userHandler.getGID(p) == this.userHandler.getGID(owner) && this.userHandler.getGID(p) != 0)) {
	        		} else if(userHandler.getUserStatus(p) >= 5){
	        			this.bl.log(p, block, 3, block.getType().name());
	        		} else if(owner != uid){
	        			p.sendMessage(ChatColor.RED + "This is not your chest.");
	        			e.setCancelled(true);
	        		} else {
	        			p.sendMessage(ChatColor.RED + "This is not your chest.");
	        			e.setCancelled(true);
	        		}
	        	}
	        }
	        
	        // Furnace Protection
	        if (e.getAction() != Action.LEFT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_BLOCK) {
	            return;
	        }
	        if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
	        	return;
	        } else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
	        	Block block = e.getClickedBlock();
	        	int uid = this.userHandler.getUID(p);
	        	if(block.getType() == Material.FURNACE){
	        		int owner = bp.getOwner(block);
	                
	                if ((owner == uid) || (this.userHandler.getGID(p) == this.userHandler.getGID(owner) && this.userHandler.getGID(p) != 0)) {
	        		} else if(userHandler.getUserStatus(p) >= 5){
	        			this.bl.log(p, block, 3, block.getType().name());
	        		} else if(owner != uid){
	        			p.sendMessage(ChatColor.RED + "This is not your furnace.");
	        			e.setCancelled(true);
	        		} else {
	        			p.sendMessage(ChatColor.RED + "This is not your furnace.");
	        			e.setCancelled(true);
	        		}
	        	}
	        }
	        
	        // Dispenser Protection
	        if (e.getAction() != Action.LEFT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_BLOCK) {
	            return;
	        }
	        if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
	        	return;
	        } else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
	        	Block block = e.getClickedBlock();
	        	int uid = this.userHandler.getUID(p);
	        	if(block.getType() == Material.DISPENSER){
	        		int owner = bp.getOwner(block);
	                
	                if ((owner == uid) || (this.userHandler.getGID(p) == this.userHandler.getGID(owner) && this.userHandler.getGID(p) != 0)) {
	        		} else if(userHandler.getUserStatus(p) >= 5){
	        			this.bl.log(p, block, 3, block.getType().name());
	        		} else if(owner != uid){
	        			p.sendMessage(ChatColor.RED + "This is not your dispenser.");
	        			e.setCancelled(true);
	        		} else {
	        			p.sendMessage(ChatColor.RED + "This is not your dispenser.");
	        			e.setCancelled(true);
	        		}
	        	}
	        }
	        
	        // Door Protection.
	        if (e.getAction() != Action.LEFT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_BLOCK) {
	            return;
	        }
	        if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
	        	return;
	        } else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
	        	Block block = e.getClickedBlock();
	        	int uid = this.userHandler.getUID(p);
	        	if(block.getType() == Material.WOOD_DOOR){
	        		int owner = bp.getOwner(block);
	                
	                if ((owner == uid) || (this.userHandler.getGID(p) == this.userHandler.getGID(owner) && this.userHandler.getGID(p) != 0)) {
	        		} else if(owner != uid){
	        			e.setCancelled(true);
	        		} else {
	        			e.setCancelled(true);
	        		}
	        	}
	        }
	 }
}
