package mc.server.di;

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
import lombok.extern.slf4j.Slf4j;
import mc.protocol.io.codec.ProtocolDecoder;
import mc.protocol.io.codec.ProtocolEncoder;
import mc.protocol.io.codec.ProtocolSplitter;
import mc.server.network.Server;
import mc.server.network.netty.handler.HandshakeHandler;
import mc.server.network.netty.NettyServer;
import mc.server.network.netty.handler.KeepAliveHandler;
import mc.server.network.netty.handler.LoginHandler;
import mc.server.network.netty.handler.StatusHandler;

import javax.inject.Provider;
import java.util.LinkedHashMap;
import java.util.Map;

@Module
@Slf4j
public class NetworkModule {

	@Provides
	Server provideServer(ServerBootstrap serverBootstrap) {
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
	ChannelInitializer<SocketChannel> provideChannelChannelInitializer(Provider<Map<String, ChannelHandler>> channelHandlerMapProvider) {
		return new ChannelInitializer<>() {
			@Override
			protected void initChannel(SocketChannel socketChannel) {
				ChannelPipeline pipeline = socketChannel.pipeline();
				channelHandlerMapProvider.get().forEach(pipeline::addLast);
			}
		};
	}

	@Provides
	Map<String, ChannelHandler> provideChannelHandlerMap(
			Provider<StatusHandler> statusHandlerProvider,
			Provider<LoginHandler> loginHandlerProvider,
			Provider<KeepAliveHandler> keepAliveHandlerProvider
	) {
		Map<String, ChannelHandler> map = new LinkedHashMap<>();

		map.put("logger", new LoggingHandler(LogLevel.DEBUG));
		map.put("protocol_splitter", new ProtocolSplitter());
		map.put("protocol_decoder", new ProtocolDecoder(true));
		map.put("protocol_encoder", new ProtocolEncoder());
		map.put("handshake_handler", new HandshakeHandler(
				statusHandlerProvider, loginHandlerProvider, keepAliveHandlerProvider));

		return map;
	}

	@Provides
	StatusHandler provideStatusHandler() {
		return new StatusHandler();
	}

	@Provides
	LoginHandler provideLoginHandler() {
		return new LoginHandler();
	}

	@Provides
	KeepAliveHandler provideKeepAliveHandler() {
		return new KeepAliveHandler();
	}
}
