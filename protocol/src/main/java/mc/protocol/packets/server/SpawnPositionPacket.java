package mc.protocol.packets.server;

import lombok.Data;
import mc.protocol.io.NetByteBuf;
import mc.protocol.model.Location;
import mc.protocol.packets.ServerSidePacket;

/**
 * Спавн позиция игрока.
 *
 * <p>Используется призаходе игрока на сервер.</p>
 *
 * <p>Структура пакета</p>
 * <pre>
 * | FIELD    | TYPE     | NOTES                 |
 * |----------|----------|-----------------------|
 * | Location | Position | Локация спавна игрока |
 * </pre>
 *
 * @see <a href="https://wiki.vg/index.php?title=Protocol&oldid=14204#Spawn_Position">Spawn Position</a>
 */
@Data
public class SpawnPositionPacket implements ServerSidePacket {

	private Location spawn;

	@Override
	public void writeSelf(NetByteBuf netByteBuf) {
		long spawnSerialized =
				((long) (floorDouble(spawn.getX()) & 0x3FFFFFF) << 38)
				| ((long) (floorDouble(spawn.getY()) & 0xFFF) << 26)
				| (floorDouble(spawn.getZ()) & 0x3FFFFFF);
		netByteBuf.writeLong(spawnSerialized);
	}

	private static int floorDouble(double value) {
		int i = (int) value;
		return value < (double) i ? i - 1 : i;
	}

}
