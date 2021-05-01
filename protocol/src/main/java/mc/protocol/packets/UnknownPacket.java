package mc.protocol.packets;

import lombok.Data;
import lombok.ToString;
import mc.protocol.State;
import mc.protocol.io.NetByteBuf;

@Data
@ToString(exclude = "rawData")
public class UnknownPacket implements ClientSidePacket {

    private final State state;
    private final int id;
    private final int dataSize;
    private byte[] rawData;

    @Override
    public void readSelf(NetByteBuf netByteBuf) {
        rawData = new byte[dataSize];
        netByteBuf.readBytes(rawData);
    }
}
