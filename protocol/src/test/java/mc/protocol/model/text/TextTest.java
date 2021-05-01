package mc.protocol.model.text;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TextTest {

	@Test
	void contentTest() {
		Text actual;
		Text expected;

		actual = Text.builder().append("123").build();
		expected = new Text(null, null, "123", null);
		assertEquals(expected, actual);

		actual = Text.builder().append("123").append(Text.of("456")).build();
		expected = new Text(null, null, "123", List.of(Text.of("456")));
		assertEquals(expected, actual);
	}
}