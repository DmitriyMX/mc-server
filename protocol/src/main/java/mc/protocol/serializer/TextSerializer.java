package mc.protocol.serializer;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import lombok.experimental.UtilityClass;
import mc.protocol.model.text.Text;

@UtilityClass
public class TextSerializer {

	public JsonObject toJsonObject(Text text) {
		JsonObject jsonObject = Json.object();

		if (text.content() != null) {
			jsonObject.add("text", text.content());
		}

		if (text.color() != null) {
			jsonObject.add("color", text.color().getName());
		}

		if (text.style() != null) {
			//@formatter:off
			if (text.style().bold() != null) jsonObject.add("bold", text.style().bold());
			if (text.style().italic() != null) jsonObject.add("italic", text.style().italic());
			if (text.style().underline() != null) jsonObject.add("underline", text.style().underline());
			if (text.style().strikethrough() != null) jsonObject.add("strikethrough", text.style().strikethrough());
			if (text.style().obfuscated() != null) jsonObject.add("obfuscated", text.style().obfuscated());
			//@formatter:on
		}

		if (text.children() != null && !text.children().isEmpty()) {
			JsonArray extra = Json.array();
			text.children().forEach(child -> extra.add(toJsonObject(child)));
			jsonObject.add("extra", extra);
		}

		return jsonObject;
	}
}
