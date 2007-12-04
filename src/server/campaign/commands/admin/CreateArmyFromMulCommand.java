/*
 * MekWars - Copyright (C) 2007
 * 
 * Original author - Jason Tighe (torren@users.sourceforge.net)
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

import java.io.File;
import java.util.StringTokenizer;
import java.util.Vector;

import megamek.common.Entity;
import megamek.common.EntityListFile;
import megamek.common.MechSummary;
import megamek.common.MechSummaryCache;
import server.MWServ;
import server.campaign.commands.Command;
import server.campaign.CampaignMain;
import server.campaign.SArmy;
import server.campaign.SPlayer;
import server.campaign.SUnit;
import server.campaign.pilot.SPilot;
import server.campaign.pilot.skills.SPilotSkill;
import server.MWChatServer.auth.IAuthenticator;

public class CreateArmyFromMulCommand implements Command {

	int accessLevel = IAuthenticator.ADMIN;

	String syntax = "Filename#Army Name#[Target Player]";

	public int getExecutionLevel() {
		return accessLevel;
	}

	public void setExecutionLevel(int i) {
		accessLevel = i;
	}

	public String getSyntax() {
		return syntax;
	}

	public void process(StringTokenizer command, String Username) {

		// access level check
		int userLevel = CampaignMain.cm.getServer().getUserLevel(Username);
		if (userLevel < getExecutionLevel()) {
			CampaignMain.cm.toUser(
					"AM:Insufficient access level for command. Level: "
							+ userLevel + ". Required: " + accessLevel + ".",
					Username, true);
			return;
		}

		SPlayer p = CampaignMain.cm.getPlayer(Username);
		String filename;
		String armyname;

		try {
			filename = command.nextToken();
			armyname = command.nextToken();
			if (command.hasMoreTokens())
				p = CampaignMain.cm.getPlayer(command.nextToken());
		} catch (Exception ex) {
			CampaignMain.cm.toUser(
					"Synatx Error: /createarmyfrommul " + syntax, Username);
			return;
		}

		if (p == null) {
			CampaignMain.cm.toUser("Unable to find target player", Username);
			return;
		}

		if (p.getArmies().size() >= CampaignMain.cm
				.getIntegerConfig("MaxLancesPerPlayer")) {
			CampaignMain.cm.toUser(p.getName()
					+ " has too many armies already!", Username);
			return;
		}

		if (!new File("./data/armies").exists()) {
			CampaignMain.cm.toUser("directory ./data/armies does not exist",
					Username);
			new File("./data/armies").mkdir();
			return;
		}

		File entityFile = new File("data/armies/" + filename);

		if (!entityFile.exists()) {
			CampaignMain.cm.toUser("Unable to find file " + filename, Username);
			return;
		}

		Vector<Entity> loadedUnits = null;
		try {
			loadedUnits = EntityListFile.loadFrom(entityFile);
			loadedUnits.trimToSize();
		} catch (Exception ex) {
			MWServ.mwlog.errLog(ex);
			CampaignMain.cm.toUser("Unable to load file " + entityFile.getName(), Username);
			return;
		}

		if (loadedUnits.size() < 1) {
			CampaignMain.cm.toUser("No units where loaded from file "
					+ entityFile.getName(), Username);
			return;
		}

		SArmy army = new SArmy(p.getName());

		army.setID(p.getFreeArmyId());
		army.setName(armyname);
		String fluff = "created for army " + armyname;
		for (Entity en : loadedUnits) {

			SUnit cm = new SUnit();
			cm.setEntity(en);
			MechSummary ms = MechSummaryCache.getInstance().getMech(en.getShortNameRaw());
            if ( ms == null ) {
                MechSummary[] units = MechSummaryCache.getInstance().getAllMechs();
                //System.err.println("unit: "+en.getShortNameRaw());
                for ( MechSummary unit :  units) {
                  //  System.err.println("Source file: "+unit.getSourceFile().getName());
                   // System.err.println("Model: "+unit.getModel());
                    //System.err.println("Chassis: "+unit.getChassis());
                    if ( unit.getModel().trim().equalsIgnoreCase(en.getModel().trim())
                    		&& unit.getChassis().trim().equalsIgnoreCase(en.getChassis().trim() )
                    		) {
            			cm.setUnitFilename(unit.getEntryName());
                        break;
                    }
                }
                
            }
            else {
            	System.err.println("Entry: "+ms.getEntryName()+" source: "+ms.getSourceFile().getName());
            	cm.setUnitFilename(ms.getEntryName());
            }
            
			cm.setId(CampaignMain.cm.getAndUpdateCurrentUnitID());
			cm.setProducer(fluff);
			cm.setWeightclass(99);//let the SUnit code handle the weightclass

			SPilot pilot = null;
			pilot = new SPilot(en.getCrew().getName(), en.getCrew()
					.getGunnery(), en.getCrew().getPiloting());

			if (pilot.getName().equalsIgnoreCase("Unnamed")
					|| pilot.getName().equalsIgnoreCase("vacant"))
				pilot
						.setName(SPilot.getRandomPilotName(CampaignMain.cm
								.getR()));

			pilot.setCurrentFaction("Common");
			StringTokenizer skillList = new StringTokenizer(en.getCrew()
					.getAdvantageList(","), ",");

			while (skillList.hasMoreTokens()) {
				String skill = skillList.nextToken();
				SPilotSkill pSkill = null;
				if (skill.equalsIgnoreCase("random"))
					pSkill = CampaignMain.cm
							.getRandomSkill(pilot, cm.getType());
				else
					pSkill = CampaignMain.cm.getPilotSkill(skill);

				pilot.getSkills().add(pSkill);
			}
			cm.setPilot(pilot);

			p.addUnit(cm, true);
			army.addUnit(cm);
		}

		p.getArmies().add(army);
		army.getBV();
		army.setOpForceSize(army.getAmountOfUnits());
		
		CampaignMain.cm.toUser("PL|SAD|" + army.toString(true, "%"), p.getName(),
				false);
		CampaignMain.cm.toUser("army created: " + armyname, p.getName(), true);
		CampaignMain.cm.doSendModMail("NOTE", Username
				+ " has created an army from file " + filename);

	}
}
