/*
 * MekWars - Copyright (C) 2007
 * 
 * Original author - jtighe (torren@users.sourceforge.net)
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 */

package server.campaign.commands.leader;

import java.util.StringTokenizer;
import java.util.Vector;

import common.UnitFactory;

import server.MWChatServer.auth.IAuthenticator;
import server.campaign.BuildTable;
import server.campaign.SHouse;
import server.campaign.SPlanet;
import server.campaign.SPlayer;
import server.campaign.CampaignMain;
import server.campaign.SUnit;
import server.campaign.SUnitFactory;
import server.campaign.commands.Command;

public class PurchaseFactoryCommand implements Command {

    // Starting out at mod level this can be lowered as needed
    int accessLevel = IAuthenticator.MODERATOR;

    public int getExecutionLevel() {
        return accessLevel;
    }

    public void setExecutionLevel(int i) {
        accessLevel = i;
    }

    String syntax = "Factory Name#Type#Weight#Planet";

    public String getSyntax() {
        return syntax;
    }

    public void process(StringTokenizer command, String Username) {

        if (accessLevel != 0) {
            int userLevel = CampaignMain.cm.getServer().getUserLevel(Username);
            if (userLevel < getExecutionLevel()) {
                CampaignMain.cm.toUser("AM:Insufficient access level for command. Level: " + userLevel + ". Required: " + accessLevel + ".", Username, true);
                return;
            }
        }

        SPlayer player = CampaignMain.cm.getPlayer(Username);
        SPlanet planet;
        SHouse house;
        double cost = 0.0;
        double flu = 0.0;
        int type = SUnit.MEK;
        int weight = SUnit.LIGHT;
        String name = "";
        int buildType = UnitFactory.BUILDMEK;

        name = command.nextToken();
        type = Integer.parseInt(command.nextToken());
        weight = Integer.parseInt(command.nextToken());
        planet = CampaignMain.cm.getPlanetFromPartialString(command.nextToken(), null);
        house = player.getMyHouse();

        if (planet == null) {
            CampaignMain.cm.toUser("Unable to find planet.", Username);
            return;
        }

        if (house.isNewbieHouse()) {
            CampaignMain.cm.toUser(CampaignMain.cm.getConfig("NewbieHouseName") + " cannot purchase new factories!", Username);
            return;
        }

        if (!planet.isOwner(house.getId())) {
            CampaignMain.cm.toUser("You do not own " + planet.getName(), Username);
            return;
        }

        switch (type) {
        case SUnit.MEK:
            buildType = UnitFactory.BUILDMEK;
            break;
        case SUnit.INFANTRY:
            buildType = UnitFactory.BUILDINFANTRY;
            break;
        case SUnit.VEHICLE:
            buildType = UnitFactory.BUILDVEHICLES;
            break;
        case SUnit.BATTLEARMOR:
            buildType = UnitFactory.BUILDBATTLEARMOR;
            break;
        case SUnit.PROTOMEK:
            buildType = UnitFactory.BUILDPROTOMECHS;
            break;
        }

        cost = CampaignMain.cm.getDoubleConfig("NewFactoryBaseCost");
        cost *= CampaignMain.cm.getDoubleConfig("NewFactoryCostModifier" + SUnit.getWeightClassDesc(weight));
        cost *= CampaignMain.cm.getDoubleConfig("NewFactoryCostModifier" + SUnit.getTypeClassDesc(type));

        cost = Math.round(cost);

        flu = CampaignMain.cm.getDoubleConfig("NewFactoryBaseFlu");
        flu *= CampaignMain.cm.getDoubleConfig("NewFactoryFluModifier" + SUnit.getWeightClassDesc(weight));
        flu *= CampaignMain.cm.getDoubleConfig("NewFactoryFlutModifier" + SUnit.getTypeClassDesc(type));

        flu = Math.round(flu);

        if (player.getMoney() < cost) {
            CampaignMain.cm.toUser("You need " + CampaignMain.cm.moneyOrFluMessage(true, true, (int) cost) + " to purchase a factory.", Username);
            return;
        }

        if (player.getInfluence() < flu) {
            CampaignMain.cm.toUser("You need " + CampaignMain.cm.moneyOrFluMessage(false, true, (int) flu) + " to purchase a factory.", Username);
            return;
        }

        player.addMoney((int)-cost);
        player.addInfluence((int)-flu);
        
        SUnitFactory fac = new SUnitFactory(name, planet, SUnit.getWeightClassDesc(weight), house.getName(), 0, CampaignMain.cm.getIntegerConfig("BaseFactoryRefreshRate"), buildType, BuildTable.STANDARD, 0);
        Vector<UnitFactory> uf = planet.getUnitFactories();
        uf.add(fac);
        fac.setPlanet(planet);
        house.removePlanet(planet);
        house.addPlanet(planet);
        
        house.updated();
        planet.updated();
        if (CampaignMain.cm.isUsingMySQL()) {
            fac.setID(CampaignMain.cm.MySQL.getFactoryIdByNameAndPlanet(name, planet.getName()));
            fac.toDB();
        }

        CampaignMain.cm.toUser("You have purchased factory "+name+" on planet "+planet.getName()+".", Username, true);
        CampaignMain.cm.doSendToAllOnlinePlayers(house, Username+" has purchased factory "+name+" on planet "+planet.getName()+".", true);
    }
}// end RequestSubFactionPromotionCommand class
