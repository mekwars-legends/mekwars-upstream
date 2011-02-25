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
 * @author Spork
 * 
 * Server Configuration Page. All new Server Options need to be added to this page or subPanels as well.
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

import admin.dialog.serverConfigDialogs.AutoProdPanel;
import admin.dialog.serverConfigDialogs.CombatPanel;
import admin.dialog.serverConfigDialogs.DirectSellPanel;
import admin.dialog.serverConfigDialogs.FactionPanel;
import admin.dialog.serverConfigDialogs.InfluencePanel;
import admin.dialog.serverConfigDialogs.NewbieHousePanel;
import admin.dialog.serverConfigDialogs.PathsPanel;
import admin.dialog.serverConfigDialogs.RepodPanel;
import admin.dialog.serverConfigDialogs.TechnicianPanel;
import admin.dialog.serverConfigDialogs.UnitsPanel;
import admin.dialog.serverConfigDialogs.VotingPanel;
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
        PathsPanel pathsPanel = new PathsPanel();// file paths
        InfluencePanel influencePanel = new InfluencePanel(mwclient);// influence settings
        RepodPanel repodPanel = new RepodPanel(mwclient);
        TechnicianPanel technicianPanel = new TechnicianPanel(mwclient);
        UnitsPanel unitsPanel = new UnitsPanel(mwclient);
        FactionPanel factionPanel = new FactionPanel(mwclient);
        DirectSellPanel directSellPanel = new DirectSellPanel();
        NewbieHousePanel newbieHousePanel = new NewbieHousePanel();
        VotingPanel votingPanel = new VotingPanel();
        AutoProdPanel autoProdPanel = new AutoProdPanel(mwclient); // Autoproduction
        CombatPanel combatPanel = new CombatPanel();// mm options, etc
        
        JPanel productionPanel = new JPanel();// was factoryOptions
        JPanel rewardPanel = new JPanel();
        JPanel miscOptionsPanel = new JPanel();// things which can't be easily categorized
        JPanel artilleryPanel = new JPanel();
        JPanel pilotsPanel = new JPanel();// allows SO's set up pilot options and personal pilot queue options
        JPanel pilotSkillsModPanel = new JPanel();// Allows the SO's to set the mods for each skill type that affects the MM game.
        JPanel pilotSkillsPanel = new JPanel();// allows SO's to select what pilot skills they want for non-Mek unit types.
        JPanel mekPilotSkillsPanel = new JPanel();// allows SO's to select what pilot skills they want for Meks
        JPanel noPlayPanel = new JPanel();
        JPanel blackMarketPanel = new JPanel();
        JPanel defectionPanel = new JPanel();// control defection access, losses therefrom, etc.
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

        JPanel GunneryModPanel = new JPanel();
        
        GunneryModPanel.setLayout(new GridLayout(3,2));
        
        BaseCheckBox = new JCheckBox("Flat G/B Mod");
        BaseCheckBox.setName("USEFLATGUNNERYBALLISTICMODIFIER");
        GunneryModPanel.add(BaseCheckBox);

        baseTextField = new JTextField(5);
        baseTextField.setName("GunneryBallisticBaseBVMod");
        baseTextField.setToolTipText("BV Mod per Ballistic Weapon");
        GunneryModPanel.add(baseTextField);
        
        BaseCheckBox = new JCheckBox("Flat G/L Mod");
        BaseCheckBox.setName("USEFLATGUNNERYLASERMODIFIER");
        GunneryModPanel.add(BaseCheckBox);

        baseTextField = new JTextField(5);
        baseTextField.setName("GunneryLaserBaseBVMod");
        baseTextField.setToolTipText("BV Mod per Laser Weapon");
        GunneryModPanel.add(baseTextField);
        
        BaseCheckBox = new JCheckBox("Flat G/M Mod");
        BaseCheckBox.setName("USEFLATGUNNERYMISSILEMODIFIER");
        GunneryModPanel.add(BaseCheckBox);

        baseTextField = new JTextField(5);
        baseTextField.setName("GunneryMissileBaseBVMod");
        baseTextField.setToolTipText("BV Mod per Missile Weapon");
        GunneryModPanel.add(baseTextField);
        
        
        SpringLayoutHelper.setupSpringGrid(SkillModSpring, 4);

        pilotSkillsModPanel.setLayout(new VerticalLayout(10));
        
        pilotSkillsModPanel.add(SkillModSpring);
        pilotSkillsModPanel.add(GunneryModPanel);

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
        pilotOptionsSpring2.add(new JLabel("Retired Pilot Takes Mech Chance:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html> Chance a retiring pilot takes his unit with him.</html>");
        baseTextField.setName("RetiredPilotTakesMechChance");
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

        BaseCheckBox = new JCheckBox("Allow Asymmetric Levelling");
        BaseCheckBox.setToolTipText("<html>If checked, pilots will be able to level up asymmetrically (2/5, 1/5, 4/2, etc)</html>");
        BaseCheckBox.setName("AllowAsymmetricPilotLevels");
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
        
        BaseCheckBox = new JCheckBox("Disconnect idle users");
        BaseCheckBox.setToolTipText("<html>Disconnect users after [MAXIDLETIME]?<br>Unchecked logs them out, but leaves them connected.</html>");
        BaseCheckBox.setName("DisconnectIdleUsers");
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
        //bmBox.setLayout(new BoxLayout(bmBox, BoxLayout.Y_AXIS));

        bmBox.setLayout(new VerticalLayout());
        
        JPanel bmCBoxSpring = new JPanel(new SpringLayout());
        JPanel bmTextSpring = new JPanel(new SpringLayout());

        bmCBoxSpring.setBorder(BorderFactory.createEtchedBorder());
        bmTextSpring.setBorder(BorderFactory.createEtchedBorder());
        
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
        
       SpringLayoutHelper.setupSpringGrid(bmTextSpring, 8);

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

        BaseCheckBox = new JCheckBox("Hide BM Units");
        BaseCheckBox.setToolTipText("If checked, unit models and BVs are hidden from the players");
        BaseCheckBox.setName("HiddenBMUnits");
        bmCBoxSpring.add(BaseCheckBox);
        
        SpringLayoutHelper.setupSpringGrid(bmCBoxSpring, 5);

        JPanel bmButtonSpring = new JPanel(new SpringLayout());
        bmButtonSpring.setBorder(BorderFactory.createEtchedBorder());
        
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

        SpringLayoutHelper.setupSpringGrid(bmButtonSpring, 2);

        JPanel BMWeightPanel = new JPanel();
        BMWeightPanel.setLayout(new BoxLayout(BMWeightPanel, BoxLayout.Y_AXIS));
        BMWeightPanel.setBorder(BorderFactory.createEtchedBorder());
        
        BaseCheckBox = new JCheckBox("Use BM Weighting Tables");
        BaseCheckBox.setName("UseBMWeightingTables");
        BMWeightPanel.add(BaseCheckBox);
        
        JPanel MekWeightPanel = new JPanel();
        
        baseTextField = new JTextField(5);
        baseTextField.setName("BMLightMekWeight");
        MekWeightPanel.add(new JLabel("Light Mek:", SwingConstants.TRAILING));
        MekWeightPanel.add(baseTextField);
        
        baseTextField = new JTextField(5);
        baseTextField.setName("BMMediumMekWeight");
        MekWeightPanel.add(new JLabel("Medium Mek:", SwingConstants.TRAILING));
        MekWeightPanel.add(baseTextField);
        
        baseTextField = new JTextField(5);
        baseTextField.setName("BMHeavyMekWeight");
        MekWeightPanel.add(new JLabel("Heavy Mek:", SwingConstants.TRAILING));
        MekWeightPanel.add(baseTextField);
        
        baseTextField = new JTextField(5);
        baseTextField.setName("BMAssaultMekWeight");
        MekWeightPanel.add(new JLabel("Assault Mek:", SwingConstants.TRAILING));
        MekWeightPanel.add(baseTextField);
        
        BMWeightPanel.add(MekWeightPanel);
        
        JPanel BMBayLimitPanel = new JPanel();
        BMBayLimitPanel.setBorder(BorderFactory.createEtchedBorder());
        baseTextField = new JTextField(5);
        baseTextField.setName("MaximumNegativeBaysFromBM");
        baseTextField.setToolTipText("-1 to disable check for negative bays.");
        BMBayLimitPanel.add(new JLabel("Maximum Negative Bays From BM:", SwingConstants.TRAILING));
        BMBayLimitPanel.add(baseTextField);
        
        JPanel BMPriceModPanel = new JPanel();
        JPanel BMPMPanel = new JPanel();
        BMPMPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        BMPriceModPanel.setBorder(BorderFactory.createEtchedBorder());
        BMPMPanel.setLayout(new GridLayout(7,5));
        BMPMPanel.add(new JLabel(" "));
        BMPMPanel.add(new JLabel("Light"));
        BMPMPanel.add(new JLabel("Medium"));
        BMPMPanel.add(new JLabel("Heavy"));
        BMPMPanel.add(new JLabel("Assault"));
        
        for (int type = Unit.MEK; type < Unit.MAXBUILD; type++) {
        	BMPMPanel.add(new JLabel(Unit.getTypeClassDesc(type)));
        	for (int weight = Unit.LIGHT; weight <= Unit.ASSAULT; weight++) {
        		baseTextField = new JTextField(5);
        		baseTextField.setName("BMPriceMultiplier_" + Unit.getWeightClassDesc(weight) + Unit.getTypeClassDesc(type) );
        		baseTextField.setToolTipText("Multiplier for faction bay " + Unit.getWeightClassDesc(weight) + " " + Unit.getTypeClassDesc(type) + " units sent to the BM.  (float value)");
        		BMPMPanel.add(baseTextField);
        	}
        }
        BMPriceModPanel.add(BMPMPanel);
        
        bmBox.add(bmTextSpring);
        bmBox.add(bmCBoxSpring);
        bmBox.add(bmButtonSpring);
        bmBox.add(BMWeightPanel);
        bmBox.add(BMBayLimitPanel);
        bmBox.add(BMPriceModPanel);
        
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
        
        JPanel uLimitsPanel = new JPanel();
        JPanel ulTopPanel = new JPanel();
        JPanel ulBottomPanel = new JPanel();
        
        ulTopPanel.add(new JLabel("Hangar Limits"));
        
        ulBottomPanel.setLayout(new GridLayout(7, 5));
        ulBottomPanel.add(new JLabel(" "));
        ulBottomPanel.add(new JLabel("Light"));
        ulBottomPanel.add(new JLabel("Medium"));
        ulBottomPanel.add(new JLabel("Heavy"));
        ulBottomPanel.add(new JLabel("Assault"));        
        
        for (int type = Unit.MEK; type < Unit.MAXBUILD; type++) {
        	ulBottomPanel.add(new JLabel(Unit.getTypeClassDesc(type)));
        	for (int weight = Unit.LIGHT; weight <= Unit.ASSAULT; weight++) {
        		baseTextField = new JTextField(5);
        		baseTextField.setName("MaxHangar" + Unit.getWeightClassDesc(weight) + Unit.getTypeClassDesc(type));
        		baseTextField.setToolTipText("Limit hangar to this many " + Unit.getWeightClassDesc(weight) + " " + Unit.getTypeClassDesc(type) + ((Unit.getTypeClassDesc(type) == "Infantry") ? "" : "s") + ".  -1 to disable limit");
        		ulBottomPanel.add(baseTextField);
        	}
        }
        ulTopPanel.setBorder(BorderFactory.createEmptyBorder(0,5,0,5));
        ulBottomPanel.setBorder(BorderFactory.createEmptyBorder(0,5,0,5));
        
        uLimitsPanel.setBorder(BorderFactory.createEtchedBorder());
        uLimitsPanel.setLayout(new VerticalLayout());
        uLimitsPanel.add(ulTopPanel);
        uLimitsPanel.add(ulBottomPanel);
        
        JPanel ulActionsPanel = new JPanel();
        JPanel ulAPTop = new JPanel();
        JPanel ulAPBottom = new JPanel();
        ulAPTop.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        ulAPBottom.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        
        BaseCheckBox = new JCheckBox("Disable Activation");
        BaseCheckBox.setName("DisableActivationIfOverHangarLimits");
        BaseCheckBox.setToolTipText("Players over the limits cannot go active.");
        ulAPTop.add(BaseCheckBox);
        BaseCheckBox = new JCheckBox("Disable AFR");
        BaseCheckBox.setName("DisableAFRIfOverHangarLimits");
        BaseCheckBox.setToolTipText("Players over the limits cannot initiate or defend Attack From Reserve.");
        ulAPTop.add(BaseCheckBox);
        
        BaseCheckBox = new JCheckBox("Use Sliding Hangar Limits");
        BaseCheckBox.setName("UseSlidingHangarLimits");
        BaseCheckBox.setToolTipText("<html>Checking this box enables modified limits that increase in cost as more units are purchased.<br>See 'Using Sliding Hangar Limits.pdf'<br><br>Please note that at this time, this is an on/off switch - the per fight and on purchase options do nothing.</html>");
        ulAPBottom.add(BaseCheckBox);
        
        ulAPBottom.add(new JLabel("Multiplier:"));
        baseTextField = new JTextField(5);
        baseTextField.setName("SlidingHangarLimitModifier");
        baseTextField.setToolTipText("Multiplier for sliding hangar limits");
        ulAPBottom.add(baseTextField);
        
        BaseCheckBox = new JCheckBox("Apply to Purchase");
        BaseCheckBox.setName("SlidingHangarLimitsAffectPurchase");
        BaseCheckBox.setToolTipText("The over-limit penalty will be applied to purchase price");
        ulAPBottom.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Apply to Payout");
        BaseCheckBox.setName("SlidingHangarLimitsAffectPayout");
        BaseCheckBox.setToolTipText("The over-limit penalty will be applied to game payout");
        ulAPBottom.add(BaseCheckBox);
        
        ulActionsPanel.setLayout(new VerticalLayout());
        ulActionsPanel.add(ulAPTop);
        ulActionsPanel.add(ulAPBottom);
        ulActionsPanel.setBorder(BorderFactory.createEtchedBorder());
        
        JPanel limitsPanel = new JPanel();
        limitsPanel.setLayout(new VerticalLayout());
        limitsPanel.add(uLimitsPanel);
        limitsPanel.add(ulActionsPanel);
        
        JPanel bmLimitsPanel = new JPanel();
        bmLimitsPanel.setLayout(new GridLayout(7, 5));
        bmLimitsPanel.add(new JLabel(" "));
        bmLimitsPanel.add(new JLabel("Light"));
        bmLimitsPanel.add(new JLabel("Medium"));
        bmLimitsPanel.add(new JLabel("Heavy"));
        bmLimitsPanel.add(new JLabel("Assault"));
        
        bmLimitsPanel.add(new JLabel("Mechs: "));
        
        BaseCheckBox = new JCheckBox();
        BaseCheckBox.setName("CanBuyBMLightMeks");
        BaseCheckBox.setToolTipText("Players can buy Light Meks from the BM");
        bmLimitsPanel.add(BaseCheckBox);
        
        BaseCheckBox = new JCheckBox();
        BaseCheckBox.setName("CanBuyBMMediumMeks");
        BaseCheckBox.setToolTipText("Players can buy Medium Meks from the BM");
        bmLimitsPanel.add(BaseCheckBox);
        
        BaseCheckBox = new JCheckBox();
        BaseCheckBox.setName("CanBuyBMHeavyMeks");
        BaseCheckBox.setToolTipText("Players can buy Heavy Meks from the BM");
        bmLimitsPanel.add(BaseCheckBox);
        
        BaseCheckBox = new JCheckBox();
        BaseCheckBox.setName("CanBuyBMAssaultMeks");
        BaseCheckBox.setToolTipText("Players can buy Assault Meks from the BM");
        bmLimitsPanel.add(BaseCheckBox);
        
        bmLimitsPanel.add(new JLabel("Vehicles: "));
        
        BaseCheckBox = new JCheckBox();
        BaseCheckBox.setName("CanBuyBMLightVehicles");
        BaseCheckBox.setToolTipText("Players can buy Light Vehicles from the BM");
        bmLimitsPanel.add(BaseCheckBox);
        
        BaseCheckBox = new JCheckBox();
        BaseCheckBox.setName("CanBuyBMMediumVehicles");
        BaseCheckBox.setToolTipText("Players can buy Medium Vehicles from the BM");
        bmLimitsPanel.add(BaseCheckBox);
        
        BaseCheckBox = new JCheckBox();
        BaseCheckBox.setName("CanBuyBMHeavyVehicles");
        BaseCheckBox.setToolTipText("Players can buy Heavy Vehicles from the BM");
        bmLimitsPanel.add(BaseCheckBox);
        
        BaseCheckBox = new JCheckBox();
        BaseCheckBox.setName("CanBuyBMAssaultVehicles");
        BaseCheckBox.setToolTipText("Players can buy Assault Vehicles from the BM");
        bmLimitsPanel.add(BaseCheckBox);
        
        bmLimitsPanel.add(new JLabel("Infantry: "));
        
        BaseCheckBox = new JCheckBox();
        BaseCheckBox.setName("CanBuyBMLightInfantry");
        BaseCheckBox.setToolTipText("Players can buy Light Infantry from the BM");
        bmLimitsPanel.add(BaseCheckBox);
        
        BaseCheckBox = new JCheckBox();
        BaseCheckBox.setName("CanBuyBMMediumInfantry");
        BaseCheckBox.setToolTipText("Players can buy Medium Infantry from the BM");
        bmLimitsPanel.add(BaseCheckBox);
        
        BaseCheckBox = new JCheckBox();
        BaseCheckBox.setName("CanBuyBMHeavyInfantry");
        BaseCheckBox.setToolTipText("Players can buy Heavy Infantry from the BM");
        bmLimitsPanel.add(BaseCheckBox);
        
        BaseCheckBox = new JCheckBox();
        BaseCheckBox.setName("CanBuyBMAssaultInfantry");
        BaseCheckBox.setToolTipText("Players can buy Assault Infantry from the BM");
        bmLimitsPanel.add(BaseCheckBox);
        
        bmLimitsPanel.add(new JLabel("BattleArmor: "));
        
        BaseCheckBox = new JCheckBox();
        BaseCheckBox.setName("CanBuyBMLightBA");
        BaseCheckBox.setToolTipText("Players can buy Light BA from the BM");
        bmLimitsPanel.add(BaseCheckBox);
        
        BaseCheckBox = new JCheckBox();
        BaseCheckBox.setName("CanBuyBMMediumBA");
        BaseCheckBox.setToolTipText("Players can buy Medium BA from the BM");
        bmLimitsPanel.add(BaseCheckBox);
        
        BaseCheckBox = new JCheckBox();
        BaseCheckBox.setName("CanBuyBMHeavyBA");
        BaseCheckBox.setToolTipText("Players can buy Heavy BA from the BM");
        bmLimitsPanel.add(BaseCheckBox);
        
        BaseCheckBox = new JCheckBox();
        BaseCheckBox.setName("CanBuyBMAssaultBA");
        BaseCheckBox.setToolTipText("Players can buy Assault BA from the BM");
        bmLimitsPanel.add(BaseCheckBox);
        
        bmLimitsPanel.add(new JLabel("Protomeks: "));
        
        BaseCheckBox = new JCheckBox();
        BaseCheckBox.setName("CanBuyBMLightProtomeks");
        BaseCheckBox.setToolTipText("Players can buy Light Protomeks from the BM");
        bmLimitsPanel.add(BaseCheckBox);
        
        BaseCheckBox = new JCheckBox();
        BaseCheckBox.setName("CanBuyBMMediumProtomeks");
        BaseCheckBox.setToolTipText("Players can buy Medium Protomeks from the BM");
        bmLimitsPanel.add(BaseCheckBox);
        
        BaseCheckBox = new JCheckBox();
        BaseCheckBox.setName("CanBuyBMHeavyProtomeks");
        BaseCheckBox.setToolTipText("Players can buy Heavy Protomeks from the BM");
        bmLimitsPanel.add(BaseCheckBox);
        
        BaseCheckBox = new JCheckBox();
        BaseCheckBox.setName("CanBuyBMAssaultProtomeks");
        BaseCheckBox.setToolTipText("Players can buy Assault Protomeks from the BM");
        bmLimitsPanel.add(BaseCheckBox);
        
        bmLimitsPanel.add(new JLabel("Aero: "));
        
        BaseCheckBox = new JCheckBox();
        BaseCheckBox.setName("CanBuyBMLightAero");
        BaseCheckBox.setToolTipText("Players can buy Light Aero from the BM");
        bmLimitsPanel.add(BaseCheckBox);
        
        BaseCheckBox = new JCheckBox();
        BaseCheckBox.setName("CanBuyBMMediumAero");
        BaseCheckBox.setToolTipText("Players can buy Medium Aero from the BM");
        bmLimitsPanel.add(BaseCheckBox);
        
        BaseCheckBox = new JCheckBox();
        BaseCheckBox.setName("CanBuyBMHeavyAero");
        BaseCheckBox.setToolTipText("Players can buy Heavy Aero from the BM");
        bmLimitsPanel.add(BaseCheckBox);
        
        BaseCheckBox = new JCheckBox();
        BaseCheckBox.setName("CanBuyBMAssaultAero");
        BaseCheckBox.setToolTipText("Players can buy Assault Aero from the BM");
        bmLimitsPanel.add(BaseCheckBox);
        
        JPanel bmLimitsBox = new JPanel();
        bmLimitsBox.setLayout(new BoxLayout(bmLimitsBox, BoxLayout.Y_AXIS));
        bmLimitsBox.setBorder(BorderFactory.createEtchedBorder());
        bmLimitsPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        JPanel titlePanel = new JPanel();
        titlePanel.add(new JLabel("Black Market Limits"));
        bmLimitsBox.add(titlePanel);
        bmLimitsBox.add(bmLimitsPanel);
        
        unitLimitsPanel.setLayout(new VerticalLayout());
        unitLimitsPanel.setBorder(BorderFactory.createEmptyBorder(5,0,5,0));
        unitLimitsPanel.add(limitsPanel);
        unitLimitsPanel.add(bmLimitsBox);
        
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
        ConfigPane.addTab("Unit Research", null, unitResearchPanel, "Unit Research Configuration");
        ConfigPane.addTab("Voting", null, votingPanel, "Voting Stuff");
        ConfigPane.addTab("Unit Limits", null, unitLimitsPanel, "Limits to unit ownership based on unit weightclass");
        ConfigPane.addTab("Autoproduction", null, autoProdPanel, "Set type and details of factory autoproduction");
        ConfigPane.addTab("Units", null, unitsPanel, "Consolidated Unit Information");
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