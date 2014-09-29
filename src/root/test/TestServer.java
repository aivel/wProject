package root.test;

import root.server.AsyncUDPServer;
import root.server.model.Server;

import java.net.InetAddress;

/**
 * Created by Max on 9/29/2014.
 */
public class TestServer {
    static public void main(String[] args) {
        final int bufferSize = 256;
        final Server server = new AsyncUDPServer(InetAddress.getLoopbackAddress(), 12304, bufferSize);

        new Thread((Runnable) server).start();
        System.out.println("Test server successfully started!");
    }
}
