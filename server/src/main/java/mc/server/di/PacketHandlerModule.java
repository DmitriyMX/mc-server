package mc.server.di;

import dagger.Module;
import dagger.Provides;
import mc.protocol.world.World;
import mc.server.PacketHandler;
import mc.server.config.Config;
import mc.server.service.PlayerManager;

@Module
public class PacketHandlerModule {

	@Provides
	public PacketHandler providePacketHandler(Config config, World world, PlayerManager playerManager) {
		return new PacketHandler(config, world, playerManager);
	}
}
