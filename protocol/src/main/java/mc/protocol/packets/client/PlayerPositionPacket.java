package mc.protocol.packets.client;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import mc.protocol.io.NetByteBuf;
import mc.protocol.model.Location;
import mc.protocol.packets.ClientSidePacket;

/**
 * Клиент сообщает о движении Игрока.
 *
 * <p>Структура пакета</p>
 * <pre>
 * | FIELD     | TYPE    | NOTES                               |
 * |-----------|---------|-------------------------------------|
 * | X         | Double  | Абсолютная позиция по X             |
 * | Feet Y    | Double  | Абсолютная позиция ног по Y.        |
 * |           |         | Голова находиться выше на 1.62f     |
 * | Z         | Double  | Абсолютная позиция по Z             |
 * | On Ground | Boolean | true, если Игрок находится на земле |
 * </pre>
 *
 * @see <a href="https://wiki.vg/index.php?title=Protocol&oldid=14204#Player_Position">Player Position</a>
 */
@NoArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class PlayerPositionPacket implements ClientSidePacket {

	private Location position;
	private boolean onGround;

	@Override
	public void readSelf(NetByteBuf netByteBuf) {
		double x = netByteBuf.readDouble();
		double y = netByteBuf.readDouble();
		double z = netByteBuf.readDouble();
		this.position = new Location(x, y, z);

		this.onGround = netByteBuf.readBoolean();
	}

	public double getYPositionHead() {
		return this.position.getY() + 1.62f;
	}
}
