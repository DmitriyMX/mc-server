package mc.server.di;

import dagger.Component;
import mc.protocol.di.ServerScope;
import mc.server.PacketHandler;
import mc.server.config.Config;
import mc.server.service.PlayerManager;

@Component(modules = {
		ConfigModule.class, PacketHandlerModule.class, WorldModule.class, PlayersModule.class
})
@ServerScope
public interface ServerComponent {

	Config getConfig();
	PacketHandler getPacketHandler();
	PlayerManager getPlayerManager();
}
