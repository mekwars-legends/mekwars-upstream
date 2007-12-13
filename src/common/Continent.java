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

/*
 * Created on 04.05.2004
 *
 */
package common;

import common.persistence.MMNetSerializable;
import common.persistence.TreeReader;
import common.persistence.TreeWriter;

/**
 * @author Helge Richter
 */
public class Continent implements MMNetSerializable {
	private PlanetEnvironment environment;
	private int size = 1;
	
	public Continent(int Size, PlanetEnvironment env) {
		this.size = Size;
		environment = env;
	}
	
	public Continent() {
		// for serialisation
	}
	

	/**
	 * @return Returns the size.
	 */
	public int getSize() {
		return size;
	}
	/**
	 * @param size The size to set.
	 */
	public void setSize(int size) {
		this.size = size;
	}
	
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Continent))
			return false;
		Continent cont = (Continent)o;
		if (cont.getSize() != getSize())
			return false;
		if (cont.getEnvironment().equals(getEnvironment()))
			return false;
		return true;
	}
	/**
	 * @return Returns the envID.
	 */
	public PlanetEnvironment getEnvironment() {
		return environment;
	}
	
   public void binOut(TreeWriter out)
   {
       out.write(getEnvironment().getId(), "envID");
       out.write(getSize(), "size");
   }
   public void binIn(TreeReader in, CampaignData data){
   }
}
