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
import java.util.TreeSet;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import client.MWClient;
import client.gui.SpringLayoutHelper;

public final class AmmoCostDialog implements ActionListener{
	
	//store the client backlink for other things to use
	private MWClient mwclient = null; 
    
	private final static String okayCommand = "Add";
	private final static String cancelCommand = "Close";

	private String windowName = "Ammo Cost Editor";	
    
	//BUTTONS
	private final JButton okayButton = new JButton("Save");
	private final JButton cancelButton = new JButton("Close");	
	
	//STOCK DIALOUG AND PANE
	private JDialog dialog;
	private JOptionPane pane;
    private JPanel ammoPanel = new JPanel(new SpringLayout());
    
	
	JTabbedPane ConfigPane = new JTabbedPane(SwingConstants.TOP);
	
	public AmmoCostDialog(MWClient c) {
		
		//save the client
		this.mwclient = c;
        
		//stored values.

		//Set the tooltips and actions for dialouge buttons
		okayButton.setActionCommand(okayCommand);
		cancelButton.setActionCommand(cancelCommand);
		cancelButton.setSelected(true);

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
		
        loadAmmo();
        
        TreeSet<String> munitions = new TreeSet<String>(mwclient.getData().getMunitionsByName().keySet());
        for ( String munitionName : munitions){
            ammoPanel.add(new JLabel(munitionName,SwingConstants.TRAILING));
            JTextField ammoCost = new JTextField(5);
            ammoCost.setName(munitionName);
            if ( mwclient.getData().getAmmoCost().get(mwclient.getData().getMunitionsByName().get(munitionName)) != null)
                ammoCost.setText(Integer.toString(mwclient.getData().getAmmoCost().get(mwclient.getData().getMunitionsByName().get(munitionName))));
            ammoPanel.add(ammoCost);
        }

        SpringLayoutHelper.setupSpringGrid(ammoPanel,4);

        banPanel.add(ammoPanel);
        
        // Set the user's options
		Object[] options = { okayButton, cancelButton };
		
		// Create the pane containing the buttons
		pane = new JOptionPane(banPanel,JOptionPane.PLAIN_MESSAGE,JOptionPane.DEFAULT_OPTION, null, options, null);
		
		// Create the main dialog and set the default button
		dialog = pane.createDialog(ammoPanel, windowName);
		dialog.getRootPane().setDefaultButton(cancelButton);


		//Show the dialog and get the user's input
		dialog.setModal(true);
		dialog.pack();
		dialog.setVisible(true);
		
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
        if ( command.equals(okayCommand)){
        	int cost = 0;
            for ( int x =0; x < ammoPanel.getComponentCount(); x++){
                
                Object field = ammoPanel.getComponent(x);
                
                if ( !( field instanceof JTextField) )
                    continue;
                
                JTextField textBox = (JTextField)field;
                Long ammo = mwclient.getData().getMunitionsByName().get(textBox.getName());
                try {
                cost = Integer.parseInt(textBox.getText());
                }catch (Exception ex) {
                	cost = 0;
                	MWClient.mwClientLog.clientErrLog(ex);
                }
                if ( mwclient.getData().getAmmoCost().get(ammo) == null || cost != mwclient.getData().getAmmoCost().get(ammo) )
                    mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c adminsetammocost#"+ammo+"#"+cost);
            }
            dialog.dispose();
            return;
        }
        else if (command.equals(cancelCommand)) {
            dialog.dispose();
		}

	}
	
    public void loadAmmo(){
            mwclient.loadAmmoCosts();
    }
}//end AmmoCostDialog.java
