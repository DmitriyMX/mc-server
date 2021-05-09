package mc.server.service;

import mc.protocol.api.ConnectionContext;
import mc.protocol.model.Location;
import mc.protocol.utils.GameMode;
import mc.server.Player;

import java.util.LinkedList;
import java.util.UUID;

public class PlayerManager {

	private final LinkedList<Player> players = new LinkedList<>();

	public Player addAndCreate(ConnectionContext context, String name, GameMode gameMode, Location location) {
		context.setUsedContext(true);
		Player player = new Player(context, UUID.randomUUID(), name, gameMode, location);
		players.add(player);
		return player;
	}

	public void remove(Player player) {
		players.remove(player);
	}

	public int online() {
		return players.size();
	}
}
