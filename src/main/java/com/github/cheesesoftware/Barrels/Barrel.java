package com.github.cheesesoftware.Barrels;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import com.gmail.filoghost.holographicdisplays.api.Hologram;

public class Barrel {
    private int ID;
    private Location location;
    private UUID creatorUUID;

    private ItemStack item;
    private int itemAmount;
    
    private List<Hologram> holograms = new ArrayList<Hologram>();

    public Barrel(int ID, Location location, UUID creatorUUID, ItemStack item, int itemAmount) {
	this.ID = ID;
	this.location = location;
	this.creatorUUID = creatorUUID;
	this.item = item;
	this.itemAmount = itemAmount;
    }

    public int getID() {
	return this.ID;
    }

    public Location getLocation() {
	return this.location;
    }

    public UUID getCreatorUUID() {
	return this.creatorUUID;
    }

    public ItemStack getItemStack() {
	return this.item;
    }

    public int getItemAmount() {
	return this.itemAmount;
    }

    public void setItemAmount(int itemAmount) {
	this.itemAmount = itemAmount;
    }
    
    public List<Hologram> getHolograms() {
	return this.holograms;
    }
    
    public void addHologram(Hologram hologram) {
	holograms.add(hologram);
    }
}
