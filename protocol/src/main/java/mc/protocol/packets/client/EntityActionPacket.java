package mc.protocol.packets.client;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import mc.protocol.io.NetByteBuf;
import mc.protocol.packets.ClientSidePacket;
import mc.protocol.utils.EntityActionAction;

/**
 * Entity Action packet.
 *
 * <p>Структура пакета</p>
 * <pre>
 * | FIELD      | TYPE   | NOTES                                     |
 * |------------|--------|-------------------------------------------|
 * | Entity ID  | VarInt | ID игрока                                 |
 * | Action ID  | VarInt | ID действия                               |
 * | Jump Boost | VarInt | Используется только при "Action ID" = 5.  |
 * |            |        | В этом случае значение будет от 0 до 100. |
 * |            |        | В остальных случаях значение 0.           |
 * </pre>
 *
 * @see <a href="https://wiki.vg/index.php?title=Protocol&oldid=14204#Entity_Action" target="_top">Entity Action</a>
 */
@NoArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class EntityActionPacket implements ClientSidePacket {

	private Integer entityId;
	private EntityActionAction action;
	private Integer jumpBoost;

	@Override
	public void readSelf(NetByteBuf netByteBuf) {
		this.entityId = netByteBuf.readVarInt();
		int actionId = netByteBuf.readVarInt();
		this.jumpBoost = netByteBuf.readVarInt();

		this.action = EntityActionAction.valueOfCode(actionId);
	}

	@Override
	public void passivate() {
		this.entityId = null;
		this.action = null;
		this.jumpBoost = null;
	}
}
