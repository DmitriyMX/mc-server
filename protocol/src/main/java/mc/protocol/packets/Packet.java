package mc.protocol.packets;

/**
 * Пакет.
 *
 * <p>Структура пакета</p>
 * <pre>
 * | FIELD       | TYPE   | NOTES                                     |
 * |-------------|--------|-------------------------------------------|
 * | SIZE        | VarInt | = sizeOf(PACKET ID) + sizeOf(PACKET DATA) |
 * | PACKET ID   | VarInt |                                           |
 * | PACKET DATA | bytes  |                                           |
 * </pre>
 *
 * @see <a href="https://wiki.vg/index.php?title=Protocol&oldid=7368#Without_compression">Packet without compression</a>
 */
public interface Packet {

}
