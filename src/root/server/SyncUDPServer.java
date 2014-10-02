package root.server;

import root.server.message.ByteMessage;
import root.server.message.model.Message;
import root.server.model.QServer;

import java.io.IOException;
import java.net.*;

/**
 * Created by Max on 10/1/2014.
 */
public class SyncUDPServer extends QServer {
    protected final int MESSAGES_AMOUNT_TO_INIT_FORCED_PROCESSING = 1;
    private DatagramSocket datagramSocket;

    public SyncUDPServer(final InetAddress address, final int port, final int bufferSize) {
        super(bufferSize, address, port);

        try {
            this.datagramSocket = new DatagramSocket(isa);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessage(final Message message) {
        try {
            final DatagramPacket datagramPacket = new DatagramPacket(message.getData(),
                    message.getData().length, message.getSenderAddress());
            datagramSocket.send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (running) {
            processOutgoingMessages();
            //update(); -- for subscriptions support, if needed
            final byte[] receivedData = new byte[bufferSize];
            final DatagramPacket datagramPacket = new DatagramPacket(receivedData, bufferSize);

            try {
                datagramSocket.receive(datagramPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }

            final InetSocketAddress senderAddress = (InetSocketAddress) datagramPacket.getSocketAddress();

            processIncomingMessage(new ByteMessage(senderAddress, receivedData));

            //processIncomingMessages(); -- not sure if we need a queue of incoming messages
        }
    }
}
