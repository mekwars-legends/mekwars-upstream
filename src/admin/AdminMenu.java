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
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 */

package admin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import megamek.client.ui.AWT.UnitLoadingDialog;
import megamek.common.TechConstants;

import common.Planet;
import common.PlanetEnvironment;
import common.UnitFactory;

import client.MWClient;
import client.gui.dialog.HouseNameDialog;
import client.gui.dialog.PlanetNameDialog;
import client.gui.dialog.TraitDialog;
import client.gui.dialog.UnitViewerDialog;

import admin.dialog.AmmoCostDialog;
import admin.dialog.BanTargetingDialog;
import admin.dialog.CommandNameDialog;
import admin.dialog.FactionConfigurationDialog;
import admin.dialog.PlanetEditorDialog;
import admin.dialog.ServerConfigurationDialog;
import admin.dialog.BannedAmmoDialog;
import admin.dialog.ComponentDisplayDialog;

public class AdminMenu extends JMenu {
	
	/**
     * 
     */
    private static final long serialVersionUID = -4734543796361026030L;
    /**
     * 
     */
    //admin menu components	
	JMenu jMenuAdminSubSave = new JMenu();//sub menus
	JMenu jMenuAdminSubSet = new JMenu();
	JMenu jMenuAdminSubCreate = new JMenu();
	JMenu jMenuAdminSubDestroy = new JMenu();
    JMenu jMenuAdminBlackMarketSettings = new JMenu("Black Market Settings");
	JMenu jMenuAdminOperations = new JMenu("Operations");
    
	JMenuItem jMenuAdminServerConfig = new JMenuItem();
	JMenuItem jMenuAdminFactionConfig = new JMenuItem();
	JMenuItem jMenuAdminTerminateAll = new JMenuItem();
	JMenuItem jMenuAdminCreatePlanet = new JMenuItem();
	JMenuItem jMenuAdminDestroyPlanet = new JMenuItem();
	JMenuItem jMenuAdminCreateFactory = new JMenuItem();
	JMenuItem jMenuAdminDestroyFactory = new JMenuItem();
	JMenuItem jMenuAdminCreateTerrain = new JMenuItem();
	JMenuItem jMenuAdminDestroyTerrain = new JMenuItem();
	JMenuItem jMenuAdminChangePlanetOwner = new JMenuItem();
	JMenuItem jMenuAdminHouseAmmoBan = new JMenuItem();
	JMenuItem jMenuAdminSetHouseFluFile = new JMenuItem();
	JMenuItem jMenuAdminSetHouseTechLevel = new JMenuItem();
	JMenuItem jMenuAdminSetFactionTraits = new JMenuItem();
	JMenuItem jMenuAdminSaveTheUniverse = new JMenuItem();
	JMenuItem jMenuAdminSaveBlackMaketSettings = new JMenuItem();
	JMenuItem jMenuAdminSavePlanetsToXML = new JMenuItem();
	JMenuItem jMenuAdminSaveServerConfigs = new JMenuItem();
	JMenuItem jMenuAdminSaveCommandLevels = new JMenuItem();
	JMenuItem jMenuAdminGrantComponents = new JMenuItem();
	JMenuItem jMenuAdminExchangePlanetOwnership = new JMenuItem();
	JMenuItem jMenuAdminLockFactory = new JMenuItem();
	JMenuItem jMenuAdminSetPlanetMapSize = new JMenuItem();
	JMenuItem jMenuAdminSetPlanetBoardSize = new JMenuItem();
	JMenuItem jMenuAdminSetPlanetTemperature = new JMenuItem();
	JMenuItem jMenuAdminSetPlanetGravity = new JMenuItem();
	JMenuItem jMenuAdminSetPlanetVacuum = new JMenuItem();
    JMenuItem jMenuAdminSetPlanetHomeWorld = new JMenuItem();
    JMenuItem jMenuAdminSetPlanetOriginalOwner= new JMenuItem();
	JMenuItem jMenuAdminSetServerAmmoBan = new JMenuItem();
    JMenuItem jMenuAdminSetBanTargeting = new JMenuItem();
	JMenuItem jMenuAdminSetCommandLevel = new JMenuItem();
    JMenuItem jMenuAdminSetMegaMekGameOptions = new JMenuItem();
    JMenuItem jMenuAdminSetAmmoCost = new JMenuItem();
    JMenuItem jMenuAdminRetrieveOperationFile = new JMenuItem();
    JMenuItem jMenuAdminSetOperationFile = new JMenuItem();
    JMenuItem jMenuAdminSetNewOperationFile = new JMenuItem();
    JMenuItem jMenuAdminSendAllOperationFiles = new JMenuItem();
    JMenuItem jMenuAdminUpdateOperations= new JMenuItem();
	JMenuItem jMenuAdminRemoveOMG = new JMenuItem();
	JMenuItem jMenuAdminOmniVariantMod = new JMenuItem();
	JMenuItem jMenuAdminCommandLists = new JMenuItem();
    JMenuItem jMenuAdminComponentMiscList = new JMenuItem();
    JMenuItem jMenuAdminComponentWeaponList = new JMenuItem();
    JMenuItem jMenuAdminComponentAmmoList = new JMenuItem();
    JMenuItem jMenuAdminSetHouseBasePilotSkill = new JMenuItem();
    
	MWClient mwclient;
	private int userLevel = 0;

	//constructor
	 public AdminMenu() {
	 	super("Server Configs");
	 }
	 
