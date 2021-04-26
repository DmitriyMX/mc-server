package mc.protocol;

import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mc.protocol.packets.Packet;

@RequiredArgsConstructor
public class ChannelContext<P extends Packet> {

	@Getter
	private final ChannelHandlerContext ctx;

	@Getter
	private final P packet;

	public void setState(State state) {
		ctx.channel().attr(NetworkAttributes.STATE).set(state);
	}
}
