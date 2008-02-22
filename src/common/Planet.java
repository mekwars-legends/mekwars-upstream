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
 * Created on 23.03.2004
 *
 */
package common;

import java.awt.Dimension;
import java.io.IOException;
import java.io.File;
import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import common.util.BinReader;
import common.util.BinWriter;
import common.util.Position;

/**
 * @author Helge Richter
 *  
 */

public class Planet implements Comparable<Object>, MutableSerializable {

	//VARIABLES
	/**
     * Unique id of this planet.
     * Mutable field (although it will not change, it has to be transfered) 
     */
    private int id;

    /**
     * name of the planet. Should be unique among planets too.
     */
    private String name;

    /**
     * position of this planet in the inner spehre map. Ranges from about -700
     * to 700 in both directions.
     */
    private Position position;	// distance calculates faster, also fewer casts

    /**
     * The unit factories on this planet. Type is UnitFactory
     * Mutable field (has to be transfered)
     */
    private Vector<UnitFactory> unitFactories = new Vector<UnitFactory>(1,1);

    /**
     * The environment modifiers for the planet.
     */
    private PlanetEnvironments environments = new PlanetEnvironments();

    /**
     * A human readable description of the planet.
     */
    private String description = "";

    /**
     * Amount of bays to add to the faction holding the planet.
     */
    private int baysProvided = 0;

    /**
     * Whether you can conquer the planet with Conquer - task.
     */
    private boolean conquerable = true;

    /**
     * How much components are produced through this planet.
     */
    private int compProduction = 0;

    /**
     * The influence each faction has on this planet.
     * Mutable field (has to be transfered)
     */
    private Influences influence;

    
    /**
     * Map and board sizes are now stored as diminsions for static map
     * usage
     * Torren
     */
    private Dimension MapSize = new Dimension(1,1); //default megamek map size
    private Dimension BoardSize = new Dimension(16,17);//default megamek board size
  
    /**
     * Vars for temperature vaccum and gravity
     * @author jtighe
     */
    
    private Dimension temperature = new Dimension(25,25);
    private double gravity = 1.0;
    private boolean vacuum = false;
    
    private int nightChance = 0;
    private int nightTempMod = 0;

    private Hashtable<Integer,AdvancedTerrain> advanceTerrain = new Hashtable<Integer,AdvancedTerrain>();
    
    /**
     * Min Planet ownership to allow a faction to use the planets resources
     * defaults to -1 so that the server wide on is used.
     */
    
    private int minPlanetOwnerShip = -1;
    
    /**
     * Boolean that states if a planet is a homeworld or not
     */

    private boolean homeWorld = false;

    /* Original Owner of the planet */
    private String originalOwner = "";
    
    /*
     * This allows SO's to set flags for planets and to be used in ops.
     */
    private TreeMap<String, String> planetFlags = new TreeMap<String, String>();
    
    /*
     * Max Planet Points. this Allows SO's to set the conquer points of a planet
     * That way some planets are harder to conquer then others.
     */
    private int maxConquestPoints = 100;

    /*
     * Planet Database ID, used in saving and retreiving the planet from the database.
     * Set this to zero, which will queue the initial save routine that it needs to insert
     * rather than update.  The save routine will then assign it a dbID.
     */
    private int DBID = 0;
    
    //CONSTRUCTORS
    public Planet(int id, String name, Position position, Influences influence) {
    	this.setId(id);
    	this.setName(name);
    	this.setPosition(position);
    	this.setInfluence(influence);
    }

    /**
     * Used for serialization
     */
    public Planet() {
    	//no content
    }
    
