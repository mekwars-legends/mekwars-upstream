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

package common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import common.util.BinReader;
import common.util.BinWriter;


/**
 * Represents a collection of continents, usually for one planet
 * 
 * @author Imi (immanuel.scholz@gmx.de)
 * seen, modified and made totally bad by McWizard
 *
 * Imi: *crhm*..."totally bad"... ;-)
 * TODO: simplify this class. subclass it from ArrayList or something like that   
 */
@SuppressWarnings({"unchecked","serial"})
public class PlanetEnvironments {

    /**
     * An terrain provider to get terrain information from.
     */
    public static transient TerrainProvider data;

    /**
     * The list of all continents. Type=Continent
     */
    private ArrayList continents = new ArrayList();

    /**
     * Iterate over all terrains in this set.
     */
    public Iterator<Continent> iterator() {
    	return continents.iterator();
    }

    /**
     * Returns the number of terrains in this set.
     */
    public int size() {
        return continents.size();
    }

    /**
     * Return all Environments as an array. You get a copy of the actual data,
     * so modifying is pointless!
     */
    public Continent[] toArray() {
        Continent[] ret = new Continent[size()];
        int i = 0;
        for (Iterator it = iterator(); it.hasNext();)
            ret[i++] = (Continent)it.next();
        return ret;
    }

    /**
     * Add a terrain to the current set. This will vanish, when Terrains are
     * initialized through XStream.
     * @TODO You should not need this and you should only initialize the terrain set with either XStream or binIn()
     */
    synchronized public void add(Continent newPE) {
        continents.add(newPE);
    }

    synchronized public void remove(String terrain) {

    	int count = 0;
    	for ( Object land : continents ){

    		//Check for multiple terrains with the same name.
    		if ( ((Continent)land).getEnvironment().getName().equals(terrain) ){
    			break;
    		}
    		count++;
    	}
    	
    	if ( count < continents.size() ) {
			continents.remove(count);
			continents.trimToSize();
    	}
    }

    synchronized public void removeAll() {
		continents.clear();
    }

    /**
     * Return the environment with the most probability to occour.
     */
    public Continent getBiggestEnvironment() {
        Continent result = new Continent(0,new PlanetEnvironment());
        for (Iterator it = iterator(); it.hasNext();) {
            Continent p = (Continent)it.next();
            if (p.getSize() > result.getSize()) result = p;
        }
        return result;
    }

    /**
     * Return the total probability of all environments.
     */
    public int getTotalEnivronmentPropabilities() {
        int result = 0;
        for (Iterator it = iterator(); it.hasNext();)
            result += ((Continent)it.next()).getSize();
        return result;
    }

    /**
     * Returns a randomEnvironment based on the propability of each
     * Environment.
     */
    public PlanetEnvironment getRandomEnvironment(Random r) {
        // use the skewer draw algorithm from Knuth.
        int probs = getTotalEnivronmentPropabilities();
        for (Iterator it = iterator(); it.hasNext();) {
            Continent pe = (Continent) it.next();
            if (r.nextInt(probs) < pe.getSize())
                return pe.getEnvironment();
            probs -= pe.getSize();
        }
        return new PlanetEnvironment();
    }

    /**
     * Writes as binary stream
     */
    public void binOut(BinWriter out) throws IOException {
        out.println(size(), "terrain.size");
        for (Iterator it = continents.iterator(); it.hasNext();) {
            Continent cont =(Continent)it.next(); 
            out.println(cont.getSize(),"size");
            out.println(cont.getEnvironment().getId(),"id");
        }
    }

    /**
     * Read from a binary stream
     */
    public void binIn(BinReader in, CampaignData data) throws IOException {
        int size = in.readInt("terrain.size");
        for (int i = 0; i < size; ++i)
        	add(new Continent(in.readInt("size"), data.getTerrain(in.readInt("id"))));
    }

    /**
     * @see common.persistence.MMNetSerializable#binOut(common.persistence.TreeWriter)
     *
    public void binOut(TreeWriter out) {
        out.write(size(), "terrain.size");
        for (Iterator it = continents.iterator(); it.hasNext();) {
            Continent cont = (Continent)it.next(); 
            out.write(cont.getSize(),"size");
            out.write(cont.getEnvironment().getId(),"id");
        }
    }

    /**
     * @see common.persistence.MMNetSerializable#binIn(common.persistence.TreeReader, common.CampaignData)
     *
    public void binIn(TreeReader in, CampaignData dataProvider) throws IOException {
        int size = in.readInt("terrain.size");
        for (int i = 0; i < size; ++i)
            add(new Continent(in.readInt("size"),dataProvider.getTerrain(in.readInt("id"))));
    }*/
}
