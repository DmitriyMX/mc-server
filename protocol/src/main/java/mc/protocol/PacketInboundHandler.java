package mc.protocol;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;
import mc.protocol.packets.ClientSidePacket;
import mc.protocol.utils.EventBus;
import mc.protocol.utils.PacketPool;

@RequiredArgsConstructor
public class PacketInboundHandler extends SimpleChannelInboundHandler<ClientSidePacket> {

	private final PacketPool poolPackets;
	private final EventBus eventBus;

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ClientSidePacket packet) throws Exception {
		State state = ctx.channel().attr(NetworkAttributes.STATE).get();
		eventBus.emit(state, new NettyConnectionContext<>(ctx, packet));

		poolPackets.returnObject(packet);
	}
}
