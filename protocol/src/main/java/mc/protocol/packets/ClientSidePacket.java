package mc.protocol.packets;

import mc.protocol.io.NetByteBuf;

/**
 * Пакеты отправляемые клиентом.
 */
public interface ClientSidePacket extends Packet {

	void readSelf(NetByteBuf netByteBuf);
}
