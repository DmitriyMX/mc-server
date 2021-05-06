package mc.protocol.api;

import mc.protocol.State;
import mc.protocol.packets.ClientSidePacket;
import mc.protocol.event.EventBus;

import java.util.function.Consumer;

public interface Server {

	void bind(String host, int port);

	void onNewConnect(Consumer<ConnectionContext> consumer);
	void onDisonnect(Consumer<ConnectionContext> consumer);

	@SuppressWarnings("java:S2326") // Сонар, ты бредишь
	<P extends ClientSidePacket> void listenPacket(State state, Class<P> packetClass, EventBus.EventHandler<P> eventHandler);
}
