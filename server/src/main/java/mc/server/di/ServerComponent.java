package mc.server.di;

import dagger.Component;
import mc.server.config.Config;

@Component(modules = ConfigModule.class)
public interface ServerComponent {

	Config getConfig();
}
