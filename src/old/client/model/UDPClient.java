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

package old.client.model;

import old.server.model.UDPAsyncServer;
import old.utils.MessageHandler;

import java.io.IOException;
import java.net.*;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Max
 */
public abstract class UDPClient extends Thread implements MessageHandler<DatagramPacket> {
    private DatagramSocket client_socket;
    private Queue<DatagramPacket> outgoing_messages;
    private int buffer_size;
    private final int MESSAGES_AMOUNT_TO_INIT_FORCED_PROCESSING = 4; // SOLUTION FOR POSSIBLE PROBLEM: TOO MANY MESSAGES ENQUEUED
    
     public UDPClient(final InetAddress address, final int port, final int buffer_size) throws SocketException {
        if (address != null)
            client_socket = new DatagramSocket(port, address);
        else
            client_socket = new DatagramSocket(port);
        
        client_socket.setSoTimeout(1_000);
        
        this.buffer_size = buffer_size;
        outgoing_messages = new ConcurrentLinkedQueue<>();
    }
    
    public UDPClient(final int port, final int buffer_size) throws SocketException, UnknownHostException {
        this(InetAddress.getLoopbackAddress(), port, buffer_size);
    }
    
    public UDPClient(final int buffer_size) throws SocketException {
        client_socket = new DatagramSocket();
        
        client_socket.setSoTimeout(1_000);
        
        this.buffer_size = buffer_size;
        outgoing_messages = new ConcurrentLinkedQueue<>();
    }

    @Override
    public void run() {
        while (true) {
            processMessages();
            
            byte[] buffer = new byte[buffer_size];
            DatagramPacket packet = new DatagramPacket(buffer, buffer_size);
            
            try {
                client_socket.receive(packet);
                processIncomingPacket(packet);
            } catch (IOException ex) {
                //Logger.getLogger(UDPAsyncServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    synchronized public void enqueueMessage(DatagramPacket msg) {
        if (msg == null)
            throw new NullPointerException();
        
        if (outgoing_messages == null)
            outgoing_messages = new ConcurrentLinkedQueue<>();
        
        outgoing_messages.add(msg);
        
        if (outgoing_messages.size() >= MESSAGES_AMOUNT_TO_INIT_FORCED_PROCESSING)
            processMessages();
    }

    @Override
    public void processMessage(DatagramPacket msg) {
        if (client_socket != null && msg != null)
            try {
                client_socket.send(msg);
            } catch (IOException ex) {
                Logger.getLogger(UDPAsyncServer.class.getName()).log(Level.SEVERE, null, ex);
            }
    }

    @Override
    public void processMessages() {
        while (outgoing_messages != null && !outgoing_messages.isEmpty())
                processMessage(outgoing_messages.poll());
    }
    
    public abstract void processIncomingPacket(DatagramPacket packet);
}
