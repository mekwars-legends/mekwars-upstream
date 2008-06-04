/*
 * MekWars - Copyright (C) 2004 
 * 
 * Derived from MegaMekNET (http://www.sourceforge.net/projects/megamek)
 * Original author Helge Richter (McWizard)
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

package client.cmd;

import java.util.StringTokenizer;

import common.CampaignData;
import common.campaign.pilot.Pilot;
import common.util.TokenReader;
import common.util.UnitUtils;

import client.MWClient;
import client.campaign.CPlayer;
import client.campaign.CUnit;
import client.gui.dialog.AdvancedRepairDialog;

/**
 * @author Imi (immanuel.scholz@gmx.de)
 */

public class PL extends Command {

    /**
     * @param client
     */
    public PL(MWClient mwclient) {
        super(mwclient);
    }

    /**
     * @see client.cmd.Command#execute(java.lang.String)
     */
    @Override
    public void execute(String input) {
        StringTokenizer st = decode(input);

        String cmd = TokenReader.readString(st);
        CPlayer player = mwclient.getPlayer();

        if (!st.hasMoreTokens())
            return;

        if (cmd.equals("FCU")) {
            mwclient.updateClient();
            return;
        }

        if (cmd.equals("RA")) // Remove army PL|RA|
            player.removeArmy(TokenReader.readInt(st));
        else if (cmd.equals("LA"))
            player.playerLockArmy(TokenReader.readInt(st));
        else if (cmd.equals("ULA"))
            player.playerUnlockArmy(TokenReader.readInt(st));
        else if (cmd.equals("TAD"))
            player.toggleArmyDisabled(TokenReader.readInt(st));
        else if (cmd.equals("SAD")) // New army. PL|SAD|army data
            player.setArmyData(TokenReader.readString(st));
        else if (cmd.equals("SABV")) // New army bv PL|army ID|new BV
            player.setArmyBV(TokenReader.readString(st));
        else if (cmd.equals("AAU")) // add unit to army PL|AAU|armyid$unitid
            player.addArmyUnit(TokenReader.readString(st));
        else if (cmd.equals("RAU")) // remove unit from army PL|RAU|armyid$unitid
            player.removeArmyUnit(TokenReader.readString(st));
        else if (cmd.equals("HD")) // hangar data feed PL|HD|<hangar string>. also used to add single new units.
            player.setHangarData(TokenReader.readString(st));
        else if (cmd.equals("RU")) // Remove hangar unit PL|RHU|id
            player.removeUnit(TokenReader.readInt(st));
        else if (cmd.equals("SE")) // set experience PL|SE|<amount>
            player.setExp(TokenReader.readInt(st));
        else if (cmd.equals("SM")) // set money PL|SM|<amount>
            player.setMoney(TokenReader.readInt(st));
        else if (cmd.equals("SB")) // set bays PL|SB|<amount>
            player.setBays(TokenReader.readInt(st));
        else if (cmd.equals("SF")) // set free bays PL|SF|<amount>
            player.setFreeBays(TokenReader.readInt(st));
        else if (cmd.equals("SI")) // set influence PL|SI|<amount>
            player.setInfluence(TokenReader.readInt(st));
        else if (cmd.equals("SR")) // set rating PL|SR|<amount>
            player.setRating(TokenReader.readDouble(st));
        else if (cmd.equals("SRP")) // set reward points PL|SRP|<amount>
            player.setRewardPoints(TokenReader.readInt(st));
        else if (cmd.equals("SH")) // set faction PL|SH|<faction string>
            player.setHouse(TokenReader.readString(st));
        else if (cmd.equals("ST")) // set Technicians PL|ST|<number of techs>
            player.setTechnicians(TokenReader.readInt(st));
        else if (cmd.equals("SSN")) // set Subfaction Name PL|SSN|Name of sub Faction;
            player.setSubFaction(TokenReader.readString(st));
        else if (cmd.equals("AAA"))// incoming autoarmy
            player.setAutoArmy(st);// give it the whole tokenizer
        else if (cmd.equals("AAM"))// incoming mines
            player.setMines(st);// give it the whole tokenizer
        else if (cmd.equals("GEA"))// incoming GunEmplacementArmy
            player.setAutoGunEmplacements(st);// give it the whole tokenizer
        else if (cmd.equals("SUS"))// set unit status (maintained, unmaintained, for sale, etc)
            player.setUnitStatus(TokenReader.readString(st));
        else if (cmd.equals("RNA"))
            player.setArmyName(TokenReader.readString(st));
        else if (cmd.equals("SAB")) // set Army bounds PL|SAB|Armyid#lowerlimit#upperlimit
            player.setArmyLimit(TokenReader.readString(st));
        else if (cmd.equals("SAL")) // set if the army is locked or not PL|SAL|armyid#true/false
            player.setArmyLock(TokenReader.readString(st));
        else if (cmd.equals("UU")) // update unit PL|UU|unitdata
            player.updateUnitData(st);
        else if (cmd.equals("UUMG")) // update unit PL|UUMG|id|location|selection
            player.updateUnitMachineGuns(st);
        else if (cmd.equals("BMW")) { // play a sound someone won the bm.
            if (mwclient.getConfig().isParam("ENABLEBMSOUND"))
                mwclient.doPlaySound(mwclient.getConfig().getParam("SOUNDONBMWIN"));
        } else if (cmd.equals("PPQ")) // Personal Pilot Queue update
            player.getPersonalPilotQueue().fromString(TokenReader.readString(st));
        else if (cmd.equals("PEU")) // Player Exclude Update
            player.setPlayerExcludes(TokenReader.readString(st), "$");
        else if (cmd.equals("AEU")) // Admin Exclude Update
            player.setAdminExcludes(TokenReader.readString(st), "$");
        else if (cmd.equals("RPU"))// Re-Position Unit
            player.repositionArmyUnit(TokenReader.readString(st));
        else if (cmd.equals("UOE"))// update ops eligibility
            player.updateOperations(TokenReader.readString(st));
        else if (cmd.equals("UTT"))// update ops eligibility
            player.updateTotalTechs(TokenReader.readString(st));
        else if (cmd.equals("UAT"))// update ops eligibility
            player.updateAvailableTechs(TokenReader.readString(st));
        else if (cmd.equals("GBB"))// Go bye bye
            mwclient.getConnector().closeConnection();
        else if (cmd.equals("UB"))// Using Bots
            mwclient.setUsingBots(TokenReader.readBoolean(st));
        else if (cmd.equals("BOST"))// Bots On the Same Team
            mwclient.setBotsOnSameTeam(TokenReader.readBoolean(st));
        else if (cmd.equals("SHFF"))// Players house fighting for
            player.setHouseFightingFor(TokenReader.readString(st));
        else if (cmd.equals("SUL")) {// Players Unit Logo
            player.setLogo(TokenReader.readString(st));
            mwclient.getMainFrame().getMainPanel().getPlayerPanel().refresh();
        } else if (cmd.equals("AP2PPQ"))// adding a single pilot back to the players PPQ
            player.getPersonalPilotQueue().addPilot(st);
        else if (cmd.equals("RPPPQ"))// Remove a pilot from the players pilot queue
            player.getPersonalPilotQueue().removePilot(st);
        else if (cmd.equals("RSOD"))// Retrieve Short Op Data
            mwclient.retrieveOpData("short", TokenReader.readString(st));
        else if (cmd.equals("UCP"))// Update/Set a clients param
            mwclient.updateParam(st);
        else if (cmd.equals("SOFL"))// Server Op Flags
            mwclient.setServerOpFlags(st);
        else if (cmd.equals("SAOFS"))// Set Army Op Force Size
            player.setArmyOpForceSize(TokenReader.readString(st));
        else if (cmd.equals("FC"))// Set Faction Configs
            player.setFactionConfigs(TokenReader.readString(st));
        else if (cmd.equals("UPBM"))// Set Faction Configs
            mwclient.updatePartsBlackMarket(TokenReader.readString(st));
        else if (cmd.equals("UPPC"))// Set Faction Configs
            mwclient.updatePlayerPartsCache(TokenReader.readString(st));
        else if (cmd.equals("RPPC"))
            mwclient.getPlayer().getPartsCache().fromString(st);
        else if (cmd.equals("STN"))
            mwclient.getPlayer().setTeamNumber(TokenReader.readInt(st));
        else if (cmd.equals("VUI")) {
            StringTokenizer data = new StringTokenizer(TokenReader.readString(st), "#");
            String filename = TokenReader.readString(data);
            int BV = TokenReader.readInt(data);
            int gunnery = TokenReader.readInt(data);
            int piloting = TokenReader.readInt(data);
            String damage = "";

            if (data.hasMoreElements())
                damage = TokenReader.readString(data);

            mwclient.getMainFrame().getMainPanel().getHSPanel().showInfoWindow(filename, BV, gunnery, piloting, damage);
        } else if (cmd.equals("VURD")) {
            StringTokenizer data = new StringTokenizer(TokenReader.readString(st), "#");
            String filename = TokenReader.readString(data);
            String damage = TokenReader.readString(data);
            CUnit unit = new CUnit(mwclient);

            unit.setUnitFilename(filename);
            unit.createEntity();
            unit.setPilot(new Pilot("Jeeves", 4, 5));
            UnitUtils.applyBattleDamage(unit.getEntity(), damage);
            new AdvancedRepairDialog(mwclient, unit, unit.getEntity(), false);
        } else if (cmd.equals("CPPC")) {
            mwclient.getPlayer().getPartsCache().clear();
        } else if (cmd.equals("UDAO")) {
            mwclient.updateOpData(true);
            if (!mwclient.isDedicated())
                mwclient.getMainFrame().updateAttackMenu();
        } else if (cmd.equals("RMF")) {
            mwclient.retrieveMul(TokenReader.readString(st));
        } else if (cmd.equals("SMFD")) {
            mwclient.getMainFrame().showMulFileList(TokenReader.readString(st));
        } else if (cmd.equals("CAFM")) {
            mwclient.getMainFrame().createArmyFromMul(TokenReader.readString(st));
        } else if (cmd.equals("USU")) {
            // Update Supported Units
            while (st.hasMoreTokens()) {
                boolean addSupport = TokenReader.readBoolean(st);
                String unitName = TokenReader.readString(st);
                if (unitName != null) {
                    if (addSupport) {
                        player.getMyHouse().addUnitSupported(unitName);
                    } else {
                        player.getMyHouse().removeUnitSupported(unitName);
                    }
                }
            }
            CampaignData.mwlog.infoLog(player.getMyHouse().getSupportedUnits().toString());
        } else if (cmd.equals("CSU")) {
            // clear supported units
            CampaignData.mwlog.infoLog("Clearing Supported Units");
            player.getMyHouse().supportedUnits.clear();
            player.getMyHouse().setNonFactionUnitsCostMore(Boolean.parseBoolean(mwclient.getserverConfigs("UseNonFactionUnitsIncreasedTechs")));
        } else if (cmd.equals("SMA")) {
            mwclient.getPlayer().setMULCreatedArmy(st);
        } else if (cmd.equals("ANH")) {
            mwclient.createNewHouse(st);
        } else if (cmd.equals("RPF")) {
            int id = TokenReader.readInt(st);
            mwclient.getData().removeHouse(id);
        } else if (cmd.equals("UDT")) {
            mwclient.addToChat(TokenReader.readString(st), mwclient.getConfig().getIntParam("USERDEFINDMESSAGETAB"));
        } else if (cmd.equals("CCC")) {
            mwclient.getCampaign().setComponentConverter(st.nextToken());
        } else if (cmd.equals("SUD")) {
            try {
                StringBuilder userData = new StringBuilder(MWClient.CAMPAIGN_PREFIX + "c sendclientdata#");

                String[] userDataSet = { "user.name", "user.language", "user.country", "user.timezone", "os.name", "os.arch", "os.version", "java.version" };

                for (int pos = 0; pos < userDataSet.length; pos++) {
                    String property = System.getProperty(userDataSet[pos], "Unknown");
                    userData.append(property);
                    userData.append("#");
                }
                mwclient.sendChat(userData.toString());
            } catch (Exception ex) {
            }
        } else
            return;

        mwclient.refreshGUI(MWClient.REFRESH_HQPANEL);
        mwclient.refreshGUI(MWClient.REFRESH_PLAYERPANEL);
        mwclient.refreshGUI(MWClient.REFRESH_BMPANEL);
    }
}
