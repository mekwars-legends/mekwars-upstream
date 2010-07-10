	/*
	 * MekWars - Copyright (C) 2009 
	 * 
	 * Original author - Bob Eldred (billypinhead@users.sourceforge.net)
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

package server.mwmysql;

import java.util.Enumeration;
import java.util.Hashtable;

public abstract class ObjectPool {
	private long expirationTime;
	private Hashtable locked;
	private Hashtable unlocked;
	abstract Object create();
	abstract void expire(Object o);
	abstract boolean validate(Object o);
	
	synchronized void killEmAll() {
		Object o;
		if (locked.size() > 0) {
			Enumeration e = locked.keys();
			while (e.hasMoreElements()) {
				o = e.nextElement();
				expire(o);
			}
		}
	}
	
	protected String countObjects() {
		return ("Unlocked: " + unlocked.size() + "  Locked: " + locked.size());
	}
	
	synchronized Object checkOut(){
		long now = System.currentTimeMillis();
		Object o;
		if ( unlocked.size() > 0 ) {
			Enumeration e = unlocked.keys();
			while (e.hasMoreElements()) {
				o = e.nextElement();
				if ((now = ((Long) unlocked.get(o)).longValue()) > expirationTime) {
					// object has expired
					unlocked.remove(o);
					expire(o);
					o = null;
				} else {
					if (validate(o)) {
						unlocked.remove(o);
						locked.put(o, new Long(now));
						return(o);
					} else {
						// Object failed validation
						unlocked.remove(o);
						expire(o);
						o = null;
					}
				}
			}
		}
		// No objects available, create a new one
		o = create();
		locked.put(o, new Long(now));
		return(o);
	}
	
	synchronized void checkIn(Object o){
		locked.remove(o);
		unlocked.put(o, new Long(System.currentTimeMillis()));
	}
	
	
	
	ObjectPool() {
		expirationTime = 30000; // 30 seconds
		locked = new Hashtable();
		unlocked = new Hashtable();
	}
}
