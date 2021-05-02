package mc.server;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mc.protocol.*;
import mc.protocol.model.Location;
import mc.protocol.model.Look;
import mc.protocol.model.ServerInfo;
import mc.protocol.packets.PingPacket;
import mc.protocol.packets.client.HandshakePacket;
import mc.protocol.packets.client.LoginStartPacket;
import mc.protocol.packets.client.StatusServerRequestPacket;
import mc.protocol.packets.server.*;
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

		var loginSuccessPacket = new LoginSuccessPacket();
		loginSuccessPacket.setUuid(UUID.randomUUID());
		loginSuccessPacket.setName(loginStartPacket.getName());

		channel.getCtx().writeAndFlush(loginSuccessPacket);
		channel.setState(State.PLAY);

		var joinGamePacket = new JoinGamePacket();
		joinGamePacket.setEntityId(random.nextInt());
		joinGamePacket.setGameMode(GameMode.SPECTATOR);
		joinGamePacket.setDimension(0/*Overworld*/);
		joinGamePacket.setDifficulty(Difficulty.PEACEFUL);
		joinGamePacket.setLevelType(LevelType.FLAT);

		channel.getCtx().write(joinGamePacket);

		Location spawnLocation = new Location(0d, 63d, 0d);

		var spawnPositionPacket = new SpawnPositionPacket();
		spawnPositionPacket.setSpawn(spawnLocation);

		channel.getCtx().write(spawnPositionPacket);

		var playerAbilitiesPacket = new PlayerAbilitiesPacket();
		playerAbilitiesPacket.setCatFly(true);
		playerAbilitiesPacket.setFlying(true);
		playerAbilitiesPacket.setCreativeMode(false);
		playerAbilitiesPacket.setInvulnerable(true);
		playerAbilitiesPacket.setFieldOfView(0.0f);
		playerAbilitiesPacket.setFlyingSpeed(0.05f);

		channel.getCtx().write(playerAbilitiesPacket);

		channel.getCtx().flush();

		var playerPositionAndLookPacket = new SPlayerPositionAndLookPacket();
		playerPositionAndLookPacket.setPosition(spawnLocation);
		playerPositionAndLookPacket.setLook(new Look(0f, 0f));
		playerPositionAndLookPacket.setTeleportId(random.nextInt());

		channel.getCtx().writeAndFlush(playerPositionAndLookPacket);
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
