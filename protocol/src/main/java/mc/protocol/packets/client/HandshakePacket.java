package mc.protocol.packets.client;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import mc.protocol.State;
import mc.protocol.io.NetByteBuf;
import mc.protocol.packets.ClientSidePacket;

/**
 * Handshake packet.
 *
 * <p>Данный пакет заставляет сервер переключить текущий {@link State}</p>
 *
 * <p>Структура пакета</p>
 * <pre>
 * | FIELD            | TYPE           | NOTES                                        |
 * |------------------|----------------|----------------------------------------------|
 * | Protocol version | VarInt         | Версия протокола [1]                         |
 * | Server address   | Stirng         | Hostname или IP                              |
 * | Server port      | Unsigned Short | Порт сервера                                 |
 * | Next stage       | VarInt         | ID State на который необходимо переключиться |
 *
 * [1] - <a href="https://wiki.vg/Protocol_version_numbers" target="_top">Protocol version numbers</a>
 * </pre>
 *
 * @see <a href="https://wiki.vg/index.php?title=Protocol&oldid=7368#Handshake" target="_top">Handshake</a>
 * @see State
 */
@NoArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class HandshakePacket implements ClientSidePacket {

	private int protocolVersion;
	private String host;
	private int port;
	private State nextState;

	@Override
	public void readSelf(NetByteBuf netByteBuf) {
		protocolVersion = netByteBuf.readVarInt();
		host = netByteBuf.readString(255);
		port = netByteBuf.readUnsignedShort();
		nextState = State.getById(netByteBuf.readVarInt());
	}

	@Override
	public void passivate() {
		this.protocolVersion = 0;
		this.host = null;
		this.port = 0;
		this.nextState = null;
	}

}
