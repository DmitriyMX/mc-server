package mc.server;

import lombok.extern.slf4j.Slf4j;
import mc.protocol.NettyServer;
import mc.protocol.ProtocolConstant;
import mc.protocol.packets.PingPacket;
import mc.protocol.packets.client.HandshakePacket;
import mc.protocol.packets.client.LoginStartPacket;
import mc.protocol.packets.client.StatusServerRequest;
import mc.protocol.packets.server.DisconnectPacket;
import mc.protocol.packets.server.StatusServerResponse;
import mc.server.config.Config;
import mc.server.di.ConfigModule;
import mc.server.di.DaggerServerComponent;
import mc.server.di.ServerComponent;

import java.nio.file.Paths;

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

		server.packetFlux(StatusServerRequest.class)
				.doOnNext(channel -> log.info("{}", channel.getPacket()))
				.subscribe(channel -> {
					StatusServerResponse response = new StatusServerResponse();
					response.setInfo("{\n" +
							"  \"version\": {\n" +
							"    \"name\": \"" + ProtocolConstant.PROTOCOL_NAME + "\",\n" +
							"    \"protocol\": " + ProtocolConstant.PROTOCOL_NUMBER + "\n" +
							"  },\n" +
							"  \"players\": {\n" +
							"    \"max\": " + config.players().maxOnlile() + ",\n" +
							"    \"online\": " + config.players().onlile() + ",\n" +
							"    \"sample\": []\n" +
							"  },\n" +
							"  \"description\": {\n" +
							"    \"text\": \"" + config.motd() + "\"\n" +
							"  }\n" +
							"}");

					channel.getCtx().writeAndFlush(response);
				});

		server.packetFlux(LoginStartPacket.class)
				.doOnNext(channel -> log.info("{}", channel.getPacket()))
				.subscribe(channel -> {
					DisconnectPacket disconnectPacket = new DisconnectPacket();
					disconnectPacket.setReason("{\n" +
							"  \"text\": \"Server is not available.\"\n" +
							"}");

					channel.getCtx().writeAndFlush(disconnectPacket).channel().disconnect();
				});

		server.bind(config.server().host(), config.server().port());
	}
}
