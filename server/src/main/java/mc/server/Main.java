package mc.server;

import lombok.extern.slf4j.Slf4j;
import mc.protocol.NettyServer;
import mc.protocol.packets.PingPacket;
import mc.protocol.packets.client.HandshakePacket;
import mc.protocol.packets.client.LoginStartPacket;
import mc.protocol.packets.client.StatusServerRequest;
import mc.protocol.packets.server.DisconnectPacket;
import mc.protocol.packets.server.StatusServerResponse;

@Slf4j
public class Main {

	public static void main(String[] args) {
		log.info("hello");

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
							"    \"name\": \"1.12.2\",\n" +
							"    \"protocol\": 340\n" +
							"  },\n" +
							"  \"players\": {\n" +
							"    \"max\": 0,\n" +
							"    \"online\": 0,\n" +
							"    \"sample\": []\n" +
							"  },\n" +
							"  \"description\": {\n" +
							"    \"text\": \"Hello world\"\n" +
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

		server.bind("127.0.0.1", 25565);
	}
}
