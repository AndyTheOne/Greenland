package main.java.hultberg.sur.Hultberg.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import main.java.hultberg.sur.Hultberg.Hultberg;
import main.java.hultberg.sur.Hultberg.handlers.CommandHandler;
import main.java.hultberg.sur.Hultberg.handlers.users.UserHandler;
import main.java.hultberg.sur.Hultberg.utils.MySQLHandler;

public class SetHomeCommand extends CommandHandler {

	private MySQLHandler sqlHandler;
	private UserHandler userHandler;
    
    public SetHomeCommand(Hultberg instance) {
    	super(instance);
        setStatus(5);
        this.userHandler = instance.getUserHandler();
        this.sqlHandler = instance.getSqlHandler();
    }

    @Override
    public boolean onPlayerCommand(Player player, Command command,
            String label, String[] args) {
        if(args.length == 0) {
        	Location location = player.getLocation();
            String worldName = location.getWorld().getName();
            int x = (int) location.getX();
            int y = (int) location.getY();
            int z = (int) location.getZ();

            if(sqlHandler.update("REPLACE INTO `homes` (uid, world, x, y, z)VALUES('"+this.userHandler.getUID(player)+"', '"+worldName+"', '"+x+"', '"+y+"', '"+z+"')")) {
                player.sendMessage(ChatColor.DARK_GREEN
                        + "Home is changed.");
            } else {
                player.sendMessage(ChatColor.RED
                        + "En feil oppstod. Fikk ikke satt nytt home.");
            }
            return true;
        }
        return false;
    }
}
