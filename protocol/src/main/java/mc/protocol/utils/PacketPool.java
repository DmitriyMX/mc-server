package mc.protocol.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mc.protocol.packets.ClientSidePacket;
import org.apache.commons.pool2.ObjectPool;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class PacketPool {

	@SuppressWarnings("rawtypes")
	private final Map<Class<? extends ClientSidePacket>, ObjectPool> mapPoolPackets;

	@SuppressWarnings("unchecked")
	public <P extends ClientSidePacket> P borrowObject(Class<P> packetClass) throws Exception {
		return (P) mapPoolPackets.get(packetClass).borrowObject();
	}

	@SuppressWarnings("unchecked")
	public <P extends ClientSidePacket> void returnObject(P packet) throws Exception {
		mapPoolPackets.get(packet.getClass()).returnObject(packet);
	}
}
