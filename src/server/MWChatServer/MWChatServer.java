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

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import server.MWServ;
import server.MWChatServer.auth.IAuthenticator;
import server.MWChatServer.auth.IRoomAuthenticator;
import server.MWChatServer.auth.NullRoomAuthenticator;
import server.MWChatServer.auth.PasswdAuthenticator;
import server.MWChatServer.commands.ICommands;
import server.MWChatServer.auth.Auth;

public class MWChatServer implements ICommands {

    protected static Properties _properties;
    protected boolean _asciiRoomNames;

    protected TimedUserList _killedUsers;
    protected IAuthenticator _authenticator;
    protected IRoomAuthenticator _roomAuthenticator;

    protected Map<String, MWChatClient> _users = Collections.synchronizedMap(new HashMap<String, MWChatClient>());
    protected HashMap<String, RoomServer> _rooms = new HashMap<String, RoomServer>();

    protected String _motd;

    protected ServerSocket _serverSocket;
    protected Dispatcher _dispatcher;
    protected Thread _dispatchThread;

    protected int _cumulativeLogins = 0;
    protected int _port = 0;
    protected int _kickBanSeconds = 60 * 60 * 24;

    public MWChatServer(String IPAddress, int port) throws Exception {
       
    	_dispatcher = createDispatcher();
        _dispatchThread = new Thread(_dispatcher, "Dispatcher");
        _dispatchThread.start();

        Properties commandProps = new Properties();
        commandProps.setProperty("signon.class", "server.MWChatServer.commands.SignOn");
        commandProps.setProperty("signon.access", "0");
        commandProps.setProperty("comm.class", "server.MWChatServer.commands.Command");
        initCommandProcessor(commandProps);

        Properties messagesProps = new Properties();
        messagesProps.setProperty("access_denied", "Accessdenied.");
        messagesProps.setProperty("sql_error", "SQLError:{0}");
        messagesProps.setProperty("already_on", "Youarealreadysignedon");
        messagesProps.setProperty("unknown_command","Sorry,Ididnotrecognizethatcommand.");
        messagesProps.setProperty("noroom", "Thereisnoroomcalled{0}");
        messagesProps.setProperty("reserved_name", "{0}isareservedname");
        messagesProps.setProperty("nosuchcommand", "Nosuchcommand:{0}");
        messagesProps.setProperty("nosuchuser", "Nosuchuser:{0}");
        messagesProps.setProperty("nosuchroom", "Nosuchroom:{0}");
        initTranslator(messagesProps);

        this._port = port;

        //construct authenticators
        _authenticator = new PasswdAuthenticator(this, true,false);
        _roomAuthenticator = new NullRoomAuthenticator();

        this._asciiRoomNames = true;

        /*
         * Binding to localhost by default was causing problems for ClanHomeWorlds.
         * Proper behaviour is to pass a null InetAddress to the server socket in
         * order to bind the socket to all available IPs/devices. If we check for a
         * real null and a "-1", we can know to bind on either the first run (when
         * no config is present) or when the value is missing from serverconfig.txt
         * 
         * @urgru 12.4.05
         */
        if (IPAddress == null || IPAddress.equals("-1"))
            _serverSocket = new ServerSocket(_port, -1, null);
        else
            _serverSocket = new ServerSocket(_port, -1, InetAddress
                    .getByName(IPAddress));

        // this is called but never initlized.
        _killedUsers = new TimedUserList(60 * 60 * 2);
    }

    public static Properties getProperties() {
        return _properties;
    }

    public int getKickBanSeconds() {
        return _kickBanSeconds;
    }

    public int getRoomAccessLevel(MWChatClient client, RoomServer rs) {
        return _roomAuthenticator.getAccessLevel(client, rs);
    }

    public MWChatClient getRoomNextOp(RoomServer rs) {
        return _roomAuthenticator.getNextOp(rs);
    }

    protected Dispatcher createDispatcher() {
        return new Dispatcher();
    }

    public Dispatcher getDispatcher() {
        return _dispatcher;
    }

    public void initTranslator(Properties p) {
        Translator.init(p);
    }

