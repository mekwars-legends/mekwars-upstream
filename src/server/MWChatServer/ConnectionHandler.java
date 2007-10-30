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
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import server.MWServ;

/**
 * The keeper of the Socket on the server side. Spawns a thread for reading from
 * the socket.
 * 
 * As each line is read from the socket, the server is notified, via the
 * IConnectionListener interface
 * 
 * Outgoing messages are passed by ChatServer to the Dispatcher who queues them
 * up and calls flush on the different CHes in turn.
 */
public class ConnectionHandler extends AbstractConnectionHandler {
    
    protected Socket _socket;
    protected PrintWriter _out;
    protected ReaderThread _reader;
    protected WriterThread _writer;
    protected InputStream _inputStream;
    protected boolean _isShutDown = false;
//    protected Dispatcher _dispatcher;

    /**
     * Construct a ConnectionHandler for the given socket
     * 
     * @param s - the socket
     * @param listener - object that will be notified with incoming messages
     * @exception IOException- if there is a problem reading or writing to the socket
     */
    public ConnectionHandler(Socket socket, MWChatClient client) throws IOException {
        
    	_client = client;
        _socket = socket;
        _socket.setKeepAlive(true);
        
        // Trying a 2 second time out on reading the socket --Torren
        _socket.setSoTimeout(2000);
        _socket.setTcpNoDelay(true);
        _socket.setSoLinger(false, 0);
        
        _out = new PrintWriter(new OutputStreamWriter(_socket.getOutputStream(), "UTF8"));
        _inputStream = socket.getInputStream();
    }

    /**
     * Called from MWChatClient. Start reading incoming
     * chat and sending to an associated dispatcher.
     */
    void init() {
    	
    	//set a dispatcher
    	//d.addHandler(this);
    	//_dispatcher = d;
    	
    	//start reading incoming data
    	try {
    		_reader = new ReaderThread(this, _client, _inputStream);
    		ThreadManager.getInstance().runInThreadFromPool(_reader);
    		//_reader.start();
    	} catch (Exception ex) {
    		MWServ.mwlog.errLog(ex);
    	} catch (OutOfMemoryError OOM) {
    		
    		/*
    		 * OOM usually mean there are no remaining threads or
    		 * sockets. This is generally not a problem.
    		 */
    		
    		try {
    			
    			//shut down everything we can
    			_out.close();
    			_out = null;
    			_socket.close();
    			_socket = null;
    			_inputStream.close();
    			_inputStream = null;
    			_client = null;
    			_reader = null;
    			
    			//garbage collect and try again
    			System.gc();
    			_reader = new ReaderThread(this, _client, _inputStream);
    			_reader.start();
   
    		} catch (Exception e) {
    			MWServ.mwlog.errLog(e);
    		}
    		
    	}
    	
    	try {
    		_writer = new WriterThread(_socket,_out);
    		ThreadManager.getInstance().runInThreadFromPool(_writer);
    		//_reader.start();
    	} catch (Exception ex) {
    		MWServ.mwlog.errLog(ex);
    	}
    }//end init()


    /**
     * Bypass the message queue to send something immediately. This is
     * used for pings and to kill clients (bad chars, banned folks, etc).
     */
    @Override
	public void queuePriorityMessage(String message) {
        synchronized (message) {
    //        MWServ.mwlog.warnLog("queuePriorityMessage Client: "
      //              + _client.getUserId() + "Size: " + message.length()
        //            + " Host: " + _client.getHost());
            _out.print(message + "\n");
            _out.flush();
        }
    }

    /**
     * @param notify
     *            to notify the ConnectionListener. Should be true for
     *            unexpected shutdowns (like if there is a socket error), and
     *            false otherwise (if client called this method on purpose)
     */
    @Override
	public synchronized void shutdown(boolean notify) {

        if (!_isShutDown) {
            _isShutDown = true;

            _reader.pleaseStop();
            _reader.interrupt();

            _writer.pleaseStop();
            _writer.interrupt();
            
            //_dispatcher.removeHandler(this);

            try {
                _socket.close();
            } catch (IOException e) {
                MWServ.mwlog.errLog("connection shutdown due to error");
                MWServ.mwlog.errLog(e);
            }

            super.shutdown(notify);
        }
    }//end shutdown()
    
	public void queueMessage(String message) {
		_writer.queueMessage(message);
	}

}
