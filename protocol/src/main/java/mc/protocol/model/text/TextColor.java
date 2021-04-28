package mc.protocol.model.text;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TextColor {
	//@formatter:off
	BLACK      ("black",       '0'),
	DARK_BLUE  ("dark_blue",   '1'),
	DARK_GREEN ("dark_green",  '2'),
	DARK_AQUA  ("dark_aqua",   '3'),
	DARK_RED   ("dark_red",    '4'),
	DARK_PUEPLE("dark_purple", '5'),
	GOLD       ("gold",        '6'),
	GRAY       ("gray",        '7'),
	DARK_GRAY  ("dark_gray",   '8'),
	BLUE       ("blue",        '9'),
	GREEN      ("green",       'a'),
	AQUA       ("aqua",        'b'),
	RED        ("red",         'c'),
	PUEPLE     ("light_purple",'d'),
	YELLOW     ("yellow",      'e'),
	WHITE      ("white",       'f'),
	RESET      ("reset",       'r');
	//@formatter:on

	private final String name;
	private final char code;
}
