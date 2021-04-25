package mc.server.network.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import mc.protocol.packets.Packet;

public abstract class AbstractPacketHandler<P extends Packet> extends SimpleChannelInboundHandler<Packet> {

	@SuppressWarnings("unchecked")
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Packet msg) throws Exception {
		channelRead1(ctx, (P) msg);
	}

	@SuppressWarnings("java:S112")
	protected abstract void channelRead1(ChannelHandlerContext ctx, P packet) throws Exception;
}
