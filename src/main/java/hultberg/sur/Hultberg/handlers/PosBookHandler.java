package main.java.hultberg.sur.Hultberg.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import main.java.hultberg.sur.Hultberg.Hultberg;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PosBookHandler {
	
	private HashMap<Player,List<Location>> positions;
    @SuppressWarnings("unused")
	private Hultberg plugin;

    public PosBookHandler(Hultberg instance) {
        this.plugin = instance;
        positions = new HashMap<Player,List<Location>>();
    }
    
    public void setLeftPos(Player p, Location loc){
    	if (!positions.containsKey(p)) {
			positions.put(p,new ArrayList<Location>());
		}

		List<Location> pair = positions.get(p);

		if (pair.size() < 1) {
			pair.add(loc);
		} else {
			pair.set(0,loc);
		}

		p.sendMessage(ChatColor.AQUA + "Position #1 is set to " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + ".");
    }
    
    public void setRightPos(Player p, Location loc){
    	if (!positions.containsKey(p)) {
			positions.put(p,new ArrayList<Location>());
		}

		List<Location> pair = positions.get(p);

		if (pair.size() < 2) {
			pair.add(1,loc);
		} else {
			pair.set(1,loc);
		}

		p.sendMessage(ChatColor.AQUA + "Position #2 is set to " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + ".");
    }
    
	public List<Block> getBlocks(Player p) {
		List<Block> blocks = new ArrayList<Block>();

		if (positions.containsKey(p)) {
			List<Location> pair = positions.get(p);

			if (pair.size() == 2) {
				Location one = pair.get(0);
				Location two = pair.get(1);

				if (one.getWorld().equals(two.getWorld())) {
					World world = one.getWorld();

					int lowX, highX, lowY, highY, lowZ, highZ;

					if (one.getBlockX() < two.getBlockX()) {
						lowX = one.getBlockX();
						highX = two.getBlockX();
					} else {
						lowX = two.getBlockX();
						highX = one.getBlockX();
					}

					if (one.getBlockY() < two.getBlockY()) {
						lowY = one.getBlockY();
						highY = two.getBlockY();
					} else {
						lowY = two.getBlockY();
						highY = one.getBlockY();
					}

					if (one.getBlockZ() < two.getBlockZ()) {
						lowZ = one.getBlockZ();
						highZ = two.getBlockZ();
					} else {
						lowZ = two.getBlockZ();
						highZ = one.getBlockZ();
					}

					for (int x = lowX; x <= highX; x++) {
						for (int y = lowY; y <= highY; y++) {
							for (int z = lowZ; z <= highZ; z++) {
								blocks.add(world.getBlockAt(x,y,z));
							}
						}
					}
				}
			}
		}

		return blocks;
	}
}
