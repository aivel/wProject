package root.server;

import root.server.message.ByteMessage;
import root.server.message.model.Message;
import root.server.model.QServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by Max on 9/29/2014.
 */
public class AsyncUDPServer extends QServer {

    private Selector channelsSelector;
    private DatagramChannel serverChannel;
    private SelectionKey selectionKey;

    public AsyncUDPServer(final InetAddress address, final int port, final int bufferSize) {
        super(bufferSize, address, port);

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
    public void run() {
        if (messageHandler == null) {
            throw new RuntimeException("Unable to start server without messageHandler");
        }

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
                    byte[] b = new byte[readBuffer.remaining()];
                    readBuffer.get(b, 0, b.length);
                    processIncomingMessage(new ByteMessage(senderAddress, b));
                }
            }

            selectedKeys.clear();

            //processIncomingMessages(); -- not sure if we need a queue of incoming messages
        }
    }

}


