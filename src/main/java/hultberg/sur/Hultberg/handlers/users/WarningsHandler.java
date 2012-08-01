package main.java.hultberg.sur.Hultberg.handlers.users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

import main.java.hultberg.sur.Hultberg.Hultberg;
import main.java.hultberg.sur.Hultberg.utils.MySQLHandler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WarningsHandler {
	private Connection conn;
	private Hultberg plugin;
	private PreparedStatement getUserWarns;
	private MySQLHandler sqlHandler;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy (HH:mm:ss)");
	private PreparedStatement getUserWarnsCount;
	private BanHandler BanHandler;
	private UserHandler users;

	public WarningsHandler(Hultberg instance) {
        this.plugin = instance;
        this.sqlHandler = instance.getSqlHandler();
        this.BanHandler = instance.getBanHandler();
        this.users = instance.getUserHandler();
    }

    public boolean initialize() {
        this.conn = this.sqlHandler.getConnection();
        try {
        	this.getUserWarns = this.conn.prepareStatement("SELECT * FROM `warns` WHERE `uid` = ?");
            this.getUserWarnsCount = this.conn.prepareStatement("SELECT COUNT(*) AS rowCount FROM `warns` WHERE `uid` = ?");
        } catch (SQLException e) {
            Hultberg.log.log(Level.SEVERE, "[Hultberg] Feil initialisering av prepared statements i WarningsHandler: ", e);
            return false;
        }
        return true;
    }
    
    public boolean exit(){
    	try {
            this.getUserWarns.close();
            this.getUserWarnsCount.close();
        } catch (SQLException e) {
            Hultberg.log.log(Level.SEVERE, "[Hultberg] Feil lukking av prepared statements i WarningsHandler: ", e);
            return false;
        }
        return true;
    }
    
    public int userWarns(String p){
		int rowCount = 0;
		try {
			getUserWarnsCount.setInt(1, this.users.getUID(p));
			ResultSet rs = getUserWarnsCount.executeQuery();
			if (rs.next()) {
				rowCount = rs.getInt("rowCount");
				rs.beforeFirst();
			}
		} catch (SQLException e) {
			Hultberg.log.log(Level.SEVERE, "[Hultberg] Feil i WarningsHandler: ", e);
		}
		return rowCount;
    }
    
    public int userWarnsOnline(Player p){
		int rowCount = 0;
    	try {
    		getUserWarnsCount.setInt(1, this.users.getUID(p));
    		ResultSet rs = getUserWarnsCount.executeQuery();
    		if (rs.next()) {
    			rowCount = rs.getInt("rowCount");
    			rs.beforeFirst();
    		}
    	} catch (SQLException e) {
    		Hultberg.log.log(Level.SEVERE, "[Hultberg] Feil i WarningsHandler: ", e);
    	}
    	return rowCount;
    }
    
    public boolean giveWarn(Player p, String givens, String reason){
		if(reason != null){
			
			if(!this.users.userExists(givens)){
				p.sendMessage("§cUser don't exists.");
				return false;
			}

			Location pll = p.getLocation();
			double px = pll.getBlockX();
			double py = pll.getBlockY();
			double pz = pll.getBlockZ();
			// ACTION \m/
			// Gi h*n en advarsel >:D
			if(this.sqlHandler.update("INSERT INTO `warns`(`uid`, `by`, `reason`, `date`, `pos`) VALUES ('" + this.users.getUID(givens) + "', '" + p.getName() + "', '" + reason + "', UNIX_TIMESTAMP(), '"+px+", "+py+", "+pz+"')")){
				// Gi en beskjed.
				if(Bukkit.getServer().getPlayer(givens) != null){
					Player player = plugin.playerMatch(givens);
					// ONLINE :D
					player.sendMessage(ChatColor.DARK_GREEN + "You have just got a warning!");
					player.sendMessage(ChatColor.DARK_GREEN + "Reason: " + ChatColor.WHITE + reason + ChatColor.GREEN + ", by " + ChatColor.WHITE + p.getName());
					p.sendMessage(ChatColor.GREEN + "You have just given " + ChatColor.WHITE + player.getName() + ChatColor.GREEN + " a warning.");
					p.sendMessage(ChatColor.WHITE + player.getName() + ChatColor.GREEN + " has now " + ChatColor.WHITE + userWarnsOnline(player) + ChatColor.GREEN + " warnings.");
				} else {
					// oFFLINE :(
					p.sendMessage(ChatColor.RED + "The player is offline. So no live-notice is sent.");
					p.sendMessage(ChatColor.GREEN + "You have just given " + ChatColor.WHITE + givens + ChatColor.GREEN + " a warning.");
					p.sendMessage(ChatColor.WHITE + givens + ChatColor.GREEN + " has now " + ChatColor.WHITE + userWarns(givens) + ChatColor.GREEN + " warnings.");
				}
				return true;
			}
		}
		return false;
    }
    
    @SuppressWarnings("unused")
	public void listWarns(Player p){
        
        try {
            //this.plugin.getServer().broadcastMessage("Player: " + p);
            //int uid = this.userHandler.getUID(p);
            //this.plugin.getServer().broadcastMessage("UID: " + uid);
        	
        	getUserWarns.setInt(1, this.users.getUID(p));
        	ResultSet rs = getUserWarns.executeQuery();

        	p.sendMessage(ChatColor.GOLD + "============================");
        	p.sendMessage(ChatColor.WHITE + p.getName() + ChatColor.DARK_GREEN + "'s warnings.");
        	p.sendMessage("");
        	int rowCount = userWarnsOnline(p);

        	if (rowCount == 0) {
        		p.sendMessage(ChatColor.WHITE + "no warnings found.");
        		return;
        	}

        	int i = 0;
        	while (rs.next()) {
        		i++;
        		Date date = new Date(rs.getLong("date") * 1000);
				p.sendMessage(ChatColor.DARK_GREEN + "Warning: "+ChatColor.WHITE + rs.getString("reason"));
				p.sendMessage(ChatColor.DARK_GREEN + "By: "+ ChatColor.WHITE + rs.getString("by") + ChatColor.DARK_GREEN + ". " + ChatColor.GREEN + "Date: " + ChatColor.WHITE + dateFormat.format(date) + ChatColor.GREEN + ".");
				p.sendMessage(ChatColor.DARK_GREEN + "Location: " + ChatColor.WHITE + rs.getString("pos") + ChatColor.GREEN + ".");
        	}
        	p.sendMessage("");
			p.sendMessage(ChatColor.DARK_GREEN + "Totalt " + ChatColor.WHITE + userWarnsOnline(p) + ChatColor.DARK_GREEN + " warnings.");
			p.sendMessage(ChatColor.GOLD + "============================");
    	} catch (SQLException e) {
            Hultberg.log.log(Level.SEVERE, "[Hultberg] Feil i WarningsHandler: ", e);
            return;
        }
    }
    
    @SuppressWarnings("unused")
	public void listWarnsOthers(Player p, String reqs){
    	Player req = plugin.getServer().getOfflinePlayer(reqs).getPlayer();
    	Player onreq = Bukkit.getPlayer(reqs);
    	if (this.users.getUserStatus(p) >= 5) {
    		if(Bukkit.getServer().getPlayer(reqs) != null){
    			try {
    				//this.plugin.getServer().broadcastMessage("Player: " + p);
    				//int uid = this.userHandler.getUID(p);
    				//this.plugin.getServer().broadcastMessage("UID: " + uid);
        	
    				getUserWarns.setInt(1, this.users.getUID(req));
    				ResultSet rs = getUserWarns.executeQuery();
    			
    				p.sendMessage(ChatColor.GOLD + "============================");
    				p.sendMessage(ChatColor.WHITE + req.getName() + ChatColor.DARK_GREEN + "'s warnings.");
    				p.sendMessage("");
    				int rowCount = userWarnsOnline(req);
    			
    				if (rowCount == 0) {
    					p.sendMessage(ChatColor.WHITE + "no warnings found.");
    				} else {
    					int i = 0;
        				while (rs.next()) {
        	        		i++;
        	        		Date date = new Date(rs.getLong("date") * 1000);
        					p.sendMessage(ChatColor.DARK_GREEN + "Warning: "+ChatColor.WHITE + rs.getString("reason"));
        					p.sendMessage(ChatColor.DARK_GREEN + "By: "+ ChatColor.WHITE + rs.getString("by") + ChatColor.DARK_GREEN + ". " + ChatColor.GREEN + "Date: " + ChatColor.WHITE + dateFormat.format(date) + ChatColor.GREEN + ".");
        					p.sendMessage(ChatColor.DARK_GREEN + "Location: " + ChatColor.WHITE + rs.getString("pos") + ChatColor.GREEN + ".");
        	        	}
        				p.sendMessage("");
        				p.sendMessage(ChatColor.DARK_GREEN + "Totalt " + ChatColor.WHITE + userWarnsOnline(req) + ChatColor.DARK_GREEN + " warnings.");
    				}    				
    				p.sendMessage(ChatColor.GOLD + "============================");
    			} catch (SQLException e) {
    				Hultberg.log.log(Level.SEVERE, "[Hultberg] Feil i WarningsHandler: ", e);
    				return;
    			}
    		} else {
    			try {
    				//this.plugin.getServer().broadcastMessage("Player: " + p);
    				//int uid = this.userHandler.getUID(p);
    				//this.plugin.getServer().broadcastMessage("UID: " + uid);
        	
    				getUserWarns.setInt(1, this.users.getUID(reqs));
    				ResultSet rs = getUserWarns.executeQuery();
    			
    				p.sendMessage(ChatColor.GOLD + "============================");
    				p.sendMessage(ChatColor.WHITE + reqs + ChatColor.DARK_GREEN + "'s warnings. §f(UID:"+this.users.getUID(reqs)+")");
    				p.sendMessage("");
    				int rowCount = userWarns(reqs);
    			
    				if (rowCount == 0) {
    					p.sendMessage(ChatColor.WHITE + "no warnings found.");
    				} else {
    					int i = 0;
        				while (rs.next()) {
        	        		i++;
        	        		Date date = new Date(rs.getLong("date") * 1000);
        					p.sendMessage(ChatColor.DARK_GREEN + "Warning: "+ChatColor.WHITE + rs.getString("reason"));
        					p.sendMessage(ChatColor.DARK_GREEN + "By: " + ChatColor.WHITE + rs.getString("by") + ChatColor.DARK_GREEN + ". " + ChatColor.GREEN + "Date: " + ChatColor.WHITE + dateFormat.format(date) + ChatColor.GREEN + ".");
        					p.sendMessage(ChatColor.DARK_GREEN + "Location: " + ChatColor.WHITE + rs.getString("pos") + ChatColor.GREEN + ".");
        	        	}
        				p.sendMessage("");
        				p.sendMessage(ChatColor.DARK_GREEN + "Totalt " + ChatColor.WHITE + userWarns(reqs) + ChatColor.DARK_GREEN + " warnings.");
    				}
    				if(this.BanHandler.isBanned(reqs)){
    					// Personen er bannet
    					p.sendMessage("----------------------------------------");
    					p.sendMessage(ChatColor.RED + "User is banned for §f" + this.BanHandler.getReason(reqs) + " §cby §f" + this.users.getNameFromUID(this.BanHandler.getBanner(reqs)) + " §c(§f" + this.BanHandler.getDate(reqs) + "§c)");
    				}
    				p.sendMessage(ChatColor.GOLD + "============================");
    			} catch (SQLException e) {
    				Hultberg.log.log(Level.SEVERE, "[Hultberg] Feil i WarningsHandler: ", e);
    				return;
    			}
    			p.sendMessage(ChatColor.RED + "Hultberg-WarnHandler : Offline-player mode is stil in development.");
    		}
    	} else {
    		p.sendMessage(ChatColor.RED + "You do not have permission.");
    	}
    }
}