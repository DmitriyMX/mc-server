package mc.protocol.io.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import mc.protocol.NetworkAttributes;
import mc.protocol.State;
import mc.protocol.io.NetByteBuf;
import mc.protocol.packets.Packet;
import mc.protocol.packets.PacketDirection;

import java.util.Objects;

public class ProtocolEncoder extends MessageToByteEncoder<Packet> {

	@Override
	protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out) {
		State state = ctx.channel().attr(NetworkAttributes.STATE).get();
		int packetId = Objects.requireNonNull(state.getIdByPacket(PacketDirection.CLIENT_BOUND, packet.getClass()));

		NetByteBuf buffer = new NetByteBuf(Unpooled.buffer());
		buffer.writeVarInt(packetId);
		packet.writeSelf(buffer);

		NetByteBuf netByteBuf = new NetByteBuf(out);
		netByteBuf.writeVarInt(buffer.readableBytes());
		netByteBuf.writeBytes(buffer);
	}
}
