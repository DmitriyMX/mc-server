package mc.protocol.model.text;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Accessors(fluent = true)
@Getter
@EqualsAndHashCode
@ToString
public class Text {

	public static final Text EMPTY = of("");

	private TextColor color;
	private TextStyle style;
	private String content;
	private List<Text> children;

	Text(TextColor color, TextStyle style, String content, List<Text> children) {
		this.color = color;
		this.style = style;
		this.content = content;
		this.children = children;
	}

	public static Text of(String content) {
		return new Text(null, null, content, null);
	}

	public static Text of(TextColor color, String content) {
		return new Text(color, null, content, null);
	}

	public static Text of(TextStyle style, String content) {
		return new Text(null, style, content, null);
	}

	public static Text of(TextColor color, TextStyle style, String content) {
		return new Text(color, style, content, null);
	}

	public static Text.Builder builder() {
		return new Text.Builder();
	}

	public static class Builder {
		private final LinkedList<Text> chain = new LinkedList<>();

		public Builder append(Text text) {
			if (text == null || EMPTY.equals(text)) {
				return this;
			}

			chain.add(text);
			return this;
		}

		public Text build() {
			if (chain.isEmpty()) {
				return EMPTY;
			}

			Text rootText = chain.pollFirst();

			if (!chain.isEmpty()) {
				rootText.children = new ArrayList<>();
				rootText.children.addAll(chain);
			}

			return rootText;
		}
	}
}
