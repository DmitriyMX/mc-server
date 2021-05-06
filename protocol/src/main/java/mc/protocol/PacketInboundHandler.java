package mc.protocol;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;
import mc.protocol.api.ConnectionContext;
import mc.protocol.packets.ClientSidePacket;
import reactor.core.publisher.Sinks;

@RequiredArgsConstructor
public class PacketInboundHandler extends SimpleChannelInboundHandler<ClientSidePacket> {

	@SuppressWarnings("rawtypes")
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ClientSidePacket packet) {
		Sinks.Many<ConnectionContext> packetSinks = ctx.channel().attr(NetworkAttributes.STATE)
				.get().getPacketSinks(packet.getClass());

		if (packetSinks != null) {
			packetSinks.tryEmitNext(new NettyConnectionContext<>(ctx, packet));
		}
	}
}
