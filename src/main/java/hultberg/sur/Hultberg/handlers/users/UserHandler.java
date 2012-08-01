package main.java.hultberg.sur.Hultberg.handlers.users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;

import main.java.hultberg.sur.Hultberg.Hultberg;
import main.java.hultberg.sur.Hultberg.utils.MySQLHandler;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class UserHandler {

    private HashMap<Player, PlayerData> users;
    private MySQLHandler sqlHandler;
    private PreparedStatement getUserPS;
    private PreparedStatement playerMatch;
    private PreparedStatement getUserFromUIDPS;
    private Hultberg plugin;
    private Connection conn;
	private PreparedStatement getUserPSID;
	private PreparedStatement getUserHome;

    public UserHandler(Hultberg instance) {
        this.plugin = instance;
        this.sqlHandler = instance.getSqlHandler();
        this.users = new HashMap<Player, PlayerData>();
    }

    public boolean initialize() {
        this.conn = this.sqlHandler.getConnection();
        try {
            this.getUserPS = this.conn.prepareStatement("SELECT * FROM users WHERE `name` = ?");
            this.getUserPSID = this.conn.prepareStatement("SELECT * FROM users WHERE `id` = ?");
            this.playerMatch = this.conn.prepareStatement("SELECT name FROM users WHERE `name` LIKE '%?%'");
            this.getUserFromUIDPS = this.conn.prepareStatement("SELECT name FROM users WHERE `id` = ?");
            this.getUserHome = this.conn.prepareStatement("SELECT * FROM homes WHERE `uid` = ?");
        } catch (SQLException e) {
            Hultberg.log.log(Level.SEVERE, "[Hultberg] Feil initialisering av prepared statements i UserHandler: ", e);
            return false;
        }
        return true;
    }
    
    public boolean exit(){
    	try {
			this.getUserPS.close();
			this.getUserPSID.close();
			this.playerMatch.close();
			this.getUserFromUIDPS.close();
			this.getUserHome.close();
		} catch (SQLException e){
			Hultberg.log.log(Level.SEVERE, "[Hultberg] Kan ikke lukke PreparedStatement-tilkobling. Kontakt utvikler om problemet.");
			return false;
		}
		return true;
    }

    public void login(Player p) {
        if (userExists(p)) {
            if (this.users.containsKey(p)) {
                this.users.remove(p);
            }
            this.users.put(p, getPlayerData(p));
        } else {
            if (register(p)) {
                this.users.put(p, getPlayerData(p));
            }
        }
    }

    public void logout(Player p) {
        this.users.remove(p);
    }

    public boolean register(Player p) {
        if (sqlHandler.update("REPLACE INTO `users` (`id`, `name`, `status`, `active`, `last_login`, `groupID`) VALUES (NULL, '" + p.getName() + "', 0, UNIX_TIMESTAMP(), UNIX_TIMESTAMP(), '0')")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean register(String name) {
        if (sqlHandler.update("REPLACE INTO `users` (`id`, `name`, `status`, `active`, `last_login`, `groupID`) VALUES (NULL, '" + name + "', '0', UNIX_TIMESTAMP(), UNIX_TIMESTAMP(), '0')")) {
            return true;
        } else {
            return false;
        }
    }

    public PlayerData getCachedPlayerData(Player p) {
        return this.users.get(p);
    }

    public PlayerData getPlayerData(Player p) {
        PlayerData pd = new PlayerData(plugin);
        try {
            this.getUserPS.setString(1, p.getName());
            ResultSet rs = this.getUserPS.executeQuery();

            while (rs.next()) {
                pd.setUID(rs.getInt(1));
                pd.setStatus(rs.getInt(3));
                pd.setPlayer(p);
                pd.setGroup(rs.getInt(6));
            }
        } catch (SQLException e) {
            Hultberg.log.log(Level.SEVERE, "[Hultberg] MySQL Error: " + Thread.currentThread().getStackTrace()[0].getMethodName(), e);
        }
        return pd;
    }

    public PlayerData getPlayerData(String name) {
        PlayerData pd = new PlayerData(plugin);
        try {
            this.getUserPS.setString(1, name);
            ResultSet rs = this.getUserPS.executeQuery();

            while (rs.next()) {
                pd.setUID(rs.getInt(1));
                pd.setStatus(rs.getInt(3));
                pd.setGroup(rs.getInt(6));
            }
        } catch (SQLException e) {
            Hultberg.log.log(Level.SEVERE, "[Hultberg] MySQL Error: " + Thread.currentThread().getStackTrace()[0].getMethodName(), e);
        }
        return pd;
    }
    
    public Location getHome(Player p) {
    	Location loc = null;
        try {
            this.getUserHome.setInt(1, this.getUID(p));

            ResultSet rs = this.getUserHome.executeQuery();

            while (rs.next()) {
            	String world = rs.getString("world");
                int x = rs.getInt("x");
                int y = rs.getInt("y");
                int z = rs.getInt("z");

                if (world != null) {
                    loc = new Location(plugin.getServer().getWorld(world),
                            x, y, z);
                }
            }
        } catch (SQLException e) {
            Hultberg.log.log(Level.SEVERE, "[Hultberg] MySQL Error: " + Thread.currentThread().getStackTrace()[0].getMethodName(), e);
        }
        return loc;
    }

    public String playerMatch(String name) {
        String player = null;
        try {
            this.playerMatch.setString(1, name);
            ResultSet rs = this.playerMatch.executeQuery();

            while(rs.next()) {
                player = rs.getString(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return player;
    }

    public void reloadUser(String name) {
        Player p = this.plugin.playerMatch(name);
        if (p != null) {
            this.users.remove(p);
            this.users.put(p, getPlayerData(p));
        }
    }

    public void reloadUser(Player p) {
        if (this.users.containsKey(p)) {
            this.users.remove(p);
        }
        this.users.put(p, getPlayerData(p));
    }

    public boolean userExists(Player p) {
        boolean exist = false;
        try {
            this.getUserPS.setString(1, p.getName());

            ResultSet rs = this.getUserPS.executeQuery();

            while (rs.next()) {
                exist = true;
            }
        } catch (SQLException e) {
            Hultberg.log.log(Level.SEVERE, "[Hultberg] MySQL Error: " + Thread.currentThread().getStackTrace()[0].getMethodName(), e);
        }
        return exist;
    }

    public boolean userExists(String name) {
        boolean exist = false;
        try {
            this.getUserPS.setString(1, name);

            ResultSet rs = this.getUserPS.executeQuery();

            while (rs.next()) {
                exist = true;
            }
        } catch (SQLException e) {
            Hultberg.log.log(Level.SEVERE, "[Hultberg] MySQL Error: " + Thread.currentThread().getStackTrace()[0].getMethodName(), e);
        }
        return exist;
    }

    public boolean setStatus(String name, int status) {
        if (sqlHandler.update("UPDATE `users` SET status='" + status + "' WHERE name='" + name + "'")) {
            return true;
        } else {
            return false;
        }
    }
    
    public boolean setGroup(Player p, int gid) {
        if (sqlHandler.update("UPDATE `users` SET groupID='" + gid + "' WHERE id='" + this.getUID(p) + "'")) {
            return true;
        } else {
            return false;
        }
    }
    

	public boolean setGroup(String name, int gid) {
		if (sqlHandler.update("UPDATE `users` SET groupID='" + gid + "' WHERE name='" + name + "'")) {
            return true;
        } else {
            return false;
        }
	}

    public int getUserStatus(Player p) {
        return this.users.get(p).getStatus();
    }

    public int getUserStatus(String name) {
        PlayerData pd = getPlayerData(name);
        return pd.getStatus();
    }

    public int getUID(Player p) {
        return this.users.get(p).getUID();
    }
    
    public int getUID(String target) {
		int uid = 0;
        try {
            this.getUserPS.setString(1, target);

            ResultSet rs = this.getUserPS.executeQuery();

            while (rs.next()) {
                uid = rs.getInt(1);
            }
        } catch (SQLException e) {
            Hultberg.log.log(Level.SEVERE, "[Hultberg] MySQL Error: " + Thread.currentThread().getStackTrace()[0].getMethodName(), e);
        }
        return uid;
	}
    
    public int getGID(Player p) {
        return this.users.get(p).getGroup();
    }
    
	public int getGID(String target) {
		int gid = 0;
        try {
            this.getUserPS.setString(1, target);

            ResultSet rs = this.getUserPS.executeQuery();

            while (rs.next()) {
                gid = rs.getInt(6);
            }
        } catch (SQLException e) {
            Hultberg.log.log(Level.SEVERE, "[Hultberg] MySQL Error: " + Thread.currentThread().getStackTrace()[0].getMethodName(), e);
        }
        return gid;
	}
	

	public int getGID(int owner) {
		int gid = 0;
        try {
            this.getUserPSID.setInt(1, owner);

            ResultSet rs = this.getUserPSID.executeQuery();

            while (rs.next()) {
                gid = rs.getInt(6);
            }
        } catch (SQLException e) {
            Hultberg.log.log(Level.SEVERE, "[Hultberg] MySQL Error: " + Thread.currentThread().getStackTrace()[0].getMethodName(), e);
        }
        return gid;
	}

    public String getNameFromUID(int uid) {
        String name = null;
        try {
            this.getUserFromUIDPS.setInt(1, uid);

            ResultSet rs = this.getUserFromUIDPS.executeQuery();

            while (rs.next()) {
                name = rs.getString(1);
            }
        } catch (SQLException e) {
            Hultberg.log.log(Level.SEVERE, "[Hultberg] MySQL Error: " + Thread.currentThread().getStackTrace()[0].getMethodName(), e);
        }
        return name;
    }

    public boolean canBuild(Player p) {
        if (getUserStatus(p) < 1) {
            p.sendMessage(ChatColor.RED + "You don't have permission to build.");
            p.sendMessage(ChatColor.RED + "Type /reg.");
            return false;
        } else {
            return true;
        }
    }

    public String getNameColor(Player p) {
    	if (getUserStatus(p.getName()) == Hultberg.ADMIN) {
            return Hultberg.ADMIN_COLOR + "[Stab] " + p.getName() + ChatColor.WHITE;
        } else if (getUserStatus(p.getName()) == Hultberg.MOD) {
            return Hultberg.MOD_COLOR + "[Mod] " + p.getName() + ChatColor.WHITE;
        } else if (getUserStatus(p.getName()) == Hultberg.BUILDER) {
            return Hultberg.BUILDER_COLOR + p.getName() + ChatColor.WHITE;
        } else if (getUserStatus(p.getName()) == Hultberg.GUEST) {
            return Hultberg.GUEST_COLOR + "[Guest] " + p.getName() + ChatColor.WHITE;
        } else {
            return p.getName();
        }
    }
    
    public String getNameColorRaw(Player p) {
    	if (getUserStatus(p.getName()) == Hultberg.ADMIN) {
            return Hultberg.ADMIN_COLOR + p.getName() + ChatColor.WHITE;
        } else if (getUserStatus(p.getName()) == Hultberg.MOD) {
            return Hultberg.MOD_COLOR + p.getName() + ChatColor.WHITE;
        } else if (getUserStatus(p.getName()) == Hultberg.BUILDER) {
            return Hultberg.BUILDER_COLOR + p.getName() + ChatColor.WHITE;
        } else if (getUserStatus(p.getName()) == Hultberg.GUEST) {
            return Hultberg.GUEST_COLOR + p.getName() + ChatColor.WHITE;
        } else {
            return p.getName();
        }
    }

    public String getNameColor(String name) {
        if (getUserStatus(name) == Hultberg.ADMIN) {
            return Hultberg.ADMIN_COLOR + "[Stab] " + name + ChatColor.WHITE;
        } else if (getUserStatus(name) == Hultberg.MOD) {
            return Hultberg.MOD_COLOR + "[Mod] " + name + ChatColor.WHITE;
        } else if (getUserStatus(name) == Hultberg.BUILDER) {
            return Hultberg.BUILDER_COLOR + name + ChatColor.WHITE;
        } else if (getUserStatus(name) == Hultberg.GUEST) {
            return Hultberg.GUEST_COLOR + "[Guest] " + name + ChatColor.WHITE;
        } else {
            return name;
        }
    }

	public int getUIDFromDB(String player) {
		int id = 0;
		try {
            this.getUserPS.setString(1, player);

            ResultSet rs = this.getUserPS.executeQuery();

            while (rs.next()) {
                id = rs.getInt("id");
            }
        } catch (SQLException e) {
            Hultberg.log.log(Level.SEVERE, "[Hultberg] MySQL Error: " + Thread.currentThread().getStackTrace()[0].getMethodName(), e);
        }
		
		return id;
	}

	public String getNameColorHtml(Player p) {
        if (getUserStatus(p) == Hultberg.ADMIN) {
            return "<span style='color:orange;'>" + "[Stab] " + p.getName() + "</span>";
        } else if (getUserStatus(p) == Hultberg.MOD) {
        	return "<span style='color:blue;'>" + "[Mod] " + p.getName() + "</span>";
        } else if (getUserStatus(p) == Hultberg.BUILDER) {
        	return "<span style='color:black;'>" + p.getName() + "</span>";
        } else if (getUserStatus(p) == Hultberg.GUEST) {
        	return "<span style='color:gray;'>" + "[Guest] " + p.getName() + "</span>";
        } else {
        	return "<span style='color:black;'>" + p.getName() + "</span>";
        }
    }

	public String getNameColorRaw(String name) {
		if (getUserStatus(name) == Hultberg.ADMIN) {
            return Hultberg.ADMIN_COLOR + name + ChatColor.WHITE;
        } else if (getUserStatus(name) == Hultberg.MOD) {
            return Hultberg.MOD_COLOR + name + ChatColor.WHITE;
        } else if (getUserStatus(name) == Hultberg.BUILDER) {
            return Hultberg.BUILDER_COLOR + name + ChatColor.WHITE;
        } else if (getUserStatus(name) == Hultberg.GUEST) {
            return Hultberg.GUEST_COLOR + name + ChatColor.WHITE;
        } else {
            return name;
        }
	}	
}