    /**
     * Read the stream back to a Planet object.
     */
    public Planet(BinReader in, Map<Integer,House> factions, CampaignData data) throws IOException {
    	this.setId(new Integer(in.readInt("id")));
    	this.setName(in.readLine("name"));
    	this.setPosition(new Position(in.readDouble("x"), in.readDouble("y")));
        int size = in.readInt("unitFactories.size");
    	this.setUnitFactories(new Vector<UnitFactory>(size,1));
        for (int i = 0; i < size; ++i) {
            UnitFactory uf = new UnitFactory();
            uf.binIn(in);
            this.getUnitFactories().add(uf);
        }
        this.setEnvironments(new PlanetEnvironments());
        this.getEnvironments().binIn(in, data);
        this.setDescription(in.readLine("description"));
        this.setBaysProvided(in.readInt("baysProvided"));
        this.setConquerable(in.readBoolean("conquerable"));
        this.setCompProduction(in.readInt("compProduction"));
        this.setInfluence(new Influences());
        this.getInfluence().binIn(in,factions);
        this.setMapSize(new Dimension(in.readInt("x"),in.readInt("y")));
        this.setBoardSize(new Dimension(in.readInt("x"),in.readInt("y")));
        this.setTemp(new Dimension(in.readInt("lowtemp"),in.readInt("hitemp")));
        this.setGravity(in.readDouble("gravity"));
        this.setVacuum(in.readBoolean("vacuum"));
        this.setMinPlanetOwnerShip(in.readInt("minplanetownership"));
        this.setHomeWorld(in.readBoolean("homeworld"));
        this.setOriginalOwner(in.readLine("originalowner"));
        size = in.readInt("AdvancedTerrain.size");
        for (int i = 0; i < size; ++i) {
            AdvancedTerrain aTerrain = new AdvancedTerrain();
            int id = in.readInt("AdvancedTerrainId");
            aTerrain.binIn(in);
            this.getAdvancedTerrain().put(new Integer(id),aTerrain);
        }
        TreeMap<String, String> map = new TreeMap<String, String>();
        size = in.readInt("PlanetFlags.size");
        for (int i = 0; i < size; ++i) {
            String key;
            String value;
            key = in.readLine("PlanetFlags.key");
            value = in.readLine("PlanetFlags.value");
            map.put(key, value);
        }
        this.setPlanetFlags(map);
        this.setConquestPoints(in.readInt("MaxInfluence"));
    }
    
    //METHODS
    /**
     * @return Returns the baysProvided.
     */
    public int getBaysProvided() {
        return baysProvided;
    }

    /**
     * @param baysProvided
     *            The baysProvided to set.
     */
    public void setBaysProvided(int baysProvided) {
        this.baysProvided = baysProvided;
    }

    public int getDBID() {
    	return DBID;
    }
    
    public void setDBID(int ID) {
    	this.DBID = ID;
    }
    
    /**
     * @return Returns the compProduction.
     */
    public int getCompProduction() {
        return compProduction;
    }

    /**
     * @param compProduction
     *            The compProduction to set.
     */	
    public void setCompProduction(int compProduction) {
        this.compProduction = compProduction;
    }

    
    /**
     * @author Torren (Jason Tighe)
     * @return the id of the current owner of the planet
     */
    public Integer getPlanetOwner(){
        Integer ownerid = this.getInfluence().getOwner();
        return ownerid;
    }
    
    /**
     * @author Torren (Jason Tighe)
     * @param faction
     * @return returns if the faction is the planet owner
     */
    public boolean isOwner(int factionid){
        Integer ownerID = getPlanetOwner();
        if (ownerID == null)
            return false;
        return  ownerID == factionid;
    }

    /**
     * @return Returns the conquerable.
     */
    public boolean isConquerable() {
        return conquerable;
    }

    /**
     * @param conquerable
     *            The conquerable to set.
     */
    public void setConquerable(boolean conquerable) {
        this.conquerable = conquerable;
    }

    /**
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     *            The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }
    
    /**
     * 
     * @return sting w/ link and name
     */
    public String getNameAsLink() {
    	return "<a href=\"JUMPTOPLANET" + name + "#\">" + name + "</a>";    	
    }

    /**
     * @param name
     *            The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the position.
     */
    public Position getPosition() {
        return position;
    }

    /**
     * @param position
     *            The position to set.
     */
    public void setPosition(Position position) {
       this.position = position;
    }

    /**
     * @return Returns the Factories.
     */
    public Vector<UnitFactory> getUnitFactories() {
        return unitFactories;
    }

