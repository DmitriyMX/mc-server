package mc.protocol;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mc.protocol.packets.ClientSidePacket;
import mc.protocol.packets.Packet;
import mc.protocol.packets.PingPacket;
import mc.protocol.packets.ServerSidePacket;
import mc.protocol.packets.client.*;
import mc.protocol.packets.server.DisconnectPacket;
import mc.protocol.packets.server.JoinGamePacket;
import mc.protocol.packets.server.LoginSuccessPacket;
import mc.protocol.packets.server.StatusServerResponse;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
public enum State {

	HANDSHAKING(-1,
			// client side
			Map.of(0x00, HandshakePacket.class)
	),
	STATUS(1,
			// client side
			Map.of(
					0x00, StatusServerRequestPacket.class,
					0x01, PingPacket.class
			),
			// server side
			Map.of(
					StatusServerResponse.class, 0x00,
					PingPacket.class, 0x01
			)
	),
	LOGIN(2,
			// server bound
			Map.of(0x00, LoginStartPacket.class),
			// client bound
			Map.of(
					DisconnectPacket.class, 0x00,
					LoginSuccessPacket.class, 0x02
			)
	),
	PLAY(3,
			// server bound
			Map.of(
					0x04, ClientSettingsPacket.class,
					0x09, PluginMessagePacket.class
			),
			// client bound
			Map.of(JoinGamePacket.class, 0x23)
	);

	@Nullable
	public static State getById(int id) {
		for (State state : State.values()) {
			if (state.id == id) {
				return state;
			}
		}

		return null;
	}

	@Getter
	private final int id;

	@Getter
	private final Map<Integer, Class<? extends ClientSidePacket>> clientSidePackets;
	private final Map<Class<? extends ServerSidePacket>, Integer> serverSidePackets;

	State(int id, Map<Integer, Class<? extends ClientSidePacket>> clientSidePackets) {
		this.id = id;
		this.clientSidePackets = clientSidePackets;
		this.serverSidePackets = Collections.emptyMap();
	}

	@Nullable
	public Class<? extends ClientSidePacket> getClientSidePacketById(int id) {
		return clientSidePackets == null ? null : clientSidePackets.get(id);
	}

	@Nullable
	public Integer getServerSidePacketId(Class<? extends Packet> clazz) {
		return serverSidePackets == null ? null : serverSidePackets.get(clazz);
	}
}
