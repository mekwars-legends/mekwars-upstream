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
 * Created on 13.04.2004
 *
 */
package server.campaign;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import common.Unit;

import server.MWServ;


/**
 * Class which read in tables and returns random filenames.
 * 
 * The BuildTable class does NOT construct any units. Factories
 * or direct building (welfare, rewards) are used to create actual
 * SUnits and add them to players.
 * 
 * @originalauthor Helge Richter
 */
@SuppressWarnings("unused") 
public class BuildTable {

	//VARIABLES
	public static final String STANDARD = "standard";
	public static final String RARE = "rare";
	public static final String REWARD = "reward";
	public static final String TECH = "tech";
	
	//NO CONSTRUCTOR. ALL METHODS STATIC
	
	/**
	 * Method which randomly selects a unit to build (filename), given a producer,
	 * unit size, mode (rare, etc) and other necessary data. This is used for all
	 * standard factory production.
	 */
	public static String getUnitFilename(String unitProducer, String size, int type_id, String dir) {
		boolean fileFound = false;
		String Filename = "";
		Vector<String> table = null;
		while (!fileFound){
			String fileToRead = getFileName(unitProducer,size,dir,type_id);
			table = getListFromFile(new File(fileToRead));
			MWServ.mwlog.errLog("1");

			/*String techFile = getTechFileName(unitProducer, size, type_id);
			if ( techFile.trim().length() > 0 )
				table.addAll(getListFromFile(new File(techFile)));
			*/
			MWServ.mwlog.errLog("2");

			int ran = CampaignMain.cm.getRandomNumber(table.size());
			MWServ.mwlog.errLog("3");

			Filename = table.elementAt(ran);
			MWServ.mwlog.errLog("4");
			if (Filename.indexOf(".") == -1)
				unitProducer = Filename;
			else
				fileFound = true;
			MWServ.mwlog.errLog("5");

		}
		return Filename;
	}
	
	/**
	 * Method which randomly selects a unit to build (filename), given only
	 * a filename. This is used only to produce welfare units.
	 */
	public static String getUnitFilename(String unitFileName) {
		boolean fileFound = false;
		String Filename = "";
		Vector<String> table = null;
		while (!fileFound){
			table = getListFromFile(new File(unitFileName));
			int ran = CampaignMain.cm.getRandomNumber(table.size());
			Filename = table.elementAt(ran);
			if (Filename.indexOf(".") == -1)
				unitFileName= Filename;
			else
				fileFound = true;
		}
		return Filename;
	}
	
	/**
	 * @param faction The Faction (i.e. - founder, Davion)
	 * @param size The weight class (i.e. - Light)
	 * @param dir  The Directory (i.e. - standard, rare)
	 * @param Type The Type of Entity (i.e. - Unit.MEK, Unit.VEHICLE)
	 * @return the Productionlist
	 */
	public static String getFileName(String faction, String weightclass,String dir,int Type) {
		/*
		 * Build the Filename, using patterns:
		 * FACTION_SIZE.txt or FACTION_SIZE&TYPE.txt
		 *  ex: Davion_Assault.txt
		 *  ex: Marik_LightVehicle
		 *  ex: WardenClan_HeavyBattleArmor.txt
		 *  ex: FadeFalcon_MediumProtoMek.txt
		 *  
		 * and the path, the same way: ./data/buildtables/YEAR/
		 *  ex: ./data/buildtables/3025/
		 *  ex: ./data/buildtables/3130/
		 */
		String addon = "";
		if (Type != Unit.MEK)
			addon = Unit.getTypeClassDesc(Type);
		String result = "./data/buildtables/"+dir+"/" + faction + "_" + weightclass + addon + ".txt";
		if (!new File(result).exists()){
			MWServ.mwlog.errLog("Unable to find build table file "+result+" using ./data/buildtables/"+dir+"/Common_" + weightclass + addon + ".txt");
			result = "./data/buildtables/"+dir+"/Common_" + weightclass + addon + ".txt";
		}
		return result;
	}
	
