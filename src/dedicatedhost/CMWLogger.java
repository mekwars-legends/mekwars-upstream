/*
 * MekWars - Copyright (C) 2004 
 * 
 * Derived from MegaMekNET (http://www.sourceforge.net/projects/megameknet)
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

package dedicatedhost;

import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.io.File;


public class CMWLogger {

	private static final int rotations = 5; // Configurable
	private static final int normFileSize = 1000000; // Configurable
	
	private static boolean logging = false;
	private static boolean addSeconds = false;
	private static boolean clientlog = false;

	private static File logDir;

	private static Logger clientErrLog;
	private static Logger clientOutputLog;
	
	private static MMNetFormatter mmnetFormatter;

	private static FileHandler clientErrHandler;
	private static FileHandler clientOutputHandler;
	
	public class MMNetFormatter extends SimpleFormatter {

		@Override
		public String format (LogRecord record) {

			GregorianCalendar now = new GregorianCalendar();
			now.setTimeInMillis(record.getMillis());
			StringBuilder sb = new StringBuilder();
			
			sb.append(now.get(Calendar.YEAR));
			if(now.get(Calendar.MONTH) < 9)
				sb.append("0");
			sb.append((now.get(Calendar.MONTH) + 1));
			if(now.get(Calendar.DAY_OF_MONTH) < 10)
				sb.append("0");
			sb.append(now.get(Calendar.DAY_OF_MONTH) + " ");
			if(now.get(Calendar.HOUR_OF_DAY) < 10)
				sb.append("0");
			sb.append(now.get(Calendar.HOUR_OF_DAY) + ":");
			if(now.get(Calendar.MINUTE) < 10)
				sb.append("0");
			if(addSeconds) {
				sb.append(now.get(Calendar.MINUTE) + ":");
				if(now.get(Calendar.SECOND) < 10)
					sb.append("0");
				sb.append(now.get(Calendar.SECOND) /* +"|" + record.getLevel() */ +"|"+ formatMessage(record) + "\n");
			} else {
				sb.append(now.get(Calendar.MINUTE) /* +"|" + record.getLevel() */ +"|"+ formatMessage(record) + "\n");
			}
			return sb.toString();
		}
	}

	/** Without argument, the constructor will build a default log dir */

	public CMWLogger(boolean client) {
		this("logs",client);
	}
	
	/** Actual constructor for the Logger.
		@param s String representing a log directory. Will be customizable
	*/

	public CMWLogger(String s, boolean client) {

		if(logging) return;

		logDir = new File(s);
		if(!logDir.exists()) {
			try {
				if(!logDir.mkdirs()) {
					System.err.println("WARNING: logging directory cannot be created!");
					System.err.println("WARNING: disabling log subsystem");
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if(!logDir.isDirectory()) {
			System.err.println("WARNING: logging directory is not a directory!");
			System.err.println("WARNING: disabling log subsystem");
			return;
		}

		if(!logDir.canWrite()) {
			System.err.println("WARNING: cannot write in logging directory!");
			System.err.println("WARNING: disabling log subsystem");
			return;
		}

		mmnetFormatter = new MMNetFormatter();

		try {
		    
			clientErrHandler = new FileHandler(logDir.getPath() + "/error", normFileSize, rotations, true);
			clientErrHandler.setLevel(Level.INFO);
			clientErrHandler.setFilter(null);
			clientErrHandler.setFormatter(mmnetFormatter);
			clientErrLog = Logger.getLogger("clientErrLogger");
			clientErrLog.setUseParentHandlers(false);
			clientErrLog.addHandler(clientErrHandler);

			clientOutputHandler = new FileHandler(logDir.getPath() + "/log", normFileSize, rotations, true);
			clientOutputHandler.setLevel(Level.INFO);
			clientOutputHandler.setFilter(null);
			clientOutputHandler.setFormatter(mmnetFormatter);
			clientOutputLog = Logger.getLogger("clientOutputLogger");
			clientOutputLog.setUseParentHandlers(false);
			clientOutputLog.addHandler(clientOutputHandler);
			client = true;
			logging = false;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void clientErrLog(String s) {
	   if ( clientlog)
	       clientErrLog.info(s);
	}

	public void clientErrLog(int s) {
	    clientErrLog.info(Integer.toString(s));
	}

	public void clientErrLog(Exception e) {
	    if ( clientlog)
	    {
			clientErrLog.warning("[" + e.toString() + "]");
			StackTraceElement[] t = e.getStackTrace();
			for(int i = 0; i < t.length; i++)
				clientErrLog.warning("   " + t[i].toString());
	    }
	}

	public void clientErrLog(Throwable e) {
	    if ( clientlog)
	    {
	        clientErrLog.warning("[" + e.toString() + "]");
	        StackTraceElement[] t = e.getStackTrace();
	        for(int i = 0; i < t.length; i++)
	            clientErrLog.warning("   " + t[i].toString());
	    }
}
	public void clientOutputLog(String s) {
	    if ( s != null && (s.startsWith("SENT: /signon") 
                || s.indexOf("CONFIRM") > 0 
                //||s.startsWith("USC") 
                //|| s.startsWith("DSD")
                ) )
	        return;
	    if ( clientlog)
	        clientOutputLog.info(s);
	}

	public void clientOutputLog(Exception e) {
	    if ( clientlog){
			clientOutputLog.warning("[" + e.toString() + "]");
			StackTraceElement[] t = e.getStackTrace();
			for(int i = 0; i < t.length; i++)
				clientOutputLog.warning("   " + t[i].toString());
	    }
	}

	public void client(boolean b){
	    clientlog = b;
	}
}
