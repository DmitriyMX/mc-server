package mc.protocol.packets;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import mc.protocol.State;
import mc.protocol.io.NetByteBuf;

@NoArgsConstructor
@Data
@ToString(exclude = "rawData")
public class UnknownPacket implements ClientSidePacket {

    private State state;
    private int id;
    private int dataSize;
    private byte[] rawData;

    @Override
    public void readSelf(NetByteBuf netByteBuf) {
        rawData = new byte[dataSize];
        netByteBuf.readBytes(rawData);
    }

    @Override
    public void passivate() {
        this.state = null;
        this.id = 0;
        this.dataSize = 0;
        this.rawData = null;
    }
}
