package mc.protocol.packets.server;

import lombok.Data;
import mc.protocol.io.NetByteBuf;
import mc.protocol.packets.ServerSidePacket;

import java.util.UUID;

/**
 * Подтверждение успешного логина.
 *
 * <p>Структура пакета</p>
 * <pre>
 * | FIELD    | TYPE        | NOTES                         |
 * |----------|-------------|-------------------------------|
 * | UUID     | String (36) | Уникальный ID игрока          |
 * | Username | String (16) | Имя игрока, выданное сервером |
 * </pre>
 *
 * @see <a href="https://wiki.vg/index.php?title=Protocol&oldid=14204#Login_Success">Login Success</a>
 */
@Data
public class LoginSuccessPacket implements ServerSidePacket {

	private UUID uuid;
	private String name;

	@Override
	public void writeSelf(NetByteBuf netByteBuf) {
		netByteBuf.writeString(uuid.toString());
		netByteBuf.writeString(name);
	}
}
