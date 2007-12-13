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

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;



/**
 * Writes in a compact format, while retaining the structure informations. This
 * make it possible to detect modifications of data format.
 *
 * @author Imi (immanuel.scholz@gmx.de)
 */
public class DatWriter implements TreeWriter {

    PrintWriter out;
    String indent = "";

    public DatWriter(String filename) throws IOException {
        out = new PrintWriter(new FileWriter(filename));
    }

    private String ensureString(String name) {
    	//TODO: This is apparently a bug. = is changed to -, but the change isn't stored.
    	//The dat writer has worked a long time without this being right, so I'm not changing
    	//it now. @urgru 7.9.06
        name.replace('=','-');
        return name;
    }

    /**
     * @see common.persistence.TreeWriter#write(int, java.lang.String)
     */
    public void write(int v, String name) {
        out.println(indent+ensureString(name)+"="+v);
    }

    /**
     * @see common.persistence.TreeWriter#write(boolean, java.lang.String)
     */
    public void write(boolean v, String name) {
        out.println(indent+ensureString(name)+"="+v);
    }

    /**
     * @see common.persistence.TreeWriter#write(double, java.lang.String)
     */
    public void write(double v, String name) {
        out.println(indent+ensureString(name)+"="+v);
    }

    /**
     * @see common.persistence.TreeWriter#write(java.lang.String, java.lang.String)
     */
    public void write(String v, String name) {
        out.println(indent+ensureString(name)+"="+v);
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
    public void write(Collection<?> v, String name) {
        name.replace('[','(');
        name.replace(']',')');
        name = ensureString(name)+"["+v.size()+"]";
        startDataBlock(name);
        int i = 0;
        for (Object it : v)
            write((MMNetSerializable)it, String.valueOf(i++));
        endDataBlock(name);
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
        out.println(indent+ensureString(name)+"={");
        indent += "  ";
    }

    /**
     * @see common.persistence.TreeWriter#endDataBlock(java.lang.String)
     */
    public void endDataBlock(String name) {
        indent = indent.substring(0,indent.length()-2);
        out.println(indent+"}");
    }
}
