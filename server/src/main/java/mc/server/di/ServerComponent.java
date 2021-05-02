package mc.server.di;

import dagger.Component;
import mc.server.PacketHandler;
import mc.server.config.Config;

@Component(modules = {
		ConfigModule.class, PacketHandlerModule.class
})
public interface ServerComponent {

	Config getConfig();
	PacketHandler getPacketHandler();
}
