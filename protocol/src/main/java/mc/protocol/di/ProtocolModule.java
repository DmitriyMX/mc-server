package mc.protocol.di;

import dagger.Module;
import dagger.Provides;
import lombok.RequiredArgsConstructor;
import mc.protocol.NettyConnectionContext;
import mc.protocol.NettyServer;
import mc.protocol.PacketInboundHandler;
import mc.protocol.api.Server;
import mc.protocol.event.EventBus;
import mc.protocol.event.SimpleEventBus;
import mc.protocol.io.codec.ProtocolDecoder;
import mc.protocol.pool.PacketPool;
import org.apache.commons.pool2.ObjectPool;

import javax.inject.Provider;

@Module
@RequiredArgsConstructor
public class ProtocolModule {

	private final boolean readUnknownPackets;

	@Provides
	@ServerScope
	Server provideServer(
			Provider<ProtocolDecoder> protocolDecoderProvider,
			Provider<PacketInboundHandler> packetInboundHandlerProvider,
			EventBus eventBus
	) {
		return new NettyServer(protocolDecoderProvider, packetInboundHandlerProvider, eventBus);
	}

	@Provides
	@ServerScope
	ProtocolDecoder provideProtocolDecoder(
			ObjectPool<NettyConnectionContext> poolNettyConnectionContext,
			PacketPool poolPackets
	) {
		return new ProtocolDecoder(readUnknownPackets, poolNettyConnectionContext, poolPackets);
	}

	@Provides
	@ServerScope
	PacketInboundHandler providePacketInboundHandler(
			ObjectPool<NettyConnectionContext> poolNettyConnectionContext,
			PacketPool packetPool,
			EventBus eventBus
	) {
		return new PacketInboundHandler(poolNettyConnectionContext, packetPool, eventBus);
	}

	@Provides
	@ServerScope
	EventBus provideEventBus() {
		return new SimpleEventBus();
	}

}