    /**
     * @param Factories The Factories to set.
     */
    public void setUnitFactories(Vector<UnitFactory> unitFactories) {
       this.unitFactories = unitFactories;
    }

    /**
     * @return Returns the environments.
     */
    public PlanetEnvironments getEnvironments() {
        return environments;
    }

    /**
     * @param environments
     *            The environments to set.
     */
    public void setEnvironments(PlanetEnvironments environments) {
      this.environments = environments;
    }

    /**
     * @return Returns the influence.
     */
    public Influences getInfluence() {
        return influence;
    }

    /**
     * @param influence
     *            The influence to set.
     */
    public void setInfluence(Influences influence) {
       this.influence = influence;
    }
    
    /**
     * checks for any unused CP and assignes them 
     * to House None id -1
     */
    public void updateInfluences() {
        int totalCP = getConquestPoints();
        
        for (House house: getInfluence().getHouses() ) {
            totalCP -= getInfluence().getInfluence(house.getId());
        }
        
        if ( totalCP > 0 ) {
            getInfluence().updateHouse(-1,totalCP);
        }
            
    }
    
    /**
     * Comparable after the id
     */
    public int compareTo(Object o) {
        Planet p = (Planet) o;
        return getId() < p.getId() ? -1 : (getId() == p.getId() ? 0 : 1);
    }
    
    /**
     * Encode all mutable fields into the stream. Use as few bits as possible.
     */
    public void encodeMutableFields(BinWriter out,CampaignData dataProvider) throws IOException {
    	out.println(getId(), "id");
    	getInfluence().encodeMutableFields(out,dataProvider);
    	binOut(out);
    }

    /**
     * Decode all mutable fields from the stream.
     */
    public void decodeMutableFields(BinReader in,CampaignData dataProvider) throws IOException {
        this.setId(new Integer(in.readInt("id")));
        this.getInfluence().decodeMutableFields(in,dataProvider);
        binIn(in,dataProvider);
    }
    
    /**
     * Write itself into the stream.
     */
    public void binOut(BinWriter out) throws IOException {
        out.println(this.getId(), "id");
        out.println(this.getName(), "name");
        out.println(this.getPosition().x, "x");
        out.println(this.getPosition().y, "y");
        out.println(this.getUnitFactories().size(), "unitFactories.size");
        for (UnitFactory i : this.getUnitFactories()) {
            i.binOut(out);
        }
        this.getEnvironments().binOut(out);
        out.println(this.getDescription(), "description");
        out.println(this.getBaysProvided(), "baysProvided");
        out.println(this.isConquerable(), "conquerable");
        out.println(this.getCompProduction(), "compProduction");
        this.getInfluence().binOut(out);
        out.println(this.getMapSize().width,"x");
        out.println(this.getMapSize().height,"y");
        out.println(this.getBoardSize().width,"x");
        out.println(this.getBoardSize().height,"y");
        out.println(this.getTemp().width,"lowtemp");
        out.println(this.getTemp().height,"hitemp");
        out.println(this.getGravity(),"gravity");
        out.println(this.isVacuum(),"vacuum");
        out.println(this.getMinPlanetOwnerShip(),"minplanetownership");
        out.println(this.isHomeWorld(),"homeworld");
        out.println(this.getOriginalOwner(),"originalowner");

        out.println(this.getAdvancedTerrain().size(),"AdvancedTerrain.size");
        for (Integer currI : this.getAdvancedTerrain().keySet()) {
            out.println(currI.intValue(),"AdvancedTerrainId");
            AdvancedTerrain aTerrain = this.getAdvancedTerrain().get(currI);
            aTerrain.binOut(out);    
        }
        
        out.println(this.getPlanetFlags().size(), "PlanetFlags.size");
        for (String key : this.getPlanetFlags().keySet()) {
            out.println(key, "PlanetFlags.key");
            out.println(this.getPlanetFlags().get(key), "PlayerFlags.value");
        }
        out.println(this.getConquestPoints(), "MaxInfluence");
    }


