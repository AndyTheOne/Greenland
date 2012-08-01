package main.java.hultberg.sur.Hultberg.commands;

import main.java.hultberg.sur.Hultberg.Hultberg;
import main.java.hultberg.sur.Hultberg.handlers.CommandHandler;
import main.java.hultberg.sur.Hultberg.handlers.Log;
import main.java.hultberg.sur.Hultberg.handlers.users.UserHandler;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SpawnItem extends CommandHandler {
	
	@SuppressWarnings("unused")
	private UserHandler userHandler;
	private Log ch;

    public SpawnItem(Hultberg instance) {
        super(instance);
        setStatus(5);
        this.userHandler = instance.getUserHandler();
        this.ch = instance.getLog();
    }

    public boolean onPlayerCommand(Player p, Command command, String label, String[] args) {
    	if(args.length == 0){
    		p.sendMessage("§cPlease type in what Item you want to spawn.");
    		return false;
    	} else {
    		int am = 1;
    		if(args[1].equals("") || args[1] == ""){
    			am = Integer.parseInt("1");
    		} else {
    			am = Integer.parseInt(args[1]);
    		}
    		Material mat = Material.matchMaterial(args[0]);
    		ItemStack n = new ItemStack(mat, am);
            p.getInventory().addItem(n);
            p.sendMessage("§aSPAWNED ITEM §c"+am+"§ax§c"+mat.toString());
            this.ch.SpawnLogAddLine(p, am, mat.toString());
            return true;
    	}
    }
}
