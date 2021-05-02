package mc.protocol;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;
import mc.protocol.packets.Packet;
import reactor.core.publisher.Sinks;

import java.util.Map;

@SuppressWarnings("rawtypes")
@RequiredArgsConstructor
public class PacketInboundHandler extends SimpleChannelInboundHandler<Packet> {

	private final Map<Class<? extends Packet>, Sinks.Many<ChannelContext>> observedMap;

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Packet packet) {
		if (observedMap.containsKey(packet.getClass())) {
			observedMap.get(packet.getClass()).tryEmitNext(new ChannelContext<>(ctx, packet));
		}
	}
}
