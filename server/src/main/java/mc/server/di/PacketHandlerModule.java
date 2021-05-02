package mc.server.di;

import dagger.Module;
import dagger.Provides;
import mc.server.PacketHandler;
import mc.server.config.Config;

@Module
public class PacketHandlerModule {

	@Provides
	public PacketHandler providePacketHandler(Config config) {
		return new PacketHandler(config);
	}
}
