package main.java.hultberg.sur.Hultberg.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import main.java.hultberg.sur.Hultberg.Hultberg;
import main.java.hultberg.sur.Hultberg.handlers.CommandHandler;
import main.java.hultberg.sur.Hultberg.handlers.users.UserHandler;
import main.java.hultberg.sur.Hultberg.utils.MySQLHandler;

public class HomeCommand extends CommandHandler {

	@SuppressWarnings("unused")
	private MySQLHandler sqlHandler;
	private UserHandler userHandler;
	
    public HomeCommand(Hultberg instance) {
        super(instance);
        setStatus(5);
        this.userHandler = instance.getUserHandler();
        this.sqlHandler = instance.getSqlHandler();
    }

    @Override
    public boolean onPlayerCommand(Player player, Command command,
            String label, String[] args) {
        if (args.length == 0) {
            Location homeLoc = this.userHandler.getHome(player);
            if (homeLoc != null) {
                if (player.teleport(homeLoc)) {
                    return true;
                } else {
                    player.sendMessage(getErrorChatColor()
                            + "En uventet feil oppstod. Kontakt utviklerne.");
                    return true;
                }

            } else {
                player.sendMessage(getErrorChatColor()
                        + "Du har ikke satt et home enda. Bruk /sethome for å sette.");
                return true;
            }
        }
        return false;
    }
}
