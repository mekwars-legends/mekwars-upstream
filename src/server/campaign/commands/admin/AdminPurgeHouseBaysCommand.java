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

import java.util.StringTokenizer;
import java.util.Vector;

import server.MWChatServer.auth.IAuthenticator;
import server.campaign.CampaignMain;
import server.campaign.SHouse;
import server.campaign.SUnit;
import server.campaign.commands.Command;

import common.Unit;

public class AdminPurgeHouseBaysCommand implements Command {

    int accessLevel = IAuthenticator.ADMIN;
    String syntax = "Faction Name#[ALL]unittype#[ALL]unitsize";

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
            CampaignMain.cm.toUser("AM:Insufficient access level for command. Level: " + userLevel + ". Required: " + accessLevel + ".", Username, true);
            return;
        }

        String faction = "";
        String strType = "";
        String strClass = "";
        int unitType = Unit.MEK;
        int unitClass = Unit.LIGHT;

        try {
            faction = command.nextToken();
            strType = command.nextToken();
        } catch (Exception ex) {
            CampaignMain.cm.toUser("Invalid syntax. Try: AdminPurgeHouseBays#faction#[ALL]unittype#[ALL]unitsize", Username, true);
            return;
        }

        SHouse h = CampaignMain.cm.getHouseFromPartialString(faction, Username);

        if (h == null) {
            return;
        }

        // Delete them from the database
        if (CampaignMain.cm.isUsingMySQL()) {
            if (strType.equalsIgnoreCase("ALL")) {
                for (int type = Unit.MEK; type < Unit.MAXBUILD; type++) {
                    for (int i = 0; i < 4; i++) {
                        Vector<SUnit> tmpVec = h.getHangar(type).elementAt(i);
                        tmpVec.trimToSize();
                        for (SUnit currU : tmpVec) {
                            CampaignMain.cm.MySQL.deleteUnit(currU.getDBId());
                        }
                    }
                }
            } else {
                strClass = command.nextToken();
                unitType = Integer.parseInt(strType);
                if (strClass.equalsIgnoreCase("ALL")) {
                    for (int i = 0; i < 4; i++) {
                        Vector<SUnit> tmpVec = h.getHangar(unitType).elementAt(i);
                        tmpVec.trimToSize();
                        for (SUnit currU : tmpVec) {
                            CampaignMain.cm.MySQL.deleteUnit(currU.getDBId());
                        }
                    }
                } else {
                    unitClass = Integer.parseInt(strClass);
                    Vector<SUnit> tmpVec = h.getHangar(unitType).elementAt(unitClass);
                    tmpVec.trimToSize();
                    for (SUnit currU : tmpVec) {
                        CampaignMain.cm.MySQL.deleteUnit(currU.getDBId());
                    }
                }
            }
        }

        try {
            if (strType.equalsIgnoreCase("ALL")) {
                for (Vector<Vector<SUnit>> hangers : h.getHangar().values()) {
                    for (int size = Unit.LIGHT; size <= Unit.ASSAULT; size++) {
                        hangers.elementAt(size).clear();
                    }
                }
            }// else select a unit type
            else {
                if (!CampaignMain.cm.isUsingMySQL()) {
                    strClass = command.nextToken();
                }
                unitType = Integer.parseInt(strType);
                Vector<Vector<SUnit>> hanger = h.getHangar(unitType);

                if (strClass.equalsIgnoreCase("ALL")) {
                    for (int size = Unit.LIGHT; size <= Unit.ASSAULT; size++) {
                        hanger.elementAt(size).clear();
                    }
                }// else one unit size
                else {
                    if (!CampaignMain.cm.isUsingMySQL()) {
                        unitClass = Integer.parseInt(strClass);
                    }
                    hanger.elementAt(unitClass).clear();
                }
            }
        } catch (Exception ex) {
            CampaignMain.cm.toUser("Invalid syntax. Try: AdminPurgeHouseBays#faction#[ALL]unittype#[ALL]unitsize", Username, true);
            return;
        }

        h.updated();
        CampaignMain.cm.doSendModMail("NOTE", Username + " has purged bays for " + h.getName());
    }
}// end AdminPurgeHouseBaysCommand