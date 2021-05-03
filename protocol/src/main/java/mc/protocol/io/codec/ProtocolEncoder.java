package mc.protocol.io.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import mc.protocol.NetworkAttributes;
import mc.protocol.State;
import mc.protocol.io.NetByteBuf;
import mc.protocol.packets.ServerSidePacket;

@Slf4j
public class ProtocolEncoder extends MessageToByteEncoder<ServerSidePacket> {

	@Override
	protected void encode(ChannelHandlerContext ctx, ServerSidePacket packet, ByteBuf out) {
		State state = ctx.channel().attr(NetworkAttributes.STATE).get();
		Integer packetId = state.getServerSidePacketId(packet.getClass());
		if (packetId == null) {
			log.error("Unknown send packet: State {} ; Class {}", state, packet.getClass());
			return;
		}

		log.debug("OUT: {}:{}", state, packet);

		NetByteBuf buffer = new NetByteBuf(Unpooled.buffer());
		buffer.writeVarInt(packetId);
		packet.writeSelf(buffer);

		NetByteBuf netByteBuf = new NetByteBuf(out);
		netByteBuf.writeVarInt(buffer.readableBytes());
		netByteBuf.writeBytes(buffer);
	}
}
