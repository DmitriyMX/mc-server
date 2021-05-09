package mc.server;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.util.PathConverter;
import lombok.extern.slf4j.Slf4j;
import mc.protocol.State;
import mc.protocol.api.Server;
import mc.protocol.di.DaggerProtocolComponent;
import mc.protocol.di.ProtocolComponent;
import mc.protocol.di.ProtocolModule;
import mc.protocol.packets.KeepAlivePacket;
import mc.protocol.packets.client.HandshakePacket;
import mc.protocol.packets.client.LoginStartPacket;
import mc.protocol.packets.client.StatusServerRequestPacket;
import mc.server.config.Config;
import mc.server.di.ConfigModule;
import mc.server.di.DaggerServerComponent;
import mc.server.di.ServerComponent;
import mc.server.service.PlayerManager;
import org.apache.commons.io.IOUtils;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@Slf4j
@SuppressWarnings("java:S106")
public class Main {
	private static final String CLI_CONFIG = "config";
	private static final String CLI_LOGCONFIG = "logconfig";

	private void run(OptionSet optionSet) {
		log.info("mc-project launch");

		ConfigModule configModule = new ConfigModule((Path) optionSet.valueOf(CLI_CONFIG));

		ServerComponent serverComponent = DaggerServerComponent.builder()
				.configModule(configModule)
				.build();

		Config config = serverComponent.getConfig();
		PlayerManager playerManager = serverComponent.getPlayerManager();

		ProtocolComponent protocolComponent = DaggerProtocolComponent.builder()
				.protocolModule(new ProtocolModule(true))
				.build();

		Server server = protocolComponent.getServer();
		PacketHandler packetHandler = serverComponent.getPacketHandler();

		server.onNewConnect(connectionContext -> connectionContext.setState(State.HANDSHAKING));
		server.onDisonnect(connectionContext -> {
			connectionContext.setState(null);
			connectionContext.getCustomProperty("player", Player.class).ifPresent(playerManager::remove);
		});

		server.listenPacket(State.HANDSHAKING, HandshakePacket.class, packetHandler::onHandshake);
		server.listenPacket(State.STATUS, KeepAlivePacket.class, packetHandler::onKeepAlive);
		server.listenPacket(State.STATUS, StatusServerRequestPacket.class, packetHandler::onServerStatus);
		server.listenPacket(State.LOGIN, LoginStartPacket.class, packetHandler::onLoginStart);
		server.listenPacket(State.PLAY, KeepAlivePacket.class, packetHandler::onKeepAlivePlay);

		server.bind(config.server().host(), config.server().port());
	}

	public static void main(String[] args) throws IOException {
		OptionParser optionParser = createOptionParser();
		OptionSet optionSet = optionParser.parse(args);

		if (optionSet.has("help")) {
			try {
				optionParser.printHelpOn(System.out);
			} catch (IOException e) {
				System.err.printf("Can't print help page: %s%n", e.getMessage());
				e.printStackTrace(System.err);
			}
			return;
		} else if (optionSet.has("init")) {
			Path configPath = (Path) optionSet.valueOf(CLI_CONFIG);
			Path logbackPath = (Path) optionSet.valueOf(CLI_LOGCONFIG);

			if (!initializeCheckFiles(configPath, logbackPath)) {
				return;
			}

			InputStream configResource = Objects.requireNonNull(Main.class.getResourceAsStream("/config-sample.yml"));
			InputStream logbackResource = Objects.requireNonNull(Main.class.getResourceAsStream("/logback-sample.xml"));

			try(OutputStream configOut = Files.newOutputStream(configPath);
			    OutputStream logbackOut = Files.newOutputStream(logbackPath)) {
				IOUtils.copy(configResource, configOut);
				IOUtils.copy(logbackResource, logbackOut);
			}

			System.out.println("Initialization environment done.");
			return;
		}

		reconfigureLogback(optionSet);

		if (log.isDebugEnabled()) {
			optionSet.asMap().forEach((optionSpec, objects) -> {
				if (optionSpec.isForHelp()) return;
				log.debug("OptionSet | {} = {}", optionSpec.options(), objects);
			});
		}

		new Main().run(optionSet);
	}

	private static OptionParser createOptionParser() {
		OptionParser optionParser = new OptionParser();

		optionParser.acceptsAll(List.of("h", "help"), "Help page").forHelp();
		optionParser.accepts("init", "Initialize environment");

		optionParser.accepts(CLI_CONFIG, "Path to configuration file")
				.withRequiredArg()
				.withValuesConvertedBy(new PathConverter())
				.defaultsTo(Paths.get("config.yml"));

		optionParser.accepts(CLI_LOGCONFIG, "Path to logger configuratuin file")
				.withRequiredArg()
				.withValuesConvertedBy(new PathConverter())
				.defaultsTo(Paths.get("logback.xml"));

		return optionParser;
	}

	private static boolean initializeCheckFiles(Path... paths) {
		for (Path path : paths) {
			if (Files.exists(path)) {
				System.err.printf("File '%s' already exist. Initialization environment canceled.%n",
						path.toAbsolutePath());
				return false;
			}
		}

		return true;
	}

	private static void reconfigureLogback(OptionSet optionSet) throws IOException {
		LoggerContext logbackContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		logbackContext.reset();
		JoranConfigurator configurator = new JoranConfigurator();

		Path logbackPath = (Path) optionSet.valueOf(CLI_LOGCONFIG);
		try(InputStream in = Objects.requireNonNull(
				Files.newInputStream(logbackPath), "File not found: " + logbackPath.toAbsolutePath())) {

			configurator.setContext(logbackContext);
			configurator.doConfigure(in);
		} catch (JoranException e) {
			throw new IOException(e);
		}
	}
}
