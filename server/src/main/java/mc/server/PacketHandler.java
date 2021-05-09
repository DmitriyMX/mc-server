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
import mc.protocol.world.Chunk;
import mc.protocol.world.World;
import mc.server.config.Config;
import mc.server.service.PlayerManager;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class PacketHandler {

	private final Random random = new Random(System.currentTimeMillis());
	private final Config config;
	private final World world;
	private final PlayerManager playerManager;

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
		if (config.players().fakeOnline().enable()) {
			serverInfo.players().online(config.players().fakeOnline().value());
		} else {
			serverInfo.players().online(playerManager.online());
		}
		serverInfo.players().sample(Collections.emptyList());
		serverInfo.description(TextSerializer.fromPlain(config.motd()));

		if (config.iconPath() != null) {
			serverInfo.favicon(faviconToBase64(config.iconPath()));
		}

		StatusServerResponse response = new StatusServerResponse();
		response.setInfo(serverInfo);

		context.sendNow(response);
	}

	@SuppressWarnings("java:S2589")
	public void onLoginStart(ConnectionContext context, LoginStartPacket loginStartPacket) {
		Player player = playerManager.addAndCreate(context, loginStartPacket.getName(), GameMode.SURVIVAL, world.getSpawn());
		context.setCustomProperty("player", player);

		var loginSuccessPacket = new LoginSuccessPacket();
		loginSuccessPacket.setUuid(player.getUuid());
		loginSuccessPacket.setName(player.getName());

		context.sendNow(loginSuccessPacket);
		context.setState(State.PLAY);

		var joinGamePacket = new JoinGamePacket();
		joinGamePacket.setEntityId(random.nextInt());
		joinGamePacket.setGameMode(player.getGameMode());
		joinGamePacket.setDimension(0/*Overworld*/);
		joinGamePacket.setDifficulty(Difficulty.PEACEFUL);
		joinGamePacket.setLevelType(world.getLevelType());

		context.send(joinGamePacket);

		var spawnPositionPacket = new SpawnPositionPacket();
		spawnPositionPacket.setSpawn(player.getLocation());

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

		Location chunkLocation = player.getLocation().toChunkXZ();
		Chunk chunk = world.getChunk(chunkLocation.getIntX(), chunkLocation.getIntZ());

		var chunkDataPacket = new ChunkDataPacket();
		chunkDataPacket.setX(chunk.getX());
		chunkDataPacket.setZ(chunk.getZ());

		context.send(chunkDataPacket);

		for (int i = 1; i <= config.world().viewDistance(); i++) {
			int minX = chunkLocation.getIntX() - i;
			int minZ = chunkLocation.getIntZ() - i;
			int maxX = chunkLocation.getIntX() + i;
			int maxZ = chunkLocation.getIntZ() + i;

			for (int z = minZ; z <= maxZ; z++) {
				for (int x = minX; x <= maxX; x++) {
					if ((z == minZ || z == maxZ) || (x == minX || x == maxX)) {
						chunkDataPacket = new ChunkDataPacket();
						chunkDataPacket.setX(x);
						chunkDataPacket.setZ(z);

						context.send(chunkDataPacket);
					}
				}
			}
		}

		context.flushSending();

		var playerPositionAndLookPacket = new SPlayerPositionAndLookPacket();
		playerPositionAndLookPacket.setPosition(player.getLocation());
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
