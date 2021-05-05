package mc.protocol;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;
import mc.protocol.packets.ClientSidePacket;
import mc.protocol.utils.EventBus;

@RequiredArgsConstructor
public class PacketInboundHandler extends SimpleChannelInboundHandler<ClientSidePacket> {

	private final EventBus eventBus;

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ClientSidePacket packet) {
		State state = ctx.channel().attr(NetworkAttributes.STATE).get();
		eventBus.emit(state, new ChannelContext<>(ctx, packet));
	}
}
