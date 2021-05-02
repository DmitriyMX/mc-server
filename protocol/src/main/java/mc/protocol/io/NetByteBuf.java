package mc.protocol.io;

import io.netty.buffer.ByteBuf;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Компонент чтения и записи данных протокола.
 *
 * <p>Data types</p>
 * <pre>
 * | TYPE           | SIZE (bytes)          | ENCODING                                            | NOTES                                                                    |
 * |----------------|-----------------------|-----------------------------------------------------|--------------------------------------------------------------------------|
 * | Boolean        | 1                     | True или False                                      | True = 0x01; False = 0x00                                                |
 * | Byte           | 1                     | Число от -128 до 127                                | 8-bit число со знаком                                                    |
 * | Unsigned Byte  | 1                     | Число от 0 до 255                                   | 8-bit без знаковое число                                                 |
 * | Short          | 2                     | Число от -32768 до 32767                            | 16-bit число со знаком                                                   |
 * | Unsigned Short | 2                     | Число от -32768 до 32767                            | 16-bit без знаковое число                                                |
 * | Int            | 4                     | Число от -2147483648 и 2147483647                   | 32-bit число со знаком                                                   |
 * | Long           | 8                     | Число от -9223372036854775808 и 9223372036854775807 | 64-bit число со знаком                                                   |
 * | Float          | 4                     | 32-bit число одинарной точности (IEEE 754-2008)     | [1]                                                                      |
 * | Double         | 8                     | 64-bit число одинарной точности (IEEE 754-2008)     | [2]                                                                      |
 * | String (n)     | >= 1 ; <= (n * 4) + 3 | Последовательность Unicode scalar values            | В начале пишется длина строки в VarInt, после чего записываются символы. |
 * |                |                       |                                                     | Каждый символ может состоять максимум из 4 байт. [3]                     |
 * |                |                       |                                                     | Максимальная длина строки - 32767 (3 - это как раз размер VarInt для     |
 * |                |                       |                                                     | этого числа).                                                            |
 * | VarInt         | >= 1 ; <= 5           | Число от -2147483648 и 2147483647                   | 32-bit число с плавающей размерностью от 1 до 5 байт                     |
 * | VarLong        | >= 1 ; <= 10          | Число от -9223372036854775808 и 9223372036854775807 | 64-bit число с плавающей размерностью от 1 до 10 байт                    |
 *
 * [1] - <a href="https://en.wikipedia.org/wiki/Single-precision_floating-point_format">Single-precision floating-point format</a>
 * [2] - <a href="https://en.wikipedia.org/wiki/Double-precision_floating-point_format">Double-precision floating-point format</a>
 * [3] - <a href="http://unicode.org/glossary/#unicode_scalar_value">Unicode Scalar Value</a>
 * </pre>
 *
 * @see <a href="https://wiki.vg/index.php?title=Protocol&oldid=7368#Data_types">Data types</a>
 */
@Slf4j
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class NetByteBuf extends ByteBuf {

	@Delegate
	private final ByteBuf byteBuf;

	public void writeUnsignedByte(int value) {
		byteBuf.writeByte((byte)(value & 0xFF));
	}

	//region String
	public String readString() {
		return readString(Short.MAX_VALUE);
	}

	@SuppressWarnings("java:S131")
	public String readString(int maxLength) {
		int length = readVarInt();

		if (length == 0) {
			return "";
		} else if (length > maxLength) {
			throw new DecoderException("String length exceeds maximum length: " + length + " > " + maxLength);
		} else if (length < 0) {
			throw new DecoderException("String length less zero!");
		}

		byte[] bytes = new byte[length * 4];
		int readbleBytes = 0;
		for (int i = 0; i < length && readableBytes() > 0; i++) {
			byte b = readByte();
			bytes[readbleBytes++] = b;

			switch ((b & 0xFF) >> 4) {
				case 0b1100:
				case 0b1101:
					bytes[readbleBytes++] = readByte();
					break;
				case 0b1110:
					bytes[readbleBytes++] = readByte();
					bytes[readbleBytes++] = readByte();
					break;
				case 0b1111:
					bytes[readbleBytes++] = readByte();
					bytes[readbleBytes++] = readByte();
					bytes[readbleBytes++] = readByte();
					break;
			}
		}

		return new String(bytes, 0, readbleBytes, StandardCharsets.UTF_8);
	}

	public void writeString(String string) {
		byte[] buf = string.getBytes(StandardCharsets.UTF_8);

		if (buf.length > Short.MAX_VALUE) {
			log.warn("String is too long: {} > {}", buf.length, Short.MAX_VALUE);
			writeVarInt(Short.MAX_VALUE);
			writeBytes(buf, 0, Short.MAX_VALUE);
		} else {
			writeVarInt(buf.length);
			writeBytes(buf);
		}
	}
	//endregion

	//region VarInt
	public int readVarInt() {
		int numRead = 0;
		int result = 0;
		byte read;
		do {
			if ((numRead + 1) > 5) {
				log.warn("VarInt is too big");
				break;
			}
			read = readByte();
			int value = (read & 0b01111111);
			result |= (value << (7 * numRead));

			numRead++;
		} while ((read & 0b10000000) != 0);

		return result;
	}

	public void writeVarInt(int value) {
		while ((value & -128) != 0) {
			writeByte(value & 127 | 128);
			value >>>= 7;
		}

		writeByte(value);
	}
	//endregion

	//region VarLong
	public long readVarLong() {
		int numRead = 0;
		long result = 0L;
		byte read;
		do {
			if (numRead > 10) {
				log.warn("VarLong is too big");
				break;
			}

			read = readByte();
			long value = (read & 0b01111111);
			result |= (value << (7 * numRead));

			numRead++;
		} while ((read & 0b10000000) != 0);

		return result;
	}

	public void writeVarLong(long value) {
		while ((value & -128L) != 0L) {
			writeByte((int) (value & 127L) | 128);
			value >>>= 7;
		}

		writeByte((int) value);
	}
	//endregion

	//region UUID
	public UUID readUUID() {
		return new UUID(readLong(), readLong());
	}

	public void writeUUID(UUID uuid) {
		writeLong(uuid.getMostSignificantBits());
		writeLong(uuid.getLeastSignificantBits());
	}
	//endregion
}
