package root.server.model;

import root.server.MessageHandler;
import root.server.message.model.Message;

/**
 * Created by Max on 9/29/2014.
 */
public interface Server extends Runnable {
    void processIncomingMessage(final Message message);
    void processIncomingMessages();
    void enqueueOutgoingMessage(final Message message);
    void processOutgoingMessages();
    void sendMessage(final Message message);
    void startRunning();
    void stopRunning();
    boolean isRunning();
    void setHandler(final MessageHandler handler);
}
