package root.server;

import root.server.message.ByteMessage;
import root.server.message.model.Message;
import root.server.model.Server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

/**
 * Created by Max on 9/29/2014.
 */
public class AsyncUDPServer implements Server, Runnable {
    private final int MESSAGES_AMOUNT_TO_INIT_FORCED_PROCESSING = 4;
    private int bufferSize;
    private Queue<Message> outgoingMessages;
    private Selector channelsSelector;
    private DatagramChannel serverChannel;
    private InetSocketAddress isa;
    private SelectionKey selectionKey;
    private boolean running;

    public AsyncUDPServer(final InetAddress address, final int port, final int bufferSize) {
        this.bufferSize = bufferSize;
        this.outgoingMessages = new ConcurrentLinkedQueue<>();
        this.isa = (address != null ? new InetSocketAddress(address, port) : new InetSocketAddress(port));

        try {
            this.serverChannel = DatagramChannel.open();
            this.channelsSelector = Selector.open();

            serverChannel.bind(isa);
            serverChannel.configureBlocking(false);
            selectionKey = serverChannel.register(channelsSelector, SelectionKey.OP_READ);
        } catch (IOException e) {
            e.printStackTrace();
        }

        channelsSelector.wakeup();
        this.running = true;
    }

    @Override
    public void processIncomingMessage(final Message message) {
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
        //
    }

    @Override
    public void enqueueOutgoingMessage(final Message message) {
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
    public void sendMessage(final Message message) {
        try {
            serverChannel.send(ByteBuffer.wrap(message.getData()), message.getSenderAddress());
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

            try {
                if (channelsSelector.selectNow() <= 0)
                    // No activity on the channel: no channels ready for i/o
                    continue;
            } catch (IOException e) {
                e.printStackTrace();
            }

            final Set<SelectionKey> selectedKeys = channelsSelector.selectedKeys();

            for (final SelectionKey key: selectedKeys) {
                if (!key.isValid()) {
                    Logger.getGlobal().warning("Invalid key!");
                    continue;
                }

                if (key.isReadable()) {
                    final ByteBuffer readBuffer = ByteBuffer.allocate(bufferSize);
                    InetSocketAddress senderAddress = null;

                    try {
                        senderAddress = (InetSocketAddress) ((DatagramChannel)key.channel()).receive(readBuffer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    readBuffer.flip();
                    processIncomingMessage(new ByteMessage(senderAddress, readBuffer.array()));
                }
            }

            selectedKeys.clear();

            //processIncomingMessages(); -- not sure if we need a queue of incoming messages
        }
    }
}