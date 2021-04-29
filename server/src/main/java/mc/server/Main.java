package mc.server;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.util.PathConverter;
import lombok.extern.slf4j.Slf4j;
import mc.protocol.NettyServer;
import mc.protocol.ProtocolConstant;
import mc.protocol.model.ServerInfo;
import mc.protocol.model.text.Text;
import mc.protocol.packets.PingPacket;
import mc.protocol.packets.client.HandshakePacket;
import mc.protocol.packets.client.LoginStartPacket;
import mc.protocol.packets.client.StatusServerRequestPacket;
import mc.protocol.packets.server.DisconnectPacket;
import mc.protocol.packets.server.StatusServerResponse;
import mc.server.config.Config;
import mc.server.di.ConfigModule;
import mc.server.di.DaggerServerComponent;
import mc.server.di.ServerComponent;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

@Slf4j
public class Main {

	private void run(OptionSet optionSet) {
		log.info("mc-project launch");

		ConfigModule configModule = new ConfigModule((Path) optionSet.valueOf("config"));

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
					serverInfo.description(Text.of(config.motd()));

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
					disconnectPacket.setReason(Text.of("Server is not available."));

					channel.getCtx().writeAndFlush(disconnectPacket).channel().disconnect();
				});

		server.bind(config.server().host(), config.server().port());
	}

	@SuppressWarnings("java:S106")
	public static void main(String[] args) {
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
		optionParser.accepts("config", "Path to configuration file")
				.withRequiredArg()
				.withValuesConvertedBy(new PathConverter())
				.defaultsTo(Paths.get("config.yml"));

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
}
