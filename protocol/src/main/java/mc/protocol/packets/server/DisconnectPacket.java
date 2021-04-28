package mc.protocol.packets.server;

import lombok.Data;
import mc.protocol.State;
import mc.protocol.io.NetByteBuf;
import mc.protocol.model.text.Text;
import mc.protocol.packets.ServerSidePacket;
import mc.protocol.serializer.TextSerializer;

/**
 * Diconnect packet.
 *
 * <p>Отключение клиента сервером с указанием причины.</p>
 *
 * <p>Структура пакета</p>
 * <pre>
 * | FIELD       | TYPE   | NOTES                            |
 * |-------------|--------|----------------------------------|
 * | JSON Reason | String | Причина отключения. Опционально. |
 * </pre>
 *
 * <p>Пример JSON Reason</p>
 * <pre>
 * {
 *     "text": "foo"
 * }
 * </pre>
 *
 * @see <a href="https://wiki.vg/index.php?title=Protocol&oldid=7368#Disconnect_2" target="_top">Disconnect</a>
 * @see State
 */
@Data
public class DisconnectPacket implements ServerSidePacket {

	/**
	 * Причина отключения.
	 */
	private Text reason;

	@Override
	public void writeSelf(NetByteBuf netByteBuf) {
		netByteBuf.writeString(TextSerializer.toJsonObject(reason).toString());
	}
}
