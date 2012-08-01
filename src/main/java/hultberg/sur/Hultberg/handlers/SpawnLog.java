package main.java.hultberg.sur.Hultberg.handlers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;

import main.java.hultberg.sur.Hultberg.Hultberg;

import org.bukkit.entity.Player;

public class SpawnLog {
    @SuppressWarnings("unused")
	private Hultberg plugin;
    @SuppressWarnings("unused")
	private File log;

    public SpawnLog(Hultberg plugin) {
        this.plugin = plugin;
    }
    
    public void addLine(Player p, String msg){
		try {
	    	java.util.Date date= new java.util.Date();
	    	
	    	BufferedWriter out = new BufferedWriter(new FileWriter("spawnlog.html", true));
	        out.write("["+new Timestamp(date.getTime())+"] " + p.getName() + " spawnet " + msg + "<br />");
	        out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
