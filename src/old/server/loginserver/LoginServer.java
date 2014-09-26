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

package old.server.loginserver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import old.server.model.UDPAsyncServer;
import old.server.packet.PacketHandler;

/**
 *
 * @author Max
 */
public class LoginServer extends UDPAsyncServer implements PacketHandler {
//    private final List<PlayerInfo> logged_players;
//    private final List<GameServerInfo> registered_game_servers;
    
    public LoginServer(final InetAddress address, final int port, final int buffer_size) throws SocketException, IOException {
        super(address, port, buffer_size);
//        logged_players = new ArrayList<>();
//        registered_game_servers = new ArrayList<>();
    }
    
    @Override
    public void handlePacket(final InetSocketAddress sender_address, final ByteBuffer byte_buffer) {
        try {
            String message_as_str = new String(byte_buffer.array());
            //System.out.println("Incoming message: " + message_as_str);
            
            final JSONObject jo = new JSONObject(message_as_str);
            final int client_type = jo.getInt(CommonPacketConsts.CLIENT_TYPE);

            switch (CommonPacketConsts.ENUM_CLIENT_TYPE.values()[client_type]) {
                case CLIENT_GAMESERVER: {
                    processGameServer(sender_address, jo);
                    break;
                }
                case CLIENT_PLAYER: {
                    processPlayer(sender_address, jo);
                    break;
                }
                default:
                    break;
            }
        } catch (JSONException ex) {
            Logger.getLogger(LoginServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            System.out.println("Exception!");

            try {
                enqueueMessage(MessageToBeEnqueued.make(PacketFactory.make(PACKET_TYPE_RESULT_FAIL, CLIENT_LOGINSERVER), sender_address));
            } catch (JSONException ex) {
                Logger.getLogger(LoginServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void processGameServer(final InetSocketAddress sender_address, 
            final JSONObject jo) throws JSONException {
        final int action_type = jo.getInt(CommonPacketConsts.ACTION_TYPE);
        
        switch (CommonPacketConsts.ENUM_ACTION_TYPE.values()[action_type]) {
            case ACTION_REGISTER: {
                // register
                registerGameServer(sender_address, jo);
                break;
            }
        }
    }
    
    private void processPlayer(final InetSocketAddress sender_address, 
            final JSONObject jo) throws JSONException, Exception {
        final int action_type = jo.getInt(CommonPacketConsts.ACTION_TYPE);
        
        switch (CommonPacketConsts.ENUM_ACTION_TYPE.values()[action_type]) {
            case ACTION_REGISTER: {
                registerPlayer(sender_address, jo);
                break;
            }
            case ACTION_PING: {
                final PlayerInfo player_info = getPlayerByAddress(sender_address);
                final long ping = System.currentTimeMillis() - player_info.getPingTimestamp();
                
                if (ping > ServerRules.MAX_PING)
                    System.out.println("MAX PING REACHED");
            }
        }
    }
    
    private PlayerInfo getPlayerByAddress(final InetSocketAddress address) throws Exception {
        for (PlayerInfo player_info: logged_players) {
            if (player_info.getPlayerAddress().equals(address))
                return player_info;
        }
        
        throw new Exception("No player found - disconnect him!");
    }
    
    private boolean registerGameServer(final InetSocketAddress sender_address, 
            final JSONObject jo) throws JSONException {
        if (registered_game_servers.stream().anyMatch((gsi) -> (gsi.getServerAddress().equals(sender_address))))
            return false;
        
        final byte slots_available = (byte)jo.getInt(LoginServerPacketConsts.GAME_SERVER_SLOTS);
        
        GameServerInfo game_server_info = new GameServerInfo(sender_address, slots_available);
        
        if (!registered_game_servers.add(game_server_info)) {
            enqueueMessage(MessageToBeEnqueued.make(PacketFactory.make(PACKET_TYPE_RESULT_FAIL, CLIENT_LOGINSERVER), sender_address));
            return false;
        }
        
        enqueueMessage(MessageToBeEnqueued.make(PacketFactory.make(PACKET_TYPE_RESULT_SUCCESS, CLIENT_LOGINSERVER), sender_address));
        
        return true;
    }
    
    private boolean registerPlayer(final InetSocketAddress sender_address, 
            final JSONObject jo) throws JSONException {
        // TODO: Configurable limitations for names and etc.
        // TODO: Check role indexes to the bound of array(enum)
        final String name = jo.getString(CommonPacketConsts.ITEM_USERNAME);
        CommonPacketConsts.ENUM_PLAYER_ROLE role = CommonPacketConsts.ENUM_PLAYER_ROLE.values()[jo.getInt(CommonPacketConsts.PLAYER_ROLE)];
        
        final GameServerInfo gsi = getFirstAvailableGameServer();
        
        if (gsi == null) {
            enqueueMessage(MessageToBeEnqueued.make(PacketFactory.make(PACKET_TYPE_RESULT_FAIL, CLIENT_LOGINSERVER), sender_address));
            return false;
        }
        
        PlayerInfo player_info = new PlayerInfo(sender_address, gsi.getServerAddress(), name, role, (short)0);
        
        if (!logged_players.add(player_info)) {
            enqueueMessage(MessageToBeEnqueued.make(PacketFactory.make(PACKET_TYPE_RESULT_FAIL, CLIENT_LOGINSERVER), sender_address));
            return false;
        }
        
        enqueueMessage(MessageToBeEnqueued.make(PacketFactory.make(PACKET_TYPE_RESULT_SUCCESS, CLIENT_LOGINSERVER), sender_address));
        
        enqueueMessage(MessageToBeEnqueued.make(PacketFactory.make(CLIENT_LOGINSERVER, name, sender_address.getHostString(), sender_address.getPort(), role),
                gsi.getServerAddress()));
        player_info.setConnected(true);
        
        return true;
    }
    
    private GameServerInfo getFirstAvailableGameServer() {
        if (registered_game_servers.isEmpty())
            return null;
        
        for (GameServerInfo gsi: registered_game_servers)
            if (gsi.hasAvailableSlots())
                return gsi;
        
        return null;
    }
    
    public void pingClients() throws JSONException {
        for (PlayerInfo player_info: logged_players) {
            final ByteBuffer byte_buffer = ByteBuffer.wrap(PacketFactory.make(CLIENT_LOGINSERVER).getMsg().getBytes());
            final MessageToBeEnqueued msg = new MessageToBeEnqueued(byte_buffer, player_info.getPlayerAddress());
            
            player_info.setPingTimestamp(System.currentTimeMillis());
            enqueueMessage(msg);
        }
    }
    
    public void pingGameServers() throws JSONException {
        // TODO: Provide ensurance of gameservers being in-time informed 
        //  about logged out clients, in case of loginserver is out of service
        for (GameServerInfo game_server_info: registered_game_servers) {
            final ByteBuffer byte_buffer = ByteBuffer.wrap(PacketFactory.make(CLIENT_LOGINSERVER).getMsg().getBytes());
            final MessageToBeEnqueued msg = new MessageToBeEnqueued(byte_buffer, game_server_info.getServerAddress());
            
            game_server_info.setPingTimestamp(System.currentTimeMillis());
            enqueueMessage(msg);
        }
    }
    
    @Override
    public void update() {
        try {
            pingClients();
            pingGameServers();
            // TODO: Delete ACTION_PING -> count time from last message 
            //  -> create separate threads for clients who's gonna be kicked
        } catch (JSONException ex) {
            Logger.getLogger(LoginServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