    public void binIn(BinReader in, CampaignData data) throws IOException {
    	this.setId(new Integer(in.readInt("id")));
    	this.setName(in.readLine("name"));
    	this.setPosition(new Position(in.readDouble("x"), in.readDouble("y")));
        int size = in.readInt("unitFactories.size");
    	this.setUnitFactories(new Vector<UnitFactory>(size,1));
        for (int i = 0; i < size; ++i) {
            UnitFactory uf = new UnitFactory();
            uf.binIn(in);
            this.getUnitFactories().add(uf);
        }
        this.setEnvironments(new PlanetEnvironments());
        this.getEnvironments().binIn(in, data);
        this.setDescription(in.readLine("description"));
        this.setBaysProvided(in.readInt("baysProvided"));
        this.setConquerable(in.readBoolean("conquerable"));
        this.setCompProduction(in.readInt("compProduction"));
        this.setInfluence(new Influences());
        this.getInfluence().binIn(in);
        this.setMapSize(new Dimension(in.readInt("x"),in.readInt("y")));
        this.setBoardSize(new Dimension(in.readInt("x"),in.readInt("y")));
        this.setTemp(new Dimension(in.readInt("lowtemp"),in.readInt("hitemp")));
        this.setGravity(in.readDouble("gravity"));
        this.setVacuum(in.readBoolean("vacuum"));
        this.setMinPlanetOwnerShip(in.readInt("minplanetownership"));
        this.setHomeWorld(in.readBoolean("homeworld"));
        this.setOriginalOwner(in.readLine("originalowner"));
        size = in.readInt("AdvancedTerrain.size");
        //CampaignData.mwlog.errLog("AdvancedTerrain.size");
        for (int i = 0; i < size; ++i) {
            AdvancedTerrain aTerrain = new AdvancedTerrain();
            int id = in.readInt("AdvancedTerrainId");
            //CampaignData.mwlog.errLog("AdvancedTerrainId "+id);
            aTerrain.binIn(in);
            this.getAdvancedTerrain().put(new Integer(id),aTerrain);
        }
        TreeMap<String, String> map = new TreeMap<String, String>();
        size = in.readInt("PlanetFlags.size");
        for (int i = 0; i < size; ++i) {
            String key;
            String value;
            key = in.readLine("PlanetFlags.key");
            value = in.readLine("PlanetFlags.value");
            map.put(key, value);
        }
        this.setPlanetFlags(map);
        
        this.setConquestPoints(in.readInt("MaxInfluence"));
    }

