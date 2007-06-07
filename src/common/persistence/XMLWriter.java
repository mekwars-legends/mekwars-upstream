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

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;


/**
 * Writing out to an xml-stream
 * @author Imi (immanuel.scholz@gmx.de)
 */
public class XMLWriter implements TreeWriter {

    private PrintWriter out;
    private String indent = "";

    private void doWrite(String v, String name) {
        if (v.length() == 0)
            out.println(indent+"<"+name+" />");
        else
            out.println(indent+"<"+name+">"+v+"</"+name+">");
    }

    /**
     * Create a XMLWriter which outputs to a specific PrintWriter
     */
    public XMLWriter(PrintWriter out) {
        this.out = out;
    }

    /**
     * @see common.persistence.TreeWriter#write(int, java.lang.String)
     */
    public void write(int v, String name) {
        doWrite(String.valueOf(v), name);
    }

    /**
     * @see common.persistence.TreeWriter#write(boolean, java.lang.String)
     */
    public void write(boolean v, String name) {
        doWrite(String.valueOf(v), name);
    }

    /**
     * @see common.persistence.TreeWriter#write(double, java.lang.String)
     */
    public void write(double v, String name) {
        doWrite(String.valueOf(v), name);
    }

    /**
     * @see common.persistence.TreeWriter#write(java.lang.String, java.lang.String)
     */
    public void write(String v, String name) {
        doWrite(v, name);
    }

    /**
     * @see common.persistence.TreeWriter#flush()
     */
    public void flush() {
        out.flush();
    }

    /**
     * @see common.persistence.TreeWriter#close()
     */
    public void close() {
        out.close();
    }

    /**
     * @see common.persistence.TreeWriter#startDataBlock(java.lang.String)
     */
    public void startDataBlock(String name) {
        out.println("<"+name+">");
        indent += "  ";
    }

    /**
     * @see common.persistence.TreeWriter#endDataBlock(java.lang.String)
     */
    public void endDataBlock(String name) {
        indent = indent.substring(0,indent.length()-2);
        out.println("</"+name+">");
    }

    /**
     * @see common.persistence.TreeWriter#write(common.persistence.MMNetSerializable, java.lang.String)
     */
    public void write(MMNetSerializable v, String name) {
        startDataBlock(name);
        v.binOut(this);
        endDataBlock(name);
    }

    /**
     * @see common.persistence.TreeWriter#write(java.util.Collection, java.lang.String)
     */
    public void write(Collection v, String name) {
        out.println("<"+name+" size="+v.size()+">");
        indent += "  ";
        for (Iterator it = v.iterator(); it.hasNext();)
            write((MMNetSerializable)it.next(), name);
        endDataBlock(name);
    }
}
