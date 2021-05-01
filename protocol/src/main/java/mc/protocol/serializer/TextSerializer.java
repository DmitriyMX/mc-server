package mc.protocol.serializer;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import lombok.experimental.UtilityClass;
import mc.protocol.model.text.Text;
import mc.protocol.model.text.TextColor;
import mc.protocol.model.text.TextStyle;

import java.util.Map;

@UtilityClass
public class TextSerializer {

	private static final Map<Character, TextStyle> legacyStyleCodes;
	private static final Map<Character, TextColor> legacyColorCodes;

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

	/**
	 * Преобразование строки вида "&4красный" в {@link Text}.
	 *
	 * @param string тест
	 * @return Text
	 */
	@SuppressWarnings({"java:S3776", "java:S2583", "java:S135"})
	public Text fromPlain(String string) {
		boolean flagSys = false;
		Text.Builder rootTextBuilder = Text.builder();
		Text.Builder textBuilder = rootTextBuilder;

		for (char ch : string.toCharArray()) {
			if (!flagSys) {
				if ('&' == ch) {
					flagSys = true;
				} else {
					textBuilder.append(ch);
				}
				continue;
			}

			if (!legacyStyleCodes.containsKey(ch) && !legacyColorCodes.containsKey(ch) && '&' == ch) {
				textBuilder.append('&');
				flagSys = false;
				continue;
			}

			//noinspection ConstantConditions
			if (textBuilder.contentBuilder() != null && textBuilder.contentBuilder().length() > 0) {
				if (textBuilder != rootTextBuilder) {
					rootTextBuilder.append(textBuilder.build());
				}
				textBuilder = Text.builder();
			}

			if (legacyStyleCodes.containsKey(ch)) {
				textBuilder.style(legacyStyleCodes.get(ch));
			} else {
				textBuilder.color(legacyColorCodes.get(ch));
			}

			flagSys = false;
		}

		if (textBuilder != rootTextBuilder) {
			rootTextBuilder.append(textBuilder.build());
		}

		return rootTextBuilder.build();
	}

	static {
		legacyColorCodes = Map.ofEntries(
				Map.entry('0', TextColor.BLACK),
				Map.entry('1', TextColor.DARK_BLUE),
				Map.entry('2', TextColor.DARK_GREEN),
				Map.entry('3', TextColor.DARK_AQUA),
				Map.entry('4', TextColor.DARK_RED),
				Map.entry('5', TextColor.DARK_PUEPLE),
				Map.entry('6', TextColor.GOLD),
				Map.entry('7', TextColor.GRAY),
				Map.entry('8', TextColor.DARK_GRAY),
				Map.entry('9', TextColor.BLUE),
				Map.entry('a', TextColor.GREEN),
				Map.entry('b', TextColor.AQUA),
				Map.entry('c', TextColor.RED),
				Map.entry('d', TextColor.PURPLE),
				Map.entry('e', TextColor.YELLOW),
				Map.entry('f', TextColor.WHITE)
		);

		legacyStyleCodes = Map.of(
				'k', TextStyle.OBFUSCATED,
				'l', TextStyle.BOLD,
				'm', TextStyle.STRIKETHOUGH,
				'n', TextStyle.UNDERLINE,
				'o', TextStyle.ITALIC
		);
	}
}
