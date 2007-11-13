/*
 * MekWars - Copyright (C) 2005
 * 
 * Original author - nmorris (urgru@users.sourceforge.net)
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 */

package server.campaign.commands.admin;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import common.Unit;

import server.MWServ;
import server.campaign.CampaignMain;
import server.campaign.SUnit;
import server.campaign.commands.Command;
import server.MWChatServer.auth.IAuthenticator;


/**
 * This command just goes through the build lists and makes sure all of the 
 * files on the build list will load with what is in the servers zip files.
 * 
 * @author Torren Oct 21, 2005
 */
public class CyclopsTemplateLoaderCommand implements Command {
	
	int accessLevel = IAuthenticator.ADMIN;
	String syntax = "";
	public int getExecutionLevel(){return accessLevel;}
	public void setExecutionLevel(int i) {accessLevel = i;}
	public String getSyntax() { return syntax;}
	
	public void process(StringTokenizer command,String Username) {
		
		if (accessLevel != 0) {
			int userLevel = CampaignMain.cm.getServer().getUserLevel(Username);
			if(userLevel < getExecutionLevel()) {
				CampaignMain.cm.toUser("AM:Insufficient access level for command. Level: " + userLevel + ". Required: " + accessLevel + ".",Username,true);
				return;
			}
		}

        //Syntax CyclopsTempalteLoader
        
        if ( !CampaignMain.cm.isUsingCyclops() )
            return;
        
        Vector<SUnit> units = new Vector<SUnit>(1,1);
        String entityName = "";

        try{
            FileInputStream in = new FileInputStream("./data/unitfiles/Meks.zip");
            ZipInputStream zipFile = new ZipInputStream(in);
            
            while ( zipFile.available() == 1){
                try{
                    ZipEntry entry = zipFile.getNextEntry();
                    entityName = entry.getName();
                    if ( entityName.startsWith("Error"))
                        continue;
                    SUnit unit = new SUnit("null",entityName,Unit.LIGHT);
                    if ( unit != null )
                        units.add(unit);
                }catch(Exception ex){}
            }
        }catch(FileNotFoundException fnf){
            MWServ.mwlog.errLog("Unable to load Meks.zip for UnitCosts.loadUnitCosts");
        }
         catch(Exception ex){
             MWServ.mwlog.errLog("Error with Meks.zip file "+ entityName);
             MWServ.mwlog.errLog(ex);
        }
         try{
             FileInputStream in = new FileInputStream("./data/unitfiles/Vehicles.zip");
             ZipInputStream zipFile = new ZipInputStream(in);
             
             while ( zipFile.available() == 1){
                 try{
                     ZipEntry entry = zipFile.getNextEntry();
                     entityName = entry.getName();
                     SUnit unit = new SUnit("null",entityName,Unit.LIGHT);
                     if ( unit != null )
                         units.add(unit);
                 }catch (Exception ex){}
             }
                 
         }catch(FileNotFoundException fnf){
             MWServ.mwlog.errLog("Unable to load Vehicles.zip for UnitCosts.loadUnitCosts");
         }
          catch(Exception ex){
              MWServ.mwlog.errLog("Error with Vehicles.zip file "+ entityName);
              MWServ.mwlog.errLog(ex);
         }
          try{
              FileInputStream in = new FileInputStream("./data/unitfiles/Infantry.zip");
              ZipInputStream zipFile = new ZipInputStream(in);
              
              while ( zipFile.available() == 1){
                  try{
                      ZipEntry entry = zipFile.getNextEntry();
                      entityName = entry.getName();
                      SUnit unit = new SUnit("null",entityName,Unit.LIGHT);
                      if ( unit != null )
                          units.add(unit);
                  }catch (Exception ex){}
              }
          }catch(FileNotFoundException fnf){
              MWServ.mwlog.errLog("Unable to load Infantry.zip for UnitCosts.loadUnitCosts");
          }
           catch(Exception ex){
               MWServ.mwlog.errLog("Error with Infantry.zip file "+ entityName);
               MWServ.mwlog.errLog(ex);
          }
         if ( !units.isEmpty() ){
            CampaignMain.cm.toUser("Sending Template Packet",Username,true);
            CampaignMain.cm.getMWCC().unitTemplateWriteFromList(units);
            CampaignMain.cm.toUser("Finished Sending Packet",Username,true);
        }
        CampaignMain.cm.toUser("Template Loading Done.",Username,true);
        CampaignMain.cm.doSendModMail("NOTE",Username+" has used the Cyclops Template loader Command!");

	}
}