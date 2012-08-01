package main.java.hultberg.sur.Hultberg;

import main.java.hultberg.sur.Hultberg.handlers.blocks.BlockLog;
import main.java.hultberg.sur.Hultberg.handlers.blocks.BlockProtect;
import main.java.hultberg.sur.Hultberg.handlers.users.UserHandler;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class LogClock {
    public LogClock(PlayerInteractEvent e, Hultberg plugin) {

        BlockProtect blockProtect = plugin.getBlockProtectHandler();
        BlockLog blockLog = plugin.getBlockLogHandler();
        UserHandler userHandler = plugin.getUserHandler();

        Player p = e.getPlayer();
        Block b = e.getClickedBlock();
        Action a = e.getAction();
        ItemStack iih = p.getItemInHand();

        // Klokka - logg
        if (iih.getType() == Material.WATCH && userHandler.getUserStatus(p) >= 5 && a == Action.RIGHT_CLICK_BLOCK) {
            p.sendMessage(ChatColor.BLUE + "-------- " + ChatColor.WHITE + "Log for: X: " + b.getX() + " Y: " + b.getY() + " Z: " + b.getZ() + " WORLD: " + b.getWorld().getName() + ChatColor.BLUE + " ---------");

            for (String line : blockLog.getBlockLog(b)) {
                p.sendMessage(line);
            }
            String username = userHandler.getNameFromUID(blockProtect.getOwner(b));
            if(username == "-1" || username == "0"){
            	p.sendMessage(ChatColor.WHITE + "Owner: " + ChatColor.BLUE + "no owner");
            } else {
            	p.sendMessage(ChatColor.WHITE + "Owner: " + ChatColor.BLUE + username);
            }
        }

    }
}