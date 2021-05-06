package mc.protocol.utils;

import mc.protocol.State;
import mc.protocol.api.ConnectionContext;
import mc.protocol.packets.ClientSidePacket;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class SimpleEventBus implements EventBus {

	private final Table<State, Class<? extends ClientSidePacket>, EventHandler> table = new Table<>();

	@Override
	public <P extends ClientSidePacket> void subscribe(State state, Class<P> packetClass, EventHandler<P> eventHandler) {
		table.put(state, packetClass, eventHandler);
	}

	@Override
	public <P extends ClientSidePacket> void emit(State state, ConnectionContext<P> channelContext) {
		EventHandler eventHandler = table.getColumnAndRow(state, channelContext.clientPacket().getClass());

		if (eventHandler != null) {
			eventHandler.handle(channelContext);
		}
	}
}
