package mc.server.di;

import dagger.Module;
import dagger.Provides;
import mc.protocol.di.ServerScope;
import mc.protocol.world.World;
import mc.server.world.VoidWorld;

@Module
public class WorldModule {

	@Provides
	@ServerScope
	public World provideWorld() {
		return new VoidWorld();
	}
}
