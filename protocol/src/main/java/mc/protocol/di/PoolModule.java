package mc.protocol.di;

import dagger.Module;
import dagger.Provides;
import mc.protocol.NettyConnectionContext;
import mc.protocol.State;
import mc.protocol.packets.ClientSidePacket;
import mc.protocol.packets.UnknownPacket;
import mc.protocol.pool.NettyConnectionContextFactory;
import mc.protocol.pool.PacketFactory;
import mc.protocol.pool.PacketPool;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Module
public class PoolModule {

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
	ObjectPool<NettyConnectionContext> providePoolNettyConnectionContext() {
		return new GenericObjectPool<>(new NettyConnectionContextFactory());
	}
}
