package mc.protocol.pool;

import mc.protocol.NettyConnectionContext;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class NettyConnectionContextFactory extends BasePooledObjectFactory<NettyConnectionContext> {

	@Override
	public NettyConnectionContext create() throws Exception {
		return new NettyConnectionContext();
	}

	@Override
	public PooledObject<NettyConnectionContext> wrap(NettyConnectionContext context) {
		return new DefaultPooledObject<>(context);
	}

	@Override
	public void passivateObject(PooledObject<NettyConnectionContext> pooledObj) {
		pooledObj.getObject().passivate();
	}
}
