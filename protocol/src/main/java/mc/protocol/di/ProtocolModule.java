package mc.protocol.di;

import dagger.Module;
import dagger.Provides;
import mc.protocol.NettyServer;
import mc.protocol.api.Server;

@Module
public class ProtocolModule {

	@Provides
	@ServerScope
	Server provideServer() {
		return new NettyServer();
	}

}