	 public void createMenu(MWClient client) {
	 	
	 	this.mwclient = client;
	 	
	 	userLevel = this.mwclient.getUser(this.mwclient.getUsername()).getUserlevel();
		/*
	 	 * Code to create the actual menu, add
	 	 * action listeners, etc. This is extracted
	 	 * from CMainFrame and could be improved
	 	 * dramatically ...
	 	 */
        jMenuAdminSubSave.setText("Save");
        jMenuAdminSubSet.setText("Set");
        jMenuAdminSubCreate.setText("Create");
        jMenuAdminSubDestroy.setText("Destroy");

        jMenuAdminCreatePlanet.setText("Create Planet");
        jMenuAdminCreatePlanet.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuAdminCreatePlanet_actionPerformed(e);
            }
        });

        jMenuAdminDestroyPlanet.setText("Destroy Planet");
        jMenuAdminDestroyPlanet.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuAdminDestroyPlanet_actionPerformed(e);
            }
        });
        jMenuAdminCreateFactory.setText("Create Factory");
        jMenuAdminCreateFactory.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuAdminCreateFactory_actionPerformed(e);
            }
        });
        jMenuAdminDestroyFactory.setText("Destroy Factory");
        jMenuAdminDestroyFactory.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuAdminDestroyFactory_actionPerformed(e);
            }
        });

        jMenuAdminCreateTerrain.setText("Create Terrain");
        jMenuAdminCreateTerrain.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuAdminCreateTerrain_actionPerformed(e);
            }
        });

        jMenuAdminDestroyTerrain.setText("Destroy Terrain");
        jMenuAdminDestroyTerrain.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuAdminDestroyTerrain_actionPerformed(e);
            }
        });

        jMenuAdminHouseAmmoBan.setText("Set Banned Ammo");
        jMenuAdminHouseAmmoBan.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuAdminHouseAmmoBan_actionPerformed(e);
            }
        });

        jMenuAdminChangePlanetOwner.setText("Change Planet Owner");
        jMenuAdminChangePlanetOwner.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuAdminChangePlanetOwner_actionPerformed(e);
            }
        });

        jMenuAdminServerConfig.setText("Server Configuration");
        jMenuAdminServerConfig.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	mwclient.getServerConfigData();
            	new ServerConfigurationDialog(mwclient);
            }
        });
        
        jMenuAdminFactionConfig.setText("Faction Configuration");
        jMenuAdminFactionConfig.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
    	        HouseNameDialog factionDialog = new HouseNameDialog(mwclient, "Faction", false,false);
    	        factionDialog.setVisible(true);
    	        String faction = factionDialog.getHouseName();
    	        factionDialog.dispose();
    	        if (faction == null || faction.length() == 0)
    	            return;

    	        mwclient.setWaiting(true);
    	        mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c GetFactionConfigs#-1#"+faction);
    	        
    	        try {
    	        	while ( mwclient.isWaiting() ){
    	        		Thread.sleep(10);
    	        		//MWClient.mwClientLog.clientErrLog("Waiting for faction config");
    	        	}
    	        }catch (Exception ex) {
    	        	ex.printStackTrace();
    	        }
    	        
            	new FactionConfigurationDialog(mwclient,faction);
            }
        });
        
        jMenuAdminTerminateAll.setText("Terminate All Games");
        jMenuAdminTerminateAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	 jMenuAdminTerminateAll_actionPerformed(e);
            }
        });

        jMenuAdminSetFactionTraits.setText("Faction Traits");
        jMenuAdminSetFactionTraits.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	new TraitDialog(mwclient,false);
            }
        });
        
        jMenuAdminSetHouseFluFile.setText("Set House Flu File");
        jMenuAdminSetHouseFluFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuAdminSetHouseFluFile_actionPerformed(e);
            }
        });
        
        jMenuAdminSetHouseTechLevel.setText("Set House Tech Level");
        jMenuAdminSetHouseTechLevel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuAdminSetHouseTechLevel_actionPerformed(e);
            }
        });

        jMenuAdminSaveTheUniverse.setText("Save The Universe");
        jMenuAdminSaveTheUniverse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuAdminSaveTheUniverse_actionPerformed(e);
            }
        });

        jMenuAdminSaveBlackMaketSettings.setText("Save Black Market Settings");
        jMenuAdminSaveBlackMaketSettings.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuAdminSaveBlackMaketSettings_actionPerformed(e);
            }
        });

        jMenuAdminSavePlanetsToXML.setText("Save Planets to XML");
        jMenuAdminSavePlanetsToXML.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuAdminSavePlanetsToXML_actionPerformed(e);
            }
        });

        jMenuAdminRemoveOMG.setText("List and Remove OMG Units");
        jMenuAdminRemoveOMG.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c adminlistandremoveomg");
            }
        });

        jMenuAdminGrantComponents.setText("Grant Components");
        jMenuAdminGrantComponents.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuAdminGrantComponents_actionPerformed(e);
            }
        });

        jMenuAdminExchangePlanetOwnership.setText("Exchange Planet Ownership");
        jMenuAdminExchangePlanetOwnership
                .addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        jMenuAdminExchangePlanetOwnership_actionPerformed(e);
                    }
                });

        jMenuAdminLockFactory.setText("Lock Factory");
        jMenuAdminLockFactory.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuAdminLockFactory_actionPerformed(e);
            }
        });

        jMenuAdminSaveServerConfigs.setText("Save Server Configuration");
        jMenuAdminSaveServerConfigs.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c AdminSaveServerConfigs");
                mwclient.reloadData();
            }
        });

        jMenuAdminSaveCommandLevels.setText("Save Command Levels");
        jMenuAdminSaveCommandLevels.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c AdminSaveCommandLevels");
            }
        });

        jMenuAdminSetPlanetMapSize.setText("Set Planet Map Size");
        jMenuAdminSetPlanetMapSize.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuAdminSetPlanetMapSize_actionPerformed(e);
            }
        });

        jMenuAdminSetPlanetBoardSize.setText("Set Planet Board Size");
        jMenuAdminSetPlanetBoardSize.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuAdminSetPlanetBoardSize_actionPerformed(e);
            }
        });

        jMenuAdminSetPlanetTemperature.setText("Set Planet Temperature");
        jMenuAdminSetPlanetTemperature.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuAdminSetPlanetTemperature_actionPerformed(e);
            }
        });

        jMenuAdminSetPlanetGravity.setText("Set Planet Gravity");
        jMenuAdminSetPlanetGravity.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuAdminSetPlanetGravity_actionPerformed(e);
            }
        });

        jMenuAdminSetPlanetVacuum.setText("Set Planet Vacuum");
        jMenuAdminSetPlanetVacuum.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c AdminSetPlanetVacuum");
                mwclient.reloadData();
            }
        });

        jMenuAdminSetPlanetHomeWorld.setText("Set Planet Home World");
        jMenuAdminSetPlanetHomeWorld.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuAdminSetPlanetHomeWorld_actionPerformed(e);
            }
        });

        jMenuAdminSetPlanetOriginalOwner.setText("Set Planet Original Owner");
        jMenuAdminSetPlanetOriginalOwner.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuAdminSetPlanetOriginalOwner_actionPerformed(e);
            }
        });

        jMenuAdminSetServerAmmoBan.setText("Set Server Ammo Ban");
        jMenuAdminSetServerAmmoBan.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuAdminServerAmmoBan_actionPerformed(e);
            }
        });

        jMenuAdminSetBanTargeting.setText("Set Ban Targeting Systems");
        jMenuAdminSetBanTargeting.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuAdminSetBanTargeting_actionPerformed(e);
            }
        });

        jMenuAdminRetrieveOperationFile.setText("Retrieve Operation File");
        jMenuAdminRetrieveOperationFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuAdminRetrieveOperationFile_actionPerformed(e);
            }
        });

        jMenuAdminSetOperationFile.setText("Set Operation File");
        jMenuAdminSetOperationFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuAdminSetOperationFile_actionPerformed(e);
            }
        });

        jMenuAdminSetNewOperationFile.setText("Set New Operation File");
        jMenuAdminSetNewOperationFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuAdminSetNewOperationFile_actionPerformed(e);
            }
        });

        jMenuAdminSendAllOperationFiles.setText("Send All Local Op Files");
        jMenuAdminSendAllOperationFiles.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuAdminSendAllOperationFiles_actionPerformed(e);
            }
        });

        jMenuAdminUpdateOperations.setText("Update Operations");
        jMenuAdminUpdateOperations.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuAdminUpdateOperations_actionPerformed(e);
            }
        });

        jMenuAdminSetAmmoCost.setText("Set Ammo Cost");
        jMenuAdminSetAmmoCost.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuAdminAmmoCost_actionPerformed(e);
            }
        });

        jMenuAdminOmniVariantMod.setText("Set Omni Variant Mod");
        jMenuAdminOmniVariantMod.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuAdminOmniVariantMod_actionPerformed(e);
            }
        });

        jMenuAdminSetCommandLevel.setText("Set Command Level");
        jMenuAdminSetCommandLevel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuAdminSetCommandLevel_actionPerformed(e);
            }
        });

        jMenuAdminSetHouseBasePilotSkill.setText("Set House Base Pilot Skills");
        jMenuAdminSetHouseBasePilotSkill.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuAdminSetHouseBasePilotSkills_actionPerformed(e);
            }
        });

        jMenuAdminCommandLists.setText("List Commands");
        jMenuAdminCommandLists.setMnemonic('L');
        jMenuAdminCommandLists.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuAdminCommandLists_actionPerformed(e);
            }
        });

        jMenuAdminComponentWeaponList.setText("List Weapon Components");
        jMenuAdminComponentWeaponList.setMnemonic('W');
        jMenuAdminComponentWeaponList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                e.setSource(0);
                jMenuAdminComponentList_actionPerformed(e);
            }
        });

        jMenuAdminComponentAmmoList.setText("List Ammo Components");
        jMenuAdminComponentAmmoList.setMnemonic('A');
        jMenuAdminComponentAmmoList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                e.setSource(2);
                jMenuAdminComponentList_actionPerformed(e);
            }
        });

        jMenuAdminComponentMiscList.setText("List Misc Components");
        jMenuAdminComponentMiscList.setMnemonic('M');
        jMenuAdminComponentMiscList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                e.setSource(1);
                jMenuAdminComponentList_actionPerformed(e);
            }
        });

        jMenuAdminSetMegaMekGameOptions.setText("Set MegaMek Game Options");
        jMenuAdminSetMegaMekGameOptions.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mwclient.loadMegaMekClient();
            }
        });

    //clear the entire menu, incase this is a reconstruction call
        this.removeAll();
        
        //then, set the menu up
        if (userLevel >= mwclient.getData().getAccessLevel("AdminChangeServerConfig"))
             this.add(jMenuAdminServerConfig);
        
        if (userLevel >= mwclient.getData().getAccessLevel("AdminChangeFactionConfig"))
            this.add(jMenuAdminFactionConfig);

        if (userLevel >= mwclient.getData().getAccessLevel("AdminTerminateAll"))
        	 this.add(jMenuAdminTerminateAll);
        
        if (this.getItemCount() > 0)
        	this.addSeparator();
        
        if ( userLevel >= mwclient.getData().getAccessLevel("AdminCreatePlanet") )
            jMenuAdminSubCreate.add(jMenuAdminCreatePlanet);
        if ( userLevel >= mwclient.getData().getAccessLevel("AdminCreateFactory") )
            jMenuAdminSubCreate.add(jMenuAdminCreateFactory);
        if ( userLevel >= mwclient.getData().getAccessLevel("AdminCreateTerrain") )
            jMenuAdminSubCreate.add(jMenuAdminCreateTerrain);
        if ( jMenuAdminSubCreate.getItemCount() > 0)
            this.add(jMenuAdminSubCreate);
        
        if ( userLevel >= mwclient.getData().getAccessLevel("AdminDestroyPlanet") )
            jMenuAdminSubDestroy.add(jMenuAdminDestroyPlanet);
        if ( userLevel >= mwclient.getData().getAccessLevel("AdminDestroyFactory") )
            jMenuAdminSubDestroy.add(jMenuAdminDestroyFactory);
        if ( userLevel >= mwclient.getData().getAccessLevel("AdminDestroyTerrain") )
            jMenuAdminSubDestroy.add(jMenuAdminDestroyTerrain);
        if ( userLevel >= mwclient.getData().getAccessLevel("AdminListAndRemoveOMG") )
            jMenuAdminSubDestroy.add(jMenuAdminRemoveOMG);
        if ( jMenuAdminSubDestroy.getItemCount() > 0)
            this.add(jMenuAdminSubDestroy);

        JMenu jMenuAdminSubSetHouse = new JMenu();
        JMenu jMenuAdminSubSetPlanet = new JMenu();

        if ( userLevel >= mwclient.getData().getAccessLevel("AdminChangePlanetOwner") )
            jMenuAdminSubSetHouse.add(jMenuAdminChangePlanetOwner);
        if ( userLevel >= mwclient.getData().getAccessLevel("AdminSetHouseFluFile") )
            jMenuAdminSubSetHouse.add(jMenuAdminSetHouseFluFile);
        if ( userLevel >= mwclient.getData().getAccessLevel("AdminSsetHouseTechLevel") )
            jMenuAdminSubSetHouse.add(jMenuAdminSetHouseTechLevel);
        if ( userLevel >= mwclient.getData().getAccessLevel("AdminGrantComponents") )
            jMenuAdminSubSetHouse.add(jMenuAdminGrantComponents);
        if ( userLevel >= mwclient.getData().getAccessLevel("AdminExchangePlanetOwnership") )
            jMenuAdminSubSetHouse.add(jMenuAdminExchangePlanetOwnership);
        if ( userLevel >= mwclient.getData().getAccessLevel("AdminSetHouseAmmoBan") )
            jMenuAdminSubSetHouse.add(jMenuAdminHouseAmmoBan);
        if ( userLevel >= mwclient.getData().getAccessLevel("AddTrait") )
            jMenuAdminSubSetHouse.add(jMenuAdminSetFactionTraits);

        jMenuAdminSubSetHouse.setText("Factions");
        if ( jMenuAdminSubSetHouse.getItemCount() > 0)
            jMenuAdminSubSet.add(jMenuAdminSubSetHouse);

        if ( userLevel >= mwclient.getData().getAccessLevel("AdminLockFactory") )
            jMenuAdminSubSetPlanet.add(jMenuAdminLockFactory);
        if ( userLevel >= mwclient.getData().getAccessLevel("AdminSetPlanetMapSize") )
            jMenuAdminSubSetPlanet.add(jMenuAdminSetPlanetMapSize);
        if ( userLevel >= mwclient.getData().getAccessLevel("AdminSetPlanetBoardSize") )
            jMenuAdminSubSetPlanet.add(jMenuAdminSetPlanetBoardSize);
        if ( userLevel >= mwclient.getData().getAccessLevel("AdminSetPlanetTemperature") )
            jMenuAdminSubSetPlanet.add(jMenuAdminSetPlanetTemperature);
        if ( userLevel >= mwclient.getData().getAccessLevel("AdminSetPlanetGravity") )
            jMenuAdminSubSetPlanet.add(jMenuAdminSetPlanetGravity);
        if ( userLevel >= mwclient.getData().getAccessLevel("AdminSetPlanetVacuum") )
            jMenuAdminSubSetPlanet.add(jMenuAdminSetPlanetVacuum);
        if ( userLevel >= mwclient.getData().getAccessLevel("AdminSetHomeWorld") )
            jMenuAdminSubSetPlanet.add(jMenuAdminSetPlanetHomeWorld);
        if ( userLevel >= mwclient.getData().getAccessLevel("AdminSetPlanetOriginalOwner") )
            jMenuAdminSubSetPlanet.add(jMenuAdminSetPlanetOriginalOwner);

        jMenuAdminSubSetPlanet.setText("Planets");
        if ( jMenuAdminSubSetPlanet.getItemCount() > 0)
            jMenuAdminSubSet.add(jMenuAdminSubSetPlanet);

        if ( userLevel >= mwclient.getData().getAccessLevel("AdminSetServerAmmoBan") )
            jMenuAdminSubSet.add(jMenuAdminSetServerAmmoBan);
        if ( userLevel >= mwclient.getData().getAccessLevel("AdminSetBanTargeting") )
            jMenuAdminSubSet.add(jMenuAdminSetBanTargeting);
        if ( userLevel >= mwclient.getData().getAccessLevel("AdminSetCommandLevel") )
            jMenuAdminSubSet.add(jMenuAdminSetCommandLevel);
        if ( userLevel >= mwclient.getData().getAccessLevel("AddOmniVariantMod") )
            jMenuAdminSubSet.add(jMenuAdminOmniVariantMod);
        if ( userLevel >= mwclient.getData().getAccessLevel("AdminSetAmmoCost") )
            jMenuAdminSubSet.add(jMenuAdminSetAmmoCost);
        if ( userLevel >= mwclient.getData().getAccessLevel("SetHouseBasePilotSkills") )
            jMenuAdminSubSet.add(jMenuAdminSetHouseBasePilotSkill);
        if ( userLevel >= 200 ){
            jMenuAdminSubSet.addSeparator();
            jMenuAdminSubSet.add(jMenuAdminSetMegaMekGameOptions);
        }
       
        if ( jMenuAdminSubSet.getItemCount() > 0)
            this.add(jMenuAdminSubSet);

        if ( userLevel >= mwclient.getData().getAccessLevel("RetrieveOperation") )
            jMenuAdminOperations.add(jMenuAdminRetrieveOperationFile);
        if ( userLevel >= mwclient.getData().getAccessLevel("SetOperation") ){
            jMenuAdminOperations.add(jMenuAdminSetOperationFile);
            jMenuAdminOperations.add(jMenuAdminSetNewOperationFile);
            jMenuAdminOperations.add(jMenuAdminSendAllOperationFiles);
        }
        if ( userLevel >= mwclient.getData().getAccessLevel("UpdateOperations") )
            jMenuAdminOperations.add(jMenuAdminUpdateOperations);

        if ( jMenuAdminOperations.getItemCount() > 0)
            this.add(jMenuAdminOperations);

        if ( userLevel >= mwclient.getData().getAccessLevel("AdminSave") )
            jMenuAdminSubSave.add(jMenuAdminSaveTheUniverse);
        if ( userLevel >= mwclient.getData().getAccessLevel("AdminSavePlanetsToXML") )
            jMenuAdminSubSave.add(jMenuAdminSavePlanetsToXML);
        if ( userLevel >= mwclient.getData().getAccessLevel("AdminSaveServerConfigs") )
            jMenuAdminSubSave.add(jMenuAdminSaveServerConfigs);
        if ( userLevel >= mwclient.getData().getAccessLevel("AdminSaveCommandLevels") )
            jMenuAdminSubSave.add(jMenuAdminSaveCommandLevels);
        if ( userLevel >= mwclient.getData().getAccessLevel("AdminSaveBlackMarketConfigs") )
            jMenuAdminSubSave.add(jMenuAdminSaveBlackMaketSettings);
        if ( jMenuAdminSubSave.getItemCount() > 0){
            this.add(jMenuAdminSubSave);
            this.addSeparator();
        }

        if ( userLevel >= 101 ) //arbitray level don't want normal mods to see it
            this.add(jMenuAdminCommandLists);

        if ( userLevel >= mwclient.getData().getAccessLevel("AdminSetBlackMarketSetting") ){
        	jMenuAdminBlackMarketSettings.add(jMenuAdminComponentWeaponList);
        	jMenuAdminBlackMarketSettings.add(jMenuAdminComponentAmmoList);
        	jMenuAdminBlackMarketSettings.add(jMenuAdminComponentMiscList);
            this.add(jMenuAdminBlackMarketSettings);
        }
	 }//end CreateMenu();
	 
	 /*
	  * Various admin methods. These really don't need to be stand alone, and
	  * could (should?) be worked back into a unified ActionPerformed command, 
	  * or back into the overloaded actionPerformed commands is createMenu();
	  * 
	  * For now, these methods remain as they were in CMainFrame.
	  * 
	  * @urgru, 6.26.05
	  */

	    public void jMenuAdminCreatePlanet_actionPerformed(ActionEvent e) {
	        String planetName = JOptionPane.showInputDialog(null, "Planet Name?");
	        if (planetName == null || planetName.length() == 0)
	            return;

	        String xcord = JOptionPane.showInputDialog(null, "Planet x coord");
	        if (xcord == null || xcord.length() == 0)
	            return;

	        String ycord = JOptionPane.showInputDialog(null, "Planet y coord?");
	        if (ycord == null || ycord.length() == 0)
	            return;

	        mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c admincreateplanet#"
	        		+ planetName + "#" + xcord + "#" + ycord + "#");
	        mwclient.reloadData();
			new PlanetEditorDialog(mwclient, planetName);

	    }

	    public void jMenuAdminDestroyPlanet_actionPerformed(ActionEvent e) {

	        PlanetNameDialog planetDialog = new PlanetNameDialog(mwclient, "Select a Planet", null);
	        planetDialog.setVisible(true);
	        String planetNamestr = planetDialog.getPlanetName();
	        planetDialog.dispose();

	        if (planetNamestr == null || planetNamestr.length() == 0)
	            return;
	        mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c admindestroyplanet#" + planetNamestr);
	        mwclient.reloadData();
	    }

	    public void jMenuAdminCreateFactory_actionPerformed(ActionEvent e) {
	        Object[] factoryTypes = { "All", "Mek", "Vehicles", "Mek & Vehicles",
	                "Infantry", "Mek & Infantry", "Vehicles & Infantry",
	                "Mek & Infantry & Vehicles", "ProtoMeks", "Mek & ProtoMeks",
	                "Vehicles & ProtoMek", "Mek & Vehicles & ProtoMek",
	                "Infantry & ProtoMek", "Mek & Infantry & ProtoMek",
	                "Vehicles & Infantry & ProtoMek",
	                "Mek & Vehicles & Infantry & ProtoMek", "BattleArmor",
	                "Mek & BattleArmor", "Vehicles & BattleArmor",
	                "Mek & Vehicles & BattleArmor", "Infantry & BattleArmor",
	                "Mek & Infantry & BattleArmor",
	                "Vehicles & Infantry & BattleArmor",
	                "Mek & Vehicles & Infantry & BattleArmor",
	                "ProtoMeks & BattleArmor", "Mek & ProtoMeks & BattleArmor",
	                "Vehicles & ProtoMek & BattleArmor",
	                "Mek & Vehicles & ProtoMek & BattleArmor",
	                "Infantry & ProtoMek & BattleArmor",
	                "Mek & Infantry & ProtoMek & BattleArmor",
	                "Vehicles & Infantry & ProtoMek & BattleArmor" };

	        Object[] factorySize = { "Light", "Medium", "Heavy", "Assault" };
	        int i;

	        PlanetNameDialog planetDialog = new PlanetNameDialog(mwclient, "Select a Planet", null);
	        planetDialog.setVisible(true);
	        String planetNamestr = planetDialog.getPlanetName();
	        planetDialog.dispose();

	        if (planetNamestr == null || planetNamestr.length() == 0)
	            return;

	        String factoryName = JOptionPane.showInputDialog(null, "Factory Name");

	        if (factoryName == null || factoryName.length() == 0)
	            return;

	        HouseNameDialog factionDialog = new HouseNameDialog(mwclient, "Faction", false,false);
	        factionDialog.setVisible(true);
	        String factionName = factionDialog.getHouseName();
	        factionDialog.dispose();

	        if (factionName == null || factionName.length() == 0)
	            return;

	        String factoryTypestr = (String) JOptionPane.showInputDialog(null,
	                "Select factory production", "Factory Production",
	                JOptionPane.INFORMATION_MESSAGE, null, factoryTypes,
	                factoryTypes[0]);

	        if (factoryTypestr == null || factoryTypestr.length() == 0)
	            return;

	        for (i = 0; i < factoryTypes.length; i++)
	            if (factoryTypestr.equals(factoryTypes[i]))
	                break;

	        int factoryTypeint = i;

	        String factorySizestr = (String) JOptionPane.showInputDialog(null,
	                "Select a factory size", "FactorySize",
	                JOptionPane.INFORMATION_MESSAGE, null, factorySize,
	                factorySize[0]);

	        if (factorySizestr == null || factorySizestr.length() == 0)
	            return;

	        StringBuilder sendCommand = new StringBuilder();
	        sendCommand.append(planetNamestr.trim() + "#" + factoryName.trim()
	                + "#" + factorySizestr.trim() + "#" + factionName.trim() + "#"
	                + factoryTypeint);

	        mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c admincreatefactory#" + sendCommand.toString());
	        mwclient.reloadData();

	    }

	    public void jMenuAdminDestroyFactory_actionPerformed(ActionEvent e) {
	        TreeSet<String> names = new TreeSet<String>();

	        PlanetNameDialog planetDialog = new PlanetNameDialog(mwclient, "Select a Planet", null);
	        planetDialog.setVisible(true);
	        String planetNamestr = planetDialog.getPlanetName();
	        planetDialog.dispose();

	        if (planetNamestr == null || planetNamestr.length() == 0)
	            return;

	        Planet planet = mwclient.getData().getPlanetByName(planetNamestr);

	        names.clear();

	        for (Iterator UF = planet.getUnitFactories().iterator(); UF.hasNext();)
	            names.add(((UnitFactory) UF.next()).getName());

	        JComboBox combo = new JComboBox(names.toArray());
	        combo.setEditable(true);
	        JOptionPane jop = new JOptionPane(combo, JOptionPane.QUESTION_MESSAGE,JOptionPane.OK_CANCEL_OPTION);

	        JDialog dlg = jop.createDialog(null, "Select factory to destroy.");
	        combo.grabFocus();
	        combo.getEditor().selectAll();

	        dlg.setVisible(true);

	        String factoryName = (String) combo.getSelectedItem();

	        if (factoryName == null || factoryName.length() == 0)
	            return;

	        mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c admindestroyfactory#" + planetNamestr + "#" + factoryName);
	        mwclient.reloadData();

	    }

	    public void jMenuAdminCreateTerrain_actionPerformed(ActionEvent e) {
	        TreeSet<String> names = new TreeSet<String>();
	        PlanetNameDialog planetDialog = new PlanetNameDialog(mwclient, "Select a Planet", null);
	        planetDialog.setVisible(true);
	        String planetNamestr = planetDialog.getPlanetName();
	        planetDialog.dispose();

	        if (planetNamestr == null || planetNamestr.length() == 0)
	            return;

	        for (Iterator it = mwclient.getData().getAllTerrains().iterator(); it.hasNext();)
	            names.add(((PlanetEnvironment) it.next()).getName());

	        /*
	         * String terrainType = JOptionPane.showInputDialog(null,"Terrain
	         * Type");
	         */

	        JComboBox combo = new JComboBox(names.toArray());
	        combo.setEditable(false);
	        JOptionPane jop = new JOptionPane(combo, JOptionPane.QUESTION_MESSAGE,JOptionPane.OK_CANCEL_OPTION);

	        JDialog dlg = jop.createDialog(null, "Select a Terrain Type.");
	        combo.grabFocus();
	        combo.getEditor().selectAll();

	        dlg.setVisible(true);
	        String terrainType = (String) combo.getSelectedItem();

	        int value = ((Integer) jop.getValue()).intValue();

	        if (value == JOptionPane.CANCEL_OPTION)
	            return;

	        if (terrainType == null || terrainType.length() == 0)
	            return;

	        String terrainChance = JOptionPane.showInputDialog(null,"Terrain Chance", new Integer(100));

	        if (terrainChance == null || terrainChance.length() == 0)
	            return;

	        mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c admincreateterrain#" + planetNamestr + "#" + terrainType + "#" + terrainChance);
	        mwclient.reloadData();

	    }

	    public void jMenuAdminDestroyTerrain_actionPerformed(ActionEvent e) {

	        PlanetNameDialog planetDialog = new PlanetNameDialog(mwclient, "Select a Planet", null);
	        planetDialog.setVisible(true);
	        String planetNamestr = planetDialog.getPlanetName();
	        planetDialog.dispose();

	        if (planetNamestr == null || planetNamestr.length() == 0)
	            return;

	        String terrainType = JOptionPane .showInputDialog(
	                        null,
	                        "Select the Terrain position: start with 0 for the top most terrain in the information box");

	        if (terrainType == null || terrainType.length() == 0)
	            return;

	        mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c admindestroyterrain#" + planetNamestr + "#" + terrainType);
	        mwclient.reloadData();

	    }

	    public void jMenuAdminChangePlanetOwner_actionPerformed(ActionEvent e) {
	        PlanetNameDialog planetDialog = new PlanetNameDialog(mwclient, "Select a Planet", null);
	        planetDialog.setVisible(true);
	        String planetNamestr = planetDialog.getPlanetName();
	        planetDialog.dispose();

	        if (planetNamestr == null || planetNamestr.length() == 0)
	            return;

	        HouseNameDialog factionDialog = new HouseNameDialog(mwclient, "New Owner", false,false);
	        factionDialog.setVisible(true);
	        String newOwner = factionDialog.getHouseName();
	        factionDialog.dispose();

	        if (newOwner == null || newOwner.length() == 0)
	            return;

	        mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c adminchangeplanetowner#" + planetNamestr + "#" + newOwner);
	        mwclient.reloadData();

	    }
	    
	    public void jMenuAdminTerminateAll_actionPerformed(ActionEvent e) {
	    	int confirm = JOptionPane.showConfirmDialog(null,"Are you sure you want to terminate all waiting/running games?");
	    	if (confirm != JOptionPane.YES_OPTION)
	    		return;
	    	mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c adminterminateall");
	    }

	    public void jMenuAdminSetHouseFluFile_actionPerformed(ActionEvent e) {

	        String factionName = JOptionPane.showInputDialog(null, "House Name:");

	        if (factionName == null || factionName.length() == 0)
	            return;

	        String fluFilePrefix = JOptionPane.showInputDialog(null, mwclient
	                .moneyOrFluMessage(false,true,-1)
	                + " File Prefix:");

	        if (fluFilePrefix == null || fluFilePrefix.length() == 0)
	            return;

	        mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c adminsethouseflufile#" + factionName + "#" + fluFilePrefix);
	    }

	    public void jMenuAdminGrantComponents_actionPerformed(ActionEvent e) {
	        Object[] Types = { "Mek", "Vehicles", "Infantry", "ProtoMek", "BattleArmor" };
	        Object[] Size = { "Light", "Medium", "Heavy", "Assault" };

	        HouseNameDialog factionDialog = new HouseNameDialog(mwclient, "Faction", false,false);
	        factionDialog.setVisible(true);
	        String factionName = factionDialog.getHouseName();
	        factionDialog.dispose();

	        if (factionName == null || factionName.length() == 0)
	            return;

	        String Typestr = (String) JOptionPane.showInputDialog(null,
	                "Select component type", "Component Type",
	                JOptionPane.INFORMATION_MESSAGE, null, Types, Types[0]);

	        if (Typestr == null || Typestr.length() == 0)
	            return;

	        String Sizestr = (String) JOptionPane.showInputDialog(null,
	                "Select a component size", "Component Size",
	                JOptionPane.INFORMATION_MESSAGE, null, Size, Size[0]);
	        if (Sizestr == null || Sizestr.length() == 0)
	            return;

	        String components = JOptionPane.showInputDialog(null,
	                "Amount of Components to add(negative number to subtract)");
	        if (components == null || components.length() == 0)
	            return;

	        mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c admingrantcomponents#" + factionName + "#" + Typestr + "#" + Sizestr + "#" + components);
	    }

	    public void jMenuAdminExchangePlanetOwnership_actionPerformed(ActionEvent e) {

	        PlanetNameDialog planetDialog = new PlanetNameDialog(mwclient, "Planet", null);
	        planetDialog.setVisible(true);
	        String planetName = planetDialog.getPlanetName();
	        planetDialog.dispose();

	        if (planetName == null || planetName.length() == 0)
	            return;

	        HouseNameDialog factionDialog = new HouseNameDialog(mwclient, "Gaining Faction", false,false);
	        factionDialog.setVisible(true);
	        String winningHouseName = factionDialog.getHouseName();
	        factionDialog.dispose();

	        if (winningHouseName == null || winningHouseName.length() == 0)
	            return;

	        factionDialog = new HouseNameDialog(mwclient, "Losing Faction", false,false);
	        factionDialog.setVisible(true);
	        String losingHouseName = factionDialog.getHouseName();
	        factionDialog.dispose();

	        if (losingHouseName == null || losingHouseName.length() == 0)
	            return;

	        String amount = JOptionPane.showInputDialog(null, "Amount");
	        if (amount == null || amount.length() == 0)
	            return;

	        mwclient.sendChat(MWClient.CAMPAIGN_PREFIX
	                + "c adminexchangeplanetownership#" + planetName + "#"
	                + winningHouseName + "#" + losingHouseName + "#" + amount);
	    }

	    public void jMenuAdminSetHousePriceMod_actionPerformed(ActionEvent e) {
	        Object[] unitTypes = { "Mek", "Vehicles", "Infantry", "ProtoMek", "BattleArmor" };
	        Object[] unitClass = { "Light", "Medium", "Heavy", "Assault" };

	        HouseNameDialog factionDialog = new HouseNameDialog(mwclient, "Faction", false,false);
	        factionDialog.setVisible(true);
	        String factionName = factionDialog.getHouseName();
	        factionDialog.dispose();

	        if (factionName == null || factionName.length() == 0)
	            return;

	        String unitTypestr = (String) JOptionPane.showInputDialog(null,
	                "Select Unit Type", "Unit Type",
	                JOptionPane.INFORMATION_MESSAGE, null, unitTypes, unitTypes[0]);

	        if (unitTypestr == null || unitTypestr.length() == 0)
	            return;

	        String unitClassstr = (String) JOptionPane.showInputDialog(null,
	                "Select Unit Class", "Unit Class",
	                JOptionPane.INFORMATION_MESSAGE, null, unitClass, unitClass[0]);

	        if (unitClassstr == null || unitClassstr.length() == 0)
	            return;

	        String priceMod = JOptionPane.showInputDialog(null, "Price Modifier:");

	        if (priceMod == null || priceMod.length() == 0)
	            return;

	        mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c adminsethousepricemod#"
	                + factionName + "#" + unitTypestr + "#" + unitClassstr + "#" + priceMod);
	    }
	    
	    public void jMenuAdminSetHouseFluMod_actionPerformed(ActionEvent e) {
	        Object[] unitTypes = { "Mek", "Vehicles", "Infantry", "ProtoMek", "BattleArmor" };
	        Object[] unitClass = { "Light", "Medium", "Heavy", "Assault" };

	        HouseNameDialog factionDialog = new HouseNameDialog(mwclient, "Faction", false,false);
	        factionDialog.setVisible(true);
	        String factionName = factionDialog.getHouseName();
	        factionDialog.dispose();

	        if (factionName == null || factionName.length() == 0)
	            return;

	        String unitTypestr = (String) JOptionPane.showInputDialog(null,
	                "Select Unit Type", "Unit Type",
	                JOptionPane.INFORMATION_MESSAGE, null, unitTypes, unitTypes[0]);

	        if (unitTypestr == null || unitTypestr.length() == 0)
	            return;

	        String unitClassstr = (String) JOptionPane.showInputDialog(null,
	                "Select Unit Class", "Unit Class",
	                JOptionPane.INFORMATION_MESSAGE, null, unitClass, unitClass[0]);

	        if (unitClassstr == null || unitClassstr.length() == 0)
	            return;

	        String fluMod = JOptionPane.showInputDialog(null, "Price Modifier:");

	        if (fluMod == null || fluMod.length() == 0)
	            return;

	        mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c adminsethouseflumod#"
	                + factionName + "#" + unitTypestr + "#" + unitClassstr + "#" + fluMod);
	    }

	    public void jMenuAdminSetHouseTechLevel_actionPerformed(ActionEvent e) {

	        HouseNameDialog factionDialog = new HouseNameDialog(mwclient, "Faction",
	                false,false);
	        factionDialog.setVisible(true);
	        String factionName = factionDialog.getHouseName();
	        factionDialog.dispose();

	        if (factionName == null || factionName.length() == 0)
	            return;

            JComboBox techCombo = new JComboBox(TechConstants.T_NAMES);
            techCombo.setEditable(false);

            JOptionPane jop = new JOptionPane(techCombo, JOptionPane.QUESTION_MESSAGE,JOptionPane.OK_CANCEL_OPTION);
            JDialog dlg = jop.createDialog(null, "Select Tech Level");
            techCombo.grabFocus();
            techCombo.getEditor().selectAll();

            dlg.setVisible(true);

            if ( (Integer)jop.getValue()  == JOptionPane.CANCEL_OPTION )
                return;
            
	        mwclient.sendChat(MWClient.CAMPAIGN_PREFIX
	                + "c adminsethousetechlevel#" + factionName + "#"
	                + techCombo.getSelectedIndex());
	    }

	    public void jMenuAdminSaveTheUniverse_actionPerformed(ActionEvent e) {
	        mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c adminsave");
	    }

	    public void jMenuAdminSaveBlackMaketSettings_actionPerformed(ActionEvent e) {
	        mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c adminsaveblackmarketconfigs");
	    }

	    public void jMenuAdminSavePlanetsToXML_actionPerformed(ActionEvent e) {
	        mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c adminsaveplanetstoxml");
	    }

	    public void jMenuAdminOmniVariantMod_actionPerformed(ActionEvent e) {
	        UnitLoadingDialog unitLoadingDialog = new UnitLoadingDialog(mwclient.getMainFrame());
	        UnitViewerDialog unitSelector = new UnitViewerDialog(mwclient.getMainFrame(), unitLoadingDialog, mwclient,UnitViewerDialog.OMNI_VARIANT_SELECTOR);
	        unitSelector.setName("Unit Selector");
	        new Thread(unitSelector).start();
	    }
	    
	    public void jMenuAdminServerAmmoBan_actionPerformed(ActionEvent e) {
            new BannedAmmoDialog(mwclient,null);
	    }

        public void jMenuAdminSetBanTargeting_actionPerformed(ActionEvent e) {
            new BanTargetingDialog(mwclient);
        }
        public void jMenuAdminRetrieveOperationFile_actionPerformed(ActionEvent e) {
            
            JComboBox opCombo = new JComboBox(mwclient.getAllOps().keySet().toArray());
            opCombo.setEditable(false);

            JOptionPane jop = new JOptionPane(opCombo, JOptionPane.QUESTION_MESSAGE,JOptionPane.OK_CANCEL_OPTION);
            JDialog dlg = jop.createDialog(null, "Select Op.");
            opCombo.grabFocus();
            opCombo.getEditor().selectAll();

            dlg.setVisible(true);

            if ( (Integer)jop.getValue()  == JOptionPane.CANCEL_OPTION )
                return;
            
            String opName = (String)opCombo.getSelectedItem();
            
            mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c RETRIEVEOPERATION#short#"+opName);
        }

        public void jMenuAdminSetOperationFile_actionPerformed(ActionEvent e) {
            
            JComboBox opCombo = new JComboBox(mwclient.getAllOps().keySet().toArray());
            opCombo.setEditable(false);

            JOptionPane jop = new JOptionPane(opCombo, JOptionPane.QUESTION_MESSAGE,JOptionPane.OK_CANCEL_OPTION);
            JDialog dlg = jop.createDialog(null, "Select Op.");
            opCombo.grabFocus();
            opCombo.getEditor().selectAll();

            dlg.setVisible(true);

            if ( (Integer)jop.getValue()  == JOptionPane.CANCEL_OPTION )
                return;
            
            String opName = (String)opCombo.getSelectedItem();
            
            File opFile = new File("./data/operations/short/"+opName+".txt");

            
            if ( !opFile.exists() ){
                return;
            }

            StringBuilder opData = new StringBuilder();
            
            try{
                FileInputStream fis = new FileInputStream(opFile);
                BufferedReader dis = new BufferedReader(new InputStreamReader(fis));
                opData.append(opName+"#");
                while (dis.ready()){
                    opData.append(dis.readLine().replaceAll("#","(pound)")+"#");
                }
                dis.close();
                fis.close();
                
            }catch(Exception ex){
                MWClient.mwClientLog.clientErrLog("Unable to read "+opFile);
                return;
            }

            mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c setoperation#short#"+opData.toString());
        }

        public void jMenuAdminSendAllOperationFiles_actionPerformed(ActionEvent e) {
            

            int result = JOptionPane.showConfirmDialog(null,"Upload All local OpFiles?","Upload Ops",JOptionPane.YES_NO_OPTION);
            
            if ( result == JOptionPane.NO_OPTION )
                return;

        	File opFiles = new File("./data/operations/short/");

            
            if ( !opFiles.exists() ){
                return;
            }

            StringBuilder opData = new StringBuilder();
            
            for (File opFile : opFiles.listFiles() ){
	            try{
	            	
	                FileInputStream fis = new FileInputStream(opFile);
	                BufferedReader dis = new BufferedReader(new InputStreamReader(fis));
	                opData.append(opFile.getName().substring(0,opFile.getName().lastIndexOf(".txt"))+"#");
	                while (dis.ready()){
	                    opData.append(dis.readLine().replaceAll("#","(pound)")+"#");
	                }
	                dis.close();
	                fis.close();
	                
	            }catch(Exception ex){
	                MWClient.mwClientLog.clientErrLog("Unable to read "+opFile);
	                return;
	            }
	            mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c setoperation#short#"+opData.toString());
	            opData.setLength(0);
	            opData.trimToSize();
            }
        }

        public void jMenuAdminSetNewOperationFile_actionPerformed(ActionEvent e) {
            
            
            String opName = JOptionPane.showInputDialog(mwclient.getMainFrame().getContentPane(),"New Op Name?");

            if ( opName == null || opName.trim().length() < 1)
                return;
            
            File opFile = new File("./data/operations/short/"+opName+".txt");

            
            if ( !opFile.exists() ){
                return;
            }

            StringBuilder opData = new StringBuilder();
            
            try{
                FileInputStream fis = new FileInputStream(opFile);
                BufferedReader dis = new BufferedReader(new InputStreamReader(fis));
                opData.append(opName+"#");
                while (dis.ready()){
                    opData.append(dis.readLine().replaceAll("#","(pound)")+"#");
                }
                dis.close();
                fis.close();
                
            }catch(Exception ex){
                MWClient.mwClientLog.clientErrLog("Unable to read "+opFile);
                return;
            }

            mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c setoperation#short#"+opData.toString());
        }

        public void jMenuAdminUpdateOperations_actionPerformed(ActionEvent e) {
            mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c updateoperations");
        }
        
        public void jMenuAdminAmmoCost_actionPerformed(ActionEvent e) {
            new AmmoCostDialog(mwclient);
        }

	    public void jMenuAdminHouseAmmoBan_actionPerformed(ActionEvent e) {
	        HouseNameDialog factionDialog = new HouseNameDialog(mwclient, "Select Faction", false,false);
	        factionDialog.setVisible(true);
	        String factionName = factionDialog.getHouseName();
	        factionDialog.dispose();

	        if (factionName == null || factionName.length() == 0)
	            return;

            new BannedAmmoDialog(mwclient,mwclient.getData().getHouseByName(factionName));
         }

        public void jMenuAdminSetHouseBasePilotSkills_actionPerformed(ActionEvent e) {
            HouseNameDialog factionDialog = new HouseNameDialog(mwclient, "Select Faction", false,false);
            factionDialog.setVisible(true);
            String factionName = factionDialog.getHouseName();
            factionDialog.dispose();

            if (factionName == null || factionName.length() == 0)
                return;
            
            Object[] unitTypes = { "Mek", "Vehicles", "Infantry", "ProtoMeks", "BattleArmor"};

            String unitTypestr = (String) JOptionPane.showInputDialog(null,
                    "Select Unit Type", "Unit Type",
                    JOptionPane.INFORMATION_MESSAGE, null, unitTypes,
                    unitTypes[0]);

            if (unitTypestr == null || unitTypestr.length() == 0)
                return;

            int i;
            for ( i = 0; i < unitTypes.length; i++)
                if (unitTypestr.equals(unitTypes[i]))
                    break;

            int unitTypeint = i;

            String gunnery = JOptionPane.showInputDialog(null, "Base Gunnery");

            if (gunnery == null || gunnery.length() == 0)
                return;

            String piloting = JOptionPane.showInputDialog(null, "Base Piloting");

            if (piloting == null || piloting.length() == 0)
                return;

            StringBuffer sendCommand = new StringBuffer();
            
            sendCommand.append(factionName);
            sendCommand.append("#");
            sendCommand.append(unitTypeint);
            sendCommand.append("#");
            sendCommand.append(gunnery);
            sendCommand.append("#");
            sendCommand.append(piloting);
            
            mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c sethousebasepilotskills#" + sendCommand.toString());
         }

	    public void jMenuAdminSetCommandLevel_actionPerformed(ActionEvent e) {

            CommandNameDialog commandDialog = new CommandNameDialog(mwclient, "Select a Command");
            commandDialog.setVisible(true);
            String commandNamestr = commandDialog.getCommandName();
            commandDialog.dispose();

            if ( commandNamestr == null || commandNamestr.equalsIgnoreCase("null") )
                return;

            String level = JOptionPane.showInputDialog(null, "Level");

	        if (level == null || level.length() == 0)
	            return;

	        mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c adminsetCommandLevel#"
	                + commandNamestr + "#" + level);
	    }

	    public void jMenuAdminLockFactory_actionPerformed(ActionEvent e) {
	        TreeSet<String> names = new TreeSet<String>();

	        PlanetNameDialog planetDialog = new PlanetNameDialog(mwclient, "Select a Planet", null);
	        planetDialog.setVisible(true);
	        String planetNamestr = planetDialog.getPlanetName();
	        planetDialog.dispose();

	        if (planetNamestr == null || planetNamestr.length() == 0)
	            return;

	        Planet planet = mwclient.getData().getPlanetByName(planetNamestr);

	        for (Iterator UF = planet.getUnitFactories().iterator(); UF.hasNext();)
	            names.add(((UnitFactory) UF.next()).getName());

	        JComboBox combo = new JComboBox(names.toArray());
	        combo.setEditable(true);
	        JOptionPane jop = new JOptionPane(combo, JOptionPane.QUESTION_MESSAGE,
	                JOptionPane.OK_CANCEL_OPTION);

	        JDialog dlg = jop.createDialog(null,
	                "Select factory to toggle the lock on.");
	        combo.grabFocus();
	        combo.getEditor().selectAll();

	        dlg.setVisible(true);

	        String factoryName = (String) combo.getSelectedItem();

	        if (factoryName == null || factoryName.length() == 0)
	            return;

	        mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c adminlockfactory#" + planetNamestr + "#" + factoryName);
	        mwclient.reloadData();

	    }

	    public void jMenuAdminSetPlanetMapSize_actionPerformed(ActionEvent e) {
	        PlanetNameDialog planetDialog = new PlanetNameDialog(mwclient, "Select a Planet", null);
	        planetDialog.setVisible(true);
	        String planetNamestr = planetDialog.getPlanetName();
	        planetDialog.dispose();

	        if (planetNamestr == null || planetNamestr.length() == 0)
	            return;

	        String xSize = JOptionPane.showInputDialog(null, "X size");

	        if (xSize == null || xSize.length() == 0)
	            return;

	        String ySize = JOptionPane.showInputDialog(null, "Y Size");

	        if (ySize == null || ySize.length() == 0)
	            return;

	        mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c adminsetplanetmapsize#" + planetNamestr + "#" + xSize + "#" + ySize);
	        mwclient.reloadData();
	    }

        public void jMenuAdminSetPlanetHomeWorld_actionPerformed(ActionEvent e){
            PlanetNameDialog planetDialog = new PlanetNameDialog(mwclient, "Select a Planet", null);
            planetDialog.setVisible(true);
            String planetNamestr = planetDialog.getPlanetName();
            planetDialog.dispose();

            if (planetNamestr == null || planetNamestr.length() == 0)
                return;

            int result = JOptionPane.showConfirmDialog(null,"Set as HomeWorld?","Set HomeWorld",JOptionPane.YES_NO_CANCEL_OPTION);
            
            if ( result == JOptionPane.CANCEL_OPTION )
                return;
            
            boolean homeworld = false;
            if ( result == JOptionPane.YES_OPTION )
                homeworld = true;
            
            mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c adminsethomeworld#" + planetNamestr + "#" + homeworld);
            mwclient.reloadData();

        }
        

	    public void jMenuAdminSetPlanetBoardSize_actionPerformed(ActionEvent e) {
	        PlanetNameDialog planetDialog = new PlanetNameDialog(mwclient, "Select a Planet", null);
	        planetDialog.setVisible(true);
	        String planetNamestr = planetDialog.getPlanetName();
	        planetDialog.dispose();

	        if (planetNamestr == null || planetNamestr.length() == 0)
	            return;

	        String xSize = JOptionPane.showInputDialog(null, "X size");

	        if (xSize == null || xSize.length() == 0)
	            return;

	        String ySize = JOptionPane.showInputDialog(null, "Y Size");

	        if (ySize == null || ySize.length() == 0)
	            return;

	        mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c adminsetplanetboardsize#" + planetNamestr + "#" + xSize + "#" + ySize);
	        mwclient.reloadData();
	    }
        
        public void jMenuAdminSetPlanetOriginalOwner_actionPerformed(ActionEvent ex) {
            
            PlanetNameDialog planetDialog = new PlanetNameDialog(mwclient, "Select a Planet", null);
            planetDialog.setVisible(true);
            String planetNamestr = planetDialog.getPlanetName();
            planetDialog.dispose();

            if (planetNamestr == null || planetNamestr.length() == 0)
                return;

            HouseNameDialog hnd = new HouseNameDialog(mwclient,"Select Original Owner",false,false);
            hnd.setVisible(true);
            String owner = hnd.getHouseName();
            hnd.dispose();
            
            if (owner == null || owner.length() == 0)
                return;

            mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c adminsetplanetoriginalowner#"+ planetNamestr +"#"+owner);
            mwclient.reloadData();
        }


	    public void jMenuAdminSetPlanetTemperature_actionPerformed(ActionEvent e) {
	        PlanetNameDialog planetDialog = new PlanetNameDialog(mwclient, "Select a Planet", null);
	        planetDialog.setVisible(true);
	        String planetNamestr = planetDialog.getPlanetName();
	        planetDialog.dispose();

	        if (planetNamestr == null || planetNamestr.length() == 0)
	            return;

	        String lowTemp = JOptionPane.showInputDialog(null, "Low Temp");

	        if (lowTemp == null || lowTemp.length() == 0)
	            return;

	        String hiTemp = JOptionPane.showInputDialog(null, "Hi Temp");

	        if (hiTemp == null || hiTemp.length() == 0)
	            return;

	        mwclient.sendChat(MWClient.CAMPAIGN_PREFIX
	                + "c adminsetplanettemperature#" + planetNamestr + "#"
	                + lowTemp + "#" + hiTemp);
	        mwclient.reloadData();
	    }

	    public void jMenuAdminSetPlanetGravity_actionPerformed(ActionEvent e) {
	        PlanetNameDialog planetDialog = new PlanetNameDialog(mwclient, "Select a Planet", null);
	        planetDialog.setVisible(true);
	        String planetNamestr = planetDialog.getPlanetName();
	        planetDialog.dispose();

	        if (planetNamestr == null || planetNamestr.length() == 0)
	            return;

	        String grav = JOptionPane.showInputDialog(null, "Gravity");

	        if (grav == null || grav.length() == 0)
	            return;

	        mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c adminsetplanetgravity#" + planetNamestr + "#" + grav);
	        mwclient.reloadData();
	    } 
	 
        public void jMenuAdminCommandLists_actionPerformed(ActionEvent e){
            CommandNameDialog commandDialog = new CommandNameDialog(mwclient, "Select a Command");
            commandDialog.setVisible(true);
            String commandNamestr = commandDialog.getCommandName();
            commandDialog.dispose();

            if ( commandNamestr != null ){
                String input = MWClient.CAMPAIGN_PREFIX +commandNamestr;
                mwclient.getMainFrame().getMainPanel().getCommPanel().setInput(input);
                mwclient.getMainFrame().getMainPanel().getCommPanel().focusInputField();
            }
        }
        
        public void jMenuAdminComponentList_actionPerformed(ActionEvent e){
            //ComponentDisplayDialog componentDialog = 
            int type = (Integer)e.getSource();
            new ComponentDisplayDialog(mwclient,type);
            //componentDialog.setVisible(true);
        }

}//end AdminMenu class