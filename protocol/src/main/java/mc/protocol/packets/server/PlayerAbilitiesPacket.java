package mc.protocol.packets.server;

import lombok.Data;
import mc.protocol.io.NetByteBuf;
import mc.protocol.packets.ServerSidePacket;

/**
 * Характеристики игрока.
 *
 * <p>Структура пакета</p>
 * <pre>
 * | FIELD                        | TYPE     | NOTES                                   |
 * |------------------------------|----------|-----------------------------------------|
 * | Flags                        | Byte     | Битовая маска флагов. См. ниже значения |
 * | Flying Speed                 | Float    | Скорость полёта                         |
 * | Field of View (FOV) Modifier | Float    | Поле зрения                             |
 * </pre>
 *
 * <p>Флаги "Flags"</p>
 * <pre>
 * Bit 0x01 - Неуязвимость (Invulnerable)
 * Bit 0x02 - В полёте (Flying)
 * Bit 0x04 - Может летать (Allow Flying)
 * Bit 0x08 - Creative Mode (Instant Break)
 * </pre>
 *
 * @see <a href="https://wiki.vg/index.php?title=Protocol&oldid=14204#Player_Abilities_.28clientbound.29">Player Abilities</a>
 */
@Data
public class PlayerAbilitiesPacket implements ServerSidePacket {

	@SuppressWarnings("java:S116")
	private byte $flags = 0;
	private float flyingSpeed;
	private float fieldOfView;

	@Override
	public void writeSelf(NetByteBuf netByteBuf) {
		netByteBuf.writeByte(this.$flags);
		netByteBuf.writeFloat(this.flyingSpeed);
		netByteBuf.writeFloat(this.fieldOfView);
	}

	//FIXME использование value значений
	public void setInvulnerable(boolean value) {
		this.$flags = (byte) (this.$flags | 0x01);
	}

	public void setFlying(boolean value) {
		this.$flags = (byte) (this.$flags | 0x02);
	}

	public void setCatFly(boolean value) {
		this.$flags = (byte) (this.$flags | 0x04);
	}

	public void setCreativeMode(boolean value) {
		this.$flags = (byte) (this.$flags | 0x08);
	}
}
