package mc.server.di;

import dagger.Module;
import dagger.Provides;
import mc.protocol.world.World;
import mc.server.PacketHandler;
import mc.server.config.Config;

@Module
public class PacketHandlerModule {

	@Provides
	public PacketHandler providePacketHandler(Config config, World world) {
		return new PacketHandler(config, world);
	}
}
