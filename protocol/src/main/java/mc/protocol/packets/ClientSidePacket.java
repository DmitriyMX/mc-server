package mc.protocol.packets;

import mc.protocol.io.NetByteBuf;
import mc.protocol.pool.Passivable;

/**
 * Пакеты отправляемые клиентом.
 */
public interface ClientSidePacket extends Packet, Passivable {

	void readSelf(NetByteBuf netByteBuf);
}
