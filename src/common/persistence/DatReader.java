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

package common.persistence;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;

import common.CampaignData;

/**
 * Reads back the data from a DatWriter-Format
 * 
 * @author Imi (immanuel.scholz@gmx.de)
 */
@SuppressWarnings({"unchecked","serial"})
public class DatReader implements TreeReader {

    private BufferedReader in;

    public DatReader(String filename) throws FileNotFoundException {
        in = new BufferedReader(new FileReader(filename));
    }

    /**
     * Represent a line in dat-format
     */
    private static final class Line {
        public String name;
        public String value;
        public int size;
    }

    private Line readLine() throws IOException {
        Line line = new Line();
        String l = in.readLine();
        int i;
        for (i = 0; l.charAt(i) == ' '; ++i);
        l = l.substring(i);
        for (i = 0; l.charAt(i) != '='; ++i);
        line.name = l.substring(0,i);
        line.value = l.substring(i+1);
        if (l.endsWith("]={")) {
            int j = line.name.indexOf('[');
            if (j != -1) {
                line.size = Integer.parseInt(line.name.substring(j+1,line.name.length()-1));
                line.name = line.name.substring(0,j);
            }
        }
        return line;
    }

    /**
     * Check if a name matches. If not, a RuntimeException is thrown. Returns
     * the value of the line.
     */
    private String check(Line line, String name) {
        name.replace('=','-');
        if (!line.name.equals(name))
            throw new RuntimeException("Data structure mismatch");
        return line.value;
    }
    
    /**
     * @see common.persistence.TreeReader#readInt(java.lang.String)
     */
    public int readInt(String name) throws IOException {
        return Integer.parseInt(check(readLine(),name));
    }

    /**
     * @see common.persistence.TreeReader#readDouble(java.lang.String)
     */
    public double readDouble(String name) throws IOException {
        return Double.parseDouble(check(readLine(),name));
    }

    /**
     * @see common.persistence.TreeReader#readBoolean(java.lang.String)
     */
    public boolean readBoolean(String name) throws IOException {
        return Boolean.getBoolean(check(readLine(),name));
    }

    /**
     * @see common.persistence.TreeReader#readString(java.lang.String)
     */
    public String readString(String name) throws IOException {
        return check(readLine(),name);
    }

    /**
     * @see common.persistence.TreeReader#readObject(common.persistence.MMNetSerializable, java.lang.String)
     */
    public void readObject(MMNetSerializable obj, CampaignData dataProvider, String name) throws IOException {
        startDataBlock(name);
        obj.binIn(this, dataProvider);
        endDataBlock(name);
    }

    /**
     * @see common.persistence.TreeReader#readCollection(java.util.Collection, java.lang.Class, java.lang.String)
     */
    public void readCollection(
            Collection col, 
            Class cl, 
            CampaignData dataProvider, 
            String name) throws IOException {
        name.replace('[','(');
        name.replace(']',')');
        Line l = readLine();
        check(l, name);
        for (int i = 0; i < l.size; ++i) {
            try {
                MMNetSerializable obj = (MMNetSerializable) cl.newInstance();
                readObject(obj, dataProvider, String.valueOf(i));
                col.add(obj);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        endDataBlock(name);
    }

    /**
     * @see common.persistence.TreeReader#startDataBlock(java.lang.String)
     */
    public void startDataBlock(String name) throws IOException {
        check(readLine(),"{");
    }

    /**
     * @see common.persistence.TreeReader#endDataBlock(java.lang.String)
     */
    public void endDataBlock(String name) throws IOException {
        Line l = new Line();
        l.name = name;
        l.value = in.readLine();
        check(l,name);
    }

    /**
     * @see common.persistence.TreeReader#close()
     */
    public void close() throws IOException {
    }

}
