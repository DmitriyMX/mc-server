package mc.server.world;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mc.protocol.world.Chunk;

@RequiredArgsConstructor
@Getter
public class VoidChunk implements Chunk {

	private final int x;
	private final int z;
}
