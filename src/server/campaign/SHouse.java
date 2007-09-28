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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;

import megamek.common.Entity;

import common.CampaignData;
import common.House;
import common.SubFaction;
import common.Unit;
import common.persistence.MMNetSerializable;
import common.persistence.TreeReader;
import common.persistence.TreeWriter;
import common.util.StringUtils;
import common.util.UnitUtils;

import server.MWServ;
import server.campaign.commands.Command;
import server.campaign.data.TimeUpdateHouse;
import server.campaign.market2.IBuyer;
import server.campaign.market2.ISeller;
import server.campaign.mercenaries.ContractInfo;
import server.campaign.mercenaries.MercHouse;
import server.campaign.operations.Operation;
import server.campaign.operations.ShortOperation;
import server.campaign.pilot.SPilot;
import sun.rmi.runtime.GetThreadPoolAction;

@SuppressWarnings( { "unchecked", "serial", "unused" })
public class SHouse extends TimeUpdateHouse implements MMNetSerializable, Comparable, ISeller, IBuyer {
   
    //store all online players in *THREE* hashes, one for each primary status
    private ConcurrentHashMap<String,SPlayer> reservePlayers = new ConcurrentHashMap<String,SPlayer>();
    private ConcurrentHashMap<String,SPlayer> activePlayers = new ConcurrentHashMap<String,SPlayer>();
    private ConcurrentHashMap<String,SPlayer> fightingPlayers = new ConcurrentHashMap<String,SPlayer>();
    
    private ConcurrentHashMap<String,SPlanet> Planets = new ConcurrentHashMap<String,SPlanet>();
    private ConcurrentHashMap<Integer,Vector<Vector<SUnit>>> Hangar = new ConcurrentHashMap<Integer,Vector<Vector<SUnit>>>();
    
    private Hashtable<String,SmallPlayer> SmallPlayers = new Hashtable<String,SmallPlayer>();
    private Hashtable<Integer,Vector<Integer>> Components = new Hashtable<Integer,Vector<Integer>>();
    private Hashtable unitComponents = new Hashtable();

    private int Money;
    private int BaysProvided = 0;
    private int ComponentProduction = 0;
    private int showProductionCountNext = 0;
    private int initialHouseRanking = 0;
    
    private String motd = "";
    
    private PilotQueues pilotQueues = new PilotQueues(this.getBaseGunnerVect(), this.getBasePilotVect(),this.getBasePilotSkillVect());
   
    private boolean inHouseAttacks = false;
	private Properties config = new Properties();
	
	private String forumName = "";
	
    private ConcurrentHashMap<String,SubFaction> subFactionList = new ConcurrentHashMap<String,SubFaction>();

    @Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("HS§");
		result.append(getName());
		result.append("|");
		result.append(getMoney());
		result.append("|");
		result.append(getHouseColor());
		result.append("|");
		result.append(this.getBaseGunner());
		result.append("|");
		result.append(getBasePilot());
		result.append("|");
		result.append(getAbbreviation());
		result.append("|");

		// Store the Meks
		for (int i = 0; i < 4; i++) {
			Vector<SUnit> tmpVec = getHangar(Unit.MEK).elementAt(i);

			tmpVec.trimToSize();
			result.append(tmpVec.size());
			result.append("|");
			
			for (SUnit currU : tmpVec) {
				result.append(currU.toString(false));
				result.append("|");
			}
		}
		
		// Store the Vehicles
		for (int i = 0; i < 4; i++) {
			Vector<SUnit> tmpVec = getHangar(Unit.VEHICLE).elementAt(i);
			
			tmpVec.trimToSize();
			result.append(tmpVec.size());
			result.append("|");
			
			for (SUnit currU : tmpVec) {
				result.append(currU.toString(false));
				result.append("|");
			}
		}
		
		//Store the Infantry
		if (Boolean.parseBoolean(this.getConfig("UseInfantry"))) {
			
			for (int i = 0; i < 4; i++) {
				Vector<SUnit> tmpVec = getHangar(Unit.INFANTRY).elementAt(i);
				
				tmpVec.trimToSize();
				result.append(tmpVec.size());
				result.append("|");
				
				for (SUnit currU : tmpVec) {
					result.append(currU.toString(false));
					result.append("|");
				}
			}
		}
		
		result.append(getLogo());
		result.append("|");

		/*
		 * USABLE NODE
		 * 
		 * used to store the number of members here in order to read in-line
		 * player saves. these saves have been repalced with seperate PFiles,
		 * and the node can now be used ...
		 */
		result.append(0);
		result.append("|");

		// Write the Components / BuildingPP's
		result.append("Components" + "|");
		Enumeration e = getComponents().keys();
		while (e.hasMoreElements()) {
			Integer id = (Integer) e.nextElement();
			Vector<Integer> v = getComponents().get(id);
			result.append(id.intValue() + "|" + v.size() + "|");
			for (int i = 0; i < v.size(); i++)
				result.append(v.elementAt(i).intValue() + "|");
		}
		result.append("EndComponents");
		result.append("|");
		
		result.append(this.getInitialHouseRanking());
		result.append("|");
		result.append(this.isConquerable());
		result.append("|");
		result.append(isInHouseAttacks());
		result.append("|");
		result.append(getId());
		result.append("|");
		result.append(this.getHousePlayerColor());
		result.append("|");
		result.append(this.getHouseDefectionFrom());
		result.append("|");// Mek pilots first
		result.append(this.getPilotQueues().getQueueSize(Unit.MEK));
		result.append("|");
		LinkedList<SPilot> PilotList = this.getPilotQueues().getPilotQueue(Unit.MEK);
		for (SPilot currP : PilotList) {
			result.append(currP.toFileFormat("#", false));
			result.append("|");
		}// veehs next
		result.append(this.getPilotQueues().getQueueSize(Unit.VEHICLE));
		result.append("|");
		PilotList = this.getPilotQueues().getPilotQueue(Unit.VEHICLE);
		for (SPilot currP : PilotList) {
			result.append(currP.toFileFormat("#", false));
			result.append("|");
		}// inf
		result.append(this.getPilotQueues().getQueueSize(Unit.INFANTRY));
		result.append("|");
		PilotList = this.getPilotQueues().getPilotQueue(Unit.INFANTRY);
		for (SPilot currP : PilotList) {
			result.append(currP.toFileFormat("#", false));
			result.append("|");
		}

		result.append(this.getHouseFluFile());
		result.append("|");

		// Store the BattleArmor (Units)
		for (int i = 0; i < 4; i++) {
			Vector<SUnit> tmpVec = getHangar(Unit.BATTLEARMOR).elementAt(i);

			tmpVec.trimToSize();
			result.append(tmpVec.size());
			result.append("|");
			
			for (SUnit currU : tmpVec) {
				result.append(currU.toString(false));
				result.append("|");
			}
		}

		// Store the ProtoMeks (Units)
		for (int i = 0; i < 4; i++) {
			Vector<SUnit> tmpVec = getHangar(Unit.PROTOMEK).elementAt(i);

			tmpVec.trimToSize();
			result.append(tmpVec.size());
			result.append("|");
			for (SUnit currU : tmpVec) {
				result.append(currU.toString(false));
				result.append("|");
			}
		}

		// Store BattleArmor (Pilots)
		result.append(this.getPilotQueues().getQueueSize(Unit.BATTLEARMOR));
		result.append("|");
		PilotList = this.getPilotQueues().getPilotQueue(Unit.BATTLEARMOR);
		for (SPilot currPilot : PilotList) {
			result.append(currPilot.toFileFormat("#", false));
			result.append("|");
		}

		// Store ProtoMeks (Pilots)
		result.append(this.getPilotQueues().getQueueSize(Unit.PROTOMEK));
		result.append("|");
		PilotList = this.getPilotQueues().getPilotQueue(Unit.PROTOMEK);
		for (SPilot currPilot : PilotList) {
			result.append(currPilot.toFileFormat("#", false));
			result.append("|");
		}

		// Save faction MOTD
		if (this.getMotd().equals(""))
			result.append(" ");
		else
			result.append(this.getMotd());
		
		result.append("|");
		result.append(this.getHouseDefectionTo());
		result.append("|");

        for ( int pos = 0; pos < Unit.MAXBUILD; pos++ ){
            result.append(getBaseGunner(pos));
            result.append("|");
            result.append(getBasePilot(pos));
            result.append("|");
        }

        for ( int pos = 0; pos < Unit.MAXBUILD; pos++ ){
        	String skill = getBasePilotSkill(pos);
        	if ( skill.length() < 1)
        		result.append(" ");
        	else
        		result.append(skill);
            result.append("|");
        }

        result.append(this.getTechLevel());
        result.append("|");
        
        result.append(subFactionList.size());
        result.append("|");
        
        for (String key : subFactionList.keySet() ){
        	result.append(subFactionList.get(key).toString());
        	result.append("|");
        }
        
