package com.github.cheesesoftware.Barrels;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.handler.TouchHandler;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;

public class Barrels extends JavaPlugin implements Listener {

    private String host = "";
    private String database = "";
    private int port = 3306;
    private String username = "";
    private String password = "";
    private int barrelMaxCapacity = 4096;

    private String connectionString = "";
    private Connection SQLConnection = null;

    private Map<Location, Barrel> barrels = new HashMap<Location, Barrel>();
    int nextID = 0;

    private boolean useHolographicDisplays = false;

    public void onDisable() {
    }

    @SuppressWarnings("deprecation")
    public void onEnable() {
	this.saveDefaultConfig();

	getServer().getPluginManager().registerEvents(this, this);

	if (!Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays"))
	    Bukkit.getLogger().severe("[Barrels] Could not load HolographicDisplays. Please install it if you want holographic barrels.");
	else
	    useHolographicDisplays = true;

	this.host = getConfig().getString("host");
	this.database = getConfig().getString("database");
	this.port = getConfig().getInt("port");
	this.username = getConfig().getString("username");
	this.password = getConfig().getString("password");
	this.barrelMaxCapacity = getConfig().getInt("barrelMaxCapacity");

	try {
	    this.connectionString = "jdbc:mysql://" + host + ":" + port + "/" + database;
	    this.SQLConnection = DriverManager.getConnection(this.connectionString, username, password);
	    String createtable = "CREATE TABLE IF NOT EXISTS `barrels-barrels` (  `id` INT UNSIGNED NOT NULL DEFAULT '0',  `world` VARCHAR(80) NOT NULL,  `x` INT NOT NULL,  `y` INT NOT NULL,  `z` INT NOT NULL,  `creator` VARCHAR(36) NOT NULL,  `material` INT NOT NULL,  `data` TINYINT NOT NULL,  `amount` INT NOT NULL,  PRIMARY KEY (`id`),  UNIQUE INDEX `id_UNIQUE` (`id` ASC));";
	    Statement s = this.SQLConnection.createStatement();
	    s.execute(createtable);

	    ResultSet result = this.executeQuery("SELECT * FROM `barrels-barrels`");
	    while (result.next()) {
		Location l = new Location(getServer().getWorld(result.getString("world")), result.getInt("x"), result.getInt("y"), result.getInt("z"));
		ItemStack itemStack = new ItemStack(Material.getMaterial(result.getInt("material")));
		itemStack.setData(new MaterialData(itemStack.getType(), result.getByte("data")));
		Barrel barrel = new Barrel(result.getInt("id"), l, UUID.fromString(result.getString("creator")), itemStack, result.getInt("amount"));
		barrels.put(l, barrel);

		if (this.useHolographicDisplays) {
		    ArrayList<Vector> locationsToCheck = new ArrayList<Vector>();
		    Vector start = l.toVector();
		    locationsToCheck.add((start.clone().add(new Vector(1, 0, 0))));
		    locationsToCheck.add((start.clone().add(new Vector(-1, 0, 0))));
		    locationsToCheck.add((start.clone().add(new Vector(0, 0, 1))));
		    locationsToCheck.add((start.clone().add(new Vector(0, 0, -1))));

		    for (Entity e : l.getChunk().getEntities()) {
			if (e instanceof ItemFrame) {
			    ItemFrame itemFrame = (ItemFrame) e;
			    for (Vector toCheck : locationsToCheck) {
				Vector frameVector = itemFrame.getLocation().getBlock().getLocation().toVector();
				Vector toCheckVector = toCheck;
				if (frameVector.equals(toCheckVector)) {
				    Hologram hologram = HologramsAPI.createHologram(this, toCheck.toLocation(itemFrame.getWorld()));
				    TextLine textLine = hologram.appendTextLine("A hologram line");
				    barrel.addHologram(hologram);
				}
			    }
			}

		    }
		}

		if (result.getInt("id") >= this.nextID)
		    this.nextID = result.getInt("id") + 1;
	    }
	} catch (Exception e) {
	    getServer().getLogger().severe(e.getMessage());
	}
    }

    public ResultSet executeQuery(String query) {
	try {
	    Statement s = this.SQLConnection.createStatement();
	    s.execute(query);
	    return s.getResultSet();
	} catch (Exception e) {
	    getServer().getLogger().severe(e.getMessage());
	}
	return null;
    }

    public void destroyBarrel(Location location) {
	Barrel barrel = barrels.get(location);
	ItemStack itemStack = new ItemStack(barrel.getItemStack());

	while (barrel.getItemAmount() > 0) {
	    itemStack.setAmount((barrel.getItemAmount() >= 64 ? 64 : barrel.getItemAmount()));
	    barrel.setItemAmount(barrel.getItemAmount() - itemStack.getAmount());
	    Item itemDropped = location.getWorld().dropItem(location, itemStack);
	    itemDropped.setVelocity(new Vector(0, 0, 0));
	}

	this.executeQuery("DELETE FROM `barrels-barrels` WHERE `id`='" + barrel.getID() + "'");
	barrels.remove(location);

    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
	
	
	
	
	getServer().broadcastMessage("sometinhigh " + e.getRightClicked().getCustomName());
	
	
	
	
	
	if (e.isCancelled())
	    return;
	if (e.getRightClicked().getType() == EntityType.ITEM_FRAME) {
	    Player p = e.getPlayer();
	    ItemFrame frame = ((ItemFrame) e.getRightClicked());

	    ItemStack frameItem = frame.getItem();
	    ItemStack playerItem = p.getItemInHand();

	    Block base = frame.getLocation().getBlock().getRelative(frame.getAttachedFace());

	    if (p.isSneaking() && barrels.containsKey(base.getLocation())) {
		p.sendMessage("[Barrels] Barrel item count: " + barrels.get(base.getLocation()).getItemAmount());
		e.setCancelled(true);
		return;
	    }

	    // Player is trying to insert item in frame to create barrel
	    if (frameItem.getType().equals(Material.AIR) && playerItem.getAmount() >= 1) {

		// Remove existing barrels that don't match the item
		if (this.barrels.containsKey(base.getLocation()) && !barrels.get(base.getLocation()).getItemStack().getType().equals(playerItem.getType())) {
		    destroyBarrel(base.getLocation());
		}

		// Base is a spruce log
		if (base.getType().equals(Material.LOG) && base.getData() == 1) {
		    Barrel barrel = new Barrel(this.nextID, base.getLocation(), p.getUniqueId(), playerItem, 0);
		    barrels.put(base.getLocation(), barrel);
		    try {
			PreparedStatement s = this.SQLConnection.prepareStatement("INSERT INTO `barrels-barrels` (id, world, x, y, z, creator, material, data, amount) VALUES (?,?,?,?,?,?,?,?,?)");
			s.setInt(1, barrel.getID());
			s.setString(2, barrel.getLocation().getWorld().getName());
			s.setInt(3, barrel.getLocation().getBlockX());
			s.setInt(4, barrel.getLocation().getBlockY());
			s.setInt(5, barrel.getLocation().getBlockZ());
			s.setString(6, barrel.getCreatorUUID().toString());
			s.setInt(7, barrel.getItemStack().getType().getId());
			s.setByte(8, barrel.getItemStack().getData().getData());
			s.setInt(9, barrel.getItemAmount());
			s.execute();

			this.nextID += 1;
		    } catch (SQLException e1) {
			e1.printStackTrace();
		    }
		    
		    Vector difference = frame.getLocation().getBlock().getLocation().toVector().subtract(barrel.getLocation().toVector());

		    p.getWorld().playEffect(frame.getLocation(), Effect.ENDER_SIGNAL, 31);
		    
		    Hologram hologram = HologramsAPI.createHologram(this, frame.getLocation().add(difference.multiply(0.2d).add(new Vector(0, 0.4, 0))));
		    TextLine textLine = hologram.appendTextLine("" + barrel.getItemAmount());
		    textLine.setTouchHandler(new TouchHandler () {

			@Override
			public void onTouch(Player arg0) {
			    // TODO Auto-generated method stub
			    
			}});
		    barrel.addHologram(hologram);
		}
		return;
	    } else {
		// Player is trying to insert stuff into the barrel
		if (!playerItem.getType().equals(Material.AIR) && playerItem.getAmount() > 0) {
		    if (barrels.containsKey(base.getLocation())) {
			Barrel barrel = barrels.get(base.getLocation());
			if (playerItem.getType().equals(barrel.getItemStack().getType())) {
			    // Insert the item
			    int amountInserting = playerItem.getAmount();
			    amountInserting = (barrel.getItemAmount() + amountInserting > barrelMaxCapacity ? barrelMaxCapacity - barrel.getItemAmount() : amountInserting);
			    if (barrel.getItemAmount() < barrelMaxCapacity && barrel.getItemAmount() + amountInserting <= barrelMaxCapacity) {

				p.getItemInHand().setAmount(p.getItemInHand().getAmount() - amountInserting);
				if (amountInserting == p.getItemInHand().getAmount()) {
				    p.setItemInHand(null);
				}

				barrel.setItemAmount(barrel.getItemAmount() + amountInserting);
				this.executeQuery("UPDATE `barrels-barrels` SET `amount`='" + barrel.getItemAmount() + "' WHERE `id`='" + barrel.getID() + "'");

				// p.sendMessage("[Barrels] " + barrel.getItemStack().getType().toString() + " amount: " + barrel.getItemAmount());
			    } else if (barrel.getItemAmount() >= barrelMaxCapacity)
				p.getWorld().playEffect(frame.getLocation(), Effect.CLICK1, 31);
			}
			e.setCancelled(true);
		    }
		} else if (barrels.containsKey(base.getLocation())) {
		    e.setCancelled(true);
		}
	    }
	}
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemFrameBrokenByEntity(HangingBreakByEntityEvent e) {
	if (e.isCancelled())
	    return;

	if (e.getEntity() instanceof ItemFrame) {
	    ItemFrame frame = ((ItemFrame) e.getEntity());
	    Block base = frame.getLocation().getBlock().getRelative(frame.getAttachedFace());
	    // Base is a spruce log
	    if (base.getType().equals(Material.LOG) && base.getData() == 1) {
		if (barrels.containsKey(base.getLocation())) {
		    destroyBarrel(base.getLocation());
		}
	    }
	}
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent e) {
	if (e.isCancelled())
	    return;

	// Block is a spruce log
	if (e.getBlock().getType().equals(Material.LOG) && e.getBlock().getData() == 1) {
	    if (barrels.containsKey(e.getBlock().getLocation()))
		destroyBarrel(e.getBlock().getLocation());
	}
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void itemFrameItemRemoval(EntityDamageByEntityEvent e) {
	if (e.isCancelled())
	    return;

	if (e.getEntity() instanceof ItemFrame && e.getDamager() instanceof Player) {
	    ItemFrame frame = ((ItemFrame) e.getEntity());
	    Player p = (Player) e.getDamager();
	    Block base = frame.getLocation().getBlock().getRelative(frame.getAttachedFace());
	    if (barrels.containsKey(base.getLocation())) {
		Barrel barrel = barrels.get(base.getLocation());
		e.setCancelled(true);
		if (barrel.getItemAmount() > 0 && barrel.getItemStack().getType().equals(frame.getItem().getType())) {
		    ItemStack s = new ItemStack(barrel.getItemStack());
		    int amountRemoved = (barrel.getItemAmount() >= 64 ? 64 : barrel.getItemAmount());
		    s.setAmount(amountRemoved);

		    barrel.setItemAmount(barrel.getItemAmount() - amountRemoved);
		    this.executeQuery("UPDATE `barrels-barrels` SET `amount`='" + barrel.getItemAmount() + "' WHERE `id`='" + barrel.getID() + "'");

		    Vector v = new Vector(frame.getFacing().getModX(), frame.getFacing().getModY(), frame.getFacing().getModZ());
		    Item itemDropped = p.getWorld().dropItem(frame.getLocation().add(v.multiply(0.4d)).add(0, -0.4d, 0), s);
		    itemDropped.setVelocity(new Vector(0, 0, 0));

		    // p.sendMessage("[Barrels] " + barrel.getItemStack().getType().toString() + " amount: " + barrel.getItemAmount());
		} else if (barrel.getItemStack().getType().equals(frame.getItem().getType())) {
		    p.getWorld().playEffect(frame.getLocation(), Effect.SMOKE, 31);
		    p.getWorld().playEffect(frame.getLocation(), Effect.CLICK1, 31);
		} else
		    e.setCancelled(false);

	    }
	}
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onInventoryMoveItem(InventoryMoveItemEvent e) {
	if (e.getDestination().getHolder() instanceof Hopper) {
	    Hopper hopper = (Hopper) e.getDestination().getHolder();
	    Block down = hopper.getBlock().getRelative(BlockFace.DOWN);
	    if (hopper.getBlock().getData() == 0 && barrels.containsKey(down.getLocation())) {
		Barrel barrel = barrels.get(down.getLocation());
		if (barrel.getItemStack().isSimilar(e.getItem())) {
		    // There is barrel and it has the same item type.

		    barrel.setItemAmount(barrel.getItemAmount() + e.getItem().getAmount());
		    this.executeQuery("UPDATE `barrels-barrels` SET `amount`='" + barrel.getItemAmount() + "' WHERE `id`='" + barrel.getID() + "'");

		    // e.getDestination().remove(new ItemStack(e.getItem().getType(), e.getItem().getAmount()));
		    // e.getSource().remove(new ItemStack(e.getItem().getType(), e.getItem().getAmount()));

		    for (ItemStack itemStack : e.getSource().getContents())
			if (itemStack != null)
			    getServer().broadcastMessage("source Item type: " + itemStack.getType().toString() + " amount: " + itemStack.getAmount());

		    for (ItemStack itemStack : e.getDestination().getContents())
			if (itemStack != null)
			    getServer().broadcastMessage("destination Item type: " + itemStack.getType().toString() + " amount: " + itemStack.getAmount());

		    e.setCancelled(true);

		    for (ItemStack itemStack : e.getSource().getContents())
			if (itemStack != null)
			    getServer().broadcastMessage("source Item type: " + itemStack.getType().toString() + " amount: " + itemStack.getAmount());

		    for (ItemStack itemStack : e.getDestination().getContents())
			if (itemStack != null)
			    getServer().broadcastMessage("destination Item type: " + itemStack.getType().toString() + " amount: " + itemStack.getAmount());

		    // e.setItem(null);
		    // e.getItem().setAmount(0);
		    // e.getItem().setType(Material.AIR);
		}
	    }
	}
    }
}