package main.java.hultberg.sur.Hultberg.handlers.blocks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import main.java.hultberg.sur.Hultberg.Hultberg;
import main.java.hultberg.sur.Hultberg.handlers.users.UserHandler;
import main.java.hultberg.sur.Hultberg.utils.MySQLHandler;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class BlockProtect {

    private MySQLHandler sqlHandler;
    private UserHandler userHandler;
    private BlockLog blocklog;

    private Connection conn;
    public PreparedStatement add;
    public PreparedStatement isProtected;
    public PreparedStatement getOwner;
	@SuppressWarnings("unused")
	private Hultberg plugin;

    public BlockProtect(Hultberg instance) {
    	this.plugin = instance;
        this.sqlHandler = instance.getSqlHandler();
        this.blocklog = instance.getBlockLogHandler();
        this.userHandler = instance.getUserHandler();
    }

    public void initialize() {
        this.conn = this.sqlHandler.getConnection();
        try {
            this.add = this.conn.prepareStatement("REPLACE INTO `blocks` (`uid`, `x`, `y`, `z`, `world`) VALUES (?, ?, ?, ?, ?)");
            this.isProtected = this.conn.prepareStatement("SELECT uid FROM blocks WHERE x=? AND y=? AND z=? AND world=?");
            this.getOwner = this.conn.prepareStatement("SELECT uid FROM blocks WHERE x=? AND y=? AND z=? AND world=?");
        } catch (SQLException e) {
            Hultberg.log.log(Level.SEVERE, "[Hultberg] Kunne ikke initialisere prepared statements for BlockProtectHandler.", e);
        }
    }

    public void exit() {
        try {
            this.add.close();
            this.isProtected.close();
            this.getOwner.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public boolean add(Player p, Block b) {
    	int uid = 0;
        uid = this.userHandler.getUID(p);

        try {
            this.blocklog.log(p, b, 1, b.getType().name());

            if (b.getType() == Material.SAPLING) {
                return true;
            } else if (b.getType() == Material.DIRT || b.getType() == Material.GRASS) {
                return true;
            }

            this.add.setInt(1, uid);
            this.add.setShort(2, (short) b.getX());
            this.add.setShort(3, (short) b.getY());
            this.add.setShort(4, (short) b.getZ());
            this.add.setString(5, b.getWorld().getName());
            this.add.executeUpdate();

            return true;
        } catch (SQLException e) {
            Hultberg.log.log(Level.SEVERE, "[Hultberg] MySQL Error: " + Thread.currentThread().getStackTrace()[0].getMethodName(), e);
            p.sendMessage("Could not protect block.");
            return false;
        }
    }
	
	public boolean addPP(String p, Block b) {
		int uid = 0;
        uid = this.userHandler.getUID(p);

        try {
            this.blocklog.log(uid, b, 5, b.getType().name());

            this.add.setInt(1, uid);
            this.add.setShort(2, (short) b.getX());
            this.add.setShort(3, (short) b.getY());
            this.add.setShort(4, (short) b.getZ());
            this.add.setString(5, b.getWorld().getName());
            this.add.executeUpdate();

            return true;
        } catch (SQLException e) {
            Hultberg.log.log(Level.SEVERE, "[Hultberg] MySQL Error: " + Thread.currentThread().getStackTrace()[0].getMethodName(), e);
            return false;
        }		
	}
    
    public boolean addtoServerUser(Player p, Block b) {
        try {

            if (b.getType() == Material.SAPLING) {
                return true;
            } else if (b.getType() == Material.DIRT || b.getType() == Material.GRASS) {
                return true;
            }

            this.add.setInt(1, 1000011);
            this.add.setShort(2, (short) b.getX());
            this.add.setShort(3, (short) b.getY());
            this.add.setShort(4, (short) b.getZ());
            this.add.setString(5, b.getWorld().getName());
            this.add.executeUpdate();

            p.sendMessage(ChatColor.GREEN + "Block protected.");
            return true;
        } catch (SQLException e) {
            Hultberg.log.log(Level.SEVERE, "[Hultberg] MySQL Error: " + Thread.currentThread().getStackTrace()[0].getMethodName(), e);
            p.sendMessage("Could not protect block.");
            return false;
        }
    }

    public boolean delete(Player p, Block b) {
        if (b.getType() == Material.SAPLING) {
            return true;
        } else if (b.getType() == Material.DIRT || b.getType() == Material.GRASS) {
            return true;
        }
        boolean deleted = false;
        int uid = this.userHandler.getUID(p);
        if (isProtected(b)) {
            int owner = getOwner(b);
            if ((owner == uid) || (this.userHandler.getGID(p) == this.userHandler.getGID(owner) && this.userHandler.getGID(p) != 0)) {
                if (sqlHandler.update("DELETE FROM blocks WHERE x='" + b.getX() + "' AND y='" + b.getY() + "' AND z='" + b.getZ() + "' AND world='" + b.getWorld().getName() + "'")) {
                    deleted = true;
                    this.blocklog.log(p, b, 0, b.getType().name());
                    //Hultberg.log.log(Level.INFO, "Logget i BP2");
                } else {
                    deleted = false;
                    p.sendMessage(ChatColor.RED + "Could not delete block from MySQL.");
                }
            } else if(owner == 1000011){
            	deleted = false;
            	p.sendMessage(ChatColor.RED + "A admin/mod has protected this block. Contact a admin/mod if its not supposed to be protected.");
            } else {
            	deleted = false;
                String name = this.userHandler.getNameFromUID(owner);
                p.sendMessage(ChatColor.RED + name + " owns this block. Contact a mod/admin to have this block removed if its not supposed to be there.");
            }
        } else {
            // Blokken er ikke beskyttet så sender kall om å fjerne den
            // Husk å legge til logg etterhvert.
            deleted = true;
            this.blocklog.log(p, b, 0, b.getType().name());
            //Hultberg.log.log(Level.INFO, "Logget i BP1");
        }
        return deleted;
    }
    
    public boolean deleteP(Player p, Block b) {
        if (b.getType() == Material.SAPLING) {
            return true;
        } else if (b.getType() == Material.DIRT || b.getType() == Material.GRASS) {
            return true;
        }
        boolean deleted = false;
        if (isProtected(b)) {
        	if (sqlHandler.update("DELETE FROM blocks WHERE x='" + b.getX() + "' AND y='" + b.getY() + "' AND z='" + b.getZ() + "' AND world='" + b.getWorld().getName() + "'")) {
            	deleted = true;
            	this.blocklog.log(p, b, 6, b.getType().name());
            } else {
                deleted = false;
                p.sendMessage(ChatColor.RED + "Could not delete block from MySQL.");
            }
        } else {
            // Blokken er ikke beskyttet så sender kall om å fjerne den
            // Husk å legge til logg etterhvert.
            deleted = true;
            this.blocklog.log(p, b, 6, b.getType().name());
            Hultberg.log.log(Level.INFO, "Logget i BP1");
        }
        return deleted;
    }

    public boolean delete(Block b) {
        if (b.getType() == Material.SAPLING) {
            return true;
        } else if (b.getType() == Material.DIRT || b.getType() == Material.GRASS) {
            return true;
        }
        if (sqlHandler.update("DELETE FROM blocks WHERE x='" + b.getX() + "' AND y='" + b.getY() + "' AND z='" + b.getZ() + "' AND world='" + b.getWorld().getName() + "'")) {
            return true;
        } else {
            return false;
        }
    }

    public int getOwner(Block b) {
        int uid = -1;
        try {
            this.getOwner.setShort(1, (short) b.getX());
            this.getOwner.setShort(2, (short) b.getY());
            this.getOwner.setShort(3, (short) b.getZ());
            this.getOwner.setString(4, b.getWorld().getName());
            ResultSet rs = this.getOwner.executeQuery();

            while (rs.next()) {
                uid = rs.getInt(1);
            }
        } catch (SQLException e) {
            Hultberg.log.log(Level.SEVERE, "[Hultberg] MySQL Error: " + Thread.currentThread().getStackTrace()[0].getMethodName(), e);
        }
        return uid;
    }

    public boolean isProtected(Block b) {
        if (b.getType() == Material.SAPLING) {
            return false;
        } else if (b.getType() == Material.DIRT || b.getType() == Material.GRASS) {
            return false;
        }
        boolean prot = false;
        try {
            this.isProtected.setShort(1, (short) b.getX());
            this.isProtected.setShort(2, (short) b.getY());
            this.isProtected.setShort(3, (short) b.getZ());
            this.isProtected.setString(4, b.getWorld().getName());
            ResultSet rs = this.isProtected.executeQuery();

            while (rs.next()) {
                prot = true;
            }
        } catch (SQLException e) {
            Hultberg.log.log(Level.SEVERE, "[Hultberg] MySQL Error: " + Thread.currentThread().getStackTrace()[0].getMethodName(), e);
        }
        return prot;
    }
}