    public void initCommandProcessor(Properties p) {
        CommandProcessorRemote.init(p);
    }

    /**
     * Get a MWChatClient by name
     */
    public MWChatClient getClient(String target) {
        try {
            return _users.get(clientKey(target));
        } catch (Exception ex) {
            MWServ.mwlog.errLog(ex);
            return null;
        }
    }

    /**
     * Determines if a user id is valid. Ensures that the name is made up of
     * alphanumerical characters and contains no spaces.
     */
    protected void validateUserId(String user) throws Exception {
        if (getName().toLowerCase().equals(user.toLowerCase()))
            throw new Exception(user + " is a reserved name");

        if (user.toLowerCase().startsWith("war bot"))
            throw new Exception(ACCESS_DENIED);

        char[] chars = user.toLowerCase().toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char ch = chars[i];
            if (!(Character.isLetterOrDigit(chars[i]) || ch == '_' || ch == '-'
                    || ch == '\\' || ch == '^' || ch == '`' || ch == '|'
                    || ch == '[' || ch == '{' || ch == ']' || ch == '}'
                    || ch == '(' || ch == ')' || ch == '\'')) {
                throw new Exception(INVALID_CHARACTER);
            }
        }
    }

    /**
     * Sign on to the server
     * 
     * @param client
     *            the MWChatClient
     * @param password
     * @exception AccessException
     *                is the login failed
     */
    public void signOn(MWChatClient client, String password) throws Exception {
        String userId = client.getUserId();
        validateUserId(userId);
        Auth auth = _authenticator.authenticate(client, password);
        int access = auth.getAccess();
        MWServ.mwlog.infoLog(client.getUserId() + " signon from "
                + client.getHost() + ".  Access = " + access
                + (client.getTunneling() ? " (tunneling)" : ""));

        if (access == IAuthenticator.NONE || _killedUsers.contains(client.getKey())) {
            throw new Exception(ACCESS_DENIED);
        }

        client.setUserId(auth.getUserId());
        synchronized (_users) {
            MWServ.mwlog.infoLog("signOn: " + client.getUserId());
            // if signed on locally, let new take precedence
            MWChatClient oldC = _users.get(clientKey(client));
            if (oldC != null) {
                oldC.killed(client.getUserId(),"Terminated by signing on elsewhere");
                MWServ.mwlog.errLog("Terminated by signing on elsewhere");
                signOff(oldC);
            }

            client.setAccessLevel(access);
            _users.put(clientKey(client), client);
            _cumulativeLogins++;
            try {
                this.joinRoom(client, "Main Chat", "");
            } catch (Exception ex) {
                MWServ.mwlog.errLog("Unable to join room");
                MWServ.mwlog.errLog(ex);
            }

        }
        client.ackSignon(auth.getUserId());
    }
    
    /**
     * Sign off of the system. This is called by the MWChatClient when the
     * socket unexpectedly closes, or when the user quits.
     */
    public void signOff(MWChatClient client) {
        if (client.getAccessLevel() == IAuthenticator.NONE) {

            // not authenticated means not logged in.
            // just kill the client and return
            if (clientKey(client) != null) {
                try {
                    _users.remove(clientKey(client));
                } catch (Exception ex) {
                    MWServ.mwlog.errLog(ex);
                }
            }

            client.die();
            return;
        }

        Object o = null;
        synchronized (_users) {
            o = _users.remove(clientKey(client));
        }

        if (o != null) {
            // inform all the rooms that this user is gone
            synchronized (_rooms) {

                for (Iterator i = _rooms.values().iterator(); i.hasNext();) {
                    RoomServer room = (RoomServer) i.next();
                    room.part(client, true);
                    if (room.isEmpty()) {
                        MWServ.mwlog.infoLog("Removing empty room: "
                                + room.getName());
                        i.remove();
                    }
                }
            }
        }

        client.die();
    }

    /**
     * Kick a user off the system.
     */
    public void kill(String victim, MWChatClient killer, String message) {
        if (killer.getAccessLevel() < IAuthenticator.MODERATOR) {
            killer.error(ACCESS_DENIED, KILL + " " + victim);
            return;
        }

        String killedKey = null;
        MWChatClient c = getClient(victim);
        if (c == null) {
            killer.error(NO_SUCH_USER, victim);
            // add user to _killedUsers even if he's not logged on
            String victimId = _authenticator.getUserId(victim);
            if (victimId != null) {
                killedKey = MWChatClient.getKey(victimId);
            }
        } else {
            MWServ.mwlog.infoLog(victim + " kicked off by " + killer._userId);
            killedKey = c.getKey();
            c.killed(killer.getUserId(), message);
            signOff(c);
            killer.ackKill(victim);
        }
        if (killedKey != null) {
            // killer.generalMessage(Translator.getMessage("kill_queued",
            // killedKey, String.valueOf(_killBanMinutes)));
            _killedUsers.add(killedKey);
        }
    }

    /**
     * Kick a user off the system by the system itself.
     */
    public void kill(String victim, String message) {
        String killedKey = null;
        MWChatClient c = getClient(victim);
        if (c == null) {
            // add user to _killedUsers even if he's not logged on
            String victimId = _authenticator.getUserId(victim);
            if (victimId != null) {
                killedKey = MWChatClient.getKey(victimId);
            }
        } else {
            MWServ.mwlog.infoLog(victim + " kicked off by server.");
            killedKey = c.getKey();
            signOff(c);
        }
        if (killedKey != null) {
            // killer.generalMessage(Translator.getMessage("kill_queued",
            // killedKey, String.valueOf(_killBanMinutes)));
            _killedUsers.add(killedKey);
        }
    }

    public void joinRoom(MWChatClient client, String roomName, String password) throws Exception {
        String key = roomKey(roomName);
        RoomServer room = _rooms.get(key);
        synchronized (_rooms) {
            if (room == null) {
                if (!_roomAuthenticator.isCreateAllowed(client, roomName,
                        password)) {
                    throw new Exception(ICommands.ROOM_ACCESS_DENIED);
                }
                room = _rooms.get(roomKey(roomName));
                if (room == null) {
                    if (_asciiRoomNames) {
                        for (int i = 0; i < roomName.length(); i++) {
                            int c = roomName.charAt(i);
                            // don't include space or DEL
                            if (c <= 32 && c >= 128) {
                                MWServ.mwlog.infoLog(client.getUserId() + " room creation rejected: " + roomName);
                                throw new Exception(ICommands.INVALID_CHARACTER);
                            }
                        }
                    }
                    MWServ.mwlog.infoLog(client.getUserId()
                            + " created new room: " + roomName);
                    room = createRoomServer(roomName, password);
                    _rooms.put(roomKey(room), room);
                }
            }
        }
        room.join(client, password);
    }

    public RoomServer createRoomServer(String roomName, String password) {
        return new RoomServer(roomName, password, this);
    }

    /**
     * returns a key for referencing the rooms hashmap. mainly to avoid
     * case-sensitivity problems.
     */
    public static String roomKey(String room) {
        return room.toLowerCase();
    }

    /**
     * returns a key for referencing the rooms hashmap. mainly to avoid
     * case-sensitivity problems.
     */
    public static String roomKey(RoomServer room) {
        return roomKey(room.getName());
    }

    /**
     * returns a key for referencing the users hashmap. mainly to avoid
     * case-sensitivity problems.
     */
    public static String clientKey(MWChatClient client) {
        return client.getKey();
    }

    /**
     * returns a key for referencing the users hashmap. mainly to avoid
     * case-sensitivity problems.
     */
    public static String clientKey(String client) {

        // Sometimes bad strings are set up. Not much to do about it except
        // return the null and hope for the best --Torren.
        if (client == null)
            return null;

        try {
            return client.toLowerCase();
        } catch (Exception ex) {
            MWServ.mwlog.errLog(ex);
            return null;
        }
    }

    /**
     * Tells the server to begin accepting connections.
     */
    protected void acceptConnections() {

        PingThread pingKeepAlive = new PingThread(this);
        pingKeepAlive.start();

        MWServ.mwlog.infoLog("Accepting socket connections on port " + _port);
        while (true) {
            try {
                Socket s = _serverSocket.accept();
                s.setTcpNoDelay(true);// couldn't hurt
                s.setKeepAlive(true);
                s.setSoTimeout(15000);// 15 second timeout
                s.setSoLinger(false, 0);
                // MWChatClient client =
                createMWChatClient(s);
            } catch (IOException e) {
                MWServ.mwlog.errLog(e);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    MWServ.mwlog.errLog(ex);
                }
            } catch (Exception ex) {
                MWServ.mwlog.errLog(ex);
                try {
                    Thread.sleep(1000);
                } catch (Exception exs) {
                    MWServ.mwlog.errLog(exs);
                }
            }
        }
    }

    protected MWChatClient createMWChatClient(Socket s) throws IOException {
        return new MWChatClient(this, s);
    }

    public String getName() {
        try {
            return java.net.InetAddress.getLocalHost().getHostName() + ":" + _port;
        } catch (java.net.UnknownHostException e) {
            throw new RuntimeException(e.toString());
        }
    }

    public boolean userExists(String username) {
        //MWServ.mwlog.infoLog("userExists: " + username);
        return _users.containsKey(username.toLowerCase());
    }

    protected void sendServerPing(String server) {
        StringBuilder sb = new StringBuilder();
        sb.append(PING);
        sb.append(DELIMITER);
        sb.append(server);
        sb.append(DELIMITER);
        sb.append(System.currentTimeMillis());

        /*
         * Had issues where people where disconnecting while the ping wasbeing
         * set which caused a Concurrent Mod Error. Put in the try to protect
         * eveyone, and added a lock to _users. - Torren
         */
        try {
            synchronized (_users) {
                MWServ.mwlog.infoLog("sendServerPing: " + server);
                Iterator<MWChatClient> clients = _users.values().iterator();
                while (clients.hasNext()) {
                    MWChatClient client = clients.next();
                    client.sendRaw(sb.toString());
                }
            }
        } catch (Exception ex) {
            MWServ.mwlog.errLog("Error while sending server ping!");
            MWServ.mwlog.errLog(ex);
        }
    }

    /**
     * Loop though all clients and make sure that the latest command or the
     * latest pong was within the last 200 seconds. If not, add to a removal
     * pile.
     * 
     * After all clients are checked, sign off everyone on toRemove list.
     */
    protected void checkForPongs() {

        synchronized (_users) {
            
        	ArrayList<MWChatClient> clientToRemove = new ArrayList<MWChatClient>();
            Iterator<MWChatClient> clients = _users.values().iterator();
            
            while (clients.hasNext()) {
                MWChatClient client = clients.next();
                if (client._connectionHandler._lastReceived + 120000 < System.currentTimeMillis())
                    clientToRemove.add(client);
            }
            
            for (MWChatClient client : clientToRemove) {
                MWServ.mwlog.infoLog("RemovalThread sign off: " + client.getUserId());
                this.signOff(client);
            }
            
        }
    }

    /**
     * PingThread was created because NFC 1.1-RC stopped using the heartbeat ping to
     * the clients. MWClients still need this heartbeat. This is the best thing I
     * could think of.
     * 
     * @author Torren (Jason Tighe) 11.7.05
     */
    private static final class PingThread extends Thread {

        private long waittime = 45000;// 45 second wait

        private MWChatServer server;

        public PingThread(MWChatServer server) {
            super("PingThread");
            MWServ.mwlog.infoLog("Starting PingThread");
            this.server = server;
        }

        @Override
		public synchronized void run() {
            try {
                while (true) {

                    // send pings
                    this.wait(waittime);
                    server.sendServerPing("server");

                    // check for pongs
                    // this.wait(waittime);
                    server.checkForPongs();

                }
            } catch (Exception ex) {
                MWServ.mwlog.errLog("Error while trying to sleep PingThread");
                MWServ.mwlog.errLog(ex);
            }

        }
    }// end PingThread class
    
}// end MWChatServer class

