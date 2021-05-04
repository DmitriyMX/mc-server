package mc.protocol.io.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mc.protocol.NetworkAttributes;
import mc.protocol.State;
import mc.protocol.io.NetByteBuf;
import mc.protocol.packets.ClientSidePacket;
import mc.protocol.packets.UnknownPacket;
import mc.protocol.utils.PacketPool;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
public class ProtocolDecoder extends ByteToMessageDecoder {

	private final boolean readUnknownPackets;
	private final PacketPool poolPackets;

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ctx.channel().attr(NetworkAttributes.STATE).set(State.HANDSHAKING);
		super.channelActive(ctx);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		ctx.channel().attr(NetworkAttributes.STATE).set(null);
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
				UnknownPacket unknownPacket = poolPackets.borrowObject(UnknownPacket.class);
				unknownPacket.setState(state);
				unknownPacket.setId(packetId);
				unknownPacket.setDataSize(netByteBuf.readableBytes());
				unknownPacket.readSelf(netByteBuf);
				out.add(unknownPacket);
			} else {
				netByteBuf.skipBytes(netByteBuf.readableBytes());
			}
		} else {
			ClientSidePacket packet = poolPackets.borrowObject(packetClass);
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
