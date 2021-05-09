package mc.protocol.world;

import mc.protocol.model.Location;
import mc.protocol.utils.LevelType;

public interface World {

	LevelType getLevelType();

	Location getSpawn();

	Chunk getChunk(int x, int z);
}
