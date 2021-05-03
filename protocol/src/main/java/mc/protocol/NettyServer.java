package mc.protocol;

import io.netty.bootstrap.ServerBootstrap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mc.protocol.di.DaggerProtocolComponent;
import mc.protocol.di.ProtocolComponent;

@Slf4j
@RequiredArgsConstructor
public class NettyServer {

	private final ServerBootstrap serverBootstrap;

	public void bind(String host, int port) {
		log.info("Network starting: {}:{}", host, port);

		try {
			serverBootstrap.bind(host, port).sync().channel().closeFuture().sync();
		} catch (InterruptedException e) {
			if (log.isTraceEnabled()) {
				log.trace("{}: {}", e.getClass().getSimpleName(), e.getMessage(), e);
			}
		}
	}

	public static NettyServer createServer() {
		ProtocolComponent component = DaggerProtocolComponent.create();
		return component.getNettyServer();
	}
}
