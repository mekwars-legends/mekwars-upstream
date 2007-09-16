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

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import megamek.common.TechConstants;

import common.persistence.MMNetSerializable;
import common.persistence.TreeReader;
import common.persistence.TreeWriter;
import common.util.BinReader;
import common.util.BinWriter;
import common.util.HTML;

/**
 * @author Helge Richter
 *
 */
public class House implements MMNetSerializable {

	public static final int RED_VALUE = 0;
	public static final int GREEN_VALUE = 1;
	public static final int BLUE_VALUE = 2;
	
	private String name = "none";
	private String logo = "";
	private String factionFluFile = "Common";
	
	private Integer id;
	private int dbId = 0;
	private Vector<Integer>baseGunner = new Vector<Integer>(Unit.MAXBUILD,1);
	private Vector<Integer>basePilot = new Vector<Integer>(Unit.MAXBUILD,1);
	private Vector<String>basePilotSkills = new Vector<String>(Unit.MAXBUILD,1);
		
	//private int factionPlayerColors[] = new int[3]; // [red,green,blue]
	
	private int factionUnitPriceMod[][] = new int[5][4]; //[Type][Weight]
	private int factionUnitFluMod[][] = new int[5][4]; //[Type][Weight]
	private int factionUnitComponentMod[][] = new int[5][4]; //[Type][Weight]
	
	private String factionColor = "#000000";
	private String abbreviation = "";
	private String factionPlayerColors = "#000000";
	
	private boolean conquerable = true;
	     
	private Hashtable<String,String> BannedAmmo = new Hashtable<String,String>();
	private int techLevel = TechConstants.T_ALLOWED_ALL;
    private boolean allowDefectionsFrom = true;
    private boolean allowDefectionsTo = true;
	
    public float usedMekBayMultiplier;
    
	/**
	 * @return Returns the baseGunner.
	 */
	public int getBaseGunner() {
		return baseGunner.elementAt(0);
	}

    /**
     * @return baseGunner vector
     */
    public Vector<Integer> getBaseGunnerVect(){
        return baseGunner;
    }
    
    /**
     * @return Returns the baseGunner.
     */
    public int getBaseGunner(int type) {
        return baseGunner.elementAt(type);
    }

    /**
     * @return basePilotSkills vector
     */
    public Vector<String> getBasePilotSkillVect(){
        return basePilotSkills;
    }
    
    /**
     * @return Returns the basePilotSkill String.
     */
    public String getBasePilotSkill(int type) {
        return basePilotSkills.elementAt(type);
    }

    /**
     * @param baseGunner The baseGunner to set.
     */
    public void setBaseGunner(int baseGunner) {
    	this.baseGunner.set(0, baseGunner);
    }
    
    /**
     * @param basePilotSkill The base piloting skill for unit <code>type</code> to set.
     */
    public void setBasePilotSkill(String basePilotSkill, int type) {
        this.basePilotSkills.set(type, basePilotSkill);
    }

    /**
     * @param baseGunner The baseGunner to set.
     */
    public void setBaseGunner(int baseGunner, int type) {
        this.baseGunner.set(type, baseGunner);
    }

    /**
     * @return Returns the basePilot.
     */
    public int getBasePilot() {
    	return basePilot.elementAt(0);
    }

    /**
     * @return Returns the basePilot.
     */
    public int getBasePilot(int type) {
        return basePilot.elementAt(type);
    }

    /**
     * @return basePilot vector
     */
    public Vector<Integer>getBasePilotVect(){
        return basePilot;
    }
    
    /**
     * @param basePilot The basePilot to set.
     */
    public void setBasePilot(int basePilot) {
    	this.basePilot.set(0,basePilot);
    }
    
    /**
     * @param basePilot The basePilot to set.
     */
    public void setBasePilot(int basePilot, int type) {
        this.basePilot.set(type,basePilot);
    }

	/**
	 * @return Returns the myAbbreviation.
	 */
	public String getAbbreviation() {
		return abbreviation;
	}

	/**
	 * @param myAbbreviation The myAbbreviation to set.
	 */
	public void setAbbreviation(String myAbbreviation) {
		abbreviation = myAbbreviation;
	}

