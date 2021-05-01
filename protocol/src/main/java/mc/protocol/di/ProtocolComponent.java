package mc.protocol.di;

import dagger.Component;
import mc.protocol.NettyServer;

@Component(modules = ProtocolModule.class)
@ServerScope
public interface ProtocolComponent {

	NettyServer getNettyServer();
}
