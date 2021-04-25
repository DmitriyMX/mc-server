package mc.server.network.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import mc.protocol.packets.client.LoginStartPacket;
import mc.protocol.packets.server.DisconnectPacket;

public class LoginHandler extends AbstractPacketHandler<LoginStartPacket> {

	@Override
	protected void channelRead1(ChannelHandlerContext ctx, LoginStartPacket packet) {
		DisconnectPacket disconnectPacket = new DisconnectPacket();
		disconnectPacket.setReason("{\n" +
				"  \"text\": \"Server is not available.\"\n" +
				"}");

		ctx.channel().writeAndFlush(disconnectPacket).channel().disconnect();
	}
}
