package mc.server.network.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import mc.protocol.packets.client.StatusServerRequest;
import mc.protocol.packets.server.StatusServerResponse;

@Slf4j
public class StatusHandler extends AbstractPacketHandler<StatusServerRequest> {

	@Override
	protected void channelRead1(ChannelHandlerContext ctx, StatusServerRequest packet) {
		log.info("{}", packet);

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

		ctx.channel().writeAndFlush(response).channel().disconnect();
	}
}
