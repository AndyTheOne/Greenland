package main.java.hultberg.sur.Hultberg.commands;

import java.util.Arrays;
import java.util.Comparator;

import main.java.hultberg.sur.Hultberg.Hultberg;
import main.java.hultberg.sur.Hultberg.handlers.CommandHandler;
import main.java.hultberg.sur.Hultberg.handlers.users.UserHandler;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class WhoCommand extends CommandHandler {

    private UserHandler userHandler;

    public WhoCommand(Hultberg instance) {
        super(instance);
        setStatus(0);
        this.userHandler = instance.getUserHandler();
    }
	
    @Override
    public boolean onPlayerCommand(Player player, Command command, String label,
			String[] args) {
        if (args.length == 0) {
            Player[] onlinePlayers = this.plugin.getServer().getOnlinePlayers();
            String utskrift = ChatColor.WHITE + "ONLINE: ";

            String[] stab = getUsersSortOnAccess(10, onlinePlayers);
            String[] vakt = getUsersSortOnAccess(5, onlinePlayers);
            String[] builder = getUsersSortOnAccess(1, onlinePlayers);
            String[] gjest = getUsersSortOnAccess(0, onlinePlayers);

            for (int i = 0; i < stab.length; i++) {
                utskrift += stab[i] + ChatColor.WHITE + ", ";
            }

            for (int i = 0; i < vakt.length; i++) {
                utskrift += vakt[i] + ChatColor.WHITE + ", ";
            }

            for (int i = 0; i < builder.length; i++) {
                utskrift += builder[i] + ChatColor.WHITE + ", ";
            }

            for (int i = 0; i < gjest.length; i++) {
                utskrift += gjest[i] + ChatColor.WHITE + ", ";
            }
            utskrift = utskrift.substring(0, utskrift.length() - 2) + ".";
            player.sendMessage(utskrift);
            player.sendMessage("Online players: " + onlinePlayers.length + ".");
            return true;
        }
		return false;
	}

    public String[] getUsersSortOnAccess(int accessLevel, Player[] users) {
        if (users.length > 0) {
            int counter = 0;
            String[] usersSorted = new String[countUsersOnAccess(accessLevel,
                    users)];
            for (int i = 0; i < users.length; i++) {
                int userAccessLevel = this.userHandler.getUserStatus(users[i]);
                if (userAccessLevel == accessLevel) {
                    usersSorted[counter] = this.userHandler.getNameColorRaw(users[i].getName());
                    counter++;
                }
            }
            NameComparator c = new NameComparator();
            Arrays.sort(usersSorted, c);
            counter = 0;
            return usersSorted;
        } else {
            return null;
        }
    }
    
    public int countUsersOnAccess(int accessLevel, Player[] users) {
        int counter = 0;
        for (int i = 0; i < users.length; i++) {
            if (this.userHandler.getUserStatus(users[i]) == accessLevel) {
                counter++;
            }
        }
        return counter;
    }
}

/**
 * Sørger for at sorteringa av spillernavn ikke tar hensyn til store bokstaver.
 */

class NameComparator implements Comparator<String> {
    public int compare(String strA, String strB) {
        return strA.compareToIgnoreCase(strB);
    }
}
