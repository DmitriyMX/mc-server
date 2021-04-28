package mc.server;

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

@Slf4j
public class Main {

	public static void main(String[] args) {
		log.info("mc-project launch");

		ConfigModule configModule;
		if (args.length > 0) {
			configModule = new ConfigModule(Paths.get(args[0]));
		} else {
			configModule = new ConfigModule(Paths.get("config.yml"));
		}

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
