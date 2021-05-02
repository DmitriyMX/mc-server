package mc.protocol.packets.server;

import lombok.Data;
import mc.protocol.io.NetByteBuf;
import mc.protocol.model.Location;
import mc.protocol.model.Look;
import mc.protocol.packets.ServerSidePacket;
import mc.protocol.packets.client.TeleportConfirmPacket;

/**
 * Установка позиции и угла осмотра Игрока.
 *
 * <p>Структура пакета</p>
 * <pre>
 * | FIELD       | TYPE   | NOTES                                                                             |
 * |-------------|--------|-----------------------------------------------------------------------------------|
 * | X           | Double | Абсолютная или относительная позиция по X. Зависит от "Flags"                     |
 * | Y           | Double | Абсолютная или относительная позиция по Y. Зависит от "Flags"                     |
 * | Z           | Double | Абсолютная или относительная позиция по Z. Зависит от "Flags"                     |
 * | Yaw         | Float  | Абсолютный или относительный поворот головы по OX, в градусах. Зависит от "Flags" |
 * | Pitch       | Float  | Абсолютный или относительный поворот головы по OY, в градусах. Зависит от "Flags" |
 * | Flags       | Byte   | Битовая маска значений флагов. См. значения ниже                                  |
 * | Teleport ID | VarInt | ID для подтверждения клиентом перемещения Игрока                                  |
 * </pre>
 *
 * <p>Значения "Flags"</p>
 * <pre>
 * | Field | Bit  |
 * |-------|------|
 * | X     | 0x01 |
 * | Y     | 0x02 |
 * | Z     | 0x04 |
 * | X_ROT | 0x08 |
 * | Y_ROT | 0x10 |
 * </pre>
 *
 * <p>Примечание от Dinnerbone про "Flags":</p>
 * <i>"It's a bitfield, X/Y/Z/Y_ROT/X_ROT. If X is set, the x value is relative and not absolute."</i>
 *
 * @see <a href="https://wiki.vg/index.php?title=Protocol&oldid=14204#Player_Position_And_Look_.28clientbound.29">Player Position And Look</a>
 * @see TeleportConfirmPacket
 */
@Data
public class PlayerPositionAndLookPacket implements ServerSidePacket {

	private Location position;
	private Look look;
	@SuppressWarnings("java:S116")
	private byte $flags = 0;
	private int teleportId;

	@Override
	public void writeSelf(NetByteBuf netByteBuf) {
		netByteBuf.writeDouble(this.position.getX());
		netByteBuf.writeDouble(this.position.getY());
		netByteBuf.writeDouble(this.position.getZ());
		netByteBuf.writeFloat(this.look.getYaw());
		netByteBuf.writeFloat(this.look.getPitch());
		netByteBuf.writeByte(this.$flags);
		netByteBuf.writeVarInt(teleportId);
	}

	//FIXME использовать value значения
	public void setFlagX(boolean value) {
		this.$flags = (byte) (this.$flags | 0x01);
	}

	public void setFlagY(boolean value) {
		this.$flags = (byte) (this.$flags | 0x02);
	}

	public void setFlagZ(boolean value) {
		this.$flags = (byte) (this.$flags | 0x04);
	}

	public void setFlagXRot(boolean value) {
		this.$flags = (byte) (this.$flags | 0x08);
	}

	public void setFlagYRot(boolean value) {
		this.$flags = (byte) (this.$flags | 0x10);
	}
}
