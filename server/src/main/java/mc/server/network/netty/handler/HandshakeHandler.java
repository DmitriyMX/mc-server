package mc.server.network.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mc.protocol.NetworkAttributes;
import mc.protocol.State;
import mc.protocol.packets.client.HandshakePacket;

import javax.inject.Provider;

@Slf4j
@RequiredArgsConstructor
public class HandshakeHandler extends AbstractPacketHandler<HandshakePacket> {

	private final Provider<StatusHandler> statusHandlerProvider;
	private final Provider<LoginHandler> loginHandlerProvider;
	private final Provider<KeepAliveHandler> keepAliveHandlerProvider;

	@Override
	protected void channelRead1(ChannelHandlerContext ctx, HandshakePacket packet) {
		log.info("{}", packet);

		ctx.channel().attr(NetworkAttributes.STATE).set(packet.getNextState());

		if (State.STATUS == packet.getNextState()) {
			ctx.pipeline().replace("handshake_handler", "status_handler", statusHandlerProvider.get());
			ctx.pipeline().addAfter("status_handler", "keepalive_handler", keepAliveHandlerProvider.get());
		} else if (State.LOGIN == packet.getNextState()) {
			ctx.channel().pipeline().replace("handshake_handler", "login_handler", loginHandlerProvider.get());
		}
	}
}
