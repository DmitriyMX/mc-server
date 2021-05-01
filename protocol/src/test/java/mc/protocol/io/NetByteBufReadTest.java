package mc.protocol.io;

import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class NetByteBufReadTest {

	private static Random random;
	private ByteArrayOutputStream baos;

	@BeforeEach
	void setUp() {
		random = new Random(System.currentTimeMillis());
		baos = new ByteArrayOutputStream();
	}

	@ParameterizedTest
	@MethodSource("paramsReadBoolean")
	void readBoolean(byte sourceByte, boolean expectedValue) {
		baos.write(sourceByte);

		NetByteBuf netByteBuf = new NetByteBuf(Unpooled.wrappedBuffer(baos.toByteArray()));

		assertEquals(expectedValue, netByteBuf.readBoolean());
	}

	@Test
	void readByte() {
		byte[] bytes = new byte[1];
		random.nextBytes(bytes);
		baos.write(bytes[0]);

		NetByteBuf netByteBuf = new NetByteBuf(Unpooled.wrappedBuffer(baos.toByteArray()));

		assertEquals(bytes[0], netByteBuf.readByte());
	}

	@ParameterizedTest
	@MethodSource("paramsReadUnsignedByte")
	void readUnsignedByte(byte sourceByte, int expectedValue) {
		baos.write(sourceByte);

		NetByteBuf netByteBuf = new NetByteBuf(Unpooled.wrappedBuffer(baos.toByteArray()));

		assertEquals(expectedValue, netByteBuf.readUnsignedByte());
	}

	@Test
	void readShort() throws IOException {
		int value = Integer.valueOf(random.nextInt()).shortValue();
		new DataOutputStream(baos).writeShort(value);

		NetByteBuf netByteBuf = new NetByteBuf(Unpooled.wrappedBuffer(baos.toByteArray()));

		assertEquals(value, netByteBuf.readShort());
	}

	@Test
	void readUnsignedShort() throws IOException {
		int value = 32768;
		new DataOutputStream(baos).writeShort(value);

		NetByteBuf netByteBuf = new NetByteBuf(Unpooled.wrappedBuffer(baos.toByteArray()));

		assertEquals(value, netByteBuf.readUnsignedShort());
	}

	@ParameterizedTest
	@MethodSource("paramsReadString")
	void readString(String string) throws IOException {
		final byte[] strBytes = string.getBytes(StandardCharsets.UTF_8);
		final byte[] bytes = new byte[strBytes.length + 1];
		bytes[0] = (byte) string.codePoints().count(); // допустим, что размер поместился в один байт
		System.arraycopy(strBytes, 0, bytes, 1, strBytes.length);

		baos.write(bytes);

		NetByteBuf netByteBuf = new NetByteBuf(Unpooled.wrappedBuffer(baos.toByteArray()));

		assertEquals(string, netByteBuf.readString());
	}

	@Test
	void readString_overSize() throws IOException {
		String string = "123";
		final byte[] strBytes = string.getBytes(StandardCharsets.UTF_8);
		final byte[] bytes = new byte[strBytes.length + 1];
		final int length = string.length();
		bytes[0] = (byte) (length + 1); // допустим, что размер поместился в один байт
		System.arraycopy(strBytes, 0, bytes, 1, strBytes.length);

		baos.write(bytes);

		NetByteBuf netByteBuf = new NetByteBuf(Unpooled.wrappedBuffer(baos.toByteArray()));

		assertThrows(DecoderException.class, () -> netByteBuf.readString(length));
	}

	@Test
	void readString_lessZero() throws IOException {
		String string = "123";
		final byte[] strBytes = string.getBytes(StandardCharsets.UTF_8);
		final byte[] bytes = new byte[strBytes.length + 5];
		bytes[0] = (byte) 0xFF;
		bytes[1] = (byte) 0xFF;
		bytes[2] = (byte) 0xFF;
		bytes[3] = (byte) 0xFF;
		bytes[4] = (byte) 0x0F;
		System.arraycopy(strBytes, 0, bytes, 5, strBytes.length);

		baos.write(bytes);

		NetByteBuf netByteBuf = new NetByteBuf(Unpooled.wrappedBuffer(baos.toByteArray()));

		assertThrows(DecoderException.class, () -> netByteBuf.readString(-1));
	}

	@ParameterizedTest
	@MethodSource("paramsReadVarInt")
	void readVarInt(byte[] sourceBytes, int expectedValue) throws IOException {
		baos.write(sourceBytes);

		NetByteBuf netByteBuf = new NetByteBuf(Unpooled.wrappedBuffer(baos.toByteArray()));

		assertEquals(expectedValue, netByteBuf.readVarInt());
	}

	@Test
	void readVarInt_tooBig() throws IOException {
		baos.write(new byte[]{ (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x0F });

		NetByteBuf netByteBuf = new NetByteBuf(Unpooled.wrappedBuffer(baos.toByteArray()));

		assertEquals(-1, netByteBuf.readVarInt());
	}

	@ParameterizedTest
	@MethodSource({"paramsReadVarInt", "paramsReadVarLong"})
	void readVarLong(byte[] sourceBytes, long expectedValue) throws IOException {
		baos.write(sourceBytes);

		NetByteBuf netByteBuf = new NetByteBuf(Unpooled.wrappedBuffer(baos.toByteArray()));

		assertEquals(expectedValue, netByteBuf.readVarLong());
	}

	@Test
	void readVarLong_tooBig() throws IOException {
		baos.write(new byte[]{ (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0x0F });

		NetByteBuf netByteBuf = new NetByteBuf(Unpooled.wrappedBuffer(baos.toByteArray()));

		assertEquals(-1, netByteBuf.readVarLong());
	}

	@Test
	void readUUID() throws IOException {
		final UUID uuid = UUID.randomUUID();
		final long mostSignificantBits = uuid.getMostSignificantBits();
		final long leastSignificantBits = uuid.getLeastSignificantBits();

		baos.write(new byte[]{
				(byte) ((mostSignificantBits >>> 56) & 0xFF),
				(byte) ((mostSignificantBits >>> 48) & 0xFF),
				(byte) ((mostSignificantBits >>> 40) & 0xFF),
				(byte) ((mostSignificantBits >>> 32) & 0xFF),
				(byte) ((mostSignificantBits >>> 24) & 0xFF),
				(byte) ((mostSignificantBits >>> 16) & 0xFF),
				(byte) ((mostSignificantBits >>> 8) & 0xFF),
				(byte) (mostSignificantBits & 0xFF)
		});
		baos.write(new byte[]{
				(byte) ((leastSignificantBits >>> 56) & 0xFF),
				(byte) ((leastSignificantBits >>> 48) & 0xFF),
				(byte) ((leastSignificantBits >>> 40) & 0xFF),
				(byte) ((leastSignificantBits >>> 32) & 0xFF),
				(byte) ((leastSignificantBits >>> 24) & 0xFF),
				(byte) ((leastSignificantBits >>> 16) & 0xFF),
				(byte) ((leastSignificantBits >>> 8) & 0xFF),
				(byte) (leastSignificantBits & 0xFF)
		});

		NetByteBuf netByteBuf = new NetByteBuf(Unpooled.wrappedBuffer(baos.toByteArray()));

		assertEquals(uuid, netByteBuf.readUUID());
	}

	@Test
	void readBytes() throws IOException {
		byte[] bytes = new byte[128];
		random.nextBytes(bytes);
		baos.write(bytes);

		byte[] actualBytes = new byte[128];
		NetByteBuf netByteBuf = new NetByteBuf(Unpooled.wrappedBuffer(baos.toByteArray()));

		assertEquals(bytes.length, netByteBuf.readableBytes());

		netByteBuf.readBytes(actualBytes);

		assertArrayEquals(bytes, actualBytes);
		assertEquals(0, netByteBuf.readableBytes());
	}

	@Test
	void read_offset() throws IOException {
		byte[] bytes = new byte[128];
		random.nextBytes(bytes);
		baos.write(bytes);

		byte[] buff = new byte[128];
		NetByteBuf netByteBuf = new NetByteBuf(Unpooled.wrappedBuffer(baos.toByteArray()));
		netByteBuf.readBytes(buff, 3, 11);

		byte[] expectedBytes = new byte[11];
		System.arraycopy(bytes, 0, expectedBytes, 0, 11);
		byte[] actualBytes = new byte[11];
		System.arraycopy(buff, 3, actualBytes, 0, 11);

		assertArrayEquals(expectedBytes, actualBytes);
	}

	@SuppressWarnings("unused")
	private static Stream<Arguments> paramsReadBoolean() {
		return Stream.of(
				Arguments.of((byte) 0x00, false),
				Arguments.of((byte) 0x01, true)
		);
	}

	@SuppressWarnings("unused")
	private static Stream<Arguments> paramsReadUnsignedByte() {
		return Stream.of(
				Arguments.of((byte) 30, 30),
				Arguments.of((byte) (0xFF & 130), 130)
		);
	}

	@SuppressWarnings("unused")
	private static Stream<Arguments> paramsReadString() {
		return Stream.of(
				Arguments.of(""),
				Arguments.of("Latin"),
				Arguments.of("Кириллица"),
				Arguments.of("العربية"),
				Arguments.of("ﬦﬣﬡ"), // Алфавитные формы представления
				Arguments.of("\uD800\uDD07") // Эгейские цифры, [один]
		);
	}

	@SuppressWarnings("unused")
	private static Stream<Arguments> paramsReadVarInt() {
		return Stream.of(
				Arguments.of(new byte[]{ 0x78 }, 120),
				Arguments.of(new byte[]{ (byte) 0xE0, 0x5D }, 12000),
				Arguments.of(new byte[]{ (byte) 0xC0, (byte) 0xA9, 0x07 }, 120000),
				Arguments.of(new byte[]{ (byte) 0x80, (byte) 0x9C, (byte) 0x9C, (byte) 0x39 }, 120_000_000),
				Arguments.of(new byte[]{ (byte) 0x80, (byte) 0x98, (byte) 0x9A, (byte) 0xBC, 0x04 }, 1_200_000_000)
		);
	}

	@SuppressWarnings("unused")
	private static Stream<Arguments> paramsReadVarLong() {
		return Stream.of(
				Arguments.of(
						new byte[]{ (byte) 0x80, (byte) 0xF0, (byte) 0x85, (byte) 0xDA, 0x2C },
						12_000_000_000L),
				Arguments.of(
						new byte[]{ (byte) 0x80, (byte) 0xE0, (byte) 0xBA, (byte) 0x84, (byte) 0xBF, 0x03 },
						120_000_000_000L),
				Arguments.of(
						new byte[]{ (byte) 0x80, (byte) 0x80, (byte) 0xF3, (byte) 0xBD, (byte) 0x9F, (byte) 0xDD,
								0x02 },
						12_000_000_000_000L),
				Arguments.of(
						new byte[]{ (byte) 0x80, (byte) 0x80, (byte) 0xEC, (byte) 0xAD, (byte) 0xCC, (byte) 0xEC,
								(byte) 0x90, 0x02},
						1_200_000_000_000_000L),
				Arguments.of(
						new byte[]{ (byte) 0x80, (byte) 0x80, (byte) 0xB0, (byte) 0xE8, (byte) 0xD3, (byte) 0xEB,
								(byte) 0x94, (byte) 0xD5, 0x01 },
						120_000_000_000_000_000L),
				Arguments.of(
						new byte[]{ (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80,
								(byte) 0x80, (byte) 0x80, (byte) 0x80, 0x01 },
						Long.MIN_VALUE)
		);
	}
}