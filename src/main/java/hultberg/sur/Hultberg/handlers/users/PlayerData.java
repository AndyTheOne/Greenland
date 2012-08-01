package main.java.hultberg.sur.Hultberg.handlers.users;

import main.java.hultberg.sur.Hultberg.Hultberg;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PlayerData {

    @SuppressWarnings("unused")
	private Hultberg plugin;

    public PlayerData(Hultberg instance) {
        this.plugin = instance;
    }

    private Player player;
    private int status;
    private int gid;
    private int uid;
    private Location Wand1 = null;
    private Location Wand2 = null;
    private Location RedstoneLocation = null;
    Block targetblock = null;

    public void setTargetblock(Block b) {
        this.targetblock = b;
    }

    public Block getTargetblock() {
        return targetblock;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void setRedstoneLocation(Location l) {
        this.RedstoneLocation = l;
    }

    public Location getRedstoneLocation() {
        return RedstoneLocation;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setGroup(int gid) {
        this.gid = gid;
    }

    public int getGroup() {
        return gid;
    }

    public void setUID(int uid) {
        this.uid = uid;
    }

    public int getUID() {
        return uid;
    }

    public void setWand1(Location l) {
        this.Wand1 = l;
    }

    public Location getWand1() {
        return Wand1;
    }

    public void setWand2(Location l) {
        this.Wand2 = l;
    }

    public Location getWand2() {
        return Wand2;
    }

}