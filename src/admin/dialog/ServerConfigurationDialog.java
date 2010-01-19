/*
 * MekWars - Copyright (C) 2004
 * 
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 */

/**
 * @author jtighe
 * 
 *         Server Configuration Page. All new Server Options need to be added To this page as well.
 */

package admin.dialog;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import client.MWClient;

import common.CampaignData;
import common.Unit;
import common.util.SpringLayoutHelper;

public final class ServerConfigurationDialog implements ActionListener {

    private final static String okayCommand = "okay";
    private final static String cancelCommand = "cancel";
    private final static String windowName = "MekWars Server Configuration";

    private JTextField baseTextField = new JTextField(5);
    private JCheckBox BaseCheckBox = new JCheckBox();
    private JRadioButton baseRadioButton = new JRadioButton();

    private final JButton okayButton = new JButton("OK");
    private final JButton cancelButton = new JButton("Cancel");

    private JDialog dialog;
    private JOptionPane pane;

    JTabbedPane ConfigPane = new JTabbedPane(SwingConstants.TOP);

    MWClient mwclient = null;

    /**
     * @author jtighe Opens the server config page in the client.
     * @param client
     */

    /**
     * @author Torren (Jason Tighe) 12/29/2005 I've completely redone how the Server config dialog works There are 2 basic fields now baseTextField which is a
     *         JTextField and baseCheckBox which is a JCheckBox. When you add a new server config add the labels to the tab then use the base fields to add the
     *         ver. make sure to set the base field's name method this is used to populate and save. ex: BaseTextField.setName("DefaultServerOptionsVariable");
     *         Two recursive methods populate and save the data to the server findAndPopulateTextAndCheckBoxes(JPanel) findAndSaveConfigs(JPanel) This change to
     *         the code removes the tediousness of having to add a new var to 3 locations when it is use. Now only 1 location needs to added and that is the
     *         vars placement on the tab in the UI.
     */
    public ServerConfigurationDialog(MWClient mwclient) {

        this.mwclient = mwclient;
        // TAB PANELS (these are added to the root pane as tabs)
        JPanel pathsPanel = new JPanel();// file paths
        JPanel influencePanel = new JPanel();// influence settings
        JPanel repodPanel = new JPanel();
        JPanel technicianPanel = new JPanel();
        JPanel unitPanel = new JPanel();
        JPanel unit2Panel = new JPanel();
        JPanel factionPanel = new JPanel();
        JPanel directSellPanel = new JPanel();
        JPanel newbieHousePanel = new JPanel();
        JPanel votingPanel = new JPanel();
        JPanel productionPanel = new JPanel();// was factoryOptions
        JPanel rewardPanel = new JPanel();
        JPanel miscOptionsPanel = new JPanel();// things which can't be easily
        // categorized
        JPanel artilleryPanel = new JPanel();
        JPanel combatPanel = new JPanel();// mm options, etc
        JPanel pilotsPanel = new JPanel();// allows SO's set up pilot options and personal pilot queue options
        JPanel pilotSkillsModPanel = new JPanel();// Allows the SO's to set the mods for each skill type that affects the MM game.
        JPanel pilotSkillsPanel = new JPanel();// allows SO's to select what pilot skills they want for non-Mek unit types.
        JPanel mekPilotSkillsPanel = new JPanel();// allows SO's to select what pilot skills they want for Meks

        JPanel noPlayPanel = new JPanel();
        JPanel blackMarketPanel = new JPanel();
        JPanel defectionPanel = new JPanel();// control defection access,
        // losses therefrom, etc.
        JPanel battleValuePanel = new JPanel();// mekwars BV adjustments
        JPanel disconenctionPanel = new JPanel();
        JPanel advancedRepairPanel = new JPanel();// Advanced Repair
        JPanel lossCompensationPanel = new JPanel();// battle loss compensation
        JPanel dbPanel = new JPanel(); // Database configuration
        JPanel singlePlayerFactionPanel = new JPanel(); // Single Player Faction Configs
        JPanel technologyResearchPanel = new JPanel(); // Technology Reseach Panel
        JPanel unitResearchPanel = new JPanel(); // Research Unit Panel
        JPanel factoryPurchasePanel = new JPanel(); // Allow players to purchase new factories.
        JPanel unitLimitsPanel = new JPanel(); // Set limits on units in a player's hangar
        /*
         * PGMH PANEL CONSTRUCTION Set up the PGMH panel, which indicates where HTML output (EXPRanking, etc.) is dumped and where certain core server files are located.
         */
        // give the path panel a box layout. its going to be smaller than some,
        // so
        // we dont need flow-nested boxes
        JPanel pathsBox = new JPanel();
        pathsBox.setLayout(new BoxLayout(pathsBox, BoxLayout.Y_AXIS));

        // and a sub panel to put a spring layout into.
        JPanel pathsSubPanel = new JPanel(new SpringLayout());

        baseTextField = new JTextField(5);

        pathsSubPanel.add(new JLabel("ELO Ranking Path:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Path to Ranking.htm");
        baseTextField.setName("RankingPath");
        pathsSubPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        pathsSubPanel.add(new JLabel("EXP Ranking Path:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Path to EXPRanking.htm");
        baseTextField.setName("EXPRankingPath");
        pathsSubPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        pathsSubPanel.add(new JLabel("News Path:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Path to News.rdf (News Feed)");
        baseTextField.setName("NewsPath");
        pathsSubPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        pathsSubPanel.add(new JLabel("House Rank Path:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Path to House Ranking file");
        baseTextField.setName("HouseRankPath");
        pathsSubPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        pathsSubPanel.add(new JLabel("XML Planet Path:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Path to up-to-date DynPlanets.xml for IS-Map-Generators");
        baseTextField.setName("XMLPlanetPath");
        pathsSubPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        pathsSubPanel.add(new JLabel("Mechstat Path:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Path to Mechstats.htm");
        baseTextField.setName("MechstatPath");
        pathsSubPanel.add(baseTextField);

        // do the spring layout.
        SpringLayoutHelper.setupSpringGrid(pathsSubPanel, 2);

        // add to the main panel.
        pathsBox.add(pathsSubPanel);

        // thats all the path naming options. put the HTML CBox here, as it
        // was in the old UI, for now. Should be moved eventually.
        BaseCheckBox = new JCheckBox("Enable HTML Output");

        BaseCheckBox.setToolTipText("Uncheck to disable html output [ranking, etc.]");
        BaseCheckBox.setName("HTMLOUTPUT");
        pathsBox.add(BaseCheckBox);
        pathsPanel.add(pathsBox);

        /*
         * INFLUENCE PANEL CONSTRUCTION Influence panel, where admins set influence gain controls (bv limits, etc) and action costs (bm bid, attack, and so on). Use nested layouts. A Box containing a Flow, which in turn contains two Springs
         */
        JPanel influenceBoxPanel = new JPanel();
        JPanel influenceFlowPanel = new JPanel();
        JPanel influenceSpring1 = new JPanel(new SpringLayout());// 7 items
        JPanel influenceSpring2 = new JPanel(new SpringLayout());// 7 items
        influenceBoxPanel.setLayout(new BoxLayout(influenceBoxPanel, BoxLayout.Y_AXIS));
        influenceBoxPanel.add(influenceFlowPanel);
        influenceFlowPanel.add(influenceSpring1);
        influenceFlowPanel.add(influenceSpring2);

        // load spring1 first
        baseTextField = new JTextField(5);
        influenceSpring1.add(new JLabel("Max Player " + mwclient.moneyOrFluMessage(false, false, -1) + ":", SwingConstants.TRAILING));
        baseTextField.setToolTipText(mwclient.moneyOrFluMessage(false, false, -1) + " ceiling");
        baseTextField.setName("InfluenceCeiling");
        influenceSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
        influenceSpring1.add(new JLabel("Min Time for " + mwclient.moneyOrFluMessage(false, true, -1) + ":", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Minimum active time to receive flu @ check.");
        baseTextField.setName("InfluenceTimeMin");
        influenceSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
        influenceSpring1.add(new JLabel("Floor Penalty:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount removed from TotalArmies when an Army abutts the MinBV");
        baseTextField.setName("FloorPenalty");
        influenceSpring1.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(influenceSpring1, 2);

        // then set up spring2
        baseTextField = new JTextField(5);
        influenceSpring2.add(new JLabel("Ceiling Penalty:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount removed from TotalArmies when an Army abutts the MaxBV");
        baseTextField.setName("CeilingPenalty");
        influenceSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        influenceSpring2.add(new JLabel("Overlap Penalty:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount removed from TotalArmies when 2 armies overlap");
        baseTextField.setName("OverlapPenalty");
        influenceSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        influenceSpring2.add(new JLabel(mwclient.moneyOrFluMessage(false, true, -1) + " Per Army:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base amount of " + mwclient.moneyOrFluMessage(false, false, -1) + " given for each army");
        baseTextField.setName("BaseInfluence");
        influenceSpring2.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(influenceSpring2, 2);

        // springs are it for now. if CBoxes come later, stick them in the box
        // =)
        influencePanel.add(influenceBoxPanel);

        /*
         * REPOD PANEL CONSTRUCTION Repod contols. Costs, factory usage, table options, etc. Use nested layouts. A Box containing a Flow and 3 Springs.
         */
        JPanel repodBoxPanel = new JPanel();
        JPanel repodCBoxGridPanel = new JPanel(new GridLayout(2, 3));
        JPanel repodSpringGrid = new JPanel(new GridLayout(2, 2));
        JPanel refreshSpring = new JPanel(new SpringLayout());
        JPanel cbillSpring = new JPanel(new SpringLayout());
        JPanel componentSpring = new JPanel(new SpringLayout());
        JPanel fluSpring = new JPanel(new SpringLayout());
        repodBoxPanel.setLayout(new BoxLayout(repodBoxPanel, BoxLayout.Y_AXIS));
        repodSpringGrid.add(cbillSpring);
        repodSpringGrid.add(fluSpring);
        repodSpringGrid.add(componentSpring);
        repodSpringGrid.add(refreshSpring);
        repodBoxPanel.add(repodCBoxGridPanel);
        repodBoxPanel.add(repodSpringGrid);

        // set up the flow panel
        BaseCheckBox = new JCheckBox("Cost " + mwclient.moneyOrFluMessage(true, true, -1));

        BaseCheckBox.setToolTipText("Check to enable " + mwclient.moneyOrFluMessage(true, true, -1) + " charges for repods");
        BaseCheckBox.setName("DoesRepodCost");
        repodCBoxGridPanel.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Use Factory");

        BaseCheckBox.setToolTipText("Check to have repodding use a factory");
        BaseCheckBox.setName("RepodUsesFactory");
        repodCBoxGridPanel.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Uses Comps");

        BaseCheckBox.setToolTipText("Check to have repodding consume components");
        BaseCheckBox.setName("RepodUsesComp");
        repodCBoxGridPanel.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Common Table");

        BaseCheckBox.setToolTipText("Check to allow all factions to repod from common table.");
        BaseCheckBox.setName("UseCommonTableForRepod");
        repodCBoxGridPanel.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Allow Random");

        BaseCheckBox.setToolTipText("Check to allow random repods.");
        BaseCheckBox.setName("RandomRepodAllowed");
        repodCBoxGridPanel.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Random Only");

        BaseCheckBox.setToolTipText("If checked, only random repods are allowed.");
        BaseCheckBox.setName("RandomRepodOnly");
        repodCBoxGridPanel.add(BaseCheckBox);

        // and then the various springs. MU first.
        baseTextField = new JTextField(5);
        cbillSpring.add(new JLabel("Light " + mwclient.moneyOrFluMessage(true, true, -1) + ":", SwingConstants.TRAILING));
        baseTextField.setToolTipText(mwclient.moneyOrFluMessage(true, true, -1) + " required to repod a light unit");
        baseTextField.setName("RepodCostLight");
        cbillSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        cbillSpring.add(new JLabel("Medium " + mwclient.moneyOrFluMessage(true, true, -1) + ":", SwingConstants.TRAILING));
        baseTextField.setToolTipText(mwclient.moneyOrFluMessage(true, true, -1) + " required to repod a medium unit");
        baseTextField.setName("RepodCostMedium");
        cbillSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        cbillSpring.add(new JLabel("Heavy " + mwclient.moneyOrFluMessage(true, true, -1) + ":", SwingConstants.TRAILING));
        baseTextField.setToolTipText(mwclient.moneyOrFluMessage(true, true, -1) + " required to repod a heavy unit");
        baseTextField.setName("RepodCostHeavy");
        cbillSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        cbillSpring.add(new JLabel("Assault " + mwclient.moneyOrFluMessage(true, true, -1) + ":", SwingConstants.TRAILING));
        baseTextField.setToolTipText(mwclient.moneyOrFluMessage(true, true, -1) + " required to repod an assault unit");
        baseTextField.setName("RepodCostAssault");
        cbillSpring.add(baseTextField);

        // now the flu spring
        baseTextField = new JTextField(5);
        fluSpring.add(new JLabel("Light " + mwclient.moneyOrFluMessage(false, true, -1) + ":", SwingConstants.TRAILING));
        baseTextField.setToolTipText(mwclient.moneyOrFluMessage(false, true, -1) + " required to repod a light unit");
        baseTextField.setName("RepodFluLight");
        fluSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        fluSpring.add(new JLabel("Medium " + mwclient.moneyOrFluMessage(false, true, -1) + ":", SwingConstants.TRAILING));
        baseTextField.setToolTipText(mwclient.moneyOrFluMessage(false, true, -1) + " required to repod a medium unit");
        baseTextField.setName("RepodFluMedium");
        fluSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        fluSpring.add(new JLabel("Heavy " + mwclient.moneyOrFluMessage(false, true, -1) + ":", SwingConstants.TRAILING));
        baseTextField.setToolTipText(mwclient.moneyOrFluMessage(false, true, -1) + " required to repod a heavy unit");
        baseTextField.setName("RepodFluHeavy");
        fluSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        fluSpring.add(new JLabel("Assault " + mwclient.moneyOrFluMessage(false, true, -1) + ":", SwingConstants.TRAILING));
        baseTextField.setToolTipText(mwclient.moneyOrFluMessage(false, true, -1) + " required to repod an assault unit");
        baseTextField.setName("RepodFluAssault");
        fluSpring.add(baseTextField);

        // then the component spring ...
        baseTextField = new JTextField(5);
        componentSpring.add(new JLabel("Light Components:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Components required to repod a light unit");
        baseTextField.setName("RepodCompLight");
        componentSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        componentSpring.add(new JLabel("Medium Components:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Components required to repod a medium unit");
        baseTextField.setName("RepodCompMedium");
        componentSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        componentSpring.add(new JLabel("Heavy Components:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Components required to repod a heavy unit");
        baseTextField.setName("RepodCompHeavy");
        componentSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        componentSpring.add(new JLabel("Assault Components:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Components required to repod an assault unit");
        baseTextField.setName("RepodCompAssault");
        componentSpring.add(baseTextField);

        // then, the refresh times
        baseTextField = new JTextField(5);
        refreshSpring.add(new JLabel("Light Refresh:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Num. miniticks requires to refresh a factory which pods a light unit");
        baseTextField.setName("RepodRefreshTimeLight");
        refreshSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        refreshSpring.add(new JLabel("Medium Refresh:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Num. miniticks requires to refresh a factory which pods a medium unit");
        baseTextField.setName("RepodRefreshTimeMedium");
        refreshSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        refreshSpring.add(new JLabel("Heavy Refresh:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Num. miniticks requires to refresh a factory which pods a heavy unit");
        baseTextField.setName("RepodRefreshTimeHeavy");
        refreshSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        refreshSpring.add(new JLabel("Assault Refresh:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Num. miniticks requires to refresh a factory which pods an assault unit");
        baseTextField.setName("RepodRefreshTimeAssault");
        refreshSpring.add(baseTextField);

        // and last, the random modifier
        JPanel repodRandomFlowTemp = new JPanel(new SpringLayout());
        baseTextField = new JTextField(5);
        repodRandomFlowTemp.add(new JLabel("Percent of Cost for Random:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Amount to reduce repod costs when a pod is random, instead of targeted.<br>Example 70 would give you 70% of the current cost.</HTML>");
        baseTextField.setName("RepodRandomMod");
        repodRandomFlowTemp.add(baseTextField);

        baseTextField = new JTextField(5);
        repodRandomFlowTemp.add(new JLabel("No Factory Repod Folder:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>If repoding does not use factories then all repods will check this folder<br>for the build tables for the house.</html>");
        baseTextField.setName("NoFactoryRepodFolder");
        repodRandomFlowTemp.add(baseTextField);

        // finalize the layout.
        SpringLayoutHelper.setupSpringGrid(cbillSpring, 4, 2);
        SpringLayoutHelper.setupSpringGrid(fluSpring, 4, 2);
        SpringLayoutHelper.setupSpringGrid(refreshSpring, 4, 2);
        SpringLayoutHelper.setupSpringGrid(componentSpring, 4, 2);
        SpringLayoutHelper.setupSpringGrid(repodRandomFlowTemp, 1, 4);
        repodBoxPanel.add(repodRandomFlowTemp);// add the temp panel for the
        // mod. this needs to be
        // rewritten.
        repodPanel.add(repodBoxPanel);

        /*
         * TECH PANEL CONSTRUCTION Technician (and bays from XP) options.
         */
        JPanel techsBox = new JPanel();
        techsBox.setLayout(new BoxLayout(techsBox, BoxLayout.Y_AXIS));
        JPanel techsCBoxFlow = new JPanel();
        JPanel techsSendRecPayFlow = new JPanel();
        JPanel techSpring = new JPanel(new SpringLayout());
        techsBox.add(techsCBoxFlow);
        techsBox.add(techsSendRecPayFlow);
        techsBox.add(techSpring);

        // the basic CBox flow
        BaseCheckBox = new JCheckBox("Use Techs");
        BaseCheckBox.setToolTipText("Unchecking disables technicians. Not advised.");

        BaseCheckBox.setName("UseTechnicians");
        techsCBoxFlow.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Use XP");

        BaseCheckBox.setToolTipText("Check grants additional technicians w/ XP.");
        BaseCheckBox.setName("UseExperience");
        techsCBoxFlow.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Decreasing Cost");

        BaseCheckBox.setToolTipText("Checking lowers tech hiring costs w/ XP.");
        BaseCheckBox.setName("DecreasingTechCost");
        techsCBoxFlow.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Disable Tech Advancement");
        BaseCheckBox.setToolTipText("Checking disables tech advancement and retiring");
        BaseCheckBox.setName("DisableTechAdvancement");
        techsCBoxFlow.add(BaseCheckBox);

        // the sendRecPay flow.
        BaseCheckBox = new JCheckBox("Sender Pays");

        BaseCheckBox.setToolTipText("If checked, a player sending a unit will pay techs.");
        BaseCheckBox.setName("SenderPaysOnTransfer");
        techsSendRecPayFlow.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Recipient Pays");

        BaseCheckBox.setToolTipText("If checked, a player receiving a unit will pay techs.");
        BaseCheckBox.setName("ReceiverPaysOnTransfer");
        techsSendRecPayFlow.add(BaseCheckBox);

        // set up the spring
        baseTextField = new JTextField(5);
        techSpring.add(new JLabel("Base Tech Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Starting cost to hire a technician");
        baseTextField.setName("BaseTechCost");
        techSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        techSpring.add(new JLabel("XP for Decrease:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount of XP required to reduce hiring cost by 1 " + mwclient.moneyOrFluMessage(true, true, -1));
        baseTextField.setName("XPForDecrease");
        techSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        techSpring.add(new JLabel("Minimum Tech Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Lowest hiring price. XP cannot reduce below this level.");
        baseTextField.setName("MinimumTechCost");
        techSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        techSpring.add(new JLabel("Additive Per Tech:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>" + "Use additive costs -- each tech costs as much as the last one, plus the additive. EG -<br>" + "with .05 set, the first tech would cost .05, the second .10, the third .15, the fourth .20,<br>" + "such that your first 4 techs cost haf a Cbill (total) to maintain, while the 10th tech costs<br>" + "half a " + mwclient.moneyOrFluMessage(true, true, -1) + " all by himself. A cap on this price can be set, after which there is no further<br>" + "increase. The ceiling ABSOLUTELY MUST be a multiple of the additive.</HTML>");
        baseTextField.setName("AdditivePerTech");
        techSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        techSpring.add(new JLabel("Additive Ceiling:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Additive ceiling. Post-game per-tech costs don't increase past this level.");
        baseTextField.setName("AdditiveCostCeiling");
        techSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        techSpring.add(new JLabel("Transfer Payment:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Percentage of usual post-game cost charged if transfer fees are enabled.");
        baseTextField.setName("TransferPayment");
        techSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        techSpring.add(new JLabel("Maint Increase:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount maintainance level is increased each slice a unit is maintained");
        baseTextField.setName("MaintainanceIncrease");
        techSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        techSpring.add(new JLabel("Maint Decrease:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount maintainance level is lowered each slice a unit is unmaintained");
        baseTextField.setName("MaintainanceDecrease");
        techSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        techSpring.add(new JLabel("Base Unmaint Level:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Mainatainance level set when a unit is first unmaintained. Set to 100 to disable.");
        baseTextField.setName("BaseUnmaintainedLevel");
        techSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        techSpring.add(new JLabel("Unmaintain Penalty:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("" + "<HTML>Maintainance reduction for units which are already below 100. If the BaseLevel is lower than current<br>" + "level minus penalty, it is used instead. Example1: A unit has a maintainance level of 90 and is set to<br>" + "unmaintained status. The unmaint penalty is 10 and base elvel is 75. 90-10 = 80, so the base level of 75 is<br>" + "set. Example2: A unit has an mlevel of 80 and is set to unmaintained. 80 - 10 = 70. 70 is set and the base<br>" + "level (75) is ignored.</HTML>");
        baseTextField.setName("UnmaintainedPenalty");
        techSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        techSpring.add(new JLabel("Transfer Scrap Level:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Units @ or under this maint. level must survive a scrap check<br>to be transfered. Set to 0 to disable</HTML>");
        baseTextField.setName("TransferScrapLevel");
        techSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        techSpring.add(new JLabel("Techs To Proto Point Ratio:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Ratio of Techs to 5 Protos Default 1 tech</HTML>");
        baseTextField.setName("TechsToProtoPointRatio");
        techSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        techSpring.add(new JLabel("Techs per Light Mek:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Number of Techs it takes to maintain a light Mek</HTML>");
        baseTextField.setName("TechsForLightMek");
        techSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        techSpring.add(new JLabel("Techs per Medium Mek:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Number of Techs it takes to maintain a medium Mek</HTML>");
        baseTextField.setName("TechsForMediumMek");
        techSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        techSpring.add(new JLabel("Techs per Heavy Mek:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Number of Techs it takes to maintain a heavy Mek</HTML>");
        baseTextField.setName("TechsForHeavyMek");
        techSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        techSpring.add(new JLabel("Techs per Assault Mek:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Number of Techs it takes to maintain an assault Mek</HTML>");
        baseTextField.setName("TechsForAssaultMek");
        techSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        techSpring.add(new JLabel("Techs per Light Vehicle:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Number of Techs it takes to maintain a light Vehicle</HTML>");
        baseTextField.setName("TechsForLightVehicle");
        techSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        techSpring.add(new JLabel("Techs per Medium Vehicle:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Number of Techs it takes to maintain a medium Vehicle</HTML>");
        baseTextField.setName("TechsForMediumVehicle");
        techSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        techSpring.add(new JLabel("Techs per Heavy Vehicle:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Number of Techs it takes to maintain a heavy Vehicle</HTML>");
        baseTextField.setName("TechsForHeavyVehicle");
        techSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        techSpring.add(new JLabel("Techs per Assault Vehicle:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Number of Techs it takes to maintain an assault Vehicle</HTML>");
        baseTextField.setName("TechsForAssaultVehicle");
        techSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        techSpring.add(new JLabel("Techs per Light Infantry:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Number of Techs it takes to maintain a light Infantry</HTML>");
        baseTextField.setName("TechsForLightInfantry");
        techSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        techSpring.add(new JLabel("Techs per Medium Infantry:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Number of Techs it takes to maintain a medium Infantry</HTML>");
        baseTextField.setName("TechsForMediumInfantry");
        techSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        techSpring.add(new JLabel("Techs per Heavy Infantry:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Number of Techs it takes to maintain a heavy Infantry</HTML>");
        baseTextField.setName("TechsForHeavyInfantry");
        techSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        techSpring.add(new JLabel("Techs per Assault Infantry:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Number of Techs it takes to maintain an assault Infantry</HTML>");
        baseTextField.setName("TechsForAssaultInfantry");
        techSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        techSpring.add(new JLabel("Techs per Light BattleArmor:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Number of Techs it takes to maintain a light BattleArmor</HTML>");
        baseTextField.setName("TechsForLightBattleArmor");
        techSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        techSpring.add(new JLabel("Techs per Medium BattleArmor:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Number of Techs it takes to maintain a medium BattleArmor</HTML>");
        baseTextField.setName("TechsForMediumBattleArmor");
        techSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        techSpring.add(new JLabel("Techs per Heavy BattleArmor:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Number of Techs it takes to maintain a heavy BattleArmor</HTML>");
        baseTextField.setName("TechsForHeavyBattleArmor");
        techSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        techSpring.add(new JLabel("Techs per Assault BattleArmor:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Number of Techs it takes to maintain an assault BattleArmor</HTML>");
        baseTextField.setName("TechsForAssaultBattleArmor");
        techSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        techSpring.add(new JLabel("Techs per Light Aero:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Number of Techs it takes to maintain a light Aero</HTML>");
        baseTextField.setName("TechsForLightAero");
        techSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        techSpring.add(new JLabel("Techs per Medium Aero:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Number of Techs it takes to maintain a medium Aero</HTML>");
        baseTextField.setName("TechsForMediumAero");
        techSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        techSpring.add(new JLabel("Techs per Heavy Aero:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Number of Techs it takes to maintain a heavy Aero</HTML>");
        baseTextField.setName("TechsForHeavyAero");
        techSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        techSpring.add(new JLabel("Techs per Assault Aero:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Number of Techs it takes to maintain an assault Aero</HTML>");
        baseTextField.setName("TechsForAssaultAero");
        techSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        techSpring.add(new JLabel("Non-House Unit Increased Techs:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Float field.  Multiplier to tech cost of non-house units.  Only used with Tech Repair.");
        baseTextField.setName("NonFactionUnitsIncreasedTechs");
        techSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        techSpring.add(new JLabel("Max Techs to Hire:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Integer field.  Max number of techs that can be hired.  Set to -1 for unlimited.  Users with more than this number of techs will lose them at next login.");
        baseTextField.setName("MaxTechsToHire");
        techSpring.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(techSpring, 4);

        // finalize the layout
        technicianPanel.add(techsBox);

        /*
         * PILOT SKILLS Panel
         */
        JPanel vehiclePilotSkillsSpring = new JPanel(new SpringLayout());
        JPanel infantryPilotSkillsSpring = new JPanel(new SpringLayout());
        JPanel protomechPilotSkillsSpring = new JPanel(new SpringLayout());
        JPanel battlearmorPilotSkillsSpring = new JPanel(new SpringLayout());
        JPanel aeroPilotSkillsSpring = new JPanel(new SpringLayout());
        JPanel bannedWSWeaponsSpring = new JPanel(new SpringLayout());
        Dimension fieldSize = new Dimension(5, 10);

        JPanel mainSpring = new JPanel(new SpringLayout());

        vehiclePilotSkillsSpring.add(new JLabel("Vee", SwingConstants.TRAILING));
        vehiclePilotSkillsSpring.add(new JLabel("Crew Skills", SwingConstants.LEADING));

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        baseTextField.setMinimumSize(fieldSize);
        vehiclePilotSkillsSpring.add(new JLabel("MA", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain maneuvering ace. Zero to disable skill</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain maneuvering ace</body></html>");
        }
        baseTextField.setName("chanceforMAforVehicle");
        vehiclePilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        vehiclePilotSkillsSpring.add(new JLabel("NAP", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain Natural Aptitude Piloting. Zero to disable skill</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain Natural Aptitude Piloting</body></html>");
        }
        baseTextField.setName("chanceforNAPforVehicle");
        vehiclePilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        vehiclePilotSkillsSpring.add(new JLabel("NAG", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain Natural Aptitude Gunnery. Zero to disable skill</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain Natural Aptitude Gunnery</body></html>");
        }
        baseTextField.setName("chanceforNAGforVehicle");
        vehiclePilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        vehiclePilotSkillsSpring.add(new JLabel("AT", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain gunnery/missile. Zero to disable skill</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain gunnery/missile</body></html>");
        }
        baseTextField.setName("chanceforATforVehicle");
        vehiclePilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        vehiclePilotSkillsSpring.add(new JLabel("TG", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain tactical genius. Zero to disable skill</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain tactical genius</body></html>");
        }
        baseTextField.setName("chanceforTGforVehicle");
        vehiclePilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        vehiclePilotSkillsSpring.add(new JLabel("WS", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain weapon specialist.  Zero to disable skill</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain weapon specialist</body></html>");
        }
        baseTextField.setName("chanceforWSforVehicle");
        vehiclePilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        vehiclePilotSkillsSpring.add(new JLabel("G/B", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain gunnery/ballistic. Zero to disable skill</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain gunnery/ballistic</body></html>");
        }
        baseTextField.setName("chanceforGBforVehicle");
        vehiclePilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        vehiclePilotSkillsSpring.add(new JLabel("G/L", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain gunnery/laser. Zero to disable skill</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain gunnery/laser</body></html>");
        }
        baseTextField.setName("chanceforGLforVehicle");
        vehiclePilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        vehiclePilotSkillsSpring.add(new JLabel("G/M", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain gunnery/missile. Zero to disable skill</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain gunnery/missile</body></html>");
        }
        baseTextField.setName("chanceforGMforVehicle");
        vehiclePilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        vehiclePilotSkillsSpring.add(new JLabel("Trait", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain trait. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain trait</body></html>");
        }
        baseTextField.setName("chanceforTNforVehicle");
        vehiclePilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        vehiclePilotSkillsSpring.add(new JLabel("EI", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain Enhanced Interface. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain Enhanced Interface</body></html>");
        }
        baseTextField.setName("chanceforEIforVehicle");
        vehiclePilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        vehiclePilotSkillsSpring.add(new JLabel("GT", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain gifted. Zero to disable skill</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain gifted</body></html>");
        }
        baseTextField.setName("chanceforGTforVehicle");
        vehiclePilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        vehiclePilotSkillsSpring.add(new JLabel("QS", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain Quick Study. Zero to disable skill</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain Quick Study</body></html>");
        }
        baseTextField.setName("chanceforQSforVehicle");
        vehiclePilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        vehiclePilotSkillsSpring.add(new JLabel("VDNI", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain the VDNI skill. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain the VDNI skill</body></html>");
        }
        baseTextField.setName("chanceforVDNIforVehicle");
        vehiclePilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        vehiclePilotSkillsSpring.add(new JLabel("BVDNI", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain the buffered VDNI skill. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain the buffered VDNI skill</body></html>");
        }
        baseTextField.setName("chanceforBVDNIforVehicle");
        vehiclePilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        vehiclePilotSkillsSpring.add(new JLabel("PS", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain the Pain Shunt skill. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain the Pain Shunt skill</body></html>");
        }
        baseTextField.setName("chanceforPSforVehicle");
        vehiclePilotSkillsSpring.add(baseTextField);

        infantryPilotSkillsSpring.add(new JLabel("Inf", SwingConstants.TRAILING));
        infantryPilotSkillsSpring.add(new JLabel("Squad Skills", SwingConstants.LEADING));

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        infantryPilotSkillsSpring.add(new JLabel("MA", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain maneuvering ace. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain maneuvering ace</body></html>");
        }
        baseTextField.setName("chanceforMAforInfantry");
        infantryPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        infantryPilotSkillsSpring.add(new JLabel("NAP", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain Natural Aptitude Piloting. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain Natural Aptitude Piloting</body></html>");
        }
        baseTextField.setName("chanceforNAPforInfantry");
        infantryPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        infantryPilotSkillsSpring.add(new JLabel("NAG", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain Natural Aptitude Gunnery. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain Natural Aptitude Gunnery</body></html>");
        }
        baseTextField.setName("chanceforNAGforInfantry");
        infantryPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        infantryPilotSkillsSpring.add(new JLabel("AT", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain astech. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain astech</body></html>");
        }
        baseTextField.setName("chanceforATforInfantry");
        infantryPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        infantryPilotSkillsSpring.add(new JLabel("TG", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain tactical genius. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain tactical genius</body></html>");
        }
        baseTextField.setName("chanceforTGforInfantry");
        infantryPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        infantryPilotSkillsSpring.add(new JLabel("WS", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain weapon specialist. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain weapon specialist</body></html>");
        }
        baseTextField.setName("chanceforWSforInfantry");
        infantryPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        infantryPilotSkillsSpring.add(new JLabel("G/B", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain gunnery/ballistic. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain gunnery/ballistic</body></html>");
        }
        baseTextField.setName("chanceforGBforInfantry");
        infantryPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        infantryPilotSkillsSpring.add(new JLabel("G/L", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain gunnery/laser. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain gunnery/laser</body></html>");
        }
        baseTextField.setName("chanceforGLforInfantry");
        infantryPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        infantryPilotSkillsSpring.add(new JLabel("G/M", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain gunnery/missile. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain gunnery/missile</body></html>");
        }
        baseTextField.setName("chanceforGMforInfantry");
        infantryPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        infantryPilotSkillsSpring.add(new JLabel("Trait", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain a trait. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain a trait</body></html>");
        }
        baseTextField.setName("chanceforTNforInfantry");
        infantryPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        infantryPilotSkillsSpring.add(new JLabel("EI", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain Enhanced Interface. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain Enhanced Interface</body></html>");
        }
        baseTextField.setName("chanceforEIforInfantry");
        infantryPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        infantryPilotSkillsSpring.add(new JLabel("GT", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain gifted. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain gifted</body></html>");
        }
        baseTextField.setName("chanceforGTforInfantry");
        infantryPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        infantryPilotSkillsSpring.add(new JLabel("QS", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain Quick Study. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain Quick Study</body></html>");
        }
        baseTextField.setName("chanceforQSforInfantry");
        infantryPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        infantryPilotSkillsSpring.add(new JLabel("PS", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain the Pain Shunt skill. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain the Pain Shunt skill</body></html>");
        }
        baseTextField.setName("chanceforPSforInfantry");
        infantryPilotSkillsSpring.add(baseTextField);

        protomechPilotSkillsSpring.add(new JLabel("Proto", SwingConstants.TRAILING));
        protomechPilotSkillsSpring.add(new JLabel("Pilot Skills", SwingConstants.LEADING));

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        protomechPilotSkillsSpring.add(new JLabel("MA", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain maneuvering ace. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain maneuvering ace</body></html>");
        }
        baseTextField.setName("chanceforMAforProtoMek");
        protomechPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        protomechPilotSkillsSpring.add(new JLabel("NAP", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain Natural Aptitude Piloting. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain Natural Aptitude Piloting</body></html>");
        }
        baseTextField.setName("chanceforNAPforProtoMek");
        protomechPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        protomechPilotSkillsSpring.add(new JLabel("NAG", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain Natural Aptitude Gunnery. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain Natural Aptitude Gunnery</body></html>");
        }
        baseTextField.setName("chanceforNAGforProtoMek");
        protomechPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        protomechPilotSkillsSpring.add(new JLabel("AT", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain astech. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain astech</body></html>");
        }
        baseTextField.setName("chanceforATforProtoMek");
        protomechPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        protomechPilotSkillsSpring.add(new JLabel("TG", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain tactical genius. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain tactical genius</body></html>");
        }
        baseTextField.setName("chanceforTGforProtoMek");
        protomechPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        protomechPilotSkillsSpring.add(new JLabel("WS", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain weapon specialist. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain weapon specialist</body></html>");
        }
        baseTextField.setName("chanceforWSforProtoMek");
        protomechPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        protomechPilotSkillsSpring.add(new JLabel("G/B", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain gunnery/ballistic. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain gunnery/ballistic</body></html>");
        }
        baseTextField.setName("chanceforGBforProtoMek");
        protomechPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        protomechPilotSkillsSpring.add(new JLabel("G/L", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain gunnery/laser. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain gunnery/laser</body></html>");
        }
        baseTextField.setName("chanceforGLforProtoMek");
        protomechPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        protomechPilotSkillsSpring.add(new JLabel("G/M", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain gunnery/missile. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain gunnery/missile</body></html>");
        }
        baseTextField.setName("chanceforGMforProtoMek");
        protomechPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        protomechPilotSkillsSpring.add(new JLabel("Trait", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to a trait. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to a trait</body></html>");
        }
        baseTextField.setName("chanceforTNforProtoMek");
        protomechPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        protomechPilotSkillsSpring.add(new JLabel("EI", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain Enhanced Interface. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain Enhanced Interface</body></html>");
        }
        baseTextField.setName("chanceforEIforProtoMek");
        protomechPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        protomechPilotSkillsSpring.add(new JLabel("GT", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain gifted. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain gifted</body></html>");
        }
        baseTextField.setName("chanceforGTforProtoMek");
        protomechPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        protomechPilotSkillsSpring.add(new JLabel("QS", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain Quick Study. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain Quick Study</body></html>");
        }
        baseTextField.setName("chanceforQSforProtoMek");
        protomechPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        protomechPilotSkillsSpring.add(new JLabel("MT", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain the Med Tech Skill. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain the Med Tech Skill</body></html>");
        }
        baseTextField.setName("chanceforMTforProtoMek");
        protomechPilotSkillsSpring.add(baseTextField);

        battlearmorPilotSkillsSpring.add(new JLabel("BA", SwingConstants.TRAILING));
        battlearmorPilotSkillsSpring.add(new JLabel("Squad Skills", SwingConstants.LEADING));

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        battlearmorPilotSkillsSpring.add(new JLabel("MA", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain maneuvering ace. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain maneuvering ace</body></html>");
        }
        baseTextField.setName("chanceforMAforBattleArmor");
        battlearmorPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        battlearmorPilotSkillsSpring.add(new JLabel("NAP", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain Natural Aptitude Piloting. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain Natural Aptitude Piloting</body></html>");
        }
        baseTextField.setName("chanceforNAPforBattleArmor");
        battlearmorPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        battlearmorPilotSkillsSpring.add(new JLabel("NAG", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain Natural Aptitude Gunnery. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain Natural Aptitude Gunnery</body></html>");
        }
        baseTextField.setName("chanceforNAGforBattleArmor");
        battlearmorPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        battlearmorPilotSkillsSpring.add(new JLabel("AT", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain astech. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain astech</body></html>");
        }
        baseTextField.setName("chanceforATforBattleArmor");
        battlearmorPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        battlearmorPilotSkillsSpring.add(new JLabel("TG", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain tactical genius. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain tactical genius</body></html>");
        }
        baseTextField.setName("chanceforTGforBattleArmor");
        battlearmorPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        battlearmorPilotSkillsSpring.add(new JLabel("WS", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain weapon specialist. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain weapon specialist</body></html>");
        }
        baseTextField.setName("chanceforWSforBattleArmor");
        battlearmorPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        battlearmorPilotSkillsSpring.add(new JLabel("G/B", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain gunnery/ballistic. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain gunnery/ballistic</body></html>");
        }
        baseTextField.setName("chanceforGBforBattleArmor");
        battlearmorPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        battlearmorPilotSkillsSpring.add(new JLabel("G/L", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain gunnery/laser. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain gunnery/laser</body></html>");
        }
        baseTextField.setName("chanceforGLforBattleArmor");
        battlearmorPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        battlearmorPilotSkillsSpring.add(new JLabel("G/M", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain gunnery/missile. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain gunnery/missile</body></html>");
        }
        baseTextField.setName("chanceforGMforBattleArmor");
        battlearmorPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        battlearmorPilotSkillsSpring.add(new JLabel("Trait", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain a trait. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain a trait</body></html>");
        }
        baseTextField.setName("chanceforTNforBattleArmor");
        battlearmorPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        battlearmorPilotSkillsSpring.add(new JLabel("EI", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain Enhanced Interface. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain Enhanced Interface</body></html>");
        }
        baseTextField.setName("chanceforEIforBattleArmor");
        battlearmorPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        battlearmorPilotSkillsSpring.add(new JLabel("GT", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain gifted. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain gifted</body></html>");
        }
        baseTextField.setName("chanceforGTforBattleArmor");
        battlearmorPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        battlearmorPilotSkillsSpring.add(new JLabel("QS", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain Quick Study. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain Quick Study</body></html>");
        }
        baseTextField.setName("chanceforQSforBattleArmor");
        battlearmorPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        battlearmorPilotSkillsSpring.add(new JLabel("PS", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain the Pain Shunt skill. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain the Pain Shunt skill</body></html>");
        }
        baseTextField.setName("chanceforPSforBattleArmor");
        battlearmorPilotSkillsSpring.add(baseTextField);

        aeroPilotSkillsSpring.add(new JLabel("Aero", SwingConstants.TRAILING));
        aeroPilotSkillsSpring.add(new JLabel("Pilot Skills", SwingConstants.LEADING));

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        baseTextField.setMinimumSize(fieldSize);
        aeroPilotSkillsSpring.add(new JLabel("MA", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain maneuvering ace. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain maneuvering ace</body></html>");
        }
        baseTextField.setName("chanceforMAforAero");
        aeroPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        aeroPilotSkillsSpring.add(new JLabel("NAP", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain Natural Aptitude Piloting. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain Natural Aptitude Piloting</body></html>");
        }
        baseTextField.setName("chanceforNAPforAero");
        aeroPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        aeroPilotSkillsSpring.add(new JLabel("NAG", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain Natural Aptitude Gunnery. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain Natural Aptitude Gunnery</body></html>");
        }
        baseTextField.setName("chanceforNAGforAero");
        aeroPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        aeroPilotSkillsSpring.add(new JLabel("AT", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain gunnery/missile. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain gunnery/missile</body></html>");
        }
        baseTextField.setName("chanceforATforAero");
        aeroPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        aeroPilotSkillsSpring.add(new JLabel("TG", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain tactical genius. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain tactical genius</body></html>");
        }
        baseTextField.setName("chanceforTGforAero");
        aeroPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        aeroPilotSkillsSpring.add(new JLabel("WS", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain weapon specialist. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain weapon specialist</body></html>");
        }
        baseTextField.setName("chanceforWSforAero");
        aeroPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        aeroPilotSkillsSpring.add(new JLabel("G/B", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain gunnery/ballistic. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain gunnery/ballistic</body></html>");
        }
        baseTextField.setName("chanceforGBforAero");
        aeroPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        aeroPilotSkillsSpring.add(new JLabel("G/L", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain gunnery/laser. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain gunnery/laser</body></html>");
        }
        baseTextField.setName("chanceforGLforAero");
        aeroPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        aeroPilotSkillsSpring.add(new JLabel("G/M", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain gunnery/missile. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain gunnery/missile</body></html>");
        }
        baseTextField.setName("chanceforGMforAero");
        aeroPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        aeroPilotSkillsSpring.add(new JLabel("Trait", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain trait. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain trait</body></html>");
        }
        baseTextField.setName("chanceforTNforAero");
        aeroPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        aeroPilotSkillsSpring.add(new JLabel("EI", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain Enhanced Interface. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain Enhanced Interface</body></html>");
        }
        baseTextField.setName("chanceforEIforAero");
        aeroPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        aeroPilotSkillsSpring.add(new JLabel("GT", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain gifted. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain gifted</body></html>");
        }
        baseTextField.setName("chanceforGTforAero");
        aeroPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        aeroPilotSkillsSpring.add(new JLabel("QS", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain Quick Study. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain Quick Study</body></html>");
        }
        baseTextField.setName("chanceforQSforAero");
        aeroPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        aeroPilotSkillsSpring.add(new JLabel("VDNI", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain the VDNI skill. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain the VDNI skill</body></html>");
        }
        baseTextField.setName("chanceforVDNIforAero");
        aeroPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        aeroPilotSkillsSpring.add(new JLabel("BVDNI", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain the buffered VDNI skill. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain the buffered VDNI skill</body></html>");
        }
        baseTextField.setName("chanceforBVDNIforAero");
        aeroPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        aeroPilotSkillsSpring.add(new JLabel("PS", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain the Pain Shunt skill. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain the Pain Shunt skill</body></html>");
        }
        baseTextField.setName("chanceforPSforAero");
        aeroPilotSkillsSpring.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(vehiclePilotSkillsSpring, 2);
        SpringLayoutHelper.setupSpringGrid(infantryPilotSkillsSpring, 2);
        SpringLayoutHelper.setupSpringGrid(protomechPilotSkillsSpring, 2);
        SpringLayoutHelper.setupSpringGrid(battlearmorPilotSkillsSpring, 2);
        SpringLayoutHelper.setupSpringGrid(aeroPilotSkillsSpring, 2);

        baseTextField = new JTextField(5);
        bannedWSWeaponsSpring.add(new JLabel("Banned WS Weapons:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Add what Weapons you do not want pilots to get Weapon Specalist in/body></html>");
        baseTextField.setName("BannedWSWeapons");
        bannedWSWeaponsSpring.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(bannedWSWeaponsSpring, 2);

        // 1x5 grid
        JPanel skillGrid = new JPanel(new GridLayout(1, 4));

        skillGrid.add(vehiclePilotSkillsSpring);
        skillGrid.add(infantryPilotSkillsSpring);
        skillGrid.add(protomechPilotSkillsSpring);
        skillGrid.add(battlearmorPilotSkillsSpring);
        skillGrid.add(aeroPilotSkillsSpring);

        mainSpring.add(skillGrid);
        mainSpring.add(bannedWSWeaponsSpring);

        SpringLayoutHelper.setupSpringGrid(mainSpring, 2, 1);

        pilotSkillsPanel.add(mainSpring);

        /*
         * Mek Pilot Skills Panel
         */

        JPanel mekPilotSkillsSpring = new JPanel(new SpringLayout());

        mekPilotSkillsSpring.add(new JLabel(" ", SwingConstants.TRAILING));
        mekPilotSkillsSpring.add(new JLabel("Mek", SwingConstants.TRAILING));
        mekPilotSkillsSpring.add(new JLabel("Pilot Skills", SwingConstants.TRAILING));
        mekPilotSkillsSpring.add(new JLabel(" ", SwingConstants.TRAILING));

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        mekPilotSkillsSpring.add(new JLabel("DM", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain dodge maneuver. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain dodge maneuver</body></html>");
        }
        baseTextField.setName("chanceforDMforMek");
        mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        mekPilotSkillsSpring.add(new JLabel("MS", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain melee specialist. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain melee specialist</body></html>");
        }
        baseTextField.setName("chanceforMSforMek");
        mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        mekPilotSkillsSpring.add(new JLabel("PR", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain pain resistance. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain pain resistance</body></html>");
        }
        baseTextField.setName("chanceforPRforMek");
        mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        mekPilotSkillsSpring.add(new JLabel("SV", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain survivalist. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain survivalist</body></html>");
        }
        baseTextField.setName("chanceforSVforMek");
        mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        mekPilotSkillsSpring.add(new JLabel("IM", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain iron man. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain iron man</body></html>");
        }
        baseTextField.setName("chanceforIMforMek");
        mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        mekPilotSkillsSpring.add(new JLabel("MA", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain maneuvering ace. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain maneuvering ace</body></html>");
        }
        baseTextField.setName("chanceforMAforMek");
        mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        mekPilotSkillsSpring.add(new JLabel("NAP", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain Natural Aptitude Piloting. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain Natural Aptitude Piloting</body></html>");
        }
        baseTextField.setName("chanceforNAPforMek");
        mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        mekPilotSkillsSpring.add(new JLabel("NAG", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain Natural Aptitude Gunnery. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain Natural Aptitude Gunnery</body></html>");
        }
        baseTextField.setName("chanceforNAGforMek");
        mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        mekPilotSkillsSpring.add(new JLabel("AT", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain Astech. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain Astech</body></html>");
        }
        baseTextField.setName("chanceforATforMek");
        mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        mekPilotSkillsSpring.add(new JLabel("TG", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain tactical genius. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain tactical genius</body></html>");
        }
        baseTextField.setName("chanceforTGforMek");
        mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        mekPilotSkillsSpring.add(new JLabel("WS", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain weapon specialist. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain weapon specialist</body></html>");
        }
        baseTextField.setName("chanceforWSforMek");
        mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        mekPilotSkillsSpring.add(new JLabel("G/B", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain gunnery/Ballistic. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain gunnery/Ballistic</body></html>");
        }
        baseTextField.setName("chanceforGBforMek");
        mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        mekPilotSkillsSpring.add(new JLabel("G/L", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain gunnery/laser. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain gunnery/laser</body></html>");
        }
        baseTextField.setName("chanceforGLforMek");
        mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        mekPilotSkillsSpring.add(new JLabel("G/M", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain gunnery/missile. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain gunnery/missile</body></html>");
        }
        baseTextField.setName("chanceforGMforMek");
        mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        mekPilotSkillsSpring.add(new JLabel("Trait", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain a trait. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain a trait</body></html>");
        }
        baseTextField.setName("chanceforTNforMek");
        mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        mekPilotSkillsSpring.add(new JLabel("EI", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain Enhanced Interface. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain Enhanced Interface</body></html>");
        }
        baseTextField.setName("chanceforEIforMek");
        mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        mekPilotSkillsSpring.add(new JLabel("GT", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain gifted. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain gifted</body></html>");
        }
        baseTextField.setName("chanceforGTforMek");
        mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        mekPilotSkillsSpring.add(new JLabel("QS", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain Quick Study. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain Quick Study</body></html>");
        }
        baseTextField.setName("chanceforQSforMek");
        mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        mekPilotSkillsSpring.add(new JLabel("MT", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain the Med Tech skill. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain the Med Tech skill</body></html>");
        }
        baseTextField.setName("chanceforMTforMek");
        mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        mekPilotSkillsSpring.add(new JLabel("Edge", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain the Edge skill. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain the Edge skill</body></html>");
        }
        baseTextField.setName("chanceforEDforMek");
        mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        mekPilotSkillsSpring.add(new JLabel("VDNI", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain the VDNI skill. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain the VDNI skill</body></html>");
        }
        baseTextField.setName("chanceforVDNIforMek");
        mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        mekPilotSkillsSpring.add(new JLabel("BVDNI", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain the buffered VDNI skill. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain the buffered VDNI skill</body></html>");
        }
        baseTextField.setName("chanceforBVDNIforMek");
        mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        mekPilotSkillsSpring.add(new JLabel("PS", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain the Pain Shunt skill. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain the Pain Shunt skill</body></html>");
        }
        baseTextField.setName("chanceforPSforMek");
        mekPilotSkillsSpring.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(mekPilotSkillsSpring, 4);

        mekPilotSkillsPanel.add(mekPilotSkillsSpring);

        /*
         * Pilot Skills Panel BV mods
         */

        JPanel SkillModSpring = new JPanel(new SpringLayout());

        baseTextField = new JTextField(5);
        SkillModSpring.add(new JLabel("Dodge Maneuver Mod", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Flat BV Mod for Dodge Maneuver");
        baseTextField.setName("DodgeManeuverBaseBVMod");
        SkillModSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        SkillModSpring.add(new JLabel("Melee Specialist Mod", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base BV Mod for Melee Specialist");
        baseTextField.setName("MeleeSpecialistBaseBVMod");
        SkillModSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        SkillModSpring.add(new JLabel("Hatchet Mod", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Base BV Mod per Hatchet/Sword<br> [(Base Increase)(unit tonage/10)]<br>+(hatchet mod * number of physical weapons)</html>");
        baseTextField.setName("HatchetRating");
        SkillModSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        SkillModSpring.add(new JLabel("Pain Resistance BV Mod", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Base BV mod for Pain Resistance<br>a base BV increase, and this would be applied for<br>every ammo critical or<br>Gauss weapon the unit has");
        baseTextField.setName("PainResistanceBaseBVMod");
        SkillModSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        SkillModSpring.add(new JLabel("Iron Man BV Mod", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Base BV mod for Iron Man<br>a base BV increase, and this would be applied for<br>every ammo critical or<br>Gauss weapon the unit has");
        baseTextField.setName("IronManBaseBVMod");
        SkillModSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        SkillModSpring.add(new JLabel("MA BV Mod", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Maneuvering Ace Base BV mod<br>(\"Base Increase\")(\"Unit's top speed\"/\"Speed rating\")</html>");
        baseTextField.setName("ManeuveringAceBaseBVMod");
        SkillModSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        SkillModSpring.add(new JLabel("MA Speed Rating", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Maneuvering Ace Base BV mod<br>(\"Base Increase\")(\"Unit's top speed\"/\"Speed rating\")</html>");
        baseTextField.setName("ManeuveringAceSpeedRating");
        SkillModSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        SkillModSpring.add(new JLabel("Tactical Genius BV", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Flat BV amount added for Tactical Genius</html>");
        baseTextField.setName("TacticalGeniusBVMod");
        SkillModSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        SkillModSpring.add(new JLabel("EI bv mod", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>BV mod added to the unit due to EI</html>");
        baseTextField.setName("EnhancedInterfaceBaseBVMod");
        SkillModSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        SkillModSpring.add(new JLabel("Edge bv mod", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>BV mod added to the unit due to Edge Skill</html>");
        baseTextField.setName("EdgeBaseBVMod");
        SkillModSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        SkillModSpring.add(new JLabel("Max Edge", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Max number of edges a pilot can have per game<br>This is akin to levels.</html>");
        baseTextField.setName("MaxEdgeChanges");
        SkillModSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        SkillModSpring.add(new JLabel("VDNI", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>BV mod added to the unit due to VDNI.</html>");
        baseTextField.setName("VDNIBaseBVMod");
        SkillModSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        SkillModSpring.add(new JLabel("BVDNI", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>BV mod added to the unit due to Buffered VDNI.</html>");
        baseTextField.setName("BufferedVDNIBaseBVMod");
        SkillModSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        SkillModSpring.add(new JLabel("Pain Shunt", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>BV mod added to the unit due to Pain Shunt.</html>");
        baseTextField.setName("PainShuntBaseBVMod");
        SkillModSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        SkillModSpring.add(new JLabel("Gifted % Mod", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Note Double Field<br>The amount off the cost of other upgrades a Gifted Pilot gets.<br>Example .05 for 5% off</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Pilots receive an extra x% chance to gain a skill when they fail<br>to level Piloting or Gunnery after a win</body></html>");
        }
        baseTextField.setName("GiftedPercent");
        SkillModSpring.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(SkillModSpring, 4);

        pilotSkillsModPanel.add(SkillModSpring);

        /*
         * Pilots options panel
         */
        JPanel pilotCBoxGrid = new JPanel(new GridLayout(4, 4));
        JPanel pilotOptionsSpring1 = new JPanel(new SpringLayout());
        JPanel pilotOptionsSpring2 = new JPanel(new SpringLayout());

        // pilotSpring1, 8 elements
        baseTextField = new JTextField(5);
        pilotOptionsSpring1.add(new JLabel("Skill Change:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("% chance for a new pilot to have a maxtech skill");
        baseTextField.setName("BornSkillChance");
        pilotOptionsSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
        pilotOptionsSpring1.add(new JLabel("Skill Gain:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>% chance for a pilot to get a skill<br>instead of a gunnery/piloting upgrade</html>");
        baseTextField.setName("SkillLevelChance");
        pilotOptionsSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
        pilotOptionsSpring1.add(new JLabel("XP Loss:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>% chance for a pilot to lose<br>accumulated XP in the Queue</html>");
        baseTextField.setName("ClearXPInQue");
        pilotOptionsSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
        pilotOptionsSpring1.add(new JLabel("Cost For Mek Pilot:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Cost to buy a new Mek pilot from the faction pools<HTML>");
        baseTextField.setName("CostToBuyNewPilot");
        pilotOptionsSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
        pilotOptionsSpring1.add(new JLabel("Max Pilots From House:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Cap for pilots in a players personal queue<br>if they have less they can purchase<br>from the faction pools<br>if Allow Players to Buy<br>with full Queues is checked");
        baseTextField.setName("MaxAllowedPilotsInQueueToBuyFromHouse");
        pilotOptionsSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
        pilotOptionsSpring1.add(new JLabel("Base Pilot Survival:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Base Survival Rate for an ejected pilot<br>If the %planet control is less then this this<br>amount is used.</html>");
        baseTextField.setName("BasePilotSurvival");
        pilotOptionsSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
        pilotOptionsSpring1.add(new JLabel("Trapped In Mech Survival:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Unique to in-mech pilots (engine kills). Penalty" + "<br>for being in a stationary unit when the capture" + "<br>crews come around and sweep the field.</html>");
        baseTextField.setName("TrappedInMechSurvivalMod");
        pilotOptionsSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
        pilotOptionsSpring1.add(new JLabel("Convert Pilots:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>% Chance that captured pilots are converted<br>and sent to faction/player pools</html>");
        baseTextField.setName("ChanceToConvertCapturedPilots");
        pilotOptionsSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
        pilotOptionsSpring1.add(new JLabel("Damage Per Hit:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Amount of damage the pilot will take per hit they receive in game<br>NOTE: This amount will be translated back into CBT hits<br>When sent back to the clients.</html>");
        baseTextField.setName("AmountOfDamagePerPilotHit");
        pilotOptionsSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
        pilotOptionsSpring1.add(new JLabel("Cost For Proto Pilot:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Cost to buy a new Proto pilot from the faction pools<HTML>");
        baseTextField.setName("CostToBuyNewProtoPilot");
        pilotOptionsSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
        pilotOptionsSpring1.add(new JLabel("Pilot Skil Sell Back Mod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Sets what percent of the original cost the pilot gets back in exp<br>when a skill is sold.<br>NOTE: This is a double filed .5 = 50%</html>");
        baseTextField.setName("PilotUpgradeSellBackPercent");
        pilotOptionsSpring1.add(baseTextField);

        // PilotSpring2 - 8 elements
        baseTextField = new JTextField(5);
        pilotOptionsSpring2.add(new JLabel("Total Skill to Retire:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Total skill (Piloting + Gunnery) of pilot must be equal to or less than this number in order to retire for free.</html>");
        baseTextField.setName("TotalSkillForFreeRetirement");
        pilotOptionsSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        pilotOptionsSpring2.add(new JLabel("Early Retire Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>" + mwclient.moneyOrFluMessage(true, true, -1) + " cost PER LEVEL to retire a pilot before free. For<br>" + "example, if Skill to Retire is 6, a pilot is 4/5 (Total:9)<br>" + "and the cost is 10, it will cost (9-6)*10=30 " + mwclient.moneyOrFluMessage(true, true, -1) + " to<br>" + "retire the 4/5.</html>");
        baseTextField.setName("CostPerLevelToRetireEarly");
        pilotOptionsSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        pilotOptionsSpring2.add(new JLabel("Best Gunnery:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Best Gunnery Skill allowed.");
        baseTextField.setName("BestGunnerySkill");
        pilotOptionsSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        pilotOptionsSpring2.add(new JLabel("Best Piloting", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Best Piloting skill allowed.");
        baseTextField.setName("BestPilotingSkill");
        pilotOptionsSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        pilotOptionsSpring2.add(new JLabel("Best Total Pilot", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Lowest skill total (Gunnery + Piloting = Total) allowed.");
        baseTextField.setName("BestTotalPilot");
        pilotOptionsSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        pilotOptionsSpring2.add(new JLabel("Base level Up Roll", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>" + "Basic 1dX required used for level up. If roll<br>" + "is less than pilot XP, pilot gains a level.</html>");
        baseTextField.setName("BaseRollToLevel");
        pilotOptionsSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        pilotOptionsSpring2.add(new JLabel("Roll Multiplier", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>" + "Base * Multiplier * (10 - total skill). If Base is 1000, and<br>" + "multiplier is 2, and skill is 3/4 (7), pilot will need to roll<br>" + "lower than his XP on 1d6000 (1000Base * 2Multi * 3Levels = 6000).</html>");
        baseTextField.setName("MultiplierPerPreviousLevel");
        pilotOptionsSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        pilotOptionsSpring2.add(new JLabel("Health per Tick", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>The number of points a pilot will heal in one tick<br>NOTE: with PPQ on pilots must be in the queue to heal<br>With PPQ off pilots will heal while in their units.</html>");
        baseTextField.setName("PilotAmountHealedPerTick");
        pilotOptionsSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        pilotOptionsSpring2.add(new JLabel("MedTech per Tick", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>The number of points a pilot will heal in one tick if they have the medtech skill<br>NOTE: with PPQ on pilots must be in the queue to heal<br>With PPQ off pilots will heal while in their units.</html>");
        baseTextField.setName("MedTechAmountHealedPerTick");
        pilotOptionsSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        pilotOptionsSpring2.add(new JLabel("Max Pilot Upgrades:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Set the maximum numbers of skills a player can give a pilot.<br>Set to -1 for unlimited.</html>");
        baseTextField.setName("MaxPilotUpgrades");
        pilotOptionsSpring2.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(pilotOptionsSpring1, 2);
        SpringLayoutHelper.setupSpringGrid(pilotOptionsSpring2, 2);

        // pilot cboxes
        BaseCheckBox = new JCheckBox("Elite BV Mod");
        BaseCheckBox.setToolTipText("Increase BV of units which are <2/X or X/<2 above FASA levels.");
        BaseCheckBox.setName("ElitePilotsBVMod");
        pilotCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("MaxTech Skills");
        BaseCheckBox.setName("PilotSkills");
        BaseCheckBox.setToolTipText("Allow MaxTech pilot skills (Manuv. Ace, Pain Resist, etc)");
        pilotCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Unlevel@Queue");
        BaseCheckBox.setToolTipText("<HTML>" + "Unchecking allows Pilots to keep skills and XP in queue<br>" + "after their rides die.</HTML>");
        BaseCheckBox.setName("ReduceSkillsInQue");
        pilotCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Allow Green Pilots");
        BaseCheckBox.setToolTipText("Check in order to allow green pilots. 4/6, 5/5, etc.");
        BaseCheckBox.setName("AllowGreenPilots");
        pilotCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Allow Vet Pilots");
        BaseCheckBox.setToolTipText("Check in order to allow vet pilots. 3/5, 4/4, etc.");
        BaseCheckBox.setName("AllowVetPilots");
        pilotCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Allow PPQ");
        BaseCheckBox.setToolTipText("<HTML>Allow Personal Pilot Queues<br>Players are allowed to keep their own pilots instead of them going to the faction pools</HTML>");
        BaseCheckBox.setName("AllowPersonalPilotQueues");
        pilotCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Allow Extra Pilots");
        BaseCheckBox.setToolTipText("<HTML>When checked the players can buy<br>pilots from the faction pool<br>even if they already have pilots of that<br>type/class in their pools</HTML>");
        BaseCheckBox.setName("AllowPlayerToBuyPilotsFromHouseWhenPoolIsFull");
        pilotCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Downed Pilots Roll");
        BaseCheckBox.setToolTipText("<HTML>When checked a downed pilot must make a survival roll<br>to see if they make it home<br>or are captured</HTML>");
        BaseCheckBox.setName("DownPilotsMustRollForSurvival");
        pilotCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Allow Retirement");
        BaseCheckBox.setToolTipText("Allow players to retire their pilots.");
        BaseCheckBox.setName("PilotRetirementAllowed");
        pilotCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Early Retirement");
        BaseCheckBox.setToolTipText("Allow players to pay a fee in order to retire their pilots earlier than normal.");
        BaseCheckBox.setName("EarlyRetirementAllowed");
        pilotCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Elite Retirements");
        BaseCheckBox.setToolTipText("<html>Randomly retire elite pilots who can't level any more. Rolls to retire are<br>" + "against the same target as their final level up. This automated retirement is separate<br>" + "from player-initiated retirement and will work even if \"Allow Retirement\" is disabled.</html>");
        BaseCheckBox.setName("RandomRetirementOfElites");
        pilotCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Common Names Only");
        BaseCheckBox.setToolTipText("Pilot names are only pulled from the Pilotnames.txt");
        BaseCheckBox.setName("UseCommonPilotNameFileOnly");
        pilotCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Random Pilot Levels");
        BaseCheckBox.setToolTipText("<html>" + "Disable to use RPG style pilot levelling. Pilots must gain<br>" + "Base * Multiplier * (10-Skill) XP to reach next level.<br>" + "Random roll to level up is removed - only raw XP is used.</html>");
        BaseCheckBox.setName("UseRandomPilotLevelups");
        pilotCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Pilot Damage Transfers");
        BaseCheckBox.setToolTipText("<html>If a pilot takes damage in a game it'll transfer back to the campaign<br>and the pilot will need to heal up.</html>");
        BaseCheckBox.setName("AllowPilotDamageToTransfer");
        pilotCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Crews Stay With Units");
        BaseCheckBox.setToolTipText("<html>If Checked Crews stay with thier units after being donated.</html>");
        BaseCheckBox.setName("CrewsStayWithUnits");
        pilotCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("All Pilots Level");
        BaseCheckBox.setToolTipText("<html>If Checked Then even losing pilots will have a chance to level.</html>");
        BaseCheckBox.setName("LosingPilotsCheckToLevel");
        pilotCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Players Level Pilots");
        BaseCheckBox.setToolTipText("<html>If Checked Then pilots do not check for leveling after each Operation<br> instead they players can buy skills and attributes with the pilots exp.</html>");
        BaseCheckBox.setName("PlayersCanBuyPilotUpgrades");
        pilotCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Pilots Must level Evenly");
        BaseCheckBox.setToolTipText("<html>If Checked then players must level their pilots skills via stair step.<br>This means no more then 1 difference between gunnery and piloting<br>unless the Pilot has NAG or NAP.</html>");
        BaseCheckBox.setName("PilotsMustLevelEvenly");
        pilotCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Players Demote Pilots");
        BaseCheckBox.setToolTipText("<html>If Checked, as well as Players Level Pilots, Then players can sell back pilots skills.</html>");
        BaseCheckBox.setName("PlayersCanSellPilotUpgrades");
        pilotCBoxGrid.add(BaseCheckBox);

        // finalize the layout
        JPanel pilotBox = new JPanel(new SpringLayout());
        JPanel pilotFlow = new JPanel();
        pilotFlow.add(pilotOptionsSpring1);
        pilotFlow.add(pilotOptionsSpring2);
        pilotBox.add(pilotFlow);
        pilotBox.add(pilotCBoxGrid);

        SpringLayoutHelper.setupSpringGrid(pilotBox, 2, 1);

        pilotsPanel.add(pilotBox);

        /*
         * UNITS PANEL CONSTRUCTION. Unit options.
         */
        JPanel mekCbillsSpring = new JPanel(new SpringLayout());
        JPanel mekFluSpring = new JPanel(new SpringLayout());
        JPanel mekComponentSpring = new JPanel(new SpringLayout());
        JPanel vehCbillsSpring = new JPanel(new SpringLayout());
        JPanel vehFluSpring = new JPanel(new SpringLayout());
        JPanel vehComponentSpring = new JPanel(new SpringLayout());
        JPanel infCbillsSpring = new JPanel(new SpringLayout());
        JPanel infFluSpring = new JPanel(new SpringLayout());
        JPanel infComponentSpring = new JPanel(new SpringLayout());

        JPanel unitSpringGrid = new JPanel(new GridLayout(3, 3));
        unitSpringGrid.add(mekCbillsSpring);
        unitSpringGrid.add(mekFluSpring);
        unitSpringGrid.add(mekComponentSpring);
        unitSpringGrid.add(vehCbillsSpring);
        unitSpringGrid.add(vehFluSpring);
        unitSpringGrid.add(vehComponentSpring);
        unitSpringGrid.add(infCbillsSpring);
        unitSpringGrid.add(infFluSpring);
        unitSpringGrid.add(infComponentSpring);

        JPanel unitsMiscSpring = new JPanel(new SpringLayout());

        JPanel unitCBoxGrid = new JPanel(new GridLayout(3, 4));

        // MEKs
        baseTextField = new JTextField(5);
        mekCbillsSpring.add(new JLabel("Light Mek Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base " + mwclient.moneyOrFluMessage(true, true, -1) + " for a light mek.");
        baseTextField.setName("LightPrice");
        mekCbillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        mekCbillsSpring.add(new JLabel("Medium Mek Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base " + mwclient.moneyOrFluMessage(true, true, -1) + " for a medium mek.");
        baseTextField.setName("MediumPrice");
        mekCbillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        mekCbillsSpring.add(new JLabel("Heavy Mek Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base " + mwclient.moneyOrFluMessage(true, true, -1) + " for a heavy mek.");
        baseTextField.setName("HeavyPrice");
        mekCbillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        mekCbillsSpring.add(new JLabel("Assault Mek Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base " + mwclient.moneyOrFluMessage(true, true, -1) + " for an assault mek.");
        baseTextField.setName("AssaultPrice");
        mekCbillsSpring.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(mekCbillsSpring, 4, 2);

        baseTextField = new JTextField(5);
        mekFluSpring.add(new JLabel("Light Mek Flu:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base flu for a light mek.");
        baseTextField.setName("LightInf");
        mekFluSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        mekFluSpring.add(new JLabel("Medium Mek Flu:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base flu for a medium mek.");
        baseTextField.setName("MediumInf");
        mekFluSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        mekFluSpring.add(new JLabel("Heavy Mek Flu:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base flu for a heavy mek.");
        baseTextField.setName("HeavyInf");
        mekFluSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        mekFluSpring.add(new JLabel("Assault Mek Flu:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base flu for an assault mek.");
        baseTextField.setName("AssaultInf");
        mekFluSpring.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(mekFluSpring, 4, 2);

        baseTextField = new JTextField(5);
        mekComponentSpring.add(new JLabel("Light Mek PP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base components to construct a light mek.");
        baseTextField.setName("LightPP");
        mekComponentSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        mekComponentSpring.add(new JLabel("Medium Mek PP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base components to construct a medium mek.");
        baseTextField.setName("MediumPP");
        mekComponentSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        mekComponentSpring.add(new JLabel("Heavy Mek PP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base components to construct a heavy mek.");
        baseTextField.setName("HeavyPP");
        mekComponentSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        mekComponentSpring.add(new JLabel("Assault Mek PP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base components to construct an assault mek.");
        baseTextField.setName("AssaultPP");
        mekComponentSpring.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(mekComponentSpring, 4, 2);

        // VEHICLES
        baseTextField = new JTextField(5);
        vehCbillsSpring.add(new JLabel("Light Veh Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base " + mwclient.moneyOrFluMessage(true, true, -1) + " for a light veh.");
        baseTextField.setName("LightVehiclePrice");
        vehCbillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        vehCbillsSpring.add(new JLabel("Medium Veh Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base " + mwclient.moneyOrFluMessage(true, true, -1) + " for a medium veh.");
        baseTextField.setName("MediumVehiclePrice");
        vehCbillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        vehCbillsSpring.add(new JLabel("Heavy Veh Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base " + mwclient.moneyOrFluMessage(true, true, -1) + " for a heavy veh.");
        baseTextField.setName("HeavyVehiclePrice");
        vehCbillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        vehCbillsSpring.add(new JLabel("Assault Veh Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base " + mwclient.moneyOrFluMessage(true, true, -1) + " for an assault veh.");
        baseTextField.setName("AssaultVehiclePrice");
        vehCbillsSpring.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(vehCbillsSpring, 4, 2);

        baseTextField = new JTextField(5);
        vehFluSpring.add(new JLabel("Light Veh Flu:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base flu for a light veh.");
        baseTextField.setName("LightVehicleInf");
        vehFluSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        vehFluSpring.add(new JLabel("Medium Veh Flu:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base flu for a medium veh.");
        baseTextField.setName("MediumVehicleInf");
        vehFluSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        vehFluSpring.add(new JLabel("Heavy Veh Flu:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base flu for a heavy veh.");
        baseTextField.setName("HeavyVehicleInf");
        vehFluSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        vehFluSpring.add(new JLabel("Assault Veh Flu:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base flu for an assault veh.");
        baseTextField.setName("AssaultVehicleInf");
        vehFluSpring.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(vehFluSpring, 4, 2);

        baseTextField = new JTextField(5);
        vehComponentSpring.add(new JLabel("Light Veh PP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base components to construct a light veh.");
        baseTextField.setName("LightVehiclePP");
        vehComponentSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        vehComponentSpring.add(new JLabel("Medium Veh PP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base components to construct a medium veh.");
        baseTextField.setName("MediumVehiclePP");
        vehComponentSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        vehComponentSpring.add(new JLabel("Heavy Veh PP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base components to construct a heavy veh.");
        baseTextField.setName("HeavyVehiclePP");
        vehComponentSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        vehComponentSpring.add(new JLabel("Assault Veh PP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base components to construct an assault veh.");
        baseTextField.setName("AssaultVehiclePP");
        vehComponentSpring.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(vehComponentSpring, 4, 2);

        // INFANTRY
        baseTextField = new JTextField(5);
        infCbillsSpring.add(new JLabel("Light Inf Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base " + mwclient.moneyOrFluMessage(true, true, -1) + " for light infantry.");
        baseTextField.setName("LightInfantryPrice");
        infCbillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        infCbillsSpring.add(new JLabel("Medium Inf Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base " + mwclient.moneyOrFluMessage(true, true, -1) + " for medium infantry.");
        baseTextField.setName("MediumInfantryPrice");
        infCbillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        infCbillsSpring.add(new JLabel("Heavy Inf Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base " + mwclient.moneyOrFluMessage(true, true, -1) + " for heavy infantry.");
        baseTextField.setName("HeavyInfantryPrice");
        infCbillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        infCbillsSpring.add(new JLabel("Assault Inf Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base " + mwclient.moneyOrFluMessage(true, true, -1) + " for assault infantry.");
        baseTextField.setName("AssaultInfantryPrice");
        infCbillsSpring.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(infCbillsSpring, 4, 2);

        baseTextField = new JTextField(5);
        infFluSpring.add(new JLabel("Light Inf Flu:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base flu for light infantry.");
        baseTextField.setName("LightInfantryInf");
        infFluSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        infFluSpring.add(new JLabel("Medium Inf Flu:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base flu for medium infantry.");
        baseTextField.setName("MediumInfantryInf");
        infFluSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        infFluSpring.add(new JLabel("Heavy Inf Flu:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base flu for heavy infantry.");
        baseTextField.setName("HeavyInfantryInf");
        infFluSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        infFluSpring.add(new JLabel("Assault Inf Flu:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base flu for assault infantry.");
        baseTextField.setName("AssaultInfantryInf");
        infFluSpring.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(infFluSpring, 4, 2);

        baseTextField = new JTextField(5);
        infComponentSpring.add(new JLabel("Light Inf PP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base components to make light infantry.");
        baseTextField.setName("LightInfantryPP");
        infComponentSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        infComponentSpring.add(new JLabel("Medium Inf PP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base components to make medium infantry.");
        baseTextField.setName("MediumInfantryPP");
        infComponentSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        infComponentSpring.add(new JLabel("Heavy Inf PP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base components to make heavy infantry.");
        baseTextField.setName("HeavyInfantryPP");
        infComponentSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        infComponentSpring.add(new JLabel("Assault Inf PP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base components to make assault infantry.");
        baseTextField.setName("AssaultInfantryPP");
        infComponentSpring.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(infComponentSpring, 4, 2);

        // set up the Misc. spring
        baseTextField = new JTextField(5);
        unitsMiscSpring.add(new JLabel("Max Armies:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Max # of armies a unit can join");
        baseTextField.setName("UnitsInMultipleArmiesAmount");
        unitsMiscSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        unitsMiscSpring.add(new JLabel("Cost Multiplier:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>This is set to lower or raise the calculated cost.<br>i.e. cost is 10 mil for a unit .1(10%) will set it to 1 mil.</html>");
        baseTextField.setName("CostModifier");
        unitsMiscSpring.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(unitsMiscSpring, 4);

        // unit cboxes
        BaseCheckBox = new JCheckBox("Use Vehs");

        BaseCheckBox.setToolTipText("Uncheck to disable Vehs.");
        BaseCheckBox.setName("UseVehicle");
        unitCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Use Inf");

        BaseCheckBox.setToolTipText("Uncheck to disable Infantry.");
        BaseCheckBox.setName("UseInfantry");
        unitCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Light Inf");

        BaseCheckBox.setToolTipText("Check to have all inf count as light.");
        BaseCheckBox.setName("UseOnlyLightInfantry");
        unitCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Light Vehs");

        BaseCheckBox.setToolTipText("Check to have all vehs count as light.");
        BaseCheckBox.setName("UseOnlyOneVehicleSize");
        unitCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Free Foot");

        BaseCheckBox.setToolTipText("Check to have Foot Inf take 0 techs/bays");
        BaseCheckBox.setName("FootInfTakeNoBays");
        unitCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Use BA");

        BaseCheckBox.setToolTipText("Uncheck to disable BattleArmor.");
        BaseCheckBox.setName("UseBattleArmor");
        unitCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Use Proto");

        BaseCheckBox.setToolTipText("Uncheck to disable ProtoMeks.");
        BaseCheckBox.setName("UseProtoMek");
        unitCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Use Aero");

        BaseCheckBox.setToolTipText("Uncheck to disable Aero.");
        BaseCheckBox.setName("UseAero");
        unitCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Real Cost");

        BaseCheckBox.setToolTipText("<html>Check to use MM calculated costs for each unit.<br>The calculated cost will be used or the unit set price which ever is higher<br>Requires a reboot of the server.</html>");
        BaseCheckBox.setName("UseCalculatedCosts");
        unitCBoxGrid.add(BaseCheckBox);

        // finalize the layout
        JPanel unitBox = new JPanel();
        unitBox.setLayout(new BoxLayout(unitBox, BoxLayout.Y_AXIS));

        unitBox.add(unitSpringGrid);
        unitBox.add(unitsMiscSpring);
        unitBox.add(unitCBoxGrid);

        unitPanel.add(unitBox);

        // Units 2
        // 3x2 grod of springs filled w/ component costs
        JPanel protoCbillsSpring = new JPanel(new SpringLayout());
        JPanel protoFluSpring = new JPanel(new SpringLayout());
        JPanel protoComponentSpring = new JPanel(new SpringLayout());
        JPanel baCbillsSpring = new JPanel(new SpringLayout());
        JPanel baFluSpring = new JPanel(new SpringLayout());
        JPanel baComponentSpring = new JPanel(new SpringLayout());
        JPanel aeroCbillsSpring = new JPanel(new SpringLayout());
        JPanel aeroFluSpring = new JPanel(new SpringLayout());
        JPanel aeroComponentSpring = new JPanel(new SpringLayout());
        JPanel unitCommanderSpring = new JPanel(new SpringLayout());

        JPanel unit2SpringGrid = new JPanel(new GridLayout(2, 3));
        unit2SpringGrid.add(protoCbillsSpring);
        unit2SpringGrid.add(protoFluSpring);
        unit2SpringGrid.add(protoComponentSpring);

        unit2SpringGrid.add(baCbillsSpring);
        unit2SpringGrid.add(baFluSpring);
        unit2SpringGrid.add(baComponentSpring);

        unit2SpringGrid.add(aeroCbillsSpring);
        unit2SpringGrid.add(aeroFluSpring);
        unit2SpringGrid.add(aeroComponentSpring);

        baseTextField = new JTextField(5);
        protoCbillsSpring.add(new JLabel("Light Proto Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base " + mwclient.moneyOrFluMessage(true, true, -1) + " for light proto.");
        baseTextField.setName("LightProtoMekPrice");
        protoCbillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        protoCbillsSpring.add(new JLabel("Medium Proto Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base " + mwclient.moneyOrFluMessage(true, true, -1) + " for medium proto.");
        baseTextField.setName("MediumProtoMekPrice");
        protoCbillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        protoCbillsSpring.add(new JLabel("Heavy Proto Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base " + mwclient.moneyOrFluMessage(true, true, -1) + " for heavy proto.");
        baseTextField.setName("HeavyProtoMekPrice");
        protoCbillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        protoCbillsSpring.add(new JLabel("Assault Proto Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base " + mwclient.moneyOrFluMessage(true, true, -1) + " for assault proto.");
        baseTextField.setName("AssaultProtoMekPrice");
        protoCbillsSpring.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(protoCbillsSpring, 4, 2);

        baseTextField = new JTextField(5);
        protoFluSpring.add(new JLabel("Light Proto Flu:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base flu for light proto.");
        baseTextField.setName("LightProtoMekInf");
        protoFluSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        protoFluSpring.add(new JLabel("Medium Proto Flu:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base flu for medium proto.");
        baseTextField.setName("MediumProtoMekInf");
        protoFluSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        protoFluSpring.add(new JLabel("Heavy Proto Flu:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base flu for heavy proto.");
        baseTextField.setName("HeavyProtoMekInf");
        protoFluSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        protoFluSpring.add(new JLabel("Assault Proto Flu:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base flu for assault proto.");
        baseTextField.setName("AssaultProtoMekInf");
        protoFluSpring.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(protoFluSpring, 4, 2);

        baseTextField = new JTextField(5);
        protoComponentSpring.add(new JLabel("Light Proto PP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base components to make light proto.");
        baseTextField.setName("LightProtoMekPP");
        protoComponentSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        protoComponentSpring.add(new JLabel("Medium Proto PP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base components to make medium proto.");
        baseTextField.setName("MediumProtoMekPP");
        protoComponentSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        protoComponentSpring.add(new JLabel("Heavy Proto PP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base components to make heavy proto.");
        baseTextField.setName("HeavyProtoMekPP");
        protoComponentSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        protoComponentSpring.add(new JLabel("Assault Proto PP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base components to make assault proto.");
        baseTextField.setName("AssaultProtoMekPP");
        protoComponentSpring.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(protoComponentSpring, 4, 2);

        baseTextField = new JTextField(5);
        baCbillsSpring.add(new JLabel("Light BA Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base " + mwclient.moneyOrFluMessage(true, true, -1) + " for light ba.");
        baseTextField.setName("LightBattleArmorPrice");
        baCbillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        baCbillsSpring.add(new JLabel("Medium BA Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base " + mwclient.moneyOrFluMessage(true, true, -1) + " for medium ba.");
        baseTextField.setName("MediumBattleArmorPrice");
        baCbillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        baCbillsSpring.add(new JLabel("Heavy BA Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base " + mwclient.moneyOrFluMessage(true, true, -1) + " for heavy ba.");
        baseTextField.setName("HeavyBattleArmorPrice");
        baCbillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        baCbillsSpring.add(new JLabel("Assault BA Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base " + mwclient.moneyOrFluMessage(true, true, -1) + " for assault ba.");
        baseTextField.setName("AssaultBattleArmorPrice");
        baCbillsSpring.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(baCbillsSpring, 4, 2);

        baseTextField = new JTextField(5);
        baFluSpring.add(new JLabel("Light BA Flu:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base flu for light battlearmor.");
        baseTextField.setName("LightBattleArmorInf");
        baFluSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        baFluSpring.add(new JLabel("Medium BA Flu:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base flu for medium battlearmor.");
        baseTextField.setName("MediumBattleArmorInf");
        baFluSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        baFluSpring.add(new JLabel("Heavy BA Flu:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base flu for heavy battlearmor.");
        baseTextField.setName("HeavyBattleArmorInf");
        baFluSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        baFluSpring.add(new JLabel("Assault BA Flu:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base flu for assault battlearmor.");
        baseTextField.setName("AssaultBattleArmorInf");
        baFluSpring.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(baFluSpring, 4, 2);

        baseTextField = new JTextField(5);
        baComponentSpring.add(new JLabel("Light BA PP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base components to make light battlearmor.");
        baseTextField.setName("LightBattleArmorPP");
        baComponentSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        baComponentSpring.add(new JLabel("Medium BA PP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base components to make medium battlearmor.");
        baseTextField.setName("MediumBattleArmorPP");
        baComponentSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        baComponentSpring.add(new JLabel("Heavy BA PP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base components to make heavy battlearmor.");
        baseTextField.setName("HeavyBattleArmorPP");
        baComponentSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        baComponentSpring.add(new JLabel("Assault BA PP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base components to make assault battlearmor.");
        baseTextField.setName("AssaultBattleArmorPP");
        baComponentSpring.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(baComponentSpring, 4, 2);

        baseTextField = new JTextField(5);
        aeroCbillsSpring.add(new JLabel("Light Aero Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base " + mwclient.moneyOrFluMessage(true, true, -1) + " for light aero.");
        baseTextField.setName("LightAeroPrice");
        aeroCbillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        aeroCbillsSpring.add(new JLabel("Medium Aero Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base " + mwclient.moneyOrFluMessage(true, true, -1) + " for medium aero.");
        baseTextField.setName("MediumAeroPrice");
        aeroCbillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        aeroCbillsSpring.add(new JLabel("Heavy Aero Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base " + mwclient.moneyOrFluMessage(true, true, -1) + " for heavy aero.");
        baseTextField.setName("HeavyAeroPrice");
        aeroCbillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        aeroCbillsSpring.add(new JLabel("Assault Aero Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base " + mwclient.moneyOrFluMessage(true, true, -1) + " for assault aero.");
        baseTextField.setName("AssaultAeroPrice");
        aeroCbillsSpring.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(aeroCbillsSpring, 4, 2);

        baseTextField = new JTextField(5);
        aeroFluSpring.add(new JLabel("Light Aero Flu:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base flu for light aero.");
        baseTextField.setName("LightAeroInf");
        aeroFluSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        aeroFluSpring.add(new JLabel("Medium Aero Flu:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base flu for medium aero.");
        baseTextField.setName("MediumAeroInf");
        aeroFluSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        aeroFluSpring.add(new JLabel("Heavy Aero Flu:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base flu for heavy aero.");
        baseTextField.setName("HeavyAeroInf");
        aeroFluSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        aeroFluSpring.add(new JLabel("Assault Aero Flu:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base flu for assault aero.");
        baseTextField.setName("AssaultAeroInf");
        aeroFluSpring.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(aeroFluSpring, 4, 2);

        baseTextField = new JTextField(5);
        aeroComponentSpring.add(new JLabel("Light Aero PP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base components to make light aero.");
        baseTextField.setName("LightAeroPP");
        aeroComponentSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        aeroComponentSpring.add(new JLabel("Medium Aero PP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base components to make medium aero.");
        baseTextField.setName("MediumAeroPP");
        aeroComponentSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        aeroComponentSpring.add(new JLabel("Heavy Aero PP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base components to make heavy aero.");
        baseTextField.setName("HeavyAeroPP");
        aeroComponentSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        aeroComponentSpring.add(new JLabel("Assault Aero PP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base components to make assault aero.");
        baseTextField.setName("AssaultAeroPP");
        aeroComponentSpring.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(aeroComponentSpring, 4, 2);
        // flow layout of labels and text boxes w/ non-original multipliers
        JPanel unit2TextFlow = new JPanel();

        baseTextField = new JTextField(5);
        unit2TextFlow.add(new JLabel("NonOrig Money Multi:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>" + "Cost multiplier for units purchased from a factory not originally<br>" + "owned by purchasing player's faction. Examples:<br>" + "1: 80 CBill Faction Base * 1.15 CBillMultiplier = 92 CBill final cost.<br>" + "2: 80 CBill Faction Base * 1.00 CBillMultiplier = 80 CBill final cost.<br>" + "3: 80 CBill Faction Base * 0.75 CBillMultiplier = 60 CBill final cost.</html>");
        baseTextField.setName("NonOriginalCBillMultiplier");
        unit2TextFlow.add(baseTextField);

        baseTextField = new JTextField(5);
        unit2TextFlow.add(new JLabel("NonOrig Flu Multi:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>" + "Flu price multiplier for units purchased from a factory not originally<br>" + "owned by purchasing player's faction. See Money Multi for examples.</html>");
        baseTextField.setName("NonOriginalInfluenceMultiplier");
        unit2TextFlow.add(baseTextField);

        baseTextField = new JTextField(5);
        unit2TextFlow.add(new JLabel("NonOrig PP Multi:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>" + "Component use multiplier for units purchased from a factory not originally<br>" + "owned by purchasing player's faction. See Money Multi for examples.</html>");
        baseTextField.setName("NonOriginalComponentMultiplier");
        unit2TextFlow.add(baseTextField);

        JPanel unit3TextFlow = new JPanel();

        baseTextField = new JTextField(5);
        unit3TextFlow.add(new JLabel("Light Type:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Title to be displayed of light factories<br>in the Client House Bays Tab</html>");
        baseTextField.setName("LightFactoryTypeTitle");
        unit3TextFlow.add(baseTextField);

        baseTextField = new JTextField(5);
        unit3TextFlow.add(new JLabel("Medium Type:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Title to be displayed of medium factories<br>in the Client House Bays Tab</html>");
        baseTextField.setName("MediumFactoryTypeTitle");
        unit3TextFlow.add(baseTextField);

        baseTextField = new JTextField(5);
        unit3TextFlow.add(new JLabel("Heavy Type:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Title to be displayed of heavy factories<br>in the Client House Bays Tab</html>");
        baseTextField.setName("HeavyFactoryTypeTitle");
        unit3TextFlow.add(baseTextField);

        baseTextField = new JTextField(5);
        unit3TextFlow.add(new JLabel("Assault Type:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Title to be displayed of assault factories<br>in the Client House Bays Tab</html>");
        baseTextField.setName("AssaultFactoryTypeTitle");
        unit3TextFlow.add(baseTextField);

        JPanel unit4TextFlow = new JPanel();

        baseTextField = new JTextField(5);
        unit4TextFlow.add(new JLabel("Mek Class:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Title to be displayed of mek factories<br>in the Client House Bays Tab</html>");
        baseTextField.setName("MekFactoryClassTitle");
        unit4TextFlow.add(baseTextField);

        baseTextField = new JTextField(5);
        unit4TextFlow.add(new JLabel("Vee Class:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Title to be displayed of vee factories<br>in the Client House Bays Tab</html>");
        baseTextField.setName("VehicleFactoryClassTitle");
        unit4TextFlow.add(baseTextField);

        baseTextField = new JTextField(5);
        unit4TextFlow.add(new JLabel("Inf Class:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Title to be displayed of infantry factories<br>in the Client House Bays Tab</html>");
        baseTextField.setName("InfantryFactoryClassTitle");
        unit4TextFlow.add(baseTextField);

        baseTextField = new JTextField(5);
        unit4TextFlow.add(new JLabel("Proto Class:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Title to be displayed of ProtoMek factories<br>in the Client House Bays Tab</html>");
        baseTextField.setName("ProtoMekFactoryClassTitle");
        unit4TextFlow.add(baseTextField);

        baseTextField = new JTextField(5);
        unit4TextFlow.add(new JLabel("BA Class:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Title to be displayed of battlearmor factories<br>in the Client House Bays Tab</html>");
        baseTextField.setName("BattleArmorFactoryClassTitle");
        unit4TextFlow.add(baseTextField);

        baseTextField = new JTextField(5);
        unit4TextFlow.add(new JLabel("Aero Class:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Title to be displayed of aero factories<br>in the Client House Bays Tab</html>");
        baseTextField.setName("AeroFactoryClassTitle");
        unit4TextFlow.add(baseTextField);

        BaseCheckBox = new JCheckBox("Allow Mek Commanders");
        BaseCheckBox.setToolTipText("<html>Allow meks to be set as unit commanders<br>for the kill all unit commanders operation victory condition</html>");
        BaseCheckBox.setName("allowUnitCommanderMek");
        unitCommanderSpring.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Allow Vee Commanders");
        BaseCheckBox.setToolTipText("<html>Allow vehicles to be set as unit commanders<br>for the kill all unit commanders operation victory condition</html>");
        BaseCheckBox.setName("allowUnitCommanderVehicle");
        unitCommanderSpring.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Allow Inf Commanders");
        BaseCheckBox.setToolTipText("<html>Allow infantry to be set as unit commanders<br>for the kill all unit commanders operation victory condition</html>");
        BaseCheckBox.setName("allowUnitCommanderInfantry");
        unitCommanderSpring.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Allow Proto Commanders");
        BaseCheckBox.setToolTipText("<html>Allow protomeks to be set as unit commanders<br>for the kill all unit commanders operation victory condition</html>");
        BaseCheckBox.setName("allowUnitCommanderProtoMek");
        unitCommanderSpring.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Allow BA Commanders");
        BaseCheckBox.setToolTipText("<html>Allow battlearmor to be set as unit commanders<br>for the kill all unit commanders operation victory condition</html>");
        BaseCheckBox.setName("allowUnitCommanderBattleArmor");
        unitCommanderSpring.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Allow VTOL Commanders");
        BaseCheckBox.setToolTipText("<html>Allow VTOL to be set as unit commanders<br>for the kill all unit commanders operation victory condition</html>");
        BaseCheckBox.setName("allowUnitCommanderVTOL");
        unitCommanderSpring.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Allow Aero Commanders");
        BaseCheckBox.setToolTipText("<html>Allow aero to be set as unit commanders<br>for the kill all unit commanders operation victory condition</html>");
        BaseCheckBox.setName("allowUnitCommanderAero");
        unitCommanderSpring.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Allow Fighting Without Commanders");
        BaseCheckBox.setToolTipText("<html>Allow players to go active without any unit commanders set in their armies<br>for the kill all unit commanders operation victory condition</html>");
        BaseCheckBox.setName("allowGoingActiveWithoutUnitCommanders");
        unitCommanderSpring.add(BaseCheckBox);
        SpringLayoutHelper.setupSpringGrid(unitCommanderSpring, 3);

        // build complete panel, wrapped in box
        JPanel unit2Box = new JPanel();
        unit2Box.setLayout(new BoxLayout(unit2Box, BoxLayout.Y_AXIS));
        unit2Box.add(unit2SpringGrid);
        unit2Box.add(unit2TextFlow);
        unit2Box.add(unit3TextFlow);
        unit2Box.add(unit4TextFlow);
        unit2Box.add(unitCommanderSpring);

        unit2Panel.add(unit2Box);

        /*
         * CONSTRUCT MEZZO/Pricemod PANEL
         */
        JPanel MekSpring = new JPanel(new SpringLayout());
        JPanel VehicleSpring = new JPanel(new SpringLayout());
        JPanel InfantrySpring = new JPanel(new SpringLayout());
        JPanel BattleArmorSpring = new JPanel(new SpringLayout());
        JPanel ProtoMekSpring = new JPanel(new SpringLayout());
        JPanel AeroSpring = new JPanel(new SpringLayout());

        JPanel buySellSpring = new JPanel(new SpringLayout());

        baseTextField = new JTextField(5);
        MekSpring.add(new JLabel("Light Mek Pricemod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount added to pricemod to direct sell a Mek.");
        baseTextField.setName("SellDirectLightMekPrice");
        MekSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        MekSpring.add(new JLabel("Medium Mek Pricemod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount added to pricemod to direct sell a Mek.");
        baseTextField.setName("SellDirectMediumMekPrice");
        MekSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        MekSpring.add(new JLabel("Heavy Mek Pricemod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount added to pricemod to direct sell a Mek.");
        baseTextField.setName("SellDirectHeavyMekPrice");
        MekSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        MekSpring.add(new JLabel("Assault Mek Pricemod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount added to pricemod to direct sell a Mek.");
        baseTextField.setName("SellDirectAssaultMekPrice");
        MekSpring.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(MekSpring, 4, 2);

        baseTextField = new JTextField(5);
        VehicleSpring.add(new JLabel("Light Vehicle Pricemod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount added to pricemod to direct sell a Vehicle.");
        baseTextField.setName("SellDirectLightVehiclePrice");
        VehicleSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        VehicleSpring.add(new JLabel("Medium Vehicle Pricemod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount added to pricemod to direct sell a Vehicle.");
        baseTextField.setName("SellDirectMediumVehiclePrice");
        VehicleSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        VehicleSpring.add(new JLabel("Heavy Vehicle Pricemod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount added to pricemod to direct sell a Vehicle.");
        baseTextField.setName("SellDirectHeavyVehiclePrice");
        VehicleSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        VehicleSpring.add(new JLabel("Assault Vehicle Pricemod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount added to pricemod to direct sell a Vehicle.");
        baseTextField.setName("SellDirectAssaultVehiclePrice");
        VehicleSpring.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(VehicleSpring, 4, 2);

        baseTextField = new JTextField(5);
        InfantrySpring.add(new JLabel("Light Infantry Pricemod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount added to pricemod to direct sell a Infantry.");
        baseTextField.setName("SellDirectLightInfantryPrice");
        InfantrySpring.add(baseTextField);

        baseTextField = new JTextField(5);
        InfantrySpring.add(new JLabel("Medium Infantry Pricemod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount added to pricemod to direct sell a Infantry.");
        baseTextField.setName("SellDirectMediumInfantryPrice");
        InfantrySpring.add(baseTextField);

        baseTextField = new JTextField(5);
        InfantrySpring.add(new JLabel("Heavy Infantry Pricemod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount added to pricemod to direct sell a Infantry.");
        baseTextField.setName("SellDirectHeavyInfantryPrice");
        InfantrySpring.add(baseTextField);

        baseTextField = new JTextField(5);
        InfantrySpring.add(new JLabel("Assault Infantry Pricemod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount added to pricemod to direct sell a Infantry.");
        baseTextField.setName("SellDirectAssaultInfantryPrice");
        InfantrySpring.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(InfantrySpring, 4, 2);

        baseTextField = new JTextField(5);
        BattleArmorSpring.add(new JLabel("Light BattleArmor Pricemod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount added to pricemod to direct sell a BattleArmor.");
        baseTextField.setName("SellDirectLightBattleArmorPrice");
        BattleArmorSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        BattleArmorSpring.add(new JLabel("Medium BattleArmor Pricemod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount added to pricemod to direct sell a BattleArmor.");
        baseTextField.setName("SellDirectMediumBattleArmorPrice");
        BattleArmorSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        BattleArmorSpring.add(new JLabel("Heavy BattleArmor Pricemod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount added to pricemod to direct sell a BattleArmor.");
        baseTextField.setName("SellDirectHeavyBattleArmorPrice");
        BattleArmorSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        BattleArmorSpring.add(new JLabel("Assault BattleArmor Pricemod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount added to pricemod to direct sell a BattleArmor.");
        baseTextField.setName("SellDirectAssaultBattleArmorPrice");
        BattleArmorSpring.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(BattleArmorSpring, 4, 2);

        baseTextField = new JTextField(5);
        AeroSpring.add(new JLabel("Light Aero Pricemod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount added to pricemod to direct sell a Aero.");
        baseTextField.setName("SellDirectLightAeroPrice");
        AeroSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        AeroSpring.add(new JLabel("Medium Aero Pricemod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount added to pricemod to direct sell a Aero.");
        baseTextField.setName("SellDirectMediumAeroPrice");
        AeroSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        AeroSpring.add(new JLabel("Heavy Aero Pricemod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount added to pricemod to direct sell a Aero.");
        baseTextField.setName("SellDirectHeavyAeroPrice");
        AeroSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        AeroSpring.add(new JLabel("Assault Aero Pricemod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount added to pricemod to direct sell a Aero.");
        baseTextField.setName("SellDirectAssaultAeroPrice");
        AeroSpring.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(AeroSpring, 4, 2);

        baseTextField = new JTextField(5);
        ProtoMekSpring.add(new JLabel("Light ProtoMek Pricemod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount added to pricemod to direct sell a ProtoMek.");
        baseTextField.setName("SellDirectLightProtoMekPrice");
        ProtoMekSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        ProtoMekSpring.add(new JLabel("Medium ProtoMek Pricemod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount added to pricemod to direct sell a ProtoMek.");
        baseTextField.setName("SellDirectMediumProtoMekPrice");
        ProtoMekSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        ProtoMekSpring.add(new JLabel("Heavy ProtoMek Pricemod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount added to pricemod to direct sell a ProtoMek.");
        baseTextField.setName("SellDirectHeavyProtoMekPrice");
        ProtoMekSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        ProtoMekSpring.add(new JLabel("Assault ProtoMek Pricemod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount added to pricemod to direct sell a ProtoMek.");
        baseTextField.setName("SellDirectAssaultProtoMekPrice");
        ProtoMekSpring.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(ProtoMekSpring, 4, 2);

        buySellSpring.add(MekSpring);
        buySellSpring.add(VehicleSpring);
        buySellSpring.add(InfantrySpring);
        buySellSpring.add(BattleArmorSpring);
        buySellSpring.add(ProtoMekSpring);
        buySellSpring.add(AeroSpring);

        SpringLayoutHelper.setupSpringGrid(buySellSpring, 2, 3);

        JPanel buySellSpring2 = new JPanel();
        buySellSpring2.setLayout(new BoxLayout(buySellSpring2, BoxLayout.Y_AXIS));

        // finalize layout
        BaseCheckBox = new JCheckBox("Use Direct Sell");
        BaseCheckBox.setName("UseDirectSell");

        buySellSpring2.add(BaseCheckBox);
        buySellSpring2.add(buySellSpring);

        // SpringLayoutHelper.setupSpringGrid(buySellSpring2 , 1, 3);
        directSellPanel.add(buySellSpring2);

        /*
         * FACTION TAB CONSTRUCTION
         */
        JPanel factionSpring1 = new JPanel(new SpringLayout());
        JPanel factionSpring2 = new JPanel(new SpringLayout());

        // faction spring #1 -- mostly SOL things
        baseTextField = new JTextField(5);
        factionSpring1.add(new JLabel("Starting " + mwclient.moneyOrFluMessage(true, true, -1) + ":", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Number of " + mwclient.moneyOrFluMessage(true, true, -1) + " given to a new SOL player");
        baseTextField.setName("PlayerBaseMoney");
        factionSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
        factionSpring1.add(new JLabel("Max SOL XP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Max amount of XP a player can earn in SOL");
        baseTextField.setName("MaxSOLExp");
        factionSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
        factionSpring1.add(new JLabel("Max SOL CBills:", SwingConstants.TRAILING));
        baseTextField.setName("MaxSOLCBills");
        factionSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
        factionSpring1.add(new JLabel("Min XP to Defect:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Min XP needed to leave SOL");
        baseTextField.setName("MinEXPforDefecting");
        factionSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
        factionSpring1.add(new JLabel("XP per House Rank:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>" + "Multiplier used to determine how much additional XP a SOL<br>" + "player must earn to join a low ranked faction. XP to Defect is<br>" + "[Min To Defect] + [Rank of Target House * XP per faction Rank]</HTML>");
        baseTextField.setName("EXPNeededPerHouseRank");
        factionSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
        factionSpring1.add(new JLabel("Min House Techs:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>" + "Minimum number of faction techs. Assigned to player<br>" + "*if* faction supplied bays are lower than the min.");
        baseTextField.setName("MinimumHouseBays");
        factionSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
        factionSpring1.add(new JLabel("Newbie Techs:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Number of faction techs given to SOL players");
        baseTextField.setName("NewbieHouseBays");
        factionSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
        factionSpring1.add(new JLabel("Merc Techs:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Number of faction techs given to mercenary players");
        baseTextField.setName("MercHouseBays");
        factionSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
        factionSpring1.add(new JLabel("EXP for Bay:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount of experience a player needs to gain 1 bay.");
        baseTextField.setName("ExperienceForBay");
        factionSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
        factionSpring1.add(new JLabel("Max Bays from EXP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Maximum number of bays a player may get from experience.");
        baseTextField.setName("MaxBaysFromEXP");
        factionSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
        factionSpring1.add(new JLabel("Donations Allowed:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Number of donations players are allowed each tick.");
        baseTextField.setName("DonationsAllowed");
        factionSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
        factionSpring1.add(new JLabel("Scraps Allowed:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Number of scraps players are allowed each tick.");
        baseTextField.setName("ScrapsAllowed");
        factionSpring1.add(baseTextField);
        baseTextField = new JTextField(5);

        factionSpring1.add(new JLabel("Max MOTD Length:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Max number of characters allowed in the MOTD.");
        baseTextField.setName("MaxMOTDLength");
        factionSpring1.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(factionSpring1, 2);

        // faction spring #2
        baseTextField = new JTextField(5);
        factionSpring2.add(new JLabel("Medium XP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("XP required to buy medium units");
        baseTextField.setName("MinEXPforMedium");
        factionSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        factionSpring2.add(new JLabel("Heavy XP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("XP required to buy heavy units");
        baseTextField.setName("MinEXPforHeavy");
        factionSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        factionSpring2.add(new JLabel("Assault XP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("XP required to buy assault units");
        baseTextField.setName("MinEXPforAssault");
        factionSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        factionSpring2.add(new JLabel("Welfare Ceiling:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Max # of Cbills a player can have to collect welfare");
        baseTextField.setName("WelfareCeiling");
        factionSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        factionSpring2.add(new JLabel("Total Hangar BV for Welfare:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("The Max BV a player can have to collect welfare.");
        baseTextField.setName("WelfareTotalUnitBVCeiling");
        factionSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        factionSpring2.add(new JLabel("Cost Multi @ Donate:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>" + "Cost to donate a unit is determined by multiplying the faction's base purchase<br>" + "cost by this value. Negative numbers will pay the player.<br>" + "Example: Purchase cost of 100 CBills * Multi of .25 = Pay 25 CB to donate.<br>" + "Example: Purchase cost of 50 CBills * Multi of 0 = Free to donate.<br>" + "Example: Purchase cost of 80 CBills * Multi of -.10 = Get paid 8 CB.</html>");
        baseTextField.setName("DonationCostMultiplier");
        factionSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        factionSpring2.add(new JLabel("Cost Multi to Buy Used:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>" + "Cost to purchase a used unit, determined by multiplying a unit's new purchase<br>" + "cost by this value. Final cost cannot be negative.<br>" + "Example: New cost of 100 CBills * Multi of .50 = Costs 50 to buy used.<br>" + "Example: Purchase cost of 50 CBills * Multi of 0 = Free to buy used.</html>");
        baseTextField.setName("UsedPurchaseCostMulti");
        factionSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        factionSpring2.add(new JLabel("Cost Multi @ Scrap:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>" + "Cost to scrap a unit is determined by multiplying the faction's base purchase<br>" + "cost by this value. Negative numbers will pay the player. This value is used for<br>" + "all units if AR is off, and for fully repaired meks if AR is on." + "Example: Purchase cost of 100 CBills * Multi of .50 = Pay 50 CB to scrap.<br>" + "Example: Purchase cost of 75 CBills * Multi of 0.0 = Free to scrap.<br>" + "Example: Purchase cost of 80 CBills * Multi of -.25 = Get paid 20 CB.</html>");
        baseTextField.setName("ScrapCostMultiplier");
        factionSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        factionSpring2.add(new JLabel("Armor Scrap Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Float field, AR Only<br>Percent of a unit's buy price to charge someone for scrapping a unit with minor armor damage<br>Negative number will give money to the player<br>.1 = 10%</html>");
        baseTextField.setName("CostToScrapOnlArmorDamage");
        factionSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        factionSpring2.add(new JLabel("Critical Scrap Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Float field, AR Only<br>Percent of a unit's buy price to charge someone for scrapping a unit with damaged criticals<br>Negative number will give money to the player<br>.1 = 10%</html>");
        baseTextField.setName("CostToScrapCriticallyDamaged");
        factionSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        factionSpring2.add(new JLabel("Engined:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Float field, AR Only<br>Percent of a unit's buy price to charge someone for scrapping an engined unit<br>Negative number will give money to the player<br>.1 = 10%</html>");
        baseTextField.setName("CostToScrapEngined");
        factionSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        factionSpring2.add(new JLabel("Leader Level:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Integer Field, Access Level given to a player when they are promoted to the faction leadership<br>NOTE: if their access level is already higher then this it will not be changed.</html>");
        baseTextField.setName("factionLeaderLevel");
        factionSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        factionSpring2.add(new JLabel("Days Between Promotions:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Integer Field, How many days a player has to wait before they can be promoted again<br>after their last promotion/demotion.</html>");
        baseTextField.setName("daysbetweenpromotions");
        factionSpring2.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(factionSpring2, 2);

        // setup CBoxes
        JPanel factionCBoxSpring = new JPanel(new SpringLayout());

        BaseCheckBox = new JCheckBox("Donate @ Unenroll");
        BaseCheckBox.setToolTipText("<html>If checked, players that unenroll will donate<br>all their units to the house bays.</html>");
        BaseCheckBox.setName("DonateUnitsUponUnenrollment");
        factionCBoxSpring.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Faction Names on Games");
        BaseCheckBox.setToolTipText("<html>If checked, faction names will replace player names in<br>completed game descriptions.</html>");
        BaseCheckBox.setName("ShowCompleteGameInfoOnTick");
        factionCBoxSpring.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Player Names in News");
        BaseCheckBox.setToolTipText("<html>If checked, player names will replace faction names in<br>news feed description of games.</html>");
        BaseCheckBox.setName("ShowCompleteGameInfoInNews");
        factionCBoxSpring.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Auto Promote Sub Factions");
        BaseCheckBox.setToolTipText("<html>If checked, a player will be automatically promoted<br>to the next higher sub faction,<br>if they are qualified.</html>");
        BaseCheckBox.setName("autoPromoteSubFaction");
        factionCBoxSpring.add(BaseCheckBox);

        SpringLayoutHelper.setupSpringGrid(factionCBoxSpring, 3);

        // finalize the layout
        JPanel factionBox = new JPanel();
        factionBox.setLayout(new BoxLayout(factionBox, BoxLayout.Y_AXIS));
        JPanel factionSpringFlow = new JPanel();
        factionSpringFlow.add(factionSpring1);
        factionSpringFlow.add(factionSpring2);
        factionBox.add(factionSpringFlow);
        factionBox.add(factionCBoxSpring);
        factionPanel.add(factionBox);

        /*
         * VOTE PANEL CONSTRUCTION
         */
        JPanel voteBoxPanel = new JPanel();
        voteBoxPanel.setLayout(new BoxLayout(voteBoxPanel, BoxLayout.Y_AXIS));
        JPanel voteSpring = new JPanel(new SpringLayout());

        // set up voting CBox
        BaseCheckBox = new JCheckBox("Enable Voting");

        BaseCheckBox.setToolTipText("If checked, players are able to cast votes.");
        BaseCheckBox.setName("VotingEnabled");
        voteBoxPanel.add(BaseCheckBox);

        // set up vote spring
        baseTextField = new JTextField(5);
        voteSpring.add(new JLabel("Base Votes:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Starting number of votes");
        baseTextField.setName("StartingVotes");
        voteSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        voteSpring.add(new JLabel("XP For Vote:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount of XP required to earn an additional vote");
        baseTextField.setName("XPForAdditionalVote");
        voteSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        voteSpring.add(new JLabel("Max Votes:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Maximum number of votes a player can have");
        baseTextField.setName("MaximumVotes");
        voteSpring.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(voteSpring, 3, 2);

        // finalize the layout

        voteBoxPanel.add(voteSpring);
        votingPanel.add(voteBoxPanel);

        /*
         * PRODUCTION/FACTORY PANEL CONSTRUCTION
         */
        JPanel refreshSpringPanel = new JPanel(new SpringLayout());
        JPanel salesSpringPanel = new JPanel(new SpringLayout());
        JPanel apSpringPanel = new JPanel(new SpringLayout());
        JPanel prodMiscPanel = new JPanel(new SpringLayout());
        JPanel prodCBoxSpring = new JPanel(new SpringLayout());
        JPanel prodCrit = new JPanel(new SpringLayout());

        // refresh spring
        baseTextField = new JTextField(5);
        refreshSpringPanel.add(new JLabel("Light Refresh:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Miniticks to refresh a light factory");
        baseTextField.setName("LightRefresh");
        refreshSpringPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        refreshSpringPanel.add(new JLabel("Medium Refresh:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Miniticks to refresh a medium factory");
        baseTextField.setName("MediumRefresh");
        refreshSpringPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        refreshSpringPanel.add(new JLabel("Heavy Refresh:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Miniticks to refresh a heavy factory");
        baseTextField.setName("HeavyRefresh");
        refreshSpringPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        refreshSpringPanel.add(new JLabel("Assault Refresh:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Miniticks to refresh a assault factory");
        baseTextField.setName("AssaultRefresh");
        refreshSpringPanel.add(baseTextField);

        // sales spring
        baseTextField = new JTextField(5);
        salesSpringPanel.add(new JLabel("Light Sale Time:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Length of faction bm sale of light unit, in ticks");
        baseTextField.setName("LightSaleTicks");
        salesSpringPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        salesSpringPanel.add(new JLabel("Medium Sale Time:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Length of faction bm sale of medium unit, in ticks");
        baseTextField.setName("MediumSaleTicks");
        salesSpringPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        salesSpringPanel.add(new JLabel("Heavy Sale Time:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Length of faction bm sale of heavy unit, in ticks");
        baseTextField.setName("HeavySaleTicks");
        salesSpringPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        salesSpringPanel.add(new JLabel("Assault Sale Time:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Length of faction bm sale of assault unit, in ticks");
        baseTextField.setName("AssaultSaleTicks");
        salesSpringPanel.add(baseTextField);

        // autoproduction spring
        baseTextField = new JTextField(5);
        apSpringPanel.add(new JLabel("Lights to AP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Number of units worth of stored components to trigger an AP attempt for light units");
        baseTextField.setName("APAtMaxLightUnits");
        apSpringPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        apSpringPanel.add(new JLabel("Mediums to AP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Number of units worth of stored components to trigger an AP attempt for medium units");
        baseTextField.setName("APAtMaxMediumUnits");
        apSpringPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        apSpringPanel.add(new JLabel("Heavies to AP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Number of units worth of stored components to trigger an AP attempt for heavy units");
        baseTextField.setName("APAtMaxHeavyUnits");
        apSpringPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        apSpringPanel.add(new JLabel("Assaults to AP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Number of units worth of stored components to trigger an AP attempt for assault units");
        baseTextField.setName("APAtMaxAssaultUnits");
        apSpringPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        apSpringPanel.add(new JLabel("AP Failure Rate:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("% of autoproduction attempts which fail and destroy components");
        baseTextField.setName("AutoProductionFailureRate");
        apSpringPanel.add(baseTextField);

        // factory misc spring
        baseTextField = new JTextField(5);
        prodMiscPanel.add(new JLabel("Max Light Units:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Max num. of light units, of each type, in factionbays.");
        baseTextField.setName("MaxLightUnits");
        prodMiscPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        prodMiscPanel.add(new JLabel("Max Other Units:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Max num. of non-light units, of each type, in factionbays.");
        baseTextField.setName("MaxOtherUnits");
        prodMiscPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        prodMiscPanel.add(new JLabel("Comp Gain Every:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>" + "Number of ticks which should pass before component gains<br>" + "are aggregated and displayed to a faction. Recommended: 4</HTML>");
        baseTextField.setName("ShowComponentGainEvery");
        prodMiscPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        prodMiscPanel.add(new JLabel("Disputed Planet Color:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Hex color a planet will show up as<br>When no single faction owns more<br>then the minimum amount of land.</html");
        baseTextField.setName("DisputedPlanetColor");
        prodMiscPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        prodMiscPanel.add(new JLabel("Min Planet OwnerShip:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>The Least amount of land a Faction own on a planet to control it");
        baseTextField.setName("MinPlanetOwnerShip");
        prodMiscPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        prodMiscPanel.add(new JLabel("Auto Factory Refresh:", SwingConstants.TRAILING));
        baseTextField.setName("FactoryRefreshPoints");
        prodMiscPanel.add(baseTextField);

        // Check Box Spring
        BaseCheckBox = new JCheckBox();
        prodCBoxSpring.add(new JLabel("Produce w/o factory:", SwingConstants.TRAILING));
        BaseCheckBox.setToolTipText("If checked, components will be produced even if no factory of a type/weightclass is owned");
        BaseCheckBox.setName("ProduceComponentsWithNoFactory");
        prodCBoxSpring.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox();
        prodCBoxSpring.add(new JLabel("Output Multipliers:", SwingConstants.TRAILING));
        BaseCheckBox.setToolTipText("If checked, personal production multipliers will be shown on ticks");
        BaseCheckBox.setName("ShowOutputMultiplierOnTick");
        prodCBoxSpring.add(BaseCheckBox);

        baseTextField = new JTextField(5);
        prodCrit.add(new JLabel("Base Component to Money:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Double Field: Number of Cbills for 1 component</html>");
        baseTextField.setName("BaseComponentToMoneyRatio");
        prodCrit.add(baseTextField);

        baseTextField = new JTextField(5);
        prodCrit.add(new JLabel("Component Mek Mod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Modifer for Mek Components to the base cost</html>");
        baseTextField.setName("ComponentToPartsModifierMek");
        prodCrit.add(baseTextField);

        baseTextField = new JTextField(5);
        prodCrit.add(new JLabel("Component Vehicle Mod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Modifer for Vehicle Components to the base cost</html>");
        baseTextField.setName("ComponentToPartsModifierVehicle");
        prodCrit.add(baseTextField);

        baseTextField = new JTextField(5);
        prodCrit.add(new JLabel("Component Infantry Mod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Modifer for Infantry Components to the base cost</html>");
        baseTextField.setName("ComponentToPartsModifierInfantry");
        prodCrit.add(baseTextField);

        baseTextField = new JTextField(5);
        prodCrit.add(new JLabel("Component BA Mod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Modifer for BA Components to the base cost</html>");
        baseTextField.setName("ComponentToPartsModifierBattleArmor");
        prodCrit.add(baseTextField);

        baseTextField = new JTextField(5);
        prodCrit.add(new JLabel("Component Proto Mod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Modifer for ProtoMek Components to the base cost</html>");
        baseTextField.setName("ComponentToPartsModifierProtoMek");
        prodCrit.add(baseTextField);

        baseTextField = new JTextField(5);
        prodCrit.add(new JLabel("Component Light Mod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Modifer for Light Components to the base cost</html>");
        baseTextField.setName("ComponentToPartsModifierLight");
        prodCrit.add(baseTextField);

        baseTextField = new JTextField(5);
        prodCrit.add(new JLabel("Component Medium Mod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Modifer for Medium Components to the base cost</html>");
        baseTextField.setName("ComponentToPartsModifierMedium");
        prodCrit.add(baseTextField);

        baseTextField = new JTextField(5);
        prodCrit.add(new JLabel("Component Heavy Mod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Modifer for Heavy Components to the base cost</html>");
        baseTextField.setName("ComponentToPartsModifierHeavy");
        prodCrit.add(baseTextField);

        baseTextField = new JTextField(5);
        prodCrit.add(new JLabel("Component Assault Mod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Modifer for Assault Components to the base cost</html>");
        baseTextField.setName("ComponentToPartsModifierAssault");
        prodCrit.add(baseTextField);

        // lay out the springs
        SpringLayoutHelper.setupSpringGrid(refreshSpringPanel, 4, 2);
        SpringLayoutHelper.setupSpringGrid(salesSpringPanel, 4, 2);
        SpringLayoutHelper.setupSpringGrid(apSpringPanel, 5, 2);
        SpringLayoutHelper.setupSpringGrid(prodMiscPanel, 2);
        SpringLayoutHelper.setupSpringGrid(prodCBoxSpring, 1, 4);
        SpringLayoutHelper.setupSpringGrid(prodCrit, 4);

        // finalize the layout
        JPanel prodGrid = new JPanel(new GridLayout(2, 2));
        prodGrid.add(refreshSpringPanel);
        prodGrid.add(salesSpringPanel);
        prodGrid.add(apSpringPanel);
        prodGrid.add(prodMiscPanel);

        productionPanel.setLayout(new BoxLayout(productionPanel, BoxLayout.Y_AXIS));

        productionPanel.add(prodGrid);
        productionPanel.add(prodCBoxSpring);
        productionPanel.add(prodCrit);

        /*
         * REWARD MENU CONSTRUCTION
         */
        JPanel rewardBox = new JPanel();
        rewardBox.setLayout(new BoxLayout(rewardBox, BoxLayout.Y_AXIS));
        JPanel rewardCBoxGrid = new JPanel(new SpringLayout());
        JPanel rewardGrid = new JPanel(new GridLayout(1, 2));
        JPanel rewardSpring1 = new JPanel(new SpringLayout());
        JPanel rewardSpring2 = new JPanel(new SpringLayout());

        JLabel rewardAllowHeader = new JLabel("Allow rewards to be used for:");
        rewardAllowHeader.setAlignmentX(Component.CENTER_ALIGNMENT);

        BaseCheckBox = new JCheckBox("DISPLAY");

        BaseCheckBox.setToolTipText("If checked, reward levels are shown to players. RECOMMENDED.");
        BaseCheckBox.setName("ShowReward");
        rewardCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox(mwclient.moneyOrFluMessage(false, true, -1));

        BaseCheckBox.setToolTipText("Check to allow players to exchange RP for flu");
        BaseCheckBox.setName("AllowInfluenceForRewards");
        rewardCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Techs");

        BaseCheckBox.setToolTipText("Check to allow players to exchange RP for techs");
        BaseCheckBox.setName("AllowTechsForRewards");
        rewardCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Units");

        BaseCheckBox.setToolTipText("Check to allow players to exchange RP for units");
        BaseCheckBox.setName("AllowUnitsForRewards");
        rewardCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Rares");

        BaseCheckBox.setToolTipText("Check to allow players to get RARE units with RP");
        BaseCheckBox.setName("AllowRareUnitsForRewards");
        rewardCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Repods");

        BaseCheckBox.setToolTipText("<html>Check to allow players to repod units with RP<br>This allows a player to repod a unit<br>even if its not on their build table<br>Random repod options based<br>on the random repod settings</html>");
        BaseCheckBox.setName("GlobalRepodAllowed");
        rewardCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Refresh");

        BaseCheckBox.setToolTipText("Check to allow players to refresh factories with RP");
        BaseCheckBox.setName("AllowFactoryRefreshForRewards");
        rewardCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Repairs");

        BaseCheckBox.setToolTipText("Check to allow players to repair units with RP");
        BaseCheckBox.setName("AllowRepairsForRewards");
        rewardCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Crit Repairs");
        BaseCheckBox.setToolTipText("Check to allow players to individual crits with RP");
        BaseCheckBox.setName("AllowCritRepairsForRewards");
        rewardCBoxGrid.add(BaseCheckBox);

        SpringLayoutHelper.setupSpringGrid(rewardCBoxGrid, 4);

        // set up spring1
        baseTextField = new JTextField(5);
        rewardSpring1.add(new JLabel("Max Reward Points:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("RP Cap");
        baseTextField.setName("XPRewardCap");
        rewardSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
        rewardSpring1.add(new JLabel("XP for Reward:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Rollover for 1 RP");
        baseTextField.setName("XPRollOverCap");
        rewardSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
        rewardSpring1.add(new JLabel("Techs for RP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Number of techs hired with 1 RP");
        baseTextField.setName("TechsForARewardPoint");
        rewardSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
        rewardSpring1.add(new JLabel("Flu for RP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount of flu given in exhcange for 1 RP");
        baseTextField.setName("InfluenceForARewardPoint");
        rewardSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
        rewardSpring1.add(new JLabel("RP for Proto:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>RP to get a protomech<br> For final cost, add to RP for desired weightclass.</html>");
        baseTextField.setName("RewardPointsForProto");
        rewardSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
        rewardSpring1.add(new JLabel("RP for BA:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>RP to get a battle armor squad<br> For final cost, add to RP for desired weightclass.</html>");
        baseTextField.setName("RewardPointsForBA");
        rewardSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
        rewardSpring1.add(new JLabel("RP for Aero:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>RP to get an Aero<br> For final cost, add to RP for desired weightclass.</html>");
        baseTextField.setName("RewardPointsForAero");
        rewardSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
        rewardSpring1.add(new JLabel("Rare Multiplier:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Rares units cost [Normal RP]*[Rare Multiplier]");
        baseTextField.setName("RewardPointMultiplierForRare");
        rewardSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
        rewardSpring1.add(new JLabel("NonHouse Multiplier:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("How much should the unit cost be multiplied by if not using faction build tables");
        baseTextField.setName("RewardPointNonHouseMultiplier");
        rewardSpring1.add(baseTextField);

        if (Boolean.parseBoolean(mwclient.getserverConfigs("UseAdvanceRepair"))) {
            baseTextField = new JTextField(5);
            rewardSpring1.add(new JLabel("RP to buy Green Tech:", SwingConstants.TRAILING));
            baseTextField.setToolTipText("RP to buy 1 green tech.");
            baseTextField.setName("RewardPointsForGreen");
            rewardSpring1.add(baseTextField);

            baseTextField = new JTextField(5);
            rewardSpring1.add(new JLabel("RP to Buy Vet Tech:", SwingConstants.TRAILING));
            baseTextField.setToolTipText("RP to buy 1 vet tech.");
            baseTextField.setName("RewardPointsForVet");
            rewardSpring1.add(baseTextField);

            baseTextField = new JTextField(5);
            rewardSpring1.add(new JLabel("RP to Repair a crit:", SwingConstants.TRAILING));
            baseTextField.setToolTipText("<html>RP to repair 1 crit.<br>NOTE: this is a double field!</html>");
            baseTextField.setName("RewardPointsForCritRepair");
            rewardSpring1.add(baseTextField);

        }

        baseTextField = new JTextField(5);
        rewardSpring1.add(new JLabel("Repod Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>How much should the repod cost<br>Random repods cost 1/2 this</htlm>");
        baseTextField.setName("GlobalRepodWithRPCost");
        rewardSpring1.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(rewardSpring1, 2);

        // set up spring2
        baseTextField = new JTextField(5);
        rewardSpring2.add(new JLabel("RP for Mek:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("RP to get a mek. For final cost, add to RP for desired weightclass.");
        baseTextField.setName("RewardPointsForAMek");
        rewardSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        rewardSpring2.add(new JLabel("RP for Veh:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("RP to get a vehicle. For final cost, add to RP for desired weightclass.");
        baseTextField.setName("RewardPointsForAVeh");
        rewardSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        rewardSpring2.add(new JLabel("RP for Inf:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("RP to get an infantry unit. For final cost, add to RP for desired weightclass.");
        baseTextField.setName("RewardPointsForInf");
        rewardSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        rewardSpring2.add(new JLabel("RP for Light:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("RP to get a light unit. Add to RP for desired type.");
        baseTextField.setName("RewardPointsForALight");
        rewardSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        rewardSpring2.add(new JLabel("RP for Medium:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("RP to get a medium unit. Add to RP for desired type.");
        baseTextField.setName("RewardPointsForAMed");
        rewardSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        rewardSpring2.add(new JLabel("RP for Heavy:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("RP to get a heavy unit. Add to RP for desired type.");
        baseTextField.setName("RewardPointsForAHeavy");
        rewardSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        rewardSpring2.add(new JLabel("RP for Assault:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("RP to get an assault unit. Add to RP for desired type.");
        baseTextField.setName("RewardPointsForAnAssault");
        rewardSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        rewardSpring2.add(new JLabel("RP to refresh:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("RP to refresh a factions factory.");
        baseTextField.setName("RewardPointToRefreshFactory");
        rewardSpring2.add(baseTextField);

        if (Boolean.parseBoolean(mwclient.getserverConfigs("UseAdvanceRepair"))) {
            baseTextField = new JTextField(5);
            rewardSpring2.add(new JLabel("RP to buy Reg Tech:", SwingConstants.TRAILING));
            baseTextField.setToolTipText("RP to buy 1 reg tech.");
            baseTextField.setName("RewardPointsForReg");
            rewardSpring2.add(baseTextField);

            baseTextField = new JTextField(5);
            rewardSpring2.add(new JLabel("RP to Buy Elite Tech:", SwingConstants.TRAILING));
            baseTextField.setToolTipText("RP to buy 1 elite tech.");
            baseTextField.setName("RewardPointsForElite");
            rewardSpring2.add(baseTextField);

            baseTextField = new JTextField(5);
            rewardSpring2.add(new JLabel("RP to Repair a unit:", SwingConstants.TRAILING));
            baseTextField.setToolTipText("RP to repair 1 unit.");
            baseTextField.setName("RewardPointsForRepair");
            rewardSpring2.add(baseTextField);

        }

        baseTextField = new JTextField(5);
        rewardSpring2.add(new JLabel("Rewards Repod Folder:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Folder to pull repod data from for use with rewards");
        baseTextField.setName("RewardsRepodFolder");
        rewardSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        rewardSpring2.add(new JLabel("Rewards Rare Build Table:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Build table that will be used in the rewards folder");
        baseTextField.setName("RewardsRareBuildTable");
        rewardSpring2.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(rewardSpring2, 2);

        // finalize the layout
        rewardGrid.add(rewardSpring1);
        rewardGrid.add(rewardSpring2);
        rewardBox.add(rewardAllowHeader);
        rewardBox.add(rewardCBoxGrid);
        rewardBox.add(rewardGrid);
        rewardPanel.add(rewardBox);

        /*
         * DEFECTION PANEL CONSTRUCTION Panel which controls most defection-related matter. Some SOL-specific things handled in Newbie panel.
         */
        JPanel defectionTextPanel1 = new JPanel(new SpringLayout());
        JPanel defectionTextPanel2 = new JPanel(new SpringLayout());
        JPanel defectionBoxPanel = new JPanel(new SpringLayout());

        // set up the defection percent loss text boxes
        baseTextField = new JTextField(5);
        defectionTextPanel1.add(new JLabel("Unit Loss Percent:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Percentage of a player's units lost during defection.");
        baseTextField.setName("DefectionUnitLossPercent");
        defectionTextPanel1.add(baseTextField);

        baseTextField = new JTextField(5);
        defectionTextPanel1.add(new JLabel("Flu Loss Percent:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Percentage of a player's influence lost during defection.");
        baseTextField.setName("DefectionInfluenceLossPercent");
        defectionTextPanel1.add(baseTextField);

        baseTextField = new JTextField(5);
        defectionTextPanel1.add(new JLabel("RP Loss Percent:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Percentage of a player's RP lost during defection.");
        baseTextField.setName("DefectionRewardLossPercent");
        defectionTextPanel1.add(baseTextField);

        baseTextField = new JTextField(5);
        defectionTextPanel1.add(new JLabel("Money Loss Percent:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Percentage of a player's money lost during defection.");
        baseTextField.setName("DefectionCBillLossPercent");
        defectionTextPanel1.add(baseTextField);

        baseTextField = new JTextField(5);
        defectionTextPanel1.add(new JLabel("XP Loss Percent:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Percentage of a player's XP lost during defection.");
        baseTextField.setName("DefectionEXPLossPercent");
        defectionTextPanel1.add(baseTextField);

        // set up defection flat loss boxes
        baseTextField = new JTextField(5);
        defectionTextPanel2.add(new JLabel("Unit Loss Flat:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Amount of player's units lost during defection.<br>No effect if % is > 0!</HTML>");
        baseTextField.setName("DefectionUnitLossFlat");
        defectionTextPanel2.add(baseTextField);

        baseTextField = new JTextField(5);
        defectionTextPanel2.add(new JLabel("Flu Loss Flat:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Amount of player's influence lost during defection.<br>No effect if % is > 0!</HTML>");
        baseTextField.setName("DefectionInfluenceLossFlat");
        defectionTextPanel2.add(baseTextField);

        baseTextField = new JTextField(5);
        defectionTextPanel2.add(new JLabel("RP Loss Flat:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Amount of player's RP lost during defection.<br>No effect if % is > 0!</HTML>");
        baseTextField.setName("DefectionRewardLossFlat");
        defectionTextPanel2.add(baseTextField);

        baseTextField = new JTextField(5);
        defectionTextPanel2.add(new JLabel("Money Loss Flat:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Amount of player's money lost during defection.<br>No effect if % is > 0!</HTML>");
        baseTextField.setName("DefectionCBillLossFlat");
        defectionTextPanel2.add(baseTextField);

        baseTextField = new JTextField(5);
        defectionTextPanel2.add(new JLabel("XP Loss Flat:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Amount of player's XP lost during defection.<br>No effect if % is > 0!</HTML>");
        baseTextField.setName("DefectionEXPLossFlat");
        defectionTextPanel2.add(baseTextField);

        // set up the springs for the text fields
        SpringLayoutHelper.setupSpringGrid(defectionTextPanel1, 5, 2);
        SpringLayoutHelper.setupSpringGrid(defectionTextPanel2, 5, 2);

        // set up checkboxen
        BaseCheckBox = new JCheckBox("Merc Penalty");

        BaseCheckBox.setToolTipText("Check to penalize players joining Mercenary factions.");
        BaseCheckBox.setName("PenalizeDefectToMerc");
        defectionBoxPanel.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Non-Conq Penalty");

        BaseCheckBox.setToolTipText("Check to penalize players joining non-conquer factions.");
        BaseCheckBox.setName("PenalizeDefectToNonConq");
        defectionBoxPanel.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Reset SOL");

        BaseCheckBox.setToolTipText("Check to reset player's units and PPQ when they leave training.");
        BaseCheckBox.setName("ReplaceUnitsLeavingSOL");
        defectionBoxPanel.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("SOL Faction Units");

        BaseCheckBox.setToolTipText("<HTML>" + "If both this box and \"Reset Leaving SOL\" are checked, players will<br>receive faction units drawn from their new faction's tables instead of<br> SOL units on defection. Units will be taken from the Standard Folder ONLY.</HTML>");
        BaseCheckBox.setName("FactionUnitsLeavingSOL");
        defectionBoxPanel.add(BaseCheckBox);

        // set up the springs for the check boxes
        SpringLayoutHelper.setupSpringGrid(defectionBoxPanel, 4);

        // finalize layout
        JPanel defectTemp = new JPanel();
        defectTemp.setLayout(new BoxLayout(defectTemp, BoxLayout.Y_AXIS));

        JPanel defectTextFlow = new JPanel();
        defectTextFlow.add(defectionTextPanel1);
        defectTextFlow.add(defectionTextPanel2);

        defectTemp.add(defectTextFlow);
        defectTemp.add(defectionBoxPanel);

        defectionPanel.add(defectTemp);

        /*
         * MISC PANEL CONSTRUCTION options that are difficult to categorize ...
         */
        JPanel miscBoxPanel = new JPanel();
        miscBoxPanel.setLayout(new BoxLayout(miscBoxPanel, BoxLayout.Y_AXIS));
        JPanel miscSpring1 = new JPanel(new SpringLayout());
        JPanel miscSpring2 = new JPanel(new SpringLayout());
        JPanel miscSpringGrid = new JPanel(new GridLayout(1, 2));
        JPanel miscCBoxSpring = new JPanel(new SpringLayout());

        // set up spring 1
        baseTextField = new JTextField(5);
        miscSpring1.add(new JLabel("Tick Time:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Length of Ticks, in ms");
        baseTextField.setName("TickTime");
        miscSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
        miscSpring1.add(new JLabel("Slice Time:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Length of Slices, in ms");
        baseTextField.setName("SliceTime");
        miscSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
        miscSpring1.add(new JLabel("Min Count BV:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Minimum BV for an army to be counted during ticks and slices");
        baseTextField.setName("MinCountForTick");
        miscSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
        miscSpring1.add(new JLabel("Max Count BV:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Maximum BV for an army to be counted during ticks and slices");
        baseTextField.setName("MaxCountForTick");
        miscSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
        miscSpring1.add(new JLabel("Min Active Time:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount of time (in seconds) a player must remain active before returning to reserve");
        baseTextField.setName("MinActiveTime");
        miscSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
        miscSpring1.add(new JLabel("Max Armies:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Max num. of armies a player may have");
        baseTextField.setName("MaxLancesPerPlayer");
        miscSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
        miscSpring1.add(new JLabel("Base Army Weight:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("How much each army counts for while figuring production");
        baseTextField.setName("BaseCountForProduction");
        miscSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
        miscSpring1.add(new JLabel("Startup Miniticks:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Number of miniticks performed 1st time a server is run");
        baseTextField.setName("FreeMinticksOnStartup");
        miscSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
        miscSpring1.add(new JLabel("Allowed MM Version:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>MM version clients should have in order to host (and play)<br>This is Set by the server from the MM file in the server folder<br>Set to -1 to disable</html>");
        baseTextField.setName("AllowedMegaMekVersion");
        miscSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
        miscSpring1.add(new JLabel("Money Short Name:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>short name you want displayed for your servers money<br>i.e. CB for CBills</html>");
        baseTextField.setName("MoneyShortName");
        miscSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
        miscSpring1.add(new JLabel("Flu Short Name:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>short name you want displayed for your servers influence<br>i.e. flu for influence</html>");
        baseTextField.setName("FluShortName");
        miscSpring1.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(miscSpring1, 2);

        // set up spring 2
        baseTextField = new JTextField(5);
        miscSpring2.add(new JLabel("Max Idle Time:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Max amount of time before an Idle playere is kicked<br>Set to 0 to turn off this option</html>");
        baseTextField.setName("MaxIdleTime");
        miscSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        miscSpring2.add(new JLabel("Save Every X:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Save PLAYER data every X slices. Houses save on ticks.");
        baseTextField.setName("SaveEverySlice");
        miscSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        miscSpring2.add(new JLabel("Min Merc XP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Minimum XP for a player to join a mercenary outfit");
        baseTextField.setName("MinEXPforMercenaries");
        miscSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        miscSpring2.add(new JLabel("Min Contract Duration:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Minimum Merc contract duration. NOTE: Unit, PP, and Land are divided by 10.");
        baseTextField.setName("MinContractEXP");
        miscSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        miscSpring2.add(new JLabel("Newbie House Name:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Name of training faction. Changing from SOL isn't recommended.");
        baseTextField.setName("NewbieHouseName");
        miscSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        miscSpring2.add(new JLabel("Money Long Name:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Name for your Servers Money");
        baseTextField.setName("MoneyLongName");
        miscSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        miscSpring2.add(new JLabel("Flu Long Name:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Name of your servers influence");
        baseTextField.setName("FluLongName");
        miscSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        miscSpring2.add(new JLabel("Last Backup Ran:", SwingConstants.TRAILING));
        baseTextField.enableInputMethods(false);
        baseTextField.setName("LastAutomatedBackup");
        miscSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        miscSpring2.add(new JLabel("Hours between backups:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Number of hours between each backup.<br>The backup will run and create zip files for planets.dat<br>houses.dat and all of the playerfiles<br>The zip files will be stored in ./campaign/backup</html>");
        baseTextField.setName("AutomaticBackupHours");
        miscSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        miscSpring2.add(new JLabel("Purge Player Files:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Number of days a player is inactive before they get purged.</html>");
        baseTextField.setName("PurgePlayerFilesDays");
        miscSpring2.add(baseTextField);

        miscSpring2.add(baseTextField);
        baseTextField = new JTextField(5);
        miscSpring2.add(new JLabel("Min MOTD Exp:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Min amount of EXP to set your houses MOTD</html>");
        baseTextField.setName("MinMOTDExp");
        miscSpring2.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(miscSpring2, 2);

        // set up CBoxen
        BaseCheckBox = new JCheckBox("IP Check");

        BaseCheckBox.setToolTipText("<HTML>" + "If checked, players who share an IP will not be" + "able to play games against each other, transfer" + "units, send money, etc.</HTML>");
        BaseCheckBox.setName("IPCheck");
        miscCBoxSpring.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Hide Active");

        BaseCheckBox.setToolTipText("If checked, all players are shown as active in the player list.");
        BaseCheckBox.setName("HideActiveStatus");
        miscCBoxSpring.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Hide ELO");
        BaseCheckBox.setToolTipText("If checked, rating/ELO will not be shown to players.");
        BaseCheckBox.setName("HideELO");
        miscCBoxSpring.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Ranks on Tick");
        BaseCheckBox.setToolTipText("Disable to stop showing Faction Ranks on Tick");
        BaseCheckBox.setName("ShowFactionRanks");
        miscCBoxSpring.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Veh Weightclass in challenges");
        BaseCheckBox.setToolTipText("Enable to show Veh Weightclass in challenges");
        BaseCheckBox.setName("ShowVehWeightclassInChallenges");
        miscCBoxSpring.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Allow Staff to See all Messages");
        BaseCheckBox.setToolTipText("<HTML>If checked all Staff Memebers<br>, despite user level, will be able to see all command messages<br>from other staff</html>");
        BaseCheckBox.setName("AllowLowerLevelUsersToSeeUpperLevelUsersDoings");
        miscCBoxSpring.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("House ticks done at slice");
        BaseCheckBox.setToolTipText("<HTML>If checked house ticks are done incrementally each slice from 1 to X<br>will be done depending on how many can be processed in the 1/2 the slice time.</html>");
        BaseCheckBox.setName("ProcessHouseTicksAtSlice");
        miscCBoxSpring.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Send single commands at a time");
        BaseCheckBox.setToolTipText("<HTML>If checked the first message in the message queue is sent to the player instead of appending<br>the whole queue to a single message sent to the player<br>NOTE: This could slow down the messages a player receives</html>");
        BaseCheckBox.setName("SendSingleCommandAtATime");
        miscCBoxSpring.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Allow RP transfer");
        BaseCheckBox.setToolTipText("<HTML>Allow players to transfer reward points in the same manner as they can transfer cbills</html>");
        BaseCheckBox.setName("AllowRPTransfer");
        miscCBoxSpring.add(BaseCheckBox);
        
        SpringLayoutHelper.setupSpringGrid(miscCBoxSpring, 3);

        // finalize layout
        miscSpringGrid.add(miscSpring1);
        miscSpringGrid.add(miscSpring2);
        miscBoxPanel.add(miscSpringGrid);
        miscBoxPanel.add(miscCBoxSpring);
        miscOptionsPanel.add(miscBoxPanel);

        /*
         * ARTILLERY TAB CONSTRUCTION Enable autoassigned artillery, and set up loadout options.
         */
        JPanel artyBox = new JPanel();
        artyBox.setLayout(new BoxLayout(artyBox, BoxLayout.Y_AXIS));
        JPanel artyCBoxGrid = new JPanel(new GridLayout(1, 2));
        JPanel artySpring = new JPanel(new SpringLayout());
        JPanel gunEmplacementCBoxGrid = new JPanel(new GridLayout(1, 2));
        JPanel gunEmplacementSpring = new JPanel(new SpringLayout());

        // set up check boxes
        BaseCheckBox = new JCheckBox("Heavy First");

        BaseCheckBox.setToolTipText("If checked, server tries to assign assault pieces before light");
        BaseCheckBox.setName("HeaviestArtilleryFirst");
        artyCBoxGrid.add(BaseCheckBox);

        // set up the spring
        baseTextField = new JTextField(5);
        artySpring.add(new JLabel("Assault File:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Filename of the units to load as assault artillery seperated by $");
        baseTextField.setName("AssaultArtilleryFile");
        artySpring.add(baseTextField);

        baseTextField = new JTextField(5);
        artySpring.add(new JLabel("Heavy File:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Filename of the units to load as heavy artillery seperated by $");
        baseTextField.setName("HeavyArtilleryFile");
        artySpring.add(baseTextField);

        baseTextField = new JTextField(5);
        artySpring.add(new JLabel("Medium File:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Filename of the units to load as medium artillery seperated by $");
        baseTextField.setName("MediumArtilleryFile");
        artySpring.add(baseTextField);

        baseTextField = new JTextField(5);
        artySpring.add(new JLabel("Light File:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Filename of the units to load as light artillery seperated by $");
        baseTextField.setName("LightArtilleryFile");
        artySpring.add(baseTextField);

        baseTextField = new JTextField(5);
        artySpring.add(new JLabel("Max Assault:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Max number of assault pieces the server will assign");
        baseTextField.setName("MaxAssaultArtillery");
        artySpring.add(baseTextField);

        baseTextField = new JTextField(5);
        artySpring.add(new JLabel("Max Heavy:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Max number of heavy pieces the server will assign");
        baseTextField.setName("MaxHeavyArtillery");
        artySpring.add(baseTextField);

        baseTextField = new JTextField(5);
        artySpring.add(new JLabel("Max Medium:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Max number of medium pieces the server will assign");
        baseTextField.setName("MaxMediumArtillery");
        artySpring.add(baseTextField);

        baseTextField = new JTextField(5);
        artySpring.add(new JLabel("Max Light:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Max number of light pieces the server will assign");
        baseTextField.setName("MaxLightArtillery");
        artySpring.add(baseTextField);

        baseTextField = new JTextField(5);
        artySpring.add(new JLabel("BV for Assault:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("BV removed from task total (all players) when assault is assigned");
        baseTextField.setName("BVForAssaultArtillery");
        artySpring.add(baseTextField);

        baseTextField = new JTextField(5);
        artySpring.add(new JLabel("BV for Heavy:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("BV removed from task total (all players) when heavy is assigned");
        baseTextField.setName("BVForHeavyArtillery");
        artySpring.add(baseTextField);

        baseTextField = new JTextField(5);
        artySpring.add(new JLabel("BV for Medium:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("BV removed from task total (all players) when medium is assigned");
        baseTextField.setName("BVForMediumArtillery");
        artySpring.add(baseTextField);

        baseTextField = new JTextField(5);
        artySpring.add(new JLabel("BV for Light:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("BV removed from task total (all players) when light is assigned");
        baseTextField.setName("BVForLightArtillery");
        artySpring.add(baseTextField);

        baseTextField = new JTextField(5);
        artySpring.add(new JLabel("Distance:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Distance, in mapsheets, that Arty is deployed from a players home edge");
        baseTextField.setName("DistanceFromMap");
        artySpring.add(baseTextField);

        baseTextField = new JTextField(5);
        artySpring.add(new JLabel("Artillery Over Run:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML><BODY>The chance of an offboard unit getting over run.<BR>This is modified by the number of hexs the unit is off the board</HTML></BODY>");
        baseTextField.setName("ArtilleryOffBoardOverRun");
        artySpring.add(baseTextField);

        baseTextField = new JTextField(5);
        artySpring.add(new JLabel("Off Board Capture:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("The chance of a off board unit being salvaged.");
        baseTextField.setName("OffBoardChanceOfCapture");
        artySpring.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(artySpring, 8, 4);

        // set up check boxes
        BaseCheckBox = new JCheckBox("Heavy First");

        BaseCheckBox.setToolTipText("If checked, server tries to assign assault pieces before light");
        BaseCheckBox.setName("HeaviestGunEmplacementFirst");
        gunEmplacementCBoxGrid.add(BaseCheckBox);

        // set up the spring
        baseTextField = new JTextField(5);
        gunEmplacementSpring.add(new JLabel("Assault File:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Filename of the units to load as assault Guns seperated by $");
        baseTextField.setName("AssaultGunEmplacementFile");
        gunEmplacementSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        gunEmplacementSpring.add(new JLabel("Heavy File:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Filename of the units to load as heavy Guns seperated by $");
        baseTextField.setName("HeavyGunEmplacementFile");
        gunEmplacementSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        gunEmplacementSpring.add(new JLabel("Medium File:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Filename of the units to load as medium Guns seperated by $");
        baseTextField.setName("MediumGunEmplacementFile");
        gunEmplacementSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        gunEmplacementSpring.add(new JLabel("Light File:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Filename of the units to load as light Guns seperated by $");
        baseTextField.setName("LightGunEmplacementFile");
        gunEmplacementSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        gunEmplacementSpring.add(new JLabel("Max Assault:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Max number of assault pieces the server will assign");
        baseTextField.setName("MaxAssaultGunEmplacement");
        gunEmplacementSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        gunEmplacementSpring.add(new JLabel("Max Heavy:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Max number of heavy pieces the server will assign");
        baseTextField.setName("MaxHeavyGunEmplacement");
        gunEmplacementSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        gunEmplacementSpring.add(new JLabel("Max Medium:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Max number of medium pieces the server will assign");
        baseTextField.setName("MaxMediumGunEmplacement");
        gunEmplacementSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        gunEmplacementSpring.add(new JLabel("Max Light:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Max number of light pieces the server will assign");
        baseTextField.setName("MaxLightGunEmplacement");
        gunEmplacementSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        gunEmplacementSpring.add(new JLabel("BV for Assault:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("BV removed from task total (all players) when assault is assigned");
        baseTextField.setName("BVForAssaultGunEmplacement");
        gunEmplacementSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        gunEmplacementSpring.add(new JLabel("BV for Heavy:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("BV removed from task total (all players) when heavy is assigned");
        baseTextField.setName("BVForHeavyGunEmplacement");
        gunEmplacementSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        gunEmplacementSpring.add(new JLabel("BV for Medium:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("BV removed from task total (all players) when medium is assigned");
        baseTextField.setName("BVForMediumGunEmplacement");
        gunEmplacementSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        gunEmplacementSpring.add(new JLabel("BV for Light:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("BV removed from task total (all players) when light is assigned");
        baseTextField.setName("BVForLightGunEmplacement");
        gunEmplacementSpring.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(gunEmplacementSpring, 4);

        // finalize layout
        artyBox.add(artyCBoxGrid);
        artyBox.add(artySpring);

        artyBox.add(gunEmplacementCBoxGrid);
        artyBox.add(gunEmplacementSpring);

        artilleryPanel.add(artyBox);

        /*
         * NEWBIE HOUSE PANEL CONSTRUCTION. Set units SOL receives and units which are legal vs SOL
         */
        JPanel newbieMekSpring = new JPanel(new SpringLayout());
        JPanel newbieVehSpring = new JPanel(new SpringLayout());
        JPanel newbieInfSpring = new JPanel(new SpringLayout());
        JPanel newbiePMSpring = new JPanel(new SpringLayout());
        JPanel newbieBASpring = new JPanel(new SpringLayout());
        JPanel newbieAeroSpring = new JPanel(new SpringLayout());
        JPanel resetUnitsSpring = new JPanel(new SpringLayout());

        JPanel newbieSpringGrid = new JPanel(new GridLayout(4, 2));
        newbieSpringGrid.add(newbieMekSpring);
        newbieSpringGrid.add(newbieVehSpring);
        newbieSpringGrid.add(newbieInfSpring);
        newbieSpringGrid.add(newbiePMSpring);
        newbieSpringGrid.add(newbieBASpring);
        newbieSpringGrid.add(newbieAeroSpring);
        newbieSpringGrid.add(resetUnitsSpring);

        // set up the mek spring
        baseTextField = new JTextField(5);
        newbieMekSpring.add(new JLabel("Light Meks:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Number of light meks given to SOL players");
        baseTextField.setName("SOLLightMeks");
        newbieMekSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        newbieMekSpring.add(new JLabel("Medium Meks:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Number of medium meks given to SOL players");
        baseTextField.setName("SOLMediumMeks");
        newbieMekSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        newbieMekSpring.add(new JLabel("Heavy Meks:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Number of heavy meks given to SOL players");
        baseTextField.setName("SOLHeavyMeks");
        newbieMekSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        newbieMekSpring.add(new JLabel("Assault Meks:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Number of assault meks given to SOL players");
        baseTextField.setName("SOLAssaultMeks");
        newbieMekSpring.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(newbieMekSpring, 4, 2);

        // now the veh spring
        baseTextField = new JTextField(5);
        newbieVehSpring.add(new JLabel("Light Vehs:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Number of light vehs given to SOL players");
        baseTextField.setName("SOLLightVehs");
        newbieVehSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        newbieVehSpring.add(new JLabel("Medium Vehs:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Number of medium vehs given to SOL players");
        baseTextField.setName("SOLMediumVehs");
        newbieVehSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        newbieVehSpring.add(new JLabel("Heavy Vehs:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Number of heavy vehs given to SOL players");
        baseTextField.setName("SOLHeavyVehs");
        newbieVehSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        newbieVehSpring.add(new JLabel("Assault Vehs:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Number of assault vehs given to SOL players");
        baseTextField.setName("SOLAssaultVehs");
        newbieVehSpring.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(newbieVehSpring, 4, 2);

        // infantry spring
        baseTextField = new JTextField(5);
        newbieInfSpring.add(new JLabel("Light Inf:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Number of light infantry units given to SOL players");
        baseTextField.setName("SOLLightInf");
        newbieInfSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        newbieInfSpring.add(new JLabel("Medium Inf:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Number of medium infantry units given to SOL players");
        baseTextField.setName("SOLMediumInf");
        newbieInfSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        newbieInfSpring.add(new JLabel("Heavy Inf:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Number of heavy infantry units given to SOL players");
        baseTextField.setName("SOLHeavyInf");
        newbieInfSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        newbieInfSpring.add(new JLabel("Assault Inf:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Number of assault infantry units given to SOL players");
        baseTextField.setName("SOLAssaultInf");
        newbieInfSpring.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(newbieInfSpring, 4, 2);

        // protomech spring
        baseTextField = new JTextField(5);
        newbiePMSpring.add(new JLabel("Light Proto:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Number of light protomech units given to SOL players");
        baseTextField.setName("SOLLightProtoMek");
        newbiePMSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        newbiePMSpring.add(new JLabel("Medium Proto:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Number of medium protomech units given to SOL players");
        baseTextField.setName("SOLMediumProtoMek");
        newbiePMSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        newbiePMSpring.add(new JLabel("Heavy Proto:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Number of heavy protomech units given to SOL players");
        baseTextField.setName("SOLHeavyProtoMek");
        newbiePMSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        newbiePMSpring.add(new JLabel("Assault Proto:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Number of assault protomech units given to SOL players");
        baseTextField.setName("SOLAssaultProtoMek");
        newbiePMSpring.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(newbiePMSpring, 2);

        // and the battle Armor spring
        baseTextField = new JTextField(5);
        newbieBASpring.add(new JLabel("Light BA:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Number of light battle armor units given to SOL players");
        baseTextField.setName("SOLLightBattleArmor");
        newbieBASpring.add(baseTextField);

        baseTextField = new JTextField(5);
        newbieBASpring.add(new JLabel("Medium BA:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Number of medium battle armor units given to SOL players");
        baseTextField.setName("SOLMediumBattleArmor");
        newbieBASpring.add(baseTextField);

        baseTextField = new JTextField(5);
        newbieBASpring.add(new JLabel("Heavy BA:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Number of heavy battle armor units given to SOL players");
        baseTextField.setName("SOLHeavyBattleArmor");
        newbieBASpring.add(baseTextField);

        baseTextField = new JTextField(5);
        newbieBASpring.add(new JLabel("Assault BA:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Number of assault battle armor units given to SOL players");
        baseTextField.setName("SOLAssaultBattleArmor");
        newbieBASpring.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(newbieBASpring, 2);

        // set up the mek spring
        baseTextField = new JTextField(5);
        newbieAeroSpring.add(new JLabel("Light Aeros:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Number of light aeros given to SOL players");
        baseTextField.setName("SOLLightAero");
        newbieAeroSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        newbieAeroSpring.add(new JLabel("Medium Aeros:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Number of medium aeros given to SOL players");
        baseTextField.setName("SOLMediumAero");
        newbieAeroSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        newbieAeroSpring.add(new JLabel("Heavy Aeros:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Number of heavy aeros given to SOL players");
        baseTextField.setName("SOLHeavyAero");
        newbieAeroSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        newbieAeroSpring.add(new JLabel("Assault Aeros:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Number of assault aeros given to SOL players");
        baseTextField.setName("SOLAssaultAero");
        newbieAeroSpring.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(newbieAeroSpring, 4, 2);

        // and last, the reset spring
        baseTextField = new JTextField(5);
        resetUnitsSpring.add(new JLabel("Units to Reset:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>" + "SOL player needs to have this many (or<br>" + "fewer) units to trigger a hangar reset</HTML>");
        baseTextField.setName("NumUnitsToQualifyForNew");
        resetUnitsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        resetUnitsSpring.add(new JLabel("Resets while immune:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>" + "Number of resets a SOL player is allowed<br>" + "after a game, while immune from attack." + "</HTML>");
        baseTextField.setName("NumResetsWhileImmune");
        resetUnitsSpring.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(resetUnitsSpring, 2);

        // finalize the layout
        JPanel newbieBox = new JPanel();
        newbieBox.setLayout(new BoxLayout(newbieBox, BoxLayout.Y_AXIS));
        newbieBox.add(newbieSpringGrid);
        newbieHousePanel.add(newbieBox);

        /*
         * COMBAT Panel Setup
         */
        JPanel combatBox = new JPanel();
        combatBox.setLayout(new BoxLayout(combatBox, BoxLayout.Y_AXIS));
        JPanel combatCBoxGrid = new JPanel(new GridLayout(4, 3));

        JPanel combatSpring1 = new JPanel(new SpringLayout());
        JPanel combatSpring2 = new JPanel(new SpringLayout());
        JPanel combatSpring3 = new JPanel(new SpringLayout());
        JPanel combatSpring4 = new JPanel(new SpringLayout());
        JPanel combatMMOptionsSpring = new JPanel(new SpringLayout());

        JPanel combatSpringFlow = new JPanel();

        combatSpringFlow.add(combatSpring1);
        combatSpringFlow.add(combatSpring2);
        combatSpringFlow.add(combatSpring3);
        combatSpringFlow.add(combatSpring4);

        BaseCheckBox = new JCheckBox("Probe In Reserve");

        BaseCheckBox.setToolTipText("Allow /c ca in reserve mode?");
        BaseCheckBox.setName("ProbeInReserve");
        combatCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Use Real_Blind_Drops");

        BaseCheckBox.setToolTipText("<HTML>Check in order to use real_blind_drop option in MM,<br> hiding units from players until they appear on the map.<br>If this option is enabled, /c tasks and join messages will not show army composition.</HTML>");
        BaseCheckBox.setName("UseBlindDrops");
        combatCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Selectable Salvage");
        BaseCheckBox.setToolTipText("If set to true then players can recoup repair costs by scrapping salvaged units");

        BaseCheckBox.setName("SelectableSalvage");
        combatCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Use Force Salvage");

        BaseCheckBox.setToolTipText("Count Mechs without a leg or 2 gyro hits as salvage?");
        BaseCheckBox.setName("ForceSalvage");
        combatCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Use Advanced Terrains");

        BaseCheckBox.setToolTipText("Use Already built maps vs terrain and RMG");
        BaseCheckBox.setName("UseStaticMaps");
        combatCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Show Inf In /c ca");

        BaseCheckBox.setName("ShowInfInCheckAttack");
        combatCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Allow Limiters");

        BaseCheckBox.setName("AllowLimiters");
        combatCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Count Inf For Limits");

        BaseCheckBox.setName("CountInfForLimiters");
        combatCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Allow Unit Ratios");

        BaseCheckBox.setToolTipText("If checked ratios will be followed otherwise anything goes.");
        BaseCheckBox.setName("AllowRatios");
        combatCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Use Prelim Op Report");

        BaseCheckBox.setToolTipText("<html>Check this to allow the players a chance<br>of receiving prelim data on a task they've accepted</html>");
        BaseCheckBox.setName("AllowPreliminaryOperationsReports");
        combatCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Display Op Name");

        BaseCheckBox.setToolTipText("Display the Op name to the defender");
        BaseCheckBox.setName("DisplayOperationName");
        combatCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Force Deactivate Players");

        BaseCheckBox.setToolTipText("<html>If this is checked then after combat players are<br>automatically deactivated immunity time is also ignored.</html>");
        BaseCheckBox.setName("ForcedDeactivation");
        combatCBoxGrid.add(BaseCheckBox);

        // spring1. 6 elements.
        baseTextField = new JTextField(5);
        combatSpring1.add(new JLabel("Upper Limit Buffer"));
        baseTextField.setToolTipText("<HTML>" + "Min Buffer On Upper Limiter. For example, a<br>" + "setting of 2 would prevent a player with a 4<br>" + "unit army from setting a limiter of 4 or 5.");
        baseTextField.setName("UpperLimitBuffer");
        combatSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
        combatSpring1.add(new JLabel("Lower Limit Buffer"));
        baseTextField.setToolTipText("<HTML>" + "Min Buffer On Lower Limiter. For example, a<br>" + "setting of 2 would prevent a player with a 7<br>" + "unit army from setting a limiter of 5 or 6.");
        baseTextField.setName("LowerLimitBuffer");
        combatSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
        combatSpring1.add(new JLabel("Default Upper Limit"));
        baseTextField.setName("DefaultUpperLimit");
        combatSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
        combatSpring1.add(new JLabel("Default Lower Limit"));
        baseTextField.setName("DefaultLowerLimit");
        combatSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
        combatSpring1.add(new JLabel("Mek to Inf Ratio:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Only Used if AllowRatios is checked<br>Set the %Ratio for Infantry to Mek if set at 50% 1 Infantry to every 2 Meks<br>If set at 200% 2 infantry to 1 Mek is allowed</body></html>");
        baseTextField.setName("MekToInfantryRatio");
        combatSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
        combatSpring1.add(new JLabel("Chance For Op Report:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>The min chance a player will receive info on the planet<br>base chance is based on players factions<br>owner ship of the planet<br>if that is lower then this number this number<br>will be used</htlm>");
        baseTextField.setName("MinChanceForAccurateOperationsReports");
        combatSpring1.add(baseTextField);

        // spring2. 6 elements.
        baseTextField = new JTextField(5);
        combatSpring2.add(new JLabel("Immunity Time", SwingConstants.TRAILING));
        baseTextField.setToolTipText("How long shall a player be immune to attacks after he just finished a task? (in seconds)");
        baseTextField.setName("ImmunityTime");
        combatSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        combatSpring2.add(new JLabel("Mek Map Factor", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Map Size Factors (Those determine how big a map will be)");
        baseTextField.setName("MekMapSizeFactor");
        combatSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        baseTextField.setToolTipText("Map Size Factors (Those determine how big a map will be)");
        combatSpring2.add(new JLabel("Veh Map Factor", SwingConstants.TRAILING));
        baseTextField.setName("VehicleMapSizeFactor");
        combatSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        baseTextField.setToolTipText("Map Size Factors (Those determine how big a map will be)");
        combatSpring2.add(new JLabel("Inf Map Factor", SwingConstants.TRAILING));
        baseTextField.setName("InfantryMapSizeFactor");
        combatSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        baseTextField.setToolTipText("Map Size Factors (Those determine how big a map will be)");
        combatSpring2.add(new JLabel("Aero Map Factor", SwingConstants.TRAILING));
        baseTextField.setName("AeroMapSizeFactor");
        combatSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        combatSpring2.add(new JLabel("Game Log Name", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Name of the game log to save to the users system");
        baseTextField.setName("MMGameLogName");
        combatSpring2.add(baseTextField);

        // spring3. 5 elements.
        baseTextField = new JTextField(5);
        baseTextField.setToolTipText("Map Size Factors (Those determine how big a map will be)");
        combatSpring3.add(new JLabel("Proto Map Factor", SwingConstants.TRAILING));
        baseTextField.setName("ProtoMekMapSizeFactor");
        combatSpring3.add(baseTextField);

        baseTextField = new JTextField(5);
        baseTextField.setToolTipText("Map Size Factors (Those determine how big a map will be)");
        combatSpring3.add(new JLabel("BA Map Factor", SwingConstants.TRAILING));
        baseTextField.setName("BattleArmorMapSizeFactor");
        combatSpring3.add(baseTextField);

        baseTextField = new JTextField(5);
        combatSpring3.add(new JLabel("Fast Hover Mod", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Add X BV to all Hovers with 8/12 and more");
        baseTextField.setName("FastHoverBVMod");
        combatSpring3.add(baseTextField);

        baseTextField = new JTextField(5);
        combatSpring3.add(new JLabel("Salvage Scrap Time", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>" + "Time, in seconds, that players have<br>" + "after game to scrap units w/o charge.</html>");
        baseTextField.setName("TimeToSelectSalvage");
        combatSpring3.add(baseTextField);

        baseTextField = new JTextField(5);
        combatSpring3.add(new JLabel("Mek to Vehicle Ratio:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Only Used if AllowRatios is checked<br>Set the %Ratio for vehicle to Mek if set at 50% 1 vehcile to every 2 Meks<br>If set at 200% 2 Vehicles to 1 Mek is allowed</body></html>");
        baseTextField.setName("MekToVehicleRatio");
        combatSpring3.add(baseTextField);

        // pack the springs.
        SpringLayoutHelper.setupSpringGrid(combatSpring1, 2);
        SpringLayoutHelper.setupSpringGrid(combatSpring2, 2);
        SpringLayoutHelper.setupSpringGrid(combatSpring3, 2);

        BaseCheckBox = new JCheckBox("Show Unit Id?");

        BaseCheckBox.setToolTipText("Unit ID are displayed to help ID units");
        BaseCheckBox.setName("MMShowUnitId");
        combatMMOptionsSpring.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Time Stamp Save Games?");

        BaseCheckBox.setToolTipText("All Save Games will have a timestamp on them");
        BaseCheckBox.setName("MMTimeStampLogFile");
        combatMMOptionsSpring.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Keep Game Log?");

        BaseCheckBox.setToolTipText("Save game log to users system");
        BaseCheckBox.setName("MMKeepGameLog");
        combatMMOptionsSpring.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Allow partial bins?");

        BaseCheckBox.setToolTipText("<html>Allow units in any army<br>to go active if they have partially full ammobins.</html>");
        BaseCheckBox.setName("AllowUnitsToActivateWithPartialBins");
        combatMMOptionsSpring.add(BaseCheckBox);

        SpringLayoutHelper.setupSpringGrid(combatMMOptionsSpring, 3);

        BaseCheckBox = new JCheckBox("Allow Attacks From Reserve?");

        BaseCheckBox.setToolTipText("Allows players to arrange games and attack while in reserve");
        BaseCheckBox.setName("AllowAttackFromReserve");
        combatSpring4.add(BaseCheckBox);

        baseTextField = new JTextField(5);
        combatSpring4.add(new JLabel("Response Time:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>The amount of time, in minutes, that the<br>defending player has to respond before the offer expires</html>");
        baseTextField.setName("AttackFromReserveResponseTime");
        combatSpring4.add(baseTextField);

        baseTextField = new JTextField(5);
        combatSpring4.add(new JLabel("Wait Time:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>How long, in minutes, the attacking player<br>has to wait before they can attack again from reserve.</html>");
        baseTextField.setName("AttackFromReserveSleepTime");
        combatSpring4.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(combatSpring4, 5);

        // finalize layout
        combatBox.add(combatCBoxGrid);
        combatBox.add(combatSpringFlow);
        combatBox.add(combatMMOptionsSpring);
        combatBox.add(combatSpring4);
        combatPanel.add(combatBox);

        /*
         * BATTLE VALUE Panel
         */
        JPanel battleValueBox = new JPanel();
        battleValueBox.setLayout(new BoxLayout(battleValueBox, BoxLayout.Y_AXIS));
        JPanel battleValueCBoxGrid = new JPanel(new GridLayout(1, -1));
        JPanel battleValueSpring = new JPanel(new SpringLayout());

        BaseCheckBox = new JCheckBox("Use Force Size Rules");
        BaseCheckBox.setToolTipText("Use the Tech Manual Force Size BV Adjustments?");

        BaseCheckBox.setName("UseOperationsRule");
        battleValueCBoxGrid.add(BaseCheckBox);

        baseTextField = new JTextField(5);
        battleValueSpring.add(new JLabel("Mek Force Size:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>This is how much of an element a mek counts as for the Force Size Calculation<br>Note this is a double field.</html>");
        baseTextField.setName("MekOperationsBVMod");
        battleValueSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        battleValueSpring.add(new JLabel("Vee Force Size:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>This is how much of an element a vee counts as for the Force Size Calculation<br>Note this is a double field.</html>");
        baseTextField.setName("VehicleOperationsBVMod");
        battleValueSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        battleValueSpring.add(new JLabel("BA Force Size:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>This is how much of an element a BA squad/star counts as for the Force Size Calculation<br>Note this is a double field.</html>");
        baseTextField.setName("BAOperationsBVMod");
        battleValueSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        battleValueSpring.add(new JLabel("Proto Force Size:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>This is how much of an element a proto counts as for the Force Size Calculation<br>Note this is a double field.</html>");
        baseTextField.setName("ProtoOperationsBVMod");
        battleValueSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        battleValueSpring.add(new JLabel("Inf Force Size:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>This is how much of an element a inf unit counts as for the Force Size Calculation<br>Note this is a double field.</html>");
        baseTextField.setName("InfantryOperationsBVMod");
        battleValueSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        battleValueSpring.add(new JLabel("Aero Force Size:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>This is how much of an element an aero counts as for the Force Size Calculation<br>Note this is a double field.</html>");
        baseTextField.setName("AeroOperationsBVMod");
        battleValueSpring.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(battleValueSpring, 4);

        // finalize layout
        battleValueBox.add(battleValueCBoxGrid);
        battleValueBox.add(battleValueSpring);
        battleValuePanel.add(battleValueBox);

        /*
         * NO PLAY setup
         */
        JPanel noPlayBox = new JPanel();
        noPlayBox.setLayout(new BoxLayout(noPlayBox, BoxLayout.Y_AXIS));

        JPanel noPlaySpring = new JPanel(new SpringLayout());

        baseTextField = new JTextField(5);
        noPlaySpring.add(new JLabel("Max Player No-Plays:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Max number of players someone can add to no-play list.");
        baseTextField.setName("NoPlayListSize");
        noPlaySpring.add(baseTextField);

        baseTextField = new JTextField(5);
        noPlaySpring.add(new JLabel("No-Play " + mwclient.moneyOrFluMessage(true, true, -1) + " Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText(mwclient.moneyOrFluMessage(true, true, -1) + " charged to remove a player from the no-play list.");
        baseTextField.setName("NoPlayMUCost");
        noPlaySpring.add(baseTextField);

        baseTextField = new JTextField(5);
        noPlaySpring.add(new JLabel("No-Play RP Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("RP charged to remove a player from the no-play list.");
        baseTextField.setName("NoPlayRPCost");
        noPlaySpring.add(baseTextField);

        baseTextField = new JTextField(5);
        noPlaySpring.add(new JLabel("No-Play " + mwclient.moneyOrFluMessage(false, true, -1) + " Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText(mwclient.moneyOrFluMessage(false, false, -1) + " charged to remove a player from the no-play list.");
        baseTextField.setName("NoPlayInfluenceCost");
        noPlaySpring.add(baseTextField);

        BaseCheckBox = new JCheckBox("Admin No-Plays Count");
        BaseCheckBox.setToolTipText("<HTML>" + "Check to have no-plays added by admins count towards the<br>" + "maximum. Note that admins can add no-plays in excess of<br>" + "the cap. Enabling this simply prevents players from adding<br>" + "their own choices to their no-play lists if admins have<br>" + "been forced to make additions equal to, or in excess of,<br>" + "the max.</HTML>");
        BaseCheckBox.setName("NoPlaysFromAdminsCountForMax");
        noPlaySpring.add(BaseCheckBox);

        SpringLayoutHelper.setupSpringGrid(noPlaySpring, 2);

        // finalize layout
        noPlayBox.add(noPlaySpring);
        noPlayPanel.add(noPlayBox);

        /*
         * BLACK MARKET setup
         */
        JPanel bmBox = new JPanel();
        bmBox.setLayout(new BoxLayout(bmBox, BoxLayout.Y_AXIS));

        JPanel bmCBoxSpring = new JPanel(new SpringLayout());
        JPanel bmTextSpring = new JPanel(new SpringLayout());

        // small text spring
        baseTextField = new JTextField(10);
        bmTextSpring.add(new JLabel("Min BM Sale Length:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Minimum sale time, in ticks.");
        baseTextField.setName("MinBMSalesTicks");
        bmTextSpring.add(baseTextField);

        baseTextField = new JTextField(10);
        bmTextSpring.add(new JLabel("Min BM Sale Price:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Minimum asking price.");
        baseTextField.setName("MinBMSalesPrice");
        bmTextSpring.add(baseTextField);

        baseTextField = new JTextField(10);
        bmTextSpring.add(new JLabel("Max BM Sale Length:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Maximum sale time, in ticks.");
        baseTextField.setName("MaxBMSalesTicks");
        bmTextSpring.add(baseTextField);

        baseTextField = new JTextField(10);
        bmTextSpring.add(new JLabel("Max BM Sale Price:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Maximum asking price. Bids CAN be higher.");
        baseTextField.setName("MaxBMSalesPrice");
        bmTextSpring.add(baseTextField);

        baseTextField = new JTextField(10);
        bmTextSpring.add(new JLabel("Min XP to Buy:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("XP required to buy units from the BM");
        baseTextField.setName("MinEXPforBMBuying");
        bmTextSpring.add(baseTextField);

        baseTextField = new JTextField(10);
        bmTextSpring.add(new JLabel("Min XP to Sell:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("XP required to sell units on the BM");
        baseTextField.setName("MinEXPforBMSelling");
        bmTextSpring.add(baseTextField);

        baseTextField = new JTextField(10);
        bmTextSpring.add(new JLabel("BM Bid " + mwclient.moneyOrFluMessage(false, true, -1) + " Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText(mwclient.moneyOrFluMessage(false, false, -1) + " charge for bidding on the BM.");
        baseTextField.setName("BMBidFlu");
        bmTextSpring.add(baseTextField);

        baseTextField = new JTextField(10);
        bmTextSpring.add(new JLabel("BM Sale " + mwclient.moneyOrFluMessage(false, true, -1) + " Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base " + mwclient.moneyOrFluMessage(false, true, -1) + " cost for a BM sale. Modified by weight.");
        baseTextField.setName("BMSellFlu");
        bmTextSpring.add(baseTextField);

        baseTextField = new JTextField(10);
        bmTextSpring.add(new JLabel("BM Size " + mwclient.moneyOrFluMessage(false, true, -1) + " Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("[SizeCost] * [Unit Weightclass] added to " + mwclient.moneyOrFluMessage(false, true, -1) + " cost of a BM sale.");
        baseTextField.setName("BMFluSizeCost");
        bmTextSpring.add(baseTextField);

        baseTextField = new JTextField(10);
        bmTextSpring.add(new JLabel("Auction Fee:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Auction fee charged to the seller after a sucessful sale<br>This is a double number i.e. 0.15 is 15%</html>");
        baseTextField.setName("AuctionFee");
        bmTextSpring.add(baseTextField);

        baseTextField = new JTextField(10);
        bmTextSpring.add(new JLabel("Rare Chance:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Percent chance of producing a rare unit and sending it to the Market.<br>This is a double Var 1.0 = 1%</html>");
        baseTextField.setName("RareChance");
        bmTextSpring.add(baseTextField);

        baseTextField = new JTextField(10);
        bmTextSpring.add(new JLabel("Rare Sale Time:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>This is the minimum amount of ticks<br>a rare unit will be listed on the black marked.</html>");
        baseTextField.setName("RareMinSaleTime");
        bmTextSpring.add(baseTextField);

        baseTextField = new JTextField(10);
        bmTextSpring.add(new JLabel("Chance unit goes to BM:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>This is the chance that over flow units from house bays<br>goto the BM instead of being scrapped.</html>");
        baseTextField.setName("ChanceToSendUnitToBM");
        bmTextSpring.add(baseTextField);

        baseTextField = new JTextField();
        bmTextSpring.add(new JLabel("No Sales:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>" + "List of factions that cannot sell on BM. $ deliminted and<br>" + "case sensitive. This stops all players in the faction from<br>" + "selling on the market as well as all sales from the faction<br>" + "when hangars/bays are full. Example: Liao$Davion$Marik$</html>");
        baseTextField.setName("BMNoSell");
        bmTextSpring.add(baseTextField);

        baseTextField = new JTextField();
        bmTextSpring.add(new JLabel("No Bids:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>" + "List of factions that cannot buy from BM. $ deliminted and<br>" + "case sensitive. This stops players from placing bids on units.<br>" + "Example: Trinity Alliance$Lyran Alliance$Word of Blake$</html>");
        baseTextField.setName("BMNoBuy");
        bmTextSpring.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(bmTextSpring, 4);

        // cbox spring - 5 elements in a 3*2 arrangement
        BaseCheckBox = new JCheckBox("Infantry Allowed");

        BaseCheckBox.setToolTipText("Check to allow player&houses to sell Infantry on the BM");
        BaseCheckBox.setName("InfantryMayBeSoldOnBM");
        bmCBoxSpring.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("BA Allowed");

        BaseCheckBox.setToolTipText("Check to allow player&houses to sell BA on the BM");
        BaseCheckBox.setName("BAMayBeSoldOnBM");
        bmCBoxSpring.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Protos Allowed");

        BaseCheckBox.setToolTipText("Check to allow player&houses to sell Protos on the BM");
        BaseCheckBox.setName("ProtosMayBeSoldOnBM");
        bmCBoxSpring.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Vehs Allowed");

        BaseCheckBox.setToolTipText("Check to allow player&houses to sell Vehs on the BM");
        BaseCheckBox.setName("VehsMayBeSoldOnBM");
        bmCBoxSpring.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Meks Allowed");

        BaseCheckBox.setToolTipText("Check to allow player&houses to sell Meks on the BM");
        BaseCheckBox.setName("MeksMayBeSoldOnBM");
        bmCBoxSpring.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Aeros Allowed");

        BaseCheckBox.setToolTipText("Check to allow player&houses to sell Aeros on the BM");
        BaseCheckBox.setName("AerosMayBeSoldOnBM");
        bmCBoxSpring.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Clan Unit Ban");

        BaseCheckBox.setToolTipText("<html>" + "Check to stop players from selling clan units on the<br>" + "BM. Faction overflow and random rares can include clan<br>" + "tech. Block faction sales entirely to stop overflow.</html>");
        BaseCheckBox.setName("BMNoClan");
        bmCBoxSpring.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Use Parts Market");
        BaseCheckBox.setToolTipText("Use the parts blackmarket this coencides with using parts to repair");
        BaseCheckBox.setName("UsePartsBlackMarket");
        bmCBoxSpring.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Allow Tech Cross Over");

        BaseCheckBox.setToolTipText("If checked IS Player are allowed to buy clan tech on the BM and visa versa.");
        BaseCheckBox.setName("AllowCrossOverTech");
        bmCBoxSpring.add(BaseCheckBox);

        SpringLayoutHelper.setupSpringGrid(bmCBoxSpring, 3);

        JPanel bmButtonSpring = new JPanel(new SpringLayout());

        ButtonGroup auctionTypes = new ButtonGroup();

        baseRadioButton = new JRadioButton("Vickery");

        baseRadioButton.setName("UseVickeryAuctionType");
        baseRadioButton.setToolTipText("<html>Vickrey auction is a modified highest sealed bid auction. Winner<br>" + "determination is the same (highest bid, earliest placement in the<br>" + "event of a tie), but the winner pays 2nd highest bid, plus one, in<br>" + "lieu of the amount he offered.<br>" + "NOTE: you must restart the server for this to take effect!</html");

        auctionTypes.add(baseRadioButton);
        bmButtonSpring.add(baseRadioButton);

        baseRadioButton = new JRadioButton("Highest Sealed Bid");

        baseRadioButton.setName("UseHighestSealedBidAuctionType");
        baseRadioButton.setToolTipText("<html>Winner is simply the highest offering person who can<br>" + "afford to pay. This, codewise, is a truncated Vickrey<br>" + "Auction. Same mechanism to find highest bidder, but no<br>" + "downward adjustment.<br>" + "NOTE: You must restart the server for this to take effect!</html>");

        auctionTypes.add(baseRadioButton);
        bmButtonSpring.add(baseRadioButton);

        SpringLayoutHelper.setupSpringGrid(bmButtonSpring, 1);

        bmBox.add(bmTextSpring);
        bmBox.add(bmCBoxSpring);
        bmBox.add(bmButtonSpring);
        blackMarketPanel.add(bmBox);

        /*
         * DISCONNECTION setup
         */
        JPanel discoSpring = new JPanel(new SpringLayout());

        baseTextField = new JTextField(5);
        discoSpring.add(new JLabel("Additional Units Destroyed:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>" + "Number of disconnecting players' units destoyed,<br>" + "in addition to those already dead from IPUs.</html>");
        baseTextField.setName("DisconnectionAddUnitsDestroyed");
        discoSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        discoSpring.add(new JLabel("Additional Units Ejected:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>" + "Number of disconnecting players' units ejected,<br>" + "in addition to those already marked as salvage<br>" + "by the in-game status updates.</html>");
        baseTextField.setName("DisconnectionAddUnitsSalvage");
        discoSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        discoSpring.add(new JLabel("Time Before Report:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>" + "Amount of time, in seconds, disconnecting player<br>" + "has to return before games is autoresolved.</html>");
        baseTextField.setName("DisconnectionTimeToReport");
        discoSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        discoSpring.add(new JLabel("Reconnection Grace Period:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>" + "Amount of time added (sec) back to disconnection counter when<br>" + "a player returns. For example, if there are 10 minutes<br>" + "before a report and a grace period of 3 minutes, a player<br>" + "who leaves for 6 minutes and then returns will have 6 minutes<br>" + "(10m to report - 6 minutes offline + 2 min grace period = 6 min)<br>" + "to return if he leaves the server a second time. This keeps<br>" + "players who need to leave/recon because of a crach from being<br>" + "penalized but prevents people from repeatedly disconnecting for<br>" + "long periods of time as a delaying tactic.");
        baseTextField.setName("DisconnectionGracePeriod");
        discoSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        discoSpring.add(new JLabel("% of normal pay:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>" + "Percentage of normal pay (0-100) given to players<br>" + "who disconnect. Loser modifiers are applied normally.</html>");
        baseTextField.setName("DisconnectionPayPercentage");
        discoSpring.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(discoSpring, 5, 2);
        disconenctionPanel.add(discoSpring);

        /*
         * ADVANCE REPAIR setup
         */
        JPanel masterBox = new JPanel();
        JPanel repairSpring = new JPanel(new SpringLayout());
        JPanel repairSpring2 = new JPanel(new SpringLayout());

        masterBox.setLayout(new BoxLayout(masterBox, BoxLayout.Y_AXIS));

        ButtonGroup repairTypes = new ButtonGroup();

        baseRadioButton = new JRadioButton("Use Techs");

        baseRadioButton.setToolTipText("<html>Use Techs as bays<br>NOTE: Save all player files and reboot<br>When turning on or off.</html>");
        baseRadioButton.setName("UseTechRepair");
        repairTypes.add(baseRadioButton);
        repairSpring.add(baseRadioButton);

        baseRadioButton = new JRadioButton("Use Statistcal Repair");

        baseRadioButton.setToolTipText("<html>Units Get damaged but they are repair all at once if the player chooses so.<br>NOTE: Save all player files and reboot<br>When turning on or off.</html>");
        baseRadioButton.setName("UseSimpleRepair");
        baseRadioButton.setSelected(true);
        repairTypes.add(baseRadioButton);
        repairSpring.add(baseRadioButton);

        baseRadioButton = new JRadioButton("Use Advanced Repair");

        baseRadioButton.setToolTipText("<html>Use Advanced Repair?<br>NOTE: Save all player files and reboot<br>When turning on or off.</html>");
        baseRadioButton.setName("UseAdvanceRepair");
        repairTypes.add(baseRadioButton);
        repairSpring.add(baseRadioButton);

        BaseCheckBox = new JCheckBox("Allow Reg Techs To Be Hired");

        BaseCheckBox.setToolTipText("Allow players to hire reg techs");
        BaseCheckBox.setName("AllowRegTechsToBeHired");
        repairSpring.add(BaseCheckBox);

        // Allow players to donate and sell damaged units.
        BaseCheckBox = new JCheckBox("Allow Selling Of Damaged Units");

        BaseCheckBox.setToolTipText("Allow players to sell damaged units on the BM");
        BaseCheckBox.setEnabled(false);
        BaseCheckBox.setName("AllowSellingOfDamagedUnits");
        repairSpring.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Allow Donating Of Damaged Units");

        BaseCheckBox.setToolTipText("Allow players to donate damaged units to their factions");
        BaseCheckBox.setName("AllowDonatingOfDamagedUnits");
        repairSpring.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Use Parts For Repairs");

        BaseCheckBox.setToolTipText("Parts are pulled from the players cache to use for repairs.");
        BaseCheckBox.setName("UsePartsRepair");
        repairSpring.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Non-Faction Units cost extra techs");
        BaseCheckBox.setToolTipText("Only used with Tech Repairs.  Increases the tech cost of non-faction units.");
        BaseCheckBox.setName("UseNonFactionUnitsIncreasedTechs");
        repairSpring.add(BaseCheckBox);
        
        BaseCheckBox = new JCheckBox("Do not allow salvage of undamaged units");
        BaseCheckBox.setToolTipText("Only used with Tech Repairs.  Players may not salvage undamaged units");
        BaseCheckBox.setName("DisallowFreshUnitSalvage");
        repairSpring.add(BaseCheckBox);

        SpringLayoutHelper.setupSpringGrid(repairSpring, 3);

        // The base cost to hire a tech.

        baseTextField = new JTextField(5);
        repairSpring2.add(new JLabel("Green Tech Hire Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Cost to hire 1 green tech");
        baseTextField.setName("GreenTechHireCost");
        repairSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        repairSpring2.add(new JLabel("Reg Tech Hire Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Cost to hire 1 reg tech");
        baseTextField.setName("RegTechHireCost");
        repairSpring2.add(baseTextField);

        // The base cost for each of these techs to do a job.
        baseTextField = new JTextField(5);
        repairSpring2.add(new JLabel("Green Tech Pay:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>The base cost for a green tech to do a repair per crit.<br>Cost is doubled for the first crit.<br>i.e. if set to 1 it would cost 4 for 3 crits.</html>");
        baseTextField.setName("GreenTechRepairCost");
        repairSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        repairSpring2.add(new JLabel("Reg Tech Pay:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>The base cost for a reg tech to do a repair per crit.<br>Cost is doubled for the first crit.<br>i.e. if set to 1 it would cost 4 for 3 crits.</html>");
        baseTextField.setName("RegTechRepairCost");
        repairSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        repairSpring2.add(new JLabel("Vet Tech Pay:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>The base cost for a vet tech to do a repair per crit.<br>Cost is doubled for the first crit.<br>i.e. if set to 1 it would cost 4 for 3 crits.</html>");
        baseTextField.setName("VetTechRepairCost");
        repairSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        repairSpring2.add(new JLabel("Elite Tech Pay:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>The base cost for a elite tech to do a repair per crit.<br>Cost is doubled for the first crit.<br>i.e. if set to 1 it would cost 4 for 3 crits.</html>");
        baseTextField.setName("EliteTechRepairCost");
        repairSpring2.add(baseTextField);

        // Hanger buy and sell back costs.
        baseTextField = new JTextField(5);
        repairSpring2.add(new JLabel("Bay Lease Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Security deposit for a new bay");
        baseTextField.setName("CostToBuyNewBay");
        repairSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        repairSpring2.add(new JLabel("Bay Deposit Return:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount of the Deposit a player gets back when returning a bay");
        baseTextField.setName("BaySellBackPrice");
        repairSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        repairSpring2.add(new JLabel("Max Bays to Lease:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>The Maximum number of bays a player can buy<br>Set to -1 for unlimited.<br>A player with more then the max will lose all the bays above the max.</html>");
        baseTextField.setName("MaxBaysToBuy");
        repairSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        repairSpring2.add(new JLabel("Time for Each Repair:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>amount of time, in seconds, that it takes to repair each damaged crit<br>Note:When repairing engines all crits are counted<br>no matter how many are damaged</html>");
        baseTextField.setName("TimeForEachRepairPoint");
        repairSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        repairSpring2.add(new JLabel("Chance Tech Dies:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>The chance out of 100 that a tech dies when a 2 is rolled on a repair roll</html>");
        baseTextField.setName("ChanceTechDiesOnFailedRepair");
        repairSpring2.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(repairSpring2, 6);

        JPanel masterPanel = new JPanel();

        JPanel masterArmorPanel = new JPanel();
        JPanel armorPanel = new JPanel(new SpringLayout());

        JPanel masterInternalPanel = new JPanel();
        JPanel internalPanel = new JPanel(new SpringLayout());

        masterPanel.setLayout(new BoxLayout(masterPanel, BoxLayout.X_AXIS));
        masterArmorPanel.setLayout(new BoxLayout(masterArmorPanel, BoxLayout.Y_AXIS));
        masterInternalPanel.setLayout(new BoxLayout(masterInternalPanel, BoxLayout.Y_AXIS));

        baseTextField = new JTextField(5);
        armorPanel.add(new JLabel("Standard:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Cost to repair 1 point of standard armor.<br>Note This is a double field!</html>");
        baseTextField.setName("CostPointStandard");
        armorPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        armorPanel.add(new JLabel("FF:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Cost to repair 1 point of Ferro-Fibrous armor..<br>Note This is a double field!</html>");
        baseTextField.setName("CostPointFF");
        armorPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        armorPanel.add(new JLabel("Reactive:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Cost to repair 1 point of reactive armor..<br>Note This is a double field!</html>");
        baseTextField.setName("CostPointReactive");
        armorPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        armorPanel.add(new JLabel("Reflective:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Cost to repair 1 point of reflective armor..<br>Note This is a double field!</html>");
        baseTextField.setName("CostPointReflective");
        armorPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        armorPanel.add(new JLabel("Hardened:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Cost to repair 1 point of hardened armor..<br>Note This is a double field!</html>");
        baseTextField.setName("CostPointHardened");
        armorPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        armorPanel.add(new JLabel("Light FF:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Cost to repair 1 point of light FF armor..<br>Note This is a double field!</html>");
        baseTextField.setName("CostPointLFF");
        armorPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        armorPanel.add(new JLabel("Heavy FF:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Cost to repair 1 point of heavy FF armor..<br>Note This is a double field!</html>");
        baseTextField.setName("CostPointHFF");
        armorPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        armorPanel.add(new JLabel("Patchwork:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Cost to repair 1 point of patchwork armor..<br>Note This is a double field!</html>");
        baseTextField.setName("CostPointPatchwork");
        armorPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        armorPanel.add(new JLabel("Stealth:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Cost to repair 1 point of stealth armor..<br>Note This is a double field!</html>");
        baseTextField.setName("CostPointStealth");
        armorPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        armorPanel.add(new JLabel("FF Proto:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Cost to repair 1 point of Ferro-Fibrous Prototype armor..<br>Note This is a double field!</html>");
        baseTextField.setName("CostPointFFProto");
        armorPanel.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(armorPanel, 6);

        masterArmorPanel.add(new JLabel("Armor"));
        masterArmorPanel.add(armorPanel);

        baseTextField = new JTextField(5);
        internalPanel.add(new JLabel("Standard:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Cost to repair 1 point of standard internal..<br>Note This is a double field!</html>");
        baseTextField.setName("CostPointStandardIS");
        internalPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        internalPanel.add(new JLabel("Endo:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Cost to repair 1 point of endo internal..<br>Note This is a double field!</html>");
        baseTextField.setName("CostPointEndoIS");
        internalPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        internalPanel.add(new JLabel("Endo Proto:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Cost to repair 1 point of endo prototype internal.<br>Note This is a double field!</html>");
        baseTextField.setName("CostPointEndoProtoIS");
        internalPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        internalPanel.add(new JLabel("Reinforced:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Cost to repair 1 point of reinforced internal.<br>Note This is a double field!</html>");
        baseTextField.setName("CostPointReinforcedIS");
        internalPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        internalPanel.add(new JLabel("Composite:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Cost to repair 1 point of composite internal.<br>Note This is a double field!</html>");
        baseTextField.setName("CostPointCompositeIS");
        internalPanel.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(internalPanel, 4);

        masterInternalPanel.add(new JLabel("Internal"));
        masterInternalPanel.add(internalPanel);

        masterPanel.add(masterArmorPanel);
        masterPanel.add(masterInternalPanel);

        JPanel masterEquipmentPanel = new JPanel();
        JPanel equipmentTextPanel = new JPanel();
        JPanel equipmentPanel = new JPanel(new SpringLayout());
        JPanel replacementTextPanel = new JPanel();
        JPanel replacementPanel = new JPanel(new SpringLayout());

        masterEquipmentPanel.setLayout(new BoxLayout(masterEquipmentPanel, BoxLayout.Y_AXIS));

        equipmentTextPanel.add(new JLabel("Critical Slot Repair Costs"));

        baseTextField = new JTextField(5);
        equipmentPanel.add(new JLabel("System:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<Html>Cost to repair each system crit.<br>Note Double Field</html>");
        baseTextField.setName("SystemCritRepairCost");
        equipmentPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        equipmentPanel.add(new JLabel("Equipment:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Cost to repair each misc equipment crit, i.e. heat sinks, actuators, ammo bins<br>Note Double Field</html>");
        baseTextField.setName("EquipmentCritRepairCost");
        equipmentPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        equipmentPanel.add(new JLabel("Engine:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Cost to repair each engine crit.<br>Note when repairing engines all crits are counted<br>I.E. XL engines will be more expensive then Standard<br>Note Double Field</html>");
        baseTextField.setName("EngineCritRepairCost");
        equipmentPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        equipmentPanel.add(new JLabel("Ballistic:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Cost to repair each ballistic weapon crit.<br>Note Double Field</html>");
        baseTextField.setName("BallisticCritRepairCost");
        equipmentPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        equipmentPanel.add(new JLabel("Energy:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Cost to repair each energy weapon crit.<br>Note Double Field</html>");
        baseTextField.setName("EnergyWeaponCritRepairCost");
        equipmentPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        equipmentPanel.add(new JLabel("Missle:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Cost to repair each missle weapon crit.<br>Note Double Field</html>");
        baseTextField.setName("MissileCritRepairCost");
        equipmentPanel.add(baseTextField);
        SpringLayoutHelper.setupSpringGrid(equipmentPanel, 6);

        replacementTextPanel.add(new JLabel("Critical Slot Replacement Costs"));

        baseTextField = new JTextField(5);
        replacementPanel.add(new JLabel("System:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<Html>Cost to replace each system crit.<br>Note Double Field</html>");
        baseTextField.setName("SystemCritReplaceCost");
        replacementPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        replacementPanel.add(new JLabel("Equipment:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Cost to replace each misc replacement crit, i.e. heat sinks, actuators, ammo bins<br>Note Double Field</html>");
        baseTextField.setName("EquipmentCritReplaceCost");
        replacementPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        replacementPanel.add(new JLabel("Ballistic:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Cost to replace each ballistic weapon crit.<br>Note Double Field</html>");
        baseTextField.setName("BallisticCritReplaceCost");
        replacementPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        replacementPanel.add(new JLabel("Energy:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Cost to replace each energy weapon crit.<br>Note Double Field</html>");
        baseTextField.setName("EnergyWeaponCritReplaceCost");
        replacementPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        replacementPanel.add(new JLabel("Missle:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Cost to replace each missle weapon crit.<br>Note Double Field</html>");
        baseTextField.setName("MissileCritReplaceCost");
        replacementPanel.add(baseTextField);
        SpringLayoutHelper.setupSpringGrid(replacementPanel, 6);

        masterEquipmentPanel.add(equipmentTextPanel);
        masterEquipmentPanel.add(equipmentPanel);
        masterEquipmentPanel.add(replacementTextPanel);
        masterEquipmentPanel.add(replacementPanel);

        JPanel masterCostModPanel = new JPanel();
        JPanel CostModTextPanel = new JPanel();
        JPanel CostModPanel = new JPanel(new SpringLayout());

        masterCostModPanel.setLayout(new BoxLayout(masterCostModPanel, BoxLayout.Y_AXIS));

        CostModTextPanel.add(new JLabel("Cost Mods For Buying Damaged Unit"));

        baseTextField = new JTextField(5);
        CostModPanel.add(new JLabel("Armor:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<Html>Cost Modifier to buy a used unit with armor damage<br>Note Double Field</html>");
        baseTextField.setName("CostModifierToBuyArmorDamagedUnit");
        CostModPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        CostModPanel.add(new JLabel("Crit:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Cost Modifier to buy a used unit with damaged crits<br>Note Double Field</html>");
        baseTextField.setName("CostModifierToBuyCritDamagedUnit");
        CostModPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        CostModPanel.add(new JLabel("Engined:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Cost Modifier to buy a used unit that has been engined.<br>Note Double Field</html>");
        baseTextField.setName("CostModifierToBuyEnginedUnit");
        CostModPanel.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(CostModPanel, 6);

        masterCostModPanel.add(CostModTextPanel);
        masterCostModPanel.add(CostModPanel);

        masterBox.add(repairSpring);
        masterBox.add(repairSpring2);
        masterBox.add(masterPanel);
        masterBox.add(masterEquipmentPanel);
        masterBox.add(masterCostModPanel);

        advancedRepairPanel.add(masterBox);

        /*
         * LOSS COMPENSATION setup defaults.setProperty("", "0");//int defaults.setProperty("", ".50");//float defaults.setProperty("", ".50");//float defaults.setProperty("", "1.0");//float defaults.setProperty("", "1.0");//float defaults.setProperty("", "1.0");//float defaults.setProperty("", "1.0");//float defaults.setProperty("", "1.0");//float defaults.setProperty("", "1.0");//float defaults.setProperty("", "0");//int. 0 ensures no payment by default.
         */
        JPanel lossCompSpring = new JPanel(new SpringLayout());

        baseTextField = new JTextField(5);
        lossCompSpring.add(new JLabel("Base Loss Payment:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base compensation given for losses. float value.");
        baseTextField.setName("BaseUnitLossPayment");
        lossCompSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        lossCompSpring.add(new JLabel("Variable Loss Payment:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>" + "Percentage of the cost of a similar new unit to the added to the Base<br>" + "Payment. For example, if Base is 10 and a unit costs 40 CBills w/ a Variable<br>" + "Loss Payment of 0.50 the starting compensation (before multipliers and<br>" + "caps) will be (40 * .5) = 20 + 10 for a total of 30 CBills. float value</html>.");
        baseTextField.setName("NewCostMultiUnitLossPayment");
        lossCompSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        lossCompSpring.add(new JLabel("Salvage Multi:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>" + "Multiplier applied to loss compensation (base + variable) if a unit is taken<br>" + "by the enemy (instead of destroyed). Example: a unit that has 30 CBill base + var<br>" + "is taken by an enemy and a multi of .5 is applied, reducing loss compensation to<br>" + "15 CBills (30 * .5 = 15) before other multis and caps.</html>");
        baseTextField.setName("SalvageMultiToUnitLossPayment");
        lossCompSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        lossCompSpring.add(new JLabel("Mek Multi:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>" + "Multiplier applied to loss compensation (base + variable) if unit in question is a mek.<br>" + "Example 1: Mek w 40 CBill base + var is destroyed * 1.25 mek multi = 50 CBills loss comp.<br>" + "Example 2: Mek w 40 CBill base + var is destroyed * 0.5 mek multi = 20 CBills loss comp.<br>" + "Note: Other multis and caps are also applied/checked. float value.</html>");
        baseTextField.setName("MekMultiToUnitLossPayment");
        lossCompSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        lossCompSpring.add(new JLabel("Veh Multi:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>" + "Multiplier applied to loss compensation (base + variable) if unit in question is a vehicle.<br>" + "Example 1: Veh w 20 CBill base + var is destroyed * 1.25 mek multi = 25 CBills loss comp.<br>" + "Example 2: Veh w 20 CBill base + var is destroyed * 0.5 mek multi = 10 CBills loss comp.<br>" + "Note: Other multis and caps are also applied/checked. float value.</html>");
        baseTextField.setName("VehMultiToUnitLossPayment");
        lossCompSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        lossCompSpring.add(new JLabel("Proto Multi:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("See Mek Multi and Veh Multi examples. float value.");
        baseTextField.setName("ProtoMultiToUnitLossPayment");
        lossCompSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        lossCompSpring.add(new JLabel("BA Multi:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("See Mek Multi and Veh Multi examples. float value.");
        baseTextField.setName("BAMultiToUnitLossPayment");
        lossCompSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        lossCompSpring.add(new JLabel("Inf Multi:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("See Mek Multi and Veh Multi examples. float value.");
        baseTextField.setName("InfMultiToUnitLossPayment");
        lossCompSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        lossCompSpring.add(new JLabel("Aero Multi:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("See Mek Multi and Veh Multi examples. float value.");
        baseTextField.setName("AeroMultiToUnitLossPayment");
        lossCompSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        lossCompSpring.add(new JLabel("Loss Cap (Multiple):", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>" + "Max amount of compensaton, expressed as a multiple of the cost of a similar<br>" + "new unit from the player's faction. The Flat cap is checked AFTER the multiple cap.<br><br>" + "Example 1: A new light mek costs 20 CBills. Base comp is 10 and var comp is<br>" + "set to .50, giving a base + var of 20 CBills. The Multiple Cap is .75 of a<br>" + "new unit's cost (20 * .75 = 15), so the compensation is capped at 15 CBills.<br>" + "<br>" + "Example 2: A new assault mek costs 100 CBills. Base compensation is 0 and var comp<br>" + "is .60, for a base + var of 60 CBills. There's also a Mek Multiplier if 1.50, which<br>" + "boosts compensation to 90 CBills. The Multiple Cap is .80 (100 * .80 = 80), so the<br>" + "compensation is reduced to 80 CBills.</html>");
        baseTextField.setName("NewCostMultiMaxUnitLossPayment");
        lossCompSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        lossCompSpring.add(new JLabel("Loss Cap (Flat):", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>" + "Max amount of compensaton, expressed as a a simple integer. The Flat cap is checked<br>" + "AFTER the multiple cap. Battle Loss Compensation can be disabled by setting a flat cap<br>" + "of 0 CBills.<br><br>" + "Example 1: A assault light mek costs 100 CBills. Base comp is 10 and var comp is<br>" + "set to .50, giving a base + var of 60 CBills. The Flat Cap 40, so the compensation is<br>" + "reduced to 40 CBills.<br>" + "<br>" + "Example 2: A new light mek costs 20 CBills. Base compensation is 10 and var comp<br>" + "is .50, for a base + var of 20 CBills. The flat cap is 40 CBills. Unless there's<br>" + "another multiplier (mek, salvage) or the payment is reduced by the Multiplier Cap,<br>" + "the player will receive more money (40 CBills) in loss comp than it cost to buy his<br>"
                + "unit in the first place. This would be bad. So set both the flat and multi caps!</html>");
        baseTextField.setName("FlatMaxUnitLossPayment");
        lossCompSpring.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(lossCompSpring, 4);
        lossCompensationPanel.add(lossCompSpring);

        /*
         * Database Configuration Panel Construction
         */
        JPanel dbSpring = new JPanel(new SpringLayout());

        BaseCheckBox = new JCheckBox("Store Unit/Pilot Histories");

        BaseCheckBox.setToolTipText("Stores Unit and Pilot histories in the database");
        BaseCheckBox.setName("StoreUnitHistoryInDatabase");
        dbSpring.add(BaseCheckBox);

        SpringLayoutHelper.setupSpringGrid(dbSpring, 2);
        dbPanel.add(dbSpring);

        /*
         * Single Player Faction Configuration Panel Construction
         */
        JPanel checkBoxPanel = new JPanel(new SpringLayout());
        JPanel playerFactionPanel = new JPanel(new SpringLayout());
        JPanel playerFactionPanel2 = new JPanel(new SpringLayout());

        masterPanel = new JPanel();
        masterPanel.setLayout(new BoxLayout(masterPanel, BoxLayout.Y_AXIS));

        BaseCheckBox = new JCheckBox("Single Player Factions");
        BaseCheckBox.setToolTipText("If this is checked then each player will have their own faction");
        BaseCheckBox.setName("AllowSinglePlayerFactions");
        checkBoxPanel.add(BaseCheckBox);

        SpringLayoutHelper.setupSpringGrid(checkBoxPanel, 1);

        baseTextField = new JTextField(5);
        playerFactionPanel.add(new JLabel("Max Faction Name:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Max Length for a faction name.");
        baseTextField.setName("MaxFactionName");
        playerFactionPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        playerFactionPanel.add(new JLabel("Max Short Name:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Max Length for a factions short name");
        baseTextField.setName("MaxFactionShortName");
        playerFactionPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        playerFactionPanel.add(new JLabel("Base Factory Refresh:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base Refresh Rate for New Factories");
        baseTextField.setName("BaseFactoryRefreshRate");
        playerFactionPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        playerFactionPanel.add(new JLabel("Base Components:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("How many components the new faction starts with for each type/class");
        baseTextField.setName("BaseFactoryComponents");
        playerFactionPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        playerFactionPanel.add(new JLabel("Base Common Table Chances:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Base number of shares for the common build table<br>in all of the starting factions build tables.</html>");
        baseTextField.setName("BaseCommonBuildTableShares");
        playerFactionPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        playerFactionPanel.add(new JLabel("Starting Bays:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Number of bays the players starting planet gets.</html>");
        baseTextField.setName("StartingPlanetBays");
        playerFactionPanel.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(playerFactionPanel, 4);

        for (int type = 0; type < Unit.MAXBUILD; type++) {
            for (int weight = 0; weight <= Unit.ASSAULT; weight++) {
                baseTextField = new JTextField(5);
                playerFactionPanel2.add(new JLabel("Starting " + Unit.getWeightClassDesc(weight) + " " + Unit.getTypeClassDesc(type) + " Factory:", SwingConstants.TRAILING));
                baseTextField.setToolTipText("Number of " + Unit.getWeightClassDesc(weight) + " " + Unit.getTypeClassDesc(type) + " factories a new faction starts with.");
                baseTextField.setName("Starting" + Unit.getWeightClassDesc(weight) + Unit.getTypeClassDesc(type) + "Factory");
                playerFactionPanel2.add(baseTextField);
            }
        }

        SpringLayoutHelper.setupSpringGrid(playerFactionPanel2, 4);

        masterPanel.add(checkBoxPanel);
        masterPanel.add(playerFactionPanel);
        masterPanel.add(playerFactionPanel2);
        singlePlayerFactionPanel.add(masterPanel);

        /*
         * Technology Research Configuration Panel Construction
         */
        masterPanel = new JPanel(new SpringLayout());

        baseTextField = new JTextField(5);
        masterPanel.add(new JLabel("Points Per Level:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Total Number of Tech Points a faction needs to move<br>to the next tech level.</html>");
        baseTextField.setName("TechPointsNeedToLevel");
        masterPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        masterPanel.add(new JLabel(mwclient.moneyOrFluMessage(true, false, -1, false) + " Per Tech Point:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Amount of " + mwclient.moneyOrFluMessage(true, false, -1, false) + " need for 1 tech point</html>");
        baseTextField.setName("TechPointCost");
        masterPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        masterPanel.add(new JLabel(mwclient.moneyOrFluMessage(false, false, -1, false) + " Per Tech Point:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Total Number of " + mwclient.moneyOrFluMessage(false, false, -1, false) + " needed to buy 1 tech point.</html>");
        baseTextField.setName("TechPointFlu");
        masterPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        masterPanel.add(new JLabel("Point " + mwclient.moneyOrFluMessage(true, false, -1, false) + " cost mod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Modifier to the cost o a tech point for each level above 1 the faction is.</html>");
        baseTextField.setName("TechLevelTechPointCostModifier");
        masterPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        masterPanel.add(new JLabel("Point " + mwclient.moneyOrFluMessage(false, false, -1, false) + " cost mod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Modifier to the cost o a tech point for each level above 1 the faction is.</html>");
        baseTextField.setName("TechLevelTechPointFluModifier");
        masterPanel.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(masterPanel, 2);
        technologyResearchPanel.add(masterPanel);

        /*
         * Unit Research Configuration Panel Construction
         */
        JPanel mainResearchPanel = new JPanel(new SpringLayout());
        JPanel researchPanel1 = new JPanel(new SpringLayout());
        JPanel researchPanel2 = new JPanel(new SpringLayout());
        masterPanel = new JPanel();
        masterPanel.setLayout(new BoxLayout(masterPanel, BoxLayout.Y_AXIS));

        baseTextField = new JTextField(5);
        mainResearchPanel.add(new JLabel("Base Research " + mwclient.moneyOrFluMessage(true, false, -1, false) + ":", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>" + mwclient.moneyOrFluMessage(true, false, -1, false) + " to buy 1 research point</html>");
        baseTextField.setName("BaseResearchCost");
        mainResearchPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        mainResearchPanel.add(new JLabel("Base Research " + mwclient.moneyOrFluMessage(false, false, -1, false) + ":", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>" + mwclient.moneyOrFluMessage(true, false, -1, false) + " to buy 1 research point</html>");
        baseTextField.setName("BaseResearchFlu");
        mainResearchPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        mainResearchPanel.add(new JLabel("Tech Level " + mwclient.moneyOrFluMessage(true, false, -1, false) + ":", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>" + mwclient.moneyOrFluMessage(true, false, -1, false) + " modifier for each<br>tech level above 1 that the faciton is</html>");
        baseTextField.setName("ResearchTechLevelCostModifer");
        mainResearchPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        mainResearchPanel.add(new JLabel("Tech Level " + mwclient.moneyOrFluMessage(false, false, -1, false) + ":", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>" + mwclient.moneyOrFluMessage(true, false, -1, false) + " modifier for each<br>tech level above 1 that the faction is</html>");
        baseTextField.setName("ResearchTechLevelFluModifer");
        mainResearchPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        mainResearchPanel.add(new JLabel("Max Research:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Max number of shares per unit a faction can research.</html>");
        baseTextField.setName("MaxUnitResearchPoints");
        mainResearchPanel.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(mainResearchPanel, 6);

        for (int type = 0; type < Unit.MAXBUILD; type++) {
            baseTextField = new JTextField(5);
            researchPanel1.add(new JLabel(Unit.getTypeClassDesc(type) + " unit " + mwclient.moneyOrFluMessage(true, false, -1, false) + ":", SwingConstants.TRAILING));
            baseTextField.setToolTipText("<HTML>" + mwclient.moneyOrFluMessage(true, false, -1, false) + " modifier for " + Unit.getTypeClassDesc(type) + " units</html>");
            baseTextField.setName("ResearchCostModifier" + Unit.getTypeClassDesc(type));
            researchPanel1.add(baseTextField);
        }

        for (int size = 0; size <= Unit.ASSAULT; size++) {
            baseTextField = new JTextField(5);
            researchPanel1.add(new JLabel(Unit.getWeightClassDesc(size) + " unit " + mwclient.moneyOrFluMessage(true, false, -1, false) + ":", SwingConstants.TRAILING));
            baseTextField.setToolTipText("<HTML>" + mwclient.moneyOrFluMessage(true, false, -1, false) + " modifier for " + Unit.getWeightClassDesc(size) + " units</html>");
            baseTextField.setName("ResearchCostModifier" + Unit.getWeightClassDesc(size));
            researchPanel1.add(baseTextField);
        }

        for (int type = 0; type < Unit.MAXBUILD; type++) {
            baseTextField = new JTextField(5);
            researchPanel2.add(new JLabel(Unit.getTypeClassDesc(type) + " unit " + mwclient.moneyOrFluMessage(false, false, -1, false) + ":", SwingConstants.TRAILING));
            baseTextField.setToolTipText("<HTML>" + mwclient.moneyOrFluMessage(true, false, -1, false) + " modifier for " + Unit.getTypeClassDesc(type) + " units</html>");
            baseTextField.setName("ResearchFluModifier" + Unit.getTypeClassDesc(type));
            researchPanel2.add(baseTextField);
        }

        for (int size = 0; size <= Unit.ASSAULT; size++) {
            baseTextField = new JTextField(5);
            researchPanel2.add(new JLabel(Unit.getWeightClassDesc(size) + " unit " + mwclient.moneyOrFluMessage(false, false, -1, false) + ":", SwingConstants.TRAILING));
            baseTextField.setToolTipText("<HTML>" + mwclient.moneyOrFluMessage(false, false, -1, false) + " modifier for " + Unit.getWeightClassDesc(size) + " units</html>");
            baseTextField.setName("ResearchFluModifier" + Unit.getWeightClassDesc(size));
            researchPanel2.add(baseTextField);
        }

        SpringLayoutHelper.setupSpringGrid(researchPanel1, 6);
        SpringLayoutHelper.setupSpringGrid(researchPanel2, 6);

        masterPanel.add(mainResearchPanel);
        masterPanel.add(researchPanel1);
        masterPanel.add(researchPanel2);
        unitResearchPanel.add(masterPanel);

        /*
         * Unit Research Configuration Panel Construction
         */
        JPanel mainPurchasePanel = new JPanel(new SpringLayout());
        JPanel purchasePanel1 = new JPanel(new SpringLayout());
        JPanel purchasePanel2 = new JPanel(new SpringLayout());
        masterPanel = new JPanel();
        masterPanel.setLayout(new BoxLayout(masterPanel, BoxLayout.Y_AXIS));

        baseTextField = new JTextField(5);
        mainPurchasePanel.add(new JLabel("New Factory " + mwclient.moneyOrFluMessage(true, false, -1, false) + ":", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>" + mwclient.moneyOrFluMessage(true, false, -1, false) + " to buy 1 factory</html>");
        baseTextField.setName("NewFactoryBaseCost");
        mainPurchasePanel.add(baseTextField);

        baseTextField = new JTextField(5);
        mainPurchasePanel.add(new JLabel("New Factory " + mwclient.moneyOrFluMessage(false, false, -1, false) + ":", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>" + mwclient.moneyOrFluMessage(true, false, -1, false) + " to buy 1 factory</html>");
        baseTextField.setName("NewFactoryBaseFlu");
        mainPurchasePanel.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(mainPurchasePanel, 4);

        for (int type = 0; type < Unit.MAXBUILD; type++) {
            baseTextField = new JTextField(5);
            purchasePanel1.add(new JLabel(Unit.getTypeClassDesc(type) + " unit " + mwclient.moneyOrFluMessage(true, false, -1, false) + ":", SwingConstants.TRAILING));
            baseTextField.setToolTipText("<HTML>" + mwclient.moneyOrFluMessage(true, false, -1, false) + " modifier for " + Unit.getTypeClassDesc(type) + " unit factory</html>");
            baseTextField.setName("NewFactoryCostModifier" + Unit.getTypeClassDesc(type));
            purchasePanel1.add(baseTextField);
        }

        for (int size = 0; size <= Unit.ASSAULT; size++) {
            baseTextField = new JTextField(5);
            purchasePanel1.add(new JLabel(Unit.getWeightClassDesc(size) + " unit " + mwclient.moneyOrFluMessage(true, false, -1, false) + ":", SwingConstants.TRAILING));
            baseTextField.setToolTipText("<HTML>" + mwclient.moneyOrFluMessage(true, false, -1, false) + " modifier for " + Unit.getWeightClassDesc(size) + " unit factory</html>");
            baseTextField.setName("NewFactoryCostModifier" + Unit.getWeightClassDesc(size));
            purchasePanel1.add(baseTextField);
        }
        SpringLayoutHelper.setupSpringGrid(purchasePanel1, 6);

        for (int type = 0; type < Unit.MAXBUILD; type++) {
            baseTextField = new JTextField(5);
            purchasePanel2.add(new JLabel(Unit.getTypeClassDesc(type) + " unit " + mwclient.moneyOrFluMessage(false, false, -1, false) + ":", SwingConstants.TRAILING));
            baseTextField.setToolTipText("<HTML>" + mwclient.moneyOrFluMessage(true, false, -1, false) + " modifier for " + Unit.getTypeClassDesc(type) + " unit factory</html>");
            baseTextField.setName("NewFactoryFluModifier" + Unit.getTypeClassDesc(type));
            purchasePanel2.add(baseTextField);
        }

        for (int size = 0; size <= Unit.ASSAULT; size++) {
            baseTextField = new JTextField(5);
            purchasePanel2.add(new JLabel(Unit.getWeightClassDesc(size) + " unit " + mwclient.moneyOrFluMessage(false, false, -1, false) + ":", SwingConstants.TRAILING));
            baseTextField.setToolTipText("<HTML>" + mwclient.moneyOrFluMessage(false, false, -1, false) + " modifier for " + Unit.getWeightClassDesc(size) + " unit factory</html>");
            baseTextField.setName("NewFactoryFluModifier" + Unit.getWeightClassDesc(size));
            purchasePanel2.add(baseTextField);
        }

        SpringLayoutHelper.setupSpringGrid(purchasePanel2, 6);

        masterPanel.add(mainPurchasePanel);
        masterPanel.add(purchasePanel1);
        masterPanel.add(purchasePanel2);
        factoryPurchasePanel.add(masterPanel);
        
        // unitLimitsPanel construction
        
        JPanel ulBox = new JPanel();
        ulBox.setLayout(new BoxLayout(ulBox, BoxLayout.Y_AXIS));
        JPanel ulMekPanel = new JPanel();
        JPanel ulVehiclePanel = new JPanel();
        JPanel ulInfantryPanel = new JPanel();
        JPanel ulProtoPanel = new JPanel();
        JPanel ulBAPanel = new JPanel();
        JPanel ulAeroPanel = new JPanel();
        JPanel mekBox = new JPanel();
        JPanel vehicleBox = new JPanel();
        JPanel infantryBox = new JPanel();
        JPanel protoBox = new JPanel();
        JPanel baBox = new JPanel();
        JPanel aeroBox = new JPanel();
        mekBox.setLayout(new BoxLayout(mekBox, BoxLayout.Y_AXIS));
        vehicleBox.setLayout(new BoxLayout(vehicleBox, BoxLayout.Y_AXIS));
        infantryBox.setLayout(new BoxLayout(infantryBox, BoxLayout.Y_AXIS));
        protoBox.setLayout(new BoxLayout(protoBox, BoxLayout.Y_AXIS));
        baBox.setLayout(new BoxLayout(baBox, BoxLayout.Y_AXIS));
        aeroBox.setLayout(new BoxLayout(aeroBox, BoxLayout.Y_AXIS));
        
        mekBox.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        vehicleBox.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        infantryBox.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        protoBox.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        baBox.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        aeroBox.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        
        ulBox.add(Box.createRigidArea(new Dimension(10,10)));

        mekBox.add(new JLabel("Meks"));
        ulMekPanel.add(new JLabel("Light", SwingConstants.TRAILING));
        baseTextField = new JTextField(5);
        baseTextField.setToolTipText("Limit hangar to this many light Meks.  -1 to disable limit");
        baseTextField.setName("MaxHangarLightMeks");
        ulMekPanel.add(baseTextField);
        ulMekPanel.add(new JLabel("Medium", SwingConstants.TRAILING));
        baseTextField = new JTextField(5);
        baseTextField.setToolTipText("Limit hangar to this many Medium Meks.  -1 to disable limit");
        baseTextField.setName("MaxHangarMediumMeks");
        ulMekPanel.add(baseTextField);
        ulMekPanel.add(new JLabel("Heavy", SwingConstants.TRAILING));
        baseTextField = new JTextField(5);
        baseTextField.setToolTipText("Limit hangar to this many Heavy Meks.  -1 to disable limit");
        baseTextField.setName("MaxHangarHeavyMeks");
        ulMekPanel.add(baseTextField);
        ulMekPanel.add(new JLabel("Assault", SwingConstants.TRAILING));
        baseTextField = new JTextField(5);
        baseTextField.setToolTipText("Limit hangar to this many Assault Meks.  -1 to disable limit");
        baseTextField.setName("MaxHangarAssaultMeks");
        ulMekPanel.add(baseTextField);
        mekBox.add(ulMekPanel);
        ulBox.add(mekBox);
        ulBox.add(Box.createRigidArea(new Dimension(10,10)));
        
        vehicleBox.add(new JLabel("Vehicles"));
        ulVehiclePanel.add(new JLabel("Light", SwingConstants.TRAILING));
        baseTextField = new JTextField(5);
        baseTextField.setToolTipText("Limit hangar to this many light Vehicles.  -1 to disable limit");
        baseTextField.setName("MaxHangarLightVehicles");
        ulVehiclePanel.add(baseTextField);
        ulVehiclePanel.add(new JLabel("Medium", SwingConstants.TRAILING));
        baseTextField = new JTextField(5);
        baseTextField.setToolTipText("Limit hangar to this many Medium Vehicles.  -1 to disable limit");
        baseTextField.setName("MaxHangarMediumVehicles");
        ulVehiclePanel.add(baseTextField);
        ulVehiclePanel.add(new JLabel("Heavy", SwingConstants.TRAILING));
        baseTextField = new JTextField(5);
        baseTextField.setToolTipText("Limit hangar to this many Heavy Vehicles.  -1 to disable limit");
        baseTextField.setName("MaxHangarHeavyVehicles");
        ulVehiclePanel.add(baseTextField);
        ulVehiclePanel.add(new JLabel("Assault", SwingConstants.TRAILING));
        baseTextField = new JTextField(5);
        baseTextField.setToolTipText("Limit hangar to this many Assault Vehicles.  -1 to disable limit");
        baseTextField.setName("MaxHangarAssaultVehicles");
        ulVehiclePanel.add(baseTextField);
        vehicleBox.add(ulVehiclePanel);
        ulBox.add(vehicleBox);
        ulBox.add(Box.createRigidArea(new Dimension(10,10)));
        
        infantryBox.add(new JLabel("Infantry"));
        ulInfantryPanel.add(new JLabel("Light", SwingConstants.TRAILING));
        baseTextField = new JTextField(5);
        baseTextField.setToolTipText("Limit hangar to this many light Infantry.  -1 to disable limit");
        baseTextField.setName("MaxHangarLightInfantry");
        ulInfantryPanel.add(baseTextField);
        ulInfantryPanel.add(new JLabel("Medium", SwingConstants.TRAILING));
        baseTextField = new JTextField(5);
        baseTextField.setToolTipText("Limit hangar to this many Medium Infantry.  -1 to disable limit");
        baseTextField.setName("MaxHangarMediumInfantry");
        ulInfantryPanel.add(baseTextField);
        ulInfantryPanel.add(new JLabel("Heavy", SwingConstants.TRAILING));
        baseTextField = new JTextField(5);
        baseTextField.setToolTipText("Limit hangar to this many Heavy Infantry.  -1 to disable limit");
        baseTextField.setName("MaxHangarHeavyInfantry");
        ulInfantryPanel.add(baseTextField);
        ulInfantryPanel.add(new JLabel("Assault", SwingConstants.TRAILING));
        baseTextField = new JTextField(5);
        baseTextField.setToolTipText("Limit hangar to this many Assault Infantry.  -1 to disable limit");
        baseTextField.setName("MaxHangarAssaultInfantry");
        ulInfantryPanel.add(baseTextField);
        infantryBox.add(ulInfantryPanel);
        ulBox.add(infantryBox);
        ulBox.add(Box.createRigidArea(new Dimension(10,10)));
        
        baBox.add(new JLabel("BA"));
        ulBAPanel.add(new JLabel("Light", SwingConstants.TRAILING));
        baseTextField = new JTextField(5);
        baseTextField.setToolTipText("Limit hangar to this many light BA.  -1 to disable limit");
        baseTextField.setName("MaxHangarLightBA");
        ulBAPanel.add(baseTextField);
        ulBAPanel.add(new JLabel("Medium", SwingConstants.TRAILING));
        baseTextField = new JTextField(5);
        baseTextField.setToolTipText("Limit hangar to this many Medium BA.  -1 to disable limit");
        baseTextField.setName("MaxHangarMediumBA");
        ulBAPanel.add(baseTextField);
        ulBAPanel.add(new JLabel("Heavy", SwingConstants.TRAILING));
        baseTextField = new JTextField(5);
        baseTextField.setToolTipText("Limit hangar to this many Heavy BA.  -1 to disable limit");
        baseTextField.setName("MaxHangarHeavyBA");
        ulBAPanel.add(baseTextField);
        ulBAPanel.add(new JLabel("Assault", SwingConstants.TRAILING));
        baseTextField = new JTextField(5);
        baseTextField.setToolTipText("Limit hangar to this many Assault BA.  -1 to disable limit");
        baseTextField.setName("MaxHangarAssaultBA");
        ulBAPanel.add(baseTextField);
        baBox.add(ulBAPanel);
        ulBox.add(baBox);
        ulBox.add(Box.createRigidArea(new Dimension(10,10)));
        
        protoBox.add(new JLabel("Protomeks"));
        ulProtoPanel.add(new JLabel("Light", SwingConstants.TRAILING));
        baseTextField = new JTextField(5);
        baseTextField.setToolTipText("Limit hangar to this many light Protomeks.  -1 to disable limit");
        baseTextField.setName("MaxHangarLightProtomeks");
        ulProtoPanel.add(baseTextField);
        ulProtoPanel.add(new JLabel("Medium", SwingConstants.TRAILING));
        baseTextField = new JTextField(5);
        baseTextField.setToolTipText("Limit hangar to this many Medium Protomeks.  -1 to disable limit");
        baseTextField.setName("MaxHangarMediumProtomeks");
        ulProtoPanel.add(baseTextField);
        ulProtoPanel.add(new JLabel("Heavy", SwingConstants.TRAILING));
        baseTextField = new JTextField(5);
        baseTextField.setToolTipText("Limit hangar to this many Heavy Protomeks.  -1 to disable limit");
        baseTextField.setName("MaxHangarHeavyProtomeks");
        ulProtoPanel.add(baseTextField);
        ulProtoPanel.add(new JLabel("Assault", SwingConstants.TRAILING));
        baseTextField = new JTextField(5);
        baseTextField.setToolTipText("Limit hangar to this many Assault Protomeks.  -1 to disable limit");
        baseTextField.setName("MaxHangarAssaultProtomeks");
        ulProtoPanel.add(baseTextField);
        protoBox.add(ulProtoPanel);
        ulBox.add(protoBox);
        ulBox.add(Box.createRigidArea(new Dimension(10,10)));
        
        aeroBox.add(new JLabel("Aero"));
        ulAeroPanel.add(new JLabel("Light", SwingConstants.TRAILING));
        baseTextField = new JTextField(5);
        baseTextField.setToolTipText("Limit hangar to this many light Aero.  -1 to disable limit");
        baseTextField.setName("MaxHangarLightAero");
        ulAeroPanel.add(baseTextField);
        ulAeroPanel.add(new JLabel("Medium", SwingConstants.TRAILING));
        baseTextField = new JTextField(5);
        baseTextField.setToolTipText("Limit hangar to this many Medium Aero.  -1 to disable limit");
        baseTextField.setName("MaxHangarMediumAero");
        ulAeroPanel.add(baseTextField);
        ulAeroPanel.add(new JLabel("Heavy", SwingConstants.TRAILING));
        baseTextField = new JTextField(5);
        baseTextField.setToolTipText("Limit hangar to this many Heavy Aero.  -1 to disable limit");
        baseTextField.setName("MaxHangarHeavyAero");
        ulAeroPanel.add(baseTextField);
        ulAeroPanel.add(new JLabel("Assault", SwingConstants.TRAILING));
        baseTextField = new JTextField(5);
        baseTextField.setToolTipText("Limit hangar to this many Assault Aero.  -1 to disable limit");
        baseTextField.setName("MaxHangarAssaultAero");
        ulAeroPanel.add(baseTextField);
        aeroBox.add(ulAeroPanel);
        ulBox.add(aeroBox);
        
        JPanel ulActionsPanel = new JPanel();
        BaseCheckBox = new JCheckBox("Disable Activation");
        BaseCheckBox.setName("DisableActivationIfOverHangarLimits");
        BaseCheckBox.setToolTipText("Players over the limits cannot go active.");
        ulActionsPanel.add(BaseCheckBox);
        BaseCheckBox = new JCheckBox("Disable AFR");
        BaseCheckBox.setName("DisableAFRIfOverHangarLimits");
        BaseCheckBox.setToolTipText("Players over the limits cannot initiate or defend Attack From Reserve.");
        ulActionsPanel.add(BaseCheckBox);
        ulBox.add(ulActionsPanel);
        
        unitLimitsPanel.add(ulBox);

        // Set the actions to generate
        okayButton.setActionCommand(okayCommand);
        cancelButton.setActionCommand(cancelCommand);
        okayButton.addActionListener(this);
        cancelButton.addActionListener(this);

        /*
         * NEW OPTIONS - need to be sorted into proper menus.
         */

        // Set tool tips (balloon help)
        okayButton.setToolTipText("Save Options");
        cancelButton.setToolTipText("Exit without saving options");

        ConfigPane.addTab("Misc Options", null, miscOptionsPanel, "Misc Stuff");
        ConfigPane.addTab("Advanced Repairs", null, advancedRepairPanel, "For all your Unit Care needs");
        ConfigPane.addTab("Black Market", null, blackMarketPanel, "Black Market access controls");
        ConfigPane.addTab("BV Options", null, battleValuePanel, "Battle Value");
        ConfigPane.addTab("Combat", null, combatPanel, "Combat");
        ConfigPane.addTab("Database", null, dbPanel, "Database Configuration");
        ConfigPane.addTab("Defection", null, defectionPanel, "Defection configuration");
        ConfigPane.addTab("Direct Sales", null, directSellPanel, "Units the lifeblood of the game");
        ConfigPane.addTab("Disconnection", null, disconenctionPanel, "Disconnection autoresolution settings");
        ConfigPane.addTab("Faction", null, factionPanel, "House Stuff");
        ConfigPane.addTab("Factory Options", null, productionPanel, "Factories That Can Do");
        ConfigPane.addTab("Factory Purchase", null, factoryPurchasePanel, "Factories For Sale");
        ConfigPane.addTab("File Paths", null, pathsPanel, "Paths");
        ConfigPane.addTab("Influence", null, influencePanel, "Influence");
        ConfigPane.addTab("Loss Compensation", null, lossCompensationPanel, "Extra Payments for salvaged/destroyed units.");
        ConfigPane.addTab("No Play", null, noPlayPanel, "Personal Blacklist/Exclusion options");
        ConfigPane.addTab("Pilots", null, pilotsPanel, "Pilot Options");
        ConfigPane.addTab("Pilot Skills(Mek)", null, mekPilotSkillsPanel, "Server Configurable Pilot Skills (Mek)");
        ConfigPane.addTab("Pilot Skills", null, pilotSkillsPanel, "Server Configurable Pilot Skills");
        ConfigPane.addTab("Pilot Skill Mods", null, pilotSkillsModPanel, "Server Configurable Pilot Skills Modifiers");
        ConfigPane.addTab("Repodding", null, repodPanel, "Repod");
        ConfigPane.addTab("Rewards", null, rewardPanel, "Reward Points");
        ConfigPane.addTab("Single Player", null, singlePlayerFactionPanel, "Single Player Faction Configuration");
        ConfigPane.addTab("SOL Units", null, newbieHousePanel, "SOL Units and Attack Limits");
        ConfigPane.addTab("Support Units", null, artilleryPanel, "Artillery and Gun Emplacements and Mines oh my!");
        ConfigPane.addTab("Techs", null, technicianPanel, "Techs");
        ConfigPane.addTab("Tech Research", null, technologyResearchPanel, "Technology Research Configuration");
        ConfigPane.addTab("Units", null, unitPanel, "Care and Feeding of Your Units");
        ConfigPane.addTab("Units 2", null, unit2Panel, "More Care and Feeding of Your Units");
        ConfigPane.addTab("Unit Research", null, unitResearchPanel, "Unit Research Configuration");
        ConfigPane.addTab("Voting", null, votingPanel, "Voting Stuff");
        ConfigPane.addTab("Unit Limits", null, unitLimitsPanel, "Limits to unit ownership based on unit weightclass");
        // Create the panel that will hold the entire UI
        JPanel mainConfigPanel = new JPanel();

        // Set the user's options
        Object[] options = { okayButton, cancelButton };

        // Create the pane containing the buttons
        pane = new JOptionPane(ConfigPane, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null, options, null);

        // Create the main dialog and set the default button
        dialog = pane.createDialog(mainConfigPanel, windowName);
        dialog.getRootPane().setDefaultButton(cancelButton);

        // load any changes someone else might have made.
        mwclient.getServerConfigData();

        for (int pos = ConfigPane.getComponentCount() - 1; pos >= 0; pos--) {
            JPanel panel = (JPanel) ConfigPane.getComponent(pos);
            findAndPopulateTextAndCheckBoxes(panel);

        }

        // Show the dialog and get the user's input
        dialog.setLocationRelativeTo(mwclient.getMainFrame());
        dialog.setModal(true);
        dialog.pack();
        dialog.setVisible(true);

        if (pane.getValue() == okayButton) {

            for (int pos = ConfigPane.getComponentCount() - 1; pos >= 0; pos--) {
                JPanel panel = (JPanel) ConfigPane.getComponent(pos);
                findAndSaveConfigs(panel);
            }
            mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c AdminSaveServerConfigs");
            mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c CampaignConfig");

            mwclient.reloadData();

        } else {
            dialog.dispose();
        }
    }

    /**
     * This Method tunnels through all of the panels to find the textfields and checkboxes. Once it find one it grabs the Name() param of the object and uses
     * that to find out what the setting should be from the mwclient.getserverConfigs() method.
     * 
     * @param panel
     */
    public void findAndPopulateTextAndCheckBoxes(JPanel panel) {
        String key = null;

        for (int fieldPos = panel.getComponentCount() - 1; fieldPos >= 0; fieldPos--) {

            Object field = panel.getComponent(fieldPos);

            if (field instanceof JPanel) {
                findAndPopulateTextAndCheckBoxes((JPanel) field);
            } else if (field instanceof JTextField) {
                JTextField textBox = (JTextField) field;

                key = textBox.getName();
                if (key == null) {
                    continue;
                }

                textBox.setMaximumSize(new Dimension(100, 10));
                try {
                    // bad hack need to format the message for the last time the
                    // backup happened
                    if (key.equals("LastAutomatedBackup")) {
                        SimpleDateFormat sDF = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
                        Date date = new Date(Long.parseLong(mwclient.getserverConfigs(key)));
                        textBox.setText(sDF.format(date));
                    } else {
                        textBox.setText(mwclient.getserverConfigs(key));
                    }
                } catch (Exception ex) {
                    textBox.setText("N/A");
                }
            } else if (field instanceof JCheckBox) {
                JCheckBox checkBox = (JCheckBox) field;

                key = checkBox.getName();
                if (key == null) {
                    CampaignData.mwlog.errLog("Null Checkbox: " + checkBox.getToolTipText());
                    continue;
                }
                checkBox.setSelected(Boolean.parseBoolean(mwclient.getserverConfigs(key)));

            } else if (field instanceof JRadioButton) {
                JRadioButton radioButton = (JRadioButton) field;

                key = radioButton.getName();
                if (key == null) {
                    CampaignData.mwlog.errLog("Null RadioButton: " + radioButton.getToolTipText());
                    continue;
                }
                radioButton.setSelected(Boolean.parseBoolean(mwclient.getserverConfigs(key)));

            }// else continue
        }
    }

    /**
     * This method will tunnel through all of the panels of the config UI to find any changed text fields or checkboxes. Then it will send the new configs to
     * the server.
     * 
     * @param panel
     */
    public void findAndSaveConfigs(JPanel panel) {
        String key = null;
        String value = null;
        for (int fieldPos = panel.getComponentCount() - 1; fieldPos >= 0; fieldPos--) {

            Object field = panel.getComponent(fieldPos);

            // found another JPanel keep digging!
            if (field instanceof JPanel) {
                findAndSaveConfigs((JPanel) field);
            } else if (field instanceof JTextField) {
                JTextField textBox = (JTextField) field;

                value = textBox.getText();
                key = textBox.getName();

                if (key == null || value == null) {
                    continue;
                }

                // don't need to save this the system does it on its own
                // --Torren.
                if (key.equals("LastAutomatedBackup")) {
                    continue;
                }

                // reduce bandwidth only send things that have changed.
                if (!mwclient.getserverConfigs(key).equalsIgnoreCase(value)) {
                    mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c AdminChangeServerConfig#" + key + "#" + value + "#CONFIRM");
                }
            } else if (field instanceof JCheckBox) {
                JCheckBox checkBox = (JCheckBox) field;

                value = Boolean.toString(checkBox.isSelected());
                key = checkBox.getName();

                if (key == null || value == null) {
                    continue;
                }
                // reduce bandwidth only send things that have changed.
                if (!mwclient.getserverConfigs(key).equalsIgnoreCase(value)) {
                    mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c AdminChangeServerConfig#" + key + "#" + value + "#CONFIRM");
                }
            } else if (field instanceof JRadioButton) {
                JRadioButton radioButton = (JRadioButton) field;

                value = Boolean.toString(radioButton.isSelected());
                key = radioButton.getName();

                if (key == null || value == null) {
                    continue;
                }
                // reduce bandwidth only send things that have changed.
                if (!mwclient.getserverConfigs(key).equalsIgnoreCase(value)) {
                    mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c AdminChangeServerConfig#" + key + "#" + value + "#CONFIRM");
                }
            }// else continue
        }

    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals(okayCommand)) {
            pane.setValue(okayButton);
            dialog.dispose();
        } else if (command.equals(cancelCommand)) {
            pane.setValue(cancelButton);
            dialog.dispose();
        }
    }
}