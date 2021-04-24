package mc.server.network.netty;

import io.netty.bootstrap.ServerBootstrap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mc.server.network.Server;

@Slf4j
@RequiredArgsConstructor
public class NettyServer implements Server {

	private final ServerBootstrap serverBootstrap;

	@Override
	public void start(String host, int port) {
		log.info("Network starting: {}:{}", host, port);

		try {
			serverBootstrap.bind(host, port)
					.sync().channel().closeFuture().sync();
		} catch (InterruptedException e) {
			if (log.isTraceEnabled()) {
				log.trace("{}: {}", e.getClass().getSimpleName(), e.getMessage(), e);
			}
		}
	}
}
