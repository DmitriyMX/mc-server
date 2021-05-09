package mc.protocol.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Location {
	private double x;
	private double y;
	private double z;

	public int getIntX() {
		return (int) x;
	}

	public int getIntZ() {
		return (int) z;
	}

	public Location toChunkXZ() {
		return new Location(this.getIntX() >> 4, 0d, this.getIntZ() >> 4);
	}
}
