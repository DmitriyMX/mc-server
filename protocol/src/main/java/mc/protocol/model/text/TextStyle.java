package mc.protocol.model.text;

import lombok.*;
import lombok.experimental.Accessors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderClassName = "Builder")
@Accessors(fluent = true)
@Getter
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode
@ToString
@SuppressWarnings("java:S1845")
public class TextStyle {

	public static final TextStyle BOLD = new TextStyle(true, null, null, null, null);
	public static final TextStyle ITALIC = new TextStyle(null, true, null, null, null);
	public static final TextStyle UNDERLINE = new TextStyle(null, null, true, null, null);
	public static final TextStyle STRIKETHOUGH = new TextStyle(null, null, null, true, null);
	public static final TextStyle OBFUSCATED = new TextStyle(null, null, null, null, true);

	public static final TextStyle RESET = new TextStyle(false, false, false, false, false);
	public static final TextStyle NONE = new TextStyle(null, null, null, null, null);

	private Boolean bold;
	private Boolean italic;
	private Boolean underline;
	private Boolean strikethrough;
	private Boolean obfuscated;

	void merge(TextStyle style) {
		if (style.bold != null) this.bold = style.bold;
		if (style.italic != null) this.italic = style.italic;
		if (style.underline != null) this.underline = style.underline;
		if (style.strikethrough != null) this.strikethrough = style.strikethrough;
		if (style.obfuscated != null) this.obfuscated = style.obfuscated;
	}
}
