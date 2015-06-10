package com.github.cheesesoftware.Barrels;

import java.util.ArrayList;
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

    private ArrayList<Hologram> holograms = new ArrayList<Hologram>();

    public Barrel(int ID, Location location, UUID creatorUUID, ItemStack item, int itemAmount) {
	this.ID = ID;
	this.location = location;
	this.creatorUUID = creatorUUID;
	this.item = item;
	this.itemAmount = itemAmount;
    }

    public void addHologram(Hologram h) {
	holograms.add(h);
    }

    public void killHolograms() {
	for (Hologram h : this.holograms) {
	    h.delete();
	}
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
}
