package mc.protocol.packets.client;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import mc.protocol.io.NetByteBuf;
import mc.protocol.model.Location;
import mc.protocol.model.Look;
import mc.protocol.packets.ClientSidePacket;

/**
 * Клиент сообщает о движении Игрока.
 *
 * <p>Структура пакета</p>
 * <pre>
 * | FIELD     | TYPE    | NOTES                                                      |
 * |-----------|---------|------------------------------------------------------------|
 * | X         | Double  | Абсолютная позиция по X                                    |
 * | Y         | Double  | Абсолютная позиция по Y.                                   |
 * |           |         | Имеется ввиду позиция ног. Голова находиться выше на 1.62f |
 * | Z         | Double  | Абсолютная позиция по Z                                    |
 * | Yaw       | Float   | Абсолютный поворот головы по OX, в градусах                |
 * | Pitch     | Float   | Абсолютный поворот головы по OY, в градусах                |
 * | On Ground | Boolean | true, если Игрок находится на земле                        |
 * </pre>
 *
 * @see <a href="https://wiki.vg/index.php?title=Protocol&oldid=14204#Player_Position_And_Look_.28serverbound.29">Player Position And Look (serverbound)</a>
 */
@NoArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class CPlayerPositionAndLookPacket implements ClientSidePacket {

	private Location position;
	private Look look;
	@SuppressWarnings("java:S116")
	private boolean onGround;

	@Override
	public void readSelf(NetByteBuf netByteBuf) {
		double x = netByteBuf.readDouble();
		double y = netByteBuf.readDouble();
		double z = netByteBuf.readDouble();
		this.position = new Location(x, y, z);

		float yaw = netByteBuf.readFloat();
		float pitch = netByteBuf.readFloat();
		this.look = new Look(yaw, pitch);

		this.onGround = netByteBuf.readBoolean();
	}

	public double getYPositionHead() {
		return this.position.getY() + 1.62f;
	}
}
