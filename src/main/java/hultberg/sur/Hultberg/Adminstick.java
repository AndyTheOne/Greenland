package main.java.hultberg.sur.Hultberg;

import main.java.hultberg.sur.Hultberg.handlers.blocks.BlockLog;
import main.java.hultberg.sur.Hultberg.handlers.blocks.BlockProtect;
import main.java.hultberg.sur.Hultberg.handlers.users.UserHandler;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Adminstick {
    public Adminstick(PlayerInteractEvent e, Hultberg plugin) {
        
        BlockProtect blockProtect = plugin.getBlockProtectHandler();
        BlockLog blockLog = plugin.getBlockLogHandler();
        UserHandler userHandler = plugin.getUserHandler();

        Player p = e.getPlayer();
        Block b = e.getClickedBlock();
        Action a = e.getAction();
        ItemStack iih = p.getItemInHand();
        
        // Adminstick
        if ((iih.getType() == Material.STICK) && (userHandler.getUserStatus(p) >= 5)) {
            // Fjerner blokken og returnerer item
            if (a == Action.LEFT_CLICK_BLOCK) {
                // Internal Error-fiks.
                if (b.getType() == Material.AIR) return;

                ItemStack n = new ItemStack(b.getType(), 1, b.getData());
                p.getInventory().addItem(n);
                blockProtect.delete(b);
                blockLog.log(p, b, 2, b.getType().name());
                b.setType(Material.AIR);

                // Fjerner blokken og retunerer ingenting.
            } else if (a == Action.RIGHT_CLICK_BLOCK) {
                blockProtect.delete(b);
                blockLog.log(p, b, 2, b.getType().name());
                b.setType(Material.AIR);

            } else {
                return;
            }
        }
    }
}
