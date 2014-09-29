package root.server.message;

import root.server.message.model.Message;

import java.net.InetSocketAddress;

/**
 * Created by Max on 9/29/2014.
 */
public class ByteMessage implements Message {
    private final byte[] data;
    private final InetSocketAddress senderAddress;

    public ByteMessage(final InetSocketAddress senderAddress, final byte[] data) {
        this.senderAddress = senderAddress;
        this.data = data;
    }

    @Override
    public byte[] getData() {
        return data;
    }

    @Override
    public InetSocketAddress getSenderAddress() {
        return senderAddress;
    }
}
