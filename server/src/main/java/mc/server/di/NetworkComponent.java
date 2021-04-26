package mc.server.di;

import dagger.Component;
import mc.server.network.Server;

@Component(modules = NetworkModule.class)
public interface NetworkComponent {

	Server getServer();
}
