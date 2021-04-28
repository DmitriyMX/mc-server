package mc.protocol.model.text;

import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.LinkedList;

@EqualsAndHashCode
@ToString
public class Text {

	public static final Text EMPTY = of("");

	private String content;
	private ImmutableList<Text> children;

	Text(String content, ImmutableList<Text> children) {
		this.content = content;
		this.children = children;
	}

	public static Text of(String content) {
		return new Text(content, null);
	}

	public static Text.Builder builder() {
		return new Text.Builder();
	}

	public static class Builder {
		private final LinkedList<Object> chain = new LinkedList<>();

		public Builder append(String content) {
			chain.add(content);
			return this;
		}

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

			StringBuilder contentBuilder = null;
			ImmutableList.Builder<Text> childrenBuilder = null;

			for (Object element : chain) {
				if (element instanceof String) {
					if (contentBuilder == null) {
						contentBuilder = new StringBuilder((String) element);
					} else {
						contentBuilder.append((String) element);
					}
				} else if (element instanceof Text) {
					if (childrenBuilder == null) {
						childrenBuilder = ImmutableList.builder();
					}

					childrenBuilder.add((Text) element);
				}
			}

			return new Text(
					contentBuilder == null ? null : contentBuilder.toString(),
					childrenBuilder == null ? null : childrenBuilder.build());
		}
	}
}
