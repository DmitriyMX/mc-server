package mc.protocol.packets;

import mc.protocol.io.NetByteBuf;

public abstract class EmptyPacket implements Packet {

	@Override
	public void readSelf(NetByteBuf netByteBuf) {
		// empty
	}

	@Override
	public void writeSelf(NetByteBuf netByteBuf) {
		// empty
	}
}
