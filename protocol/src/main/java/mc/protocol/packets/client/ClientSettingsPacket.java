package mc.protocol.packets.client;

import lombok.*;
import mc.protocol.ChatMode;
import mc.protocol.MainHand;
import mc.protocol.io.NetByteBuf;
import mc.protocol.packets.ClientSidePacket;

/**
 * Client settings packet.
 *
 * <p>Структура пакета</p>
 * <pre>
 * | FIELD                | TYPE          | NOTES                                             |
 * |--------------------  |---------------|---------------------------------------------------|
 * | Locale               | String (16)   | например en_gb                                    |
 * | View Distance        | Byte          | Дистанция отрисовки со стороны Клиента, в чанках. |
 * | Chat Mode            | VarInt        | 0: enabled                                        |
 * |                      |               | 1: commands only                                  |
 * |                      |               | 2: hidden                                         |
 * |                      |               | [1]                                               |
 * | Chat Colors          | Boolean       | “Colors” multiplayer setting (???)                |
 * | Displayed Skin Parts | Unsigned Byte | битовая маска отображения скина. См. ниже         |
 * | Main Hand            | VarInt        | 0: Left                                           |
 * |                      |               | 1: Right                                          |
 *
 * [1] - <a href="https://wiki.vg/index.php?title=Chat&oldid=13165#Processing_chat">Processing chat</a>
 * </pre>
 *
 * <p>Биты "Displayed Skin Parts"</p>
 * <pre>
 * Bit 0 (0x01): Плащ (Cape)
 * Bit 1 (0x02): Рубашка (Jacket)
 * Bit 2 (0x04): Левый рукав (Left Sleeve)
 * Bit 3 (0x08): Правый рукав (Right Sleeve)
 * Bit 4 (0x10): Левая штанина (Left Pants Leg)
 * Bit 5 (0x20): Правая штанина (Right Pants Leg)
 * Bit 6 (0x40): Шлем (Hat)
 * </pre>
 *
 * @see <a href="https://wiki.vg/index.php?title=Protocol&oldid=14204#Client_Settings">Client Settings</a>
 */
@NoArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class ClientSettingsPacket implements ClientSidePacket {

	private String locale;
	private int viewDistance;
	private ChatMode chatMode;
	private boolean chatColors;
	@SuppressWarnings("java:S116")
	private int $displayedSkinPartsBitMask;
	private MainHand mainHand;

	@Override
	public void readSelf(NetByteBuf netByteBuf) {
		this.locale = netByteBuf.readString(16);
		this.viewDistance = netByteBuf.readByte();
		this.chatMode = ChatMode.valueById(netByteBuf.readVarInt());
		this.chatColors = netByteBuf.readBoolean();
		this.$displayedSkinPartsBitMask = netByteBuf.readUnsignedByte();
		this.mainHand = MainHand.valueById(netByteBuf.readVarInt());
	}

	public boolean isCapeEnabled() {
		return ($displayedSkinPartsBitMask & 0x01) > 0;
	}

	public boolean isJacketEnabled() {
		return ($displayedSkinPartsBitMask & 0x02) > 0;
	}

	public boolean isLeftSleeveEnabled() {
		return ($displayedSkinPartsBitMask & 0x04) > 0;
	}

	public boolean isRightSleeveEnabled() {
		return ($displayedSkinPartsBitMask & 0x08) > 0;
	}

	public boolean isLeftPantsEnabled() {
		return ($displayedSkinPartsBitMask & 0x10) > 0;
	}

	public boolean isRightPantsEnabled() {
		return ($displayedSkinPartsBitMask & 0x20) > 0;
	}

	public boolean isHatEnabled() {
		return ($displayedSkinPartsBitMask & 0x40) > 0;
	}
}
