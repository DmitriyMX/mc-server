package mc.protocol.model.text;

import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;
import lombok.ToString;

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
		private String content;
		private ImmutableList.Builder<Text> childrenBuilder;

		public Builder append(String content) {
			if (this.content == null) {
				this.content = content;
			} else {
				this.content += content;
			}
			return this;
		}

		public Builder append(Text text) {
			if (text == null || EMPTY.equals(text)) {
				return this;
			}

			if (this.childrenBuilder == null) {
				this.childrenBuilder = ImmutableList.builder();
			}

			this.childrenBuilder.add(text);
			return this;
		}

		public Text build() {
			if (content == null && childrenBuilder == null) {
				return EMPTY;
			} else {
				return new Text(content,
						childrenBuilder == null ? null : childrenBuilder.build());
			}
		}
	}
}
