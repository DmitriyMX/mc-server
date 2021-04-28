package mc.protocol.di;

import com.google.common.collect.ImmutableMap;
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
import mc.protocol.ChannelContext;
import mc.protocol.NettyServer;
import mc.protocol.PacketInboundHandler;
import mc.protocol.State;
import mc.protocol.io.codec.ProtocolDecoder;
import mc.protocol.io.codec.ProtocolEncoder;
import mc.protocol.io.codec.ProtocolSplitter;
import mc.protocol.packets.Packet;
import reactor.core.publisher.Sinks;

import javax.inject.Provider;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

@Module
public class ProtocolModule {

	@SuppressWarnings("rawtypes")
	@Provides
	NettyServer provideServer(ServerBootstrap serverBootstrap,
							  Map<Class<? extends Packet>, Sinks.Many<ChannelContext>> observedMap) {
		return new NettyServer(serverBootstrap, observedMap);
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
			protected void initChannel(SocketChannel socketChannel) {
				ChannelPipeline pipeline = socketChannel.pipeline();
				channelHandlerMapProvider.get().forEach(pipeline::addLast);
			}
		};
	}

	@SuppressWarnings("rawtypes")
	@Provides
	Map<String, ChannelHandler> provideChannelHandlerMap(
			Map<Class<? extends Packet>, Sinks.Many<ChannelContext>> observedMap) {

		Map<String, ChannelHandler> map = new LinkedHashMap<>();

		map.put("packet_splitter", new ProtocolSplitter());
		map.put("logger", new LoggingHandler(LogLevel.DEBUG));
		map.put("packet_decoder", new ProtocolDecoder(true));
		map.put("packet_encoder", new ProtocolEncoder());
		map.put("packet_handler", new PacketInboundHandler(observedMap));

		return map;
	}

	@SuppressWarnings("rawtypes")
	@Provides
	@ServerScope
	Map<Class<? extends Packet>, Sinks.Many<ChannelContext>> provideObservedMap() {
		ImmutableMap.Builder<Class<? extends Packet>, Sinks.Many<ChannelContext>> builder = ImmutableMap.builder();

		Stream.of(State.values())
				.flatMap(state -> state.getClientSidePackets().values().stream())
				.forEach(packetClass -> builder.put(packetClass, Sinks.many().multicast().directBestEffort()));

		return builder.build();
	}
}
