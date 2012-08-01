package main.java.hultberg.sur.Hultberg;

import main.java.hultberg.sur.Hultberg.handlers.PosBookHandler;
import main.java.hultberg.sur.Hultberg.handlers.users.UserHandler;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PosBook {
	public PosBook(PlayerInteractEvent e, Hultberg plugin) {
        UserHandler userHandler = plugin.getUserHandler();
        PosBookHandler pb = plugin.getPosBookHandler();

        Player p = e.getPlayer();
        Block b = e.getClickedBlock();
        Action a = e.getAction();
        ItemStack iih = p.getItemInHand();

        // PosBook.
        if (iih.getType() == Material.BOOK && userHandler.getUserStatus(p) >= 5) {
        	Location location = e.getClickedBlock().getLocation();
            // Fjerner blokken og returnerer item
            if (a == Action.LEFT_CLICK_BLOCK) {
                // Internal Error-fiks.
                if (b.getType() == Material.AIR) return;
                
                pb.setLeftPos(p, location);

                // Fjerner blokken og retunerer ingenting.
            } else if (a == Action.RIGHT_CLICK_BLOCK) {
            	// Internal Error-fiks.
                if (b.getType() == Material.AIR) return;
                
                pb.setRightPos(p, location);
                
            } else {
                return;
            }
        }
	}
}
