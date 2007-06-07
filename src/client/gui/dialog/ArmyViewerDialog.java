
/*
 * MekWars - Copyright (C) 2004, 2005 
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

/*
 * MechSelectorJDialog.java - Copyright (C) 2002,2004 Josh Yockey
 * 
 *  This program is free software; you can redistribute it and/or modify it 
 *  under the terms of the GNU General Public License as published by the Free 
 *  Software Foundation; either version 2 of the License, or (at your option) 
 *  any later version.
 * 
 *  This program is distributed in the hope that it will be useful, but 
 *  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 *  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 *  for more details.
 */

/* 
 * Thanks to the MegaMek Crew for the Code base
 * Modified by Torren (Jason Tighe)
 * From Megamek.client.MechSelectorDialgo.java
 */

package client.gui.dialog;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowListener;
import java.util.StringTokenizer;
import java.util.TreeSet;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpringLayout;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.DefaultListModel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import common.Unit;

import client.MWClient;
import client.campaign.CArmy;
import client.campaign.CPlayer;
import client.campaign.CUnit;
import client.gui.SpringLayoutHelper;


/* 
 * Allows a user to sort through a list of MechSummaries and select one
 */

public class ArmyViewerDialog extends JDialog implements ActionListener, KeyListener, ListSelectionListener, WindowListener, ItemListener {
	
	/**
     * 
     */
    private static final long serialVersionUID = -3851019509649287454L;


    //how long after a key is typed does a new search begin
	private final static int KEY_TIMEOUT = 1000;

	
	private StringBuilder m_sbSearch = new StringBuilder();
	private long m_nLastSearch = 0;
	
	private DefaultListModel defaultModel = null;
	private ListSelectionModel listSelectionModel= null;
	private JList armyList = null;
	private JScrollPane listScrollPane = null;
	private JScrollPane leftScrollPane = null;
	
	private JButton bCancel = new JButton("Close");
	private JButton bSelect = new JButton("Select");
	
	private JTextArea armyView = null;
	
	private JComboBox teamBox = new JComboBox();
	
	private CPlayer player = null;
	private String opName = null;
	private int selectedArmyId = -1;
    private TreeSet<Integer> validArmyList = new TreeSet<Integer>();
    private MWClient mwclient = null;
    private String planetName = null;
    private String defenderName = null;
    private int opID = -1;
    private int teamNumbers = -1;
    
    private int viewerMode = 0;
    
    public static final int AVD_DEFEND = 0;
    public static final int AVD_ATTACK = 1;
    public static final int AVD_ATTACKFROMRESERVE = 2;
    
