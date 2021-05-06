package mc.protocol.utils;

import mc.protocol.State;
import mc.protocol.api.ConnectionContext;
import mc.protocol.packets.ClientSidePacket;

public interface EventBus {

	<P extends ClientSidePacket> void subscribe(State state, Class<P> packetClass, EventHandler<P> eventHandler);

	<P extends ClientSidePacket> void emit(State state, ConnectionContext channelContext, P packet);

	@FunctionalInterface
	interface EventHandler<P extends ClientSidePacket> {
		void handle(ConnectionContext channelContext, P packet);
	}
}
