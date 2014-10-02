package root.server.model;

import com.sun.istack.internal.NotNull;
import root.server.AsyncUDPServer;
import root.server.MessageHandler;
import root.server.message.model.Message;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * QServer. This implementation of Server firstly collects all the
 *  outgoing messages to a queue. When maximum allowed queue length
 *  is reached, all the messages are being sent.
 *
 * Created by Max on 10/2/2014.
 */
public abstract class QServer implements Server {
    protected static final Object monitor = new Object();
    protected final int MESSAGES_AMOUNT_TO_INIT_FORCED_PROCESSING = 1;
    protected int bufferSize;
    protected Queue<Message> outgoingMessages;
    protected volatile boolean running;
    @NotNull
    protected MessageHandler messageHandler;
    protected InetSocketAddress isa;

    public QServer(final int bufferSize, final InetAddress address, final int port) {
        this.running = true;
        this.bufferSize = bufferSize;
        this.outgoingMessages = new ConcurrentLinkedQueue<>();
        this.isa = (address != null ? new InetSocketAddress(address, port) : new InetSocketAddress(port));
    }

    @Override
    public void processIncomingMessage(final Message message) {
        final String msgString = new String( message.getData() ).trim();
        System.out.println("Incoming message: '" + msgString + "' from "
                + message.getSenderAddress().toString() );
        messageHandler.handleMessage(message);
    }

    @Override
    @Deprecated
    public void processIncomingMessages() {}

    @Override
    public void enqueueOutgoingMessage(final Message message) {
        synchronized (AsyncUDPServer.monitor) {
            if (outgoingMessages == null) {
                throw new NullPointerException("outgoingMessages queue should have been created to this moment!");
            }

            outgoingMessages.add(message);

            if (outgoingMessages.size() >= MESSAGES_AMOUNT_TO_INIT_FORCED_PROCESSING) {
                processOutgoingMessages();
            }
        }
    }

    @Override
    public void processOutgoingMessages() {
        synchronized (monitor) {
            if (outgoingMessages == null)
                throw new NullPointerException("outgoingMessages queue should have been created to this moment!");

            while (!outgoingMessages.isEmpty())
                sendMessage(outgoingMessages.poll());
        }
    }

    @Override
    public void sendMessage(final Message message) {}

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
    public void setMessageHandler(final MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }
}
