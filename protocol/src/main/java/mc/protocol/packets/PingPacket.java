package mc.protocol.packets;

import lombok.Data;
import mc.protocol.io.NetByteBuf;

/**
 * Пинг-пакет.
 *
 * <p>Эхо-пакет, которым проверяется качество соединения между <b>Клиентом</b> и <b>Сервером</b>.</p>
 *
 * <p>По спецификации:</p>
 * <oi>
 *     <li>если <b>Сервер</b> не ответил <b>Клиенту</b> в течении 20 секунд, <b>Клиент</b> отключается
 *     и выдаёт ошибку <i>"Timed out"</i>.</li>
 *     <li>если <b>Клиент</b> не отвечает <b>Серверу</b> в течении 30 секунд, <b>Сервер</b> отключает <b>Клиента</b>.</li>
 * </oi>
 *
 * <p>Структура пакета</p>
 * <pre>
 * | FIELD   | TYPE | NOTES                  |
 * |---------|------|------------------------|
 * | Payload | Long | Любое уникальное число |
 * </pre>
 *
 * @see <a href="https://wiki.vg/index.php?title=Protocol&oldid=7368#Keep_Alive">Keep Alive</a>
 */
@Data
public class PingPacket implements Packet {

	private Long payload;

	@Override
	public void readSelf(NetByteBuf netByteBuf) {
		payload = netByteBuf.readLong();
	}

	@Override
	public void writeSelf(NetByteBuf netByteBuf) {
		netByteBuf.writeLong(payload);
	}
}
