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

package server.campaign.commands.admin;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.StringTokenizer;

import server.MWServ;
import server.campaign.commands.Command;
import server.campaign.CampaignMain;
import server.campaign.SHouse;
import server.campaign.SPlanet;
import server.campaign.SUnitFactory;
import server.MWChatServer.auth.IAuthenticator;

import common.AdvancedTerrain;
import common.Continent;
import common.Unit;

public class AdminSavePlanetsToXMLCommand implements Command {
	int accessLevel = IAuthenticator.ADMIN;
	String syntax = "";
	public int getExecutionLevel(){return accessLevel;}
	public void setExecutionLevel(int i) {accessLevel = i;}
	public String getSyntax() { return syntax;}
	
	public void process(StringTokenizer command,String Username) {
		
		//access level check
		int userLevel = CampaignMain.cm.getServer().getUserLevel(Username);
		if(userLevel < getExecutionLevel()) {
			CampaignMain.cm.toUser("Insufficient access level for command. Level: " + userLevel + ". Required: " + accessLevel + ".",Username,true);
			return;
		}
		
		try {
			FileOutputStream out = new FileOutputStream("./campaign/saveplanets.xml");
			PrintStream p = new PrintStream(out);
			p.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE DOCUMENT SYSTEM \"planets.dtd\">");
			p.println("<DOCUMENT>");
			p.println("<MEGAMEKNETPLANETDATA>");
			Iterator e = CampaignMain.cm.getData().getAllPlanets().iterator();
			while (e.hasNext()) {
				SPlanet planet = (SPlanet) e.next();
				p.println("	<PLANET>");
				p.println("		<NAME>"+planet.getName()+"</NAME>");
				p.println("		<COMPPRODUCTION>"+planet.getCompProduction()+"</COMPPRODUCTION>");
				p.println("		<XCOOD>"+ planet.getPosition().x+"</XCOOD>");
				p.println("		<YCOOD>"+planet.getPosition().y+"</YCOOD>");
				p.println("		<INFLUENCE>");
				Iterator flu = planet.getInfluence().getHouses().iterator();
				while (flu.hasNext()) {
					p.println("			<INF>");
					SHouse faction = (SHouse) flu.next();
					p.println("				<FACTION>"+faction.getName()+"</FACTION>");
					p.println("				<AMOUNT>"+planet.getInfluence().getInfluence(faction.getId())+"</AMOUNT>");
					p.println("			</INF>");
				}
				p.println("		</INFLUENCE>");
				p.print("       <ORIGINALOWNER>");
				p.print(planet.getOriginalOwner());
				p.println("</ORIGINALOWNER>");
				Iterator UF = planet.getUnitFactories().iterator();
				while (UF.hasNext()) {
					p.println("		<UNITFACTORY>");
					SUnitFactory factory = (SUnitFactory)UF.next();
					p.println("			<FACTORYNAME>"+factory.getName()+"</FACTORYNAME>");
					p.println("			<SIZE>"+factory.getSize()+"</SIZE>");
					p.println("			<FOUNDER>"+factory.getFounder()+"</FOUNDER>");
					
					if ( factory.canProduce(Unit.MEK))
						p.println("			<TYPE>Mek</TYPE>");
					if ( factory.canProduce(Unit.INFANTRY))
						p.println("			<TYPE>Infantry</TYPE>");
					if ( factory.canProduce(Unit.VEHICLE))
						p.println("			<TYPE>Vehicle</TYPE>");
                    if ( factory.canProduce(Unit.PROTOMEK))
                        p.println("         <TYPE>PROTOMEK</TYPE>");
                    if ( factory.canProduce(Unit.BATTLEARMOR))
                        p.println("         <TYPE>BATTLEARMOR</TYPE>");
					p.println("		</UNITFACTORY>");	
				}
				
				for (Iterator it = planet.getEnvironments().iterator(); it.hasNext();) {
					p.println("		<CONTINENT>");
					Continent pe = (Continent) it.next();
					p.println("			<TERRAIN>"+pe.getEnvironment().getName()+"</TERRAIN>");
					p.println("			<SIZE>"+pe.getSize()+"</SIZE>");
                    if (CampaignMain.cm.getBooleanConfig("UseStaticMaps")){
                        AdvancedTerrain aTerrain = new AdvancedTerrain();
                        aTerrain = planet.getAdvancedTerrain().get(new Integer(pe.getEnvironment().getId()));
                        if ( aTerrain != null ){
                            p.println("                 <ADVANCETERRAIN>");
                            if ( aTerrain.getDisplayName().length() <= 1)
                                p.println("                         <TERRAINNAME>"+pe.getEnvironment().getName()+"/<TERRAINNAME>");
                            else
                                p.println("                         <TERRAINNAME>"+aTerrain.getDisplayName()+"</TERRAINNAME>");
                            p.println("                         <XMAP>"+aTerrain.getXSize()+"</XMAP>");
                            p.println("                         <YMAP>"+aTerrain.getYSize()+"</YMAP>");
                            p.println("                         <MAP>"+aTerrain.isStaticMap()+"</MAP>");
                            p.println("                         <XBOARD>"+aTerrain.getXBoardSize()+"</XBOARD>");
                            p.println("                         <YBOARD>"+aTerrain.getYBoardSize()+"</YBOARD>");
                            p.println("                         <LOWTEMP>"+aTerrain.getLowTemp()+"</LOWTEMP>");
                            p.println("                         <HITEMP>"+aTerrain.getHighTemp()+"</HITEMP>");
                            p.println("                         <GRAVITY>"+aTerrain.getGravity()+"</GRAVITY>");
                            p.println("                         <VACUUM>"+aTerrain.isVacuum()+"</VACUUM>");
                            p.println("                         <NIGHTCHANCE>"+aTerrain.getNightChance()+"</NIGHTCHANCE>");
                            p.println("                         <NIGHTMOD>"+aTerrain.getNightTempMod()+"</NIGHTMOD>");
                            p.println("                         <MAPNAME>"+aTerrain.getStaticMapName()+"</MAPNAME>");
                            p.println("                 </ADVANCETERRAIN>");
                        }
                    }

					p.println("		</CONTINENT>");
				}
                if (!CampaignMain.cm.getBooleanConfig("UseStaticMaps") ){
    				p.println("		<XMAP>"+planet.getMapSize().width+"</XMAP>");
    				p.println("		<YMAP>"+planet.getMapSize().height+"</YMAP>");
    				p.println("		<XBOARD>"+planet.getBoardSize().width+"</XBOARD>");
    				p.println("		<YBOARD>"+planet.getBoardSize().height+"</YBOARD>");
    				p.println("		<TEMPERATURE>");
    				p.println("			<LOWTEMP>"+planet.getTemp().width+"</LOWTEMP>");
    				p.println("			<HITEMP>"+planet.getTemp().height+"</HITEMP>");
    				p.println("		</TEMPERATURE>");
    				p.println("		<GRAVITY>"+planet.getGravity()+"</GRAVITY>");
    				p.println("		<VACUUM>"+planet.isVacuum()+"</VACUUM>");
                }
                p.println("     <WAREHOUSE>"+planet.getBaysProvided()+"</WAREHOUSE>");
                if ( planet.getPlanetFlags().size() > 0 ) {
                	p.println("     <PLANETOPFLAGS>");
	                for ( String key:planet.getPlanetFlags().keySet() ) {
	                	p.println("          <OPKEY>"+key+"</OPKEY>");
	                	p.println("          <OPNAME>"+planet.getPlanetFlags().get(key)+"</OPNAME>");
	                }
	                p.println("     </PLANETOPFLAGS>");
                }
                p.println("     <HOMEWORLD>"+planet.isHomeWorld()+"</HOMEWORLD>");
				p.println("	</PLANET>");
			}
			p.println("</MEGAMEKNETPLANETDATA>");
			p.println("</DOCUMENT>");
			p.close();
			out.close();
		}
		catch ( Exception ex){MWServ.mwlog.errLog(ex);}
		CampaignMain.cm.toUser("XML saved!",Username,true);
		CampaignMain.cm.doSendModMail("NOTE",Username + " has saved the universe to XML");   
		
	}
}