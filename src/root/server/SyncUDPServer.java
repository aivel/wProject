package root.server;

import root.server.message.ByteMessage;
import root.server.message.model.Message;
import root.server.model.Server;

import java.io.IOException;
import java.net.*;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Max on 10/1/2014.
 */
public class SyncUDPServer implements Server, Runnable {
    private final int MESSAGES_AMOUNT_TO_INIT_FORCED_PROCESSING = 4;
    private boolean running;
    private InetSocketAddress isa;
    private DatagramSocket datagramSocket;
    private Queue<Message> outgoingMessages;
    private int bufferSize;

    public SyncUDPServer(final InetAddress address, final int port, final int bufferSize) {
        this.bufferSize = bufferSize;
        this.outgoingMessages = new ConcurrentLinkedQueue<>();
        this.isa = (address != null ? new InetSocketAddress(address, port) : new InetSocketAddress(port));
        this.running = true;

        try {
            this.datagramSocket = new DatagramSocket(isa);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processIncomingMessage(Message message) {
        final String msgString = new String( message.getData() ).trim();
        System.out.println("Incoming message: '" + msgString + "' from "
                + message.getSenderAddress().toString() );

        if (msgString.startsWith("r")) {
            final Message msg = new ByteMessage(message.getSenderAddress(), "response text!".getBytes());
            enqueueOutgoingMessage(msg);
        }
    }

    @Override
    public void processIncomingMessages() {

    }

    @Override
    public void enqueueOutgoingMessage(Message message) {
        synchronized (outgoingMessages) {
            if (outgoingMessages == null)
                throw new NullPointerException("outgoingMessages queue should have been created to this moment!");

            outgoingMessages.add(message);

            if (MESSAGES_AMOUNT_TO_INIT_FORCED_PROCESSING != 0 &&
                    outgoingMessages.size() >= MESSAGES_AMOUNT_TO_INIT_FORCED_PROCESSING)
                processOutgoingMessages();
        }
    }

    @Override
    public void processOutgoingMessages() {
        synchronized (outgoingMessages) {
            if (outgoingMessages == null)
                throw new NullPointerException("outgoingMessages queue should have been created to this moment!");

            while (!outgoingMessages.isEmpty())
                sendMessage(outgoingMessages.poll());
        }
    }

    @Override
    public void sendMessage(Message message) {
        try {
            //serverChannel.send(ByteBuffer.wrap(message.getData()), message.getSenderAddress());
            final DatagramPacket datagramPacket = new DatagramPacket(message.getData(),
                    message.getData().length, message.getSenderAddress());
            datagramSocket.send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startRunning() {
        running = true;
    }

    @Override
    public void stopRunning() {
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
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
