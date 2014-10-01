package root.test;

import com.sun.istack.internal.NotNull;
import root.server.AsyncUDPServer;
import root.server.MessageHandler;
import root.server.message.ByteMessage;
import root.server.message.model.Message;
import root.server.model.Server;

import java.net.InetAddress;

/**
 * Created by Max on 9/29/2014.
 */
public class TestServer {
    static public void main(String[] args) {
        final int bufferSize = 256;
        final Server server = new AsyncUDPServer(InetAddress.getLoopbackAddress(), 12304, bufferSize);
        server.setHandler(new MessageHandler(server) {

            @Override
            public void handleMessage(@NotNull final Message message) {
                System.out.println(new String(message.getData()));
                final Message msg = new ByteMessage(message.getSenderAddress(), message.getData());
                sendResponse(msg);
            }

        });
        new Thread(server).start();
        System.out.println("Test server successfully started!");
    }
}
