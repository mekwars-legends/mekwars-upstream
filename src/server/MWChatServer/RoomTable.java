/*
 * MekWars - Copyright (C) 2005 
 * 
 * Original author - Torren (torren@users.sourceforge.net)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 */


/*
 * Derived from NFCChat, a GPL chat client/server. 
 * Original code can be found @ http://nfcchat.sourceforge.net
 * Our thanks to the original authors.
 */ 
/**
 * 
 * @author Torren (Jason Tighe) 11.5.05 
 * 
 */
package server.MWChatServer;

import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 * The Room Table keeps track of all the rooms across the system, and the 
 * users each room contains.
 */ 
public class RoomTable {
    private HashMap<String,Map<String,String>> _rooms = new HashMap<String,Map<String,String>>();
    private HashMap<String,String> _roomNames = new HashMap<String,String>();
    private MWChatServer _server;
    
    public RoomTable(MWChatServer server) {
        _server = server;
    }

    public boolean userExistsInRoom(String username, String roomname) {
        return getUsers(roomname).contains(username);
    }
    
    public boolean roomExists(String roomname) {
        return _roomNames.keySet().contains(roomname);
    }
    
    public Collection<String> getRoomNames() {
        LinkedList<String> newList = new LinkedList<String>();
        newList.addAll(_roomNames.values());
        return newList;
    }
        
    public Collection<String> getUsers(String room) {
        LinkedList<String> newList = new LinkedList<String>();
        String key = MultiValueHashMap.key(room);
        Map<String,String> users = _rooms.get(key);
        if (users != null) {
            newList.addAll(users.values());
        }
        return newList;
    }
        
    public int countUsers(String room) {
        return MultiValueHashMap.size(_rooms, MultiValueHashMap.key(room));
    }
        
    public boolean join(String room, String user) {
        String key = MultiValueHashMap.key(room);
        boolean isNew = MultiValueHashMap.size(_rooms, key) == 0;
        MultiValueHashMap.put(_rooms, key, user);
        if (isNew) {
            _roomNames.put(key, room);
        }
        return isNew;
    }
        
    public boolean part(String room, String user) {
        String key = MultiValueHashMap.key(room);
        MultiValueHashMap.remove(_rooms, key, user);
            
        if (MultiValueHashMap.size(_rooms, key) == 0) {
            _roomNames.remove(key);
            _rooms.remove(key);
            return true;
        }
        return false;
    }

    /**
     * part method duplcate used internally (by this class).
     * instead of removing the map entries, it returns the key
     * to be removed.
     */
    private String internalPart(String room, String user) {
        String key = MultiValueHashMap.key(room);
        MultiValueHashMap.remove(_rooms, key, user);
            
        if (MultiValueHashMap.size(_rooms, key) == 0) {
            return key;
        }
        return null;
    }


    public Collection signoff(String user) {
        LinkedList deadRooms = new LinkedList();
        for (Iterator i = _roomNames.values().iterator(); i.hasNext(); ) {
            String room = (String)i.next();
            String deadRoom = internalPart(room, user);
            if (deadRoom != null) {
                i.remove();
                _rooms.remove(deadRoom);
            }
        }
        return deadRooms;
    }

    public void dump(PrintStream out) {
        out.println("RoomTable dump for Server: "+_server.getName());
        MultiValueHashMap.dump(_rooms, out);
    }
}