    	return result.toString();
	}

	public Hashtable<Integer, Vector<Integer>> getComponents() {
		return Components;
	}

	public void toDB() {
		MWServ.mwlog.dbLog("Saving Faction " + getName());
		
		PreparedStatement ps = null;
		StringBuffer sql = new StringBuffer();
		ResultSet rs = null;
		try {
			if(getDBId()== 0) {
				// Not in the database - INSERT it
				sql.setLength(0);
				sql.append("INSERT into factions set ");
				sql.append("fName = ?, ");
				sql.append("fMoney = ?, ");
				sql.append("fColor = ?, ");
				sql.append("fAbbreviation = ?, ");
				sql.append("fLogo = ?, ");
				sql.append("fInitialHouseRanking = ?, ");
				sql.append("fConquerable = ?, ");
				sql.append("fPlayerColors = ?, ");
				sql.append("fInHouseAttacks = ?, ");
				sql.append("fAllowDefectionsFrom = ?, ");
				sql.append("fFluFile = ?, ");
				sql.append("fMOTD = ?, ");
				sql.append("fAllowDefectionsTo = ?, ");
				sql.append("fTechLevel = ?, ");
				sql.append("fBaseGunner = ?, ");
				sql.append("fBasePilot = ?, ");
				sql.append("fIsNewbieHouse = ?, ");
				sql.append("fIsMercHouse = ?");

				ps = CampaignMain.cm.MySQL.getPreparedStatement(sql.toString(), PreparedStatement.RETURN_GENERATED_KEYS);
				ps.setString(1, getName());
				ps.setInt(2, getMoney());
				ps.setString(3, getHouseColor());
				ps.setString(4, getAbbreviation());
				ps.setString(5, getLogo());
				ps.setInt(6, getInitialHouseRanking());
				ps.setString(7, Boolean.toString(isConquerable()));
				ps.setString(8, getHousePlayerColor());
				ps.setString(9, Boolean.toString(isInHouseAttacks()));
				ps.setString(10, Boolean.toString(getHouseDefectionFrom()));
				ps.setString(11, getHouseFluFile());
				ps.setString(12, getMotd());
				ps.setString(13, Boolean.toString(getHouseDefectionTo()));
				ps.setInt(14, getTechLevel());
				ps.setInt(15, getBaseGunner());
				ps.setInt(16, getBasePilot());
				ps.setString(17, Boolean.toString(isNewbieHouse()));
				ps.setString(18, Boolean.toString(isMercHouse()));
				ps.executeUpdate();
				rs = ps.getGeneratedKeys();
				rs.next();
				setDBId(rs.getInt(1));
			} else {
				// Already in the database - UPDATE it
				sql.setLength(0);
				sql.append("UPDATE factions set ");
				sql.append("fName = ?, ");
				sql.append("fMoney = ?, ");
				sql.append("fColor = ?, ");
				sql.append("fAbbreviation = ?, ");
				sql.append("fLogo = ?, ");
				sql.append("fInitialHouseRanking = ?, ");
				sql.append("fConquerable = ?, ");
				sql.append("fPlayerColors = ?, ");
				sql.append("fInHouseAttacks = ?, ");
				sql.append("fAllowDefectionsFrom = ?, ");
				sql.append("fFluFile = ?, ");
				sql.append("fMOTD = ?, ");
				sql.append("fAllowDefectionsTo = ?, ");
				sql.append("fTechLevel = ?, ");
				sql.append("fBaseGunner = ?, ");
				sql.append("fBasePilot = ? ");
				sql.append("WHERE ID = ?");
				ps = CampaignMain.cm.MySQL.getPreparedStatement(sql.toString());
				ps.setString(1, getName());
				ps.setInt(2, getMoney());
				ps.setString(3, getHouseColor());
				ps.setString(4, getAbbreviation());
				ps.setString(5, getLogo());
				ps.setInt(6, getInitialHouseRanking());
				ps.setString(7, Boolean.toString(isConquerable()));
				ps.setString(8, getHousePlayerColor());
				ps.setString(9, Boolean.toString(isInHouseAttacks()));
				ps.setString(10, Boolean.toString(getHouseDefectionFrom()));
				ps.setString(11, getHouseFluFile());
				ps.setString(12, getMotd());
				ps.setString(13, Boolean.toString(getHouseDefectionTo()));
				ps.setInt(14, getTechLevel());
				ps.setInt(15, getBaseGunner());
				ps.setInt(16, getBasePilot());
				ps.setInt(17, getDBId());
				ps.executeUpdate();				
			}
			

		// Now we do the vectors
			
			// Mechs
			for (int i = 0; i < 4; i++) {
				Vector<SUnit> tmpVec = getHangar(Unit.MEK).elementAt(i);
				tmpVec.trimToSize();
				for (SUnit currU : tmpVec) {
					currU.toDB();
				}
			}
			//Vehicles
			for (int i = 0; i < 4; i++) {
				Vector<SUnit> tmpVec = getHangar(Unit.VEHICLE).elementAt(i);
				tmpVec.trimToSize();
				for (SUnit currU : tmpVec) {
					currU.toDB();
				}
			}
			//Infantry
			if(Boolean.parseBoolean(getConfig("UseInfantry"))) {
				for (int i = 0; i < 4; i++) {
					Vector<SUnit> tmpVec = getHangar(Unit.INFANTRY).elementAt(i);
					tmpVec.trimToSize();
					for (SUnit currU : tmpVec) {
						currU.toDB();
					}
				}				
			}
			//BattleArmor
				for (int i = 0; i < 4; i++) {
					Vector<SUnit> tmpVec = getHangar(Unit.BATTLEARMOR).elementAt(i);
					tmpVec.trimToSize();
					for (SUnit currU : tmpVec) {
						currU.toDB();
					}
				}
			// Protomechs
				for (int i = 0; i < 4; i++) {
					Vector<SUnit> tmpVec = getHangar(Unit.PROTOMEK).elementAt(i);
					tmpVec.trimToSize();
					for (SUnit currU : tmpVec) {
						currU.toDB();
					}
				}
			// Pilot Queues
				// Mechs
				LinkedList<SPilot> PilotList = getPilotQueues().getPilotQueue(Unit.MEK);
				for (SPilot currP: PilotList) {
					currP.toDB(Unit.MEK, -1);
				}
				// Vehicles
				PilotList = getPilotQueues().getPilotQueue(Unit.VEHICLE);
				for (SPilot currP: PilotList) {
					currP.toDB(Unit.VEHICLE, -1);
				}
				
				// Infantry
				PilotList = getPilotQueues().getPilotQueue(Unit.INFANTRY);
				for (SPilot currP: PilotList) {
					currP.toDB(Unit.INFANTRY, -1);
				}
				
				// BattleArmor
				PilotList = getPilotQueues().getPilotQueue(Unit.BATTLEARMOR);
				for (SPilot currP: PilotList) {
					currP.toDB(Unit.BATTLEARMOR, -1);
				}
				
				// ProtoMechs
				PilotList = getPilotQueues().getPilotQueue(Unit.PROTOMEK);
				for (SPilot currP: PilotList) {
					currP.toDB(Unit.PROTOMEK, -1);
				}
				

				// Components
				ps.executeUpdate("DELETE from factionComponents WHERE factionID = " + getDBId());
				Enumeration en = getComponents().keys();
				while (en.hasMoreElements()) {
					Integer id = (Integer) en.nextElement();
					Vector<Integer> v = getComponents().get(id);
					for (int i = 0; i < v.size(); i ++){
						ps.executeUpdate("INSERT into factionComponents set factionID = " + getDBId() + ", unitType = " + id.intValue() + ", unitWeight = " + i + ", components = " + v.elementAt(i).intValue());
					}
				}

				// Pilot Skill
				// Change this so it doesn't save if it's blank.
				ps.executeUpdate("DELETE from faction_pilot_skills WHERE factionID = " + getDBId());
				for (int pos = 0; pos < Unit.MAXBUILD; pos ++ ) {
					String skill = getBasePilotSkill(pos);

 					ps = CampaignMain.cm.MySQL.getPreparedStatement("INSERT into faction_pilot_skills set factionID = ?, skillID = ?, pilotSkills = ?");
					ps.setInt(1, getDBId());
					ps.setString(3, skill);
					ps.setInt(2, pos);
					ps.executeUpdate();
			
				}
				// BaseGunner & Pilot
				ps.executeUpdate("DELETE from faction_base_gunnery_piloting where factionId = " + getDBId());
				for (int pos = 0; pos < Unit.MAXBUILD; pos++){
					ps.executeUpdate("INSERT into faction_base_gunnery_piloting set factionID = " + getDBId() + ", unitType = " + pos + ", baseGunnery = " + getBaseGunner(pos) + ", basePiloting = " + getBasePilot(pos));
				}
				if(rs != null)
					rs.close();
				ps.close();
				} catch (SQLException e) {
			MWServ.mwlog.dbLog("SQL Error in FactionHandler.saveFaction: " + e.getMessage());
		}	
	}
	
	/**
	 * Save itself to a TreeWriter
	 */
	@Override
	public void binOut(TreeWriter out) {
		super.binOut(out);
		out.write(getMoney(), "money");
		// Store the Meks
		for (int i = 0; i < 4; i++) {
			Vector<SUnit> tmpVec = this.getHangar(Unit.MEK).elementAt(i);
			out.write(tmpVec, "hangar" + i);
		}
		// Store the Vehicles
		for (int i = 0; i < 4; i++) {
			Vector<SUnit> tmpVec = this.getHangar(Unit.VEHICLE).elementAt(i);
			out.write(tmpVec, "vehicle" + i);
		}
		if (Boolean.parseBoolean(this.getConfig("UseInfantry"))) {
			// Store the Infantry
			for (int i = 0; i < 4; i++) {
				Vector<SUnit> tmpVec = this.getHangar(Unit.INFANTRY).elementAt(i);
				out.write(tmpVec, "infantry" + i);
			}
		}
		out.write(getAllOnlinePlayers().values(), "members");
		
		// Write the BuildingPP's
		out.startDataBlock("components");
		out.write(Components.size(), "size");
		Enumeration e = Components.keys();
		while (e.hasMoreElements()) {
			Integer id = (Integer) e.nextElement();
			Vector<Integer> v = Components.get(id);
			out.startDataBlock("component");
			out.write(id.intValue(), "id");
			StringBuilder ids = new StringBuilder();
			for (int i = 0; i < v.size(); i++)
				ids.append(v.elementAt(i) + ((i + 1 < v.size() ? "," : "")));
			out.write(ids.toString(), "ids");
			out.endDataBlock("component");
		}
		out.endDataBlock("components");
	}

	/**
	 * Save itself to a TreeWriter
	 */
	@Override
	public void binIn(TreeReader in, CampaignData data) throws IOException {
		super.binIn(in, data);
		setMoney(in.readInt("money"));
	}

	public String fromString(String s, Random r) {
		try {

			//strip leadin.
			s = s.substring(3);

			StringTokenizer ST = new StringTokenizer(s, "|");
			setName(ST.nextToken());

			// start the chat logging.
			MWServ.mwlog.createFactionLogger(this.getName());

			setMoney(((Integer.parseInt(ST.nextToken()))));
			setHouseColor(ST.nextToken());
			setBaseGunner((Integer.parseInt(ST.nextToken())));
			setBasePilot((Integer.parseInt(ST.nextToken())));

			setAbbreviation(ST.nextToken());

			getHangar().put(new Integer(Unit.MEK), new Vector(5, 1));
			getHangar().put(new Integer(Unit.VEHICLE), new Vector(5, 1));
			getHangar().put(new Integer(Unit.INFANTRY), new Vector(5, 1));
			getHangar().put(new Integer(Unit.PROTOMEK), new Vector(5, 1));
			getHangar().put(new Integer(Unit.BATTLEARMOR), new Vector(5, 1));
			// Init all of the hangars
			for (int i = 0; i < 4; i++) {

				getHangar(Unit.MEK).add(new Vector<SUnit>(1,1));
				getHangar(Unit.VEHICLE).add(new Vector<SUnit>(1,1));
				getHangar(Unit.INFANTRY).add(new Vector<SUnit>(1,1));
				getHangar(Unit.BATTLEARMOR).add(new Vector<SUnit>(1,1));
				getHangar(Unit.PROTOMEK).add(new Vector<SUnit>(1,1));
			}

            boolean newbieHouse = this.isNewbieHouse();
			
            // READ THE MEKS
			for (int i = 0; i < 4; i++) {
				// Vector v = new Vector();
				int numofmechs = (Integer.parseInt(ST.nextToken()));
				SUnit m = null;
				for (int j = 0; j < numofmechs; j++) {
					m = new SUnit();
					m.fromString(ST.nextToken());
					
					if ( newbieHouse ){
						int priceForUnit = this.getPriceForUnit(m.getWeightclass(), m.getType());
						int rareSalesTime = Integer.parseInt(this.getConfig("RareMinSaleTime"));
						CampaignMain.cm.getMarket().addListing("Faction_" + this.getName(), m,priceForUnit, rareSalesTime);
						m.setStatus(Unit.STATUS_FORSALE);
					}
					addUnit(m,false);
                   
				}
			}
			
			// READ THE VEHICLES
			for (int i = 0; i < 4; i++) {
				// Vector v = new Vector();
				int numofvehicles = (Integer.parseInt(ST.nextToken()));
				SUnit m;
				for (int j = 0; j < numofvehicles; j++) {
					m = new SUnit();
					m.fromString(ST.nextToken());
                    
					if ( newbieHouse ){
						int priceForUnit = this.getPriceForUnit(m.getWeightclass(), m.getType());
						int rareSalesTime = Integer.parseInt(this.getConfig("RareMinSaleTime"));
						CampaignMain.cm.getMarket().addListing("Faction_" + this.getName(), m,priceForUnit, rareSalesTime);
						m.setStatus(Unit.STATUS_FORSALE);
					}
					addUnit(m,false);
    
				}
			}
			
			// READ THE INFANTRY
			if (Boolean.parseBoolean(this.getConfig("UseInfantry"))) {
				for (int i = 0; i < 4; i++) {
					int numofinfantry = (Integer.parseInt(ST.nextToken()));
					SUnit m;
					for (int j = 0; j < numofinfantry; j++) {
						m = new SUnit();
						m.fromString(ST.nextToken());
						
						if ( newbieHouse ){
							int priceForUnit = this.getPriceForUnit(m.getWeightclass(), m.getType());
							int rareSalesTime = Integer.parseInt(this.getConfig("RareMinSaleTime"));
							CampaignMain.cm.getMarket().addListing("Faction_" + this.getName(), m,priceForUnit, rareSalesTime);
							m.setStatus(Unit.STATUS_FORSALE);
						}
						addUnit(m,false);
                        
					}// end for(num infantry)
				}// end for(4 weight classes)
			}// end if("Use Infantry")

			/*
			 * USABLE NODE
			 * 
			 * used to store the number of members here in order to read in-line
			 * player saves. these saves have been replaced with seperate
			 * PFiles, and the node can now be used ...
			 */
			ST.nextElement();// burn the membercount

			/*
			 * Another bad-old-code feature. "Components" will be the next token
			 * on any modern server. Loop remains in case someone tries to use
			 * old MMNET data with players saved in-line.
			 */
			String next = ST.nextToken();
			while (!next.equals("Components")) {
				next = ST.nextToken();
			}

			// init the componet array(vectors)
			getComponents().put(Unit.MEK, new Vector<Integer>(4, 1));
			getComponents().put(Unit.VEHICLE, new Vector<Integer>(4, 1));
			getComponents().put(Unit.INFANTRY, new Vector<Integer>(4, 1));
			getComponents().put(Unit.BATTLEARMOR, new Vector<Integer>(4, 1));
			getComponents().put(Unit.PROTOMEK, new Vector<Integer>(4, 1));

			for (int i = 0; i < 4; i++) {
				getComponents().get(Unit.MEK).add(0);
				getComponents().get(Unit.VEHICLE).add(0);
				getComponents().get(Unit.INFANTRY).add(0);
				getComponents().get(Unit.BATTLEARMOR).add(0);
				getComponents().get(Unit.PROTOMEK).add(0);
			}

			boolean finished = false;
			while (!finished) {
				next = ST.nextToken();
				if (!next.equals("EndComponents")) {
					Integer id = new Integer(next);
					int count = Integer.parseInt(ST.nextToken());
					for (int i = 0; i < count; i++) {
						Vector v = getComponents().get(new Integer(id));
						int val = new Integer(ST.nextToken());
						v.setElementAt(val, i);
					}
					// getComponents().put(id,v);
				} else
					finished = true;
			}
			if (ST.hasMoreElements())
				setInitialHouseRanking(Integer.parseInt(ST.nextToken()));
			if (ST.hasMoreElements())
				setConquerable(Boolean.parseBoolean(ST.nextToken()));
			if (ST.hasMoreElements())
				setInHouseAttacks(Boolean.parseBoolean(ST.nextToken()));
            //Used to read the house id here but if you have to recreate a house from
            //Pfiles this could cause issues. now we just set the ID to -1
            //and let the server pick an id. --Torren
            if (ST.hasMoreElements())
                ST.nextToken();
			setId(-1);
			if (ST.hasMoreElements()) {
				String housePlayerColor = ST.nextToken(); 
				try {
					int redColor = Integer.parseInt(housePlayerColor);
					int greenColor = Integer.parseInt(ST.nextToken());
					int blueColor = Integer.parseInt(ST.nextToken());
					
					this.setHousePlayerColors(Integer.toHexString(redColor)+Integer.toHexString(greenColor)+Integer.toHexString(blueColor));
				} catch (Exception ex) {
					this.setHousePlayerColors(housePlayerColor);
				}
			}

			if (ST.hasMoreElements()) {
				this.setHouseDefectionFrom(Boolean.parseBoolean(ST.nextToken()));
			}

			if (ST.hasMoreElements()) {// meks
				int pilotCount = Integer.parseInt(ST.nextToken());
				for (; pilotCount > 0; pilotCount--) {
					SPilot p = new SPilot();
					p.fromFileFormat(ST.nextToken(), "#");
					this.getPilotQueues().loadPilot(Unit.MEK, p);
				}
			}

			if (ST.hasMoreElements()) {// vees
				int pilotCount = Integer.parseInt(ST.nextToken());
				for (; pilotCount > 0; pilotCount--) {
					SPilot p = new SPilot();
					p.fromFileFormat(ST.nextToken(), "#");
					this.getPilotQueues().loadPilot(Unit.VEHICLE, p);
				}
			}

			if (ST.hasMoreElements()) {// inf
				int pilotCount = Integer.parseInt(ST.nextToken());
				for (; pilotCount > 0; pilotCount--) {
					SPilot p = new SPilot();
					p.fromFileFormat(ST.nextToken(), "#");
					this.getPilotQueues().loadPilot(Unit.INFANTRY, p);
				}
			}

			if (ST.hasMoreElements()) {
				this.setHouseFluFile(ST.nextToken());
			}

			//READ THE BattleArmor
			if (ST.hasMoreElements()) {

				for (int i = 0; i < 4; i++) {
					int numofmechs = Integer.parseInt(ST.nextToken());
					SUnit m = null;
					for (int j = 0; j < numofmechs; j++) {
						m = new SUnit();
						m.fromString(ST.nextToken());
                       
						if ( newbieHouse ){
							int priceForUnit = this.getPriceForUnit(m.getWeightclass(), m.getType());
							int rareSalesTime = Integer.parseInt(this.getConfig("RareMinSaleTime"));
							CampaignMain.cm.getMarket().addListing("Faction_" + this.getName(), m,priceForUnit, rareSalesTime);
							m.setStatus(Unit.STATUS_FORSALE);
						}
						addUnit(m,false);
                       
					}
				}
			}

			//READ THE Protos
			if (ST.hasMoreElements()) {
				for (int i = 0; i < 4; i++) {
					int numofmechs = Integer.parseInt(ST.nextToken());
					SUnit m = null;
					for (int j = 0; j < numofmechs; j++) {
						m = new SUnit();
						m.fromString(ST.nextToken());

						if ( newbieHouse ){
							int priceForUnit = this.getPriceForUnit(m.getWeightclass(), m.getType());
							int rareSalesTime = Integer.parseInt(this.getConfig("RareMinSaleTime"));
							CampaignMain.cm.getMarket().addListing("Faction_" + this.getName(), m,priceForUnit, rareSalesTime);
							m.setStatus(Unit.STATUS_FORSALE);
						}
						addUnit(m,false);

					}
				}
			}

			if (ST.hasMoreElements()) {// BattleArmor
				int pilotCount = Integer.parseInt(ST.nextToken());
				for (; pilotCount > 0; pilotCount--) {
					SPilot p = new SPilot();
					p.fromFileFormat(ST.nextToken(), "#");
					this.getPilotQueues().loadPilot(Unit.BATTLEARMOR, p);
				}
			}

			if (ST.hasMoreElements()) {// ProtoMeks
				int pilotCount = Integer.parseInt(ST.nextToken());
				for (; pilotCount > 0; pilotCount--) {
					SPilot p = new SPilot();
					p.fromFileFormat(ST.nextToken(), "#");
					this.getPilotQueues().loadPilot(Unit.PROTOMEK, p);
				}
			}

			if (ST.hasMoreElements())
				this.setMotd(ST.nextToken());
			if (ST.hasMoreElements())
				this.setHouseDefectionTo(Boolean.parseBoolean(ST.nextToken()));

            if ( ST.hasMoreTokens() ){
                try{
                    for ( int pos = 0; pos < Unit.MAXBUILD; pos++ ){
                        setBaseGunner(Integer.parseInt(ST.nextToken()),pos);
                        setBasePilot(Integer.parseInt(ST.nextToken()),pos);
                    }
                }catch(Exception ex){
                    setPilotQueues(new PilotQueues(this.getBaseGunnerVect(), this.getBasePilotVect(), this.getBasePilotSkillVect()));
                    getPilotQueues().setFactionString(this.getName());// set the faction name for the queue
                }
            }
            
            if ( ST.hasMoreTokens() ){
                try{
                    for ( int pos = 0; pos < Unit.MAXBUILD; pos++ ){
                    	String skill = ST.nextToken();
                   		setBasePilotSkill(skill, pos);
                    }
                }catch(Exception ex){
                    setPilotQueues(new PilotQueues(this.getBaseGunnerVect(), this.getBasePilotVect(), this.getBasePilotSkillVect()));
                    getPilotQueues().setFactionString(this.getName());// set the faction name for the queue
                }
            }
            
            if ( ST.hasMoreElements() )
            	this.setTechLevel(Integer.parseInt(ST.nextToken()));
            
            setPilotQueues(new PilotQueues(this.getBaseGunnerVect(), this.getBasePilotVect(), this.getBasePilotSkillVect()));
            getPilotQueues().setFactionString(this.getName());// set the faction name for the queue
            
            
            if ( ST.hasMoreTokens() ){
            	int amount = Integer.parseInt(ST.nextToken());
            	
            	for (;amount > 0; amount--){
            		SubFaction newSubFaction = new SubFaction();
            		newSubFaction.fromString(ST.nextToken());
            		subFactionList.put(newSubFaction.getConfig("Name"),newSubFaction);
            	}
            }
            
            //subFactionList.put("Test SubFaction", new SubFaction("Test Faction"));
            // Stuff for MercHouse.. Has to be here until someone tells me how
			// to move it :) - McWiz
			if (this.isMercHouse()) {
				MWServ.mwlog.mainLog("Merc House");
				int contractamount = 0;
				if (ST.hasMoreElements())
					contractamount = Integer.parseInt(ST.nextToken());
				Hashtable merctable = new Hashtable();
				for (int i = 0; i < contractamount; i++) {
					ContractInfo ci = new ContractInfo();
					ci.fromString(ST.nextToken());
					merctable.put(ci.getPlayerName(), ci);
				}
				((MercHouse) this).setOutstandingContracts(merctable);
			}
			// if (CampaignMain.cm.isDebugEnabled())
			MWServ.mwlog.mainLog("House loaded: " + getName());

			/*
			 * this.getPilotQueues().setBaseGunnery(this.getBaseGunner());
			 * this.getPilotQueues().setBasePiloting(this.getBasePilot());
			 */

			this.loadConfigFile();
			this.setUsedMekBayMultiplier(Float.parseFloat(getConfig("UsedPurchaseCostMulti")));
			return s;
		} catch (Exception ex) {
			MWServ.mwlog.errLog(ex);
			MWServ.mwlog.errLog("Error while loading faction: " + this.getName()+ " Going forward anyway ...");
			return s;
		}
	}

	public SHouse(int id) {
		super(id);
	}

	/**
	 * Constructor used for serialization
	 */
	public SHouse() {
		reservePlayers = new ConcurrentHashMap<String,SPlayer>();
		activePlayers = new ConcurrentHashMap<String,SPlayer>();
		fightingPlayers = new ConcurrentHashMap<String,SPlayer>();
		SmallPlayers = new Hashtable<String,SmallPlayer>();
        for( int pos = 0; pos < Unit.MAXBUILD; pos++ ){
            setBaseGunner(4,pos);
            setBasePilot(5,pos);
        }

	}
	
	/*
	 * Players are stores in 3 seperate hashtables. Each
	 * hash is indicative of a different activity level. As
	 * players move back and forth between these levels, they
	 * are transferred from hash to hash. At NO TIME should
	 * a player exist in multiple hashes.
	 * 
	 * This 3-hash system replaces the old fighting/logged in
	 * 2 hash system and the SPlayer's activity boolean.
	 * 
	 * TODO: massively improve commenting here.
	 * @urgru 1.14.06
	 */
	public ConcurrentHashMap<String,SPlayer> getReservePlayers() {
		return reservePlayers;
	}

	public ConcurrentHashMap<String,SPlayer> getActivePlayers() {
		return activePlayers;
	}

	public ConcurrentHashMap<String,SPlayer> getFightingPlayers() {
		return fightingPlayers;
	}

	public int getBaysProvided() {
		return BaysProvided;
	}

	public int getComponentProduction() {
		return ComponentProduction;
	}

	public void setPilotQueues(PilotQueues q) {
		pilotQueues = q;
	}

	public SHouse(int id, String name, String HouseColor, int BaseGunner, int BasePilot, String abbreviation) {
		super(id);
		setAbbreviation(abbreviation);
		setHouseColor(HouseColor);
		setName(name);
		
		MWServ.mwlog.createFactionLogger(this.getName());
		// Vehicles = new Vector();

		for (int j = 0; j < 5; j++) // Type
		{
			Vector v = new Vector();
			for (int i = 0; i < 4; i++) // Weight
			{
				v.add(new Integer(0));
			}
			v.trimToSize();
			getComponents().put(new Integer(j), v);
		}
		// currentPP = new Vector();
		setMoney(0);
		getHangar().put(new Integer(Unit.MEK), new Vector(1,1));
		getHangar().put(new Integer(Unit.VEHICLE), new Vector(1,1));
		getHangar().put(new Integer(Unit.INFANTRY), new Vector(1,1));
		getHangar().put(new Integer(Unit.PROTOMEK), new Vector(1,1));
		getHangar().put(new Integer(Unit.BATTLEARMOR), new Vector(1,1));
		for (int i = 0; i < 4; i++) {
			Vector v = new Vector(1,1);
			getHangar(Unit.MEK).add(v);
		}
		for (int i = 0; i < 4; i++) {
			Vector v = new Vector(1,1);
			getHangar(Unit.VEHICLE).add(v);
		}
		if (Boolean.parseBoolean(this.getConfig("UseInfantry"))) {
			for (int i = 0; i < 4; i++) {
				Vector v = new Vector(1,1);
				getHangar(Unit.INFANTRY).add(v);
			}
		}
		for (int i = 0; i < 4; i++) {
			Vector v = new Vector(1,1);
			getHangar(Unit.PROTOMEK).add(v);
		}
		for (int i = 0; i < 4; i++) {
			Vector v = new Vector(1,1);
			getHangar(Unit.BATTLEARMOR).add(v);
		}

		// init the componet array(vectors)
		getComponents().put(Unit.MEK, new Vector<Integer>(4, 1));
		getComponents().put(Unit.VEHICLE, new Vector<Integer>(4, 1));
		getComponents().put(Unit.INFANTRY, new Vector<Integer>(4, 1));
		getComponents().put(Unit.BATTLEARMOR, new Vector<Integer>(4, 1));
		getComponents().put(Unit.PROTOMEK, new Vector<Integer>(4, 1));

		for (int i = 0; i < 4; i++) {
			getComponents().get(Unit.MEK).add(0);
			getComponents().get(Unit.VEHICLE).add(0);
			getComponents().get(Unit.INFANTRY).add(0);
			getComponents().get(Unit.BATTLEARMOR).add(0);
			getComponents().get(Unit.PROTOMEK).add(0);
		}

	}

	public ConcurrentHashMap<Integer, Vector<Vector<SUnit>>> getHangar() {
		return Hangar;
	}

	public Vector<Vector<SUnit>> getHangar(int Type_id) {
		if (Hangar == null || Hangar.size() < Type_id)
			return null;
		return Hangar.get(Type_id);
	}

	public boolean isNewbieHouse() {
		return false;
	}

	public boolean isMercHouse() {
		return false;
	}

	public SHouse getHouseFightingFor(SPlayer player) {
		return this;
	}

	@Override
	public boolean equals(Object o) {
		SHouse h = null;
		
		try {
			h = (SHouse)o;
		} catch (ClassCastException e) {
			return false;
		}
			
		if (h == null)
			return false;
		
		if (h.getName().equals(getName()))
			return true;
		
		return false;
	}

	public void addDispossessedPilot(SUnit u, boolean skipSkillChange) {
		
		if (u.hasVacantPilot())
			return;
		
		if (skipSkillChange)//special capture path
			getPilotQueues().addPilot(u.getType(), (SPilot) u.getPilot(), true);
		else//normal de-levalling addition	
			getPilotQueues().addPilot(u.getType(), (SPilot) u.getPilot());
		if(CampaignMain.cm.isUsingMySQL())
			CampaignMain.cm.MySQL.linkPilotToFaction(((SPilot)u.getPilot()).getDBId(), this.getDBId());
	}

	public PilotQueues getPilotQueues() {
		return pilotQueues;
	}

	public SPilot getNewPilot(int uType) {
		return getPilotQueues().getPilot(uType);
	}

	/**
	 * Method which checks all three activity states
	 * to see if a player w/ a given name is logged in
	 * to the faction
	 */
	public boolean isLoggedIntoFaction(String playerName) {
		
		String lowerName = playerName.toLowerCase();
		if (getReservePlayers().containsKey(lowerName))
			return true;
		else if (getActivePlayers().containsKey(lowerName))
			return true;
		else if (getFightingPlayers().containsKey(lowerName))
			return true;
		
		//not in the faction under any status.
		return false;
	}

	public int remainingHangarSpaceForWeightclass(int Weightclass, int TypeID) {

        //don't want to count units that are for sale.
        int trueHangarSize = this.getNumberOfNonSaleUnits(getHangar(TypeID).elementAt(Weightclass));
        
		if (Weightclass == Unit.LIGHT) {
			if (TypeID != Unit.MEK)
				return (Integer.parseInt(this.getConfig("MaxLightUnits"))/2) - trueHangarSize;

			// else
			return Integer.parseInt(this.getConfig("MaxLightUnits")) - trueHangarSize;
		}

		// else (nonlight weighclass)
		if (TypeID != Unit.MEK)
			return (Integer.parseInt(this.getConfig("MaxOtherUnits")) / 2) - trueHangarSize;

		return Integer.parseInt(this.getConfig("MaxOtherUnits")) - trueHangarSize;
	}

	/**
	 * Returns the number of players who count for mintick production.
	 * Called from SHouse.tick(). Factored out to keep the tick more
	 * or less redable.
	 * 
	 * An active player counts if he has at least one army that is
	 * between min and max BVs to count, and has been active for a
	 * whole tick.
	 * 
	 * Fighting players may or may not count, depending on the weight
	 * assigned by the admins to the game-type they are playing.
	 * 
	 * 
	 * @return double Amount of Production produced by players
	 */
	private double getNumberOfPlayersWhoCountForProduction() {
		
		double result = 0;
		boolean showOutput = Boolean.parseBoolean(this.getConfig("ShowOutputMultiplierOnTick"));
		MWServ.mwlog.debugLog("Staring getNumberOfPlayersWhoCountForProduction");
		//loop through all of the active players
		for (SPlayer currP : this.getActivePlayers().values()) {
			
			MWServ.mwlog.debugLog("Counting "+currP.getName());
            if ( System.currentTimeMillis() < currP.getActiveSince()+Long.parseLong(this.getConfig("InfluenceTimeMin")) )
                continue;
            MWServ.mwlog.debugLog("Getting army weight");
			//move on to the next player if value is 0.
			double value = currP.getWeightedArmyNumber();
			if (value <= 0)
				continue;
			
			//add the players weight to the total faction multiplier
			result += value;
			
			MWServ.mwlog.debugLog("Showing output");
			//if enabled, show the player his personal worth
			if (showOutput) {
				String toShow = "You counted towards production this tick";
				DecimalFormat myFormatter = new DecimalFormat("###.##");
				String output = myFormatter.format(value);
				toShow += " (" + output + " points worth)";
				CampaignMain.cm.toUser(toShow + ".", currP.getName(), true);
			}
			
		}//end for(active players)
		
		MWServ.mwlog.debugLog("Getting all fighting players");
		//now loop through all of the fighting players
		for (SPlayer currP : this.getFightingPlayers().values()) {
			
			/*
			 * Get the player's short op. He's fighing, so there should
			 * always be one, but check for a null just in case.
			 */
			MWServ.mwlog.debugLog("checking short operation for "+currP.getName());
			ShortOperation so = CampaignMain.cm.getOpsManager().getShortOpForPlayer(currP);
			if (so == null)
				continue;

			MWServ.mwlog.debugLog("Getting data for op "+so.getName()+" for player "+currP.getName());
			Operation o = CampaignMain.cm.getOpsManager().getOperation(so.getName());
			double value = o.getDoubleValue("CountGameForProduction");
			if (value < 0)
				value = 0;
			
			MWServ.mwlog.debugLog("adding value.");
			//add the players weight to the total faction multiplier
			result += value;
			
			MWServ.mwlog.debugLog("Showing output");
			//if enabled, show the player his personal worth
			if (value > 0 && showOutput) {
				String toReturn = "You counted towards production this tick";
				DecimalFormat myFormatter = new DecimalFormat("###.##");
				String output = myFormatter.format(value);
				toReturn += " (" + output + " points worth)";
				CampaignMain.cm.toUser(toReturn + ".", currP.getName(), true);
			}
		}//end for(fighting players)

		MWServ.mwlog.debugLog("returning with results.");
		//pass back the aggregate value.
		return result;
	}

	/**
	 * have the faction perform tick duties (gather income, referesh factories)
	 * and clean out its hangars and PP excesses (either via scrapping,
	 * industrial accidents, or BM sales), then report the tick results to all
	 * of its faction members.
	 */
	public String tick(boolean real, int tickid) {
		
		MWServ.mwlog.debugLog("Inside SHouse.Tick for: "+this.getName());
		String result = "<font color=\"black\">-------> <b>Tick! [" + tickid + "]</b><br>";
		StringBuilder hsUpdates = new StringBuilder();
		
		double tickworth = 0;

		MWServ.mwlog.debugLog("Getting number of players who count for production");

		//non-real ticks occur the first time a server starts, when free minticks are given away
		if (!real)
			tickworth = 10;// give 10 players worth ...
		else// if real, get the weighted number of valid players
			tickworth = this.getNumberOfPlayersWhoCountForProduction();

		MWServ.mwlog.debugLog("Calculating refresh points");
		double cComp = getComponentProduction();
		int componentsToAdd = (int) (tickworth * cComp);
		int refreshToAdd = (int) Math.round(tickworth);

		if ( Integer.parseInt(this.getConfig("FactoryRefreshPoints")) > -1)
			//Allow Servers to refresh factories without having active players.
			refreshToAdd = Integer.parseInt(this.getConfig("FactoryRefreshPoints"));

		MWServ.mwlog.debugLog("Geting planet income and refreshing factories");
		// Get income, and refresh factories
		Iterator e = getPlanets().values().iterator();
		while (e.hasNext()) {// loop through all planets which the faction
			// has territory on
			SPlanet p = (SPlanet) e.next();
			if (this.equals(p.getOwner())){
				MWServ.mwlog.debugLog("Updating planet "+p.getName());
				hsUpdates.append(p.tick(refreshToAdd));// call the planetary tick
			}
		}

		// then add to the faction PP pools
		boolean useMekPP = Boolean.parseBoolean(this.getConfig("UseMek"));
		boolean useVehiclePP = Boolean.parseBoolean(this.getConfig("UseVehicle"));
		boolean useInfantryPP = Boolean.parseBoolean(this.getConfig("UseInfantry"));
		boolean useProtoMekPP = Boolean.parseBoolean(this.getConfig("UseProtoMek"));
		boolean useBattleArmorPP = Boolean.parseBoolean(this.getConfig("UseBattleArmor"));

		for (int i = 0; i < 4; i++) {// loop through each weight class,
			// adding PP
			if (useMekPP) {
				MWServ.mwlog.debugLog("Updating House Mek Parts: "+i);
				hsUpdates.append(this.addPP(i, Unit.MEK, componentsToAdd, false));
				addComponentsProduced(Unit.MEK, componentsToAdd);
			} 
			
			if (useVehiclePP) {
				MWServ.mwlog.debugLog("Updating House Vehicle Parts: "+i);
				hsUpdates.append(this.addPP(i, Unit.VEHICLE, componentsToAdd, false));
				addComponentsProduced(Unit.VEHICLE, componentsToAdd);
			}

			if (useInfantryPP) {
				MWServ.mwlog.debugLog("Updating House Infantry: "+i);
				if (!Boolean.parseBoolean(this.getConfig("UseOnlyLightInfantry")) || i == Unit.LIGHT)
					hsUpdates.append(this.addPP(i, Unit.INFANTRY, componentsToAdd, false));
				addComponentsProduced(Unit.INFANTRY, componentsToAdd);
			}

			if (useProtoMekPP) {
				MWServ.mwlog.debugLog("Updating House ProtoMek: "+i);
				hsUpdates.append(this.addPP(i, Unit.PROTOMEK, componentsToAdd, false));
				addComponentsProduced(Unit.PROTOMEK, componentsToAdd);
			} 

			if (useBattleArmorPP) {
				MWServ.mwlog.debugLog("Updating House BA: "+i);
				hsUpdates.append(this.addPP(i, Unit.BATTLEARMOR, componentsToAdd, false));
				addComponentsProduced(Unit.BATTLEARMOR, componentsToAdd);
			}
		}

		/*
		 * Loop throuhgh all hangars and component vectors, looking for overages. Remove
		 * units (destroy or sell) and components (destroy or build units) until under caps.
		 * 
		 * This block of code was formerly SHouse.cleanUpHangarAndPP. Moved inline with
		 * the rest of tick() in order to facilities house status updates. @urgru 6.10.06
		 */
		
		//strings to build on, so info can be sorted in event/type/weight order	
		StringBuilder mechsProduced = new StringBuilder();
		StringBuilder industrialAccidents = new StringBuilder();
		StringBuilder scrapExcuses = new StringBuilder();
		StringBuilder marketAdditions = new StringBuilder();
		
		MWServ.mwlog.debugLog("Checking for Unit Overflow");
		/*
		 * Loop though every type and weight class, looking for overflow. If
		 * there are more units than allowed in the hangar, dispose of random
		 * units by scrapping or selling (on Market) until back at cap.
		 */
		for (int type_id = 0; type_id < Unit.TOTALTYPES; type_id++) {
			for (int i = Unit.LIGHT; i <= Unit.ASSAULT; i++) {
				
				//keep scrapping/selling until we're at cap.
				while (remainingHangarSpaceForWeightclass(i, type_id) < 0) {
					
					//get vector of units of the right weight, then select a
					//random unit from the stack.
					Vector<SUnit> v = this.getHangar(type_id).elementAt(i);
					SUnit randUnit = v.elementAt(CampaignMain.cm.getR().nextInt(v.size()));
					
					if (randUnit.getStatus() == Unit.STATUS_FORSALE)
						continue;
					
					int bmPercent = Integer.parseInt(this.getConfig("ChanceToSendUnitToBM"));
					if (this.maySellOnBM() && CampaignMain.cm.getR().nextInt(101) < bmPercent && SUnit.mayBeSoldOnMarket(randUnit)) {
						
						//Use standard factory pricing for the unit, and configured ticks.
						int minPrice = this.getPriceForUnit(i, type_id);
						String saleTicksString = Unit.getWeightClassDesc(randUnit.getWeightclass())+ "SaleTicks";
                        //add 1 to the sale tick due to a quirk with the BM autoupdate.
                        //The the unit is sent to the player before the new tick counter so the clients
                        //are a tick ahead of the server.
						int saleTicks = Integer.parseInt(this.getConfig(saleTicksString))+1;
						
						// Add the unit to the market, and tell the faction
						CampaignMain.cm.getMarket().addListing(this.getName(),randUnit, minPrice, saleTicks);
						marketAdditions.append(StringUtils.aOrAn(randUnit.getModelName(), false) + " was added to the black market.<br>");
						hsUpdates.append(this.getHSUnitRemovalString(randUnit));//"remove" unit from client's perspective
						randUnit.setStatus(Unit.STATUS_FORSALE);
					} else {
						String currScrapExcuse = getExcuseForUnitFailure(randUnit);
						scrapExcuses.append(currScrapExcuse + "<br>");
						hsUpdates.append(this.removeUnit(randUnit, false));
					}
				}// end while(too many units)
				
			}// end weight class loop
		}// end unit type loop
		
		MWServ.mwlog.debugLog("Doing Component Overflow");
		/*
		 * Loop through all types/weightclasses as above, but look for component
		 * overflow instead of hangar overage. Here we either scrap the
		 * components (aka "industrial accident") or autoproduce a brand new
		 * unit and drop it in the house hangar.
		 * 
		 * We look for component overflow after hangar overflow in order to be
		 * sure that newly autoproduced units aren't immediately dumped onto the
		 * market or nuked.
		 */
		for (int type_id = 0; type_id < Unit.TOTALTYPES; type_id++) {
			for (int weight = 0; weight < 4; weight++) {
				
				while (this.getPP(weight, type_id) > this.getMaxAllowedPP(weight, type_id)) {
					
					int randomLossFactor = CampaignMain.cm.getR().nextInt(this.getPPCost(weight, type_id)) + 1;
					
					//see if we should have an accident
					boolean accident = false;
					SUnitFactory m = this.getNativeFactoryForProduction(type_id,weight);
					if (CampaignMain.cm.getR().nextInt(100) + 1 <= Integer.parseInt(this.getConfig("AutoProductionFailureRate")))
						accident = true;
			
					//no factory to produce, or random accident
					if (m == null || accident) {
						hsUpdates.append(this.addPP(weight, type_id, -randomLossFactor, false));
						if (type_id == Unit.INFANTRY) {
							//silently reduce the component numbers. think of a spiffy message later @urgru 9.17.04
						} else
							industrialAccidents.append("An industrial accident destroys a substantial cache of "
								+ Unit.getWeightClassDesc(weight)
								+ " " + Unit.getTypeClassDesc(type_id)
								+ " components.<br>");
					} 
					
					//else, make a new unit
					else {
						SUnit newUnit = m.getMechProduced(type_id, this.getNewPilot(type_id));
						MWServ.mwlog.debugLog("AP Unit "+newUnit.getModelName());
						hsUpdates.append(this.addUnit(newUnit, false));
						hsUpdates.append(this.addPP(weight, type_id, -(this.getPPCost(weight,type_id)), false));
						
						/*
						 * set refresh and add to back end of the HS update. if the refresh is added in-line in the
						 * SUnitFactory, the command is sent BEFORE the final HS command, which then overwrites the
						 * correct refresh time w/ an incorrect reflesh time that reflects player activity.
						 */
						 if (!Boolean.parseBoolean(this.getConfig("UseCalculatedCosts"))){
					            //set the refresh miniticks
					    		if (m.getWeightclass() == Unit.LIGHT)
					    			hsUpdates.append(m.addRefresh((Integer.parseInt(this.getConfig("LightRefresh")) * 100) / m.getRefreshSpeed(), false));
					    		else if (m.getWeightclass() == Unit.MEDIUM)
					    			hsUpdates.append(m.addRefresh((Integer.parseInt(this.getConfig("MediumRefresh")) * 100) / m.getRefreshSpeed(), false));
					    		else if (m.getWeightclass() == Unit.HEAVY)
					    			hsUpdates.append(m.addRefresh((Integer.parseInt(this.getConfig("HeavyRefresh")) * 100) / m.getRefreshSpeed(), false));
					    		else if (m.getWeightclass() == Unit.ASSAULT)
					    			hsUpdates.append(m.addRefresh((Integer.parseInt(this.getConfig("AssaultRefresh")) * 100) / m.getRefreshSpeed(), false));
					        }
						
						if (type_id == Unit.INFANTRY)// infantry exclusive message
							mechsProduced.append("A militia unit [" + newUnit.getModelName()
							+ "] from "	+ m.getPlanet().getName()
							+ " activated for front line duty!<br>");
						
						else // non infantry, so use a standard build message
							mechsProduced.append("Technicians assembled a "
								+ newUnit.getModelName() + " at " + m.getName() + " on "
								+ m.getPlanet().getName() + ".<br>");
					}
					
				}// end while(PP > MaxPP)
			}// end for(all 4 weight classes)
		}// end for(all 3 types)
		
		// now, assemble the strings
		result += mechsProduced.toString() + marketAdditions.toString() + industrialAccidents.toString() + scrapExcuses.toString();

		MWServ.mwlog.debugLog("show Production Count");
		if ((getShowProductionCountNext() - 1) <= 0) {
			setShowProductionCountNext((Integer.parseInt(this.getConfig("ShowComponentGainEvery"))));
			
			//report how many mechs of each weight class the faction can produce.
			int MekComponents = getComponentsProduced(Unit.MEK);
			int VehComponents = getComponentsProduced(Unit.VEHICLE);
			int InfComponents = getComponentsProduced(Unit.INFANTRY);
			int ProtoComponents = getComponentsProduced(Unit.PROTOMEK);
			int BAComponents = getComponentsProduced(Unit.BATTLEARMOR);

			DecimalFormat myFormatter = new DecimalFormat("###.##");

			result += "<br><i><b>Your factories produced enough components to make:</b></i><br>";
			if (Boolean.parseBoolean(this.getConfig("UseMek"))) {
				result += myFormatter.format(MekComponents/(Double.parseDouble(this.getConfig("LightPP")))) + " Light meks<br>";
				result += myFormatter.format(MekComponents/(Double.parseDouble(this.getConfig("MediumPP")))) + " Medium meks<br>";
				result += myFormatter.format(MekComponents/(Double.parseDouble(this.getConfig("HeavyPP")))) + " Heavy meks<br>";
				result += myFormatter.format(MekComponents/(Double.parseDouble(this.getConfig("AssaultPP")))) + " Assault meks<br>";
			}
			if (Boolean.parseBoolean(this.getConfig("UseVehicle"))) {
				result += myFormatter.format(VehComponents/(Double.parseDouble(this.getConfig("LightVehiclePP")))) + " Light vehicles<br>";
				result += myFormatter.format(VehComponents/(Double.parseDouble(this.getConfig("MediumVehiclePP")))) + " Medium vehicles<br>";
				result += myFormatter.format(VehComponents/(Double.parseDouble(this.getConfig("HeavyVehiclePP")))) + " Heavy vehicles<br>";
				result += myFormatter.format(VehComponents/(Double.parseDouble(this.getConfig("AssaultVehiclePP")))) + " Assault vehicles<br>";
			}
			if (Boolean.parseBoolean(this.getConfig("UseInfantry"))) {

				// show only light, and no weightclass if UseOnlyLight
				if (Boolean.parseBoolean(this.getConfig("UseOnlyLightInfantry")))
					result += myFormatter.format(InfComponents/(Double.parseDouble(this.getConfig("LightInfantryPP")))) + " Infantry<br>";

				// otherwise, show it same as everything else
				else {
					result += myFormatter.format(InfComponents/(Double.parseDouble(this.getConfig("LightInfantryPP")))) + " Light infantry<br>";
					result += myFormatter.format(InfComponents/(Double.parseDouble(this.getConfig("MediumInfantryPP")))) + " Medium infantry<br>";
					result += myFormatter.format(InfComponents/(Double.parseDouble(this.getConfig("HeavyInfantryPP")))) + " Heavy infantry<br>";
					result += myFormatter.format(InfComponents/(Double.parseDouble(this.getConfig("AssaultInfantryPP")))) + " Assault infantry<br>";
				}
			}// end if(UseInfantry)
			if (Boolean.parseBoolean(this.getConfig("UseProtoMek"))) {
				result += myFormatter.format(ProtoComponents/(Double.parseDouble(this.getConfig("LightProtoMekPP")))) + " Light protomechs<br>";
				result += myFormatter.format(ProtoComponents/(Double.parseDouble(this.getConfig("MediumProtoMekPP")))) + " Medium protomechs<br>";
				result += myFormatter.format(ProtoComponents/(Double.parseDouble(this.getConfig("HeavyProtoMekPP")))) + " Heavy protomechs<br>";
				result += myFormatter.format(ProtoComponents/(Double.parseDouble(this.getConfig("AssaultProtoMekPP")))) + " Assault protomechs<br>";
			}
			if (Boolean.parseBoolean(this.getConfig("UseBattleArmor"))) {
				result += myFormatter.format(BAComponents/(Double.parseDouble(this.getConfig("LightBattleArmorPP")))) + " Light battle armor<br>";
				result += myFormatter.format(BAComponents/(Double.parseDouble(this.getConfig("MediumBattleArmorPP")))) + " Medium battle armor<br>";
				result += myFormatter.format(BAComponents/(Double.parseDouble(this.getConfig("HeavyBattleArmorPP")))) + " Heavy battle armor<br>";
				result += myFormatter.format(BAComponents/(Double.parseDouble(this.getConfig("AssaultBattleArmorPP")))) + " Assault battle armor<br>";
			}

			MWServ.mwlog.debugLog("SetComponentsProduced");
			// and return the result to CampaignMain in order to have it sent to the players
			setComponentsProduced(Unit.MEK, 0);
			setComponentsProduced(Unit.VEHICLE, 0);
			setComponentsProduced(Unit.INFANTRY, 0);
			setComponentsProduced(Unit.PROTOMEK, 0);
			setComponentsProduced(Unit.BATTLEARMOR, 0);
		} else
			addShowProductionCountNext(-1);

		result += "</font>";

		MWServ.mwlog.debugLog("Send House Updates");
		//send house updates, if not empty
		if (hsUpdates.length() > 0)
			CampaignMain.cm.doSendToAllOnlinePlayers(this, "HS|" + hsUpdates.toString(), false);
		MWServ.mwlog.debugLog("returning");
		return result;
	}

	/**
	 * @author V.I. Lenin aka Travis Shade
	 * @param m
	 * @return
	 * 
	 * TODO: Refactor to reduce redundant code. Should use typename.toLowerCase()
	 *       in place of explicit paths to filenames.
	 */
	private String getExcuseForUnitFailure(SUnit m) {

		if (m.getType() == Unit.MEK)
			return this.scrapExcuseHelper("./data/scrapmessages/mekscrapmessages.txt",m);
			
		else if (m.getType() == Unit.VEHICLE)
			return this.scrapExcuseHelper("./data/scrapmessages/vehiclescrapmessages.txt",m);

		else if (m.getType() == Unit.PROTOMEK)
			return this.scrapExcuseHelper("./data/scrapmessages/protoscrapmessages.txt",m);
		
		else if (m.getType() == Unit.BATTLEARMOR)
			return this.scrapExcuseHelper("./data/scrapmessages/bascrapmessages.txt",m);

		else if (m.getType() == Unit.INFANTRY)
			return this.scrapExcuseHelper("./data/scrapmessages/infantryscrapmessages.txt",m);
		
		//This should never be reached :)
		return "A " + m.getModelName() + " was kidnapped by aliens from outer space";
	}
	
	/**
	 * Helper method for SHouse.getExcuseForUnitFailure()
	 * that factors out highly redundant input stream code.
	 */
	private String scrapExcuseHelper(String filepath, SUnit unit) {
		
		try {
			
			//set up input buffers
			FileInputStream fis = new FileInputStream(filepath);
			BufferedReader dis = new BufferedReader(new InputStreamReader(fis));
			
			//pick random message, given count from line 1
			int messages = Integer.parseInt(dis.readLine());
			int id = CampaignMain.cm.getR().nextInt(messages);
			
			//read lines until counter reaches randomly selected message
			String scrapMessage = "";
			while (dis.ready()) {
				scrapMessage = dis.readLine();
				if (id <= 0)
					break;
				id--;
			}
			
			//close buffers
			dis.close();
			fis.close();
			
			//replace targetted text w/ unit & pilot specific messages and return.
			String scrapMessageWithPilot = scrapMessage.replaceAll("PILOT",unit.getPilot().getName());
			String scrapMessageForPlayer = scrapMessageWithPilot.replaceAll("UNIT",unit.getModelName());
			return scrapMessageForPlayer;

		} catch (Exception e) {//./data/scrapmessages/ is 21 chars. strip path leader and just name file w/ problems.
			MWServ.mwlog.errLog("A problem occured with your " + filepath.substring(21,filepath.length()) + " file!");
			return "A " + unit.getModelName() + " was kidnapped by aliens from outer space";
		}
	}

	// VOTE AND RANKING METHODS @urgru 9/12/04
	/*
	 * Need to make a few temp vectors when a faction is first created, which
	 * hold ranking orders. Think about how to do this while still being
	 * efficient w/i Hibernate. Looping through the entive vote vector for each
	 * player to get a typecount seems too inefficient for words --- but may be
	 * fine w/ SQL.
	 * 
	 * Talk about this with Helge before implementing anything.
	 */

	// PRODUCTION POINT METHODS @urgru 02/03/03
	/**
	 * A method which returns the number of PP a faction has for a specified
	 * weight class
	 * 
	 * @param weight - the weight class to return PP for
	 * @return type_id - number of PP the faction has for a given weight class
	 */
	public int getPP(int weight, int type_id) {
		Vector<Integer> v = this.getComponents().get(type_id);
		if (v == null)
			return 0;
		Integer i = v.elementAt(weight);
		if (i == null)
			return 0;
		return i.intValue();
	}

	public Vector<SUnitFactory> getPossibleFactoryForProduction(int type, int weight, boolean ignoreRefresh) {
		Vector<SUnitFactory> possible = new Vector<SUnitFactory>(1,1);
		Iterator e = Planets.values().iterator();
		while (e.hasNext()) {
			SPlanet p = (SPlanet) e.next();
			Vector v = p.getFactoriesOfWeighclass(weight);
			for (int i = 0; i < v.size(); i++) {
				SUnitFactory MF = (SUnitFactory) v.elementAt(i);
				if (MF.canProduce(type) && (ignoreRefresh || MF.getTicksUntilRefresh() < 1)) {
					possible.add(MF);
				}
			}
		}
		return possible;
	}

	/**
	 * Method that returns a factory originally owned by this faction which is able
	 * to produce units of the requested tyoe and weight. This is used during ticks
	 * and with a-specific requests (RequestCommand), so that units build randomly
	 * on ticks or pursuant to a general purchase request are from the faction's own
	 * tables.
	 */
	public SUnitFactory getNativeFactoryForProduction(int type, int weight) {
		
		//get all possible @ weight and type and return if none exist
		Vector<SUnitFactory> allPossible = getPossibleFactoryForProduction(type, weight, false);
		if (allPossible.size() == 0)
			return null;
		
		//sort out non-faction factories and return if none exist
		Vector<SUnitFactory> factionPossible = new Vector<SUnitFactory>(1,1);
		for (SUnitFactory currFac : allPossible) {
			if (currFac.getFounder().equalsIgnoreCase(this.getName()))
				factionPossible.add(currFac);
		}
		if (factionPossible.size() == 0)
			return null;
		
		//select a random factory to return
		int rand = CampaignMain.cm.getR().nextInt(factionPossible.size());
		return (factionPossible.elementAt(rand));
	}

	public int getMaxAllowedPP(int weight, int type_id) {
		String unitAPMax = "APAtMax" + Unit.getWeightClassDesc(weight) + "Units";
		int maxUnits = Integer.parseInt(this.getConfig(unitAPMax));
		return maxUnits * this.getPPCost(weight, type_id);
	}

	/**
	 * A method which returns the PP COST of a unit. Meks and Vehicles are
	 * segregated by weightclass. Infantry are flat priced accross all weight
	 * classes.
	 * 
	 * @param weight - the weight class to be checked
	 * @return int - the PP cost
	 */
	public int getPPCost(int weight, int type_id) {

		int result = Integer.MAX_VALUE;
		String classtype = Unit.getWeightClassDesc(weight)+ Unit.getTypeClassDesc(type_id) + "PP";
 
		if (type_id == Unit.MEK)
			result = Integer.parseInt(this.getConfig(Unit.getWeightClassDesc(weight) + "PP"));
		else
			result = Integer.parseInt(this.getConfig(classtype));

		// modify the result by the faction price modifier
		result += this.getHouseUnitComponentMod(type_id, weight);

		// dont allow negative component use
		result = Math.max(1, result);
		
		return result;
	}

	/**
	 * A method which adds a specified number of PP to Stores of the given
	 * weight class. Can send house status updates, but also returns cmd to
	 * be added to longer lists of changes.
	 * 
	 * @param weight   - int, the weight class to add to
	 * @param type_id  - int, type of of PP to add
	 * @param quantity - int, number of components to add
	 */
	public String addPP(int weight, int type_id, int val, boolean sendUpdate) {
		
		//store starting PP
		int startingPP = this.getPP(weight, type_id);
		
		try { 
			
			//nothing to add if they have no factories.
			if (!Boolean.parseBoolean(this.getConfig("ProduceComponentsWithNoFactory")) && this.getPossibleFactoryForProduction(type_id, weight, true).size() < 1 && val > 0)
				return "";
			
			//standard addition
			Vector<Integer> v = this.getComponents().get(type_id);
			v.setElementAt(new Integer(v.elementAt(weight).intValue() + val), weight);
		} catch (Exception ex) {
			MWServ.mwlog.errLog(ex);
			MWServ.mwlog.errLog("Error in addPP()");
			MWServ.mwlog.errLog("weight: " + weight + " type: " + type_id + " value: " + val);
			Vector v = new Vector(1,1);
			for (int i = 0; i < 4; i++)// Weight
				v.add(new Integer(0));
			
			getComponents().put(new Integer(type_id), v);
		}
		
		//if PP is unchanged, no need to send a real update
		if (startingPP == getPP(weight, type_id))
			return "";
		
		//else, PP changed and we need to make an update string
		String hsUpdate = this.getHSPPChangeString(weight,type_id);
		if (sendUpdate)
			CampaignMain.cm.doSendToAllOnlinePlayers(this, "HS|" + hsUpdate, false);
		
		return hsUpdate;
	}

	/**
	 * A method which returns a unit from the SHouse's queue. This should only
	 * be called from SHouse (during ticks) or RequestDonatedCommand (during an
	 * ask). If there is no queue'd unit of the given weightclass/type, a null
	 * is returned.
	 * 
	 * WARNING!! getEntity() returns a unit, which means it cannot return a HS|
	 * command string like removeUnit() does. Code that makes use of getEntity
	 * will need to set up and send one using getHSUnitRemovalString().
	 */
	public SUnit getEntity(int weightclass, int type_id) {
		Vector<SUnit> s;
		try {
			s = this.getHangar(type_id).elementAt(weightclass);
		} catch (Exception ex) {
			MWServ.mwlog.errLog(ex);
			MWServ.mwlog.errLog("Empty Vector in getEntity");
			return null;
		}
		
		if (s == null)
			return null;
		
		else if (getNumberOfNonSaleUnits(s) > 0) {
            SUnit m = null;
            
            /*
             * FIXME: This is an extremely dangerous loop. Although default settings
             * on servers should prevent a faction bay from emptying while a unit is
             * on the market, if someone changed the settings (such that there were 1
             * or 2 units per bay), there's a risk that all would be bought and this 
             * will become an infinite loop.
             */
            while (true) {
    			int ran = CampaignMain.cm.getR().nextInt(s.size());
    			m = s.elementAt(ran);
                if (m.getStatus() != Unit.STATUS_FORSALE){
        			s.removeElementAt(ran);
        			m.setStatus(Unit.STATUS_OK);
                    break;
                }
            }
			return m;
		}
		return null;
	}

    private int getNumberOfNonSaleUnits(Vector<SUnit> units){
        int count = 0;
        
        for ( SUnit unit : units ){
            if ( unit.getStatus() != Unit.STATUS_FORSALE )
                count++;
        }
        return count;
    }
    
	/**
	 * Method required for ISeller compliance. Used to distinguish between human
	 * controlled actors (SPlayer class) and factions/automated actors (this).
	 */
	public boolean isHuman() {
		return false;
	}

	/**
	 * Method required for compliance with ISeller. Loop through all house
	 * queues and return a unit with matching ID, or null if no matching unit is
	 * found.
	 * 
	 * NOTE: This should be used sparingly. Outside of the Market and various
	 * admin commands, there are ALWAYS better ways to get a unit from SHouse.
	 */
	public SUnit getUnit(int unitIDtoFind) {

		//for all types and weight classes
		for (int type_id = 0; type_id < Unit.TOTALTYPES; type_id++) {
			for (int i = Unit.LIGHT; i <= Unit.ASSAULT; i++) {

				//Loop through all units of the current type/weightclass
				Iterator it = ((Vector) this.getHangar(type_id).elementAt(i)).iterator();
				while (it.hasNext()) {
					SUnit currU = (SUnit) it.next();
					if (currU.getId() == unitIDtoFind)
						return currU;
				}

			}// end weight class loop
		}// end unit type loop

		//no matching unit in any weight/type queue
		return null;
	}

	/**
	 * Simple method which determines whether a given SHouse (and its players) may access
	 * the market to SELL units. We check this loop continuously instead of saving a value
	 * in the SHouse (inefficient) b/c the config may change between checks.
	 */
	public boolean maySellOnBM() {
		StringTokenizer blockedFactions = new StringTokenizer(this.getConfig("BMNoSell"), "$");
		while (blockedFactions.hasMoreTokens()) {
			if (getName().equals(blockedFactions.nextToken()))
				return false;
		}
		return true;
	}
	
	/**
	 * Simple method which determines whether a given SHouse (and its players) may access
	 * the market to BUY units. We check this loop continuously instead of saving a value
	 * in the SHouse (inefficient) b/c the config may change between checks.
	 */
	public boolean mayBuyFromBM() {
		StringTokenizer blockedFactions = new StringTokenizer(this.getConfig("BMNoBuy"), "$");
		while (blockedFactions.hasMoreTokens()) {
			if (getName().equals(blockedFactions.nextToken()))
				return false;
		}
		return true;
	}
	
	public SPlayer getPlayer(String s) {
		
		String lowerName = s.toLowerCase();
		if (this.getReservePlayers().containsKey(lowerName))
			return this.getReservePlayers().get(lowerName);
		if (this.getActivePlayers().containsKey(lowerName))
			return this.getActivePlayers().get(lowerName);
		if (this.getFightingPlayers().containsKey(lowerName))
			return this.getFightingPlayers().get(lowerName);
		
		return null;
	}

	/**
	 * A method which returns the MU cost of a specified campaign unit.
	 * 
	 * @return int - # of MU it takes to buy a unit of the given weight class
	 */
	public int getPriceForUnit(int weightclass, int type_id) {
		int result = Integer.MAX_VALUE;
		String classtype = Unit.getWeightClassDesc(weightclass)+ Unit.getTypeClassDesc(type_id) + "Price";

		if (Boolean.parseBoolean(this.getConfig("UseCalculatedCosts"))) {
			double cost = 0;
			if (type_id == Unit.VEHICLE || type_id == Unit.MEK)
				cost = CampaignMain.cm.getUnitCostLists().getMinCostValue(weightclass, type_id);
			else
				cost = CampaignMain.cm.getUnitCostLists().getMinCostValue(Unit.LIGHT, type_id);
			result = (int) (cost * Double.valueOf(this.getConfig("CostModifier")));
			return result;
		}

		if (type_id == Unit.MEK)
			result = Integer.parseInt(this.getConfig(Unit.getWeightClassDesc(weightclass) + "Price"));
		else
			result = Integer.parseInt(this.getConfig(classtype));

		// modify the result by the faction price modifier
		result += this.getHouseUnitPriceMod(type_id, weightclass);

		// dont allow negative pricing
		if (result < 0)
			result = 0;

		return result;
	}// end getPriceForUnit()

	/**
	 * A method which returns the influence cost of a specified campaign mech.
	 * 
	 * @return int - # of PP it takes to buy a mech of the given units weight class
	 */
	public int getInfluenceForUnit(int weightclass, int type_id) {
		int result = Integer.MAX_VALUE;
		String classtype = Unit.getWeightClassDesc(weightclass)+ Unit.getTypeClassDesc(type_id) + "Inf";

		if (type_id == Unit.MEK)
			result = Integer.parseInt(this.getConfig(Unit.getWeightClassDesc(weightclass)+ "Inf"));
		else
			result = Integer.parseInt(this.getConfig(classtype));

		// modify the result by the faction price modifier
		result += this.getHouseUnitFluMod(type_id, weightclass);

		// dont allow negative pricing
		if (result < 0)
			result = 0;
		
		return result;
	}

	public void addPlanet(SPlanet p) {
		if (getPlanets().get(p.getName()) == null) {
			getPlanets().put(p.getName(), p);
			setBaysProvided(getBaysProvided() + p.getBaysProvided());
			setComponentProduction(getComponentProduction() + p.getCompProduction());
		}
	}

	public void removePlanet(SPlanet p) {
		if (getPlanets().get(p.getName()) != null) {
			getPlanets().remove(p.getName());
			setBaysProvided(getBaysProvided() - p.getBaysProvided());
			setComponentProduction(getComponentProduction() - p.getCompProduction());
		}
	}

	public void transferMoney(SPlayer p, int amount) {
		if (p != null) {
			p.addMoney(amount);
			this.setMoney(this.getMoney() - amount);
		}
	}

	public String removeUnit(SUnit unitToRemove, boolean sendUpdate) {


		Vector<SUnit> Weightclass = this.getHangar(unitToRemove.getType()).elementAt(unitToRemove.getWeightclass());
		Weightclass.remove(unitToRemove);
		
		String hsUpdate = this.getHSUnitRemovalString(unitToRemove);
		if(sendUpdate)
			CampaignMain.cm.doSendToAllOnlinePlayers(this, "HS|" + hsUpdate, false);
		
		return hsUpdate;
	}

	/**
	 * Pass-though method. <code>boolean isNew</code> is unused in SHouse;
	 * however, it is needed to comply with IBuyer.
	 */
	public String addUnit(SUnit unit, boolean isNew, boolean sendUpdate) {
		return this.addUnit(unit, sendUpdate);
	}

	/**
	 * Method which adds a unit to the house. If sendUpdate is true, all logged
	 * in house members are sent an HS|AU|. AU| cmd is returned for use in bulk
	 * commands by other methods, like SHouse.tick().
	 */
	public String addUnit(SUnit unit, boolean sendUpdate) {

        if ( Boolean.parseBoolean(this.getConfig("AllowPersonalPilotQueues")) 
                && (unit.getType() == Unit.MEK || unit.getType() == Unit.PROTOMEK)
                && !unit.hasVacantPilot() ){
            this.getPilotQueues().addPilot(unit.getType(),(SPilot)unit.getPilot());
            if(CampaignMain.cm.isUsingMySQL()) {
            	CampaignMain.cm.MySQL.linkPilotToFaction(((SPilot)unit.getPilot()).getDBId(), this.getDBId());
            }
            unit.setPilot(new SPilot("Vacant",99,99));
        }
        
		if (Boolean.parseBoolean(this.getConfig("UseOnlyOneVehicleSize")) && unit.getType() == Unit.VEHICLE)
			unit.setWeightclass(Unit.LIGHT);

		Vector<SUnit> weightClass = getHangar(unit.getType()).elementAt(unit.getWeightclass());
		if (weightClass.contains(unit))
			return "";
		if(CampaignMain.cm.isUsingMySQL()){
			if(unit.getDBId()== 0)
				unit.toDB();
			CampaignMain.cm.MySQL.linkUnitToFaction(unit.getDBId(), this.getDBId());
		}
		weightClass.add(unit);
		
		String hsUpdate = this.getHSUnitAdditionString(unit);
		if (sendUpdate)
			CampaignMain.cm.doSendToAllOnlinePlayers(this, "HS|" + hsUpdate, false);
		
		return hsUpdate;	
	}

	/*
	 * Log a player into the faction and put him on reserve (normal)
	 * status. This should be called only from CM.doLoginPlayer(). If
	 * the player is signing on, the SignOn command will handle the
	 * reconnectionCheck() and adjust status to fighting if necessary.
	 */
	protected String doLogin(SPlayer p) {
		
		//lowercase the name
		String realName = p.getName();
		String lowerName = realName.toLowerCase();
		
		//test to see if the player is already in the hashes
		if (this.isLoggedIntoFaction(lowerName)) {
			CampaignMain.cm.toUser("CS|" + SPlayer.STATUS_RESERVE, realName, false);
			return null;
		}
				
		//update the player's myHouse
		CampaignMain.cm.toUser("PL|SH|" + this.getName(), realName, false);

		Date d = new Date(System.currentTimeMillis());
		MWServ.mwlog.mainLog(d + ":" + "User Logged into House: " + realName);
		
		/*
         * Remove from all status hashes and place in reserve, in case the players
         * was somewho disconnected and not recognized while signing back on. The
         * code will later check for a running game and escalate to fighting state
         * if needed.
         */
        reservePlayers.remove(lowerName);
        activePlayers.remove(lowerName);
        fightingPlayers.remove(lowerName);
        
        getReservePlayers().put(lowerName, p);
		p.setLastSentStatus("");
		
		CampaignMain.cm.toUser("CS|" + SPlayer.STATUS_RESERVE, realName, false);
		
		//send player his pilot lists and exclude lists
		CampaignMain.cm.toUser("PL|PPQ|" + p.getPersonalPilotQueue().toString(true), realName, false);
		CampaignMain.cm.toUser("PL|AEU|" + p.getExclusionList().adminExcludeToString("$"), realName, false);
		CampaignMain.cm.toUser("PL|PEU|"+ p.getExclusionList().playerExcludeToString("$"), realName, false);
		
        /*
		 * Old code used to look for a running task here, and send auto
		 * armies and game options to players who had running games. Players
		 * who had games were put in the fighting members hash, players who
		 * did not were placed in the active hash.
		 * 
		 * Now we use doReconnectionCheck() in the Server's SignOn cmd
		 * after the login is processed. This sends any autoarmies/options
		 * and stops discon threads. It also removes fighting players from
		 * active and places them in fighting, as appropriate.
		 * 
		 * In sum, we can put all players in the Reserve hash at this point,
		 * and they will be properly moved afterwards when setBusyNoOpList()
		 * is run.
		 */
		CampaignMain.cm.getIThread().removeImmunity(p);//logging in player should NEVER be immune
		
		//send player the MOTD
		Command c = CampaignMain.cm.getServerCommands().get("MOTD");
		c.process(new StringTokenizer("",""),realName);
		
		//send the current BM and HS to the player
		CampaignMain.cm.getMarket().sendCompleteMarketStatus(p);
		CampaignMain.cm.toUser("HS|CA|0", realName, false);//clear old data
		CampaignMain.cm.toUser(this.getCompleteStatus(),realName, false);
		CampaignMain.cm.getPartsMarket().updatePartsBlackMarketPlayer(p);
		
		/*
		 * Now that the player is loaded and has a fresh timestamp look for
		 * a corresponding SmallPlayer.
		 * 
		 * If the smallplayer exists, nothing needs to be done. The
		 * SmallPlayer's values will all (with the exception of faction,
		 * which is hardset during generation) be over written with the
		 * latest SPlayerData information when the various set() calls are
		 * made during SPlayer.fromString() during player load.
		 * 
		 * Otherwise, make a new SmallPlayer with the SPlayer's info and
		 * insert it into the Hashtable. @urgru
		 */
		SmallPlayer smallp = SmallPlayers.get(lowerName);
		if (smallp == null) {// make a new one
			smallp = new SmallPlayer(p.getExperience(), p.getLastOnline(),p.getRating(), realName, p.getFluffText(), this);
			SmallPlayers.put(lowerName, smallp);
		}
		
		//send the player the latest data from the factionbays
		p.setLastOnline(System.currentTimeMillis());//must be done after smallplayer creation
		
		return ("<font color=\"black\">[*] Logged into " + getColoredNameAsLink() + ".</font>");
	}

	/**
	 * Remove a player from the house lists. Should be called only
	 * from CampaignMain's .doLogout(), which sends needed status
	 * updates to all players and sets up save information.
	 * 
	 * We don't need to worry about disconnections or oddly timed
	 * logouts (eg - midgame). The only time that kind of abrupt
	 * removal should be allowed is when a client closes of loses
	 * its connection, which is handled by ServerWrapper.signOff().
	 */
	protected void doLogout(SPlayer p) {
		
		//if the is already logged in, return
		String realName = p.getName();
		String lowerName = realName.toLowerCase();
		if (!this.isLoggedIntoFaction(lowerName))
			return;

		//note: this removes the player from all attacker/defender lists.
		//if (p.getDutyStatus() == SPlayer.STATUS_ACTIVE)
			p.setActive(false);

		//remove from all status hashes
		reservePlayers.remove(lowerName);
		activePlayers.remove(lowerName);
		fightingPlayers.remove(lowerName);
		
		//add info to logs
		Date d = new Date(System.currentTimeMillis());
		MWServ.mwlog.mainLog(d + ":" + "User Logged out: " + realName);
		CampaignMain.cm.toUser("CS|" + SPlayer.STATUS_LOGGEDOUT,realName, false);
	}

	/**
	 * Completely remove a player from the house. Very simple. Donate
	 * the players units, clear out his votes, then nuke hims pfile.
	 */
	public void removePlayer(SPlayer p, boolean donateMechs) {
		
		//check to make sure he's not null
		if (p == null)
			return;
		
		//log the player out of the house
		this.doLogout(p);
		
        //Never send the newbie mechs back to the house bays.
        if ( this.isNewbieHouse() )
            donateMechs = false;
        
		//if we're donating all units, do so
		if (donateMechs) {
			StringBuilder hsUpdates = new StringBuilder();
            boolean allowDamagedUnits = CampaignMain.cm.isUsingAdvanceRepair() && Boolean.parseBoolean(this.getConfig("AllowDonatingOfDamagedUnits"));
			for (SUnit currUnit : p.getUnits()){

                boolean damaged = (!UnitUtils.canStartUp(currUnit.getEntity()) 
                        || UnitUtils.hasArmorDamage(currUnit.getEntity()) 
                        || UnitUtils.hasCriticalDamage(currUnit.getEntity())); 

                if ((damaged && allowDamagedUnits) || !damaged )
                	hsUpdates.append(addUnit(currUnit, false));
            }
			
			//if units were donated, send updates to factionmates
			if (hsUpdates.length() > 0)
				CampaignMain.cm.doSendToAllOnlinePlayers(this, "HS|" + hsUpdates.toString(), false);
		}

		/*
		 * The player is moving to a new faction (or quitting). Rather
		 * than letting all of his votes remain and count, strip them.
		 */
		CampaignMain.cm.getVoteManager().removeAllVotesByPlayer(p);
		CampaignMain.cm.getVoteManager().removeAllVotesForPlayer(p);
		
		//remove small player. don't delete the pfile.
		p.getMyHouse().getSmallPlayers().remove(p.getName().toLowerCase());
				
		//if the player is in the save queue, remove him
		p.setSave(false);

	}//end removePlayer()

	/*
	 * Used by RangeCommand and CheckDistCommand.
	 * TODO: Refactor and remove.
	 */
	public int getDistanceTo(SPlanet p, SPlayer player) {
		// Is the faction on the planet?
		if (p.getInfluence().getInfluence(this.getId()) > 10)
			return 0;

        double distSq = Integer.MAX_VALUE;
		double tdist;

		Iterator e = CampaignMain.cm.getData().getAllPlanets().iterator();
		while (e.hasNext()) {
			SPlanet pl = (SPlanet) e.next();
			// Only consider planet if we control at least 25%
			if (pl.getInfluence().getInfluence(this.getId()) >= 25) {
				tdist = pl.getPosition().distanceSq(p.getPosition());
				if (tdist < distSq)
					distSq = tdist;
			}
		}
		return (int) distSq;
	}

	/**
	 * Generates serialized version of SHouse to send to clients for
	 * HouseStatus tab. Complete status is sent on login. Afterwards,
	 * changes are transmitted incremementally.
	 */
	public String getCompleteStatus() {
		
		String cmdDelim = "|";//used to separate HS| subcommands
		String internalDelim = "$";//used to separate elements within subcommands
		
		//first item, name
		StringBuilder result = new StringBuilder();
		result.append("HS|FN|" + this.getName() + cmdDelim);
		
		/*
		 * Second, append misc. component information. Standard
		 * loop through all weight classes and types.
		 * 
		 * Structure:
		 * CC|weight$type$components$producableunits|
		 */
		for (int type_id = 0; type_id < Unit.TOTALTYPES; type_id++) {
			for (int weight = Unit.LIGHT; weight <= Unit.ASSAULT; weight++)
				result.append(this.getHSPPChangeString(weight,type_id));
		}
		
		
		/*
		 * Third block - factories. Use AF| commands to add factories to
		 * each type and weight class. Similar to component loop above, but
		 * factory entries contain more information.
		 * 
		 * Loop through all worlds, check control, and send owned factories.
		 * 
		 * Structure:
		 * AF|weight$metatype$founder$planet$name$refreshtime|
		 */
		for (SPlanet currPlanet : getPlanets().values()) {
			
			//skip unowned & contested worlds
			if (!this.equals(currPlanet.getOwner()))
				continue;
			
			for (int i = 0; i < currPlanet.getUnitFactories().size(); i++) {
				SUnitFactory currFactory = (SUnitFactory)currPlanet.getUnitFactories().get(i);
				result.append("AF" + cmdDelim);//cmd header
				
				result.append(currFactory.getWeightclass() + internalDelim);
				result.append(currFactory.getType() + internalDelim);
				
				result.append(currFactory.getFounder() + internalDelim);
				result.append(currFactory.getPlanet().getName() + internalDelim);
				result.append(currFactory.getName() + internalDelim);
				result.append(currFactory.getTicksUntilRefresh());
				
				result.append(cmdDelim);
			}			
		}
		
		/*
		 * Fourth, and final, block - units in faction bays.
		 */
		for (int type_id = 0; type_id < Unit.TOTALTYPES; type_id++) {
			
			for (int weight = Unit.LIGHT; weight <= Unit.ASSAULT; weight++) {
				
				//skip units that are for sale. send all others.
				Vector<SUnit> unitSet = this.getHangar(type_id).elementAt(weight); 
				for (SUnit currU : unitSet) {
					if (currU.getStatus() == Unit.STATUS_FORSALE)
						continue;
					result.append(this.getHSUnitAdditionString(currU));
				}
			}
		}

		return result.toString();
	}
	
	/**
	 * Construct a string to send to clients if unit is added.
	 * Format is: AU|weight$type$chassis$model$damage|
	 */
	public String getHSUnitAdditionString(SUnit u) {
		
		StringBuilder result = new StringBuilder();
		
		//header info
		result.append("AU|");
		result.append(u.getWeightclass() + "$" + u.getType() + "$");
		
		//unit information (note: no pilot info included)
		Entity currE = u.getEntity();
		result.append(currE.getChassis() + " " + currE.getModel() + "$");
		result.append(u.getId());//ID used to remove units. Never shown to players in GUI.
		
		//if using AR, send damage information
		if (CampaignMain.cm.isUsingAdvanceRepair())
			result.append("$" + UnitUtils.unitBattleDamage(currE));
	
		//fianlize and return
		result.append("|");
		return result.toString();
	}
	
	/**
	 * Construct a string to send to clients if PP changes.
	 * Format is CC|weight$type$components$producableunits|
	 */
	public String getHSPPChangeString(int weight, int type_id) {
		
		StringBuilder result = new StringBuilder();
		
		int costPerUnit = Math.max(1, this.getPPCost(weight, type_id));
		int currentPP = this.getPP(weight,type_id);
		
		result.append("CC|");
		result.append(weight + "$" + type_id + "$");
		result.append(currentPP + "$" + (currentPP/costPerUnit));
		
		result.append("|");
		return result.toString();
	}
	
	/**
	 * Construct a string to send to clients if unit is removed from a house.
	 * Called by SHouse internally, but also outside of SHouse as as a follow-up
	 * to SHouse.getEntity().
	 */
	public String getHSUnitRemovalString(SUnit u) {
		
		StringBuilder result = new StringBuilder();
		
		//header info
		result.append("RU|");
		result.append(u.getWeightclass() + "$" + u.getType() + "$");
		result.append(u.getId());
			
		//fianlize and return
		result.append("|");
		return result.toString();
	}

	// Getter and Setter
	public int getMoney() {
		return Money;
	}

	public String getColoredName() {
		return "<font color=\"" + this.getHouseColor() + "\">" + this.getName() + "</font>";
	}

	public String getColoredNameAsLink() {
		return "<font color=\"" + this.getHouseColor() + "\">" + this.getNameAsLink() + "</font>";
	}

	public String getColoredAbbreviation(boolean includeBrackets) {
		String toReturn = "<font color=\"" + this.getHouseColor() + "\">";
		if (includeBrackets)
			toReturn += "[";
		toReturn += this.getAbbreviation();
		if (includeBrackets)
			toReturn += "]";
		return toReturn += "</font>";
	}

	public ConcurrentHashMap<String, SPlanet> getPlanets() {
		return Planets;
	}

	public void setMoney(int newMoney) {
		Money = newMoney;
	}

	public int getComponentsProduced(int unitType) {
	    Integer component = (Integer)unitComponents.get(unitType); 
	    if (component == null)
	        return 0;
	    //else
	    return component.intValue();
	}

	public int getShowProductionCountNext() {
		return showProductionCountNext;
	}

	/**
	 * @return the small player hashtable
	 */
	public Hashtable<String, SmallPlayer> getSmallPlayers() {
		return SmallPlayers;
	}

	// Comparable
	public int compareTo(Object o) {
		SHouse h = (SHouse) o;
		if (this.getMoney() > h.getMoney())
			return 1;
		else if (this.getMoney() < h.getMoney())
			return -1;
		return this.getName().compareTo(h.getName());
	}

	public void addMoney(int amount) {
		this.setMoney(this.getMoney() + amount);
	}

	public void addComponentsProduced(int unitType, int amount) {
		this.setComponentsProduced(unitType, this.getComponentsProduced(unitType)+ amount);
	}

	public void addShowProductionCountNext(int amount) {
		this.setShowProductionCountNext(this.getShowProductionCountNext()+ amount);
	}

	/**
	 * Returns all online players. Should be used sparingly.
	 * 
	 * TODO: Remove references to this method, where possible.
	 */
	public Hashtable<String, SPlayer> getAllOnlinePlayers() {
		Hashtable<String,SPlayer> allPlayers = new Hashtable<String,SPlayer>();
		allPlayers.putAll(this.getReservePlayers());
		allPlayers.putAll(this.getActivePlayers());
		allPlayers.putAll(this.getFightingPlayers());
		return allPlayers;
	}

	/**
	 * @param baysProvided - The baysProvided to set.
	 */
	public void setBaysProvided(int baysProvided) {
		BaysProvided = baysProvided;
	}

	/**
	 * @param componentProduction - The componentProduction to set.
	 */
	public void setComponentProduction(int componentProduction) {
		ComponentProduction = componentProduction;
	}

	public void setComponentsProduced(int unitType, int components) {
		this.unitComponents.put(new Integer(unitType), new Integer(components));
	}

	public void setShowProductionCountNext(int i) {
		showProductionCountNext = i;
	}
	
	/**
	 * @return Returns the initialHouseRanking.
	 */
	public int getInitialHouseRanking() {
		return initialHouseRanking;
	}

	/**
	 * @param initialHouseRanking - the initialHouseRanking to set.
	 */
	public void setInitialHouseRanking(int initialHouseRanking) {
		this.initialHouseRanking = initialHouseRanking;
	}
	
	/**
	 * @return Returns the MOTD.
	 */
	public String getMotd() {
		return motd;
	}
	
	/**
	 * @param motd - the MOTD to set.
	 */
	public void setMotd(String motd) {
		this.motd = motd;
	}

	/**
	 * @return Returns the inHouseAttacks.
	 */
	public boolean isInHouseAttacks() {
		return inHouseAttacks;
	}

	/**
	 * @param inHouseAttacks The inHouseAttacks to set.
	 */
	public void setInHouseAttacks(boolean inHouseAttacks) {
		this.inHouseAttacks = inHouseAttacks;
	}
	
	public float getHighestUnitCost(int weight, int type) {
		float cost = 0;
		Vector<SUnit> s;

		try {
			s = this.getHangar(type).elementAt(weight);
		} catch (Exception ex) {
			MWServ.mwlog.errLog(ex);
			MWServ.mwlog.errLog("Empty Vector in getHighestUnitCost");
			return Float.MAX_VALUE;
		}
		if (s == null)
			return Float.MAX_VALUE;

		for (SUnit unit : s) {
			if (unit.getEntity().getCost() > cost)
				cost = (float)unit.getEntity().getCost();
		}

		return cost;

	}

	public Properties getConfig() {
		return config;
	}
	
	public boolean getBooleanConfig(String key) {
		try {
			return Boolean.parseBoolean(this.getConfig(key));
		} catch (Exception ex) {
			return false;
		}
	}

	public int getIntegerConfig(String key) {
		try {
			return Integer.parseInt(this.getConfig(key));
		} catch (Exception ex) {
			return -1;
		}
	}

	public double getDoubleConfig(String key) {
		try {
			return Double.parseDouble(this.getConfig(key));
		} catch (Exception ex) {
			return -1;
		}
	}

	public float getFloatConfig(String key) {
		try {
			return Float.parseFloat(this.getConfig(key));
		} catch (Exception ex) {
			return -1;
		}
	}

	public float getLongConfig(String key) {
		try {
			return Long.parseLong(this.getConfig(key));
		} catch (Exception ex) {
			return -1;
		}
	}

	public String getConfig(String key) {
		
		if ( config == null || config.getProperty(key) == null) {
			return CampaignMain.cm.getConfig(key);
		}
		return config.getProperty(key).trim();
	}
	
	public void saveConfigFile() {
		
		if ( config == null )
			return;
		
		if ( config.size() < 1 ) {
			config = null;
			return;
		}
			
		String fileName = "./data/"+this.getName().toLowerCase()+"_configs.dat";
		try{
			config.setProperty("TIMESTAMP", Long.toString((System.currentTimeMillis())));
			PrintStream ps = new PrintStream(new FileOutputStream(fileName));
			config.store(ps, "Faction Config");
			ps.close();
		} catch (FileNotFoundException fe) {
			MWServ.mwlog.errLog(fileName+" not found");
		} catch (Exception ex) {
			MWServ.mwlog.errLog(ex);
		}   

	}

	public void saveConfigFileToDB() {
		if ( config == null )
			return;
		
		if ( config.size() < 1 ) {
			config = null;
			return;
		}
		int dbId = this.getDBId();
		PreparedStatement ps = null;
		try {
			ps = CampaignMain.cm.MySQL.getPreparedStatement("DELETE from faction_configs WHERE factionID = " + dbId);
			ps.executeUpdate();
			config.setProperty("TIMESTAMP", Long.toString((System.currentTimeMillis())));
			for (Enumeration e = config.keys(); e.hasMoreElements();) {
				String key = (String)e.nextElement();
				String val = config.getProperty(key);
				String sql = "INSERT into faction_configs SET factionID = " + dbId + ", configKey = ?, configValue = ?";
				ps = CampaignMain.cm.MySQL.getPreparedStatement(sql);
				ps.setString(1, key);
				ps.setString(2, val);
				ps.executeUpdate();
			}
			ps.close();
		} catch(SQLException e) {
			MWServ.mwlog.dbLog("SQL Error in SHouse.saveConfigFileToDB: " + e.getMessage());
		}
	}
	
	public void loadConfigFile() {
		
		File configFile = new File("./data/" + this.getName().toLowerCase()+"_configs.dat");
		
		if ( !configFile.exists() ) 
			return;
		
		try {
			config = new Properties();
			config.load(new FileInputStream(configFile));
		}
		catch(Exception ex) {
			MWServ.mwlog.errLog(ex);
		}
	}

	public void loadConfigFileFromDB() {
		ResultSet rs = null;
		Statement stmt = null;
		try {
			stmt = CampaignMain.cm.MySQL.getStatement();
			rs = stmt.executeQuery("SELECT configKey, configValue from faction_configs WHERE factionID = " + this.getDBId());
			boolean configCreated=false;
			while(rs.next()) {
				if(!configCreated) {
					this.config = new Properties();
					configCreated=true;
				}
				this.config.setProperty(rs.getString("configKey"), rs.getString("configValue"));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			MWServ.mwlog.dbLog("SQL Error in SHouse.loadConfigFileFromDB: " + e.getMessage());
		}
	}
	
	public void setForumName(String name) {
		this.forumName = name;
	}
	
	public String getForumName() {
		return this.forumName;
	}
	
	public ConcurrentHashMap<String,SubFaction> getSubFactionList(){
		return subFactionList;
	}
}// end SHouse.java
