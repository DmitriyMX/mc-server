package mc.protocol.packets.server;

import io.netty.buffer.Unpooled;
import lombok.Data;
import mc.protocol.io.NetByteBuf;
import mc.protocol.packets.ServerSidePacket;

/**
 * Данные чанка.
 *
 * <p>Структура пакета</p>
 * <pre>
 * | FIELD                    | TYPE         | NOTES                                                                              |
 * |--------------------------|------------- |------------------------------------------------------------------------------------|
 * | Chunk X                  | Integer      | Координаты чанка (координата блока, делённая на 16, округленная в меньшую сторону) |
 * | Chunk Z                  | Integer      | Координаты чанка (координата блока, делённая на 16, округленная в меньшую сторону) |
 * | Ground-Up Continuous     | Boolean      | См. Chunk Format                                                                   |
 * | Primary Bit Mask         | VarInt       | Битовая маска, где каждый бит - это часть чанка (0-15)                             |
 * | Size of Data             | VarInt       | Размер поля "Data"                                                                 |
 * | Data                     | Byte array   | Данные чанка. См. Chunk Format                                                     |
 * | Number of block entities | VarInt       | Количество элементов в поле "Block entities"                                       |
 * | Block entities           | Array of NBT | Все сущности в чанке                                                               |
 * </pre>
 *
 * @see <a href="https://wiki.vg/index.php?title=Protocol&oldid=14204#Chunk_Data">Chunk Data</a>
 * @see <a href="https://wiki.vg/index.php?title=Chunk_Format&oldid=14135">Chunk Format</a>
 */
@Data
public class ChunkDataPacket implements ServerSidePacket {

	private int x;
	private int z;

	@Override
	public void writeSelf(NetByteBuf netByteBuf) {
		netByteBuf.writeInt(x);
		netByteBuf.writeInt(z);
		netByteBuf.writeBoolean(true); // Ground-Up Continuous
		netByteBuf.writeVarInt(0b11111111); // Primary Bit Mask

		NetByteBuf data = new NetByteBuf(Unpooled.buffer());
		// <Data>
		for (int i = 0; i < 16; i++) {
			NetByteBuf dataBuff = new NetByteBuf(Unpooled.buffer(4096));
			NetByteBuf blockLight = new NetByteBuf(Unpooled.buffer(2048));
			NetByteBuf skyLight = new NetByteBuf(Unpooled.buffer(2048));
			NetByteBuf biomes = new NetByteBuf(Unpooled.buffer(256));


			// <Chunk Section>
			data.writeUnsignedByte(13); // Bits Per Block
			//   <Palette>
			data.writeUnsignedByte(0); // Palette Length (for direct)
			//     <Palette Data/>
			//   </Palette>
			data.writeVarInt(dataBuff.readableBytes()); // Data Array Length
			data.writeBytes(dataBuff); // Data Array
			data.writeBytes(blockLight); // Block Light
			data.writeBytes(skyLight); // Sky Light
			// </Chunk Section>
			data.writeBytes(biomes); // Biomes
		}
		// </Data>

		netByteBuf.writeVarInt(data.readableBytes()); // Size of Data
		netByteBuf.writeBytes(data); // Data
		netByteBuf.writeVarInt(0); // Number of block entities
		/* write NBT's */
	}
}
