package root.server.message.model;

import java.net.InetSocketAddress;

/**
 * Created by Max on 9/29/2014.
 */
public interface Message {
    public byte[] getData();
    InetSocketAddress getSenderAddress();
}
