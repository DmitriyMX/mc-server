package mc.protocol.packets.server;

import lombok.Data;
import mc.protocol.io.NetByteBuf;
import mc.protocol.packets.Packet;

/**
 * Status server packet, response.
 *
 * <p>Информация о сервере</p>
 *
 * <p>Структура пакета
 * <pre>
 * | FIELD         | TYPE   | NOTES                                   |
 * |---------------|--------|-----------------------------------------|
 * | JSON Response | String | Информация о сервере в JSON формате [1] |
 *
 * [1] - <a href="https://wiki.vg/index.php?title=Server_List_Ping&oldid=7555#Response" target="_top">Server List Ping: Response</a>
 * </pre></p>
 */
@Data
public class StatusServerResponse implements Packet {

	/**
	 * Информация о серере в формате JSON
	 *
	 * <p>Пример</p>
	 * <pre>
	 * {
	 *     "version": {
	 *         "name": "1.8.7",
	 *         "protocol": 47
	 *     },
	 *     "players": {
	 *         "max": 100,
	 *         "online": 5,
	 *         "sample": [
	 *             {
	 *                 "name": "thinkofdeath",
	 *                 "id": "4566e69f-c907-48ee-8d71-d7ba5aa00d20"
	 *             }
	 *         ]
	 *     },
	 *     "description": {
	 *         "text": "Hello world"
	 *     },
	 *     "favicon": "data:image/png;base64,&lt;data&gt;"
	 * }
	 * </pre>
	 */
	private String info;

	@Override
	public void readSelf(NetByteBuf netByteBuf) {
		info = netByteBuf.readString();
	}

	@Override
	public void writeSelf(NetByteBuf netByteBuf) {
		netByteBuf.writeString(info);
	}
}
