package mc.server;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mc.protocol.*;
import mc.protocol.model.Location;
import mc.protocol.model.ServerInfo;
import mc.protocol.packets.PingPacket;
import mc.protocol.packets.client.HandshakePacket;
import mc.protocol.packets.client.LoginStartPacket;
import mc.protocol.packets.client.StatusServerRequestPacket;
import mc.protocol.packets.server.JoinGamePacket;
import mc.protocol.packets.server.LoginSuccessPacket;
import mc.protocol.packets.server.SpawnPositionPacket;
import mc.protocol.packets.server.StatusServerResponse;
import mc.protocol.serializer.TextSerializer;
import mc.server.config.Config;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Collections;
import java.util.Random;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class PacketHandler {

	private final Config config;
	private final Random random = new Random(System.currentTimeMillis());

	public void onHandshake(ChannelContext<HandshakePacket> channel) {
		channel.setState(channel.getPacket().getNextState());
	}

	public void onKeepAlive(ChannelContext<PingPacket> channel) {
		channel.getCtx().writeAndFlush(channel.getPacket()).channel().disconnect();
	}

	public void onServerStatus(ChannelContext<StatusServerRequestPacket> channel) {
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
	}

	public void onLoginStart(ChannelContext<LoginStartPacket> channel) {
		LoginStartPacket loginStartPacket = channel.getPacket();

		LoginSuccessPacket loginSuccessPacket = new LoginSuccessPacket();
		loginSuccessPacket.setUuid(UUID.randomUUID());
		loginSuccessPacket.setName(loginStartPacket.getName());

		log.info("{}", loginSuccessPacket);
		channel.getCtx().writeAndFlush(loginSuccessPacket);
		channel.setState(State.PLAY);

		JoinGamePacket joinGamePacket = new JoinGamePacket();
		joinGamePacket.setEntityId(random.nextInt());
		joinGamePacket.setGameMode(GameMode.SPECTATOR);
		joinGamePacket.setDimension(0/*Overworld*/);
		joinGamePacket.setDifficulty(Difficulty.PEACEFUL);
		joinGamePacket.setLevelType(LevelType.FLAT);

		log.info("{}", joinGamePacket);
		channel.getCtx().write(joinGamePacket);

		SpawnPositionPacket spawnPositionPacket = new SpawnPositionPacket();
		spawnPositionPacket.setSpawn(new Location(0d, 63d, 0d));

		log.info("{}", spawnPositionPacket);
		channel.getCtx().write(spawnPositionPacket);

		channel.getCtx().flush();
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
