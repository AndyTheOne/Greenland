package main.java.hultberg.sur.Hultberg.listeners;

import main.java.hultberg.sur.Hultberg.Hultberg;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

public class ChunkListener implements Listener {
	@SuppressWarnings("unused")
	private Hultberg plugin;
	
    double maxSpeed = 0.65;
    double HIGHSpeed = 2.0;
    double launchSpeed = 3;
    double constantSpeed = 0.4;

    public ChunkListener(Hultberg instance) {
        this.plugin = instance;
    }
    
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        @SuppressWarnings("unused")
		long time = System.currentTimeMillis();
        World world = event.getChunk().getWorld();
        Chunk chunk = event.getChunk();
    
        if (world.getEnvironment().equals(World.Environment.NORMAL)) {
        	for (int x = chunk.getX() * 16; x < (chunk.getX() + 1) * 16; x++)
            for (int y = chunk.getZ() * 16; y < (chunk.getZ() + 1) * 16; y++) {
                if (world.getBlockAt(x, 0, y).getType() != Material.BEDROCK) {
                	world.getBlockAt(x, 0, y).setType(Material.BEDROCK);
                }
                if (world.getBlockAt(x, 1, y).getType() == Material.BEDROCK) {
                	world.getBlockAt(x, 1, y).setType(Material.STONE);
                }
                if (world.getBlockAt(x, 2, y).getType() == Material.BEDROCK) {
                   	world.getBlockAt(x, 2, y).setType(Material.STONE);
                }
                if (world.getBlockAt(x, 3, y).getType() == Material.BEDROCK) {
                   	world.getBlockAt(x, 3, y).setType(Material.STONE);
                }
                if (world.getBlockAt(x, 4, y).getType() == Material.BEDROCK) {
                	world.getBlockAt(x, 4, y).setType(Material.STONE);
                }
        	}
        }
    }

}
