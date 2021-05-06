package mc.protocol.api;

import mc.protocol.State;
import mc.protocol.packets.ClientSidePacket;
import mc.protocol.utils.EventBus;

import java.util.function.Consumer;

public interface Server {

	void bind(String host, int port);

	void onNewConnect(Consumer<ConnectionContext<?>> consumer);
	void onDisonnect(Consumer<ConnectionContext<?>> consumer);

	<P extends ClientSidePacket> void listenPacket(State state, Class<P> packetClass, EventBus.EventHandler<P> eventHandler);
}
