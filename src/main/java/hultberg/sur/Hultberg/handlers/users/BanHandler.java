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

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class BanHandler {
	
	@SuppressWarnings("unused")
	private Hultberg plugin;
	private MySQLHandler sqlHandler;
	private PreparedStatement getUserIsBanned;
	private PreparedStatement getUserIsBannedint;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy (HH:mm:ss)");
	private Connection conn;
	private UserHandler users;

	public BanHandler(Hultberg instance){
		this.plugin = instance;
		this.sqlHandler = instance.getSqlHandler();
		this.users = instance.getUserHandler();
	}
	
	public boolean initialize() {
        this.conn = this.sqlHandler.getConnection();
        try {
        	this.getUserIsBanned = this.conn.prepareStatement("SELECT * FROM `bans` WHERE banned_user = ?");
            this.getUserIsBannedint = this.conn.prepareStatement("SELECT COUNT(*) AS rowCount FROM `bans` WHERE `banned_user` = ?");
        } catch (SQLException e) {
        	Hultberg.log.log(Level.SEVERE, "[Ban] Feil initialisering av prepared statements i BanHandler: ", e);
            return false;
        }
        return true;
    }
	
	public boolean exit(){
		try {
			this.getUserIsBanned.close();
			this.getUserIsBannedint.close();
		} catch (SQLException e){
			Hultberg.log.log(Level.SEVERE, "[Ban] Kan ikke lukke PreparedStatement-tilkobling. Kontakt utvikler om problemet.");
			return false;
		}
		return true;
	}
	
	public boolean setBan(int uid, int uid2, String reason) {
		if (sqlHandler.update("INSERT INTO `bans` (`banned_user`, `banner`, `reason`, `date`) VALUES ('" + uid + "', '" + uid2 + "', '" + reason + "', NOW())")) {
            return true;
        } else {
            return false;
        }		
	}
	
	public int isBannedint(String p){
		int rowCount = 0;
    	try {
    		getUserIsBannedint.setInt(1, this.users.getUID(p));
    		ResultSet rs = getUserIsBannedint.executeQuery();
    		if (rs.next()) {
    			rowCount = rs.getInt("rowCount");
    			rs.beforeFirst();
    		}
    	} catch (SQLException e) {
    		Hultberg.log.log(Level.SEVERE, "[Ban] Feil i BanHandler: ", e);
    	}
    	return rowCount;
    }
	
	public boolean isBanned(Player p) {
		int rowCount = isBannedint(p.getName());

        if (rowCount == 1) {
        	return true;
        } else {
        	return false;
        }
	}

	public boolean isBanned(String string) {
		int rowCount = isBannedint(string);

        if (rowCount == 1) {
        	return true;
        } else {
        	return false;
        }
	}

	public String getReason(Player p) {
		String reason = null;
		try {
            this.getUserIsBanned.setInt(1, this.users.getUID(p.getName()));
            ResultSet rs = this.getUserIsBanned.executeQuery();

            while (rs.next()) {
                reason = rs.getString("reason");
            }
        } catch (SQLException e) {
        	Hultberg.log.log(Level.SEVERE, "[Ban] MySQL Error: " + Thread.currentThread().getStackTrace()[0].getMethodName(), e);
        }
		return reason;
	}
	
	public String getBanner(Player p) {
		String banner = null;
		try {
            this.getUserIsBanned.setInt(1, this.users.getUID(p.getName()));
            ResultSet rs = this.getUserIsBanned.executeQuery();

            while (rs.next()) {
                banner = rs.getString("banner");
            }
        } catch (SQLException e) {
        	Hultberg.log.log(Level.SEVERE, "[Ban] MySQL Error: " + Thread.currentThread().getStackTrace()[0].getMethodName(), e);
        }
		return banner;
	}
	
	public String getDate(Player p) {
		String n = null;
		try {
            this.getUserIsBanned.setInt(1, this.users.getUID(p.getName()));
            ResultSet rs = this.getUserIsBanned.executeQuery();

            while (rs.next()) {
                Date date = new Date(rs.getLong("date") * 1000);
                n = dateFormat.format(date);
            }
        } catch (SQLException e) {
        	Hultberg.log.log(Level.SEVERE, "[Ban] MySQL Error: " + Thread.currentThread().getStackTrace()[0].getMethodName(), e);
        }
		return n;
	}

	public boolean unban(int uid) {
		if (sqlHandler.update("DELETE FROM bans WHERE banned_user = '"+uid+"'")) {
            return true;
        } else {
            return false;
        }
	}

	public String getReason(String reqs) {
		String reason = null;
		try {
            this.getUserIsBanned.setInt(1, this.users.getUID(reqs));
            ResultSet rs = this.getUserIsBanned.executeQuery();

            while (rs.next()) {
                reason = rs.getString("reason");
            }
        } catch (SQLException e) {
        	Hultberg.log.log(Level.SEVERE, "[Ban] MySQL Error: " + Thread.currentThread().getStackTrace()[0].getMethodName(), e);
        }
		return reason;
	}

	public int getBanner(String p) {
		int banner = 0;
		try {
            this.getUserIsBanned.setInt(1, this.users.getUID(p));
            ResultSet rs = this.getUserIsBanned.executeQuery();

            while (rs.next()) {
                banner = rs.getInt("banner");
            }
        } catch (SQLException e) {
        	Hultberg.log.log(Level.SEVERE, "[Ban] MySQL Error: " + Thread.currentThread().getStackTrace()[0].getMethodName(), e);
        }
		return banner;
	}

	public String getDate(String p) {
		String n = null;
		try {
            this.getUserIsBanned.setInt(1, this.users.getUID(p));
            ResultSet rs = this.getUserIsBanned.executeQuery();

            while (rs.next()) {
                Date date = new Date(rs.getLong("date") * 1000);
                n = dateFormat.format(date);
            }
        } catch (SQLException e) {
        	Hultberg.log.log(Level.SEVERE, "[Ban] MySQL Error: " + Thread.currentThread().getStackTrace()[0].getMethodName(), e);
        }
		return n;
	}

	public ChatColor getBanner(int uid) {
		// TODO Auto-generated method stub
		return null;
	}
}
