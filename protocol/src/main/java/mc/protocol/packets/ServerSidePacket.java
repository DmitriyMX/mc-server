package mc.protocol.packets;

import mc.protocol.io.NetByteBuf;

/**
 * Пакеты отправляемые сервером.
 */
public interface ServerSidePacket extends Packet {

	void writeSelf(NetByteBuf netByteBuf);
}
