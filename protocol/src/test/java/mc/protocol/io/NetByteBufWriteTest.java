package mc.protocol.io;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class NetByteBufWriteTest {

	private static Random random;

	@BeforeAll
	static void setUp() {
		random = new Random(System.currentTimeMillis());
	}

	@ParameterizedTest
	@MethodSource("paramsWriteBoolean")
	void writeBoolean(boolean sourceValue, byte expectedByte) {
		ByteBuf byteBuf = Unpooled.buffer();
		NetByteBuf netByteBuf = new NetByteBuf(byteBuf);

		netByteBuf.writeBoolean(sourceValue);

		assertEquals(expectedByte, byteBuf.array()[0]);
	}

	@ParameterizedTest
	@MethodSource("paramsWriteByte")
	void writeByte(byte sourceValue, byte expectedByte) {
		ByteBuf byteBuf = Unpooled.buffer();
		NetByteBuf netByteBuf = new NetByteBuf(byteBuf);

		netByteBuf.writeByte(sourceValue);

		assertEquals(expectedByte, byteBuf.array()[0]);
	}

	@ParameterizedTest
	@MethodSource("paramsWriteString")
	void writeString(String string, int exceptedLength) {
		ByteBuf byteBuf = Unpooled.buffer();
		NetByteBuf netByteBuf = new NetByteBuf(byteBuf);

		netByteBuf.writeString(string);

		byte[] actualArray = netByteBuf.copy(0, netByteBuf.readableBytes()).array();
		int actualLength = actualArray[0]; // допустим, что размер поместился в один байт
		assertEquals(exceptedLength, actualLength);

		byte[] dataBytes = new byte[actualArray.length - 1];
		System.arraycopy(actualArray, 1, dataBytes, 0, dataBytes.length);
		assertEquals(string, new String(dataBytes, StandardCharsets.UTF_8));
	}

	//возможно этот тест нужно перенести в NetByteBufReadTest
	@Test
	void writeString_overSize() {
		String overSizeString = RandomStringUtils.randomAscii(Short.MAX_VALUE + Short.MAX_VALUE);

		ByteBuf byteBuf = Unpooled.buffer();
		NetByteBuf netByteBuf = new NetByteBuf(byteBuf);

		netByteBuf.writeString(overSizeString);

		NetByteBuf netByteBuf2 = new NetByteBuf(byteBuf.copy());
		String actualString = netByteBuf2.readString();

		String expectedString = overSizeString.substring(0, Short.MAX_VALUE);

		assertEquals(expectedString, actualString);
	}

	@ParameterizedTest
	@MethodSource("paramsWriteVarInt")
	void writeVarInt(int sourceValue, byte[] expectedBytes) {
		ByteBuf byteBuf = Unpooled.buffer();
		NetByteBuf netByteBuf = new NetByteBuf(byteBuf);

		netByteBuf.writeVarInt(sourceValue);
		byte[] actualArray = netByteBuf.copy(0, netByteBuf.readableBytes()).array();

		assertArrayEquals(expectedBytes, actualArray);
	}

	@ParameterizedTest
	@MethodSource({ "paramsWriteVarInt", "paramsWriteVarLong" })
	void writeVarLong(long sourceValue, byte[] expectedBytes) {
		ByteBuf byteBuf = Unpooled.buffer();
		NetByteBuf netByteBuf = new NetByteBuf(byteBuf);

		netByteBuf.writeVarLong(sourceValue);
		byte[] actualArray = netByteBuf.copy(0, netByteBuf.readableBytes()).array();

		assertArrayEquals(expectedBytes, actualArray);
	}

	@Test
	void writeUUID() {
		final UUID uuid = UUID.randomUUID();

		ByteBuf byteBuf = Unpooled.buffer();
		NetByteBuf netByteBuf = new NetByteBuf(byteBuf);

		netByteBuf.writeUUID(uuid);

		final long mostSignificantBits = uuid.getMostSignificantBits();
		final long leastSignificantBits = uuid.getLeastSignificantBits();

		byte[] actualArray = netByteBuf.copy(0, netByteBuf.readableBytes()).array();

		assertArrayEquals(new byte[]{
						(byte) ((mostSignificantBits >>> 56) & 0xFF),
						(byte) ((mostSignificantBits >>> 48) & 0xFF),
						(byte) ((mostSignificantBits >>> 40) & 0xFF),
						(byte) ((mostSignificantBits >>> 32) & 0xFF),
						(byte) ((mostSignificantBits >>> 24) & 0xFF),
						(byte) ((mostSignificantBits >>> 16) & 0xFF),
						(byte) ((mostSignificantBits >>> 8) & 0xFF),
						(byte) (mostSignificantBits & 0xFF),

						(byte) ((leastSignificantBits >>> 56) & 0xFF),
						(byte) ((leastSignificantBits >>> 48) & 0xFF),
						(byte) ((leastSignificantBits >>> 40) & 0xFF),
						(byte) ((leastSignificantBits >>> 32) & 0xFF),
						(byte) ((leastSignificantBits >>> 24) & 0xFF),
						(byte) ((leastSignificantBits >>> 16) & 0xFF),
						(byte) ((leastSignificantBits >>> 8) & 0xFF),
						(byte) (leastSignificantBits & 0xFF) },
				actualArray);
	}

	@Test
	void writeBytes() {
		byte[] bytes = new byte[128];
		random.nextBytes(bytes);

		ByteBuf byteBuf = Unpooled.buffer();
		NetByteBuf netByteBuf = new NetByteBuf(byteBuf);

		netByteBuf.writeBytes(bytes);
		byte[] actualArray = netByteBuf.copy(0, netByteBuf.readableBytes()).array();

		assertArrayEquals(bytes, actualArray);
	}

	@Test
	void write_offset() {
		byte[] bytes = new byte[128];
		random.nextBytes(bytes);

		ByteBuf byteBuf = Unpooled.buffer();
		NetByteBuf netByteBuf = new NetByteBuf(byteBuf);

		netByteBuf.writeBytes(bytes, 3, 11);

		byte[] actualBytes = new byte[11];
		System.arraycopy(byteBuf.array(), 0, actualBytes, 0, 11);

		byte[] expectedBytes = new byte[11];
		System.arraycopy(bytes, 3, expectedBytes, 0, 11);

		assertArrayEquals(expectedBytes, actualBytes);
	}

	@SuppressWarnings("unused")
	private static Stream<Arguments> paramsWriteBoolean() {
		return Stream.of(
				Arguments.of(false, (byte) 0x00),
				Arguments.of(true, (byte) 0x01)
		);
	}

	@SuppressWarnings("unused")
	private static Stream<Arguments> paramsWriteByte() {
		byte b = Integer.valueOf(random.nextInt()).byteValue();

		return Stream.of(
				Arguments.of(b, b),
				Arguments.of((byte) 128, (byte) -128)
		);
	}

	@SuppressWarnings("unused")
	private static Stream<Arguments> paramsWriteString() {
		return Stream.of(
				Arguments.of("", 0),
				Arguments.of("Latin", 5),
				Arguments.of("Кириллица", 37),
				// (9) -> "РљРёСЂРёР»Р»РёС†Р°"(18) => 18*2=36 (37?)
				Arguments.of("العربية", 30),
				// (7) -> "Ш§Щ„Ш№Ш±ШЁЩЉШ©"(14) => 14*2=28 (30?)
				Arguments.of("ﬦﬣﬡ", 18), // Алфавитные формы представления
				// (3) -> "п¬¦п¬Јп¬Ў"(9) => 9*2=18
				Arguments.of("\uD800\uDD07", 4) // Эгейские цифры, [один]
				// (1) -> "𐄇" => ...4!
		);
	}

	@SuppressWarnings("unused")
	private static Stream<Arguments> paramsWriteVarInt() {
		return Stream.of(
				Arguments.of(120, new byte[]{ 0x78 }),
				Arguments.of(12000, new byte[]{ (byte) 0xE0, 0x5D }),
				Arguments.of(120000, new byte[]{ (byte) 0xC0, (byte) 0xA9, 0x07 }),
				Arguments.of(120000000, new byte[]{ (byte) 0x80, (byte) 0x9C, (byte) 0x9C, (byte) 0x39 }),
				Arguments.of(1200000000, new byte[]{ (byte) 0x80, (byte) 0x98, (byte) 0x9A, (byte) 0xBC, 0x04 })
		);
	}

	@SuppressWarnings("unused")
	private static Stream<Arguments> paramsWriteVarLong() {
		return Stream.of(
				Arguments.of(
						12_000_000_000L,
						new byte[]{ (byte) 0x80, (byte) 0xF0, (byte) 0x85, (byte) 0xDA, 0x2C }),
				Arguments.of(
						120_000_000_000L,
						new byte[]{ (byte) 0x80, (byte) 0xE0, (byte) 0xBA, (byte) 0x84, (byte) 0xBF, 0x03 }),
				Arguments.of(
						12_000_000_000_000L,
						new byte[]{ (byte) 0x80, (byte) 0x80, (byte) 0xF3, (byte) 0xBD, (byte) 0x9F, (byte) 0xDD,
								0x02 }),
				Arguments.of(
						1_200_000_000_000_000L,
						new byte[]{ (byte) 0x80, (byte) 0x80, (byte) 0xEC, (byte) 0xAD, (byte) 0xCC, (byte) 0xEC,
								(byte) 0x90, 0x02 }),
				Arguments.of(
						120_000_000_000_000_000L,
						new byte[]{ (byte) 0x80, (byte) 0x80, (byte) 0xB0, (byte) 0xE8, (byte) 0xD3, (byte) 0xEB,
								(byte) 0x94, (byte) 0xD5, 0x01 }),
				Arguments.of(
						Long.MIN_VALUE,
						new byte[]{ (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80,
								(byte) 0x80, (byte) 0x80, (byte) 0x80, 0x01 })
		);
	}
}
