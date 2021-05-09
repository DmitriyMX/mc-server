package mc.protocol.api;

import mc.protocol.State;
import mc.protocol.packets.ServerSidePacket;

import java.util.Optional;

public interface ConnectionContext {

	/**
	 * @deprecated костыль
	 */
	@Deprecated
	void setUsedContext(boolean value);

	/**
	 * @deprecated костыль
	 */
	@Deprecated
	boolean isUsedContext();

	State getState();
	void setState(State state);

	/**
	 * @deprecated костыль
	 */
	@Deprecated
	<T> void setCustomProperty(String key, T value);

	/**
	 * @deprecated костыль
	 */
	@Deprecated
	<T> Optional<T> getCustomProperty(String key, Class<T> classResult);

	void send(ServerSidePacket packet);
	void sendNow(ServerSidePacket packet);
	void flushSending();

	void disconnect();
}
