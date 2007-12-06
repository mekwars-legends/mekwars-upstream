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
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import common.AdvancedTerrain;
import common.Continent;
import common.House;
import common.Planet;
import common.PlanetEnvironment;
import common.UnitFactory;

import client.MWClient;

import client.gui.SpringLayoutHelper;

public final class PlanetEditorDialog implements ActionListener, KeyListener{
	
	//store the client backlink for other things to use
	private MWClient mwclient = null; 
	private String planetName = "";
	private AdvancedTerrain aTerrain = new AdvancedTerrain();
	private int advanceTerrainId = -1;
	private Planet selectedPlanet; 
	private boolean useAdvancedTerrain = false;
	private TreeSet<String>removedOwners = new TreeSet<String>();
	private HashMap<String, Integer>ownersMap = new HashMap<String, Integer>();
	private TreeSet<String>removedTerrain = new TreeSet<String>();
	private HashMap<String, Integer>terrainMap = new HashMap<String, Integer>();
	private TreeSet<String>removedFactory = new TreeSet<String>();
	private HashMap<String, String>factoryMap = new HashMap<String, String>();
	private HashMap<String, AdvancedTerrain>advancedTerrainMap = new HashMap<String, AdvancedTerrain>();
	
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
	private final static String vacuumCBCommand = "VacuumCB";
	
	private final static String windowName = "Vertigo's Planet Editor";
	
	
	//BUTTONS
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
	
	//TEXT FIELDS
	//tab names
	private final JTextField DisplayNameText = new JTextField(5);
	private final JTextField StaticMapNameText = new JTextField(5);
	private final JTextField XSizeText = new JTextField(5);
	private final JTextField YSizeText = new JTextField(5);
	private final JTextField XBoardSizeText = new JTextField(5);
	private final JTextField YBoardSizeText = new JTextField(5);
	private final JTextField LowTempText = new JTextField(5);
	private final JTextField HighTempText = new JTextField(5);
	private final JTextField GravityText = new JTextField(5);
	private final JTextField NightChanceText = new JTextField(5);
	private final JTextField NightTempModText = new JTextField(5);
    private final JTextField MinVisibilityText = new JTextField(5);
    private final JTextField MaxVisibilityText = new JTextField(5);
	private final JTextField planetBays = new JTextField(5);
	private final JTextField planetComps = new JTextField(5);
	private final JTextField planetXPosition = new JTextField(5);
	private final JTextField planetYPosition = new JTextField(5);
	private final JTextField newTerrainPercent = new JTextField(5);
	private final JTextField newFactoryName = new JTextField(10);
	private final JTextField newFactoryBuildTable = new JTextField(10);
	private final JTextField currentFactionOwnerShip = new JTextField(5);
	private final JTextField newFacitonOwnerShip = new JTextField(5);
	private final JTextField currentTerrainPercent = new JTextField(5);
	private final JTextField minPlanetOwnerShip = new JTextField(5);
	private final JTextField planetConquerPoints = new JTextField(5);
	
	private final JCheckBox isStaticMapCB = new JCheckBox();
	private final JCheckBox isVacuumCB = new JCheckBox();
	private final JCheckBox isHomeWorldCB = new JCheckBox();
	
	//STOCK DIALOUG AND PANE
	private JDialog dialog;
	private JOptionPane pane;
	private JPanel masterPanel;
	private JPanel planets;
	private JPanel planetInfo;
	private JPanel planetProduction;
	private JPanel planetTerrain;
	private JPanel planetAdvancedTerrain;
	
    private String[] factoryTypes = { "All", "Mek", "Vee", "Mek & Vee",
            "Inf", "Mek & Inf", "Vee & Inf",
            "Mek & Inf & Vee", "Proto", "Mek & Proto",
            "Vee & Proto", "Mek & Vee & Proto",
            "Inf & Proto", "Mek & Inf & Proto",
            "Vee & Inf & Proto",
            "Mek & Vee & Inf & Proto", "BA",
            "Mek & BA", "Vee & BA",
            "Mek & Vee & BA", "Inf & BA",
            "Mek & Inf & BA",
            "Vee & Inf & BA",
            "Mek & Vee & Inf & BA",
            "Proto & BA", "Mek & Proto & BA",
            "Vee & Proto & BA",
            "Mek & Vee & Proto & BA",
            "Inf & Proto & BA",
            "Mek & Inf & Proto & BA",
            "Vee & Inf & Proto & BA" };

    private String[] factorySizes = { "Light", "Medium", "Heavy", "Assault" };

	//Combo boxes
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
	
	JTabbedPane ConfigPane = new JTabbedPane(SwingConstants.TOP);
	
