package mc.protocol.model.text;

import com.google.common.collect.ImmutableList;
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
		expected = new Text("123", null);
		assertEquals(expected, actual);

		actual = Text.builder().append("123").append("456").build();
		expected = new Text("123456", null);
		assertEquals(expected, actual);
	}

	@Test
	void childrenTest() {
		Text actual;
		Text expected;

		actual = Text.builder().append("123").append((Text) null).build();
		expected = new Text("123", null);
		assertEquals(expected, actual);

		actual = Text.builder().append("123").append(Text.EMPTY).build();
		expected = new Text("123", null);
		assertEquals(expected, actual);

		Text child = Text.of("456");
		actual = Text.builder().append("123").append(child).build();
		expected = new Text("123", ImmutableList.of(child));
		assertEquals(expected, actual);
	}
}