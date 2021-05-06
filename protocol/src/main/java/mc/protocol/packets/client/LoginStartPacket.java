package mc.protocol.packets.client;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import mc.protocol.io.NetByteBuf;
import mc.protocol.packets.ClientSidePacket;

/**
 * Login start packet.
 *
 * <p>Начало авторизации.</p>
 *
 * <p>Структура пакета</p>
 * <pre>
 * | FIELD | TYPE   | NOTES            |
 * |-------|--------|------------------|
 * | Name  | String | Имя/Логин игрока |
 * </pre>
 *
 * @see <a href="https://wiki.vg/index.php?title=Protocol&oldid=7368#Login_Start" target="_top">Login start</a>
 */
@NoArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class LoginStartPacket implements ClientSidePacket {

	private String name;

	@Override
	public void readSelf(NetByteBuf netByteBuf) {
		this.name = netByteBuf.readString();
	}

	@Override
	public void passivate() {
		this.name = null;
	}

}
