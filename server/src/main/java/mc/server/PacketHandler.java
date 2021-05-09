package mc.server;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mc.protocol.*;
import mc.protocol.api.ConnectionContext;
import mc.protocol.model.Location;
import mc.protocol.model.Look;
import mc.protocol.model.ServerInfo;
import mc.protocol.packets.KeepAlivePacket;
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
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class PacketHandler {

	private final Random random = new Random(System.currentTimeMillis());
	private final Config config;

	public void onHandshake(ConnectionContext context, HandshakePacket packet) {
		context.setState(packet.getNextState());
	}

	public void onKeepAlive(ConnectionContext context, KeepAlivePacket packet) {
		context.sendNow(packet);
		context.disconnect();
	}

	public void onKeepAlivePlay(ConnectionContext context, KeepAlivePacket packet) {
		try {
			TimeUnit.MILLISECONDS.sleep(50);
			context.sendNow(packet);
		} catch (InterruptedException e) {
			if (log.isTraceEnabled()) {
				log.trace("{}", e.getMessage(), e);
			}
		}
	}

	@SuppressWarnings("unused")
	public void onServerStatus(ConnectionContext context, StatusServerRequestPacket packet) {
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

	public void onLoginStart(ConnectionContext context, LoginStartPacket loginStartPacket) {
		var loginSuccessPacket = new LoginSuccessPacket();
		loginSuccessPacket.setUuid(UUID.randomUUID());
		loginSuccessPacket.setName(loginStartPacket.getName());

		context.sendNow(loginSuccessPacket);
		context.setState(State.PLAY);

		var joinGamePacket = new JoinGamePacket();
		joinGamePacket.setEntityId(random.nextInt());
		joinGamePacket.setGameMode(GameMode.SURVIVAL);
		joinGamePacket.setDimension(0/*Overworld*/);
		joinGamePacket.setDifficulty(Difficulty.PEACEFUL);
		joinGamePacket.setLevelType(LevelType.FLAT);

		context.send(joinGamePacket);

		Location spawnLocation = new Location(7d, 130d, 7d);

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

		KeepAlivePacket keepAlivePacket = new KeepAlivePacket();
		keepAlivePacket.setPayload(System.currentTimeMillis());

		context.send(keepAlivePacket);

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
