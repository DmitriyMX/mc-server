package mc.server;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mc.protocol.api.ConnectionContext;
import mc.protocol.model.Location;
import mc.protocol.utils.GameMode;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class Player {

	private final ConnectionContext connectionContext;
	private final UUID uuid;
	private final String name;
	private final GameMode gameMode;
	private final Location location;
}
