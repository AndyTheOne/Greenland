package main.java.hultberg.sur.Hultberg.listeners;

import java.util.Iterator;

import main.java.hultberg.sur.Hultberg.Hultberg;
import main.java.hultberg.sur.Hultberg.handlers.blocks.BlockLog;
import main.java.hultberg.sur.Hultberg.handlers.blocks.BlockProtect;
import main.java.hultberg.sur.Hultberg.handlers.users.UserHandler;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;

public class EntityListener implements Listener {

    @SuppressWarnings("unused")
	private final Hultberg plugin;
	private UserHandler userHandler;
	private BlockProtect bp;
	@SuppressWarnings("unused")
	private BlockLog bl;

    public EntityListener(Hultberg instance) {
        this.plugin = instance;
        this.userHandler = instance.getUserHandler();
        this.bp = instance.getBlockProtectHandler();
        this.bl = instance.getBlockLogHandler();
    }
    
    @SuppressWarnings("unused")
	@EventHandler
    public void onEntityDamage(EntityDamageEvent event){
    	World world = event.getEntity().getWorld();
        Entity attacker = null;
        Entity defender = event.getEntity();
        DamageCause type = event.getCause();
        if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent subEvent = (EntityDamageByEntityEvent) event;
            attacker = subEvent.getDamager();
            if (defender instanceof Player && attacker instanceof Player) {
            	Player p = (Player) defender;
            	Player pp = (Player) attacker;
            	int def_uid = this.userHandler.getUID(p.getName());
            	int att_uid = this.userHandler.getUID(pp.getName());
            	if(this.userHandler.getUserStatus(pp.getName()) == 0){
            		pp.sendMessage(ChatColor.DARK_RED+"You cant hurt users.");
            		event.setCancelled(true);
                	return;
            	} else if(this.userHandler.getUserStatus(p.getName()) == 0){
            		pp.sendMessage(ChatColor.DARK_RED+"You cant hurt guests.");
            		event.setCancelled(true);
                	return;
            	} else {
            		if(this.userHandler.getGID(att_uid) == this.userHandler.getGID(def_uid) && this.userHandler.getGID(att_uid) != 0){
                    	event.setCancelled(true);
                    	return;
                    } else {
                    	event.setCancelled(false);
                    	return;
                    }
            	} 
            }            	
            
            if((defender instanceof Player) && (attacker instanceof Creature || attacker instanceof Monster)){
            	Player p = (Player) defender;
            	if(this.userHandler.getUserStatus(p.getName()) == 0){
            		event.setCancelled(true);
            		return;
            	} else {
            		event.setCancelled(false);
            		return;
            	}
            } else if(attacker instanceof Player && (defender instanceof Animals || defender instanceof Creature)){
            	Player pp = (Player) attacker;
            	
            	if(this.userHandler.getUserStatus(pp.getName()) == 0){
            		pp.sendMessage(ChatColor.DARK_RED+"You cant hurt animals/creatures.");
            		event.setCancelled(true);
            		return;
            	} else {
            		event.setCancelled(false);
            		return;
            	}
            }
        }
        
        if (event.getEntity() instanceof Player) {
        	Player p = (Player) event.getEntity();
        	if(this.userHandler.getUserStatus(p) == 0){
            	if (type == DamageCause.DROWNING) {
                	event.setCancelled(true);
                } else if (type == DamageCause.CONTACT) {
                	event.setCancelled(true);
                } else if (type == DamageCause.ENTITY_EXPLOSION) {
                    if ((attacker instanceof Creature)) {
                        event.setCancelled(true);
                    } else if (!(attacker instanceof LivingEntity)) {
                        event.setCancelled(true);
                    }
                } else if (type == DamageCause.FALL) {
                	event.setCancelled(true);
                } else if (type == DamageCause.SUFFOCATION) {
                	event.setCancelled(true);
                } else if (type == DamageCause.FIRE) {
                	event.setCancelled(true);
                } else if (type == DamageCause.FIRE_TICK) {
                	event.setCancelled(true);
                } else if (type == DamageCause.LAVA) {
                	event.setCancelled(true);
                } else if (type == DamageCause.STARVATION) {
                    event.setCancelled(true);
                    if (event.getEntity() instanceof Player) {
                        p.setFoodLevel(20);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamageByBlock(EntityDamageByBlockEvent event) {

    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        Location l = event.getLocation();
        @SuppressWarnings("unused")
		Entity e = event.getEntity();
        @SuppressWarnings("unused")
		World world = l.getWorld();
        Iterator<Block> blocks = event.blockList().iterator();
        

        while (blocks.hasNext()) {
			Block block = blocks.next();
			
			if(block.getType() == Material.WOOD || block.getType() == Material.GRASS || block.getType() == Material.DIRT || block.getType() == Material.SAND || block.getType() == Material.GRAVEL || block.getType() == Material.LOG || block.getType() == Material.LEAVES  || block.getType() == Material.LONG_GRASS){
				// NOPE!
        	} else {
				if (bp.isProtected(block)) {
					blocks.remove();
				}
			}
		}
    }
}
