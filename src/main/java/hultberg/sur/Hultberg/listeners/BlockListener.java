package main.java.hultberg.sur.Hultberg.listeners;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import main.java.hultberg.sur.Hultberg.Hultberg;
import main.java.hultberg.sur.Hultberg.handlers.blocks.BlockLog;
import main.java.hultberg.sur.Hultberg.handlers.blocks.BlockProtect;
import main.java.hultberg.sur.Hultberg.handlers.users.UserHandler;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class BlockListener implements Listener {
	@SuppressWarnings("unused")
	private ArrayList<Location> ra = new ArrayList<Location>();
    private Set<Material> blocks;

    @SuppressWarnings("unused")
	private final Hultberg plugin;
    private UserHandler userHandler;
    private BlockProtect blockProtect;
    private BlockLog blockLog;

    public BlockListener(Hultberg instance) {
        this.plugin = instance;
        this.userHandler = instance.getUserHandler();
        this.blockProtect = instance.getBlockProtectHandler();
        this.blockLog = instance.getBlockLogHandler();
        blocks = new HashSet<Material>();
        blocks.add(Material.REDSTONE_WIRE);
        blocks.add(Material.REDSTONE_TORCH_ON);
        blocks.add(Material.REDSTONE_TORCH_OFF);
        blocks.add(Material.DIODE_BLOCK_OFF);
        blocks.add(Material.DIODE_BLOCK_ON);
        blocks.add(Material.LEVER);
        blocks.add(Material.STONE_BUTTON);
        blocks.add(Material.RAILS);
        blocks.add(Material.TORCH);
    }
    
    @EventHandler
    public void onBlockForm(BlockFormEvent e) {
        @SuppressWarnings("unused")
		World w = e.getBlock().getWorld();

        if ((!e.isCancelled()) && (e.getNewState().getType() == Material.ICE)) {
            e.setCancelled(true);
        } else if ((!e.isCancelled()) && (e.getNewState().getType() == Material.SNOW)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        if (blocks.contains(event.getToBlock().getType())) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onBlockBurn(BlockBurnEvent event){      		
        if(this.blockProtect.isProtected(event.getBlock())){
        	// Dont destroy block.
        	event.setCancelled(true);
        } else {
        	// Destroy.
        	event.setCancelled(false);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        Block b = e.getBlock();
        Location l = b.getLocation();
        Block s = b.getWorld().getBlockAt((int)Math.floor(l.getX()), (int)Math.floor(l.getY()) - 1, (int)Math.floor(l.getZ()));

        // Kista - logg
        if (b.getType() == Material.LOCKED_CHEST && this.userHandler.getUserStatus(p) >= 5) {
            e.setCancelled(true);
            e.setBuild(true);

            p.sendMessage(ChatColor.BLUE + "------ " + ChatColor.WHITE + "Log for: X: " + b.getX() + " Y: " + b.getY() + " Z: " + b.getZ() + " WORLD: " + b.getWorld().getName() + ChatColor.BLUE + " -------");
            for (String line : this.blockLog.getBlockLog(b)) {
                p.sendMessage(line);
            }

            return;
        }
        
        if(s.getType() == Material.GLASS && b.getType() == Material.RAILS){
        	e.setCancelled(true);
        	b.setType(Material.RAILS);
        }
        
        // Bedrock utplassering
        if(b.getType() == Material.BEDROCK && this.userHandler.getUserStatus(p) == 1){
        	b.setType(Material.AIR);
        	e.setCancelled(true);
        }

        // Kreves det tillatelse for bygging og kan brukeren bygge?
        if (!this.userHandler.canBuild(p)) {
            e.setCancelled(true);
        } else {
            if (!this.blockProtect.add(p, b)) {
                e.setBuild(false);
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        Block b = e.getBlock();
        // Kreves det tillatelse for bygging og kan brukeren bygge?
        if (!this.userHandler.canBuild(p)) {
            e.setCancelled(true);
            return;
        } else {
        	// Spesielt for dører
        	if(b.getType() == Material.IRON_DOOR || b.getType() == Material.WOOD_DOOR){
        		Location l = b.getLocation();
        		Block s = b.getWorld().getBlockAt((int)Math.floor(l.getX()), (int)Math.floor(l.getY()) - 1, (int)Math.floor(l.getZ()));
        		if(!this.blockProtect.delete(p, s)){
        			e.setCancelled(true);
        			return;
        		}
        		
        		//Hultberg.log.log(Level.INFO, "Logget i BP6");
        	}
        	
        	// Spesielt for RAILS
        	Location l = b.getLocation();
        	Block s = b.getWorld().getBlockAt((int)Math.floor(l.getX()), (int)Math.floor(l.getY()) + 1, (int)Math.floor(l.getZ()));
        	if(s.getType() == Material.RAILS){
        		if(this.blockProtect.isProtected(s)){
        			// This rail is protected...
        			// Check owner...
        			int owner = this.blockProtect.getOwner(s);
        			int uid = this.userHandler.getUID(p);
        			if((owner == uid) || (this.userHandler.getGID(p) == this.userHandler.getGID(owner))){
        				// You shall not PASS!
        				e.setCancelled(true);
        				p.sendMessage(ChatColor.RED + this.userHandler.getNameFromUID(owner) + " owns the railon this block.");
        				return;
        			} else {
        				// Owner wants to remove block under the rail.
        				e.setCancelled(false);
        				return;
        			}
        		} else {
        			// the rail is not protected...
        			// Let him pass.
        			e.setCancelled(false);
        			return;
        		}
        	}
        	
        	if(!this.blockProtect.delete(p, b)){
                 e.setCancelled(true);
                 return;
             }
        	
        }
        
        if(p.getGameMode() == GameMode.SURVIVAL){
        	// Glassblokker dropper glass-item.
        	if (b.getType() == Material.GLASS) {
        		e.setCancelled(true);
        		b.setType(Material.AIR);
        		b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.GLASS, 1));
        	}
        
        	// Glasspane dropper glasspane-item.
        	if (b.getType() == Material.THIN_GLASS) {
        		e.setCancelled(true);
        		b.setType(Material.AIR);
        		b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.THIN_GLASS, 1));
        	}

        	// Glowstone-blokker dropper glowstone-item i stedet for glowstone dust.
        	if (b.getType() == Material.GLOWSTONE) {            
        		e.setCancelled(true);
        		b.setType(Material.AIR);
        		b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.GLOWSTONE, 1));
        	}
        }
    }
}
