package mc.protocol;

import io.netty.bootstrap.ServerBootstrap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mc.protocol.di.DaggerProtocolComponent;
import mc.protocol.di.ProtocolComponent;
import mc.protocol.packets.ClientSidePacket;
import mc.protocol.utils.EventBus;

@Slf4j
@RequiredArgsConstructor
public class NettyServer {

	private final ServerBootstrap serverBootstrap;
	private final EventBus eventBus;

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

	public <P extends ClientSidePacket> void listenPacket(State state, Class<P> packetClass, EventBus.EventHandler<P> eventHandler) {
		this.eventBus.subscribe(state, packetClass, eventHandler);
	}

	public static NettyServer createServer() {
		ProtocolComponent component = DaggerProtocolComponent.create();
		return component.getNettyServer();
	}
}
