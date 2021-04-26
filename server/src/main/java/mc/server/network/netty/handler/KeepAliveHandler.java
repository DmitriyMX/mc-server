package mc.server.network.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import mc.protocol.packets.PingPacket;

@Slf4j
public class KeepAliveHandler extends AbstractPacketHandler<PingPacket> {

	@Override
	protected void channelRead1(ChannelHandlerContext ctx, PingPacket packet) {
		log.info("{}", packet);

		ctx.writeAndFlush(packet).channel().disconnect();
	}
}
