package mc.protocol;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mc.protocol.packets.Packet;
import mc.protocol.packets.PacketDirection;
import mc.protocol.packets.client.HandshakePacket;
import mc.protocol.packets.client.LoginStartPacket;
import mc.protocol.packets.client.StatusServerRequest;
import mc.protocol.packets.server.DisconnectPacket;
import mc.protocol.packets.server.StatusServerResponse;

import javax.annotation.Nullable;

@RequiredArgsConstructor
public enum State {

	HANDSHAKING(-1,
			// server bound
			ImmutableBiMap.of(0x00, HandshakePacket.class)
	),
	STATUS(1,
			// server bound
			ImmutableBiMap.of(0x00, StatusServerRequest.class),
			// client bound
			ImmutableBiMap.of(0x00, StatusServerResponse.class)
	),
	LOGIN(2,
			// server bound
			ImmutableBiMap.of(0x00, LoginStartPacket.class),
			// client bound
			ImmutableBiMap.of(0x00, DisconnectPacket.class)
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

	private final BiMap<Integer, Class<? extends Packet>> serverBoundPackets;
	private final BiMap<Integer, Class<? extends Packet>> clientBoundPackets;

	State(int id, BiMap<Integer, Class<? extends Packet>> serverBoundPackets) {
		this.id = id;
		this.serverBoundPackets = serverBoundPackets;
		this.clientBoundPackets = ImmutableBiMap.of();
	}

	@Nullable
	public Class<? extends Packet> getPacketById(PacketDirection direction, int id) {
		if (PacketDirection.CLIENT_BOUND == direction) {
			return clientBoundPackets == null ? null : clientBoundPackets.get(id);
		} else {
			return serverBoundPackets == null ? null : serverBoundPackets.get(id);
		}
	}

	@Nullable
	public Integer getIdByPacket(PacketDirection direction, Class<? extends Packet> clazz) {
		if (PacketDirection.CLIENT_BOUND == direction) {
			return clientBoundPackets == null ? null : clientBoundPackets.inverse().get(clazz);
		} else {
			return serverBoundPackets == null ? null : serverBoundPackets.inverse().get(clazz);
		}
	}
}
