package mc.server;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.util.PathConverter;
import lombok.extern.slf4j.Slf4j;
import mc.protocol.NettyServer;
import mc.protocol.ProtocolConstant;
import mc.protocol.model.ServerInfo;
import mc.protocol.packets.PingPacket;
import mc.protocol.packets.client.HandshakePacket;
import mc.protocol.packets.client.LoginStartPacket;
import mc.protocol.packets.client.StatusServerRequestPacket;
import mc.protocol.packets.server.DisconnectPacket;
import mc.protocol.packets.server.StatusServerResponse;
import mc.protocol.serializer.TextSerializer;
import mc.server.config.Config;
import mc.server.di.ConfigModule;
import mc.server.di.DaggerServerComponent;
import mc.server.di.ServerComponent;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@SuppressWarnings("java:S106")
public class Main {
	private static final String CLI_CONFIG = "config";

	private void run(OptionSet optionSet) {
		log.info("mc-project launch");

		ConfigModule configModule = new ConfigModule((Path) optionSet.valueOf(CLI_CONFIG));

		ServerComponent serverComponent = DaggerServerComponent.builder()
				.configModule(configModule)
				.build();

		Config config = serverComponent.getConfig();

		NettyServer server = NettyServer.createServer();

		server.packetFlux(HandshakePacket.class)
				.doOnNext(channel -> log.info("{}", channel.getPacket()))
				.subscribe(channel -> channel.setState(channel.getPacket().getNextState()));

		server.packetFlux(PingPacket.class)
				.doOnNext(channel -> log.info("{}", channel.getPacket()))
				.subscribe(channel -> channel.getCtx().writeAndFlush(channel.getPacket()).channel().disconnect());

		server.packetFlux(StatusServerRequestPacket.class)
				.doOnNext(channel -> log.info("{}", channel.getPacket()))
				.subscribe(channel -> {
					ServerInfo serverInfo = new ServerInfo();
					serverInfo.version().name(ProtocolConstant.PROTOCOL_NAME);
					serverInfo.version().protocol(ProtocolConstant.PROTOCOL_NUMBER);
					serverInfo.players().max(config.players().maxOnlile());
					serverInfo.players().online(config.players().onlile());
					serverInfo.players().sample(Collections.emptyList());
					serverInfo.description(TextSerializer.fromPlain(config.motd()));

					if (config.iconPath() != null) {
						serverInfo.favicon(faviconToBase64(config.iconPath()));
					}

					StatusServerResponse response = new StatusServerResponse();
					response.setInfo(serverInfo);

					channel.getCtx().writeAndFlush(response);
				});

		server.packetFlux(LoginStartPacket.class)
				.doOnNext(channel -> log.info("{}", channel.getPacket()))
				.subscribe(channel -> {
					DisconnectPacket disconnectPacket = new DisconnectPacket();
					disconnectPacket.setReason(TextSerializer.fromPlain(config.disconnectReason()));

					channel.getCtx().writeAndFlush(disconnectPacket).channel().disconnect();
				});

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
			Path logbackPath = Paths.get(System.getProperty("logback.configurationFile", "logback.xml"));

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
		optionParser.accepts(CLI_CONFIG, "Path to configuration file")
				.withRequiredArg()
				.withValuesConvertedBy(new PathConverter())
				.defaultsTo(Paths.get("config.yml"));
		optionParser.accepts("init", "Initialize environment");

		return optionParser;
	}

	private static String faviconToBase64(Path iconPath) {
		try {
			return "data:image/png;base64," +
					Base64.getEncoder().encodeToString(
							IOUtils.toByteArray(Files.newInputStream(iconPath)));
		} catch (IOException e) {
			log.error("Can't read icon '{}'", iconPath.toAbsolutePath(), e);
			return "";
		}
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
}
