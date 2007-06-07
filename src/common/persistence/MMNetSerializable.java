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

import common.CampaignData;



/**
 * Classes implement this to interact smootly with the MMNet-Serializable 
 * interface. As example you can put objects of this interface to the
 * Writer/Reader classes of the framework.
 * 
 * @author Imi (immanuel.scholz@gmx.de)
 */
public interface MMNetSerializable {
    /**
     * Serialize itself to the tree writer
     */
    void binOut(TreeWriter out);
    
    /**
     * Reconstruct itself from the tree writer
     * @param dataProvider Some classes may need additional information to
     *      fully reconstruct themself, as example because only id-information
     *      is stored. This additional data is provided through the 
     *      dataProvider class.
     */
    void binIn(TreeReader in, CampaignData dataProvider) throws IOException;
}
