package mc.protocol;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;
import mc.protocol.packets.ClientSidePacket;
import mc.protocol.utils.PacketPool;
import reactor.core.publisher.Sinks;

@RequiredArgsConstructor
public class PacketInboundHandler extends SimpleChannelInboundHandler<ClientSidePacket> {

	private final PacketPool poolPackets;

	@SuppressWarnings("rawtypes")
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ClientSidePacket packet) throws Exception {
		Sinks.Many<ChannelContext> packetSinks = ctx.channel().attr(NetworkAttributes.STATE)
				.get().getPacketSinks(packet.getClass());

		if (packetSinks != null) {
			packetSinks.tryEmitNext(new ChannelContext<>(ctx, packet));
		}

		poolPackets.returnObject(packet);
	}
}
