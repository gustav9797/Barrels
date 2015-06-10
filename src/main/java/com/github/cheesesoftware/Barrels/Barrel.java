package com.github.cheesesoftware.Barrels;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class Barrel {
    private int ID;
    private Location location;
    private UUID creatorUUID;

    private ItemStack item;
    private int itemAmount;

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
}
