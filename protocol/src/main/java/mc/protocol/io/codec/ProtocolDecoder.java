package mc.protocol.io.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mc.protocol.NettyConnectionContext;
import mc.protocol.NetworkAttributes;
import mc.protocol.State;
import mc.protocol.api.ConnectionContext;
import mc.protocol.io.NetByteBuf;
import mc.protocol.packets.ClientSidePacket;
import mc.protocol.packets.UnknownPacket;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
public class ProtocolDecoder extends ByteToMessageDecoder {

	private final boolean readUnknownPackets;
	private final Consumer<ConnectionContext<?>> consumerNewConnection;
	private final Consumer<ConnectionContext<?>> consumerDisconnect;

	@Override
	public void channelActive(@Nonnull ChannelHandlerContext ctx) throws Exception {
		consumerNewConnection.accept(new NettyConnectionContext<>(ctx, null));
		super.channelActive(ctx);
	}

	@Override
	public void channelInactive(@Nonnull ChannelHandlerContext ctx) throws Exception {
		consumerDisconnect.accept(new NettyConnectionContext<>(ctx, null));
		super.channelInactive(ctx);
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		State state = Objects.requireNonNull(ctx.channel().attr(NetworkAttributes.STATE).get());
		NetByteBuf netByteBuf = new NetByteBuf(in);

		int packetId = netByteBuf.readVarInt();
		Class<? extends ClientSidePacket> packetClass = state.getClientSidePacketById(packetId);
		if (packetClass == null) {
			log.warn("Unknown packet: State {} ; Id 0x{}", state, packetIdAsHexcode(packetId));

			if (readUnknownPackets) {
				UnknownPacket unknownPacket = new UnknownPacket(state, packetId, netByteBuf.readableBytes());
				unknownPacket.readSelf(netByteBuf);
				out.add(unknownPacket);
			} else {
				netByteBuf.skipBytes(netByteBuf.readableBytes());
			}
		} else {
			ClientSidePacket packet = packetClass.getDeclaredConstructor().newInstance();
			packet.readSelf(netByteBuf);
			log.debug("IN: {}:{}", state, packet);
			out.add(packet);
		}
	}

	private String packetIdAsHexcode(int packetId) {
		String hexPacketId = Integer.toHexString(packetId).toUpperCase();
		if (hexPacketId.length() == 1) hexPacketId = "0" + hexPacketId;

		return hexPacketId;
	}
}
