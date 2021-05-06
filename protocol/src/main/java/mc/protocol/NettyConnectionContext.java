package mc.protocol;

import io.netty.channel.ChannelHandlerContext;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.experimental.Accessors;
import mc.protocol.api.ConnectionContext;
import mc.protocol.packets.ServerSidePacket;
import mc.protocol.pool.Passivable;

@EqualsAndHashCode
public class NettyConnectionContext implements ConnectionContext, Passivable {

	@Accessors(chain = true)
	@Setter
	private ChannelHandlerContext ctx;

	@Override
	public State getState() {
		return ctx.channel().attr(NetworkAttributes.STATE).get();
	}

	@Override
	public void setState(State state) {
		ctx.channel().attr(NetworkAttributes.STATE).set(state);
	}

	@Override
	public void send(ServerSidePacket packet) {
		ctx.write(packet);
	}

	@Override
	public void sendNow(ServerSidePacket packet) {
		ctx.writeAndFlush(packet);
	}

	@Override
	public void flushSending() {
		ctx.flush();
	}

	@Override
	public void disconnect() {
		ctx.disconnect();
	}

	@Override
	public void passivate() {
		this.ctx = null;
	}
}
