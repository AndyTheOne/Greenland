/*
 * This file is made by Thypthon. And the idea is by Insane by Xstasy and Jcfk
 */

package main.java.hultberg.sur.Hultberg.commands;

import main.java.hultberg.sur.Hultberg.Hultberg;
import main.java.hultberg.sur.Hultberg.handlers.CommandHandler;
import main.java.hultberg.sur.Hultberg.handlers.users.WarningsHandler;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class LW extends CommandHandler {

	private WarningsHandler wh;

	public LW(Hultberg instance) {
        super(instance);
        setStatus(1);
        this.wh = instance.getWarnHandler();
        this.userHandler = instance.getUserHandler();
    }

    @Override
    public boolean onPlayerCommand(Player p, Command command, String label, String[] args) {
        // The user wants to see his own warns.
    	this.wh.listWarns(p);
        return true;
    }
}