	public PlanetEditorDialog(MWClient c, String planetName) {
		
		//save the client
		this.mwclient = c;
		this.planetName = planetName;
		
		useAdvancedTerrain = Boolean.parseBoolean(mwclient.getserverConfigs("UseStaticMaps"));
		//Set the tooltips and actions for dialouge buttons
		okayButton.setActionCommand(okayCommand);
		cancelButton.setActionCommand(cancelCommand);
		refreshButton.setActionCommand(refreshCommand);
		
		okayButton.addActionListener(this);
		cancelButton.addActionListener(this);
		refreshButton.addActionListener(this);
		okayButton.setToolTipText("Save Options");
		cancelButton.setToolTipText("Exit without saving changes");
		refreshButton.setToolTipText("Reload data");
		
		//CREATE THE PANELS
		masterPanel = new JPanel();
		masterPanel.setLayout(new BoxLayout(masterPanel,BoxLayout.Y_AXIS));
		
		loadAllPanels();
		masterPanel.add(planets);
		masterPanel.add(planetInfo);
		masterPanel.add(planetProduction);
		masterPanel.add(planetTerrain);
		masterPanel.add(planetAdvancedTerrain);
		
		// Set the user's options
		Object[] options = { refreshButton, okayButton, cancelButton };
		
		// Create the pane containing the buttons
		pane = new JOptionPane(masterPanel,JOptionPane.PLAIN_MESSAGE,JOptionPane.DEFAULT_OPTION, null, options, null);

		// Create the main dialog and set the default button
		dialog = pane.createDialog(masterPanel, windowName);
		dialog.getRootPane().setDefaultButton(okayButton);

		//Show the dialog and get the user's input
		dialog.setLocation(mwclient.getMainFrame().getLocation().x+dialog.getWidth()/2,mwclient.getMainFrame().getLocation().y);
		dialog.setModal(true);
		dialog.pack();
		dialog.setVisible(true);
		
		/*if (pane.getValue() == okayButton) {
			
		}
		else 
			dialog.dispose();*/
	}
	

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();

