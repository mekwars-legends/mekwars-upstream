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
import java.util.zip.Deflater;

import server.MMServ;
import server.MWChatServer.commands.ICommands;

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
    
	protected static final int MAX_DEFLATED_SIZE = 29999;

    protected Socket _socket;
    protected PrintWriter _out;
    protected ReaderThread _reader;
    protected InputStream _inputStream;
    protected boolean _isShutDown = false;
    protected Deflater _deflater = new Deflater();
    protected byte[] _deflatedBytes = new byte[MAX_DEFLATED_SIZE];
    protected Dispatcher _dispatcher;

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
    void init(Dispatcher d) {
    	
    	//set a dispatcher
    	d.addHandler(this);
    	_dispatcher = d;
    	
    	//start reading incoming data
    	try {
    		_reader = new ReaderThread(this, _client, _inputStream);
    		_reader.start();
    	} catch (Exception ex) {
    		MMServ.mmlog.errLog(ex);
    	} catch (OutOfMemoryError OOM) {
    		
    		/*
    		 * OOM usually mean there are no remaining threads or
    		 * sockets. This is generally not a problem, but is at
    		 * times an issue for MMNET, which runs in a contrained
    		 * environment and has hard resouce caps.
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
    			MMServ.mmlog.errLog(e);
    		}
    		
    	}
    }//end init()

    /**
     * Called by dispatcher. Sends queued messages to a downstream
     * client. Small messages are sent uncompressed, but large items
     * are GZIP'ed before transmission.
     */
    public void flush() {
        
    	// no need to synchronize the size() call, we don't care
        // if we get the wrong answer once in a while :P
        int nMessages = _messages.size();
        if (nMessages == 0) {
            return;
        }
        
        /*
         * Lock the message queue, check size of the contents and determine
         * whether the contents should be sent raw or be compressed.
         */
        synchronized (_messages) {
            
        	/*
        	 * @comment from NFC code
        	 * 
        	 * I tried base64-encoding the deflated output, since that is much
        	 * easier for the client to handle, but that pretty much eats all
        	 * the gains from deflation. So, raw bytes it is.
        	 */
            StringBuilder sb = new StringBuilder();
            while (!_messages.isEmpty()) {
                
            	//add to buffer, and break messages with newlines
            	sb.append(_messages.remove(0));
                sb.append("\n");
                
                /*
                 * if the buffer exceeds 9000 chars, compress and send immedaitely. one
                 * server was crashing as soon as an outbound buffer huit 14k chats. This
                 * prevents the buffer overload, but costs bandwidth :-(
                 */
                if (sb.length() >= 9000) {
                    deflateAndSend(sb.toString());
                    sb.setLength(0);
                }
            }
            
            String s = sb.toString();
            
            //If the message is brief (under 200 chars), send uncompressed, then return.
            if (s.length() < 200) {
              //  MMServ.mmlog.warnLog("Client: " + _client.getUserId() + " /" + _client.getHost() + " Size: " + s.length() + " Message: " + s);
                _out.print(s);
                _out.flush();
                return;
            }
            
            //9000 chars > message > 200 chars. Send compressed.
            deflateAndSend(s);
        }
    }
    
    /**
     * Deflate a string, then send it to the downstream client.
     * 
     * @param s - String to deflate
     */
    private void deflateAndSend(String s) {
        
    	try {
            byte[] rawBytes = s.getBytes("UTF8");
            _deflatedBytes = new byte[s.length()];
            _deflater.reset();
            _deflater.setInput(rawBytes);
            _deflater.finish();

            int n = _deflater.deflate(_deflatedBytes);// should always be nonzero since we called finish()

            //MMServ.mmlog.warnLog("Deflating Message for " + _client.getUserId()+ "/" + _client.getHost() + " from " + s.length() + " to " + n + " Message: " + s);
            
            /*
             * @NFC comment
             * 
             * we need to include the number of bytes so client can easily read the deflated
             * section into a byte array and decompress. (At first I wanted to just create an
             * InflaterInputStream over the socket on the client side, but it turns out that
             * it's @$%^ _impossible_ to get a truly unbuffered reader and the IIS and normal
             * stream reader cannot coexist no matter how hard you try.)
             */
            
            /*
             * Old NFCChat println command, collapsed into deflate b/c this is the only place
             * it was used. Combine a prefix indicating deflation with a delimiter and the byte
             * array, then print to the PrintWriter.
             */
            String o = ICommands.DEFLATED + ICommands.DELIMITER + n + ICommands.DELIMITER + s.length() +"\n";
            try {
                _out.print(o);
            } catch (Exception ex) {
                MMServ.mmlog.errLog(ex);
            }
            
            /*
             * End of NFC prinln, resumption of deflateAndSend.
             */
            _out.flush();
            _socket.getOutputStream().write(_deflatedBytes, 0, n);
            _socket.getOutputStream().flush();
            
        } catch (Exception e) {
            MMServ.mmlog.errLog("Socket error; shutting down client");
            MMServ.mmlog.errLog(e);
            //Commenting out for now. letting the socket get closed in the 
            //readerthread code. --Torren
            //shutdown(true);
        }

    }

    /**
     * Bypass the message queue to send something immediately. This is
     * used for pings and to kill clients (bad chars, banned folks, etc).
     */
    @Override
	public void queuePriorityMessage(String message) {
        synchronized (_messages) {
    //        MMServ.mmlog.warnLog("queuePriorityMessage Client: "
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

            _dispatcher.removeHandler(this);

            try {
                _socket.close();
            } catch (IOException e) {
                MMServ.mmlog.errLog("connection shutdown due to error");
                MMServ.mmlog.errLog(e);
            }

            super.shutdown(notify);
        }
    }//end shutdown()
    
}
