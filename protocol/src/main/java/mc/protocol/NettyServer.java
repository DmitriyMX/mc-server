package mc.protocol;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mc.protocol.api.ConnectionContext;
import mc.protocol.api.Server;
import mc.protocol.io.codec.ProtocolDecoder;
import mc.protocol.io.codec.ProtocolEncoder;
import mc.protocol.io.codec.ProtocolSplitter;
import mc.protocol.utils.PacketPool;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
public class NettyServer implements Server {

	private final PacketPool packetPool;
	private Consumer<ConnectionContext<?>> consumerNewConnection;
	private Consumer<ConnectionContext<?>> consumerDisconnect;

	@Override
	public void bind(String host, int port) {
		log.info("Network starting: {}:{}", host, port);

		try {
			createServerBootstrap().bind(host, port).sync().channel().closeFuture().sync();
		} catch (InterruptedException e) {
			if (log.isTraceEnabled()) {
				log.trace("{}: {}", e.getClass().getSimpleName(), e.getMessage(), e);
			}
		}
	}

	@Override
	public void onNewConnect(Consumer<ConnectionContext<?>> consumer) {
		this.consumerNewConnection = consumer;
	}

	@Override
	public void onDisonnect(Consumer<ConnectionContext<?>> consumer) {
		this.consumerDisconnect = consumer;
	}

	private ServerBootstrap createServerBootstrap() {
		ServerBootstrap bootstrap = new ServerBootstrap();

		bootstrap.group(new NioEventLoopGroup(1), new NioEventLoopGroup())
				.channel(NioServerSocketChannel.class)
				.childHandler(createChannelChannelInitializer());

		return bootstrap;
	}

	private ChannelInitializer<SocketChannel> createChannelChannelInitializer() {
		return new ChannelInitializer<>() {
			@Override
			protected void initChannel(@Nonnull SocketChannel socketChannel) {
				ChannelPipeline pipeline = socketChannel.pipeline();
				createChannelHandlerMap().forEach(pipeline::addLast);
			}
		};
	}

	private Map<String, ChannelHandler> createChannelHandlerMap() {
		Map<String, ChannelHandler> map = new LinkedHashMap<>();

		map.put("packet_splitter", new ProtocolSplitter());
		map.put("logger", new LoggingHandler(LogLevel.DEBUG));
		map.put("packet_decoder", new ProtocolDecoder(true, packetPool, consumerNewConnection, consumerDisconnect));
		map.put("packet_encoder", new ProtocolEncoder());
		map.put("packet_handler", new PacketInboundHandler(packetPool));

		return map;
	}
}
