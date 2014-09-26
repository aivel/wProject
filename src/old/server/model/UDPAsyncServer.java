/*
 * Copyright 2014 Max.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package old.server.model;
import old.server.packet.Packet;
import old.server.packet.PacketHandler;
import old.utils.MessageHandler;
import old.utils.Pair;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Max
 */
public abstract class UDPAsyncServer extends Thread implements MessageHandler<UDPAsyncServer.MessageToBeEnqueued>, PacketHandler {
    private final int MESSAGES_AMOUNT_TO_INIT_FORCED_PROCESSING = 4;
    /**
     * Describes message format to be enqueued
    */
    public static final class MessageToBeEnqueued extends Pair<ByteBuffer, SocketAddress> {
        public MessageToBeEnqueued(ByteBuffer first, SocketAddress second) {
            super(first, second);
        }
        
        static public MessageToBeEnqueued make(final String first, final SocketAddress second) {
            return new MessageToBeEnqueued(ByteBuffer.wrap(first.getBytes()), second);
        }
        
        static public MessageToBeEnqueued make(final Packet first, final SocketAddress second) {
            return new MessageToBeEnqueued(ByteBuffer.wrap(first.getMsg().getBytes()), second);
        }
    }
    
    private int buffer_size;
    private Queue<MessageToBeEnqueued> outgoing_messages;
    private Selector channels_selector;
    private DatagramChannel server_channel;
    private InetSocketAddress isa;
    private SelectionKey selection_key;
    
    public UDPAsyncServer(final InetAddress address, final int port, final int buffer_size) throws SocketException, IOException {
        this.buffer_size = buffer_size;
        outgoing_messages = new LinkedBlockingQueue<>();
        channels_selector = Selector.open();
        server_channel = DatagramChannel.open();
        
        if (address != null)
            isa = new InetSocketAddress(address, port);
        else
            isa = new InetSocketAddress(port);
        
        server_channel.bind(isa);
        server_channel.configureBlocking(false);
        selection_key = server_channel.register(channels_selector, SelectionKey.OP_READ);
        channels_selector.wakeup();
    }
    
    public UDPAsyncServer(final int port, final int buffer_size) throws SocketException, UnknownHostException, IOException {
        this(InetAddress.getLoopbackAddress(), port, buffer_size);
    }
    
    @Override
    public void run() {
        while (true) {
            try {
                processMessages();
                
                update();
                
                if (channels_selector.selectNow() <= 0)
                    // No activity on the channel: no channels ready for i/o
                    continue;
                
                Set selected_keys = channels_selector.selectedKeys();
                Iterator key_iterator = selected_keys.iterator();
                
                while (key_iterator.hasNext()) {
                    SelectionKey key = (SelectionKey) key_iterator.next();
                    
                    if ( !key.isValid() ) {
                        Logger.getGlobal().warning("invalid key!");
                        continue;
                    }
                    
                    if (key.isReadable()) {
                        ByteBuffer byte_buffer = ByteBuffer.allocate(buffer_size);
                        InetSocketAddress sender_address = (InetSocketAddress) ((DatagramChannel)key.channel()).receive(byte_buffer);
                        byte_buffer.flip();
                        handlePacket(sender_address, byte_buffer);
                    }
                }
                
                selected_keys.clear();
            } catch (IOException ex) {
                Logger.getLogger(UDPAsyncServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @Override
    synchronized public void enqueueMessage(final MessageToBeEnqueued msg) {
        if (outgoing_messages == null)
            outgoing_messages = new LinkedBlockingQueue<>();
        
        outgoing_messages.add(msg);
        
        if (outgoing_messages.size() >= MESSAGES_AMOUNT_TO_INIT_FORCED_PROCESSING)
            processMessages();
    }
    
    @Override
    public void processMessages() {
        while (outgoing_messages != null && !outgoing_messages.isEmpty())
                processMessage( outgoing_messages.poll() );
    }
    
    @Override
    public void processMessage(final MessageToBeEnqueued msg) {
        try {
            server_channel.send(msg.getFirst(), msg.getSecond());
        } catch (IOException ex) {
            Logger.getLogger(UDPAsyncServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public abstract void update();
}
