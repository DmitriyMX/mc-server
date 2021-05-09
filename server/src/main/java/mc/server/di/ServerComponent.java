package mc.server.di;

import dagger.Component;
import mc.protocol.di.ServerScope;
import mc.server.PacketHandler;
import mc.server.config.Config;

@Component(modules = {
		ConfigModule.class, PacketHandlerModule.class, WorldModule.class
})
@ServerScope
public interface ServerComponent {

	Config getConfig();
	PacketHandler getPacketHandler();
}
