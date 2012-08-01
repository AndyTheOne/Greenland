/*
 *   This file is part of Insane.
 *
 *   Insane is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Insane is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with Insane.  If not, see <http://www.gnu.org/licenses/>.
 */

package main.java.hultberg.sur.Hultberg.commands;

import main.java.hultberg.sur.Hultberg.Hultberg;
import main.java.hultberg.sur.Hultberg.handlers.CommandHandler;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.Location;

public class SetSpawn extends CommandHandler {

    public SetSpawn(Hultberg instance) {
        super(instance);
        setStatus(10);
    }

    @Override
    public boolean onPlayerCommand(Player p, Command command, String label, String[] args) {
    	if(p.isOp()){
    		Location spawn = p.getLocation();
    		p.getWorld().setSpawnLocation(spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ());	
    		p.sendMessage(ChatColor.GREEN + "Spawn changed in " + ChatColor.WHITE + p.getWorld().getName() + ChatColor.GREEN + "!");
    	}
        return true;
    }
}