		if (command.equals(okayCommand)) {
			if ( !saveAllData() )
				JOptionPane.showMessageDialog(mwclient.getMainFrame(), "Unable to Save Data, Check Error Logs");
			try{
				mwclient.refreshData();
				removedFactory.clear();
				removedTerrain.clear();
				removedOwners.clear();
			}catch(Exception ex){
				MWClient.mwClientLog.clientErrLog("PlanetEditorDialog Save Error!");
				MWClient.mwClientLog.clientErrLog(ex);
			}
			refreshAllPanels();
		}
		else if ( command.equals(cancelCommand)){
		    dialog.dispose();
		}else if ( command.equals(refreshCommand)){
			this.planetName = this.planetNames.getSelectedItem().toString();
			this.selectedPlanet = mwclient.getData().getPlanetByName(this.planetName);
			removedOwners.clear();
			removedTerrain.clear();
			removedFactory.clear();
			refreshAllPanels();
		}else if ( command.equals(planetTerrainsCombo)){
			if ( planetTerrains.getItemCount() > 0 ){
				currentTerrainPercent.setText(Integer.toString(terrainMap.get(planetTerrains.getSelectedItem().toString())));
				this.advanceTerrainId = getTerrainId();
				loadAdvancedTerrainsData();
			}
		}else if ( command.equals(planetOwnersListCommand)){
			try{
				currentFactionOwnerShip.setText(Integer.toString(this.ownersMap.get(planetOwnersList.getSelectedItem().toString())));
			}catch(Exception ex){
				currentFactionOwnerShip.setText("");
			}
		}else if ( command.equals(resetOwnersCommand) ){
			//Lets move all the current owners to the remove pile and then add the original owner
			//as the one true owner.
			
			for (int pos = 0; pos < planetOwnersList.getItemCount(); pos++){
				String name = planetOwnersList.getItemAt(pos).toString();
				if ( name.equals(houseNames.getSelectedItem().toString()))
					continue;
				removedOwners.add(name);
			}
			
			planetOwnersList.removeActionListener(this);
			planetOwnersList.removeAllItems();
			planetOwnersList.addItem(houseNames.getSelectedItem().toString());
			planetOwnersList.addActionListener(this);
			ownersMap.put(houseNames.getSelectedItem().toString(),100);
			planetOwnersList.setActionCommand(planetOwnersListCommand);
			if ( planetOwnersList.getItemCount() > 0 )
				planetOwnersList.setSelectedIndex(0);
		}else if ( command.equals(addOwnerCommand) ){
			try{
				int percent = Integer.parseInt(newFacitonOwnerShip.getText().trim().replaceAll("%",""));
				String newOwner = ownerNames.getSelectedItem().toString(); 
				removedOwners.remove(newOwner);
				ownersMap.put(newOwner,percent);
				planetOwnersList.addItem(newOwner);
				ownerNames.removeItem(newOwner);
				if ( ownerNames.getItemCount() > 0 )
					ownerNames.setSelectedIndex(0);
			}catch (NumberFormatException nfe){
				JOptionPane.showMessageDialog(this.dialog,"Invalid Number Format Please try again");
			}
		}else if ( command.equals(removeOwnerCommand) ){
			try{
				String removedOwner = planetOwnersList.getSelectedItem().toString(); 
				removedOwners.add(removedOwner);
				ownersMap.remove(removedOwner);
				planetOwnersList.removeItem(removedOwner);
				ownerNames.addItem(removedOwner);
				currentFactionOwnerShip.setText("");
				if ( planetOwnersList.getItemCount() > 0)
					planetOwnersList.setSelectedIndex(0);
			}catch (Exception ex){
				ex.printStackTrace();
			}
			
		}else if ( command.equals(addFactoryCommand) ){
			String factoryName = newFactoryName.getText().trim();
			String factoryDesc = factoryName+"#"+factorySize.getSelectedItem().toString()+"#"+factoryOwners.getSelectedItem().toString()+"#"+factoryType.getSelectedIndex()+"#"+newFactoryBuildTable.getText().trim();
			String fullFactoryName = factorySize.getSelectedItem().toString()+ " " + factoryType.getSelectedItem().toString() + " " + newFactoryName.getText().trim() +" "+factoryOwners.getSelectedItem().toString();
			factoryMap.put(factoryName, factoryDesc);
			planetFactories.addItem(fullFactoryName);
			newFactoryName.setText("");
			newFactoryBuildTable.setText("");
			factorySize.setSelectedIndex(0);
			factoryOwners.setSelectedIndex(0);
			factoryType.setSelectedIndex(0);
		}else if ( command.equals(removeFactoryCommand) ){
			if ( planetFactories.getItemCount() < 1 )
				return;
			String factoryName = "";
			for ( UnitFactory factory : this.selectedPlanet.getUnitFactories() ){
				factoryName = factory.getSize()+ " " + factory.getFullTypeString().trim() + " " + factory.getName() +" "+factory.getFounder();
				if ( planetFactories.getSelectedItem().toString().trim().equals(factoryName) ){
					removedFactory.add(factory.getName());
					factoryMap.remove(factory.getName());
					planetFactories.removeItemAt(planetFactories.getSelectedIndex());
					break;
				}
			}
			//loadPlanetProductionData();
		}else if ( command.equals(removeAllFactoriesCommand) ){
			removedFactory.addAll(factoryMap.keySet());
			factoryMap.clear();
			planetFactories.removeAllItems();
		}else if ( command.equals(addTerrainCommand) ){
			try{
				int percent = Integer.parseInt(newTerrainPercent.getText().trim().replaceAll("%", ""));
				terrainMap.put(allTerrains.getSelectedItem().toString(),percent);
				planetTerrains.addItem(allTerrains.getSelectedItem().toString().trim());
				planetTerrains.setSelectedIndex(0);
				if ( useAdvancedTerrain ) {
					AdvancedTerrain at = new AdvancedTerrain();
					at.setDisplayName(allTerrains.getSelectedItem().toString());
					advancedTerrainMap.put(allTerrains.getSelectedItem().toString(),at);
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}else if ( command.equals(RemoveTerrainCommand) ){
			currentTerrainPercent.setText("");
			if ( planetTerrains.getItemCount() > 0 ){
				String terrainName = planetTerrains.getSelectedItem().toString().trim();
				terrainMap.remove(terrainName);
				removedTerrain.add(terrainName);
				planetTerrains.removeItemAt(planetTerrains.getSelectedIndex());
				if ( planetTerrains.getItemCount() > 0 )
					planetTerrains.setSelectedIndex(0);
				if ( useAdvancedTerrain )
					advancedTerrainMap.remove(terrainName);
			}
		}else if ( command.equals(removeAllTerrainsCommand)){
			removedTerrain.addAll(terrainMap.keySet());
			if ( useAdvancedTerrain )
				advancedTerrainMap.clear();
			terrainMap.clear();
			planetTerrains.removeAllItems();
			currentTerrainPercent.setText("");
		}else if ( command.equals(staticMapCBCommmand) || command.equals(vacuumCBCommand)) {
			updateAdvancedTerrain();
		}
	}

	private void loadAllPanels() {
		
		selectedPlanet = mwclient.getData().getPlanetByName(this.planetName);
		
		loadPlanetNames();
		loadPlanetInfo();
		loadPlanetProduction();
		loadPlanetTerrain();
		
		this.advanceTerrainId = getTerrainId();
		loadAdvancedTerrains();
		
		masterPanel.repaint();
	}
	
	private void refreshAllPanels() {
		
		loadPlanetNamesData();
		loadPlanetInfoData();
		loadPlanetProductionData();
		loadPlanetTerrainData();
		this.advanceTerrainId = getTerrainId();

		loadAdvancedTerrainsData();
	}
	
	private void loadPlanetInfo() {
		planetInfo = new JPanel();
		planetInfo.setLayout(null);

		Dimension textFieldSize = new Dimension(35,22);
		Dimension comboBoxSize = new Dimension(150,22);
		planetInfo.setLayout(new BoxLayout(planetInfo,BoxLayout.Y_AXIS));
		JPanel panel1 = new JPanel();
		panel1.add(new JLabel("Coords:",JLabel.TRAILING));

		planetXPosition.setPreferredSize(textFieldSize);
		planetXPosition.setMaximumSize(textFieldSize);
		planetXPosition.setMinimumSize(textFieldSize);
		planetXPosition.setText(Double.toString( this.selectedPlanet.getPosition().getX()));
		planetXPosition.setToolTipText("Planets X Coord");
		panel1.add(planetXPosition);
		panel1.add(new JLabel(",",JLabel.TRAILING));

		planetYPosition.setPreferredSize(textFieldSize);
		planetYPosition.setMaximumSize(textFieldSize);
		planetYPosition.setMinimumSize(textFieldSize);
		planetYPosition.setText(Double.toString(this.selectedPlanet.getPosition().getY()));
		planetYPosition.setToolTipText("Planets Y Coord");
		panel1.add(planetYPosition);

		isHomeWorldCB.setText("HomeWorld");
		isHomeWorldCB.setSelected(this.selectedPlanet.isHomeWorld());
		panel1.add(isHomeWorldCB);

		JPanel panel2 = new JPanel();
		panel2.add(new JLabel("MinOwnerShip:",JLabel.TRAILING));
		minPlanetOwnerShip.setText(Integer.toString(selectedPlanet.getMinPlanetOwnerShip()));
		panel2.add(minPlanetOwnerShip);
		minPlanetOwnerShip.setPreferredSize(textFieldSize);
		minPlanetOwnerShip.setMaximumSize(textFieldSize);
		minPlanetOwnerShip.setMinimumSize(textFieldSize);
		
		panel2.add(new JLabel("Conquer Points:",JLabel.TRAILING));
		planetConquerPoints.setPreferredSize(textFieldSize);
		planetConquerPoints.setMaximumSize(textFieldSize);
		planetConquerPoints.setMinimumSize(textFieldSize);
		planetConquerPoints.setText(Integer.toString(selectedPlanet.getConquestPoints()));
		panel2.add(planetConquerPoints);

		JPanel panel3 = new JPanel(new SpringLayout());
		panel3.add(new JLabel("Original Owner:",JLabel.TRAILING));
		houseNames = new JComboBox();
		populateHouseNames(houseNames);
		
		houseNames.setPreferredSize(comboBoxSize);
		houseNames.setMaximumSize(comboBoxSize);
		houseNames.setMinimumSize(comboBoxSize);
		houseNames.setSelectedItem(selectedPlanet.getOriginalOwner());
		panel3.add(houseNames);
		
		SpringLayoutHelper.setupSpringGrid(panel3,2);
		
		JPanel  panel4= new JPanel();
		panel4.setLayout(new BoxLayout(panel4,BoxLayout.LINE_AXIS));
		
		panel4.add(new JLabel("Owners:",JLabel.LEFT));
		
		TreeSet<String> houseList = new TreeSet<String>();
		for ( House house : this.selectedPlanet.getInfluence().getHouses() ){
			houseList.add(house.getName());
			ownersMap.put(house.getName(),this.selectedPlanet.getInfluence().getInfluence(house.getId()));
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
		this.planetInfo.setBorder(BorderFactory.createLineBorder(Color.black));
		planetInfo.repaint();
	}
	
	private void loadPlanetProduction() {
		planetProduction = new JPanel();
		Dimension textFieldSize = new Dimension(35,22);
		Dimension comboBoxSize = new Dimension(150,22);
		
		this.planetProduction.setBorder(BorderFactory.createLineBorder(Color.black));
		planetProduction.setLayout(new BoxLayout(planetProduction,BoxLayout.Y_AXIS));

		JPanel panel1 = new JPanel(new SpringLayout());
		panel1.add(new JLabel("Warehouses:",JLabel.TRAILING));

		planetBays.setText(Integer.toString(this.selectedPlanet.getBaysProvided()));
		planetBays.setName("BaysProvided");
		planetBays.addActionListener(this);
		planetBays.setPreferredSize(textFieldSize);
		planetBays.setMaximumSize(textFieldSize);
		planetBays.setMinimumSize(textFieldSize);
		panel1.add(planetBays);
		
		panel1.add(new JLabel("Production:",JLabel.TRAILING));

		planetComps.setText(Integer.toString(this.selectedPlanet.getCompProduction()));
		planetComps.setName("CompProduction");
		planetComps.addActionListener(this);
		planetComps.setPreferredSize(textFieldSize);
		planetComps.setMaximumSize(textFieldSize);
		planetComps.setMinimumSize(textFieldSize);
		panel1.add(planetComps);
		
		SpringLayoutHelper.setupSpringGrid(panel1,4);
		
		JPanel panel2 = new JPanel();
		
		panel2.add(new JLabel("Factories",JLabel.TRAILING));
		
		TreeSet<String>factoryList = new TreeSet<String>();
		for ( UnitFactory factory : this.selectedPlanet.getUnitFactories() ){
			factoryList.add(factory.getSize()+ " " + factory.getFullTypeString().trim() + " " + factory.getName() +" "+factory.getFounder());
			factoryMap.put(factory.getName(),factory.getName()+"#"+factory.getSize()+"#"+factory.getFounder()+"#"+factory.getType());
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
		panel3.add(new JLabel("Build Table",JLabel.TRAILING));
		panel3.add(newFactoryBuildTable);
		newFactoryBuildTable.setToolTipText("Factory can use a sub folder of Standard for its build tables");
		
		SpringLayoutHelper.setupSpringGrid(panel3,4);
		
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
		this.planetTerrain.setBorder(BorderFactory.createLineBorder(Color.black));
		planetTerrain.setLayout(new BoxLayout(planetTerrain,BoxLayout.Y_AXIS));
		
		JPanel panel1 = new JPanel();
		
		panel1.add(new JLabel("Terrains:",JLabel.CENTER));
		
		TreeSet<String> terrainList = new TreeSet<String>();
		Iterator<Continent> terrains = this.selectedPlanet.getEnvironments().iterator();
		while ( terrains.hasNext()  ){
			Continent terrain = terrains.next();
			terrainList.add(terrain.getEnvironment().getName());
			terrainMap.put(terrain.getEnvironment().getName(),terrain.getSize());
		}
		
		planetTerrains = new JComboBox(terrainList.toArray());
		planetTerrains.addActionListener(this);
		planetTerrains.setActionCommand(planetTerrainsCombo);
		if ( planetTerrains.getItemCount() > 0 )
			planetTerrains.setSelectedIndex(0);
		currentTerrainPercent.addKeyListener(this);
		
		panel1.add(planetTerrains);
		panel1.add(currentTerrainPercent);
		
		JPanel panel2 = new JPanel();
		allTerrains = new JComboBox();
		terrainList = new TreeSet<String>();
		for ( PlanetEnvironment terrain : mwclient.getData().getAllTerrains() ){
			if ( terrainMap.containsKey(terrain.getName()))
				continue;
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
		
		this.planetAdvancedTerrain.setBorder(BorderFactory.createLineBorder(Color.black));
		/*
		 * Format the Reward Points panel. Spring layout.
		 */
		aTerrain = mwclient.getData().getPlanetByName(planetName).getAdvancedTerrain().get(advanceTerrainId);
		planetAdvancedTerrain.setLayout(new BoxLayout(planetAdvancedTerrain,BoxLayout.Y_AXIS));
		
		JPanel textPanel = new JPanel(new SpringLayout());
		JPanel checkboxPanel = new JPanel();
		
		if ( useAdvancedTerrain ){
			
			for ( int aTerrainId : this.selectedPlanet.getAdvancedTerrain().keySet() ) {
				advancedTerrainMap.put(mwclient.getData().getTerrain(aTerrainId).getName(), this.selectedPlanet.getAdvancedTerrain().get(aTerrainId));
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
	
			textPanel.add(new JLabel("Night Chance:", SwingConstants.TRAILING));
			NightChanceText.setToolTipText("Chance for night to occur on this terrain");
			NightChanceText.addKeyListener(this);
			textPanel.add(NightChanceText);
	
			textPanel.add(new JLabel("Temp Reduction:", SwingConstants.TRAILING));
			NightTempModText.setToolTipText("<html>Calculated temp is reduced by <br> this much at night and 1/2 as much at dusk/down<br>i.e. if set to 10 and the temp is 50 the new temp with be 40</html>");
			NightTempModText.addKeyListener(this);
			textPanel.add(NightTempModText);
	
	        textPanel.add(new JLabel("Min Visibility:", SwingConstants.TRAILING));
	        MinVisibilityText.setToolTipText("The Minium Visibility this map can see");
	        MinVisibilityText.addKeyListener(this);
	        textPanel.add(MinVisibilityText);
	
	        textPanel.add(new JLabel("Max Visibility:", SwingConstants.TRAILING));
	        MaxVisibilityText.setToolTipText("<html>The Max Visibility this map can see</html>");
	        MaxVisibilityText.addKeyListener(this);
	        textPanel.add(MaxVisibilityText);
	
			//run the spring layout
			SpringLayoutHelper.setupSpringGrid(textPanel,4);
			
			
			isStaticMapCB.setText("Use Static Maps");
			isStaticMapCB.setToolTipText("Check if you want to use static maps.");
			isStaticMapCB.addActionListener(this);
			isStaticMapCB.setActionCommand(staticMapCBCommmand);
			checkboxPanel.add(isStaticMapCB);
			
			isVacuumCB.setText("Has a Vaccum");
			isVacuumCB.setToolTipText("Check if you want a vacuum for this terrain.");
			this.isVacuumCB.addActionListener(this);
			isVacuumCB.setActionCommand(vacuumCBCommand);
			checkboxPanel.add(isVacuumCB);
			
			//SpringLayoutHelper.setupSpringGrid(checkboxPanel,1,2);
		}else{
			
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
	
			//run the spring layout
			SpringLayoutHelper.setupSpringGrid(textPanel,4);
		}
		planetAdvancedTerrain.add(checkboxPanel);
		planetAdvancedTerrain.add(textPanel);

		if ( aTerrain != null && useAdvancedTerrain) {
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
	        MinVisibilityText.setText(Integer.toString(aTerrain.getMinVisibility()));
	        MaxVisibilityText.setText(Integer.toString(aTerrain.getMaxVisibility()));
			
			isStaticMapCB.setSelected(aTerrain.isStaticMap());
			isVacuumCB.setSelected(aTerrain.isVacuum());
		}else {
			LowTempText.setText(Integer.toString(this.selectedPlanet.getTemp().width));
			HighTempText.setText(Double.toString(this.selectedPlanet.getTemp().height));
			GravityText.setText(Double.toString(this.selectedPlanet.getGravity()));
			NightChanceText.setText(Integer.toString(this.selectedPlanet.getNightChance()));
			NightTempModText.setText(Integer.toString(this.selectedPlanet.getNightTempMod()));
		}
		planetAdvancedTerrain.repaint();
	}
	
	private void loadPlanetNames() {
		
		this.planets = new JPanel();
		Collection<Planet> planets = mwclient.getData().getAllPlanets();
		Dimension comboBoxSize = new Dimension(150,22);
		//setup the a list of names to feed into a list
		TreeSet<String> pNames = new TreeSet<String>();//tree to alpha sort
		for (Iterator it = planets.iterator(); it.hasNext();)
			pNames.add(((Planet)it.next()).getName());

		this.planetNames = new JComboBox(pNames.toArray());
		this.planetNames.setSelectedItem(this.planetName);
		this.planetNames.addActionListener(this);
		this.planetNames.setActionCommand(refreshCommand);
		planetNames.setPreferredSize(comboBoxSize);
		planetNames.setMaximumSize(comboBoxSize);
		planetNames.setMinimumSize(comboBoxSize);
		
		this.planets.add(new JLabel("Planet:",JLabel.TRAILING));
		this.planets.add(this.planetNames);

		this.planets.setBorder(BorderFactory.createLineBorder(Color.black));
	}
	
	private void loadPlanetInfoData() {

		planetXPosition.setText(Double.toString( this.selectedPlanet.getPosition().getX()));
		planetXPosition.setToolTipText("Planets X Coord");

		planetYPosition.setText(Double.toString(this.selectedPlanet.getPosition().getY()));
		planetYPosition.setToolTipText("Planets Y Coord");

		isHomeWorldCB.setText("HomeWorld");
		isHomeWorldCB.setSelected(this.selectedPlanet.isHomeWorld());
		ownersMap.clear();
		
		houseNames.removeAllItems();
		populateHouseNames(houseNames);
		houseNames.setSelectedItem(selectedPlanet.getOriginalOwner());
		
		TreeSet<String> houseList = new TreeSet<String>();

		for ( House house : this.selectedPlanet.getInfluence().getHouses() ){
			if ( removedOwners.contains(house.getName()) )
				continue;
				houseList.add(house.getName());
				ownersMap.put(house.getName(),this.selectedPlanet.getInfluence().getInfluence(house.getId()));
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

		planetBays.setText(Integer.toString(this.selectedPlanet.getBaysProvided()));
		planetBays.setName("BaysProvided");
		planetBays.addActionListener(this);
		
		planetComps.setText(Integer.toString(this.selectedPlanet.getCompProduction()));
		planetComps.setName("CompProduction");
		planetComps.addActionListener(this);
		
		factoryMap.clear();
		TreeSet<String>factoryList = new TreeSet<String>();
		for ( UnitFactory factory : this.selectedPlanet.getUnitFactories() ){
			if ( removedFactory.contains(factory.getName()) )
				continue;
			factoryList.add(factory.getSize()+ " " + factory.getFullTypeString().trim() + " " + factory.getName() +" "+factory.getFounder());
			factoryMap.put(factory.getName(),factory.getName()+"#"+factory.getSize()+"#"+factory.getFounder()+"#"+factory.getType());
		}
		
		planetFactories.removeAllItems();
		addAllItems(planetFactories,factoryList);
	}
	
	private void loadPlanetTerrainData() {

		TreeSet<String> terrainList = new TreeSet<String>();
		Iterator<Continent> terrains = this.selectedPlanet.getEnvironments().iterator();
		terrainMap.clear();
		while ( terrains.hasNext()  ){
			Continent terrain = terrains.next();
			if ( removedTerrain.contains(terrain.getEnvironment().getName()))
				continue;
			terrainList.add(terrain.getEnvironment().getName());
			terrainMap.put(terrain.getEnvironment().getName(),terrain.getSize());
		}
		
		planetTerrains.removeActionListener(this);
		planetTerrains.removeAllItems();
		addAllItems(planetTerrains,terrainList);
		planetTerrains.addActionListener(this);
		planetTerrains.setActionCommand(planetTerrainsCombo);
		if ( planetTerrains.getItemCount() > 0 )
			planetTerrains.setSelectedIndex(0);

		allTerrains.removeAllItems();
		terrainList = new TreeSet<String>();
		for ( PlanetEnvironment terrain : mwclient.getData().getAllTerrains() ){
			if ( terrainMap.containsKey(terrain.getName()))
				continue;
			terrainList.add(terrain.getName());
		}
		
		addAllItems(allTerrains, terrainList);

		
	}
	
	private void loadAdvancedTerrainsData() {
		

		if ( planetTerrains.getItemCount() < 1 ){
			aTerrain = null;
		}else
			aTerrain = advancedTerrainMap.get(planetTerrains.getSelectedItem().toString());

		if ( aTerrain != null) {
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
	        MinVisibilityText.setText(Integer.toString(aTerrain.getMinVisibility()));
	        MaxVisibilityText.setText(Integer.toString(aTerrain.getMaxVisibility()));
			
			isStaticMapCB.setSelected(aTerrain.isStaticMap());
			isVacuumCB.setSelected(aTerrain.isVacuum());
		}else {
			DisplayNameText.setText("");
			StaticMapNameText.setText("");
			XSizeText.setText("");
			YSizeText.setText("");
			XBoardSizeText.setText("");
			YBoardSizeText.setText("");

			LowTempText.setText(Integer.toString(this.selectedPlanet.getTemp().width));
			HighTempText.setText(Double.toString(this.selectedPlanet.getTemp().height));
			GravityText.setText(Double.toString(this.selectedPlanet.getGravity()));
			NightChanceText.setText(Integer.toString(this.selectedPlanet.getNightChance()));
			NightTempModText.setText(Integer.toString(this.selectedPlanet.getNightTempMod()));
	        MinVisibilityText.setText("");
	        MaxVisibilityText.setText("");
	        
			isStaticMapCB.setSelected(false);
			isVacuumCB.setSelected(false);
		}
	}
	
	private void loadPlanetNamesData() {
	
		//setup the a list of names to feed into a list
		this.planetNames.removeActionListener(this);
		this.planetNames.removeAllItems();
		TreeSet<String> pNames = new TreeSet<String>();//tree to alpha sort
		for (Planet planet : mwclient.getData().getAllPlanets())
			pNames.add(planet.getName());

		planetNames.removeAllItems();
		addAllItems(this.planetNames,pNames);
	
		this.planetNames.setSelectedItem(this.planetName);
		this.planetNames.addActionListener(this);
		this.planetNames.setActionCommand(refreshCommand);

	}

	private void populateHouseNames(JComboBox combo) {
		
		TreeSet<String> factionNames = new TreeSet<String>();//tree to alpha sort
		for (House house : mwclient.getData().getAllHouses()) {
            factionNames.add(house.getName());
		}

		addAllItems(combo, factionNames);
	}
	
	private void addAllItems(JComboBox combo, TreeSet<String> list){
		
		for ( String name : list )
			combo.addItem(name);
	}
	
	private int getTerrainId(){
		try{
			return mwclient.getData().getTerrainByName(this.planetTerrains.getSelectedItem().toString().trim()).getId();
		}catch(Exception ex){
			return -1;
		}
	}


	public void keyPressed(KeyEvent arg0) {
	}


	public void keyReleased(KeyEvent e) {
		if ( e.getComponent() == null )
			return;
		
		if ( e.getComponent().equals(currentFactionOwnerShip) ){
			try{
				int percent = Integer.parseInt(currentFactionOwnerShip.getText().trim().replaceAll("%", ""));
				ownersMap.put(planetOwnersList.getSelectedItem().toString(), percent);
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}else if ( e.getComponent().equals(currentTerrainPercent) ){
			try{
				int percent = Integer.parseInt(currentTerrainPercent.getText().trim().replaceAll("%", ""));
				terrainMap.put(planetTerrains.getSelectedItem().toString(), percent);
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}else if ( e.getComponent().equals(this.StaticMapNameText)
				|| e.getComponent().equals(this.DisplayNameText)
				|| e.getComponent().equals(this.XBoardSizeText)
				|| e.getComponent().equals(this.YBoardSizeText)
				|| e.getComponent().equals(this.XSizeText)
				|| e.getComponent().equals(this.YSizeText)
				|| e.getComponent().equals(this.MinVisibilityText)
				|| e.getComponent().equals(this.MaxVisibilityText)
				|| e.getComponent().equals(this.LowTempText)
				|| e.getComponent().equals(this.HighTempText)
				|| e.getComponent().equals(this.NightChanceText)
				|| e.getComponent().equals(this.NightTempModText)
				|| e.getComponent().equals(this.GravityText)
				){
			updateAdvancedTerrain();
		}
	}


	public void keyTyped(KeyEvent e) {
	}
	

	private void updateAdvancedTerrain() {
		AdvancedTerrain aTerrain = advancedTerrainMap.get(planetTerrains.getSelectedItem().toString());
		if ( aTerrain == null ){
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
		aTerrain.setMinVisibility(Integer.parseInt(MinVisibilityText.getText()));
		aTerrain.setMaxVisibility(Integer.parseInt(MaxVisibilityText.getText()));
		aTerrain.setStaticMap(isStaticMapCB.isSelected());
		aTerrain.setVacuum(isVacuumCB.isSelected());
	}
	
	private boolean saveAllData(){
		
		try{
			removeOwners();
			removeFactories();
			removeTerrain();
			
			saveOwners();
			saveFactories();
			saveTerrain();
			saveAdvancedTerrain();
			saveMisc();
			
		}catch (Exception ex){
			MWClient.mwClientLog.clientErrLog(ex);
			return false;
		}
		
		return true;
	}

	private void removeOwners(){
		
		if ( removedOwners.size() < 1 )
			return;
		
		for ( String owner : removedOwners ){
			mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c AdminRemovePlanetOwnership#"+this.planetName+"#"+owner);
		}
	}
	
	private void removeFactories(){
		mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c AdminRemoveAllFactories#"+this.planetName);
	}
	
	private void removeTerrain(){
		mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c AdminRemoveAllTerrain#"+this.planetName);
	}
	
	private void saveOwners(){
		
		for ( String owner : ownersMap.keySet() ){
			mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c AdminUpdatePlanetOwnership#"+this.planetName+"#"+owner+"#"+ownersMap.get(owner));
		}
	}
	
	private void saveFactories(){
		for ( String factory : factoryMap.keySet() ){
			mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c AdminCreateFactory#"+this.planetName+"#"+factoryMap.get(factory));
		}
		
	}
	
	private void saveTerrain(){
		for ( String terrain : terrainMap.keySet() ){
			mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c AdminCreateTerrain#"+this.planetName+"#"+terrain+"#"+terrainMap.get(terrain));
		}
		
	}

	private void saveAdvancedTerrain(){
		
		if ( useAdvancedTerrain ){
			for ( String terrain : terrainMap.keySet() ){
				AdvancedTerrain aTerrain = advancedTerrainMap.get(terrain);
				PlanetEnvironment pTerrain = mwclient.getData().getTerrainByName(terrain);
				if ( pTerrain == null ){
					MWClient.mwClientLog.clientErrLog("Unable to find Terrain "+terrain+" on planet "
							+selectedPlanet.getName());
					throw new NullPointerException();
				}
				int id = pTerrain.getId();
				
				if ( aTerrain == null ){
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
					aTerrain.setMinVisibility(Integer.parseInt(MinVisibilityText.getText()));
					aTerrain.setMaxVisibility(Integer.parseInt(MaxVisibilityText.getText()));
					aTerrain.setStaticMap(isStaticMapCB.isSelected());
					aTerrain.setVacuum(isVacuumCB.isSelected());
				}
				
				mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c SetAdvancedPlanetTerrain#"+planetName
		        +"#"+ id
		        +"#"+ (aTerrain.getDisplayName().trim().length() < 1 ? terrain : aTerrain.getDisplayName())
		    	+"#"+ aTerrain.getXSize()
		    	+"#"+ aTerrain.getYSize()
		    	+"#"+ aTerrain.isStaticMap()
		    	+"#"+ aTerrain.getXBoardSize()
		    	+"#"+ aTerrain.getYBoardSize()
		    	+"#"+ aTerrain.getLowTemp()
		    	+"#"+ aTerrain.getHighTemp()
		    	+"#"+ aTerrain.getGravity()
		    	+"#"+ aTerrain.isVacuum()
		    	+"#"+ aTerrain.getNightChance()
		    	+"#"+ aTerrain.getNightTempMod()
		    	+"#"+ aTerrain.getStaticMapName()
		        +"#"+ aTerrain.getMinVisibility()
		        +"#"+ aTerrain.getMaxVisibility());
			}
		}else{
			mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c AdminSetPlanetBoardSize#"+planetName+"#"+XBoardSizeText.getText()+"#"+YBoardSizeText.getText());
			mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c AdminSetPlanetGravity#"+planetName+"#"+GravityText.getText());
			mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c AdminSetPlanetMapSize#"+planetName+"#"+XSizeText.getText()+"#"+YSizeText.getText());
			mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c AdminSetPlanetTemperature#"+planetName+"#"+LowTempText.getText()+"#"+HighTempText.getText());
			mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c AdminSetPlanetVacuum#"+planetName+"#"+isVacuumCB.isSelected());
		}
	}
	
	private void saveMisc(){
		
		if ( !planetXPosition.getText().equals(Double.toString(this.selectedPlanet.getPosition().getX())) || !planetYPosition.getText().equals(Double.toString(this.selectedPlanet.getPosition().getY())))
			mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c AdminMovePlanet#"+planetName+"#"+planetXPosition.getText()+"#"+planetYPosition.getText());
		if ( !houseNames.getSelectedItem().toString().equals(this.selectedPlanet.getOriginalOwner()) )
			mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c AdminSetPlanetOriginalOwner#"+planetName+"#"+houseNames.getSelectedItem().toString());
		if ( !minPlanetOwnerShip.getText().equals(Integer.toString(this.selectedPlanet.getMinPlanetOwnerShip())) )
			mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c SetPlanetMinOwnerShip#"+planetName+"#"+minPlanetOwnerShip.getText());
		if ( !planetConquerPoints.getText().equals(Integer.toString(this.selectedPlanet.getConquestPoints())) )
			mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c SetPlanetConquerPoints#"+planetName+"#"+planetConquerPoints.getText());
		if ( isHomeWorldCB.isSelected() != selectedPlanet.isHomeWorld() )
			mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c Adminsethomeworld#"+isHomeWorldCB.isSelected());
		if ( !planetBays.getText().equals(Integer.toString(this.selectedPlanet.getBaysProvided())) )
			mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c Setplanetwarehouse#"+planetName+"#"+planetBays.getText());
		if ( !planetComps.getText().equals(Integer.toString(this.selectedPlanet.getCompProduction())) )
			mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c Setplanetcompproduction#"+planetName+"#"+planetComps.getText());

	}
}//end PlanetEditorDialog.java
