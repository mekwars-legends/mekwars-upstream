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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.SpringLayout;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import common.CampaignData;
import common.util.SpringLayoutHelper;

import megamek.common.MiscType;

import client.MWClient;

public final class BanTargetingDialog implements ActionListener{
	
	//store the client backlink for other things to use
	private MWClient mwclient = null; 
    
	private final static String okayCommand = "Add";
	private final static String cancelCommand = "Close";

	private String windowName = "Ban Targeting Editor";	
    private ArrayList<JCheckBox> cBoxArrayList = new ArrayList<JCheckBox>();
    
	//BUTTONS
	private final JButton okayButton = new JButton("Save");
	private final JButton cancelButton = new JButton("Close");	
	
	//STOCK DIALOUG AND PANE
	private JDialog dialog;
	private JOptionPane pane;
	
	JTabbedPane ConfigPane = new JTabbedPane();
	
	public BanTargetingDialog(MWClient c) {
		
		//save the client
		this.mwclient = c;
        
		//stored values.

		//Set the tooltips and actions for dialouge buttons
		okayButton.setActionCommand(okayCommand);
		cancelButton.setActionCommand(cancelCommand);
		
		okayButton.addActionListener(this);
		cancelButton.addActionListener(this);
		okayButton.setToolTipText("Save");
        cancelButton.setToolTipText("Exit without saving changes");
		
		
		//CREATE THE PANELS
		JPanel banPanel = new JPanel();//player name, etc
		
		/*
		 * Format the Reward Points panel. Spring layout.
		 */
		banPanel.setLayout(new BoxLayout(banPanel,BoxLayout.Y_AXIS));
		
		JPanel targetingPanel = new JPanel(new SpringLayout());
		
        loadBanTargeting();
        
        for ( int targetType = 0; targetType < MiscType.targSysNames.length; targetType++ ){
            if ( targetType == MiscType.T_TARGSYS_TARGCOMP )
                continue;
            JCheckBox cBox = new JCheckBox();
            cBox.setText(MiscType.getTargetSysName(targetType));
            cBox.setSelected(checkBan(targetType));
            targetingPanel.add(cBox);
            cBoxArrayList.add(cBox);
        }

        SpringLayoutHelper.setupSpringGrid(targetingPanel,2);

        banPanel.add(targetingPanel);
        
        // Set the user's options
		Object[] options = { okayButton, cancelButton };
		
		// Create the pane containing the buttons
		pane = new JOptionPane(banPanel,JOptionPane.PLAIN_MESSAGE,JOptionPane.DEFAULT_OPTION, null, options, null);
		
        windowName = " Ban Targeting Systems Dialog";
		// Create the main dialog and set the default button
		dialog = pane.createDialog(targetingPanel, windowName);
		dialog.getRootPane().setDefaultButton(cancelButton);


		//Show the dialog and get the user's input
		dialog.setModal(true);
		dialog.pack();
		dialog.setVisible(true);
		
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
        HashMap<Integer,String> targetingTypes = mwclient.getData().getBannedTargetingSystems();
        
        if ( command.equals(okayCommand)){
                for ( JCheckBox tempBox : cBoxArrayList ){
                    int targetingSystem = MiscType.getTargetSysType(tempBox.getText());
                    
                    //Check box has been selected and should be updated to the server
                    if ( tempBox.isSelected() && !targetingTypes.containsKey(targetingSystem) )
                        mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c AdminSetBanTargeting#"
                                +targetingSystem);
                    //Checkbox has been unselected and should be updated to the server
                    else if ( !tempBox.isSelected() && targetingTypes.containsKey(targetingSystem)) 
                        mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c AdminSetBanTargeting#"
                                +targetingSystem);
                    
            }
            
            dialog.dispose();
            return;
        }
        else if (command.equals(cancelCommand)) {
            dialog.dispose();
		}

	}
	
    public void loadBanTargeting(){
            mwclient.loadBanTargeting();
    }
    
    public boolean checkBan(int targetingType){

        try{
            return mwclient.getData().getBannedTargetingSystems().containsKey(targetingType);
        }catch(Exception ex){
            CampaignData.mwlog.errLog("Unable to find ammo "+MiscType.getTargetSysName(targetingType));
            return false;
        }

    }
	
}//end BannedAmmoDialog.java
