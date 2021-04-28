package mc.protocol.packets.server;

import lombok.Data;
import mc.protocol.io.NetByteBuf;
import mc.protocol.model.ServerInfo;
import mc.protocol.packets.ServerSidePacket;
import mc.protocol.serializer.ServerInfoSerializer;

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
 * </pre>
 *
 * <p>Пример JSON Response</p>
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
 *
 * <p><code>`$.favicon`</code> должен быть формата PNG и размеры 64x64 px</p>
 */
@Data
public class StatusServerResponse implements ServerSidePacket {

	/**
	 * Информация о серере.
	 */
	private ServerInfo info;

	@Override
	public void writeSelf(NetByteBuf netByteBuf) {
		netByteBuf.writeString(ServerInfoSerializer.toJsonObject(info).toString());
	}
}
