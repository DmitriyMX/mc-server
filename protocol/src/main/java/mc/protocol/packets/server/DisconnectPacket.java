package mc.protocol.packets.server;

import com.eclipsesource.json.Json;
import lombok.Data;
import mc.protocol.State;
import mc.protocol.io.NetByteBuf;
import mc.protocol.packets.ServerSidePacket;

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
	private String reason;

	@Override
	public void writeSelf(NetByteBuf netByteBuf) {
		netByteBuf.writeString(Json.object().add("text", reason).toString());
	}
}
