package root.server;

import com.sun.istack.internal.NotNull;
import root.server.message.model.Message;
import root.server.model.Server;

/**
 * Created by Semyon Danilov on 01.10.2014.
 */
public abstract class MessageHandler {

    @NotNull
    private Server server;

    public MessageHandler(final Server server) {
        this.server = server;
    }

    public void sendResponse(@NotNull final Message message) {
        server.enqueueOutgoingMessage(message);
    }

    public abstract void handleMessage(@NotNull final Message message);
}
