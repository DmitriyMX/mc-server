package mc.protocol;

import io.netty.channel.ChannelHandlerContext;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import mc.protocol.api.ConnectionContext;
import mc.protocol.packets.ServerSidePacket;
import mc.protocol.pool.Passivable;

import java.util.Map;
import java.util.Optional;

@EqualsAndHashCode
public class NettyConnectionContext implements ConnectionContext, Passivable {

	@Accessors(chain = true)
	@Setter
	private ChannelHandlerContext ctx;

	/**
	 * @deprecated костыль
	 */
	@Deprecated
	@Getter
	@Setter
	private boolean usedContext;

	@Override
	public State getState() {
		return ctx.channel().attr(NetworkAttributes.STATE).get();
	}

	@Override
	public void setState(State state) {
		ctx.channel().attr(NetworkAttributes.STATE).set(state);
	}

	/**
	 * @deprecated костыль
	 */
	@Deprecated
	@Override
	public <T> void setCustomProperty(String key, T value) {
		Map<String, Object> map = ctx.channel().attr(NetworkAttributes.CUSTOM_PROPERTIES).get();
		map.put(key, value);
	}

	/**
	 * @deprecated костыль
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	@Override
	public <T> Optional<T> getCustomProperty(String key, Class<T> classResult) {
		Map<String, Object> map = ctx.channel().attr(NetworkAttributes.CUSTOM_PROPERTIES).get();
		return (Optional<T>) Optional.ofNullable(map.getOrDefault(key, null));
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
