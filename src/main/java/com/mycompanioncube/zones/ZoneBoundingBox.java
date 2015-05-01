package com.mycompanioncube.zones;

/**
 * Bounding box for a zone
 * 
 * @author Serial Coder Lain (serialcoderlain@gmail.com)
 */
public class ZoneBoundingBox {

	protected final int sX;
	protected final int sY;
	protected final int sZ;
	protected final int eX;
	protected final int eY;
	protected final int eZ;

	public ZoneBoundingBox(int sX, int sY, int sZ, int eX, int eY, int eZ) {
		this.sX = Math.max(sX, eX);
		this.sY = Math.max(sY, eY);
		this.sZ = Math.max(sZ, eZ);
		this.eX = Math.min(sX, eX);
		this.eY = Math.min(sY, eY);
		this.eZ = Math.min(sZ, eZ);
	}

	public ZoneBoundingBox(int cX, int cY, int cZ) {
		this(cX * 16, cY * 16, cZ * 16, (cX + 1) * 16, (cY + 1) * 16,
				(cZ + 1) * 16);
	}

	public ZoneBoundingBox(int cX, int y1, int y2, int cZ) {
		this(cX * 16, y1, cZ * 16, (cX + 1) * 16, y2,
				(cZ + 1) * 16);

	}

	public boolean isInZone(int x, int y, int z) {
		return (x <= sX && x >= eX && y <= sY && y >= eY && z <= sZ && z >= eZ);
	}

	@Override
	public String toString() {
		return "<" + sX + ", " + sY + ", " + sZ + "> - " + "<" + eX + ", " + eY
				+ ", " + eZ + ">";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof ZoneBoundingBox)) return false;
		
		ZoneBoundingBox z = (ZoneBoundingBox) obj;
		
		return (z.sX == sX &&
				z.sY == sY &&
				z.sZ == sZ &&
				z.eX == eX &&
				z.eY == eY &&
				z.eZ == eZ);
	}
}
