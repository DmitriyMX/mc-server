package mc.protocol.api;

import mc.protocol.State;
import mc.protocol.packets.ClientSidePacket;
import mc.protocol.packets.ServerSidePacket;

public interface ConnectionContext<P extends ClientSidePacket> {

	State getState();
	void setState(State state);

	P clientPacket();

	void send(ServerSidePacket packet);
	void sendNow(ServerSidePacket packet);
	void flushSending();

	void disconnect();
}