	public static String getTechFileName(String faction, String weightclass,int Type) {
		/*
		 * Build the Filename, using patterns:
		 * FACTION_SIZE.txt or FACTION_SIZE&TYPE.txt
		 *  ex: Davion_Assault.txt
		 *  ex: Marik_LightVehicle
		 *  ex: WardenClan_HeavyBattleArmor.txt
		 *  ex: FadeFalcon_MediumProtoMek.txt
		 *  
		 * and the path, the same way: ./data/buildtables/YEAR/
		 *  ex: ./data/buildtables/3025/
		 *  ex: ./data/buildtables/3130/
		 */
		String addon = "";
		if (Type != Unit.MEK)
			addon = Unit.getTypeClassDesc(Type);
		String result = "./data/buildtables/"+BuildTable.TECH+"/" + faction + "_" + weightclass + addon + ".txt";
		if (!new File(result).exists()){
			result = "";
		}
		return result;
	}
	
	/**
	 * This reads the unit tables. Format should be:
	 * 
	 * 1  UnitA.hmp
	 * 4  UnitB.hmp
	 * 5  UnitC.hmp
	 * 10 UnitD.hmp
	 * 
	 * This table would produce 50% D's, 25% C's,
	 * 20% B's and 5% A's (Weight/20).
	 * 
	 * @param prodFile tHe File to load from
	 * @param Type the type of unit to load
	 * 
	 * @return buildtable in a Hashtable that also contains a "TotalEntries" key
	 */
	private static Vector<String> getListFromFile(File prodFile){
		Vector<String>  result = new Vector<String>();
		ConcurrentHashMap<String, Integer> unitHolder = new ConcurrentHashMap<String, Integer>();
		try {
			
			FileInputStream fis = new FileInputStream(prodFile);
			BufferedReader dis = new BufferedReader(new InputStreamReader(fis));
			while (dis.ready()) {

				/*
				 * Read the line and remove excess whitespace. Removing whitespace
				 * sensitivity will allow ops to make more readable files; however,
				 * any unit file name that contains 2 spaces consecutively (is there
				 * such a thing?) will be broken. 
				 */
				String l = dis.readLine();
				if (l == null || l.trim().length() == 0)
					continue;
				
				l = l.trim();
				l = l.replaceAll("\\s+"," ");//reduce multi-spaces to one space
				if (l.indexOf(" ") == 0)
					l = l.substring(1,l.length());
				
				StringTokenizer ST = new StringTokenizer(l.trim());
				if (ST.hasMoreElements()) {
					int amount = Integer.parseInt((String)ST.nextElement());
					StringBuilder filename = new StringBuilder();
					while (ST.hasMoreElements()) {
						filename.append(ST.nextToken());
						if (ST.hasMoreTokens())
							filename.append(" ");
					}
					unitHolder.put(filename.toString(), amount);
				}
			}
			dis.close();
			fis.close();

			while ( unitHolder.size() > 0 ){
				for (String filename : unitHolder.keySet() ){
					result.add(filename);
					int count = unitHolder.get(filename);
					if ( --count < 1)
						unitHolder.remove(filename);
					else
						unitHolder.put(filename, count);
						
				}
			}
			result.trimToSize();
		} catch (Exception ex) {
			MWServ.mwlog.errLog(ex);
		}
		return result;
	}
	
	private static void addTech(String faction, int weightclass, int type, String unitName, int chances){
		ConcurrentHashMap<String, Integer> unitHolder = new ConcurrentHashMap<String, Integer>();
		
		if ( chances < 1 )
			return;
		
		try {
			
			String techFileName = getTechFileName(faction, SUnit.getWeightClassDesc(weightclass), type);
			if ( techFileName.trim().length() < 1 )
				return;
			File techFile = new File(techFileName); 
			FileInputStream fis = new FileInputStream(techFile);
			BufferedReader dis = new BufferedReader(new InputStreamReader(fis));
			while (dis.ready()) {

				/*
				 * Read the line and remove excess whitespace. Removing whitespace
				 * sensitivity will allow ops to make more readable files; however,
				 * any unit file name that contains 2 spaces consecutively (is there
				 * such a thing?) will be broken. 
				 */
				String l = dis.readLine();
				if (l == null || l.trim().length() == 0)
					continue;
				
				l = l.trim();
				l = l.replaceAll("\\s+"," ");//reduce multi-spaces to one space
				if (l.indexOf(" ") == 0)
					l = l.substring(1,l.length());
				
				StringTokenizer ST = new StringTokenizer(l.trim());
				if (ST.hasMoreElements()) {
					int amount = Integer.parseInt((String)ST.nextElement());
					StringBuilder filename = new StringBuilder();
					while (ST.hasMoreElements()) {
						filename.append(ST.nextToken());
						if (ST.hasMoreTokens())
							filename.append(" ");
					}
					unitHolder.put(filename.toString(),amount);
				}
			}
			dis.close();
			fis.close();
			
			if ( unitHolder.containsKey(unitName) )
				unitHolder.put(unitName,unitHolder.get(unitName)+chances);
			else
				unitHolder.put(unitName,chances);
			
			BuildTable.saveBuildTableFile(techFile, unitHolder);
		} catch (Exception ex) {
			MWServ.mwlog.errLog(ex);
		}

	}
	
