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

package old.server.packet;

import static game.server.packet.CommonPacketConsts.ENUM_ACTION_TYPE.ACTION_PING;
import static game.server.packet.CommonPacketConsts.ENUM_ACTION_TYPE.ACTION_REGISTER;
import static game.server.packet.CommonPacketConsts.ENUM_ACTION_TYPE.ACTION_UPDATE;
import game.server.packet.CommonPacketConsts.ENUM_CLIENT_TYPE;
import game.server.packet.CommonPacketConsts.ENUM_MOVE_DIRECTION;
import game.server.packet.CommonPacketConsts.ENUM_PLAYER_ROLE;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Max
 */
public class PacketFactory {
    public enum PACKET_TYPE {PACKET_TYPE_AUTH_PLAYER, PACKET_TYPE_DISCONNECT_PLAYER, 
                      PACKET_TYPE_RESULT_FAIL, PACKET_TYPE_RESULT_SUCCESS};
    
    public static Packet make(final PACKET_TYPE packet_type,
                              final ENUM_CLIENT_TYPE client_type) throws JSONException {
        // Disconnect player, result success, result fail
        final JSONObject jo = new JSONObject();
        
        jo.put(CommonPacketConsts.CLIENT_TYPE, client_type);
        
        switch (PACKET_TYPE.values()[packet_type.ordinal()]) {
            case PACKET_TYPE_DISCONNECT_PLAYER: {
                jo.put(CommonPacketConsts.ACTION_TYPE, CommonPacketConsts.ENUM_ACTION_TYPE.ACTION_UNREGISTER.ordinal());
                break;
            }
            case PACKET_TYPE_RESULT_SUCCESS: {
                jo.put(CommonPacketConsts.ACTION_TYPE, CommonPacketConsts.ENUM_ACTION_TYPE.ACTION_RESULT.ordinal());
                jo.put(CommonPacketConsts.RESULT, CommonPacketConsts.ENUM_RESULT.RESULT_SUCCESS.ordinal());
                break;
            }
            case PACKET_TYPE_RESULT_FAIL: {
                jo.put(CommonPacketConsts.ACTION_TYPE, CommonPacketConsts.ENUM_ACTION_TYPE.ACTION_RESULT.ordinal());
                jo.put(CommonPacketConsts.RESULT, CommonPacketConsts.ENUM_RESULT.RESULT_FAIL.ordinal());
                break;
            }
            default: {
                break;
            }
        }
        
        final Packet packet = new Packet( jo.toString() );
        
        return packet;
    }
    
    public static Packet make(final ENUM_CLIENT_TYPE client_type, final String username,
            final String sender_host, final int sender_port, final ENUM_PLAYER_ROLE player_role) throws JSONException {
        // Redirect packet
        final JSONObject jo = new JSONObject();
        
        jo.put(CommonPacketConsts.CLIENT_TYPE, client_type.ordinal());
        jo.put(CommonPacketConsts.ACTION_TYPE, ACTION_REGISTER.ordinal());
        jo.put(CommonPacketConsts.ITEM_USERNAME, username);
        jo.put(CommonPacketConsts.ITEM_HOST, sender_host);
        jo.put(CommonPacketConsts.ITEM_PORT, sender_port);
        jo.put(CommonPacketConsts.PLAYER_ROLE, player_role.ordinal());
        
        final Packet packet = new Packet( jo.toString() );
        
        return packet;
    }
    
    public static Packet make(final float ball_x, final float ball_y, 
            final float p1_x, final float p1_y, 
            final float p2_x, final float p2_y) throws JSONException {
        // Update game state
        final JSONObject jo = new JSONObject();
        
        jo.put(CommonPacketConsts.BALL_POS_X, ball_x);
        jo.put(CommonPacketConsts.BALL_POS_Y, ball_y);
        jo.put(CommonPacketConsts.P1_POS_X, p1_x);
        jo.put(CommonPacketConsts.P1_POS_Y, p1_y);
        jo.put(CommonPacketConsts.P2_POS_X, p2_x);
        jo.put(CommonPacketConsts.P2_POS_Y, p2_y);
        
        final Packet packet = new Packet(jo.toString());
        
        return packet;
    }
    
    public static Packet make(final ENUM_CLIENT_TYPE client_type,
            final String msg) throws JSONException {
        // Update (?) msg
        final JSONObject jo = new JSONObject();
        
        jo.put(CommonPacketConsts.CLIENT_TYPE, client_type.ordinal());
        jo.put(CommonPacketConsts.ACTION_TYPE, ACTION_UPDATE.ordinal());
        jo.put(CommonPacketConsts.MESSAGE, msg);
        
        final Packet packet = new Packet(jo.toString());
        
        return packet;
    }
    
    public static Packet make(final ENUM_CLIENT_TYPE client_type, 
            final ENUM_MOVE_DIRECTION move_direction) throws JSONException {
        // Update direction
        final JSONObject jo = new JSONObject();
        
        jo.put(CommonPacketConsts.CLIENT_TYPE, client_type.ordinal());
        jo.put(CommonPacketConsts.ACTION_TYPE, ACTION_UPDATE.ordinal());
        jo.put(CommonPacketConsts.MOVE_DIRECTION, move_direction.ordinal());
        
        final Packet packet = new Packet(jo.toString());
        
        return packet;
    }
    
    public static Packet make(final ENUM_CLIENT_TYPE client_type) throws JSONException {
        // Ping packet
        final JSONObject jo = new JSONObject();
        
        jo.put(CommonPacketConsts.CLIENT_TYPE, client_type.ordinal());
        jo.put(CommonPacketConsts.ACTION_TYPE, ACTION_PING.ordinal());
        
        final Packet packet = new Packet(jo.toString());
        
        return packet;
    }
}
