package mc.server.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.nio.file.Path;

@Accessors(fluent = true)
@Getter
@Setter
@ToString
public class Config {

	private final Server server = new Server();
	private final Players players = new Players();

	private String motd;
	private String disconnectReason;
	private Path iconPath;

	@Getter
	@Setter
	@ToString
	public static class Server {
		private String host;
		private int port;
	}

	@Getter
	@Setter
	@ToString
	public static class Players {
		private int maxOnlile;
		private int onlile;
	}
}
