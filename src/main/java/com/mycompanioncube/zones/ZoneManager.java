package com.mycompanioncube.zones;

import java.util.ArrayList;
import java.util.List;

/**
 * Manager for keeping track of zones.
 *  
 * @author Serial Coder Lain (serialcoderlain@gmail.com)
 */
public class ZoneManager {
	/**
	 * List of all zones
	 */
	protected List<Zone> zones = new ArrayList<Zone>();

	/**
	 * Find the zone at the given x, y, z coordinates
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public Zone getZone(int x, int y, int z) {
		for (Zone zo : zones) {
			if (zo.isInZone(x, y, z)) {
				return zo;
			}
		}
		return null;
	}

	/**
	 * Add a new zone to the manager list
	 * @param buildZone
	 */
	public void add(Zone buildZone) {
		zones.add(buildZone);

	}
	/**
	 * Removes a zone from the manager list
	 * @param buildZone
	 */
	public void delete(Zone buildZone) {
		zones.remove(buildZone);

	}
	
	/**
	 * Get a zone by its name.
	 * @param a
	 * @return
	 */
	public Zone getZoneByName(String a) {
		for (Zone zo : zones) {
			if (zo.getName().equals(a)) {
				return zo;
			}
		}
		return null;
	}

	/**
	 * Get a list of all the zones
	 * @return
	 */
	public List<Zone> getZones() {
		return zones;
	}
}
