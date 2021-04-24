package mc.server.di;

import dagger.Module;
import dagger.Provides;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import mc.server.network.Server;
import mc.server.network.netty.NettyServer;

import javax.inject.Named;
import java.util.Collections;
import java.util.List;

@Module
public class NetworkModule {

	@Provides
	Server provideServer(ServerBootstrap serverBootstrap) {
		return new NettyServer(serverBootstrap);
	}

	@Provides
	ServerBootstrap provideServerBootstrap(
			@Named("boss-group") EventLoopGroup bossGroup,
			@Named("worker-group") EventLoopGroup workerGroup,
			ChannelInitializer<SocketChannel> channelChannelInitializer
	) {
		ServerBootstrap bootstrap = new ServerBootstrap();

		bootstrap.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(channelChannelInitializer);

		return bootstrap;
	}

	@Provides
	@Named("boss-group")
	EventLoopGroup provideBossGroup() {
		return new NioEventLoopGroup(1);
	}

	@Provides
	@Named("worker-group")
	EventLoopGroup provideWorkerGroup() {
		return new NioEventLoopGroup();
	}

	@Provides
	ChannelInitializer<SocketChannel> provideChannelChannelInitializer(List<ChannelHandler> channelHandlerList) {
		return new ChannelInitializer<>() {
			@Override
			protected void initChannel(SocketChannel socketChannel) {
				final ChannelPipeline pipeline = socketChannel.pipeline();
				channelHandlerList.forEach(pipeline::addLast);
			}
		};
	}

	@Provides
	List<ChannelHandler> provideChannelHandlerList() {
		return Collections.singletonList(new LoggingHandler());
	}
}
