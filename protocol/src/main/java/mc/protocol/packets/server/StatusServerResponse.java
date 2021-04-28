package mc.protocol.packets.server;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.google.common.collect.Streams;
import lombok.Data;
import mc.protocol.io.NetByteBuf;
import mc.protocol.model.ServerInfo;
import mc.protocol.packets.ServerSidePacket;

import java.util.stream.Collector;

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
		JsonObject jsonObject = Json.object()
				.add("version", createVersionObj())
				.add("players", createPlayersObj())
				.add("description", Json.object().add("text", info.description()));

		if (info.favicon() != null && !info.favicon().isEmpty()) {
			jsonObject.add("favicon", info.favicon());
		}

		netByteBuf.writeString(jsonObject.toString());
	}

	private JsonObject createVersionObj() {
		return Json.object()
				.add("name", info.version().name())
				.add("protocol", info.version().protocol());
	}

	private JsonObject createPlayersObj() {
		JsonArray sampleArr = info.players().sample().stream()
				.map(samplePlayer -> Json.object()
						.add("name", samplePlayer.name())
						.add("id", samplePlayer.id()))
				.collect(Collector.of(Json::array, JsonArray::add, StatusServerResponse::jsonArrayAddAll));

		return Json.object()
				.add("max", info.players().max())
				.add("online", info.players().online())
				.add("sample", sampleArr);
	}

	private static JsonArray jsonArrayAddAll(JsonArray jsonArrayTo, JsonArray jsonArrayFrom) {
		Streams.stream(jsonArrayFrom).forEach(jsonArrayTo::add);
		return jsonArrayTo;
	}
}
