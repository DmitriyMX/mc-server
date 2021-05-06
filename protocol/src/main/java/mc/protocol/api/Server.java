package mc.protocol.api;

import java.util.function.Consumer;

public interface Server {

	void bind(String host, int port);

	void onNewConnect(Consumer<ConnectionContext<?>> consumer);
	void onDisonnect(Consumer<ConnectionContext<?>> consumer);
}
