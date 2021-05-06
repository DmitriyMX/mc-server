package mc.server;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mc.protocol.*;
import mc.protocol.api.ConnectionContext;
import mc.protocol.model.Location;
import mc.protocol.model.Look;
import mc.protocol.model.ServerInfo;
import mc.protocol.packets.PingPacket;
import mc.protocol.packets.client.HandshakePacket;
import mc.protocol.packets.client.LoginStartPacket;
import mc.protocol.packets.client.StatusServerRequestPacket;
import mc.protocol.packets.server.*;
import mc.protocol.serializer.TextSerializer;
import mc.protocol.utils.Difficulty;
import mc.protocol.utils.GameMode;
import mc.protocol.utils.LevelType;
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

	private final Random random = new Random(System.currentTimeMillis());
	private final Config config;

	public void onHandshake(ConnectionContext<HandshakePacket> context) {
		context.setState(context.clientPacket().getNextState());
	}

	public void onKeepAlive(ConnectionContext<PingPacket> context) {
		context.sendNow(context.clientPacket());
		context.disconnect();
	}

	public void onKeepAlivePlay(ConnectionContext<PingPacket> context) {
		context.sendNow(context.clientPacket());
	}

	public void onServerStatus(ConnectionContext<StatusServerRequestPacket> context) {
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

		context.sendNow(response);
	}

	public void onLoginStart(ConnectionContext<LoginStartPacket> context) {
		LoginStartPacket loginStartPacket = context.clientPacket();

		var loginSuccessPacket = new LoginSuccessPacket();
		loginSuccessPacket.setUuid(UUID.randomUUID());
		loginSuccessPacket.setName(loginStartPacket.getName());

		context.sendNow(loginSuccessPacket);
		context.setState(State.PLAY);

		var joinGamePacket = new JoinGamePacket();
		joinGamePacket.setEntityId(random.nextInt());
		joinGamePacket.setGameMode(GameMode.SPECTATOR);
		joinGamePacket.setDimension(0/*Overworld*/);
		joinGamePacket.setDifficulty(Difficulty.PEACEFUL);
		joinGamePacket.setLevelType(LevelType.FLAT);

		context.send(joinGamePacket);

		Location spawnLocation = new Location(0d, 63d, 0d);

		var spawnPositionPacket = new SpawnPositionPacket();
		spawnPositionPacket.setSpawn(spawnLocation);

		context.send(spawnPositionPacket);

		var playerAbilitiesPacket = new PlayerAbilitiesPacket();
		playerAbilitiesPacket.setCatFly(true);
		playerAbilitiesPacket.setFlying(true);
		playerAbilitiesPacket.setCreativeMode(false);
		playerAbilitiesPacket.setInvulnerable(true);
		playerAbilitiesPacket.setFieldOfView(0.0f);
		playerAbilitiesPacket.setFlyingSpeed(0.05f);

		context.send(playerAbilitiesPacket);

		context.flushSending();

		var chunkDataPacket = new ChunkDataPacket();
		chunkDataPacket.setX(0);
		chunkDataPacket.setZ(0);

		context.sendNow(chunkDataPacket);

		var playerPositionAndLookPacket = new SPlayerPositionAndLookPacket();
		playerPositionAndLookPacket.setPosition(spawnLocation);
		playerPositionAndLookPacket.setLook(new Look(0f, 0f));
		playerPositionAndLookPacket.setTeleportId(random.nextInt());

		context.send(playerPositionAndLookPacket);

		PingPacket pingPacket = new PingPacket();
		pingPacket.setPayload(System.currentTimeMillis());

		context.send(pingPacket);

		context.flushSending();
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