    /**
     * Returns a long description of this planet as html-code.
	 */
	public StringBuilder getLongDescription(boolean client) {
	            
		StringBuilder result = new StringBuilder("Information for Planet: <b>");
        result.append(getName()+"</b><br><br>");
        //    result.append("</b> ("+ getDescription() + ")<br><br>");
        result.append("<b>Location:</b> "+(int)getPosition().x+" x "+(int)getPosition().y+ " y<br>" + Math.round(getPosition().distanceSq(0.0, 0.0)) + " Lightyears from the galaxy center <br><br>");

		result.append("<b>Industry:</b><br>");
        // factories
        if (getCompProduction() > 0)
                result.append("Heavy industry allows an export of "
                        + getCompProduction() + " parts.<br>");
        if (getBaysProvided() > 0){
                result.append("A warehouse on this world provides all players with " + getBaysProvided()
                        + " extra bays.<br>");
        }
        
        if (getUnitFactories().size() > 0)
        {
            String founder = "";
            if ( getUnitFactories().size() == 1)
                result.append("<br><b>Factory:</b><br>");
            else
                result.append("<br><b>Factories:</b><br>");
            for (UnitFactory u : getUnitFactories()) {
            	founder = u.getFounder();
            	if (client)
            	    result.append("<img src=\"file:///"+ new File(".").getAbsolutePath()+"/data/images/open"+founder+".gif\">"+u.getSize()+" "
                            +u.getFullTypeString() + u.getName() + " built by " + founder +"<br>");
            	else
            	    result.append("<img src=\"./data/images/open"+founder+".gif\">"+u.getSize()+" "
                        +u.getFullTypeString() + u.getName() + " built by " + founder +"<br>");
            }
        }

        result.append("<br><b>Planetary Conditions</b><br>");
        result.append("Atmosphere: ");
        if ( isVacuum() )
            result.append("None<br>");
        else
            result.append("Present<br>");
        result.append("Gravity: "+getGravity()+"<br>");
        result.append("Average Low: "+getTemp().width+"<br>");
        result.append("Average High: "+getTemp().height+"<br>");
        
        result.append("<br><b>Terrain:</b><br>");
        int maxProbab = getEnvironments().getTotalEnivronmentPropabilities();
        if (getEnvironments().size() < 1)
            result.append("nothing special");
        else {
            for (Continent pe : getEnvironments().toArray()) {
                int curProb = (pe.getSize()*100/maxProbab); 
                if (curProb < 10)
                	result.append("0");
                result.append(curProb+"% ");
                if (client)
                    result.append(pe.getEnvironment().toImageAbsolutePathDescription());
                else
                    result.append(pe.getEnvironment().toImageDescription());
                
                String terrainName = pe.getEnvironment().getName();
                
                result.append(" " + terrainName);
                result.append("<br>");
            }
        }
       
        // influence
        result.append("<br><b>Influence:</b><br>");
        for (House h : getInfluence().getHouses()) {
            String color = "#999999";
            String name = "None";
            int id = -1;
            
            if ( h != null ) {
                color = h.getHouseColor();
                name = h.getName();
                id = h.getId();
            }

            result.append("<font color="+color+">"+name+ "</font> (" + getInfluence().getInfluence(id) + ")");
            result.append(", ");
        } // End for Each
        
        result.replace(result.length()-2, result.length(),"<br>");

        if ( this.getPlanetFlags().size() > 0){
            result.append("<br><b>Points of Intereset:</b><br>");
            for( String value : this.getPlanetFlags().values() ){
                result.append(value+", ");
            }
            result.replace(result.length()-2, result.length(),"<br> <br>");
        }//end if planet has flags
        return result;
	}

	/**
	 * @see  Only a hack! Only use if you know what you're doing!
	 * @param id The id to set.
	 */
	public void setId(int id) {
		this.id = id;
	}
	
    /**
     * @return Returns the id.
     */
    public int getId() {
        return id;
    }

