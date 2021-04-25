package mc.server.network.netty;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mc.protocol.NetworkAttributes;
import mc.protocol.packets.client.HandshakePacket;

import javax.inject.Provider;

@Slf4j
@RequiredArgsConstructor
public class HandshakeHandler extends AbstractPacketHandler<HandshakePacket> {

	private final Provider<StatusHandler> statusHandlerProvider;

	@Override
	protected void channelRead1(ChannelHandlerContext ctx, HandshakePacket packet) {
		log.info("{}", packet);

		ctx.channel().attr(NetworkAttributes.STATE).set(packet.getNextState());
		ctx.pipeline().replace("handshake_handler", "status_handler", statusHandlerProvider.get());
	}
}
