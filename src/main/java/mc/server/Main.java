package mc.server;

import lombok.extern.slf4j.Slf4j;
import mc.server.di.DaggerNetworkComponent;
import mc.server.di.NetworkComponent;
import mc.server.network.Server;

@Slf4j
public class Main {

	public static void main(String[] args) {
		log.info("hello");

		NetworkComponent networkComponent = DaggerNetworkComponent.create();
		Server server = networkComponent.getServer();
		server.start("127.0.0.1", 25565);
	}
}
