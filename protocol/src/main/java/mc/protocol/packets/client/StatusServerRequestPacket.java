package mc.protocol.packets.client;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import mc.protocol.packets.EmptyPacket;

/**
 * Status server packet, request.
 *
 * <p>Клиент запрашивает получение информации о сервере</p>
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class StatusServerRequestPacket extends EmptyPacket {

}
