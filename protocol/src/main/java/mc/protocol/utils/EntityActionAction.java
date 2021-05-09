package mc.protocol.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nullable;

@RequiredArgsConstructor
public enum EntityActionAction {
	START_SNEAKING(0),
	STOP_SNEAKING(1),
	LEAVE_BED(2),
	START_SPRINTING(3),
	STOP_SPRINTING(4),
	START_JUMP_WITH_HORSE(5),
	STOP_JUMP_WITH_HORSE(6),
	OPEN_HORSE_INVENTORY(7),
	START_FLYING_WITH_ELYTRA(8);

	@Nullable
	public static EntityActionAction valueOfCode(int code) {
		for (EntityActionAction action : EntityActionAction.values()) {
			if (action.code == code) {
				return action;
			}
		}

		return null;
	}

	@Getter
	private final int code;
}
