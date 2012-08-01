package main.java.hultberg.sur.Hultberg.listeners;

import main.java.hultberg.sur.Hultberg.Hultberg;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.PoweredMinecart;
import org.bukkit.entity.StorageMinecart;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleBlockCollisionEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.event.vehicle.VehicleUpdateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class VehicleListener implements Listener {
        
        double maxSpeed                 = 0.65;
        double HIGHSpeed                = 2.0;
        double launchSpeed              = 3;
        double constantSpeed            = 0.4;
                
		@SuppressWarnings("unused")
		private final Hultberg plugin;

        public VehicleListener(Hultberg plugin) {
                this.plugin = plugin;
        }

        public static int getItemIDFromName(String name) {
        	Material[] all = Material.values();
        	
        	if (name == null)
        		return 0;
        	
        	String[] args = name.split(":");
        	for (int i = 0; i < all.length; i++) {
        		if (all[i].name().equalsIgnoreCase(args[0])) {
        			if (all[i].getId() == 35 && args.length > 1) {
        				if (Integer.valueOf(args[1]) >= 0 && Integer.valueOf(args[1]) <= 15) {
        					return all[i].getId();
        				}
    				} else if (all[i].getId() == 44 && args.length > 1) {
    					if (Integer.valueOf(args[1]) >= 0 && Integer.valueOf(args[1]) <= 3) {
    						return all[i].getId();
    					}
    				} else if (all[i].getId() == 17 && args.length > 1) {
    					if (Integer.valueOf(args[1]) >= 0 && Integer.valueOf(args[1]) <= 2) {
    						return all[i].getId();
    					}
    				} else {
    					return all[i].getId();
    				}
    			}
        	}
        	return 0;
        }
        
        @EventHandler
        public void onVehicleDamage(VehicleDamageEvent event) {
        	if (!event.getVehicle().isEmpty()) {
        		if(event.getVehicle() instanceof Boat) {
        			event.setCancelled(true);
        		}
        		
        		if ((event.getVehicle() instanceof Minecart)) {
        			if (!(event.getVehicle().getPassenger() == event.getAttacker())) {
        				event.setCancelled(true);
        			}
        			
        			else if ((event.getVehicle().getPassenger() != null) && (event.getAttacker() != null) && (event.getAttacker().getEntityId() == event.getVehicle().getPassenger().getEntityId())) {
        				event.setDamage(0);
        				event.setCancelled(true);
        			} else {
        				event.setCancelled(true);
        			}
        		}
        	}
        }
        
        public void onVehicleEntityCollision (VehicleEntityCollisionEvent event) {
        	if ((event.getVehicle() instanceof Minecart)) {
        		Entity collisioner = event.getEntity();
        		
        		if ((collisioner instanceof LivingEntity)) {
        			LivingEntity victim = (LivingEntity)collisioner;
        			if ((!(victim instanceof Player)) && (!(victim instanceof Wolf))) {
        				victim.remove();
        				event.setCancelled(true);
        				event.setCollisionCancelled(true);
        				event.setPickupCancelled(true);
        			}
        		}
        	}
        }
        
        public void onVehicleBlockCollision(VehicleBlockCollisionEvent event) {
        	if(event.getVehicle() instanceof Boat) {
        		if ((!event.getVehicle().isEmpty()) && ((event.getBlock().getTypeId() != 8) || (event.getBlock().getTypeId() != 9))) {
        			Player localPlayer = (Player)event.getVehicle().getPassenger();
        			event.getVehicle().teleport(localPlayer.getLocation());
        		}
        	}
        }
        
    	private void doFrictionFix(Minecart cart, Vector nowSpeed) {
    		// FRICTION FIX		
    		double spd = 0.0039;
    		if(nowSpeed.getX() > 0) {
    			nowSpeed.setX(nowSpeed.getX() + spd);
    		} else if(nowSpeed.getX() < 0) {
    			nowSpeed.setX(nowSpeed.getX() - spd);
    		}
    		if(nowSpeed.getZ() > 0) {
    			nowSpeed.setZ(nowSpeed.getZ() + spd);
    		} else if(nowSpeed.getZ() < 0) {
    			nowSpeed.setZ(nowSpeed.getZ() - spd);
    		}
    			
    		if(nowSpeed.getX() < -maxSpeed)
    			nowSpeed.setX(-maxSpeed);
    		if(nowSpeed.getX() > maxSpeed)
    			nowSpeed.setX(maxSpeed);
    		if(nowSpeed.getZ() < -maxSpeed)
    			nowSpeed.setZ(-maxSpeed);
    		if(nowSpeed.getZ() > maxSpeed)
    			nowSpeed.setZ(maxSpeed);
    			
    		cart.setMaxSpeed(maxSpeed);
    		cart.setVelocity(nowSpeed);
    		// FRICTION FIX END
    	}
    	@EventHandler
	    public void onVehicleMove(VehicleMoveEvent event) {
	    	Vehicle vehicle = event.getVehicle();
	        
	        // Kun minecarts!
	    	if (!(vehicle instanceof Minecart)) {
	    		return;
	    	}
	    	
	    	Location from = event.getFrom();
	    	Location to = event.getTo();
	        
	    	if (from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ()) {
	    		handleMinecartBlockChange(event);
	    	}
	    }
    	@EventHandler
	    public void onVehicleUpdate(VehicleUpdateEvent event) {        
	    	if(!(event.getVehicle() instanceof Minecart || event.getVehicle() instanceof PoweredMinecart || event.getVehicle() instanceof StorageMinecart)) {
	    		return;
	    	}
	    	
	    	Minecart cart = (Minecart)(event.getVehicle());
	    	Chunk current = cart.getWorld().getChunkAt(cart.getLocation()); 
	    	Block under = current.getBlock(cart.getLocation().getBlockX(), cart.getLocation().getBlockY() - 1, cart.getLocation().getBlockZ());
	    	Block rail = cart.getWorld().getBlockAt(cart.getLocation()); 
	    	Block signBlock = current.getBlock(cart.getLocation().getBlockX(), cart.getLocation().getBlockY() - 2, cart.getLocation().getBlockZ());
	    	byte color = under.getData();

	    	if(rail.getType() != Material.RAILS && rail.getType() != Material.STONE_PLATE && rail.getType() != Material.WOOD_PLATE) {
	    		return;
	    	}
	    	
	    	if(rail.getType() == Material.STONE_PLATE || rail.getType() == Material.WOOD_PLATE) {
	    		cart.setDerailedVelocityMod(new Vector(0.95, 0.95, 0.95));
	    		} else {
	    			cart.setDerailedVelocityMod(new Vector(0.5, 0.5, 0.5));
	    		}
	    	
	    	Vector nowSpeed = cart.getVelocity();	
	    	doFrictionFix(cart, nowSpeed);
	    	
	    	if(((rail.getData() == 0x5 && nowSpeed.getZ () > 0) || (rail.getData() == 0x4 && nowSpeed.getZ () < 0))) {
	    		
	    		nowSpeed.setZ(nowSpeed.getZ() * 1.3);
	    		
	    		if(nowSpeed.getZ() < -maxSpeed)
            		nowSpeed.setZ(-maxSpeed);
            	if(nowSpeed.getZ() > maxSpeed)
            		nowSpeed.setZ(maxSpeed);
            			
            	cart.setMaxSpeed(maxSpeed);
            	cart.setVelocity(nowSpeed);			
            }
	    	
	    	if(((rail.getData() == 0x2 && nowSpeed.getX () > 0) || (rail.getData() == 0x3 && nowSpeed.getX () < 0))) {
	    		
	    		nowSpeed.setX(nowSpeed.getX() * 1.3);
            			
            	if(nowSpeed.getX() < -maxSpeed)
            		nowSpeed.setX(-maxSpeed);
            	if(nowSpeed.getX() > maxSpeed)
            		nowSpeed.setX(maxSpeed);
            	cart.setMaxSpeed(maxSpeed);
            	cart.setVelocity(nowSpeed);
            }
            		
            if (under.getType() == Material.WOOL) {
                if(color == DyeColor.LIME.getData()) {
                        Vector speed = cart.getVelocity();
                        speed.setX(speed.getX() * 8);
                        speed.setZ(speed.getZ() * 8);                   
                        
                        if(speed.getX() < 0)
                                if(speed.getX() < -maxSpeed)
                                        speed.setX(-maxSpeed);
                        if(speed.getX() > 0)
                                if(speed.getX() > maxSpeed)
                                        speed.setX(maxSpeed);
                        
                        if(speed.getZ() < 0)
                                if(speed.getZ() < -maxSpeed)
                                        speed.setZ(-maxSpeed);
                        if(speed.getZ() > 0)
                                if(speed.getZ() > maxSpeed)
                                        speed.setZ(maxSpeed);
                                                
                        cart.setMaxSpeed(maxSpeed);
                        cart.setVelocity(speed);
                }
                else if(color == DyeColor.GRAY.getData()) {
                        // BREMSE 50%
                        Vector speed = cart.getVelocity();
                        speed.setX(speed.getX() * 0.85);
                        speed.setZ(speed.getZ() * 0.85);                                                        
                        cart.setVelocity(speed);
                }
                else if(color == DyeColor.SILVER.getData()) {
                        // BREMSE 25%
                        Vector speed = cart.getVelocity();
                        speed.setX(speed.getX() * 0.95);
                        speed.setZ(speed.getZ() * 0.95);                                                
                        cart.setVelocity(speed);
                }
                else if(color == DyeColor.PINK.getData()) {
                        cart.eject();
                } 
                else if(color == DyeColor.GREEN.getData()) {
                        Vector speed = cart.getVelocity();
                        speed.setX(speed.getX() * 2);
                        speed.setZ(speed.getZ() * 2);                   
                        
                        if(speed.getX() < 0)
                                if(speed.getX() < -maxSpeed)
                                        speed.setX(-maxSpeed);
                        if(speed.getX() > 0)
                                if(speed.getX() > maxSpeed)
                                        speed.setX(maxSpeed);
                        
                        if(speed.getZ() < 0)
                                if(speed.getZ() < -maxSpeed)
                                        speed.setZ(-maxSpeed);
                        if(speed.getZ() > 0)
                                if(speed.getZ() > maxSpeed)
                                        speed.setZ(maxSpeed);
                        
                        cart.setMaxSpeed(maxSpeed);
                        cart.setVelocity(speed);
                }
                else if(color == DyeColor.YELLOW.getData() && !under.isBlockIndirectlyPowered()) {
                        // REVERS
                        Vector speed = cart.getVelocity();
                        speed.setX(-speed.getX());
                        speed.setZ(-speed.getZ());                                                      
                        cart.setVelocity(speed);
                }
                else if(color == DyeColor.RED.getData()) {
                        cart.setVelocity(cart.getVelocity().multiply(0.0));
                }
                else if(color == DyeColor.ORANGE.getData()) {
                        Vector speed = cart.getVelocity();
                        if(speed.getX() < 0)
                                        speed.setX(-constantSpeed);
                        if(speed.getX() > 0)
                                        speed.setX(constantSpeed);
                        if(speed.getZ() < 0)
                                        speed.setZ(-constantSpeed);
                        if(speed.getZ() > 0)
                                        speed.setZ(constantSpeed);
                        
                        cart.setVelocity(speed);
                }
                else if(color == DyeColor.BLACK.getData() && !under.isBlockIndirectlyPowered() && signBlock.getType().equals(Material.SIGN_POST)) {
                        Sign sign = (Sign)signBlock.getState();
                        if(sign.getLine(1).equalsIgnoreCase("[Stasjon]")) {
                                Vector speed = new Vector();
                                speed.setX(0);
                                speed.setY(0);
                                speed.setZ(0);                  
                                cart.setVelocity(speed);
                        }
                }
                else if(color == DyeColor.BLACK.getData() && under.isBlockIndirectlyPowered() && signBlock.getType().equals(Material.SIGN_POST)) {
                        Sign sign = (Sign)signBlock.getState();
                        if(sign.getLine(1).equalsIgnoreCase("[Stasjon]")) {
                                Vector speed = new Vector();
                                speed.setX(0);
                                speed.setZ(0);
                                
                                        if (sign.getRawData() == 0x0) {
                                                // VEST
                                                speed.setZ(-0.6);
                                                cart.setVelocity(speed);
                                        } else if (sign.getRawData() == 0x4) {
                                                // NORD
                                                speed.setX(0.6);
                                                cart.setVelocity(speed);
                                        } else if (sign.getRawData() == 0x8) {
                                                // ØST
                                                speed.setZ(0.6);
                                                cart.setVelocity(speed);
                                        } else if (sign.getRawData() == 0xC) {
                                                // SØR
                                                speed.setX(-0.6);
                                                cart.setVelocity(speed);
                                        }
                                }
                        }
                
		}
	}
    
	@EventHandler
    public void onVehicleCreate(VehicleCreateEvent event) {
        Vehicle vehicle = event.getVehicle();
        
        // Kun minecarts!
        if (!(vehicle instanceof Minecart)) {
            return;
        }
        Minecart minecart = (Minecart) vehicle;
        //minecart.setSlowWhenEmpty(true);
        minecart.setMaxSpeed(2.0D);
    }
         
    protected void handleMinecartBlockChange(VehicleMoveEvent event) {
        if(event.getVehicle().getPassenger() != null) {
                if(event.getVehicle().getPassenger() instanceof Player) {
                        Minecart cart 	= (Minecart)(event.getVehicle());
                        Location to   	= event.getTo();
                        Player player 	= (Player) event.getVehicle().getPassenger();
                        Block rail   	= cart.getWorld().getBlockAt(cart.getLocation()); 
                        Block under 	= to.getBlock().getRelative(0, -1, 0);
                        byte color 		= under.getData();
                        if(event.getVehicle().getPassenger() instanceof Player) {                       
                                if(rail.getData() != 0x2 || rail.getData() != 0x3) {
                                        if(rail.getData() != 0x5 || rail.getData() != 0x4) {
                                        Vector speed = cart.getVelocity();
                                        speed.setX(speed.getX() * 2);
                                        speed.setZ(speed.getZ() * 2);
                                                if(speed.getX() < 0)
                                                        if(speed.getX() < -maxSpeed)
                                                                speed.setX(-maxSpeed);
                                                if(speed.getX() > 0)
                                                        if(speed.getX() > maxSpeed)
                                                                speed.setX(maxSpeed);
                                                
                                                if(speed.getZ() < 0)
                                                        if(speed.getZ() < -maxSpeed)
                                                                speed.setZ(-maxSpeed);
                                                if(speed.getZ() > 0)
                                                        if(speed.getZ() > maxSpeed)
                                                                speed.setZ(maxSpeed);
                                                cart.setMaxSpeed(maxSpeed);
                                                cart.setVelocity(speed);
                                        } else {
                                                cart.setMaxSpeed(0.4D);
                                        }
                                } else {
                                                cart.setMaxSpeed(0.4D);
                                        }
                        }
                        if (under.getType() == Material.WOOL) {
                                if(color == DyeColor.BLUE.getData()) {
                                        Block skilt = under.getRelative(0, -1, 0);
                                        if(skilt != null && skilt.getType() == Material.SIGN_POST) {
                                                Sign sign = (Sign) skilt.getState();
                                                if(player != null) {
                                                        String message = ChatColor.GREEN + sign.getLine(0) + sign.getLine(1) + sign.getLine(2) + sign.getLine(3);
                                                        player.sendMessage(message);
                                                }
                                                
                                        }
                                }
                        }
                }
        }
    }
	@EventHandler
	public void onVehicleExit(VehicleExitEvent event) {         
        if(event.getExited() instanceof Player) {
                Player player = (Player) event.getExited();
                if ((event.getVehicle() instanceof Minecart)) {
                        Minecart cart = (Minecart) event.getVehicle();
                        player.getInventory().addItem(new ItemStack(Material.MINECART, 1));
                        cart.remove();
                  
                } else if ((event.getVehicle() instanceof Boat)) {
                        Boat boat = (Boat) event.getVehicle();
                        player.getInventory().addItem(new ItemStack(Material.BOAT, 1));
                        boat.remove();
                }
        }
    }
    
}