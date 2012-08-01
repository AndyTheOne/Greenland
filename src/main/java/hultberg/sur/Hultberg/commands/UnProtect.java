package main.java.hultberg.sur.Hultberg.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import main.java.hultberg.sur.Hultberg.Hultberg;
import main.java.hultberg.sur.Hultberg.handlers.CommandHandler;
import main.java.hultberg.sur.Hultberg.handlers.PosBookHandler;
import main.java.hultberg.sur.Hultberg.handlers.blocks.BlockProtect;
import main.java.hultberg.sur.Hultberg.handlers.users.UserHandler;

public class UnProtect extends CommandHandler {

	@SuppressWarnings("unused")
	private UserHandler userHandler;
	private BlockProtect bp;
	private PosBookHandler pb;

    public UnProtect(Hultberg instance) {
        super(instance);
        setStatus(5);
        this.userHandler = instance.getUserHandler();
        this.pb = instance.getPosBookHandler();
        this.bp = instance.getBlockProtectHandler();
    }
    
    public boolean onPlayerCommand(Player p, Command command, String label, String[] args) {
		List<Block> blocks = this.pb.getBlocks(p);

		if (blocks.size() == 0) {
			p.sendMessage(ChatColor.AQUA + "No pos is selected.");
			return true;
		}

		p.sendMessage(ChatColor.AQUA + "Unprotecting region (" + blocks.size() + " blocks)...");

		for (Block block : blocks) {
			if(block.getType() == Material.AIR || block.getType() == Material.DIRT || block.getType() == Material.SAPLING || block.getType() == Material.GRASS || block.getType() == Material.GRAVEL || block.getType() == Material.SAND){
				// NO!
			} else {
				this.bp.delete(block);
			}   				
		}

		p.sendMessage(ChatColor.AQUA + "Region unprotected.");
		return true;
    }
}