	public ArmyViewerDialog(MWClient mwclient, String opName, StringTokenizer validArmyList, int mode, String planet, String defender, int opid, int teamNumbers) {
        super(mwclient.getMainFrame(),"Army Viewer", true);//dummy frame as owner
    	
		//save params
		player = mwclient.getPlayer();
		this.opName = opName;
        this.mwclient = mwclient;
        this.viewerMode = mode;
        this.planetName = planet;
        this.defenderName = defender;
        this.opID = opid;
        this.teamNumbers = teamNumbers;
        
        //We are defending and need to parse out which armies we can do that with!
        if ( validArmyList != null )
            while ( validArmyList.hasMoreElements() )
                this.validArmyList.add(Integer.parseInt(validArmyList.nextToken()));

        teamBox.setPreferredSize(new Dimension(100,22));
        teamBox.setMaximumSize(new Dimension(100,22));
        teamBox.setMinimumSize(new Dimension(100,22));

        if ( teamNumbers > 1 ) {
        	for ( int team = 1; team <= teamNumbers; team++)
        		teamBox.addItem("Team #"+team);
        }
        //construct text boxes
		armyView = new JTextArea(15,38);
		armyView.setAutoscrolls(true);
		//construct a model and list
		defaultModel = new DefaultListModel();
		armyList = new JList(defaultModel);
        
		listSelectionModel = armyList.getSelectionModel();
		armyList.setVisibleRowCount(Integer.parseInt(mwclient.getserverConfigs("MaxLancesPerPlayer")));
		listSelectionModel.addListSelectionListener(this);

		//place the list and text boxes in scroll panes
		listScrollPane = new JScrollPane(armyList);
		leftScrollPane = new JScrollPane(armyView);
		
		//set list/scroll options
		listScrollPane.setAlignmentX(LEFT_ALIGNMENT);
		listScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		leftScrollPane.setAlignmentX(LEFT_ALIGNMENT);
		leftScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		armyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		//set fonts
		armyView.setFont(new Font("Monospaced", Font.PLAIN, 11));
		armyList.setFont(new Font("Monospaced", Font.PLAIN, 11));
		
		
		//panel w/ 1x3 SpringLayout for the mechView bits
		JPanel textBoxSpring = new JPanel(new SpringLayout());
		textBoxSpring.add(listScrollPane);
		textBoxSpring.add(leftScrollPane);
		SpringLayoutHelper.setupSpringGrid(textBoxSpring,3);
		
		//set up a formatting holder for the cancel button
		JPanel buttonHolder = new JPanel();
		buttonHolder.add(bSelect);
		buttonHolder.add(bCancel);
		
		//set up the overall SpringLayout
		JPanel springHolder = new JPanel(new SpringLayout());
		if ( this.teamNumbers > 1)
			springHolder.add(teamBox);
		springHolder.add(textBoxSpring);
		springHolder.add(buttonHolder);
		SpringLayoutHelper.setupSpringGrid(springHolder,1);
		this.getContentPane().add(springHolder);
		this.getRootPane().setDefaultButton(bSelect);
        
		clearArmyPreview();
		setSize(785, 560);
		setResizable(false);
		
		//add all the listeners
		armyList.addListSelectionListener(this);
		armyList.addKeyListener(this);
		bCancel.addActionListener(this);
		bSelect.addActionListener(this);
		addWindowListener(this);

        armyList.setSelectedIndex(-1);
        this.setModal(false);
        this.sortArmies();
        this.pack();
        this.setVisible(true);
        this.armyList.requestFocus();
	}
	
	private void sortArmies() {
		defaultModel.clear();
		int x=0;

        if ( opName != null ){
            for (CArmy currArmy : player.getArmies()) {
                //include only armies which can actually make an attack
                if (currArmy.getLegalOperations().contains(opName))
                    defaultModel.add(x++, formatArmy(currArmy));
                
            }
        } else {//Defend
    		for (CArmy army : player.getArmies()) {
                if ( this.validArmyList.contains(army.getID()))
                    defaultModel.add(x++, formatArmy(army));
    		}
        }
		repaint();
	}
	
	private void searchFor(String search) {
        int i =0;
        for (CArmy army : player.getArmies()) {
			if (army.getName().toLowerCase().startsWith(search)) {
				armyList.setSelectedIndex(i);
				armyList.ensureIndexIsVisible(i);
				break;
			}
            i++;
		}
	}
	
	@Override
	public void setVisible(boolean show) {
		this.setLocationRelativeTo(null);
		super.setVisible(show);
		this.pack();
	}
	
