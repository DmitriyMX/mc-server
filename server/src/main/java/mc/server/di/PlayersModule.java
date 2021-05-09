package mc.server.di;

import dagger.Module;
import dagger.Provides;
import mc.protocol.di.ServerScope;
import mc.server.service.PlayerManager;

@Module
public class PlayersModule {

	@Provides
	@ServerScope
	PlayerManager providePlayerManager() {
		return new PlayerManager();
	}
}
