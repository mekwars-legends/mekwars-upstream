/*
 * MekWars - Copyright (C) 2007 
 * 
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
 * @author jtighe
 * This command is used to set Black Market Settings
 * for max/min cost and production.
 * 
 */
package server.campaign.commands.admin;

import java.util.Enumeration;
import java.util.StringTokenizer;

import megamek.common.AmmoType;
import megamek.common.EquipmentType;

import common.Equipment;

import server.campaign.commands.Command;
import server.campaign.CampaignMain;
import server.MWChatServer.auth.IAuthenticator;

public class AutoFillBlackMarketSettingCommand implements Command {

    int accessLevel = IAuthenticator.ADMIN;
    String syntax = "Min Cost Modifer#Max Cost Modifer#Min Production#Max Production";

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

        // get config var and new setting
        double minCost = 1.0;
        double maxCost = 1.0;
        double maxCostMod = 1.0;
        double minCostMod = 1.0;
        double baseCost = 1.0;

        String minProduction = "";
        String maxProduction = "";

        minCostMod = Double.parseDouble(command.nextToken());
        maxCostMod = Double.parseDouble(command.nextToken());
        minProduction = command.nextToken();
        maxProduction = command.nextToken();

        Enumeration<EquipmentType> list = EquipmentType.getAllTypes();
        double crits = 1;

        while (list.hasMoreElements()) {

            EquipmentType eq = list.nextElement();


            if (eq instanceof AmmoType) {
                crits = ((AmmoType) eq).getRackSize();
            } else {
                try {
                    crits = eq.getCriticals(null);
                } catch (Exception ex) {
                    continue;
                }
            }

            crits = Math.max(crits, 1);
            baseCost = eq.getCost() / crits;

            minCost = baseCost * minCostMod;
            maxCost = baseCost * maxCostMod;

            Equipment bme = CampaignMain.cm.getBlackMarketEquipmentTable().get(eq.getInternalName());

            if (bme == null) {
                bme = new Equipment();
                bme.setEquipmentInternalName(eq.getInternalName());
            }

            bme.setMinCost(minCost);
            bme.setMaxCost(maxCost);
            bme.setMinProduction(Integer.parseInt(minProduction));
            bme.setMaxProduction(Integer.parseInt(maxProduction));

            CampaignMain.cm.getBlackMarketEquipmentTable().put(eq.getInternalName(), bme);
        }

        CampaignMain.cm.toUser("AM:Done setting equipment costs for the black market.", Username);
    }// end process
}