	private static String destroyTech(String faction, int weightclass, int type, int amountToDestroy){
		Vector<String>  result = new Vector<String>();
		ConcurrentHashMap<String, Integer> unitHolder = new ConcurrentHashMap<String, Integer>();
		StringBuffer destroyedTech = new StringBuffer();

		if ( amountToDestroy < 1 )
			return "Nothing to destroy";
		
		try {
			
			String techFileName = getTechFileName(faction, SUnit.getWeightClassDesc(weightclass), type);
			if ( techFileName.trim().length() < 1 )
				return "Nothing to destroy";
			File techFile = new File(techFileName); 
			FileInputStream fis = new FileInputStream(techFile);
			BufferedReader dis = new BufferedReader(new InputStreamReader(fis));
			while (dis.ready()) {

				/*
				 * Read the line and remove excess whitespace. Removing whitespace
				 * sensitivity will allow ops to make more readable files; however,
				 * any unit file name that contains 2 spaces consecutively (is there
				 * such a thing?) will be broken. 
				 */
				String l = dis.readLine();
				if (l == null || l.trim().length() == 0)
					continue;
				
				l = l.trim();
				l = l.replaceAll("\\s+"," ");//reduce multi-spaces to one space
				if (l.indexOf(" ") == 0)
					l = l.substring(1,l.length());
				
				StringTokenizer ST = new StringTokenizer(l.trim());
				if (ST.hasMoreElements()) {
					int amount = Integer.parseInt((String)ST.nextElement());
					StringBuilder filename = new StringBuilder();
					while (ST.hasMoreElements()) {
						filename.append(ST.nextToken());
						if (ST.hasMoreTokens())
							filename.append(" ");
					}
					result.add(filename.toString());
				}
			}
			dis.close();
			fis.close();

			if ( result.size() <= amountToDestroy ){
				techFile.delete();
				return "all technological advances have been wiped out";
			}
			
			for ( ; amountToDestroy > 0; amountToDestroy-- ){
				int ran = CampaignMain.cm.getRandomNumber(result.size());
				destroyedTech.append(result.elementAt(ran));
				destroyedTech.append(", " );
				result.removeElementAt(ran);
			}
			result.trimToSize();
			
			//get rid of the ending ", "
			destroyedTech.delete(destroyedTech.length()-3, destroyedTech.length()-1);
			destroyedTech.append(".");
			
			for (String filename : result){
				if ( unitHolder.containsKey(filename) )
					unitHolder.put(filename,unitHolder.get(filename)+1);
				else
					unitHolder.put(filename,1);
			}

			BuildTable.saveBuildTableFile(techFile, unitHolder);
		} catch (Exception ex) {
			MWServ.mwlog.errLog(ex);
		}
		return destroyedTech.toString();
	}
	
	public static void saveBuildTableFile(File file, ConcurrentHashMap<String,Integer> unitHolder){
		try{
			FileOutputStream fos = new FileOutputStream(file);
			PrintStream ps = new PrintStream(fos);
			
			for (String key : unitHolder.keySet())
				ps.println(unitHolder.get(key)+" "+key);
			
			ps.close();
			fos.close();
		}
		catch(Exception ex){
			MWServ.mwlog.errLog(ex);
		}

	}
}