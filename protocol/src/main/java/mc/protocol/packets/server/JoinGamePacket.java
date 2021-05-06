package mc.protocol.packets.server;

import lombok.Data;
import mc.protocol.utils.Difficulty;
import mc.protocol.utils.GameMode;
import mc.protocol.utils.LevelType;
import mc.protocol.io.NetByteBuf;
import mc.protocol.packets.ServerSidePacket;

/**
 * Join game packet.
 *
 * <p>Структура пакета</p>
 * <pre>
 * | FIELD              | TYPE          | NOTES                                                                |
 * |--------------------|---------------|----------------------------------------------------------------------|
 * | Entity ID          | Integer       | ID сущности (игрока)                                                 |
 * | Gamemode           | Unsigned Byte | 0: Survival                                                          |
 * |                    |               | 1: Creative                                                          |
 * |                    |               | 2: Adventure                                                         |
 * |                    |               | 3: Spectator                                                         |
 * |                    |               | Bit 3 (0x8) is the hardcore flag.                                    |
 * | Dimension          | Integer       | -1: Nether                                                           |
 * |                    |               | 0: Overworld                                                         |
 * |                    |               | 1: End                                                               |
 * | Difficulty         | Unsigned Byte | 0: peaceful                                                          |
 * |                    |               | 1: easy                                                              |
 * |                    |               | 2: normal                                                            |
 * |                    |               | 3: hard                                                              |
 * | Max Players        | Unsigned Byte | Когда-то использовался клиентом для                                  |
 * |                    |               | отображения списка игроков. Теперь не используется                   |
 * | Level Type         | String (16)   | Принимает одно из значений:                                          |
 * |                    |               | default, flat, largeBiomes, amplified, default_1_1                   |
 * | Reduced Debug Info | Boolean       | Если true, то Клиент отображает меньше отладочной информации (в F3?) |
 * </pre>
 *
 * @see <a href="https://wiki.vg/index.php?title=Protocol&oldid=14204#Join_Game">Join Game</a>
 */
@Data
public class JoinGamePacket implements ServerSidePacket {

	private int entityId;
	private GameMode gameMode;
	private int dimension;
	private Difficulty difficulty;
	private LevelType levelType;
	private boolean reducedDebugInfo;

	@Override
	public void writeSelf(NetByteBuf netByteBuf) {
		netByteBuf.writeInt(entityId);
		netByteBuf.writeUnsignedByte(gameMode.getId());
		netByteBuf.writeInt(dimension);
		netByteBuf.writeUnsignedByte(difficulty.getId());
		netByteBuf.writeUnsignedByte(0); // Max Players, unused
		netByteBuf.writeString(levelType.getType());
		netByteBuf.writeBoolean(reducedDebugInfo);
	}
}
