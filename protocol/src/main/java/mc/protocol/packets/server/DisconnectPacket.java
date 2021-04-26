package mc.protocol.packets.server;

import lombok.Data;
import mc.protocol.State;
import mc.protocol.io.NetByteBuf;
import mc.protocol.packets.Packet;

/**
 * Diconnect packet.
 *
 * <p>Отключение клиента сервером с указанием причины.</p>
 *
 * <p>Структура пакета</p>
 * <pre>
 * | FIELD  | TYPE | NOTES                            |
 * |--------|------|----------------------------------|
 * | Reason | Text | Причина отключения. Опционально. |
 * </pre>
 *
 * @see <a href="https://wiki.vg/index.php?title=Protocol&oldid=7368#Disconnect_2" target="_top">Disconnect</a>
 * @see State
 */
@Data
public class DisconnectPacket implements Packet {

	/**
	 * Причина отключения.
	 *
	 * <p>Пример:</p>
	 * <pre>
	 * {
	 *     "text": "foo"
	 * }
	 * </pre>
	 */
	private String reason;

	@Override
	public void readSelf(NetByteBuf netByteBuf) {
		this.reason = netByteBuf.readString();
	}

	@Override
	public void writeSelf(NetByteBuf netByteBuf) {
		netByteBuf.writeString(reason);
	}
}
