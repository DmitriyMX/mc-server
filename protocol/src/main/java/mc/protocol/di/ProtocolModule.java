package mc.protocol.di;

import dagger.Module;
import dagger.Provides;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import mc.protocol.NettyServer;
import mc.protocol.PacketInboundHandler;
import mc.protocol.State;
import mc.protocol.io.codec.ProtocolDecoder;
import mc.protocol.io.codec.ProtocolEncoder;
import mc.protocol.io.codec.ProtocolSplitter;
import mc.protocol.packets.ClientSidePacket;
import mc.protocol.packets.UnknownPacket;
import mc.protocol.utils.PacketFactory;
import mc.protocol.utils.PacketPool;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

import javax.annotation.Nonnull;
import javax.inject.Provider;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Module
public class ProtocolModule {

	@Provides
	NettyServer provideServer(ServerBootstrap serverBootstrap) {
		return new NettyServer(serverBootstrap);
	}

	@Provides
	ServerBootstrap provideServerBootstrap(ChannelInitializer<SocketChannel> channelChannelInitializer) {
		ServerBootstrap bootstrap = new ServerBootstrap();

		bootstrap.group(new NioEventLoopGroup(1), new NioEventLoopGroup())
				.channel(NioServerSocketChannel.class)
				.childHandler(channelChannelInitializer);

		return bootstrap;
	}

	@Provides
	ChannelInitializer<SocketChannel> provideChannelChannelInitializer(
			Provider<Map<String, ChannelHandler>> channelHandlerMapProvider) {

		return new ChannelInitializer<>() {
			@Override
			protected void initChannel(@Nonnull SocketChannel socketChannel) {
				ChannelPipeline pipeline = socketChannel.pipeline();
				channelHandlerMapProvider.get().forEach(pipeline::addLast);
			}
		};
	}

	@Provides
	Map<String, ChannelHandler> provideChannelHandlerMap(PacketPool packetPool) {
		Map<String, ChannelHandler> map = new LinkedHashMap<>();

		map.put("packet_splitter", new ProtocolSplitter());
		map.put("logger", new LoggingHandler(LogLevel.DEBUG));
		map.put("packet_decoder", new ProtocolDecoder(true, packetPool));
		map.put("packet_encoder", new ProtocolEncoder());
		map.put("packet_handler", new PacketInboundHandler(packetPool));

		return map;
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
}
