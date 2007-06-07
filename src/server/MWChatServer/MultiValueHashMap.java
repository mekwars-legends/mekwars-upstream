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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Utilities for using a Map with multiple values for each key.
 *
 * The values for these maps end up being HashMaps themselves.  
 */
public class MultiValueHashMap {
    public static void put(Map<String,Map<String,String>> map, String key, String value) {
        Map<String,String> valueMap = map.get(key);
        if (valueMap == null) {
            valueMap = new HashMap<String,String>();
            map.put(key, valueMap);
        }
        valueMap.put(key(value), value);
    }

    public static void remove(Map map, Object key, Object value) {
        HashMap valueMap = (HashMap)map.get(key);
        if (valueMap == null) {
            return;
        } 
        valueMap.remove(key(value));
    }

    public static int size(Map map, Object key) {
        HashMap valueMap = (HashMap)map.get(key);
        if (valueMap == null)
            return 0;
        return valueMap.size();
    }

    public static String key(Object o) {
        return o.toString().toUpperCase();
    }

    public static void dump(Map m, PrintStream out) {
        for (Iterator i = m.keySet().iterator(); i.hasNext(); ) {
            Object key = i.next();
            out.println(key);
            Map inner = (Map)m.get(key);
            for (Iterator j = inner.keySet().iterator(); j.hasNext(); ) {
                key = j.next();
                Object value = inner.get(key);
                out.println("|- " + key + " = " + value);
            }
        }
    }
}
