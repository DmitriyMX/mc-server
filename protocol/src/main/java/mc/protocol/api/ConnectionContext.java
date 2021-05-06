package mc.protocol.api;

import mc.protocol.State;
import mc.protocol.packets.ServerSidePacket;

public interface ConnectionContext {

	State getState();
	void setState(State state);

	void send(ServerSidePacket packet);
	void sendNow(ServerSidePacket packet);
	void flushSending();

	void disconnect();
}
