package mc.protocol.di;

import dagger.Component;
import mc.protocol.api.Server;

@Component(modules = ProtocolModule.class)
@ServerScope
public interface ProtocolComponent {

	Server getServer();
}
