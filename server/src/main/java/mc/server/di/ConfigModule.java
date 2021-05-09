package mc.server.di;

import dagger.Module;
import dagger.Provides;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mc.server.config.Config;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Slf4j
@Module
@RequiredArgsConstructor
public class ConfigModule {

	private final Path configPath;

	@Provides
	Config provideConfig() {
		Config config = new Config();
		Map<String, Object> map = new Yaml().load(readConfigAsString());

		config.server().host(fromYamlPath("server/host", map, "127.0.0.1"));
		config.server().port(fromYamlPath("server/port", map, 25565));

		config.motd(fromYamlPath("motd", map, ""));
		config.disconnectReason(fromYamlPath("disconnect-reason", map, ""));

		config.players().maxOnlile(fromYamlPath("players/max-online", map, 0));
		config.players().fakeOnline().enable(fromYamlPath("players/fake-online/enable", map, false));
		config.players().fakeOnline().value(fromYamlPath("players/fake-online/value", map, 0));

		config.world().viewDistance(fromYamlPath("world/view-distance", map, 0));

		if (Boolean.TRUE.equals(fromYamlPath("icon/enable", map, false))) {
			config.iconPath(Paths.get(fromYamlPath("icon/path", map, "favicon.png")));
		}

		map.clear();
		return config;
	}

	private String readConfigAsString() {
		try {
			return Files.readString(configPath);
		} catch (IOException e) {
			log.error("Can't load config from '{}'", configPath.toAbsolutePath(), e);
			return "";
		}
	}

	@SuppressWarnings("unchecked")
	private static <T> T fromYamlPath(String mapPath, Map<String, Object> map, T defaultValue) {
		String[] keys = mapPath.split("/", 2);

		if (map.containsKey(keys[0])) {
			Object object = map.get(keys[0]);
			if (keys.length > 1) {
				return fromYamlPath(keys[1], (Map<String, Object>) object, defaultValue);
			} else {
				return (T) object;
			}
		} else {
			return defaultValue;
		}
	}
}
