package mc.protocol.utils;

import mc.protocol.ChannelContext;
import mc.protocol.State;
import mc.protocol.packets.ClientSidePacket;

public interface EventBus {

	<P extends ClientSidePacket> void subscribe(State state, Class<P> packetClass, EventHandler<P> eventHandler);

	<P extends ClientSidePacket> void emit(State state, ChannelContext<P> channelContext);

	@FunctionalInterface
	interface EventHandler<P extends ClientSidePacket> {
		void handle(ChannelContext<P> channelContext);
	}
}
