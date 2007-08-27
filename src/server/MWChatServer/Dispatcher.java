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

import java.util.ArrayList;

import server.MWServ;

public class Dispatcher implements Runnable {
    protected ArrayList<ConnectionHandler> _handlers;
    int _handlerCount;
    protected boolean _keepGoing = true;

    public Dispatcher() {
        _handlers = new ArrayList<ConnectionHandler>(100);
        _handlerCount = 0;
    }

    public synchronized void addHandler(ConnectionHandler ch) {
        _handlers.add(ch);
        _handlerCount++;
    }
    
    public synchronized void removeHandler(ConnectionHandler ch) {

    	_handlers.remove(ch);
    	_handlerCount = _handlers.size(); // in case it failed

    }

    public void run() {
        while (_keepGoing) {
            try {
                long start = System.currentTimeMillis();
                flushAll();
                long elapsed = System.currentTimeMillis() - start;
                if (elapsed < 20) {
	                Thread.sleep(20 - elapsed);
                }
            }
            catch (InterruptedException e) { 
                MWServ.mwlog.errLog(e);
            }
        }
    }
    
    private synchronized void flushAll() {
		for (int i = 0; i < _handlerCount; i++) {
			ConnectionHandler ch = (_handlers.get(i));
			
			// catch SocketException: Broken Pipe
            try{
                ch.flush();
            } catch(Exception ex){
                MWServ.mwlog.errLog(ex);
            }
		}
    }

    public void pleaseStop() {
        _keepGoing = false;
    }
}

