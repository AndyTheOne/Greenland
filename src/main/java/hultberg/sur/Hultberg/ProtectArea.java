package main.java.hultberg.sur.Hultberg;

import java.util.HashMap;
import java.util.List;

import main.java.hultberg.sur.Hultberg.handlers.users.UserHandler;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ProtectArea {
	@SuppressWarnings("unused")
	public ProtectArea(PlayerInteractEvent e, Hultberg plugin) {
		final HashMap<Player,List<Location>> positions;
		positions = new HashMap<Player,List<Location>>();
		UserHandler userHandler = plugin.getUserHandler();
		
		Player p = e.getPlayer();
        Block b = e.getClickedBlock();
        Action a = e.getAction();
        ItemStack iih = p.getItemInHand();
		
		if (iih.getType() == Material.WOOD_AXE && userHandler.getUserStatus(p) >= 5 && a == Action.RIGHT_CLICK_BLOCK) {
			
		}
		
	}
}
