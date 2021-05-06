package mc.protocol.di;

import dagger.Module;
import dagger.Provides;
import mc.protocol.NettyServer;
import mc.protocol.State;
import mc.protocol.api.Server;
import mc.protocol.packets.ClientSidePacket;
import mc.protocol.packets.UnknownPacket;
import mc.protocol.utils.EventBus;
import mc.protocol.utils.PacketFactory;
import mc.protocol.utils.PacketPool;
import mc.protocol.utils.SimpleEventBus;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Module
public class ProtocolModule {

	@Provides
	@ServerScope
	Server provideServer(PacketPool packetPool, EventBus eventBus) {
		return new NettyServer(packetPool, eventBus);
	}

	@Provides
	@ServerScope
	@SuppressWarnings({ "rawtypes", "unchecked" })
	PacketPool providePacketPool() {
		Map<Class<? extends ClientSidePacket>, ObjectPool> map = Stream.of(State.values())
				.flatMap(state -> state.getClientSidePackets().values().stream())
				.distinct()
				.collect(Collectors.toMap(
						packetClass -> packetClass,
						packetClass -> new GenericObjectPool(new PacketFactory<>(packetClass))));
		map.put(UnknownPacket.class, new GenericObjectPool(new PacketFactory<>(UnknownPacket.class)));

		return new PacketPool(map);
	}

	@Provides
	@ServerScope
	EventBus provideEventBus() {
		return new SimpleEventBus();
	}
}