    /**
     * @see common.persistence.MMNetSerializable#binOut(common.persistence.TreeWriter)
     *
    public void binOut(TreeWriter out) {
        out.write(this.getId(), "id");
        out.write(this.getName(), "name");
        out.write(this.getPosition().x, "x");
        out.write(this.getPosition().y, "y");
        out.write(this.getUnitFactories(), "unitFactories");
        out.write(this.getEnvironments(), "environments");
        out.write(this.getDescription(), "description");
        out.write(this.getBaysProvided(), "baysProvided");
        out.write(this.isConquerable(), "conquerable");
        out.write(this.getCompProduction(), "compProduction");
        out.write(this.getInfluence(), "influence");
        out.write(this.getMapSize().width,"x");
        out.write(this.getMapSize().height,"y");
        out.write(this.getBoardSize().width,"x");
        out.write(this.getBoardSize().height,"y");
        out.write(this.getTemp().width,"lowtemp");
        out.write(this.getTemp().height,"hitemp");
        out.write(this.getGravity(),"gravity");
        out.write(this.isVacuum(),"vacuum");
        out.write(this.getMinPlanetOwnerShip(),"minplanetownership");
        out.write(this.isHomeWorld(),"homeworld");
        out.write(this.getOriginalOwner(),"originalowner");

    }

    /**
     * @see common.persistence.MMNetSerializable#binIn(common.persistence.TreeReader)
     *
    public void binIn(TreeReader in, CampaignData dataProvider) throws IOException {
    	this.setId(new Integer(in.readInt("id")));
    	this.setName(in.readString("name"));
    	this.setPosition(new Position(in.readDouble("x"), in.readDouble("y")));
    	this.setUnitFactories(new Vector<UnitFactory>(1,1));
        in.readCollection(this.getUnitFactories(), UnitFactory.class, dataProvider, "unitFactories");
        this.setEnvironments(new PlanetEnvironments());
        in.readObject(this.getEnvironments(), dataProvider, "environments");
        this.setDescription(in.readString("description"));
        this.setBaysProvided(in.readInt("baysProvided"));
        this.setConquerable(in.readBoolean("conquerable"));
        this.setCompProduction(in.readInt("compProduction"));
        this.setInfluence(new Influences());
        in.readObject(this.getInfluence(), dataProvider, "influence");
        this.setMapSize(new Dimension(in.readInt("x"),in.readInt("y")));
        this.setBoardSize(new Dimension(in.readInt("x"),in.readInt("y")));
        this.setTemp(new Dimension(in.readInt("lowtemp"),in.readInt("hitemp")));
        this.setGravity(in.readDouble("gravity"));
        this.setVacuum(in.readBoolean("vacuum"));
        this.setMinPlanetOwnerShip(in.readInt("minplanetownership"));
        this.setHomeWorld(in.readBoolean("homeworld"));
        this.setOriginalOwner(in.readString("originalowner"));
        TreeMap<String, String> map = new TreeMap<String, String>();
        int size = in.readInt("PlanetFlags.size");
        for (int i = 0; i < size; ++i) {
            String key;
            String value;
            key = in.readString("PlanetFlags.key");
            value = in.readString("PlanetFlags.value");
            map.put(key, value);
        }
        this.setPlanetFlags(map);


    }
    */
    public StringBuilder getAdvanceDescription(int level) {
	            
		StringBuilder result = new StringBuilder("Information for Planet: <b>");
        result.append(getName()+"</b><br><br>");
        //    result.append("</b> ("+ getDescription() + ")<br><br>");
        result.append("<b>Location:</b> "+(int)getPosition().x+" x "+(int)getPosition().y+ " y<br>" + Math.round(getPosition().distanceSq(0.0, 0.0)) + " Lightyears from the galaxy center <br><br>");

		result.append("<b>Industry:</b><br>");
        // factories
        if (getCompProduction() > 0)
                result.append("Heavy industry allows an export of "
                        + getCompProduction() + " parts.<br>");
        if (getBaysProvided() > 0)
        	result.append("A warehouse on this world provides all players with " + getBaysProvided()
                    + " extra .<br><br>");
        if (getUnitFactories().size() > 0)
        {
            String founder = "";
            if ( getUnitFactories().size() == 1)
                result.append("<br><b>Factory:</b><br>");
            else
                result.append("<br><b>Factories:</b><br>");
            for (UnitFactory u : getUnitFactories()) {
            	founder = u.getFounder();
            	    result.append("<img src=\"file:///"+ new File(".").getAbsolutePath()+"/data/images/open"+founder+".gif\">"+u.getSize()+" "
                            +u.getFullTypeString() + u.getName() + " built by " + founder +"<br>");
            }
        }
    
        result.append("<br><b>Terrain:</b><br>");
        int maxProbab = getEnvironments().getTotalEnivronmentPropabilities();
        if (getEnvironments().size() < 1)
            result.append("nothing special");
        else {
            for (Continent pe : getEnvironments().toArray()) {
                int curProb = (pe.getSize()*100/maxProbab); 
                if (curProb < 10)
                	result.append("0");
                result.append(curProb+"% ");
                result.append(pe.getEnvironment().toImageAbsolutePathDescription());
                AdvancedTerrain aTerrain = this.getAdvancedTerrain().get(new Integer(pe.getEnvironment().getId()));
                if ( aTerrain == null ) {
                	result.append("Nothing special");
                }else {
	                result.append(" " + aTerrain.getDisplayName());
	                result.append("<br>Atmosphere: ");
	                if ( aTerrain.isVacuum())
	                    result.append("None<br>");
	                else
	                    result.append("Present<br>");
	                result.append("Gravity: "+ aTerrain.getGravity());
	                result.append("<br>Average Low: "+ aTerrain.getLowTemp());
	                result.append("<br>Average High: "+ aTerrain.getHighTemp());
	                if ( level >= 100 ){
	                    result.append("<br>Night Chance: "+aTerrain.getNightChance());
	                    result.append("<br>Night Temp Mod: "+aTerrain.getNightTempMod());
	                    if ( aTerrain.isStaticMap()){
	                        result.append("<br>Map Name: "+aTerrain.getStaticMapName());
	                        result.append("<br>Map Size(XxY): "+aTerrain.getXSize()+" x "+aTerrain.getYSize());
	                        result.append("<br>Board Size(XxY): "+aTerrain.getXBoardSize()+" x "+aTerrain.getYBoardSize());
	                    }
	                    result.append("<br>Min Visibility: "+aTerrain.getMinVisibility());
	                    result.append("<br>Max Visibility: "+aTerrain.getMaxVisibility());
	                }
                }
                result.append("<br>");
            }
        }
       
        // influence
        result.append("<br><br><b>Influence:</b><br>");
        for (House h  : getInfluence().getHouses()) {
            
            String color = "#999999";
            String name = "None";
            int id = -1;
            
            if ( h != null ) {
                color = h.getHouseColor();
                name = h.getName();
                id = h.getId();
            }
            
            result.append("<font color="+color+">"+name+ "</font> (" + getInfluence().getInfluence(id) + ")");
            result.append(", ");
        } // while*/
        result.replace(result.length()-2, result.length(),"<br>");
        if ( this.getPlanetFlags().size() > 0){
            result.append("<br><b>Points of Intereset:</b><br>");
            for( String value : this.getPlanetFlags().values() ){
                result.append(value+", ");
            }
            result.delete(result.length()-2, result.length());
            result.append("<br><br>");
        }//end if planet has flags
        return result;
	}

