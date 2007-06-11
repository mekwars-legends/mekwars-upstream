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

package server.campaign;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;
import java.awt.Dimension;

import common.AdvanceTerrain;
import common.CampaignData;
import common.Continent;
import common.Influences;
import common.PlanetEnvironment;
import common.Unit;
import common.UnitFactory;
import common.util.Position;

import server.MMServ;
import server.campaign.data.TimeUpdatePlanet;

@SuppressWarnings({"unchecked","serial"})
public class SPlanet extends TimeUpdatePlanet implements Serializable,
Comparable {
	
	private SHouse owner = null;
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("PL#");
		result.append(getName());
		result.append("#");
		result.append(getCompProduction());
		if (getUnitFactories() != null) {
			result.append("#" + getUnitFactories().size());
			for (int i = 0; i < getUnitFactories().size(); i++) {
				SUnitFactory MF = (SUnitFactory) getUnitFactories().get(i);
				result.append("#" + MF.toString());
/**
 * Commenting this out temporarily.
 */	
			//if(CampaignMain.cm.isUsingMySQL())
			//  CampaignMain.cm.MySQL.saveFactory(MF);
				
			}
		} else
			result.append("#0");
		
		result.append("#" + getPosition().getX());
		result.append("#" + getPosition().getY());
		result.append("#");
		Iterator it = getInfluence().getHouses().iterator();
		while (it.hasNext()) {
			SHouse next = (SHouse) it.next();
			result.append(next.getName());
			result.append("$"); // change for unusual influence
			result.append(getInfluence().getInfluence(next.getId()));
			result.append("$"); // change for unusual influence
		}
		result.append("#");
		result.append(getEnvironments().size());
		result.append("#");
		for (it = getEnvironments().iterator(); it.hasNext();) {
			Continent t = (Continent) it.next();
			result.append(t.getSize());
			result.append("#");
			result.append(t.getEnvironment().getName());
			result.append("#");
			if (CampaignMain.cm.getBooleanConfig("UseStaticMaps")){
				AdvanceTerrain aTerrain = this.getAdvanceTerrain().get(new Integer(t.getEnvironment().getId()));
				if ( aTerrain == null )
					aTerrain = new AdvanceTerrain(); //no data start it over. first time starting advance maps.
				if ( aTerrain.getDisplayName().length() <= 1)
					aTerrain.setDisplayName(t.getEnvironment().getName());
				result.append(aTerrain.toString());
				result.append("#");
			}
		}
		if (getDescription().equals(""))
			result.append(" ");
		else
			result.append(getDescription());
		result.append("#");
		result.append(this.getBaysProvided());
		result.append("#");
		result.append(this.isConquerable());
		result.append("#");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		result.append(sdf.format(this.getLastChanged()));
		result.append("#");
		result.append(getId());
		result.append("#");
		result.append(getMapSize().width);
		result.append("#");
		result.append(getMapSize().height);
		result.append("#");
		result.append(getBoardSize().width);
		result.append("#");
		result.append(getBoardSize().height);
		result.append("#");
		result.append(getTemp().width);
		result.append("#");
		result.append(getTemp().height);
		result.append("#");
		result.append(getGravity());
		result.append("#");
		result.append(isVacuum());
		result.append("#");
		result.append(getNightChance());
		result.append("#");
		result.append(getNightTempMod());
		result.append("#");
		result.append(this.getMinPlanetOwnerShip());
		result.append("#");
		result.append(isHomeWorld());
		result.append("#");
		result.append(getOriginalOwner());
        result.append("#");
		
        if ( this.getPlanetFlags().size() > 0){
            for ( String key : this.getPlanetFlags().keySet() ){
                result.append(key+"^");
            }
                
            result.append("#");
        }
        else
            result.append("^^#");
        
		return result.toString();
	}
	
	/**
	 * 
	 */
	public String fromString(String s, Random r, CampaignData data) {
		//debug
		MMServ.mmlog.mainLog(s);
		s = s.substring(3);
		StringTokenizer ST = new StringTokenizer(s, "#");
		setName(ST.nextToken());
		setCompProduction(Integer.parseInt(ST.nextToken()));
		//Read Factories
		if(!CampaignMain.cm.isUsingMySQL()){
			int hasMF = Integer.parseInt(ST.nextToken());
			for (int i = 0; i < hasMF; i++) {
				SUnitFactory mft = new SUnitFactory();
				mft.fromString(ST.nextToken(), this, r);
				getUnitFactories().add(mft);
			}
		}
		else
		{
			// Load from the database
			CampaignMain.cm.MySQL.loadFactories(this);	
			int hasMF = Integer.parseInt(ST.nextToken());
			for (int i = 0; i < hasMF; i++)
				ST.nextToken();	
		}		
		setPosition(new Position(Double.parseDouble(ST.nextToken()),
		Double.parseDouble(ST.nextToken())));
		
		int Infcount = 0;
		try {
			HashMap influence = new HashMap();
			{
				StringTokenizer influences = new StringTokenizer(ST.nextToken(), "$");
				while (influences.hasMoreElements()) {
					String HouseName = influences.nextToken();
					SHouse h = (SHouse)data.getHouseByName(HouseName);
					Integer HouseInf = new Integer(influences.nextToken());
					Infcount += HouseInf.intValue();
					if (h != null) 
						influence.put(new Integer(h.getId()), HouseInf);
					else
						MMServ.mmlog.errLog("House not found: " + HouseName);
				}
			}
			//getInfluence().setInfluence(influence);
			setInfluence(new Influences(influence));
		} catch (RuntimeException ex) {
			MMServ.mmlog.errLog("Problem on Planet: " + this.getName());
			MMServ.mmlog.errLog(ex);
		}
		if (ST.hasMoreElements()) {
			int Envs = Integer.parseInt(ST.nextToken());
			for (int i = 0; i < Envs; i++) {
				int size = Integer.parseInt(ST.nextToken());
				String terrain =  ST.nextToken();
				int terrainNumber = 0;
				PlanetEnvironment planetEnvironment = null;
				
				/*Bug reported if you screw with the positions of the terrains in
				 * terrain.xml you'll screw up the planet terrains
				 * this will now allow you to load via int and then save via name
				 * so the terrain will always be correct no matter the position of the terrain
				 * in the terrain.xml.  
				 */
				try{
					terrainNumber = Integer.parseInt(terrain);
					planetEnvironment = data.getTerrain(terrainNumber);
				}catch(Exception ex){
					planetEnvironment = data.getTerrainByName(terrain);
				}
				
				Continent PE = new Continent(size,planetEnvironment);
				if (CampaignMain.cm.getBooleanConfig("UseStaticMaps")){
					AdvanceTerrain aTerrain = new AdvanceTerrain();
					
					String tempHolder = ST.nextToken();
					if ( tempHolder.indexOf("$") < 0 ){
						aTerrain.setDisplayName(tempHolder);
						aTerrain.setXSize(Integer.parseInt(ST.nextToken()));
						aTerrain.setYSize(Integer.parseInt(ST.nextToken()));
						aTerrain.setStaticMap(Boolean.parseBoolean(ST.nextToken()));
						aTerrain.setXBoardSize(Integer.parseInt(ST.nextToken()));
						aTerrain.setYBoardSize(Integer.parseInt(ST.nextToken()));
						aTerrain.setLowTemp(Integer.parseInt(ST.nextToken()));
						aTerrain.setHighTemp(Integer.parseInt(ST.nextToken()));
						aTerrain.setGravity(Double.parseDouble(ST.nextToken()));
						aTerrain.setVacuum(Boolean.parseBoolean(ST.nextToken()));
						aTerrain.setNightChance(Integer.parseInt(ST.nextToken()));
						aTerrain.setNightTempMod(Integer.parseInt(ST.nextToken()));
						aTerrain.setStaticMapName(ST.nextToken());
					}
					else{
						aTerrain = new AdvanceTerrain(tempHolder);
					}
					this.getAdvanceTerrain().put(new Integer(PE.getEnvironment().getId()),aTerrain);
				}
				getEnvironments().add(PE);
			}
		}
		if (ST.hasMoreElements())
			setDescription(ST.nextToken());
		if (ST.hasMoreElements())
			this.setBaysProvided(Integer.parseInt(ST.nextToken()));
		if (ST.hasMoreElements())
			setConquerable(Boolean.parseBoolean(ST.nextToken()));
		if (ST.hasMoreElements()){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			try{
				setTimestamp(sdf.parse(ST.nextToken()));
			} catch (Exception ex) {
				//No biggy, but will cause senseless Data transfer, so:
				MMServ.mmlog.errLog("The following excepion is not critical, but will cause useless bandwith usage: please fix!");
				MMServ.mmlog.errLog(ex);
				setTimestamp(new Date(0));
			}
		} else
			setTimestamp(new Date(0));
		if (ST.hasMoreElements()){
			setId(Integer.parseInt(ST.nextToken()));
		}
		if (ST.hasMoreElements()){
			int x = (Integer.parseInt(ST.nextToken()));
			int y = (Integer.parseInt(ST.nextToken()));
			setMapSize(new Dimension(x,y));
		}
		if (ST.hasMoreElements()){
			int x = (Integer.parseInt(ST.nextToken()));
			int y = (Integer.parseInt(ST.nextToken()));
			setBoardSize(new Dimension(x,y));
		}
		if ( ST.hasMoreElements())
		{
			int x = (Integer.parseInt(ST.nextToken()));
			int y = (Integer.parseInt(ST.nextToken()));
			setTemp(new Dimension(x,y));
		}
		if (ST.hasMoreElements()){
			setGravity(Double.parseDouble(ST.nextToken()));
		}
		if (ST.hasMoreElements()){
			setVacuum(Boolean.parseBoolean(ST.nextToken()));
		}
		if ( ST.hasMoreElements())
		{
			int chance = (Integer.parseInt(ST.nextToken()));
			int mod = (Integer.parseInt(ST.nextToken()));
			setNightChance(chance);
			setNightTempMod(mod);
		}
		if ( ST.hasMoreElements())
			setMinPlanetOwnerShip(Integer.parseInt(ST.nextToken()));
		if ( ST.hasMoreTokens() )
			setHomeWorld(Boolean.parseBoolean(ST.nextToken()));
		if ( ST.hasMoreTokens() )
			setOriginalOwner(ST.nextToken());
		if ( ST.hasMoreTokens() ){
		    StringTokenizer str = new StringTokenizer(ST.nextToken(),"^");
            TreeMap< String, String> map = new TreeMap<String, String>();
            while ( str.hasMoreTokens() ){
                String key = str.nextToken();
                if ( CampaignMain.cm.getData().getPlanetOpFlags().containsKey(key))
                    map.put(key,CampaignMain.cm.getData().getPlanetOpFlags().get(key));
            }
            this.setPlanetFlags(map);
        }
		setOwner(null,checkOwner(),false);
		
		return s;
	}
	
	/**
	 * Use the other constructor as soon as you do not need the manual 
	 * serialization support through fromString() anymore. 
	 * 
	 */
	public SPlanet() {
		//super(CampaignMain.cm.getData().getUnusedPlanetID(),"", new Position(0,0), null);
        super();
		setTimestamp(new Date(0));
        setOriginalOwner(CampaignMain.cm.getConfig("NewbieHouseName"));
	}
	
	public SPlanet(int id, String name, Influences flu, int income, int CompProd, double xcood, double ycood) {
		super(id, name, new Position(xcood, ycood), flu);
		setCompProduction(CompProd);
		setTimestamp(new Date(0));
        setOriginalOwner(CampaignMain.cm.getConfig("NewbieHouseName"));
	}
	
	public SUnitFactory getRandomUnitFactory() {
		if (getUnitFactories().size() == 0)
			return null;
		//else
		return (SUnitFactory) getUnitFactories().get(CampaignMain.cm.getR().nextInt(getUnitFactories().size()));
	}
	
	public SUnitFactory getBestUnitFactory() {
		if (getUnitFactories().size() == 0) return null;
		SUnitFactory result = null;
		for (int i = 0; i < getUnitFactories().size(); i++) {
			SUnitFactory MF = (SUnitFactory) getUnitFactories().get(i);
			if (result == null)
				result = MF;
			else {
				if (MF.getWeightclass() > result.getWeightclass()) {
					result = MF;
				} else if (MF.getWeightclass() == result.getWeightclass()) {
					if (MF.getBestTypeProducable() < result.getBestTypeProducable()) result = MF;
				}
			}
		}
		return result;
	}
	
	public Vector<SUnitFactory> getFactoriesByName(String s) {
		Vector<SUnitFactory> result = new Vector<SUnitFactory>();
		for (int i = 0; i < getUnitFactories().size(); i++) {
			SUnitFactory MF = (SUnitFactory) getUnitFactories().get(i);
			if (MF.getName().equals(s)) result.add(MF);
		}
		return result;
	}
	
	public Vector<SUnitFactory> getFactoriesOfWeighclass(int weightclass) {
		Vector<SUnitFactory> result = new Vector<SUnitFactory>();
		for (int i = 0; i < getUnitFactories().size(); i++) {
			SUnitFactory MF = (SUnitFactory) getUnitFactories().get(i);
			if (MF.getWeightclass() == weightclass) result.add(MF);
		}
		return result;
	}
	
	/**
	 * @param Attacker - attacking faction
	 * @return potential defending hosues (ie - those with territory on the world)
	 */
	public Vector getDefenders(SHouse Attacker) {
		Vector result = new Vector();
		Iterator it = getInfluence().getHouses().iterator();
		while (it.hasNext()) {
			SHouse h = (SHouse) it.next();
			if (!h.equals(Attacker) || Attacker.isInHouseAttacks())
				result.add(h);
		}
		return result;
	}
	
	@Override
	public boolean equals(Object o) {
		
		SPlanet p = null;
		try {
			p = (SPlanet)o;
		} catch (ClassCastException e) {
			return false;
		}
		
		if (o == null)
			return false;
		
		return p.getId() == this.getId();
	}
	
	/**
	 * Do a tick - call tick on he planets MF, if it has one, and return the
	 * amount of income generated by the planet Income = base income * the
	 * number of miniticks registered at a tick
	 */
	public String tick(int refreshminiticks) {
		//Tick all Factories
		StringBuilder hsUpdates = new StringBuilder();
		for (int i = 0; i < getUnitFactories().size(); i++) {
			SUnitFactory MF = (SUnitFactory) getUnitFactories().get(i);
			int total = 0;
			if (MF.canProduce(Unit.MEK)) total += refreshminiticks;
			if (MF.canProduce(Unit.VEHICLE)) total += refreshminiticks;
			if (MF.canProduce(Unit.INFANTRY)) total += refreshminiticks;
			if (MF.canProduce(Unit.PROTOMEK)) total += refreshminiticks;
			if (MF.canProduce(Unit.BATTLEARMOR)) total += refreshminiticks;
			hsUpdates.append(MF.addRefresh(-total,false));
		}
		return hsUpdates.toString();
	}
	
	public String getSmallStatus(boolean useHTML) {
		
		StringBuilder result = new StringBuilder();
		if (useHTML)
			result.append(this.getNameAsColoredLink());
		else
			result.append(getName());
	
		for (int i = 0; i < getUnitFactories().size(); i++) {
			SUnitFactory MF = (SUnitFactory) getUnitFactories().get(i);
			result.append(" [" + MF.getSize() + "," + MF.getFounder() + "," + MF.getTypeString() + "]");
		}
		
		result.append(":");
		Iterator it = getInfluence().getHouses().iterator();
		while (it.hasNext()) {
			SHouse h = (SHouse) it.next();
			result.append(h.getName() + "(" + getInfluence().getInfluence(h.getId()) + "%)");
			if (it.hasNext())
				result.append(", ");
			else if (useHTML) result.append("<br>");
		}
		return result.toString();
	}
	
	public SHouse checkOwner() {
		
		if (getInfluence() == null) {
			MMServ.mmlog.errLog("getINF == null Planet: " + getName());
			return null;
		}
		
		SHouse h = null;
		Integer houseID = this.getInfluence().getOwner();
		
		if ( houseID == null )
			return null;
		
		h = (SHouse)CampaignMain.cm.getData().getHouse(houseID);
		
		if ( this.getInfluence().getInfluence(houseID) < this.getMinPlanetOwnerShip() )
			return null;
		
		return h;
	}
	
	public SHouse getOwner() {
		/*
		 * Null owner is possible, but should be uncommon. Check the
		 * owner again to make sure the this is true before returning.
		 */
		if (owner == null)
			checkOwner();
		return owner;
	}
	
	
	public void setOwner(SHouse oldOwner, SHouse newOwner, boolean sendHouseUpdates) {
				
        if (owner != null)//this is the same as oldowner in most cases
            owner.removePlanet(this);
        
		if (newOwner != null) { 
			owner = newOwner;
			owner.addPlanet(this);
		}
		
		if (sendHouseUpdates)
			this.sendHouseStatusUpdate(oldOwner, newOwner);
	}
	
	
	public int doGainInfluence(SHouse winner, SHouse loser, int amount, boolean adminExchange) {
		
		if (!winner.isConquerable() && !adminExchange)
			return 0;
		
		int infgain = getInfluence().moveInfluence(winner, loser, amount);
		//dont bother with updates if land has not changed hands.
        if (infgain > 0) {
        	
    		//winner.updated();
    		//loser.updated();
    		this.updated();
    		
    		SHouse oldOwner = owner;
    		SHouse newOwner = checkOwner();
    		setOwner(oldOwner, newOwner, true);
        }
		return infgain;
	}
	
	/*
	 * Helper method that sends updates to online players when a world changes hands.
	 */
	private void sendHouseStatusUpdate(SHouse oldOwner, SHouse newOwner) {
		
		//don't do anything if there's no change is ownership
		if (oldOwner != null && oldOwner.equals(newOwner))
			return;
		else if (oldOwner == null && newOwner == null)
			return;

		//if the world has factories, build strings to send
		StringBuilder oldOwnerHSUpdates = new StringBuilder();
		StringBuilder newOwnerHSUpdates = new StringBuilder();
		for (UnitFactory currUF : getUnitFactories()) {

			oldOwnerHSUpdates.append("RF|" + currUF.getWeightclass() + "$" + currUF.getType()
					+ "$" + this.getName() + "$" + currUF.getName() + "|");

			newOwnerHSUpdates.append("AF|" + currUF.getWeightclass() + "$" + currUF.getType()
					+ "$" + currUF.getFounder() + "$" + this.getName() + "$" + currUF.getName()
					+ "$" + currUF.getTicksUntilRefresh() + "|");
		}

		//send updates to non-null houses, so long as update strings have length > 0 (real updates)
		if (oldOwner != null && oldOwnerHSUpdates.length() > 0)
			CampaignMain.cm.doSendToAllOnlinePlayers(oldOwner, "HS|" + oldOwnerHSUpdates.toString(), false);
		if (newOwner != null && newOwnerHSUpdates.length() > 0)
			CampaignMain.cm.doSendToAllOnlinePlayers(newOwner, "HS|" + newOwnerHSUpdates.toString(), false);

    }
	
	public String getShortDescription(boolean withTerrain) {
		StringBuilder result = new StringBuilder(getName());
		if (withTerrain) {
			Continent p = getEnvironments().getBiggestEnvironment();
			PlanetEnvironment pe = p.getEnvironment();
			if (pe != null)
				result.append(" " + pe.toImageDescription());
			
			if (this.getUnitFactories().size() > 0) {
				for (int i = 0; i < this.getUnitFactories().size(); i++) {
					SUnitFactory MF = ((SUnitFactory) this.getUnitFactories().get(i));
					result.append(MF.getIcons());
				}
			}
			if (pe != null && getEnvironments().getTotalEnivronmentPropabilities() > 0)
				result.append(" (" + Math.round((double) p.getSize() * 100 / getEnvironments() .getTotalEnivronmentPropabilities()) + "% correct)");
			else
				result.append(" (100% correct)");
		}
		return result.toString();
	}
	
	/**
	 * Method which returns a coloured link name for a planet.
	 */
	public String getNameAsColoredLink() {
		
		String colorString = "";
		if (owner == null) {
			colorString = CampaignMain.cm.getConfig("DisputedPlanetColor");//malformed gets you black?
		} else 
			colorString = owner.getHouseColor();
		
		String toReturn = "<font color=\"" + colorString + "\">" + getNameAsLink() + "</font>";	
		return toReturn;
	}
	
	@Override
	public int getMinPlanetOwnerShip(){
        
		int ownership = super.getMinPlanetOwnerShip();
		if (ownership < 0)
			ownership = CampaignMain.cm.getIntegerConfig("MinPlanetOwnerShip");
		
		return ownership;
	}
	
}