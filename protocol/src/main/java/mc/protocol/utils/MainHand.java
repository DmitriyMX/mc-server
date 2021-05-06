package mc.protocol.utils;

import javax.annotation.Nullable;

public enum MainHand {
	LEFT,
	RIGHT;

	@Nullable
	public static MainHand valueById(int id) {
		// а зачем усложнять?
		//@formatter:off
		if (id == 0) return LEFT;
		else if (id == 1) return RIGHT;
		else return null;
		//@formatter:on
	}
}
