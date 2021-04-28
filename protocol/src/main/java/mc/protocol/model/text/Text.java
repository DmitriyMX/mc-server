package mc.protocol.model.text;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class Text {

	public static final Text EMPTY = of("");

	private String content;

	Text(String content) {
		this.content = content;
	}

	public static Text of(String content) {
		return new Text(content);
	}

	public static Text.Builder builder() {
		return new Text.Builder();
	}

	public static class Builder {
		private String content;

		public Builder append(String content) {
			if (this.content == null) {
				this.content = content;
			} else {
				this.content += content;
			}
			return this;
		}

		public Text build() {
			if (content == null) {
				return EMPTY;
			} else {
				return new Text(content);
			}
		}
	}
}
