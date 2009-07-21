/*
 * MekWars - Copyright (C) 2004 
 * 
 * Derived from MegaMekNET (http://www.sourceforge.net/projects/megameknet)
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

package admin.dialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import megamek.common.PlanetaryConditions;
import client.MWClient;

import common.AdvancedTerrain;
import common.CampaignData;
import common.Continent;
import common.House;
import common.Planet;
import common.Terrain;
import common.UnitFactory;
import common.util.SpringLayoutHelper;

public final class PlanetEditorDialog implements ActionListener, KeyListener {

    // store the client backlink for other things to use
    private MWClient mwclient = null;
    private String planetName = "";
    private AdvancedTerrain aTerrain = new AdvancedTerrain();
    private int advanceTerrainId = -1;
    private Planet selectedPlanet;
    private boolean useAdvancedTerrain = false;
    private TreeSet<String> removedOwners = new TreeSet<String>();
    private HashMap<String, Integer> ownersMap = new HashMap<String, Integer>();
    private TreeSet<String> removedTerrain = new TreeSet<String>();
    private HashMap<String, Integer> terrainMap = new HashMap<String, Integer>();
    private TreeSet<String> removedFactory = new TreeSet<String>();
    private HashMap<String, String> factoryMap = new HashMap<String, String>();
    private HashMap<String, AdvancedTerrain> advancedTerrainMap = new HashMap<String, AdvancedTerrain>();

    private final static String okayCommand = "Save";
    private final static String cancelCommand = "Cancel";
    private final static String refreshCommand = "Refresh";
    private final static String addOwnerCommand = "AddOwner";
    private final static String removeOwnerCommand = "RemoveOwner";
    private final static String resetOwnersCommand = "ResetOwners";
    private final static String addFactoryCommand = "AddFactory";
    private final static String removeFactoryCommand = "RemoveFactory";
    private final static String removeAllFactoriesCommand = "RemoveAllFactories";
    private final static String addTerrainCommand = "AddTerrain";
    private final static String RemoveTerrainCommand = "RemoveTerrain";
    private final static String removeAllTerrainsCommand = "RemoveAllTerrains";
    private final static String planetTerrainsCombo = "PlanetTerrainsCombo";
    private final static String planetOwnersListCommand = "PlanetOwnersList";
    private final static String staticMapCBCommmand = "StaticMapCB";
    private final static String atmosphereCommand = "Atmosphere";

    private final static String windowName = "Vertigo's Planet Editor";

    // BUTTONS
    private final JButton okayButton = new JButton("Save");
    private final JButton cancelButton = new JButton("Close");
    private final JButton refreshButton = new JButton("Refresh");
    private final JButton addOwnerButton = new JButton("Add Owner");
    private final JButton removeOwnerButton = new JButton("Remove Owner");
    private final JButton resetOwnersButton = new JButton("Reset All Owners");
    private final JButton addFactory = new JButton("Add Factory");
    private final JButton removeFactory = new JButton("Remove Factory");
    private final JButton removeAllFactories = new JButton("Remove All");
    private final JButton addTerrain = new JButton("Add Terrain");
    private final JButton removeTerrain = new JButton("Remove Terrain");
    private final JButton removeAllTerrains = new JButton("Remove All");

    // TEXT FIELDS
    // tab names
    private final JTextField DisplayNameText = new JTextField(5);
    private final JTextField StaticMapNameText = new JTextField(5);
    private final JTextField XSizeText = new JTextField(5);
    private final JTextField YSizeText = new JTextField(5);
    private final JTextField XBoardSizeText = new JTextField(5);
    private final JTextField YBoardSizeText = new JTextField(5);
    private final JTextField LowTempText = new JTextField(5);
    private final JTextField HighTempText = new JTextField(5);
    private final JTextField GravityText = new JTextField(5);
    private final JTextField DuskChanceText = new JTextField(5);
    private final JTextField NightChanceText = new JTextField(5);
    private final JTextField MoonLessNightChanceText = new JTextField(5);
    private final JTextField PitchBlackNightChanceText = new JTextField(5);
    private final JTextField NightTempModText = new JTextField(5);
    private final JTextField lightSnowfallChanceText = new JTextField(5);
    private final JTextField moderateSnowfallChanceText = new JTextField(5);
    private final JTextField heavySnowfallChanceText = new JTextField(5);
    private final JTextField lightRainfallChanceText = new JTextField(5);
    private final JTextField moderateRainfallChanceText = new JTextField(5);
    private final JTextField heavyRainfallChanceText = new JTextField(5);
    private final JTextField downPourChanceText = new JTextField(5);
    private final JTextField sleetChanceText = new JTextField(5);
    private final JTextField iceStormChanceText = new JTextField(5);
    private final JTextField lightHailChanceText = new JTextField(5);
    private final JTextField heavyHailChanceText = new JTextField(5);
    private final JTextField lightWindsChanceText = new JTextField(5);
    private final JTextField moderateWindsChanceText = new JTextField(5);
    private final JTextField strongWindsChanceText = new JTextField(5);
    private final JTextField stormWindsChanceText = new JTextField(5);
    private final JTextField tornadoF13WindsChanceText = new JTextField(5);
    private final JTextField tornadoF4ChanceText = new JTextField(5);
    private final JTextField lightFogChanceText = new JTextField(5);
    private final JTextField heavyFogChanceText = new JTextField(5);
    private final JTextField emiChanceText = new JTextField(5);
    private final JTextField planetBays = new JTextField(5);
    private final JTextField planetComps = new JTextField(5);
    private final JTextField planetXPosition = new JTextField(5);
    private final JTextField planetYPosition = new JTextField(5);
    private final JTextField newTerrainPercent = new JTextField(5);
    private final JTextField newFactoryName = new JTextField(10);
    private final JTextField newFactoryBuildTable = new JTextField(10);
    private final JTextField newFactoryAccessLevel = new JTextField(10);
    private final JTextField currentFactionOwnerShip = new JTextField(5);
    private final JTextField newFacitonOwnerShip = new JTextField(5);
    private final JTextField currentTerrainPercent = new JTextField(5);
    private final JTextField minPlanetOwnerShip = new JTextField(5);
    private final JTextField planetConquerPoints = new JTextField(5);

    private final JCheckBox isStaticMapCB = new JCheckBox();
    private final JCheckBox isHomeWorldCB = new JCheckBox();
    private final JCheckBox isConquerable = new JCheckBox();

    // STOCK DIALOUG AND PANE
    private JDialog dialog;
    private JOptionPane pane;
    private JPanel masterPanel;
    private JPanel planets;
    private JPanel planetInfo;
    private JPanel planetProduction;
    private JPanel planetTerrain;
    private JPanel planetAdvancedTerrain;
    private JTabbedPane ConfigPane = new JTabbedPane(SwingConstants.TOP);

    private String[] factoryTypes = { "All", "Mek", "Vee", "Mek & Vee", "Inf", "Mek & Inf", "Vee & Inf", "Mek & Inf & Vee", "Proto", "Mek & Proto", "Vee & Proto", "Mek & Vee & Proto", "Inf & Proto", "Mek & Inf & Proto", "Vee & Inf & Proto", "Mek & Vee & Inf & Proto", "BA", "Mek & BA", "Vee & BA", "Mek & Vee & BA", "Inf & BA", "Mek & Inf & BA", "Vee & Inf & BA", "Mek & Vee & Inf & BA", "Proto & BA", "Mek & Proto & BA", "Vee & Proto & BA", "Mek & Vee & Proto & BA", "Inf & Proto & BA", "Mek & Inf & Proto & BA", "Vee & Inf & Proto & BA", "VTOL", "Aero" };

    private String[] factorySizes = { "Light", "Medium", "Heavy", "Assault" };

    // Combo boxes
    private JComboBox planetNames;
    private JComboBox houseNames;
    private JComboBox planetOwnersList;
    private JComboBox planetFactories;
    private JComboBox planetTerrains;
    private JComboBox allTerrains;
    private JComboBox factorySize = new JComboBox(factorySizes);
    private JComboBox factoryType = new JComboBox(factoryTypes);
    private JComboBox factoryOwners;
    private JComboBox ownerNames;
    private JComboBox atmosphere = new JComboBox(PlanetaryConditions.atmoNames);

    public PlanetEditorDialog(MWClient c, String planetName) {

        // save the client
        mwclient = c;
        this.planetName = planetName;

        useAdvancedTerrain = Boolean.parseBoolean(mwclient.getserverConfigs("UseStaticMaps"));
        // Set the tooltips and actions for dialouge buttons
        okayButton.setActionCommand(okayCommand);
        cancelButton.setActionCommand(cancelCommand);
        refreshButton.setActionCommand(refreshCommand);

        okayButton.addActionListener(this);
        cancelButton.addActionListener(this);
        refreshButton.addActionListener(this);
        okayButton.setToolTipText("Save Options");
        cancelButton.setToolTipText("Exit without saving changes");
        refreshButton.setToolTipText("Reload data");

        // CREATE THE PANELS
        masterPanel = new JPanel();
        masterPanel.setLayout(new BoxLayout(masterPanel, BoxLayout.Y_AXIS));

        loadAllPanels();
        masterPanel.add(planets);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.add(planetInfo);
        infoPanel.add(planetProduction);

        JPanel terrainPanel = new JPanel();
        terrainPanel.setLayout(new BoxLayout(terrainPanel, BoxLayout.Y_AXIS));
        terrainPanel.add(planetTerrain);
        terrainPanel.add(planetAdvancedTerrain);

        ConfigPane.addTab("Info", infoPanel);
        ConfigPane.addTab("Terrain", terrainPanel);
        masterPanel.add(ConfigPane);

        // Set the user's options
        Object[] options = { refreshButton, okayButton, cancelButton };

        // Create the pane containing the buttons
        pane = new JOptionPane(masterPanel, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null, options, null);

        // Create the main dialog and set the default button
        dialog = pane.createDialog(masterPanel, windowName);
        dialog.getRootPane().setDefaultButton(cancelButton);

        // Show the dialog and get the user's input
        dialog.setModal(true);
        dialog.pack();
        dialog.setLocationRelativeTo(c.getMainFrame());
        dialog.setVisible(true);

    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (command.equals(okayCommand)) {
            if (!saveAllData()) {
                JOptionPane.showMessageDialog(mwclient.getMainFrame(), "Unable to Save Data, Check Error Logs");
            }
            try {
                mwclient.refreshData();
                removedFactory.clear();
                removedTerrain.clear();
                removedOwners.clear();
            } catch (Exception ex) {
                CampaignData.mwlog.errLog("PlanetEditorDialog Save Error!");
                CampaignData.mwlog.errLog(ex);
            }
            refreshAllPanels();
        } else if (command.equals(cancelCommand)) {
            dialog.dispose();
        } else if (command.equals(refreshCommand)) {
            planetName = planetNames.getSelectedItem().toString();
            selectedPlanet = mwclient.getData().getPlanetByName(planetName);
            removedOwners.clear();
            removedTerrain.clear();
            removedFactory.clear();
            refreshAllPanels();
        } else if (command.equals(planetTerrainsCombo)) {
            if (planetTerrains.getItemCount() > 0) {
                currentTerrainPercent.setText(Integer.toString(terrainMap.get(planetTerrains.getSelectedItem().toString())));
                advanceTerrainId = getTerrainId();
                loadAdvancedTerrainsData();
            }
        } else if (command.equals(planetOwnersListCommand)) {
            try {
                currentFactionOwnerShip.setText(Integer.toString(ownersMap.get(planetOwnersList.getSelectedItem().toString())));
            } catch (Exception ex) {
                currentFactionOwnerShip.setText("");
            }
        } else if (command.equals(resetOwnersCommand)) {
            // Lets move all the current owners to the remove pile and then add
            // the original owner
            // as the one true owner.

            for (int pos = 0; pos < planetOwnersList.getItemCount(); pos++) {
                String name = planetOwnersList.getItemAt(pos).toString();
                if (name.equals(houseNames.getSelectedItem().toString())) {
                    ownersMap.put(houseNames.getSelectedItem().toString(), selectedPlanet.getConquestPoints());
                }
                removedOwners.add(name);
            }

            planetOwnersList.removeActionListener(this);
            planetOwnersList.removeAllItems();
            planetOwnersList.addItem(houseNames.getSelectedItem().toString());
            planetOwnersList.addActionListener(this);
            ownersMap.put(houseNames.getSelectedItem().toString(), 100);
            planetOwnersList.setActionCommand(planetOwnersListCommand);
            if (planetOwnersList.getItemCount() > 0) {
                planetOwnersList.setSelectedIndex(0);
            }
        } else if (command.equals(addOwnerCommand)) {
            try {
                int percent = Integer.parseInt(newFacitonOwnerShip.getText().trim().replaceAll("%", ""));
                String newOwner = ownerNames.getSelectedItem().toString();
                removedOwners.remove(newOwner);
                ownersMap.put(newOwner, percent);
                planetOwnersList.addItem(newOwner);
                ownerNames.removeItem(newOwner);
                if (ownerNames.getItemCount() > 0) {
                    ownerNames.setSelectedIndex(0);
                }
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(dialog, "Invalid Number Format Please try again");
            }
        } else if (command.equals(removeOwnerCommand)) {
            try {
                String removedOwner = planetOwnersList.getSelectedItem().toString();
                removedOwners.add(removedOwner);
                ownersMap.remove(removedOwner);
                planetOwnersList.removeItem(removedOwner);
                ownerNames.addItem(removedOwner);
                currentFactionOwnerShip.setText("");
                if (planetOwnersList.getItemCount() > 0) {
                    planetOwnersList.setSelectedIndex(0);
                }
            } catch (Exception ex) {
                CampaignData.mwlog.errLog(ex);
            }

        } else if (command.equals(addFactoryCommand)) {
            String factoryName = newFactoryName.getText().trim();
            String factoryDesc = factoryName + "#" + factorySize.getSelectedItem().toString() + "#" + factoryOwners.getSelectedItem().toString() + "#" + factoryType.getSelectedIndex() + "#";
            if (newFactoryBuildTable.getText().trim().length() > 1) {
                factoryDesc += newFactoryBuildTable.getText().trim();
            } else {
                factoryDesc += " ";
            }
            factoryDesc += "#" + newFactoryAccessLevel.getText();

            String fullFactoryName = factorySize.getSelectedItem().toString() + " " + factoryType.getSelectedItem().toString() + " " + newFactoryName.getText().trim() + " " + factoryOwners.getSelectedItem().toString() + " " + newFactoryBuildTable.getText() + " " + newFactoryAccessLevel.getText();
            factoryMap.put(factoryName, factoryDesc);
            planetFactories.addItem(fullFactoryName);
            newFactoryName.setText("");
            newFactoryBuildTable.setText("");
            newFactoryAccessLevel.setText("0");
            factorySize.setSelectedIndex(0);
            factoryOwners.setSelectedIndex(0);
            factoryType.setSelectedIndex(0);
        } else if (command.equals(removeFactoryCommand)) {
            if (planetFactories.getItemCount() < 1) {
                return;
            }
            String factoryName = "";
            for (UnitFactory factory : selectedPlanet.getUnitFactories()) {
                factoryName = factory.getSize() + " " + factory.getFullTypeString().trim() + " " + factory.getName() + " " + factory.getFounder() + " " + factory.getBuildTableFolder() + " " + factory.getAccessLevel();
                if (planetFactories.getSelectedItem().toString().trim().equals(factoryName)) {
                    removedFactory.add(factory.getName());
                    factoryMap.remove(factory.getName());
                    planetFactories.removeItemAt(planetFactories.getSelectedIndex());
                    break;
                }
            }
            // loadPlanetProductionData();
        } else if (command.equals(removeAllFactoriesCommand)) {
            removedFactory.addAll(factoryMap.keySet());
            factoryMap.clear();
            planetFactories.removeAllItems();
        } else if (command.equals(addTerrainCommand)) {
            try {
                int percent = Integer.parseInt(newTerrainPercent.getText().trim().replaceAll("%", ""));
                terrainMap.put(allTerrains.getSelectedItem().toString(), percent);
                planetTerrains.addItem(allTerrains.getSelectedItem().toString().trim());
                planetTerrains.setSelectedIndex(0);
                if (useAdvancedTerrain) {
                    AdvancedTerrain at = new AdvancedTerrain();
                    at.setDisplayName(allTerrains.getSelectedItem().toString());
                    advancedTerrainMap.put(allTerrains.getSelectedItem().toString(), at);
                }
            } catch (Exception ex) {
                CampaignData.mwlog.errLog(ex);
            }
        } else if (command.equals(RemoveTerrainCommand)) {
            currentTerrainPercent.setText("");
            if (planetTerrains.getItemCount() > 0) {
                String terrainName = planetTerrains.getSelectedItem().toString().trim();
                terrainMap.remove(terrainName);
                removedTerrain.add(terrainName);
                planetTerrains.removeItemAt(planetTerrains.getSelectedIndex());
                if (planetTerrains.getItemCount() > 0) {
                    planetTerrains.setSelectedIndex(0);
                }
                if (useAdvancedTerrain) {
                    advancedTerrainMap.remove(terrainName);
                }
            }
        } else if (command.equals(removeAllTerrainsCommand)) {
            removedTerrain.addAll(terrainMap.keySet());
            if (useAdvancedTerrain) {
                advancedTerrainMap.clear();
            }
            terrainMap.clear();
            planetTerrains.removeAllItems();
            currentTerrainPercent.setText("");
        } else if (command.equals(staticMapCBCommmand) || command.equals(atmosphereCommand)) {
            updateAdvancedTerrain();
        }
    }

    private void loadAllPanels() {

        selectedPlanet = mwclient.getData().getPlanetByName(planetName);

        loadPlanetNames();
        loadPlanetInfo();
        loadPlanetProduction();
        loadPlanetTerrain();

        advanceTerrainId = getTerrainId();
        loadAdvancedTerrains();

        masterPanel.repaint();
    }

    private void refreshAllPanels() {

        loadPlanetNamesData();
        loadPlanetInfoData();
        loadPlanetProductionData();
        loadPlanetTerrainData();
        advanceTerrainId = getTerrainId();

        loadAdvancedTerrainsData();
    }

    private void loadPlanetInfo() {
        planetInfo = new JPanel();
        planetInfo.setLayout(null);

        Dimension textFieldSize = new Dimension(35, 22);
        Dimension comboBoxSize = new Dimension(150, 22);
        planetInfo.setLayout(new BoxLayout(planetInfo, BoxLayout.Y_AXIS));
        JPanel panel1 = new JPanel();
        panel1.add(new JLabel("Coords:", JLabel.TRAILING));

        planetXPosition.setPreferredSize(textFieldSize);
        planetXPosition.setMaximumSize(textFieldSize);
        planetXPosition.setMinimumSize(textFieldSize);
        planetXPosition.setText(Double.toString(selectedPlanet.getPosition().getX()));
        planetXPosition.setToolTipText("Planets X Coord");
        panel1.add(planetXPosition);
        panel1.add(new JLabel(",", JLabel.TRAILING));

        planetYPosition.setPreferredSize(textFieldSize);
        planetYPosition.setMaximumSize(textFieldSize);
        planetYPosition.setMinimumSize(textFieldSize);
        planetYPosition.setText(Double.toString(selectedPlanet.getPosition().getY()));
        planetYPosition.setToolTipText("Planets Y Coord");
        panel1.add(planetYPosition);

        isHomeWorldCB.setText("HomeWorld");
        isHomeWorldCB.setSelected(selectedPlanet.isHomeWorld());
        panel1.add(isHomeWorldCB);

        isConquerable.setText("Conquerable");
        isConquerable.setSelected(selectedPlanet.isConquerable());
        isConquerable.setToolTipText("If Checked then conquer points can be taken from this planet");
        panel1.add(isConquerable);

        JPanel panel2 = new JPanel();
        panel2.add(new JLabel("MinOwnerShip:", JLabel.TRAILING));
        minPlanetOwnerShip.setText(Integer.toString(selectedPlanet.getMinPlanetOwnerShip()));
        panel2.add(minPlanetOwnerShip);
        minPlanetOwnerShip.setPreferredSize(textFieldSize);
        minPlanetOwnerShip.setMaximumSize(textFieldSize);
        minPlanetOwnerShip.setMinimumSize(textFieldSize);

        panel2.add(new JLabel("Conquer Points:", JLabel.TRAILING));
        planetConquerPoints.setPreferredSize(textFieldSize);
        planetConquerPoints.setMaximumSize(textFieldSize);
        planetConquerPoints.setMinimumSize(textFieldSize);
        planetConquerPoints.setText(Integer.toString(selectedPlanet.getConquestPoints()));
        panel2.add(planetConquerPoints);

        JPanel panel3 = new JPanel(new SpringLayout());
        panel3.add(new JLabel("Original Owner:", JLabel.TRAILING));
        houseNames = new JComboBox();
        populateHouseNames(houseNames);

        houseNames.setPreferredSize(comboBoxSize);
        houseNames.setMaximumSize(comboBoxSize);
        houseNames.setMinimumSize(comboBoxSize);
        houseNames.setSelectedItem(selectedPlanet.getOriginalOwner());
        panel3.add(houseNames);

        SpringLayoutHelper.setupSpringGrid(panel3, 2);

        JPanel panel4 = new JPanel();
        panel4.setLayout(new BoxLayout(panel4, BoxLayout.LINE_AXIS));

        panel4.add(new JLabel("Owners:", JLabel.LEFT));

        TreeSet<String> houseList = new TreeSet<String>();
        for (House house : selectedPlanet.getInfluence().getHouses()) {
            houseList.add(house.getName());
            ownersMap.put(house.getName(), selectedPlanet.getInfluence().getInfluence(house.getId()));
        }

        planetOwnersList = new JComboBox(houseList.toArray());
        planetOwnersList.addActionListener(this);
        planetOwnersList.setActionCommand(planetOwnersListCommand);
        planetOwnersList.setSelectedIndex(0);
        planetOwnersList.setPreferredSize(comboBoxSize);
        planetOwnersList.setMaximumSize(comboBoxSize);
        planetOwnersList.setMinimumSize(comboBoxSize);

        panel4.add(planetOwnersList);

        currentFactionOwnerShip.setPreferredSize(textFieldSize);
        currentFactionOwnerShip.setMaximumSize(textFieldSize);
        currentFactionOwnerShip.setMinimumSize(textFieldSize);
        currentFactionOwnerShip.addKeyListener(this);
        panel4.add(currentFactionOwnerShip);

        JPanel panel5 = new JPanel();

        ownerNames = new JComboBox();
        populateHouseNames(ownerNames);
        ownerNames.setPreferredSize(comboBoxSize);
        ownerNames.setMaximumSize(comboBoxSize);
        ownerNames.setMinimumSize(comboBoxSize);
        panel5.add(ownerNames);
        panel5.add(newFacitonOwnerShip);

        JPanel buttonPanel = new JPanel();

        addOwnerButton.addActionListener(this);
        addOwnerButton.setActionCommand(addOwnerCommand);
        removeOwnerButton.addActionListener(this);
        removeOwnerButton.setActionCommand(removeOwnerCommand);
        resetOwnersButton.addActionListener(this);
        resetOwnersButton.setActionCommand(resetOwnersCommand);

        buttonPanel.add(addOwnerButton);
        buttonPanel.add(removeOwnerButton);
        buttonPanel.add(resetOwnersButton);
        planetInfo.add(panel1);
        planetInfo.add(panel2);
        planetInfo.add(panel3);
        planetInfo.add(panel4);
        planetInfo.add(panel5);
        planetInfo.add(buttonPanel);
        planetInfo.setBorder(BorderFactory.createLineBorder(Color.black));
        planetInfo.repaint();
    }

    private void loadPlanetProduction() {
        planetProduction = new JPanel();
        Dimension textFieldSize = new Dimension(35, 22);
        Dimension comboBoxSize = new Dimension(150, 22);

        planetProduction.setBorder(BorderFactory.createLineBorder(Color.black));
        planetProduction.setLayout(new BoxLayout(planetProduction, BoxLayout.Y_AXIS));

        JPanel panel1 = new JPanel(new SpringLayout());
        panel1.add(new JLabel("Warehouses:", JLabel.TRAILING));

        planetBays.setText(Integer.toString(selectedPlanet.getBaysProvided()));
        planetBays.setName("BaysProvided");
        planetBays.addActionListener(this);
        planetBays.setPreferredSize(textFieldSize);
        planetBays.setMaximumSize(textFieldSize);
        planetBays.setMinimumSize(textFieldSize);
        panel1.add(planetBays);

        panel1.add(new JLabel("Production:", JLabel.TRAILING));

        planetComps.setText(Integer.toString(selectedPlanet.getCompProduction()));
        planetComps.setName("CompProduction");
        planetComps.addActionListener(this);
        planetComps.setPreferredSize(textFieldSize);
        planetComps.setMaximumSize(textFieldSize);
        planetComps.setMinimumSize(textFieldSize);
        panel1.add(planetComps);

        SpringLayoutHelper.setupSpringGrid(panel1, 4);

        JPanel panel2 = new JPanel();

        panel2.add(new JLabel("Factories", JLabel.TRAILING));

        TreeSet<String> factoryList = new TreeSet<String>();
        for (UnitFactory factory : selectedPlanet.getUnitFactories()) {
            factoryList.add(factory.getSize() + " " + factory.getFullTypeString().trim() + " " + factory.getName() + " " + factory.getFounder() + " " + factory.getBuildTableFolder() + " " + factory.getAccessLevel());
            factoryMap.put(factory.getName(), factory.getName() + "#" + factory.getSize() + "#" + factory.getFounder() + "#" + factory.getType() + "#" + factory.getBuildTableFolder() + "#" + factory.getAccessLevel());
        }

        planetFactories = new JComboBox(factoryList.toArray());
        planetFactories.setOpaque(false);
        panel2.add(planetFactories);

        JPanel panel3 = new JPanel(new SpringLayout());

        newFactoryName.setToolTipText("Name of your new factory");
        factoryOwners = new JComboBox();
        populateHouseNames(factoryOwners);
        factoryOwners.setPreferredSize(comboBoxSize);
        factoryOwners.setMaximumSize(comboBoxSize);
        factoryOwners.setMinimumSize(comboBoxSize);
        panel3.add(factorySize);
        panel3.add(factoryType);
        panel3.add(factoryOwners);
        panel3.add(newFactoryName);
        panel3.add(new JLabel("Build Table", JLabel.TRAILING));
        panel3.add(newFactoryBuildTable);
        newFactoryBuildTable.setToolTipText("Factory can use a sub folder of Standard for its build tables");
        panel3.add(new JLabel("Access Level", JLabel.TRAILING));
        panel3.add(newFactoryAccessLevel);
        newFactoryAccessLevel.setToolTipText("Subfaction level needed to access this factory");

        SpringLayoutHelper.setupSpringGrid(panel3, 4);

        JPanel buttonPanel = new JPanel();

        addFactory.addActionListener(this);
        addFactory.setActionCommand(addFactoryCommand);
        buttonPanel.add(addFactory);

        removeFactory.addActionListener(this);
        removeFactory.setActionCommand(removeFactoryCommand);
        buttonPanel.add(removeFactory);

        removeAllFactories.addActionListener(this);
        removeAllFactories.setActionCommand(removeAllFactoriesCommand);
        buttonPanel.add(removeAllFactories);

        planetProduction.add(panel1);
        planetProduction.add(panel2);
        planetProduction.add(panel3);
        planetProduction.add(buttonPanel);
        planetProduction.repaint();
    }

    private void loadPlanetTerrain() {
        planetTerrain = new JPanel();
        planetTerrain.setBorder(BorderFactory.createLineBorder(Color.black));
        planetTerrain.setLayout(new BoxLayout(planetTerrain, BoxLayout.Y_AXIS));

        JPanel panel1 = new JPanel();

        panel1.add(new JLabel("Terrains:", JLabel.CENTER));

        TreeSet<String> terrainList = new TreeSet<String>();
        Iterator<Continent> terrains = selectedPlanet.getEnvironments().iterator();
        while (terrains.hasNext()) {
            Continent terrain = terrains.next();
            terrainList.add(terrain.getEnvironment().getName());
            terrainMap.put(terrain.getEnvironment().getName(), terrain.getSize());
        }

        planetTerrains = new JComboBox(terrainList.toArray());
        planetTerrains.addActionListener(this);
        planetTerrains.setActionCommand(planetTerrainsCombo);
        if (planetTerrains.getItemCount() > 0) {
            planetTerrains.setSelectedIndex(0);
        }
        currentTerrainPercent.addKeyListener(this);

        panel1.add(planetTerrains);
        panel1.add(currentTerrainPercent);

        JPanel panel2 = new JPanel();
        allTerrains = new JComboBox();
        terrainList = new TreeSet<String>();
        for (Terrain terrain : mwclient.getData().getAllTerrains()) {
            if (terrainMap.containsKey(terrain.getName())) {
                continue;
            }
            terrainList.add(terrain.getName());
        }

        addAllItems(allTerrains, terrainList);
        panel2.add(allTerrains);

        newTerrainPercent.setToolTipText("Enter the % Chance for this new Terrain");
        panel2.add(newTerrainPercent);

        JPanel buttonPanel = new JPanel();

        addTerrain.addActionListener(this);
        addTerrain.setActionCommand(addTerrainCommand);
        buttonPanel.add(addTerrain);

        removeTerrain.addActionListener(this);
        removeTerrain.setActionCommand(RemoveTerrainCommand);
        buttonPanel.add(removeTerrain);

        removeAllTerrains.addActionListener(this);
        removeAllTerrains.setActionCommand(removeAllTerrainsCommand);
        buttonPanel.add(removeAllTerrains);

        planetTerrain.add(panel1);
        planetTerrain.add(panel2);
        planetTerrain.add(buttonPanel);
        planetTerrain.repaint();
    }

    private void loadAdvancedTerrains() {

        planetAdvancedTerrain = new JPanel();

        planetAdvancedTerrain.setBorder(BorderFactory.createLineBorder(Color.black));
        /*
         * Format the Reward Points panel. Spring layout.
         */
        aTerrain = mwclient.getData().getPlanetByName(planetName).getAdvancedTerrain().get(advanceTerrainId);
        planetAdvancedTerrain.setLayout(new BoxLayout(planetAdvancedTerrain, BoxLayout.Y_AXIS));

        JPanel textPanel = new JPanel(new SpringLayout());
        JPanel checkboxPanel = new JPanel();

        if (useAdvancedTerrain) {

            for (int aTerrainId : selectedPlanet.getAdvancedTerrain().keySet()) {
                advancedTerrainMap.put(mwclient.getData().getTerrain(aTerrainId).getName(), selectedPlanet.getAdvancedTerrain().get(aTerrainId));
            }
            textPanel.add(new JLabel("Display Name:", SwingConstants.TRAILING));
            DisplayNameText.setToolTipText("Name of the Continent to appear in planet info");
            DisplayNameText.addKeyListener(this);
            textPanel.add(DisplayNameText);

            textPanel.add(new JLabel("Static Map Name:", SwingConstants.TRAILING));
            StaticMapNameText.setToolTipText("<HTML>Name of a static map you wish to use instead of the RMG<BR>Static Map must be checked to work<br>leave on surprise to grab a random map");
            StaticMapNameText.addKeyListener(this);
            textPanel.add(StaticMapNameText);

            textPanel.add(new JLabel("Map X Size:", SwingConstants.TRAILING));
            XSizeText.setToolTipText("For static maps X size of the Map");
            XSizeText.addKeyListener(this);
            textPanel.add(XSizeText);

            textPanel.add(new JLabel("Map Y Size:", SwingConstants.TRAILING));
            YSizeText.setToolTipText("For static maps Y size of the Map");
            YSizeText.addKeyListener(this);
            textPanel.add(YSizeText);

            textPanel.add(new JLabel("Board X Size:", SwingConstants.TRAILING));
            XBoardSizeText.setToolTipText("For static maps X size of the Board");
            XBoardSizeText.addKeyListener(this);
            textPanel.add(XBoardSizeText);

            textPanel.add(new JLabel("Board Y Size:", SwingConstants.TRAILING));
            YBoardSizeText.setToolTipText("For static maps Y size of the Board");
            YBoardSizeText.addKeyListener(this);
            textPanel.add(YBoardSizeText);

            textPanel.add(new JLabel("EMI Chance:", SwingConstants.TRAILING));
            emiChanceText.setToolTipText("Chance emi is in effect on the planet.");
            emiChanceText.addKeyListener(this);
            textPanel.add(emiChanceText);

            textPanel.add(new JLabel("Low Temp:", SwingConstants.TRAILING));
            LowTempText.setToolTipText("The Lowest temp for this terrain");
            LowTempText.addKeyListener(this);
            textPanel.add(LowTempText);

            textPanel.add(new JLabel("High Temp:", SwingConstants.TRAILING));
            HighTempText.setToolTipText("The Highest temp for this terrain");
            HighTempText.addKeyListener(this);
            textPanel.add(HighTempText);

            textPanel.add(new JLabel("Gravity:", SwingConstants.TRAILING));
            GravityText.setToolTipText("Gravity for this terrain");
            GravityText.addKeyListener(this);
            textPanel.add(GravityText);

            textPanel.add(new JLabel("Dusk Chance:", SwingConstants.TRAILING));
            DuskChanceText.setToolTipText("Chance for dusk to occur on this terrain");
            DuskChanceText.addKeyListener(this);
            textPanel.add(DuskChanceText);

            textPanel.add(new JLabel("Night Chance:", SwingConstants.TRAILING));
            NightChanceText.setToolTipText("Chance for night to occur on this terrain");
            NightChanceText.addKeyListener(this);
            textPanel.add(NightChanceText);

            textPanel.add(new JLabel("Moonless Night:", SwingConstants.TRAILING));
            MoonLessNightChanceText.setToolTipText("Chance for a moonless night to occur on this terrain");
            MoonLessNightChanceText.addKeyListener(this);
            textPanel.add(MoonLessNightChanceText);

            textPanel.add(new JLabel("Pitch Black:", SwingConstants.TRAILING));
            PitchBlackNightChanceText.setToolTipText("Chance for a pitch black night to occur on this terrain");
            PitchBlackNightChanceText.addKeyListener(this);
            textPanel.add(PitchBlackNightChanceText);

            textPanel.add(new JLabel("Temp Reduction:", SwingConstants.TRAILING));
            NightTempModText.setToolTipText("<html>Calculated temp is reduced by <br> this much at night and 1/2 as much at dusk/down<br>i.e. if set to 10 and the temp is 50 the new temp with be 40</html>");
            NightTempModText.addKeyListener(this);
            textPanel.add(NightTempModText);

            textPanel.add(new JLabel("Light Rainfall:", SwingConstants.TRAILING));
            lightRainfallChanceText.setToolTipText("<html>Chance for Light Rainfall</html>");
            lightRainfallChanceText.addKeyListener(this);
            textPanel.add(lightRainfallChanceText);

            textPanel.add(new JLabel("Moderate Rainfall:", SwingConstants.TRAILING));
            moderateRainfallChanceText.setToolTipText("<html>Chance for moderate Rainfall</html>");
            moderateRainfallChanceText.addKeyListener(this);
            textPanel.add(moderateRainfallChanceText);

            textPanel.add(new JLabel("Heavy Rainfall:", SwingConstants.TRAILING));
            heavyRainfallChanceText.setToolTipText("<html>Chance for Heavy Rainfall</html>");
            heavyRainfallChanceText.addKeyListener(this);
            textPanel.add(heavyRainfallChanceText);

            textPanel.add(new JLabel("Downpour:", SwingConstants.TRAILING));
            downPourChanceText.setToolTipText("<html>Chance for a downpour</html>");
            downPourChanceText.addKeyListener(this);
            textPanel.add(downPourChanceText);

            textPanel.add(new JLabel("Light Snowfall:", SwingConstants.TRAILING));
            lightSnowfallChanceText.setToolTipText("<html>Chance for Light Snowfall</html>");
            lightSnowfallChanceText.addKeyListener(this);
            textPanel.add(lightSnowfallChanceText);

            textPanel.add(new JLabel("Moderate Snowfall:", SwingConstants.TRAILING));
            moderateSnowfallChanceText.setToolTipText("<html>Chance for Moderate Snowfall</html>");
            moderateSnowfallChanceText.addKeyListener(this);
            textPanel.add(moderateSnowfallChanceText);

            textPanel.add(new JLabel("Heavy Snowfall:", SwingConstants.TRAILING));
            heavySnowfallChanceText.setToolTipText("<html>Chance for Heavy Snowfall</html>");
            heavySnowfallChanceText.addKeyListener(this);
            textPanel.add(heavySnowfallChanceText);

            textPanel.add(new JLabel("Sleet:", SwingConstants.TRAILING));
            sleetChanceText.setToolTipText("<html>Chance for Sleet</html>");
            sleetChanceText.addKeyListener(this);
            textPanel.add(sleetChanceText);

            textPanel.add(new JLabel("Ice Storm:", SwingConstants.TRAILING));
            iceStormChanceText.setToolTipText("<html>Chance for Ice Storm</html>");
            iceStormChanceText.addKeyListener(this);
            textPanel.add(iceStormChanceText);

            textPanel.add(new JLabel("Light Hail:", SwingConstants.TRAILING));
            lightHailChanceText.setToolTipText("<html>Chance for light hail</html>");
            lightHailChanceText.addKeyListener(this);
            textPanel.add(lightHailChanceText);

            textPanel.add(new JLabel("Heavy Hail:", SwingConstants.TRAILING));
            heavyHailChanceText.setToolTipText("<html>Chance for heavy hail</html>");
            heavyHailChanceText.addKeyListener(this);
            textPanel.add(heavyHailChanceText);

            textPanel.add(new JLabel("Light Winds:", SwingConstants.TRAILING));
            lightWindsChanceText.setToolTipText("<html>Chance for Light Winds</html>");
            lightWindsChanceText.addKeyListener(this);
            textPanel.add(lightWindsChanceText);

            textPanel.add(new JLabel("Moderate Winds:", SwingConstants.TRAILING));
            moderateWindsChanceText.setToolTipText("<html>Chance for Moderate Winds</html>");
            moderateWindsChanceText.addKeyListener(this);
            textPanel.add(moderateWindsChanceText);

            textPanel.add(new JLabel("Strong Winds:", SwingConstants.TRAILING));
            strongWindsChanceText.setToolTipText("<html>Chance for Strong Winds</html>");
            strongWindsChanceText.addKeyListener(this);
            textPanel.add(strongWindsChanceText);

            textPanel.add(new JLabel("Storm Winds:", SwingConstants.TRAILING));
            stormWindsChanceText.setToolTipText("<html>Chance for Storm Winds</html>");
            stormWindsChanceText.addKeyListener(this);
            textPanel.add(stormWindsChanceText);

            textPanel.add(new JLabel("Tornado F1-F3:", SwingConstants.TRAILING));
            tornadoF13WindsChanceText.setToolTipText("<html>Chance for an F1-F3 Tornado</html>");
            tornadoF13WindsChanceText.addKeyListener(this);
            textPanel.add(tornadoF13WindsChanceText);

            textPanel.add(new JLabel("Tornado F4+:", SwingConstants.TRAILING));
            tornadoF4ChanceText.setToolTipText("<html>Chance for an F4+ Tornado</html>");
            tornadoF4ChanceText.addKeyListener(this);
            textPanel.add(tornadoF4ChanceText);

            textPanel.add(new JLabel("Light Fog:", SwingConstants.TRAILING));
            lightFogChanceText.setToolTipText("<html>Chance for light fog</html>");
            lightFogChanceText.addKeyListener(this);
            textPanel.add(lightFogChanceText);

            textPanel.add(new JLabel("Heavy Fog:", SwingConstants.TRAILING));
            heavyFogChanceText.setToolTipText("<html>Chance for heavy fog</html>");
            heavyFogChanceText.addKeyListener(this);
            textPanel.add(heavyFogChanceText);

            // run the spring layout
            SpringLayoutHelper.setupSpringGrid(textPanel, 6);

            isStaticMapCB.setText("Use Static Maps");
            isStaticMapCB.setToolTipText("Check if you want to use static maps.");
            isStaticMapCB.setActionCommand(staticMapCBCommmand);
            checkboxPanel.add(isStaticMapCB);

            checkboxPanel.add(new JLabel("Atmosphere:", SwingConstants.TRAILING));
            atmosphere.setToolTipText("<html>What type of atmosphere this terrain has.</html>");
            atmosphere.setActionCommand(atmosphereCommand);
            checkboxPanel.add(atmosphere);

        } else {

            textPanel.add(new JLabel("Low Temp:", SwingConstants.TRAILING));
            LowTempText.setToolTipText("The Lowest temp for this terrain");
            textPanel.add(LowTempText);

            textPanel.add(new JLabel("High Temp:", SwingConstants.TRAILING));
            HighTempText.setToolTipText("The Highest temp for this terrain");
            textPanel.add(HighTempText);

            textPanel.add(new JLabel("Gravity:", SwingConstants.TRAILING));
            GravityText.setToolTipText("Gravity for this terrain");
            textPanel.add(GravityText);

            textPanel.add(new JLabel("Night Chance:", SwingConstants.TRAILING));
            NightChanceText.setToolTipText("Chance for night to occur on this terrain");
            textPanel.add(NightChanceText);

            textPanel.add(new JLabel("Temp Reduction:", SwingConstants.TRAILING));
            NightTempModText.setToolTipText("<html>Calculated temp is reduced by <br> this much at night and 1/2 as much at dusk/down<br>i.e. if set to 10 and the temp is 50 the new temp with be 40</html>");
            textPanel.add(NightTempModText);

            // run the spring layout
            SpringLayoutHelper.setupSpringGrid(textPanel, 4);
        }
        planetAdvancedTerrain.add(checkboxPanel);
        planetAdvancedTerrain.add(textPanel);

        if (aTerrain != null && useAdvancedTerrain) {
            DisplayNameText.setText(aTerrain.getDisplayName());
            StaticMapNameText.setText(aTerrain.getStaticMapName());
            XSizeText.setText(Integer.toString(aTerrain.getXSize()));
            YSizeText.setText(Integer.toString(aTerrain.getYSize()));
            XBoardSizeText.setText(Integer.toString(aTerrain.getXBoardSize()));
            YBoardSizeText.setText(Integer.toString(aTerrain.getYBoardSize()));
            LowTempText.setText(Integer.toString(aTerrain.getLowTemp()));
            HighTempText.setText(Integer.toString(aTerrain.getHighTemp()));
            GravityText.setText(Double.toString(aTerrain.getGravity()));
            NightChanceText.setText(Integer.toString(aTerrain.getNightChance()));
            NightTempModText.setText(Integer.toString(aTerrain.getNightTempMod()));
            heavyRainfallChanceText.setText(Integer.toString(aTerrain.getHeavyRainfallChance()));
            heavySnowfallChanceText.setText(Integer.toString(aTerrain.getHeavySnowfallChance()));
            lightRainfallChanceText.setText(Integer.toString(aTerrain.getLightRainfallChance()));
            moderateWindsChanceText.setText(Integer.toString(aTerrain.getModerateWindsChance()));
            DuskChanceText.setText(Integer.toString(aTerrain.getDuskChance()));
            MoonLessNightChanceText.setText(Integer.toString(aTerrain.getMoonLessNightChance()));
            PitchBlackNightChanceText.setText(Integer.toString(aTerrain.getPitchBlackNightChance()));
            moderateRainfallChanceText.setText(Integer.toString(aTerrain.getModerateRainFallChance()));
            downPourChanceText.setText(Integer.toString(aTerrain.getDownPourChance()));
            lightSnowfallChanceText.setText(Integer.toString(aTerrain.getLightSnowfallChance()));
            moderateSnowfallChanceText.setText(Integer.toString(aTerrain.getModerateSnowFallChance()));
            sleetChanceText.setText(Integer.toString(aTerrain.getSleetChance()));
            iceStormChanceText.setText(Integer.toString(aTerrain.getIceStormChance()));
            lightHailChanceText.setText(Integer.toString(aTerrain.getLightHailChance()));
            heavyHailChanceText.setText(Integer.toString(aTerrain.getHeavyHailChance()));
            lightFogChanceText.setText(Integer.toString(aTerrain.getLightFogChance()));
            heavyFogChanceText.setText(Integer.toString(aTerrain.getHeavyFogChance()));
            emiChanceText.setText(Integer.toString(aTerrain.getEMIChance()));
            lightWindsChanceText.setText(Integer.toString(aTerrain.getLightWindsChance()));
            strongWindsChanceText.setText(Integer.toString(aTerrain.getStrongWindsChance()));
            stormWindsChanceText.setText(Integer.toString(aTerrain.getStormWindsChance()));
            tornadoF13WindsChanceText.setText(Integer.toString(aTerrain.getTornadoF13WindsChance()));
            tornadoF4ChanceText.setText(Integer.toString(aTerrain.getTornadoF4WindsChance()));
            isStaticMapCB.setSelected(aTerrain.isStaticMap());
            atmosphere.setSelectedIndex(aTerrain.getAtmosphere());
            atmosphere.addActionListener(this);
            isStaticMapCB.addActionListener(this);
        } else {
            LowTempText.setText(Integer.toString(selectedPlanet.getTemp().width));
            HighTempText.setText(Double.toString(selectedPlanet.getTemp().height));
            GravityText.setText(Double.toString(selectedPlanet.getGravity()));
            NightChanceText.setText(Integer.toString(selectedPlanet.getNightChance()));
            NightTempModText.setText(Integer.toString(selectedPlanet.getNightTempMod()));
        }
        planetAdvancedTerrain.repaint();
    }

    private void loadPlanetNames() {

        planets = new JPanel();
        Collection<Planet> planets = mwclient.getData().getAllPlanets();
        Dimension comboBoxSize = new Dimension(150, 22);
        // setup the a list of names to feed into a list
        TreeSet<String> pNames = new TreeSet<String>();// tree to alpha sort
        for (Planet planet : planets) {
            pNames.add(planet.getName());
        }

        planetNames = new JComboBox(pNames.toArray());
        planetNames.setSelectedItem(planetName);
        planetNames.addActionListener(this);
        planetNames.setActionCommand(refreshCommand);
        planetNames.setPreferredSize(comboBoxSize);
        planetNames.setMaximumSize(comboBoxSize);
        planetNames.setMinimumSize(comboBoxSize);

        this.planets.add(new JLabel("Planet:", JLabel.TRAILING));
        this.planets.add(planetNames);

        this.planets.setBorder(BorderFactory.createLineBorder(Color.black));
    }

    private void loadPlanetInfoData() {

        planetXPosition.setText(Double.toString(selectedPlanet.getPosition().getX()));
        planetXPosition.setToolTipText("Planets X Coord");

        planetYPosition.setText(Double.toString(selectedPlanet.getPosition().getY()));
        planetYPosition.setToolTipText("Planets Y Coord");

        isHomeWorldCB.setText("HomeWorld");
        isHomeWorldCB.setSelected(selectedPlanet.isHomeWorld());

        isConquerable.setText("Conquerable");
        isConquerable.setSelected(selectedPlanet.isConquerable());

        ownersMap.clear();

        houseNames.removeAllItems();
        populateHouseNames(houseNames);
        houseNames.setSelectedItem(selectedPlanet.getOriginalOwner());

        TreeSet<String> houseList = new TreeSet<String>();

        for (House house : selectedPlanet.getInfluence().getHouses()) {
            if (removedOwners.contains(house.getName())) {
                continue;
            }
            houseList.add(house.getName());
            ownersMap.put(house.getName(), selectedPlanet.getInfluence().getInfluence(house.getId()));
        }

        planetOwnersList.removeActionListener(this);
        planetOwnersList.removeAllItems();
        addAllItems(planetOwnersList, houseList);
        planetOwnersList.addActionListener(this);
        planetOwnersList.setActionCommand(planetOwnersListCommand);
        planetOwnersList.setSelectedIndex(0);

        minPlanetOwnerShip.setText(Integer.toString(selectedPlanet.getMinPlanetOwnerShip()));
        planetConquerPoints.setText(Integer.toString(selectedPlanet.getConquestPoints()));

        ownerNames.removeAllItems();
        populateHouseNames(ownerNames);

    }

    private void loadPlanetProductionData() {

        planetBays.setText(Integer.toString(selectedPlanet.getBaysProvided()));
        planetBays.setName("BaysProvided");
        planetBays.addActionListener(this);

        planetComps.setText(Integer.toString(selectedPlanet.getCompProduction()));
        planetComps.setName("CompProduction");
        planetComps.addActionListener(this);

        factoryMap.clear();
        TreeSet<String> factoryList = new TreeSet<String>();
        for (UnitFactory factory : selectedPlanet.getUnitFactories()) {
            if (removedFactory.contains(factory.getName())) {
                continue;
            }
            factoryList.add(factory.getSize() + " " + factory.getFullTypeString().trim() + " " + factory.getName() + " " + factory.getFounder() + " " + factory.getBuildTableFolder() + " " + factory.getAccessLevel());
            factoryMap.put(factory.getName(), factory.getName() + "#" + factory.getSize() + "#" + factory.getFounder() + "#" + factory.getType() + "#" + factory.getBuildTableFolder() + "#" + factory.getAccessLevel());
        }

        planetFactories.removeAllItems();
        addAllItems(planetFactories, factoryList);
    }

    private void loadPlanetTerrainData() {

        TreeSet<String> terrainList = new TreeSet<String>();
        Iterator<Continent> terrains = selectedPlanet.getEnvironments().iterator();
        terrainMap.clear();
        while (terrains.hasNext()) {
            Continent terrain = terrains.next();
            if (removedTerrain.contains(terrain.getEnvironment().getName())) {
                continue;
            }
            terrainList.add(terrain.getEnvironment().getName());
            terrainMap.put(terrain.getEnvironment().getName(), terrain.getSize());
        }

        planetTerrains.removeActionListener(this);
        planetTerrains.removeAllItems();
        addAllItems(planetTerrains, terrainList);
        planetTerrains.addActionListener(this);
        planetTerrains.setActionCommand(planetTerrainsCombo);
        if (planetTerrains.getItemCount() > 0) {
            planetTerrains.setSelectedIndex(0);
        }

        allTerrains.removeAllItems();
        terrainList = new TreeSet<String>();
        for (Terrain terrain : mwclient.getData().getAllTerrains()) {
            if (terrainMap.containsKey(terrain.getName())) {
                continue;
            }
            terrainList.add(terrain.getName());
        }

        addAllItems(allTerrains, terrainList);

    }

    private void loadAdvancedTerrainsData() {

        if (planetTerrains.getItemCount() < 1) {
            aTerrain = null;
        } else {
            aTerrain = advancedTerrainMap.get(planetTerrains.getSelectedItem().toString());
        }

        if (aTerrain != null) {
            DisplayNameText.setText(aTerrain.getDisplayName());
            StaticMapNameText.setText(aTerrain.getStaticMapName());
            XSizeText.setText(Integer.toString(aTerrain.getXSize()));
            YSizeText.setText(Integer.toString(aTerrain.getYSize()));
            XBoardSizeText.setText(Integer.toString(aTerrain.getXBoardSize()));
            YBoardSizeText.setText(Integer.toString(aTerrain.getYBoardSize()));
            LowTempText.setText(Integer.toString(aTerrain.getLowTemp()));
            HighTempText.setText(Integer.toString(aTerrain.getHighTemp()));
            GravityText.setText(Double.toString(aTerrain.getGravity()));
            NightChanceText.setText(Integer.toString(aTerrain.getNightChance()));
            NightTempModText.setText(Integer.toString(aTerrain.getNightTempMod()));
            heavyRainfallChanceText.setText(Integer.toString(aTerrain.getHeavyRainfallChance()));
            heavySnowfallChanceText.setText(Integer.toString(aTerrain.getHeavySnowfallChance()));
            lightRainfallChanceText.setText(Integer.toString(aTerrain.getLightRainfallChance()));
            moderateWindsChanceText.setText(Integer.toString(aTerrain.getModerateWindsChance()));
            DuskChanceText.setText(Integer.toString(aTerrain.getDuskChance()));
            MoonLessNightChanceText.setText(Integer.toString(aTerrain.getMoonLessNightChance()));
            PitchBlackNightChanceText.setText(Integer.toString(aTerrain.getPitchBlackNightChance()));
            moderateRainfallChanceText.setText(Integer.toString(aTerrain.getModerateRainFallChance()));
            downPourChanceText.setText(Integer.toString(aTerrain.getDownPourChance()));
            lightSnowfallChanceText.setText(Integer.toString(aTerrain.getLightSnowfallChance()));
            moderateSnowfallChanceText.setText(Integer.toString(aTerrain.getModerateSnowFallChance()));
            sleetChanceText.setText(Integer.toString(aTerrain.getSleetChance()));
            iceStormChanceText.setText(Integer.toString(aTerrain.getIceStormChance()));
            lightHailChanceText.setText(Integer.toString(aTerrain.getLightHailChance()));
            heavyHailChanceText.setText(Integer.toString(aTerrain.getHeavyHailChance()));
            lightFogChanceText.setText(Integer.toString(aTerrain.getLightFogChance()));
            heavyFogChanceText.setText(Integer.toString(aTerrain.getHeavyFogChance()));
            emiChanceText.setText(Integer.toString(aTerrain.getEMIChance()));
            lightWindsChanceText.setText(Integer.toString(aTerrain.getLightWindsChance()));
            strongWindsChanceText.setText(Integer.toString(aTerrain.getStrongWindsChance()));
            stormWindsChanceText.setText(Integer.toString(aTerrain.getStormWindsChance()));
            tornadoF13WindsChanceText.setText(Integer.toString(aTerrain.getTornadoF13WindsChance()));
            tornadoF4ChanceText.setText(Integer.toString(aTerrain.getTornadoF4WindsChance()));

            atmosphere.removeActionListener(this);
            atmosphere.setSelectedIndex(aTerrain.getAtmosphere());
            atmosphere.addActionListener(this);

            isStaticMapCB.removeActionListener(this);
            isStaticMapCB.setSelected(aTerrain.isStaticMap());
            isStaticMapCB.addActionListener(this);
        } else {
            DisplayNameText.setText("");
            StaticMapNameText.setText("");
            XSizeText.setText("");
            YSizeText.setText("");
            XBoardSizeText.setText("");
            YBoardSizeText.setText("");

            LowTempText.setText(Integer.toString(selectedPlanet.getTemp().width));
            HighTempText.setText(Double.toString(selectedPlanet.getTemp().height));
            GravityText.setText(Double.toString(selectedPlanet.getGravity()));
            NightChanceText.setText(Integer.toString(selectedPlanet.getNightChance()));
            NightTempModText.setText(Integer.toString(selectedPlanet.getNightTempMod()));
            heavyRainfallChanceText.setText("");
            heavySnowfallChanceText.setText("");
            lightRainfallChanceText.setText("");
            moderateWindsChanceText.setText("");
            heavyRainfallChanceText.setText("");
            heavySnowfallChanceText.setText("");
            lightRainfallChanceText.setText("");
            moderateWindsChanceText.setText("");
            DuskChanceText.setText("");
            MoonLessNightChanceText.setText("");
            PitchBlackNightChanceText.setText("");
            moderateRainfallChanceText.setText("");
            downPourChanceText.setText("");
            lightSnowfallChanceText.setText("");
            moderateSnowfallChanceText.setText("");
            sleetChanceText.setText("");
            iceStormChanceText.setText("");
            lightHailChanceText.setText("");
            heavyHailChanceText.setText("");
            lightFogChanceText.setText("");
            heavyFogChanceText.setText("");
            emiChanceText.setText("");
            lightWindsChanceText.setText("");
            strongWindsChanceText.setText("");
            stormWindsChanceText.setText("");
            tornadoF13WindsChanceText.setText("");
            tornadoF4ChanceText.setText("");

            // isStaticMapCB.setSelected(false);
        }
    }

    private void loadPlanetNamesData() {

        // setup the a list of names to feed into a list
        planetNames.removeActionListener(this);
        planetNames.removeAllItems();
        TreeSet<String> pNames = new TreeSet<String>();// tree to alpha sort
        for (Planet planet : mwclient.getData().getAllPlanets()) {
            pNames.add(planet.getName());
        }

        planetNames.removeAllItems();
        addAllItems(planetNames, pNames);

        planetNames.setSelectedItem(planetName);
        planetNames.addActionListener(this);
        planetNames.setActionCommand(refreshCommand);

    }

    private void populateHouseNames(JComboBox combo) {

        TreeSet<String> factionNames = new TreeSet<String>();// tree to alpha
                                                             // sort
        for (House house : mwclient.getData().getAllHouses()) {
            factionNames.add(house.getName());
        }

        addAllItems(combo, factionNames);
    }

    private void addAllItems(JComboBox combo, TreeSet<String> list) {

        // combo.addItem("None");
        for (String name : list) {
            combo.addItem(name);
        }
    }

    private int getTerrainId() {
        try {
            return mwclient.getData().getTerrainByName(planetTerrains.getSelectedItem().toString().trim()).getId();
        } catch (Exception ex) {
            return -1;
        }
    }

    public void keyPressed(KeyEvent arg0) {
    }

    public void keyReleased(KeyEvent e) {
        if (e.getComponent() == null) {
            return;
        }

        if (e.getComponent().equals(currentFactionOwnerShip)) {
            try {
                int percent = Integer.parseInt(currentFactionOwnerShip.getText().trim().replaceAll("%", ""));
                ownersMap.put(planetOwnersList.getSelectedItem().toString(), percent);
            } catch (Exception ex) {
                CampaignData.mwlog.errLog(ex);
            }
        } else if (e.getComponent().equals(currentTerrainPercent)) {
            try {
                int percent = Integer.parseInt(currentTerrainPercent.getText().trim().replaceAll("%", ""));
                terrainMap.put(planetTerrains.getSelectedItem().toString(), percent);
            } catch (Exception ex) {
                CampaignData.mwlog.errLog(ex);
            }
        } else if (e.getComponent().equals(StaticMapNameText) || e.getComponent().equals(DisplayNameText) || e.getComponent().equals(XBoardSizeText) || e.getComponent().equals(YBoardSizeText) || e.getComponent().equals(XSizeText) || e.getComponent().equals(YSizeText) || e.getComponent().equals(LowTempText) || e.getComponent().equals(HighTempText) || e.getComponent().equals(NightChanceText) || e.getComponent().equals(NightTempModText) || e.getComponent().equals(GravityText) || e.getComponent().equals(lightRainfallChanceText) || e.getComponent().equals(heavyRainfallChanceText) || e.getComponent().equals(heavySnowfallChanceText) || e.getComponent().equals(moderateWindsChanceText) || e.getComponent().equals(DuskChanceText) || e.getComponent().equals(MoonLessNightChanceText) || e.getComponent().equals(PitchBlackNightChanceText) || e.getComponent().equals(moderateRainfallChanceText) || e.getComponent().equals(downPourChanceText) || e.getComponent().equals(lightSnowfallChanceText) || e.getComponent().equals(moderateSnowfallChanceText) || e.getComponent().equals(sleetChanceText) || e.getComponent().equals(iceStormChanceText) || e.getComponent().equals(lightHailChanceText) || e.getComponent().equals(heavyHailChanceText) || e.getComponent().equals(lightFogChanceText) || e.getComponent().equals(heavyFogChanceText) || e.getComponent().equals(emiChanceText) || e.getComponent().equals(lightWindsChanceText) || e.getComponent().equals(strongWindsChanceText) || e.getComponent().equals(stormWindsChanceText) || e.getComponent().equals(tornadoF13WindsChanceText) || e.getComponent().equals(tornadoF4ChanceText)) {
            updateAdvancedTerrain();
        }
    }

    public void keyTyped(KeyEvent e) {
    }

    private void updateAdvancedTerrain() {
        AdvancedTerrain aTerrain = advancedTerrainMap.get(planetTerrains.getSelectedItem().toString());
        if (aTerrain == null) {
            aTerrain = new AdvancedTerrain();
            advancedTerrainMap.put(planetTerrains.getSelectedItem().toString(), aTerrain);
        }

        aTerrain.setDisplayName(DisplayNameText.getText());
        aTerrain.setStaticMapName(StaticMapNameText.getText());
        aTerrain.setXSize(Integer.parseInt(XSizeText.getText()));
        aTerrain.setYSize(Integer.parseInt(YSizeText.getText()));
        aTerrain.setXBoardSize(Integer.parseInt(XBoardSizeText.getText()));
        aTerrain.setYBoardSize(Integer.parseInt(YBoardSizeText.getText()));
        aTerrain.setLowTemp(Integer.parseInt(LowTempText.getText()));
        aTerrain.setHighTemp(Integer.parseInt(HighTempText.getText()));
        aTerrain.setGravity(Double.parseDouble(GravityText.getText()));
        aTerrain.setNightChance(Integer.parseInt(NightChanceText.getText()));
        aTerrain.setNightTempMod(Integer.parseInt(NightTempModText.getText()));
        aTerrain.setHeavySnowfallChance(Integer.parseInt(heavySnowfallChanceText.getText()));
        aTerrain.setLightRainfallChance(Integer.parseInt(lightRainfallChanceText.getText()));
        aTerrain.setHeavyRainfallChance(Integer.parseInt(heavyRainfallChanceText.getText()));
        aTerrain.setModerateWindsChance(Integer.parseInt(moderateWindsChanceText.getText()));
        aTerrain.setStaticMap(isStaticMapCB.isSelected());
        aTerrain.setDuskChance(Integer.parseInt(DuskChanceText.getText()));
        aTerrain.setMoonLessNightChance(Integer.parseInt(MoonLessNightChanceText.getText()));
        aTerrain.setPitchBlackNightChance(Integer.parseInt(PitchBlackNightChanceText.getText()));
        aTerrain.setModerateRainFallChance(Integer.parseInt(moderateRainfallChanceText.getText()));
        aTerrain.setDownPourChance(Integer.parseInt(downPourChanceText.getText()));
        aTerrain.setLightSnowfallChance(Integer.parseInt(lightSnowfallChanceText.getText()));
        aTerrain.setModerateSnowFallChance(Integer.parseInt(moderateSnowfallChanceText.getText()));
        aTerrain.setSleetChance(Integer.parseInt(sleetChanceText.getText()));
        aTerrain.setIceStormChance(Integer.parseInt(iceStormChanceText.getText()));
        aTerrain.setLightHailChance(Integer.parseInt(lightHailChanceText.getText()));
        aTerrain.setHeavyHailChance(Integer.parseInt(heavyHailChanceText.getText()));
        aTerrain.setLightFogChance(Integer.parseInt(lightFogChanceText.getText()));
        aTerrain.setHeavyfogChance(Integer.parseInt(heavyFogChanceText.getText()));
        aTerrain.setEMIChance(Integer.parseInt(emiChanceText.getText()));
        aTerrain.setLightWindChance(Integer.parseInt(lightWindsChanceText.getText()));
        aTerrain.setStrongWindsChance(Integer.parseInt(strongWindsChanceText.getText()));
        aTerrain.setStormWindsChance(Integer.parseInt(stormWindsChanceText.getText()));
        aTerrain.setTornadoF13WindChance(Integer.parseInt(tornadoF13WindsChanceText.getText()));
        aTerrain.setTornadoF4WindsChance(Integer.parseInt(tornadoF4ChanceText.getText()));

        aTerrain.setAtmosphere(atmosphere.getSelectedIndex());

    }

    private boolean saveAllData() {

        try {
            removeOwners();
            removeFactories();
            removeTerrain();

            saveOwners();
            saveFactories();
            saveTerrain();
            saveAdvancedTerrain();
            saveMisc();

        } catch (Exception ex) {
            CampaignData.mwlog.errLog(ex);
            return false;
        }

        return true;
    }

    private void removeOwners() {

        if (removedOwners.size() < 1) {
            return;
        }

        for (String owner : removedOwners) {
            mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c AdminRemovePlanetOwnership#" + planetName + "#" + owner);
        }
    }

    private void removeFactories() {
        mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c AdminRemoveAllFactories#" + planetName);
    }

    private void removeTerrain() {
        mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c AdminRemoveAllTerrain#" + planetName);
    }

    private void saveOwners() {

        for (String owner : ownersMap.keySet()) {
            mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c AdminUpdatePlanetOwnership#" + planetName + "#" + owner + "#" + ownersMap.get(owner));
        }
    }

    private void saveFactories() {
        for (String factory : factoryMap.keySet()) {
            mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c AdminCreateFactory#" + planetName + "#" + factoryMap.get(factory));
        }

    }

    private void saveTerrain() {
        for (String terrain : terrainMap.keySet()) {
            mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c AdminCreateTerrain#" + planetName + "#" + terrain + "#" + terrainMap.get(terrain));
        }

    }

    private void saveAdvancedTerrain() {

        if (useAdvancedTerrain) {
            for (String terrain : terrainMap.keySet()) {
                AdvancedTerrain aTerrain = advancedTerrainMap.get(terrain);
                Terrain pTerrain = mwclient.getData().getTerrainByName(terrain);
                if (pTerrain == null) {
                    CampaignData.mwlog.errLog("Unable to find Terrain " + terrain + " on planet " + selectedPlanet.getName());
                    throw new NullPointerException();
                }
                int id = pTerrain.getId();

                if (aTerrain == null) {
                    aTerrain = new AdvancedTerrain();
                    aTerrain.setDisplayName(DisplayNameText.getText());
                    aTerrain.setStaticMapName(StaticMapNameText.getText());
                    aTerrain.setXSize(Integer.parseInt(XSizeText.getText()));
                    aTerrain.setYSize(Integer.parseInt(YSizeText.getText()));
                    aTerrain.setXBoardSize(Integer.parseInt(XBoardSizeText.getText()));
                    aTerrain.setYBoardSize(Integer.parseInt(YBoardSizeText.getText()));
                    aTerrain.setLowTemp(Integer.parseInt(LowTempText.getText()));
                    aTerrain.setHighTemp(Integer.parseInt(HighTempText.getText()));
                    aTerrain.setGravity(Double.parseDouble(GravityText.getText()));
                    aTerrain.setNightChance(Integer.parseInt(NightChanceText.getText()));
                    aTerrain.setNightTempMod(Integer.parseInt(NightTempModText.getText()));
                    aTerrain.setHeavySnowfallChance(Integer.parseInt(heavySnowfallChanceText.getText()));
                    aTerrain.setLightRainfallChance(Integer.parseInt(lightRainfallChanceText.getText()));
                    aTerrain.setHeavyRainfallChance(Integer.parseInt(heavyRainfallChanceText.getText()));
                    aTerrain.setModerateWindsChance(Integer.parseInt(moderateWindsChanceText.getText()));
                    aTerrain.setStaticMap(isStaticMapCB.isSelected());
                    aTerrain.setDuskChance(Integer.parseInt(DuskChanceText.getText()));
                    aTerrain.setMoonLessNightChance(Integer.parseInt(MoonLessNightChanceText.getText()));
                    aTerrain.setPitchBlackNightChance(Integer.parseInt(PitchBlackNightChanceText.getText()));
                    aTerrain.setModerateRainFallChance(Integer.parseInt(moderateRainfallChanceText.getText()));
                    aTerrain.setDownPourChance(Integer.parseInt(downPourChanceText.getText()));
                    aTerrain.setLightSnowfallChance(Integer.parseInt(lightSnowfallChanceText.getText()));
                    aTerrain.setModerateSnowFallChance(Integer.parseInt(moderateSnowfallChanceText.getText()));
                    aTerrain.setSleetChance(Integer.parseInt(sleetChanceText.getText()));
                    aTerrain.setIceStormChance(Integer.parseInt(iceStormChanceText.getText()));
                    aTerrain.setLightHailChance(Integer.parseInt(lightHailChanceText.getText()));
                    aTerrain.setHeavyHailChance(Integer.parseInt(heavyHailChanceText.getText()));
                    aTerrain.setLightFogChance(Integer.parseInt(lightFogChanceText.getText()));
                    aTerrain.setHeavyfogChance(Integer.parseInt(heavyFogChanceText.getText()));
                    aTerrain.setEMIChance(Integer.parseInt(emiChanceText.getText()));
                    aTerrain.setLightWindChance(Integer.parseInt(lightWindsChanceText.getText()));
                    aTerrain.setStrongWindsChance(Integer.parseInt(strongWindsChanceText.getText()));
                    aTerrain.setStormWindsChance(Integer.parseInt(stormWindsChanceText.getText()));
                    aTerrain.setTornadoF13WindChance(Integer.parseInt(tornadoF13WindsChanceText.getText()));
                    aTerrain.setTornadoF4WindsChance(Integer.parseInt(tornadoF4ChanceText.getText()));

                    aTerrain.setAtmosphere(atmosphere.getSelectedIndex());
                }

                if (aTerrain.getDisplayName().trim().length() < 1) {
                    aTerrain.setDisplayName(terrain);
                }
                mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c SetAdvancedPlanetTerrain#" + planetName + "#" + id + "#" + aTerrain.toString());

            }
        } else {
            mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c AdminSetPlanetBoardSize#" + planetName + "#" + XBoardSizeText.getText() + "#" + YBoardSizeText.getText());
            mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c AdminSetPlanetGravity#" + planetName + "#" + GravityText.getText());
            mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c AdminSetPlanetMapSize#" + planetName + "#" + XSizeText.getText() + "#" + YSizeText.getText());
            mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c AdminSetPlanetTemperature#" + planetName + "#" + LowTempText.getText() + "#" + HighTempText.getText());
        }
    }

    private void saveMisc() {

        if (!planetXPosition.getText().equals(Double.toString(selectedPlanet.getPosition().getX())) || !planetYPosition.getText().equals(Double.toString(selectedPlanet.getPosition().getY()))) {
            mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c AdminMovePlanet#" + planetName + "#" + planetXPosition.getText() + "#" + planetYPosition.getText());
        }
        if (!houseNames.getSelectedItem().toString().equals(selectedPlanet.getOriginalOwner())) {
            mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c AdminSetPlanetOriginalOwner#" + planetName + "#" + houseNames.getSelectedItem().toString());
        }
        if (!minPlanetOwnerShip.getText().equals(Integer.toString(selectedPlanet.getMinPlanetOwnerShip()))) {
            mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c SetPlanetMinOwnerShip#" + planetName + "#" + minPlanetOwnerShip.getText());
        }
        if (!planetConquerPoints.getText().equals(Integer.toString(selectedPlanet.getConquestPoints()))) {
            mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c SetPlanetConquerPoints#" + planetName + "#" + planetConquerPoints.getText());
        }
        if (isHomeWorldCB.isSelected() != selectedPlanet.isHomeWorld()) {
            mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c Adminsethomeworld#" + planetName + "#" + isHomeWorldCB.isSelected());
        }

        if (isConquerable.isSelected() != selectedPlanet.isConquerable()) {
            mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c SetPlanetConquer#" + planetName + "#" + isConquerable.isSelected());
        }

        if (!planetBays.getText().equals(Integer.toString(selectedPlanet.getBaysProvided()))) {
            mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c Setplanetwarehouse#" + planetName + "#" + planetBays.getText());
        }
        if (!planetComps.getText().equals(Integer.toString(selectedPlanet.getCompProduction()))) {
            mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c Setplanetcompproduction#" + planetName + "#" + planetComps.getText());
        }

    }
}// end PlanetEditorDialog.java
