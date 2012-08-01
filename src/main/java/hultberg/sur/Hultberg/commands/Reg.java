/*
 * This file is made by Thypthon. And the idea is by Insane by Xstasy and Jcfk
 */

package main.java.hultberg.sur.Hultberg.commands;

import main.java.hultberg.sur.Hultberg.Hultberg;
import main.java.hultberg.sur.Hultberg.handlers.CommandHandler;
import main.java.hultberg.sur.Hultberg.handlers.users.UserHandler;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class Reg extends CommandHandler {

	@SuppressWarnings("unused")
	private UserHandler userHandler;

    public Reg(Hultberg instance) {
        super(instance);
        setStatus(0);
        this.userHandler = instance.getUserHandler();
    }

    public boolean onPlayerCommand(Player p, Command command, String label, String[] args) {
    	/*if(this.userHandler.getUserStatus(p) == 0){
    		p.sendMessage(ChatColor.GOLD + "=============={ §9/REG §6}==============");
    		p.sendMessage(ChatColor.GREEN + "1. §fGoto §ahttp://www.norbuild.ipbhost.com §fand register an account.");
    		p.sendMessage(ChatColor.GREEN + "2. §fClick on §a\"§fBuilding permits§a\"§f and write an application.");
    		p.sendMessage("§a3.§f Say in-game to a §9mod§f/§6admin §fthat you have writen an application.");
    		p.sendMessage("§a4. §fFollow the rest.");
        	return true;
    	} else if(this.userHandler.getUserStatus(p) == 10){
    		p.sendMessage(ChatColor.RED + "(Command preview)");
    		p.sendMessage(ChatColor.GOLD + "=============={ §9/REG §6}==============");
    		p.sendMessage(ChatColor.GREEN + "1. §fGoto §http://www.norbuild.ipbhost.com §fand register an account.");
    		p.sendMessage(ChatColor.GREEN + "2. §fClick on §a\"§fBuilding permits§a\"§f and write an application.");
    		p.sendMessage("§a3.§f Say in-game to a §9mod§f/§6admin §fthat you have writen an application.");
    		p.sendMessage("§a4. §fFollow the rest.");
        	return true;
    	} else {
    		p.sendMessage(ChatColor.RED + "You are not a guest.");
    	}*/
    	p.sendMessage(ChatColor.GOLD + "=============={ §9/REG §6}==============");
    	p.sendMessage(ChatColor.WHITE + "§c[§a!§c]§f"+ChatColor.GRAY+" Ask a mod/admin to be user, because forum is under dev.");
    	p.sendMessage(ChatColor.WHITE + " §9-§f This server has a local chat (128 blocks) to send a shout type /g [msg].");
    	p.sendMessage(ChatColor.WHITE + " §9-§f We have block protection, and to share blocks make a group! /group");
    	p.sendMessage(ChatColor.WHITE + " §9-§f Its PvP, but people in the same group can't kill eatch other.");
    	p.sendMessage(ChatColor.WHITE + " §9-§f TNT and creepers don't explode chest, gold block, diamond bloc, mossy cobble or redstone.");
		return false;
    }
}