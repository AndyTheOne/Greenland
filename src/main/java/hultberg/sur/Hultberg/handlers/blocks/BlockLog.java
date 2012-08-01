package main.java.hultberg.sur.Hultberg.handlers.blocks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;

import main.java.hultberg.sur.Hultberg.Hultberg;
import main.java.hultberg.sur.Hultberg.handlers.users.UserHandler;
import main.java.hultberg.sur.Hultberg.utils.MySQLHandler;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class BlockLog {

    private MySQLHandler sqlHandler;
    private UserHandler userHandler;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy (HH:mm:ss)");

    private Connection conn;
    private PreparedStatement get;
    public PreparedStatement setlog;

    public BlockLog(Hultberg instance) {
        this.sqlHandler = instance.getSqlHandler();
        this.userHandler = instance.getUserHandler();
    }

    public void initialize() {
        this.conn = this.sqlHandler.getConnection();
        try {
            this.setlog = this.conn.prepareStatement("INSERT INTO `blocklog` (`uid`, `x`, `y`, `z`, `action`, `world`, `data`, `time`) VALUES (?, ?, ?, ?, ?, ?, ?, UNIX_TIMESTAMP());");
            this.get = this.conn.prepareStatement("SELECT * FROM blocklog WHERE x=? AND y=? AND z=? AND world=? ORDER BY time ASC");
        } catch (SQLException e) {
            Hultberg.log.log(Level.SEVERE, "[Hultberg] Kunne ikke initialisere prepared statements for BlockLog.", e);
        }
    }

    public void exit() {
        try {
            this.setlog.close();
            this.get.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public boolean log(Player p, Block b, int action, String data) {
        try {
            this.setlog.setInt(1, this.userHandler.getUID(p));
            this.setlog.setShort(2, (short) b.getX());
            this.setlog.setShort(3, (short) b.getY());
            this.setlog.setShort(4, (short) b.getZ());
            this.setlog.setInt(5, action);
            this.setlog.setString(6, b.getWorld().getName());
            this.setlog.setString(7, data);

            this.setlog.executeUpdate();
            //Hultberg.log.log(Level.INFO, "Logget i log(Player)");
            return true;
        } catch (SQLException e) {
            Hultberg.log.log(Level.SEVERE, "[Hultberg] Kunne ikke logge blokkendringen. ", e);
            return false;
        }
        
    }
    
    public boolean logMob(Block b, int action, String data) {
        try {
            this.setlog.setInt(1, 999990);
            this.setlog.setShort(2, (short) b.getX());
            this.setlog.setShort(3, (short) b.getY());
            this.setlog.setShort(4, (short) b.getZ());
            this.setlog.setInt(5, action);
            this.setlog.setString(6, b.getWorld().getName());
            this.setlog.setString(7, data);

            this.setlog.executeUpdate();
            //Hultberg.log.log(Level.INFO, "Logget i log(Player)");
            return true;
        } catch (SQLException e) {
            Hultberg.log.log(Level.SEVERE, "[Hultberg] Kunne ikke logge blokkendringen. ", e);
            return false;
        }
        
    }

    public boolean log(int uid, Block b, int action, String data) {
        try {
            this.setlog.setInt(1, uid);
            this.setlog.setShort(2, (short) b.getX());
            this.setlog.setShort(3, (short) b.getY());
            this.setlog.setShort(4, (short) b.getZ());
            this.setlog.setInt(5, action);
            this.setlog.setString(6, b.getWorld().getName());
            this.setlog.setString(7, data);

            this.setlog.executeUpdate();
            return true;
        } catch (SQLException e) {
            Hultberg.log.log(Level.SEVERE, "[Hultberg] Kunne ikke logge blokkendringen. ", e);
            return false;
        }
    }

    public ArrayList<String> getBlockLog(Block b) {

        ResultSet rs = null;
        ArrayList<String> row = new ArrayList<String>();

        try {
            this.get.setShort(1, (short) b.getX());
            this.get.setShort(2, (short) b.getY());
            this.get.setShort(3, (short) b.getZ());
            this.get.setString(4, b.getWorld().getName());
            this.get.execute();
            rs = this.get.getResultSet();


            while (rs.next()) {

                Date date = new Date(rs.getLong("time") * 1000);
                row.add("[" + dateFormat.format(date) + "]"
                        + this.userHandler.getNameFromUID(rs.getInt("uid"))
                        + ActiontoText(rs.getInt("action"), rs.getString("data")));
            }
            rs.close();

        } catch (SQLException e) {
            Hultberg.log.log(Level.SEVERE, "[Hultberg] SQL Exception i getBlockLog ", e);
        }
        return row;
    }

    public String ActiontoText(int action, String data) {
        if (action == 0) {
            return " removed " + data;
        } else if (action == 1) {
            return " placed " + data;
        } else if (action == 2) {
            return " admin sticked " + data;
        } else if (action == 3) {
            return " as mod opend";
        } else if (action == 4) {
            return " removed " + data + " from the chest";
        } else if (action == 5) {
            return " protected " + data;
        } else if (action == 6) {
            return " unprotected " + data;
        } else if (action == 7) {
            return " creeper/tnt exploded " + data;
        } else {
            return " unknown method";
        }
    }

	    public boolean logP(int uid, int x, int y, int z, String data) {
        try {
            this.setlog.setInt(1, uid);
            this.setlog.setShort(2, (short) x);
            this.setlog.setShort(3, (short) y);
            this.setlog.setShort(4, (short) z);
            this.setlog.setString(5, " portected area");
            this.setlog.setString(6, "world");
            this.setlog.setString(7, data);

            this.setlog.executeUpdate();
            return true;
        } catch (SQLException e) {
            Hultberg.log.log(Level.SEVERE, "[Hultberg] Kunne ikke logge blokkendringen. ", e);
            return false;
        }
    }
}