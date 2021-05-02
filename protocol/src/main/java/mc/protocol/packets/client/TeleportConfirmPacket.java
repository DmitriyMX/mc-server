package mc.protocol.packets.client;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import mc.protocol.io.NetByteBuf;
import mc.protocol.packets.ClientSidePacket;
import mc.protocol.packets.server.PlayerPositionAndLookPacket;

/**
 * Teleport сonfirm packet.
 *
 * <p>Структура пакета</p>
 * <pre>
 * | FIELD       | TYPE   | NOTES                                                     |
 * |-------------|--------|-----------------------------------------------------------|
 * | Teleport ID | VarInt | ID, который был выдан пакетом {@link PlayerPositionAndLookPacket} |
 * </pre>
 *
 * @see <a href="https://wiki.vg/index.php?title=Protocol&oldid=7368#Login_Start" target="_top">Login start</a>
 * @see PlayerPositionAndLookPacket
 */
@NoArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class TeleportConfirmPacket implements ClientSidePacket {

	private int teleportId;

	@Override
	public void readSelf(NetByteBuf netByteBuf) {
		this.teleportId = netByteBuf.readVarInt();
	}
}
