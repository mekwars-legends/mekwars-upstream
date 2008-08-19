/*
 * MekWars - Copyright (C) 2007
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
 * 
 * Portions of this dialog derived from work done by Imanuel Schultz. Original
 * part of MegaMekNET's client.gui.actions pacakge as SearchHouseActionListener.java.
 * See http://www.sourceforge.net/projects/megameknet for more info.
 */

package client.gui.dialog;

//awt imports
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

//util imports
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

//swing imports
import javax.swing.BoxLayout;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpringLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

//mekwars imports
import client.MWClient;
import common.util.SpringLayoutHelper;
import common.House;



public class SubFactionNameDialog extends JDialog implements ActionListener {

	/**
     * 
     */
    private static final long serialVersionUID = 3552906075410667280L;
    //variables
	private final TreeSet<String>subFactionNames;
	private House faction = null;
	
	private JList matchingHousesList;
	private JScrollPane scrollPane;//holds the JList
	private JTextField nameField;//input field
	private final JButton okayButton = new JButton("OK");
	private final JButton cancelButton = new JButton("Cancel");	
	private final String okayCommand = "Okay";

	private String subFactionName = null;
	private boolean addblank = false;
	
	//constructor
	public SubFactionNameDialog(MWClient mwclient, String boxText, String factionName) {
		
		/*
		 * NOTE: variables are final in order to
		 * allow access by caretUpdate()
		 */
		
		//super, and variable saves
		super(mwclient.getMainFrame(),boxText, true);//dummy frame as owner
		this.faction = mwclient.getData().getHouseByName(factionName);
		
		//setup the a list of names to feed into a list
		this.subFactionNames = new TreeSet<String>();//tree to alpha sort

		if ( faction == null )
			return;
		
		for (String subFaction : faction.getSubFactionList().keySet()) {
			subFactionNames.add(subFaction);
		}
		final Object[] allSubFactionNames = subFactionNames.toArray();
		
		//construct the faction name list
		matchingHousesList = new JList(allSubFactionNames);
		matchingHousesList.setVisibleRowCount(10);
		matchingHousesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		//the name field, for user input. caretUpdate
		//does most of the work to update list contents
		nameField = new JTextField();//field for user input
		nameField.addCaretListener(new CaretListener(){
			public void caretUpdate(CaretEvent e) {
				new Thread() {
					@Override
					public void run(){
						String text = nameField.getText();
						if (text == null || text.equals("")) {
							matchingHousesList.setListData(allSubFactionNames);
							return;
						}
						ArrayList<String> possibleHouses = new ArrayList<String>();
						text = text.toLowerCase();
						for (String subFaction : faction.getSubFactionList().keySet()) {	
							if (subFaction.toLowerCase().indexOf(text) != -1)
								possibleHouses.add(subFaction);
						}
						matchingHousesList.setListData(possibleHouses.toArray());
						
						/*
						 * Try to select a faction with a STARTING string which matched
						 * the seach index. If none is available, use the first faction.
						 * 
						 * Hacky, but functional. @urgru 5.2.05
						 */
						boolean shouldContinue = true;
						int element = 0;
						Iterator<String> it = possibleHouses.iterator();
						while (it.hasNext() && shouldContinue) {
							String name = it.next();
							if (name.toLowerCase().startsWith(text)) {
								matchingHousesList.setSelectedIndex(element);
								shouldContinue = false;
							}
							element++;
						}
						
						//looped through without finding a starting match. set 0.
						if (shouldContinue) {
							matchingHousesList.setSelectedIndex(0);
						}
						
					}
				}.start();
			}
		});
		
		//put the list in a scroll pane
		scrollPane = new JScrollPane(matchingHousesList);
		scrollPane.setAlignmentX(LEFT_ALIGNMENT);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		//set up listeners for the buttons
		okayButton.setActionCommand(okayCommand);
		okayButton.addActionListener(this);
		cancelButton.addActionListener(this);
		
		//do some formatting. rawr.
		JPanel springPanel = new JPanel(new SpringLayout());
		springPanel.add(nameField);
		springPanel.add(scrollPane);
		SpringLayoutHelper.setupSpringGrid(springPanel,2,1);
		
		JPanel buttonFlow = new JPanel();
		buttonFlow.add(okayButton);
		buttonFlow.add(cancelButton);
		
		JPanel generalLayout = new JPanel();
		generalLayout.setLayout(new BoxLayout(generalLayout, BoxLayout.Y_AXIS));
		generalLayout.add(springPanel);
		generalLayout.add(buttonFlow);
		this.getContentPane().add(generalLayout);
		this.pack();
		
		this.checkMinimumSize();
		this.setResizable(true);
		
		//set a default button
		this.getRootPane().setDefaultButton(okayButton);
		
		//center the dialog.
		this.setLocationRelativeTo(mwclient.getMainFrame());
       
	}
	
	
	/**
	 * OK or CANCEL buttons pressed. Handle any
	 * changes and then close the dialouge.
	 */
	public void actionPerformed(ActionEvent event) {
		
		String command = event.getActionCommand();
		
		if (command.equals(okayCommand)) {
			 String selectedHouse = (String)matchingHousesList.getSelectedValue();
		        if (selectedHouse == null)
		        	selectedHouse = nameField.getText();
		        if (!addblank && (selectedHouse == null || selectedHouse.equals("")))
		        	return;
		        if (matchingHousesList.getModel().getSize() == 1)
		        	selectedHouse = (String)matchingHousesList.getModel().getElementAt(0);
				for (String subFaction : faction.getSubFactionList().keySet()) {	
		        	if (selectedHouse.equals(subFaction)) {
		        	    this.setSubFactionName(subFaction);
		        	    this.setVisible(false);
		        		//this.dispose();
		        		return;
		        	}
		        }
				//New SubFaction
        	    this.setSubFactionName(nameField.getText());
        	    this.setVisible(false);
        	    return;
		        //JOptionPane.showMessageDialog(null,"Unknown House");
		}
		
		//dispose of the dialog
		this.dispose();
		
	}//end actionPerformed
	
	private void checkMinimumSize() {
		
		Dimension curDim = this.getSize();
		
		int height = 0;
		int width = 0;
		boolean shouldRedraw = false;
		
		if (curDim.getWidth() < 300) {
			width = 300;
			shouldRedraw = true;
		} else
			width = (int)curDim.getWidth();
		
		if (curDim.getHeight() < 150) {
			height = 150;
			shouldRedraw = true;
		} else
			height = (int)curDim.getHeight();
		
		if (shouldRedraw) {
			this.setSize(new Dimension(width, height));
		}
		
	}//end checkMinimumSize

	private void setSubFactionName(String name){
	    this.subFactionName = name;
	}
	
	public String getSubFactionName(){
	    return this.subFactionName;
	}
}
