package mc.protocol.serializer;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.google.common.collect.Streams;
import lombok.experimental.UtilityClass;
import mc.protocol.model.ServerInfo;

import java.util.stream.Collector;

@UtilityClass
public class ServerInfoSerializer {

	public JsonObject toJsonObject(ServerInfo info) {
		JsonObject jsonObject = Json.object()
				.add("version", createVersionObj(info))
				.add("players", createPlayersObj(info))
				.add("description", TextSerializer.toJsonObject(info.description()));

		if (info.favicon() != null && !info.favicon().isEmpty()) {
			jsonObject.add("favicon", info.favicon());
		}

		return jsonObject;
	}

	private JsonObject createVersionObj(ServerInfo info) {
		return Json.object()
				.add("name", info.version().name())
				.add("protocol", info.version().protocol());
	}

	private JsonObject createPlayersObj(ServerInfo info) {
		JsonArray sampleArr = info.players().sample().stream()
				.map(samplePlayer -> Json.object()
						.add("name", samplePlayer.name())
						.add("id", samplePlayer.id()))
				.collect(Collector.of(Json::array, JsonArray::add, ServerInfoSerializer::jsonArrayAddAll));

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
