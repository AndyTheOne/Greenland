package main.java.hultberg.sur.Hultberg.handlers.groups;

import main.java.hultberg.sur.Hultberg.Hultberg;

import org.bukkit.entity.Player;

public class GroupData {

    @SuppressWarnings("unused")
	private Hultberg plugin;

    public GroupData(Hultberg instance) {
        this.plugin = instance;
    }

    private Player owner;
    private String name;
    private int m;
    private int gid;

    public void setGroupOwner(Player p){
    	this.owner = p;
    }
    
    public Player getGroupOwner(){
    	return owner;
    }
    
    public void setMembers(int members){
    	this.m = members;
    }
    
    public int getMemebers(){
    	return m;
    }
    
    public void setGID(int groupID){
    	this.gid = groupID;
    }
    
    public int getGID(){
    	return gid;
    }
    
    public void setGroupName(String gname){
    	this.name = gname;
    }
    
    public String getGroupName(){
    	return name;
    }

}