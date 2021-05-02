package mc.protocol.packets.client;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import mc.protocol.io.NetByteBuf;
import mc.protocol.model.Look;
import mc.protocol.packets.ClientSidePacket;

/**
 * Клиент сообщает о повороте головы Игрока.
 *
 * <p>Структура пакета</p>
 * <pre>
 * | FIELD     | TYPE    | NOTES                                       |
 * |-----------|---------|---------------------------------------------|
 * | Yaw       | Float   | Абсолютный поворот головы по OX, в градусах |
 * | Pitch     | Float   | Абсолютный поворот головы по OY, в градусах |
 * | On Ground | Boolean | true, если Игрок находится на земле         |
 * </pre>
 *
 * @see <a href="https://wiki.vg/index.php?title=Protocol&oldid=14204#Player_Look">Player Look</a>
 */
@NoArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class PlayerLookPacket implements ClientSidePacket {

	private Look look;
	private boolean onGround;

	@Override
	public void readSelf(NetByteBuf netByteBuf) {
		float yaw = netByteBuf.readFloat();
		float pitch = netByteBuf.readFloat();
		this.look = new Look(yaw, pitch);

		this.onGround = netByteBuf.readBoolean();
	}
}
