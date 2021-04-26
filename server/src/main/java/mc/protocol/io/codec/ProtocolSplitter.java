package mc.protocol.io.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import mc.protocol.io.NetByteBuf;

import java.util.List;

public class ProtocolSplitter extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
		NetByteBuf netByteBuf = new NetByteBuf(in);
		netByteBuf.markReaderIndex();

		do {
			byte[] sizePacketRaw = new byte[3];
			for (int i = 0; i < 3; ++i) {
				sizePacketRaw[i] = netByteBuf.readByte();

				if (sizePacketRaw[i] >= 0) {
					break;
				}
			}

			int sizePacket = new NetByteBuf(Unpooled.wrappedBuffer(sizePacketRaw)).readVarInt();

			if (netByteBuf.readableBytes() >= sizePacket) {
				byte[] bytes = new byte[sizePacket];
				netByteBuf.readBytes(bytes);
				out.add(Unpooled.wrappedBuffer(bytes));
			} else {
				netByteBuf.resetReaderIndex();
				break;
			}
		} while (netByteBuf.readableBytes() > 0);
	}
}
