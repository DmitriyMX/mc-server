package mc.protocol;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;
import mc.protocol.packets.ClientSidePacket;
import mc.protocol.event.EventBus;
import mc.protocol.pool.PacketPool;
import org.apache.commons.pool2.ObjectPool;

@RequiredArgsConstructor
public class PacketInboundHandler extends SimpleChannelInboundHandler<ClientSidePacket> {

	private final ObjectPool<NettyConnectionContext> poolNettyConnectionContext;
	private final PacketPool poolPackets;
	private final EventBus eventBus;

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ClientSidePacket packet) throws Exception {
		State state = ctx.channel().attr(NetworkAttributes.STATE).get();

		NettyConnectionContext context = poolNettyConnectionContext.borrowObject().setCtx(ctx);
		eventBus.emit(state, context, packet);

		poolNettyConnectionContext.returnObject(context);
		poolPackets.returnObject(packet);
	}
}
