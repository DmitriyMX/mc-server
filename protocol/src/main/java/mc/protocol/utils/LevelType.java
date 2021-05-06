package mc.protocol.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum LevelType {
	DEFAULT_TYPE("default"),
	FLAT("flat"),
	LARGE_BIOMES("largeBiomes"),
	AMPLIFIED("amplified"),
	DEFAULT_1_1("default_1_1");

	private final String type;
}
