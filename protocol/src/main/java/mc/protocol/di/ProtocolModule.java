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
import mc.protocol.io.codec.ProtocolDecoder;
import mc.protocol.io.codec.ProtocolEncoder;
import mc.protocol.io.codec.ProtocolSplitter;
import mc.protocol.utils.EventBus;
import mc.protocol.utils.SimpleEventBus;

import javax.annotation.Nonnull;
import javax.inject.Provider;
import java.util.LinkedHashMap;
import java.util.Map;

@Module
public class ProtocolModule {

	@Provides
	NettyServer provideServer(ServerBootstrap serverBootstrap, EventBus eventBus) {
		return new NettyServer(serverBootstrap, eventBus);
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
	Map<String, ChannelHandler> provideChannelHandlerMap(EventBus eventBus) {
		Map<String, ChannelHandler> map = new LinkedHashMap<>();

		map.put("packet_splitter", new ProtocolSplitter());
		map.put("logger", new LoggingHandler(LogLevel.DEBUG));
		map.put("packet_decoder", new ProtocolDecoder(true));
		map.put("packet_encoder", new ProtocolEncoder());
		map.put("packet_handler", new PacketInboundHandler(eventBus));

		return map;
	}

	@Provides
	@ServerScope
	EventBus provideEventBus() {
		return new SimpleEventBus();
	}
}
