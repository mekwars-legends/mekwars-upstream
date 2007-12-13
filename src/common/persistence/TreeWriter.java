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

import java.util.Collection;


/**
 * This is an interface to write tree liked hirachical data to a stream. XML are
 * one example of such data and so XMLTreeWriter is writing XML data. ;)
 * 
 * Each write - function taking two arguments, an value and the name of the
 * data. This name is not neccesarry needed by all implementations of 
 * TreeWriter, but if you want type safety or something similar, you will use
 * it ;).
 * 
 * Also every write will first close chield the data block (if any open).  
 * 
 * @author Imi (immanuel.scholz@gmx.de)
 */
public interface TreeWriter {
    public void write(int v, String name);
    public void write(boolean v, String name);
    public void write(double v, String name);
    public void write(String v, String name);
    public void write(MMNetSerializable v, String name);
    public void write(Collection<?> v, String name);
    
    /**
     * Flushes the stream. This should make sure, the data is actually written 
     * to the stream and not buffered somewhere. 
     */
    public void flush();
    /**
     * Closes the stream. 
     */
    public void close();
    
    /**
     * Starts a new data block (means, go deeper in the tree hirarchy)
     */
    public void startDataBlock(String name);
    /**
     * Ends the given data block.
     */
    public void endDataBlock(String name);
}