    public int getFactoryCount(){
    	
    	//int count = 0;
    	return getUnitFactories().size();
    	/*for (Iterator i = getUnitFactories().iterator(); i.hasNext();) {
    		count++;
    		i.next();
        }
    	return count;*/
    }
   
    public Dimension getMapSize() {return MapSize;}
    public void setMapSize(Dimension map){
        this.MapSize = map;
    }
    
    public Dimension getBoardSize() {return BoardSize;}
    public void setBoardSize(Dimension board){
    	this.BoardSize = board;
    }
    
    public Dimension getTemp() {return this.temperature;}
    public void setTemp (Dimension temp){
        this.temperature = temp;
    }
    
    public double getGravity(){return gravity;}
    public void setGravity(double grav){
        this.gravity = grav;
    }
    
    public boolean isVacuum() {return vacuum;}
    public void setVacuum(boolean vac){
        this.vacuum = vac;
    }
    
    public int getNightChance(){return nightChance;}
    public void setNightChance(int chance){
        nightChance = chance;
    }

    public int getNightTempMod(){return nightTempMod;}
    public void setNightTempMod(int mod){
       nightTempMod = mod;
    }

    public Hashtable<Integer,AdvancedTerrain> getAdvancedTerrain() {
    	return advanceTerrain;
    }
    
    public void setAdvancedTerrain(Hashtable<Integer,AdvancedTerrain> terrain){
        this.advanceTerrain = terrain;
    }

    public int getMinPlanetOwnerShip() {return minPlanetOwnerShip;}

    public void setMinPlanetOwnerShip(int ownership){
        this.minPlanetOwnerShip = ownership;
    }
    
    public void setHomeWorld(boolean homeworld){
        this.homeWorld = homeworld;
    }
    
    public boolean isHomeWorld(){
        return this.homeWorld;
    }
    
    public void setOriginalOwner(String owner){
        this.originalOwner = owner;
    }
    
    public String getOriginalOwner(){
        return originalOwner;
    }

    public TreeMap<String, String> getPlanetFlags(){
        return planetFlags;
    }
    
    public void setPlanetFlags(TreeMap<String, String> flags){
        this.planetFlags = flags;
    }
    
    public int getConquestPoints(){
    	return maxConquestPoints;
    }
    
    public void setConquestPoints(int points){
    	maxConquestPoints = Math.max(1,points );
    }
}
