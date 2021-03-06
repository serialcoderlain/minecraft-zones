package com.mycompanioncube.zones;

import java.util.ArrayList;
import java.util.List;

/**
 * Class defining a single Zone with multiple bounding boxes
 * 
 * @author Serial Coder Lain (serialcoderlain@gmail.com)
 */
public class Zone {
	/** Zone name */
	protected String zoneName;

	/** Creator name */
	protected String zoneCreator = "System";

	/** List of all the bounding boxes that makes up this zone */
	protected List<ZoneBoundingBox> boundingBoxes = new ArrayList<ZoneBoundingBox>();
	protected List<String> playerFound = new ArrayList<String>();

	/** Is this zone protected */
	private boolean isProtected = false;

	/**
	 * Default constructor
	 * 
	 * @param newZoneName
	 *            The name of the zone
	 */
	public Zone(String newZoneName) {
		this(newZoneName, "System");
	}

	/**
	 * Creates a zone and specifies the creator of the zone
	 * 
	 * @param newZoneName
	 *            The name of the zone
	 * @param zoneCreator
	 *            The name of the player creating the zone
	 */
	public Zone(String newZoneName, String zoneCreator) {
		this.zoneName = newZoneName;
		this.zoneCreator = zoneCreator;
	}

	/**
	 * Checks to see if the given x,y,z position is within any of the bounding
	 * boxes that make up this zone.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return True if this position is contained within this zone
	 */
	public boolean isInZone(int x, int y, int z) {
		if (boundingBoxes != null) {
			for (ZoneBoundingBox b : boundingBoxes) {
				if (b.isInZone(x, y, z)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Checks to see if this zone has been visited by the specified user
	 * 
	 * @param username
	 * @return True if user has visited this zone before
	 */
	public boolean hasPlayerVisitedZone(String username) {
		if (playerFound == null)
			return false;
		return playerFound.contains(username);
	}

	/**
	 * Set the zone as visited by the specified user
	 * 
	 * @param username
	 */
	public void setZoneVisitedByPlayer(String username) {
		if (playerFound == null)
			playerFound = new ArrayList<String>();
		playerFound.add(username);
	}

	/**
	 * Returns the name of the person creating the zone
	 * @return
	 */
	public String getZoneCreator() {
		return zoneCreator;
	}
	
	/**
	 * Sets the name of the person who created this zone
	 * @param zoneCreator
	 */
	public void setZoneCreator(String zoneCreator) {
		this.zoneCreator = zoneCreator;
	}
	
	/**
	 * Sets this zone as protected (hinders spawning of hostile mobs)
	 * 
	 * @param isProtected
	 */
	public void setProtected(boolean isProtected) {
		this.isProtected = isProtected;
	}

	/**
	 * Returns the name of this zone
	 * 
	 * @return
	 */
	public String getName() {
		return zoneName;
	}
	
	public void setName(String zoneName) {
		this.zoneName = zoneName;
	}

	/**
	 * Builder function that creates a new zone based on the given coordinates
	 * 
	 * @param string
	 * @param x1
	 * @param y1
	 * @param z1
	 * @param x2
	 * @param y2
	 * @param z2
	 * @return
	 */
	public static Zone buildZone(String string, int x1, int y1, int z1, int x2, int y2, int z2) {
		Zone z = new Zone(string);
		z.addBox(new ZoneBoundingBox(x1, y1, z1, x2, y2, z2));
		return z;
	}

	/**
	 * Adds a bounding box to this zone.
	 * 
	 * @param zoneBoundingBox
	 */
	void addBox(ZoneBoundingBox zoneBoundingBox) {
		if (boundingBoxes == null)
			boundingBoxes = new ArrayList<ZoneBoundingBox>();

		if (!boundingBoxes.contains(zoneBoundingBox)) {
			boundingBoxes.add(zoneBoundingBox);
		}
	}

	public boolean isProtected() {
		return isProtected;
	}

	/**
	 * Returns all the bounding boxes that makes up this zone
	 * 
	 * @return
	 */
	public List<ZoneBoundingBox> getBoxes() {
		return boundingBoxes;
	}

	/**
	 * Removes a bounding box from the zone.
	 * 
	 * @param zoneBoundingBox
	 * @return
	 */
	public boolean removeBox(ZoneBoundingBox zoneBoundingBox) {
		ZoneBoundingBox r = null;

		if (boundingBoxes == null)
			return false;

		for (ZoneBoundingBox b : boundingBoxes) {
			if (b.equals(zoneBoundingBox)) {
				r = b;
			}
		}

		if (r != null) {
			boundingBoxes.remove(r);
			return true;
		}

		return false;
	}
}
