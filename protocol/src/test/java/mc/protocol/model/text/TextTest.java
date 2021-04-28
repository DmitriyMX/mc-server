package mc.protocol.model.text;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TextTest {

	@Test
	void emptyTest() {
		Text actual = Text.builder().build();
		Text expected = Text.EMPTY;

		assertEquals(expected, actual);
	}

	@Test
	void contentTest() {
		Text actual;
		Text expected;

		actual = Text.builder().append("123").build();
		expected = new Text("123");
		assertEquals(expected, actual);

		actual = Text.builder().append("123").append("456").build();
		expected = new Text("123456");
		assertEquals(expected, actual);
	}
}