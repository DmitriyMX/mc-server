package mc.server.world;

import mc.protocol.model.Location;
import mc.protocol.utils.LevelType;
import mc.protocol.world.Chunk;
import mc.protocol.world.World;

public class VoidWorld implements World {

	private static final Location spawn = new Location(7d, 130d, 7d);

	@Override
	public LevelType getLevelType() {
		return LevelType.FLAT;
	}

	@Override
	public Location getSpawn() {
		return VoidWorld.spawn;
	}

	@Override
	public Chunk getChunk(int x, int z) {
		return new VoidChunk(x, z);
	}
}
