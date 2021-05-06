package mc.protocol.utils;

import javax.annotation.Nullable;

public enum ChatMode {
	FULL,
	COMMANDS_ONLY,
	HIDDEN;

	@Nullable
	public static ChatMode valueById(int id) {
		// а зачем усложнять?
		//@formatter:off
		if (id == 1) return FULL;
		else if (id == 2) return COMMANDS_ONLY;
		else if (id == 3) return HIDDEN;
		else return null;
		//@formatter:on
	}
}
