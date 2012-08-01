package main.java.hultberg.sur.Hultberg.handlers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;

import main.java.hultberg.sur.Hultberg.Hultberg;
import main.java.hultberg.sur.Hultberg.handlers.users.UserHandler;

import org.bukkit.entity.Player;

public class Log {
    @SuppressWarnings("unused")
	private Hultberg plugin;
    @SuppressWarnings("unused")
	private File log;
	private UserHandler userHandler;

    public Log(Hultberg plugin) {
        this.plugin = plugin;
        this.userHandler = plugin.getUserHandler();
    }
    
    public void ChatLogAddLine(Player p, String msg){
		try {
			java.util.Date date= new java.util.Date();
			
			String content = "["+new Timestamp(date.getTime())+"] " + this.userHandler.getNameColorHtml(p) + ": " + msg + "<br />";
			 
			BufferedWriter out = new BufferedWriter(new FileWriter("chatlog.html", true));
	        out.write(content);
	        out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void SpawnLogAddLine(Player p, int am, String item){
		try {
			java.util.Date date= new java.util.Date();
			
			String content = "["+new Timestamp(date.getTime())+"] " + p.getName() + " spawnet "+ am + "x" + item + "<br />";
			 
			BufferedWriter out = new BufferedWriter(new FileWriter("spawnlog.html", true));
	        out.write(content);
	        out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void KickAndBanLogAddLine(Player p, String target, String reason, int what){
		try {
			java.util.Date date= new java.util.Date();
			
			String content = "";
			
			if(what == 1){
				content = "["+new Timestamp(date.getTime())+"] " + p.getName() + " kicket " + target + " for " + reason + "<br />";
			} else if(what == 2){
				content = "["+new Timestamp(date.getTime())+"] " + p.getName() + " bannet " + target + " for " + reason + "<br />";
			}
 
			BufferedWriter out = new BufferedWriter(new FileWriter("kickandbanlog.html", true));
	        out.write(content);
	        out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
