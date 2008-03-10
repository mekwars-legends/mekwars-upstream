/*
 * MekWars - Copyright (C) 2008 
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

/**
 * 
 * @author Torren (Jason Tighe) 3.9.08 
 * 
 */

package server.util;

import server.campaign.SUnit;

public class ComponentToCritsConverter{

    private int minCritLevel = 10;
    private int componentUsedType = SUnit.MEK;
    private int componentUsedWeight = SUnit.LIGHT;
    private String critName = "All";
    
    /**
     * 
     * @param int level
     */
    public void setMinCritLevel(int level) {
        this.minCritLevel = level;
    }
    
    /**
     * 
     * @return int
     */
    public int getMinCritLevel() {
        return this.minCritLevel;
    }
    
    /**
     * 
     * @param int type
     */
    public void setComponentUsedType(int type) {
        this.componentUsedType = type;
    }

    /**
     * 
     * @return int
     */
    public int getComponentUsedType() {
        return this.componentUsedType;
    }
    
    /**
     * 
     * @param int weight
     */
    public void setComponentUsedWeight(int weight) {
        this.componentUsedWeight = weight;
    }
    
    /**
     * 
     * @return int
     */
    public int getComponentUsedWeight() {
        return this.componentUsedWeight;
    }
    
    /**
     * 
     * @param String crit
     */
    public void setCritName(String crit) {
        this.critName = crit;
    }
    
    /**
     * 
     * @return string
     */
    public String getCritName() {
        return this.critName;
    }
    
    public String toString() {
        StringBuffer results = new StringBuffer();
        
        results.append(critName);
        results.append(" |");
        results.append(minCritLevel);
        results.append("|");
        results.append(componentUsedType);
        results.append("|");
        results.append(componentUsedWeight);
        results.append("|");
        
        return results.toString();
    }
}
