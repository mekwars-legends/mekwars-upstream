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

import java.io.IOException;
import java.util.Collection;

import common.CampaignData;



/**
 * This provides an Reader interface to read back from a stream wrote to with
 * a TreeWriter.
 *
 * @author Imi (immanuel.scholz@gmx.de)
 */
public interface TreeReader {
    
    /**
     * Reads an integer
     */
    public int readInt(String name) throws IOException;

    /**
     * Reads an double
     */
    public double readDouble(String name) throws IOException;

    /**
     * Reads an boolean
     */
    public boolean readBoolean(String name) throws IOException;

    /**
     * Reads an string
     */
    public String readString(String name) throws IOException;

    /**
     * Fills an object from the stream
     */
    public void readObject(MMNetSerializable obj, 
            CampaignData dataProvider, 
            String name) throws IOException;

    /**
     * Fills an Collection from the stream. The elements must be of the type
     * MMNetSerializable and of the same type. This type must have a default
     * constructor.
     * @param col The collection to read the data into.
     * @param cl The class of the elements to be read. If you pass null as this,
     *      framework drivers may try to autodetect the type depend on
     *      informations in the data file, but this does not work with all 
     *      drivers.
     */
    public void readCollection(Collection<?> col, 
            Class<?> cl, 
            CampaignData dataProvider,
            String name) throws IOException;

    /**
     * Read the start of a new data block.
     */
    public void startDataBlock(String name) throws IOException;

    /**
     * Read the end of a data block.
     */
    public void endDataBlock(String name) throws IOException;
    
    /**
     * Closes the input.
     */
    public void close() throws IOException;
}