	/**
	 * @return Returns the conquerable.
	 */
	public boolean isConquerable() {
		return conquerable;
	}

	/**
	 * @param conquerable The conquerable to set.
	 */
	public void setConquerable(boolean conquerable) {
		this.conquerable = conquerable;
	}

	/**
	 * @return Returns the factionColor.
	 */
	public String getHouseColor() {
		return factionColor;
	}

	/**
	 * @param factionColor The factionColor to set.
	 */
	public void setHouseColor(String factionColor) {
		this.factionColor = factionColor;
	}

	/**
	 * @return Returns the logo.
	 */
	public String getLogo() {
		return logo;
	}

	/**
	 * @param logo The logo to set.
	 */
	public void setLogo(String logo) {
		this.logo = logo;
	}

	/**
	 * @return Returns the logo.
	 */
	public String getHouseFluFile() {
		return factionFluFile;
	}

	/**
	 * @param logo The logo to set.
	 */
	public void setHouseFluFile(String factionFlu) {
		this.factionFluFile = factionFlu;
	}
	
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
	
	public String getNameAsLink() {
		return "<a href=\"MEKWARS/c faction#" + name + "\">" + name + "</a>";
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
    /**
     * @return Returns the id.
     */
    public int getId() {
    	if (id == null)
    		return -1;
        return id.intValue();
    }
    
    public int getDBId() {
    	return dbId;
    }
    
    public void setDBId(int id) {
    	dbId = id;
    }

    /**
     * @param id
     */
    public House(int id) {
    	this.id = id;
        for( int pos = 0; pos < Unit.MAXBUILD; pos++ ){
            baseGunner.add(4);
            basePilot.add(5);
            basePilotSkills.add(" ");
        }
    }

    /**
     * Constructor used for serialization
     */
    public House() {
        for( int pos = 0; pos < Unit.MAXBUILD; pos++ ){
            baseGunner.add(4);
            basePilot.add(5);
            basePilotSkills.add(" ");
        }
    }

    /**
     * Write itself to an binary stream.
     */
    public void binOut(BinWriter out) throws IOException {
    	
        out.println(id.intValue(), "id");
        out.println(name, "name");
        out.println(logo, "logo");
        out.println(getBaseGunner(), "baseGunner");
        out.println(getBasePilot(), "basePilot");
        out.println(factionColor, "factionColor");
        
        out.println(factionPlayerColors,"factionPlayerColor");
        
        out.println(abbreviation, "abbreviation");
        out.println(conquerable, "conquerable");
        
        for ( int type = 0; type < 5; type++ )
            for ( int weight = 0; weight < 4; weight++)
                out.println(this.getHouseUnitComponentMod(type,weight),"componentMod"+type+weight);
        for ( int type = 0; type < 5; type++ )
            for ( int weight = 0; weight < 4; weight++)
                out.println(this.getHouseUnitPriceMod(type,weight),"priceMod"+type+weight);
        for ( int type = 0; type < 5; type++ )
            for ( int weight = 0; weight < 4; weight++)
                out.println(this.getHouseUnitFluMod(type,weight),"fluMod"+type+weight);
 
        out.println(this.getBannedAmmo().size(),"factionbannedammosize");
        Enumeration banned = this.getBannedAmmo().keys();
        while (banned.hasMoreElements()){
            String munition =(String)banned.nextElement();
            out.println(munition,"munition");
        }
        
        for( int pos = 0; pos < Unit.MAXBUILD; pos++ ){
        	out.println(basePilotSkills.elementAt(pos), "factionBasePilotSkill");
        }
        
        out.println(this.getTechLevel(), "techLevel");
        out.println(this.getHouseDefectionFrom(), "defectFrom");
        out.println(this.getHouseDefectionTo(), "defectTo");
        out.println(this.getUsedMekBayMultiplier(), "usedMekBayMultiplier");
    }

    /**
     * Read itself from a stream.
     */
    public House(BinReader in) throws IOException {
    	
        for( int pos = 0; pos < Unit.MAXBUILD; pos++ ){
            baseGunner.add(4);
            basePilot.add(5);
            basePilotSkills.add(" ");
        }

     	id = new Integer(in.readInt("id"));
    	name = HTML.br2cr(in.readLine("name"));
    	logo = HTML.br2cr(in.readLine("logo"));
    	setBaseGunner(in.readInt("baseGunner"));
    	setBasePilot(in.readInt("basePilot"));
    	factionColor = in.readLine("factionColor");
    	
    	factionPlayerColors = in.readLine("factionPlayerColor");
    	
    	abbreviation = in.readLine("abbreviation");
    	conquerable = in.readBoolean("conquerable");

        for ( int type = 0; type < 5; type++ )
            for ( int weight = 0; weight < 4; weight++)
                this.setHouseUnitComponentMod(type,weight,in.readInt("componentMod"+type+weight));
        for ( int type = 0; type < 5; type++ )
            for ( int weight = 0; weight < 4; weight++)
                this.setHouseUnitPriceMod(type,weight,in.readInt("priceMod"+type+weight));
        for ( int type = 0; type < 5; type++ )
            for ( int weight = 0; weight < 4; weight++)
                this.setHouseUnitFluMod(type,weight,in.readInt("fluMod"+type+weight));
    
        int size = in.readInt("factionbannedammosize");
        for( ; size > 0; size--)
            BannedAmmo.put(in.readLine("munition"),"Banned");
        
        for( int pos = 0; pos < Unit.MAXBUILD; pos++ ){
        	basePilotSkills.set(pos,in.readLine("factionBasePilotSkill"));
        }
        
        this.setTechLevel(in.readInt("techLevel"));
        this.setHouseDefectionFrom(in.readBoolean("defectFrom"));
        this.setHouseDefectionTo(in.readBoolean("defectTo"));
        this.setUsedMekBayMultiplier((float)in.readDouble("usedMekBayMultiplier"));
    }
	
	/**
	 * @TODO This is only a hack and should ONLY be used by experienced personnel!
	 * @param id The id to set.
	 */
	public void setId(int id) {
		this.id = id;
	}

    /**
     * @see common.persistence.MMNetSerializable#binOut(common.persistence.TreeWriter)
     */
    public void binOut(TreeWriter out) {
    	
    	out.write(id.intValue(), "id");
        out.write(name, "name");
        out.write(logo, "logo");
        out.write(baseGunner, "baseGunner");
        out.write(basePilot, "basePilot");
        out.write(factionColor, "factionColor");
        
        out.write(factionPlayerColors,"factionPlayerColor");
        
        out.write(abbreviation, "abbreviation");
        out.write(conquerable, "conquerable");

        for ( int type = 0; type < 5; type++ )
            for ( int weight = 0; weight < 4; weight++)
                out.write(this.getHouseUnitComponentMod(type,weight),"componentMod"+type+weight);
        for ( int type = 0; type < 5; type++ )
            for ( int weight = 0; weight < 4; weight++)
                out.write(this.getHouseUnitPriceMod(type,weight),"priceMod"+type+weight);
        for ( int type = 0; type < 5; type++ )
            for ( int weight = 0; weight < 4; weight++)
                out.write(this.getHouseUnitFluMod(type,weight),"fluMod"+type+weight);
        
        out.write(this.getBannedAmmo().size(),"factionbannedammosize");
        Enumeration banned = this.getBannedAmmo().keys();
        while (banned.hasMoreElements())
            out.write((String)banned.nextElement(),"munition");

        for( int pos = 0; pos < Unit.MAXBUILD; pos++ ){
        	out.write(basePilotSkills.elementAt(pos),"factionBasePilotSkill");
        }

    }

    /**
     * @see common.persistence.MMNetSerializable#binIn(common.persistence.TreeReader)
     */
    public void binIn(TreeReader in, CampaignData dataProvider)throws IOException {
    	
    	for( int pos = 0; pos < Unit.MAXBUILD; pos++ ){
            baseGunner.add(4);
            basePilot.add(5);
            basePilotSkills.add(" ");
        }
    	
    	id = new Integer(in.readInt("id"));
    	name = HTML.br2cr(in.readString("name"));
    	logo = HTML.br2cr(in.readString("logo"));
    	setBaseGunner(in.readInt("baseGunner"));
    	setBasePilot(in.readInt("basePilot"));
    	factionColor = in.readString("factionColor");
    	
    	factionPlayerColors = in.readString("factionPlayerColor");
    	
    	abbreviation = in.readString("abbreviation");
    	conquerable = in.readBoolean("conquerable");
    	
        for ( int type = 0; type < 5; type++ )
            for ( int weight = 0; weight < 4; weight++)
                this.setHouseUnitComponentMod(type,weight,in.readInt("componentMod"+type+weight));
        for ( int type = 0; type < 5; type++ )
            for ( int weight = 0; weight < 4; weight++)
                this.setHouseUnitPriceMod(type,weight,in.readInt("priceMod"+type+weight));
        for ( int type = 0; type < 5; type++ )
            for ( int weight = 0; weight < 4; weight++)
                this.setHouseUnitFluMod(type,weight,in.readInt("fluMod"+type+weight));
        
        int size = in.readInt("factionbannedammosize");
        for( ; size > 0; size--)
            BannedAmmo.put(in.readString("munition"),"Banned");

        for( int pos = 0; pos < Unit.MAXBUILD; pos++ ){
        	basePilotSkills.set(pos,in.readString("factionBasePilotSkill"));
        }
    }
    
	/**
     * @get the unit price mod for a faction
     */
	public int getHouseUnitPriceMod(int type, int weight) {
		return this.factionUnitPriceMod[type][weight];
	}
	
	/**
     * sets the unit price mod for a faction
     */
	public void setHouseUnitPriceMod(int type, int weight, int mod) {
		this.factionUnitPriceMod[type][weight] = mod;
	}
	
	/**
     * @get the unit price mod for a faction
     */
	public int getHouseUnitFluMod(int type, int weight) {
		return this.factionUnitFluMod[type][weight];
	}
	
	/**
     * sets the unit price mod for a faction
     */
	public void setHouseUnitFluMod(int type, int weight, int mod) {
		this.factionUnitFluMod[type][weight] = mod;
	}
	
	/**
     * gets the unit component mod for a faction
     */
	public int getHouseUnitComponentMod(int type, int weight) {
		return factionUnitComponentMod[type][weight];
	}
	
	/**
     * sets the unit component mod for a faction.
     */
	public void setHouseUnitComponentMod(int type, int weight, int mod) {
		this.factionUnitComponentMod[type][weight] = mod;
	}
	
	public void setHousePlayerColors(String factionPlayerColor ){
		if ( factionPlayerColor.startsWith("#") )
			this.factionPlayerColors = factionPlayerColor;
		else
			this.factionPlayerColors = "#"+factionPlayerColor;
	}
	
	public String getHousePlayerColor(){
		return this.factionPlayerColors;
	}

	//public void setBannedAmmo(Hashtable<String,String> ban){
	//    BannedAmmo = ban;
	//}
	
	public Hashtable<String,String> getBannedAmmo(){
	    return BannedAmmo;
	}
	
    public void setTechLevel(int level) {
    	if ( techLevel < TechConstants.T_IS_LEVEL_1 )
    		this.techLevel = TechConstants.T_ALL;
    	else
    		this.techLevel = level;
    }
    
    public int getTechLevel() {
    	return this.techLevel;
    }
    
	public boolean getHouseDefectionFrom() {
		return allowDefectionsFrom;
	}

	public void setHouseDefectionFrom(boolean defection) {
		allowDefectionsFrom = defection;
	}

	public boolean getHouseDefectionTo() {
		return allowDefectionsTo;
	}

	public void setHouseDefectionTo(boolean defection) {
		allowDefectionsTo = defection;
	}
	
	public void setUsedMekBayMultiplier(float mult) {
		this.usedMekBayMultiplier = mult;
	}
	
	public float getUsedMekBayMultiplier() {
		return this.usedMekBayMultiplier;
	}

}