	private String formatArmy(CArmy army) {

        StringBuilder result = new StringBuilder();
		result.append(makeLength("#"+Integer.toString(army.getID()),3) + " "); 
        result.append(makeLength(army.getName(),15) + " ");
        result.append(makeLength("BV: "+Integer.toString(army.getBV()), 10)); 

        return result.toString();
	}
	
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == bCancel) {
			this.setVisible(false);
            selectedArmyId = -1;
		}
		if (ae.getSource() == bSelect){
			try{
			    
                String armyID = (String)armyList.getSelectedValue();
                
                armyID = armyID.substring(1,armyID.indexOf(" "));

                selectedArmyId = Integer.parseInt(armyID);
                
                if ( viewerMode == AVD_DEFEND ) {
                	int team = -1;
                	
                	if ( this.teamNumbers > 1 )
                		team = teamBox.getSelectedIndex()+1;
                    mwclient.sendChat("/c defend#" + opID  + "#" + armyID+"#"+team);
                }
                else if ( viewerMode == AVD_ATTACK)
                    mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c attack#" + opName + "#" + armyID + "#" + planetName);
                else 
                    mwclient.sendChat("/c attackfromreserve#"+opName+"#"+armyID+"#"+planetName+"#"+defenderName);
				this.setVisible(false);
                
			}
			catch(Exception ex){
				MWClient.mwClientLog.clientErrLog(ex);
				//MMClient.mwClientLog.clientErrLog("Problem with actionPerformed in RepodDialog");
			}
		}// end unit selector if.
	}
	
	/**
	 * for compliance with ListSelectionListener
	 */
	public void valueChanged(ListSelectionEvent event) {
		
		int selected = armyList.getSelectedIndex();
		if (selected == -1) {
			clearArmyPreview();
			return;
		}
        
        String armyID = (String)armyList.getSelectedValue();
        
        armyID = armyID.substring(1,armyID.indexOf(" "));
		previewArmy(Integer.parseInt(armyID));
        
	}
    
	
	public void itemStateChanged(ItemEvent ie) {
		
		Object currSelection = armyList.getSelectedValue();

        armyList.setSelectedValue(currSelection, true);
	}
	
	void clearArmyPreview() {
		armyView.setEditable(false);
		armyView.setText("");
		
		//Remove preview image.        
		previewArmy(-1);
		
	}
	
	void previewArmy(int armyID) {
		
		
		armyView.setEditable(false);
		if ( armyID > -1 ) {
            StringBuilder armyText = new StringBuilder();
            CArmy army = player.getArmy(armyID);
            for ( Unit unit : army.getUnits() ){
                armyText.append(makeLength("#"+unit.getId(),7)+" "+makeLength(((CUnit)unit).getModelName(),12)+" ");
                if ( unit.getType() == Unit.VEHICLE || unit.getType() == Unit.MEK )
                    armyText.append(" ("+unit.getPilot().getGunnery()+"/"+unit.getPilot().getPiloting()+")");
                else
                   armyText.append(" ("+unit.getPilot().getGunnery()+")");
                armyText.append(" BV: "+((CUnit)unit).getBV()+"\n");
            }
			armyView.setText(armyText.toString());
		} else {
			armyView.setText("No army selected");
		}
		armyView.setCaretPosition(0);
		
	}
	
    public int getSelectedArmyID(){
        return selectedArmyId;
    }
    
	private static final String SPACES = "                        ";
	private String makeLength(String s, int nLength) {
		if (s.length() == nLength)
			return s;
		else if (s.length() > nLength)
			return s.substring(0, nLength - 2) + "..";
		else
			return s + SPACES.substring(0, nLength - s.length());
	}
	
	public void keyReleased(java.awt.event.KeyEvent ke) {
		//no action on release
	}
	
	public void keyPressed(java.awt.event.KeyEvent ke) {
		if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
			ActionEvent event = new ActionEvent(bCancel,ActionEvent.ACTION_PERFORMED,"");
			actionPerformed(event);
		}
		long curTime = System.currentTimeMillis();
		if (curTime - m_nLastSearch > KEY_TIMEOUT) {
			m_sbSearch = new StringBuilder();
		}
		m_nLastSearch = curTime;
		m_sbSearch.append(ke.getKeyChar());
		searchFor(m_sbSearch.toString().toLowerCase());
	}
	
	public void keyTyped(java.awt.event.KeyEvent ke) {
	}
	
	// WindowListener
	public void windowActivated(java.awt.event.WindowEvent windowEvent) {
	}    
	public void windowClosed(java.awt.event.WindowEvent windowEvent) {
	}    
	public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		this.dispose();
	}    
	public void windowDeactivated(java.awt.event.WindowEvent windowEvent) {
	}    
	public void windowDeiconified(java.awt.event.WindowEvent windowEvent) {
	}    
	public void windowIconified(java.awt.event.WindowEvent windowEvent) {
	}    
	public void windowOpened(java.awt.event.WindowEvent windowEvent) {
	}
}
