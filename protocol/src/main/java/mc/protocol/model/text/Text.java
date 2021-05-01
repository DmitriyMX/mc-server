package mc.protocol.model.text;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Getter;
import lombok.experimental.Accessors;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Accessors(fluent = true)
@AllArgsConstructor
@Data
public class Text {

	private TextColor color;
	private TextStyle style;
	private String content;
	private List<Text> children;

	public static Text of(String string) {
		return new Text(null, null, string, null);
	}

	public static Text of(TextColor color, String string) {
		return new Text(color, null, string, null);
	}

	public static Text of(TextStyle style, String string) {
		return new Text(null, style, string, null);
	}

	public static Text of(TextColor color, TextStyle style, String string) {
		return new Text(color, style, string, null);
	}

	public static Builder builder() {
		return new Builder();
	}

	@NoArgsConstructor
	@ToString
	public static class Builder {

		@Getter(onMethod = @__(@Nullable))
		private StringBuilder contentBuilder;
		private TextStyle.Builder styleBuilder;
		private TextColor color;
		private List<Text> children;

		public Builder append(char content) {
			if (this.contentBuilder == null) {
				this.contentBuilder = new StringBuilder();
			}

			this.contentBuilder.append(content);
			return this;
		}

		public Builder append(String content) {
			if (this.contentBuilder == null) {
				this.contentBuilder = new StringBuilder(content);
			} else {
				this.contentBuilder.append(content);
			}
			return this;
		}

		public Builder append(Text text) {
			if (children == null) {
				children = new ArrayList<>();
			}

			children.add(text);
			return this;
		}

		public Builder style(TextStyle style) {
			//@formatter:off
			if (style.bold() != null) bold(style.bold());
			if (style.italic() != null) italic(style.italic());
			if (style.underline() != null) underline(style.underline());
			if (style.strikethrough() != null) strikethrough(style.strikethrough());
			if (style.obfuscated() != null) obfuscated(style.obfuscated());
			//@formatter:on

			return this;
		}

		public Builder color(TextColor color) {
			this.color = color;
			return this;
		}

		public Builder bold(Boolean bold) {
			if (this.styleBuilder == null) {
				this.styleBuilder = TextStyle.builder();
			}

			this.styleBuilder.bold(bold);
			return this;
		}

		public Builder italic(Boolean italic) {
			if (this.styleBuilder == null) {
				this.styleBuilder = TextStyle.builder();
			}

			this.styleBuilder.italic(italic);
			return this;
		}

		public Builder underline(Boolean underline) {
			if (this.styleBuilder == null) {
				this.styleBuilder = TextStyle.builder();
			}

			this.styleBuilder.underline(underline);
			return this;
		}

		public Builder strikethrough(Boolean strikethrough) {
			if (this.styleBuilder == null) {
				this.styleBuilder = TextStyle.builder();
			}

			this.styleBuilder.strikethrough(strikethrough);
			return this;
		}

		public Builder obfuscated(Boolean obfuscated) {
			if (this.styleBuilder == null) {
				this.styleBuilder = TextStyle.builder();
			}

			this.styleBuilder.obfuscated(obfuscated);
			return this;
		}

		public Text build() {
			return new Text(
					color,
					styleBuilder == null ? null : styleBuilder.build(),
					contentBuilder == null ? null : contentBuilder.toString(),
					children);
		}
	}
}
