package mc.protocol.event;

import mc.protocol.State;
import mc.protocol.api.ConnectionContext;
import mc.protocol.packets.ClientSidePacket;
import mc.protocol.utils.Table;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class SimpleEventBus implements EventBus {

	private final Table<State, Class<? extends ClientSidePacket>, EventHandler> table = new Table<>();

	@Override
	public <P extends ClientSidePacket> void subscribe(State state, Class<P> packetClass, EventHandler<P> eventHandler) {
		table.put(state, packetClass, eventHandler);
	}

	@Override
	public <P extends ClientSidePacket> void emit(State state, ConnectionContext channelContext, P packet) {
		EventHandler eventHandler = table.getColumnAndRow(state, packet.getClass());

		if (eventHandler != null) {
			eventHandler.handle(channelContext, packet);
		}
	}
}
