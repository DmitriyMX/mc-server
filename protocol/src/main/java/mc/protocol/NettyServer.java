package mc.protocol;

import io.netty.bootstrap.ServerBootstrap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mc.protocol.di.DaggerProtocolComponent;
import mc.protocol.di.ProtocolComponent;
import mc.protocol.packets.Packet;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Map;

@SuppressWarnings("rawtypes")
@Slf4j
@RequiredArgsConstructor
public class NettyServer {

	private final ServerBootstrap serverBootstrap;
	private final Map<Class<? extends Packet>, Sinks.Many<ChannelContext>> observedMap;

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

	@SuppressWarnings("unchecked")
	public <P extends Packet> Flux<ChannelContext<P>> packetFlux(Class<P> packetClass) {
		return observedMap.get(packetClass).asFlux().map(ChannelContext.class::cast);
	}

	public static NettyServer createServer() {
		ProtocolComponent component = DaggerProtocolComponent.create();
		return component.getNettyServer();
	}
}
