package mc.protocol.packets.client;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import mc.protocol.io.NetByteBuf;
import mc.protocol.packets.ClientSidePacket;

/**
 * Plugin Message packet.
 *
 * <p>Канал связи для модов и плагинов.</p>
 *
 * <p>Структура пакета</p>
 * <pre>
 * | FIELD        | TYPE        | NOTES            |
 * |--------------|-------------|------------------|
 * | Channel name | String (20) | Название канала  |
 * | Data         | Byte array  | Любые данные     |
 * </pre>
 *
 * @see <a href="https://wiki.vg/index.php?title=Protocol&oldid=14204#Plugin_Message_.28serverbound.29">Plugin Message (serverbound)</a>
 * @see <a href="https://wiki.vg/index.php?title=Plugin_channels&oldid=14089">Plugin channels</a>
 * @see <a href="https://dinnerbone.com/blog/2012/01/13/minecraft-plugin-channels-messaging/">Minecraft Plugin Channels + Messaging</a>
 */
@NoArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class PluginMessagePacket implements ClientSidePacket {

	private String channelName;
	private byte[] rawData;

	@Override
	public void readSelf(NetByteBuf netByteBuf) {
		this.channelName = netByteBuf.readString(20);
		this.rawData = new byte[netByteBuf.readableBytes()];
		netByteBuf.readBytes(this.rawData);
	}

	@Override
	public void passivate() {
		this.channelName = null;
		this.rawData = null;
	}
}
