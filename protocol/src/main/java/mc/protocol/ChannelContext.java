package mc.protocol;

import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mc.protocol.packets.ClientSidePacket;

@RequiredArgsConstructor
public class ChannelContext<P extends ClientSidePacket> {

	@Getter
	private final ChannelHandlerContext ctx;

	@Getter
	private final P packet;

	public void setState(State state) {
		ctx.channel().attr(NetworkAttributes.STATE).set(state);
	}
}
