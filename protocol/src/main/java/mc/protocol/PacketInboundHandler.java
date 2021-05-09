package mc.protocol;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mc.protocol.packets.ClientSidePacket;
import mc.protocol.event.EventBus;
import mc.protocol.pool.PacketPool;
import org.apache.commons.pool2.ObjectPool;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class PacketInboundHandler extends SimpleChannelInboundHandler<ClientSidePacket> {

	private static final String CLIENT_FORCE_DISCONNECTED_IOEXCEPTION_MESSAGE_RU = "Программа на вашем хост-компьютере разорвала установленное подключение";

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

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		if (cause instanceof IOException && cause.getLocalizedMessage().equalsIgnoreCase(CLIENT_FORCE_DISCONNECTED_IOEXCEPTION_MESSAGE_RU)) {
			log.warn("Client '{}' force disconnected", ctx.channel().remoteAddress());
			if (log.isTraceEnabled()) {
				log.trace("", cause);
			}
		} else {
			log.error("{}", cause.getMessage(), cause);
		}
	}
}
