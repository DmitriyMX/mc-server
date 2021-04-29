package mc.protocol.serializer;

import mc.protocol.model.text.Text;
import mc.protocol.model.text.TextColor;
import mc.protocol.model.text.TextStyle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TextSerializerTest {

	@ParameterizedTest
	@MethodSource("paramsPlain")
	void fromPlain(String sample, Text expected) {
		Text actual = TextSerializer.fromPlain(sample);
		assertEquals(expected, actual);
	}

	@SuppressWarnings("unused")
	static Stream<Arguments> paramsPlain() {
		return Stream.of(
				Arguments.of("text", Text.of("text")),
				Arguments.of("&&text", Text.of("&text")),
				Arguments.of("&ztext", Text.of("text")),
				Arguments.of("&4red_text", Text.of(TextColor.DARK_RED, "red_text")),
				Arguments.of("&l&4red_text", Text.of(TextColor.DARK_RED, TextStyle.BOLD, "red_text")),
				Arguments.of("&4&lred_text", Text.of(TextColor.DARK_RED, TextStyle.BOLD, "red_text")),

				Arguments.of("&4red_text &eyellow_text", Text.builder()
						.color(TextColor.DARK_RED)
						.append("red_text ")
						.append(Text.of(TextColor.YELLOW, "yellow_text"))
						.build())
		);
	}
}