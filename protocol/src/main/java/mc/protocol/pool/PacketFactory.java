package mc.protocol.pool;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mc.protocol.packets.ClientSidePacket;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

@Slf4j
@RequiredArgsConstructor
public class PacketFactory<P extends ClientSidePacket> extends BasePooledObjectFactory<P> {

	private final Class<P> clazz;

	@Override
	public P create() throws Exception {
		return clazz.getDeclaredConstructor().newInstance();
	}

	@Override
	public PooledObject<P> wrap(P packet) {
		return new DefaultPooledObject<>(packet);
	}

	@Override
	public void passivateObject(PooledObject<P> pooledPacket) {
		pooledPacket.getObject().passivate();
	}
}
