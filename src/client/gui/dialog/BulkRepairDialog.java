/*
 * MekWars - Copyright (C) 2005 
 * 
 * Original author - Torren (torren@users.sourceforge.net)
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

package client.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import common.campaign.pilot.Pilot;
import common.campaign.pilot.skills.PilotSkill;
import common.util.UnitUtils;

import megamek.common.CriticalSlot;
import megamek.common.Entity;
import megamek.common.Mech;
import megamek.common.Mounted;
import megamek.common.WeaponType;

import client.MWClient;
import client.campaign.CUnit;
import client.gui.SpringLayoutHelper;

@SuppressWarnings({"unchecked","serial"})
    public class BulkRepairDialog extends JFrame implements ActionListener, KeyListener, ChangeListener{
	
	//store the client backlink for other things to use
	private MWClient mwclient = null;
    private Entity unit = null;
    private CUnit playerUnit = null;
    
	private final static String okayCommand = "Add";
	private final static String cancelCommand = "Close";
    
	private String windowName = "Bulk Repair Dialog";
	
    private final int ARMOR = 1;
    private final int INTERNAL = 2;
    private final int WEAPONS = 3;
    private final int EQUIPMENT = 4;
    private final int SYSTEMS = 5;
    private final int ENGINES = 6;
    
    //BUTTONS
    
	private final JButton okayButton = new JButton("Start Repairs");
	private final JButton cancelButton = new JButton("Close");	
	
	//STOCK DIALOUG AND PANE
	//private JDialog dialog;
	private JOptionPane pane;
    private JPanel MasterPanel = new JPanel();
    private JPanel contentPane;
    
    private JPanel masterBox = new JPanel(new SpringLayout());
    private JPanel repairBox = new JPanel();
    private JPanel techBox = new JPanel();
    private JPanel rollBox = new JPanel();
    private JPanel costBox = new JPanel();
    private JPanel timeBox = new JPanel();

    private JPanel blankPanel1 = new JPanel();
    private JPanel blankPanel2 = new JPanel();
    private JPanel totalTextPanel = new JPanel();
    private JPanel totalPanel = new JPanel();
    private JPanel timePanel = new JPanel();
    
    private JCheckBox repairCB = new JCheckBox();

    //Text boxes
    private JLabel costField = new JLabel();
    private JLabel timeField = new JLabel();

    private SpinnerNumberModel baseRollEditor = new SpinnerNumberModel(); 
    private JSpinner baseRollField = new JSpinner(baseRollEditor);
    
    private JComboBox techComboBox = new JComboBox();
    private int repairType = BulkRepairDialog.TYPE_BULK;
    
    public static int TYPE_BULK = 0;
    public static int TYPE_SIMPLE = 1;
    public static int TYPE_SALVAGE = 2;
    
	public BulkRepairDialog(MWClient c, int unitID, int repairType) {
        
        //super(c.getMainFrame(),"Repair Dialog", true);
        //super();

		//save the client
		this.mwclient = c;
        this.playerUnit =c.getPlayer().getUnit(unitID);
        this.repairType = repairType;
        
        synchronized (playerUnit.getEntity()) {
            this.unit = playerUnit.getEntity();
        }
        windowName = unit.getShortNameRaw()+" Repair Dialog";
        
        this.addKeyListener(this);
        
		//stored values.

        if ( isSalvage() )
        	okayButton.setText("Start Salvage");
		//Set the tooltips and actions for dialouge buttons
		okayButton.setActionCommand(okayCommand);
        okayButton.addActionListener(this);
        okayButton.setToolTipText("Start Bulk Repairs.");
        okayButton.setMnemonic('S');

        cancelButton.setActionCommand(cancelCommand);
		cancelButton.addActionListener(this);
        cancelButton.setToolTipText("Close the bulk repair dialog");
        cancelButton.setDefaultCapable(true);
        
		//CREATE THE PANELS
        MasterPanel.setLayout(new BoxLayout(MasterPanel, BoxLayout.Y_AXIS));
        
        repairBox.setLayout(new BoxLayout(repairBox, BoxLayout.Y_AXIS));
        costBox.setLayout(new BoxLayout(costBox, BoxLayout.Y_AXIS));
        techBox.setLayout(new BoxLayout(techBox, BoxLayout.Y_AXIS));
        rollBox.setLayout(new BoxLayout(rollBox, BoxLayout.Y_AXIS));
        timeBox.setLayout(new BoxLayout(timeBox, BoxLayout.Y_AXIS));

        loadPanel();

        // Set the user's options
		Object[] options = { okayButton, cancelButton };


		// Create the pane containing the buttons
		pane = new JOptionPane(MasterPanel,JOptionPane.PLAIN_MESSAGE,JOptionPane.OK_CANCEL_OPTION, null, options, null);

        this.setIconImage(mwclient.getConfig().getImage("REPAIR").getImage());
        this.setTitle(windowName);

        contentPane = (JPanel) this.getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(pane, BorderLayout.CENTER);
        this.setResizable(true);
        if ( !isBulk() )
            this.setSize(new Dimension(440,287));
        else
            this.setSize(new Dimension(369,287));
        this.setExtendedState(Frame.NORMAL);
        contentPane.addKeyListener(this);
        pane.addKeyListener(this);
        MasterPanel.addKeyListener(this);
        cancelButton.addKeyListener(this);
        masterBox.addKeyListener(this);
        repairBox.addKeyListener(this);
        costBox.addKeyListener(this);
        techBox.addKeyListener(this);
        rollBox.addKeyListener(this);
        timeBox.addKeyListener(this);
        
        this.addKeyListener(this);

        this.repaint();
        //this.setLocation((mwclient.getMainFrame().getWidth()/2)-(this.getWidth()/2),(mwclient.getMainFrame().getHeight()/2)-(this.getHeight()/2));
        this.setLocation(Math.max(mwclient.getMainFrame().getLocation().x,mwclient.getMainFrame().getLocation().x+((mwclient.getMainFrame().getWidth()/2)-(this.getWidth()/2))),Math.max(mwclient.getMainFrame().getLocation().y+(mwclient.getMainFrame().getHeight()/2)-this.getHeight()/2,mwclient.getMainFrame().getLocation().y));

        //this.pack();
        this.setVisible(true);
        setRepair();
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();

        if ( command.equals(okayCommand)){
            

            if ( isSimple() ){
                StringBuilder sb = new StringBuilder();
                for (int type = ARMOR; type <= ENGINES; type++){
                    sb.append("#"+((JComboBox)techBox.getComponent(type)).getSelectedIndex());
                    sb.append("#"+((JSpinner)rollBox.getComponent(type)).getValue().toString());
                }
                mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c simplerepair#"+playerUnit.getId()+sb.toString());
            }else if (isSalvage()){
                mwclient.getSMT().removeAllWorkOrders(unit.getExternalId());
                checkArmor();
                checkWeapons();
                checkEquipment();
                checkSystems();
                checkEngines();
                checkInternal();
            }else{
                mwclient.getRMT().removeAllWorkOrders(unit.getExternalId());
    
                checkInternal();
                checkArmor();
                checkWeapons();
                checkEquipment();
                checkSystems();
                checkEngines();
            }
            
            mwclient.getConfig().saveConfig();
            super.dispose();
            return;
        }
        else if (command.equals(cancelCommand)) {
            super.dispose();
		}
        else{
            try{
                int location = Integer.parseInt(command);
                int techType = ((JComboBox)techBox.getComponent(location)).getSelectedIndex();
                if ( techType == UnitUtils.TECH_PILOT )
                    techType = playerUnit.getPilot().getSkills().getPilotSkill(PilotSkill.AstechSkillID).getLevel();
                ((JSpinner)rollBox.getComponent(location)).setValue(UnitUtils.techBaseRoll(techType));
                
                if ( ((JCheckBox)repairBox.getComponent(location)).isSelected() )
                    setCost(location);
                else
                  ((JLabel)costBox.getComponent(location)).setText("0");
                setTotalCost();
            }
            catch(Exception ex){}
        }
	}
	
    private void loadPanel(){
        String[] techList = null;
        
        Pilot pilot = this.playerUnit.getPilot();
        
        if ( pilot.getSkills().has(PilotSkill.AstechSkillID) ){ 
            techList = new String[]{"Green","Reg","Vet","Elite","Pilot"};
        }
        else
            techList = new String[]{"Green","Reg","Vet","Elite"};
        
        
        repairBox.add(new JLabel("Repair Type"));
        techBox.add(new JLabel("Tech Type",SwingConstants.LEFT));
        rollBox.add(new JLabel("Base Roll",SwingConstants.LEFT));
        costBox.add(new JLabel("Cost",SwingConstants.LEFT));
        if ( isSimple() )
            timeBox.add(new JLabel("Time",SwingConstants.LEFT));


        for ( int x= 0; x< 6; x++){
            techComboBox = new JComboBox(techList);
            techComboBox.addActionListener(this);
            techComboBox.setActionCommand(Integer.toString(x+1));
            techComboBox.addKeyListener(this);
            techBox.add(techComboBox);
            baseRollEditor = new SpinnerNumberModel();
            baseRollEditor.setMaximum(12);
            baseRollEditor.setMinimum(3);
            baseRollEditor.setStepSize(1);
            baseRollEditor.setValue(8);
            baseRollField = new JSpinner(baseRollEditor);
            baseRollField.addKeyListener(this);
            baseRollField.addChangeListener(this);
            baseRollField.setName(Integer.toString(x+1));
            baseRollField.setEnabled(!isSalvage());
            rollBox.add(baseRollField);
            costField = new JLabel("0");
            costField.setAlignmentX(Component.CENTER_ALIGNMENT);
            costField.setPreferredSize(new Dimension(50,100));
            costField.setToolTipText("This is an estimated cost based on tech type and base roll");
            costBox.add(costField);
            timeField = new JLabel("0",SwingConstants.LEFT);
            timeField.setPreferredSize(new Dimension(50,100));
            timeField.setToolTipText("This is an estimated time based on tech type and base roll");
            timeBox.add(timeField);
        }
        
        repairCB = new JCheckBox("Armor");
        repairCB.addKeyListener(this);
        repairCB.addActionListener(this);
        repairCB.setActionCommand(Integer.toString(ARMOR));
        repairCB.setSelected(!isBulk());
        repairCB.setEnabled(!isSimple());
        repairBox.add(repairCB);

        repairCB = new JCheckBox("Structure");
        repairCB.addKeyListener(this);
        repairCB.addActionListener(this);
        repairCB.setActionCommand(Integer.toString(INTERNAL));
        repairCB.setSelected(!isBulk());
        repairCB.setEnabled(!isSimple());
        repairBox.add(repairCB);

        repairCB = new JCheckBox("Weapons");
        repairBox.add(repairCB);
        repairCB.addKeyListener(this);
        repairCB.setActionCommand(Integer.toString(WEAPONS));
        repairCB.addActionListener(this);
        repairCB.setSelected(!isBulk());
        repairCB.setEnabled(!isSimple());

        repairCB = new JCheckBox("Equipment");
        repairBox.add(repairCB);
        repairCB.addKeyListener(this);
        repairCB.addActionListener(this);
        repairCB.setActionCommand(Integer.toString(EQUIPMENT));
        repairCB.setSelected(!isBulk());
        repairCB.setEnabled(!isSimple());

        repairCB = new JCheckBox("Systems");
        repairBox.add(repairCB);
        repairCB.addKeyListener(this);
        repairCB.addActionListener(this);
        repairCB.setActionCommand(Integer.toString(SYSTEMS));
        repairCB.setSelected(!isBulk());
        repairCB.setEnabled(!isSimple());

        repairCB = new JCheckBox("Engines");
        repairCB.addKeyListener(this);
        repairCB.addActionListener(this);
        repairBox.add(repairCB);
        repairCB.setActionCommand(Integer.toString(ENGINES));
        repairCB.setSelected(!isBulk());
        repairCB.setEnabled(!isSimple());
        
        masterBox.add(repairBox);
        masterBox.add(techBox);
        masterBox.add(rollBox);
        masterBox.add(costBox);
        if ( isSimple() )
            masterBox.add(timeBox);

        blankPanel1.add(new JLabel(" "));
        blankPanel2.add(new JLabel(" "));
        
        costField = new JLabel("0");
        costField.setToolTipText("This is an estimated cost based on tech type and base roll");
        totalPanel.add(costField);
        totalTextPanel.add(new JLabel("Estimated Total:") );

        masterBox.add(blankPanel1);
        masterBox.add(blankPanel2);
        masterBox.add(totalTextPanel);
        masterBox.add(totalPanel);
        if ( isSimple() ){
            timeField = new JLabel("0");
            timePanel.add(timeField);
            masterBox.add(timePanel);
        }

        if ( isSimple() )
            SpringLayoutHelper.setupSpringGrid(masterBox,5);
        else
            SpringLayoutHelper.setupSpringGrid(masterBox,4);
        MasterPanel.add(masterBox);
        
        if ( isSimple() ){
            for ( int type = ARMOR; type <= ENGINES; type++)
                setCost(type);
            setTotalCost();
        }
    }
    
    public void setRepair() {
    
    	int tech = 0;
    	int roll = 0;
    	
    	if ( isSalvage() ){
    		setArmorCost();
    		setInternalCost();
    		setSystemCost();
    		setEngineCost();
    		setWeaponCost();
    		setEquipmentCost();
    		setTotalCost();
    		return;
    	}
    	
    	if ( UnitUtils.hasArmorDamage(unit) ) {
    		((JCheckBox)repairBox.getComponent(ARMOR)).setSelected(true);
            tech = Integer.parseInt(mwclient.getConfigParam(("REPAIRARMORTECH")));
            roll = Integer.parseInt(mwclient.getConfigParam(("REPAIRARMORROLL")));
    		((JComboBox)techBox.getComponent(ARMOR)).setSelectedIndex(tech);
    		((JSpinner)rollBox.getComponent(ARMOR)).setValue(roll);
    		setArmorCost();
    	}
    	
    	if (  UnitUtils.hasISDamage(unit)) {
    		((JCheckBox)repairBox.getComponent(INTERNAL)).setSelected(true);
            tech = Integer.parseInt(mwclient.getConfigParam(("REPAIRINTERNALTECH")));
            roll = Integer.parseInt(mwclient.getConfigParam(("REPAIRINTERNALROLL")));
    		((JComboBox)techBox.getComponent(INTERNAL)).setSelectedIndex(tech);
    		((JSpinner)rollBox.getComponent(INTERNAL)).setValue(roll);
    		setInternalCost();
    	}
    		
    	if (UnitUtils.hasCriticalDamage(unit) ) {
            for ( int location = 0; location < unit.locations(); location++ ){
	           for( int slot = 0; slot < unit.getNumberOfCriticals(location); slot++){
	                CriticalSlot cs = unit.getCritical(location,slot);
	                if ( cs == null )
	                    continue;
	                if ( !cs.isBreached() && !cs.isDamaged() )
	                    continue;
	                if (cs.getType() == CriticalSlot.TYPE_EQUIPMENT) {
	                    Mounted mounted = unit.getEquipment(cs.getIndex());
	                    
	                    if ( !mounted.isDestroyed() && !mounted.isMissing() )
	                    	continue;
	                    
	                    //Only want to set a Tech to work on the Mounted object that is destroyed don't need to
	                    //add multiple techs to a single weapon
	                    if ( mounted.getType() instanceof WeaponType ){
	                		((JCheckBox)repairBox.getComponent(WEAPONS)).setSelected(true);
	                        tech = Integer.parseInt(mwclient.getConfigParam(("REPAIRWEAPONSTECH")));
	                        roll = Integer.parseInt(mwclient.getConfigParam(("REPAIRWEAPONSROLL")));
	                		((JComboBox)techBox.getComponent(WEAPONS)).setSelectedIndex(tech);
	                		((JSpinner)rollBox.getComponent(WEAPONS)).setValue(roll);
	                		setWeaponCost();
	                    }
		                else if ( !(mounted.getType() instanceof WeaponType ) ){
		                	((JCheckBox)repairBox.getComponent(EQUIPMENT)).setSelected(true);
	                        tech = Integer.parseInt(mwclient.getConfigParam(("REPAIREQUIPMENTTECH")));
	                        roll = Integer.parseInt(mwclient.getConfigParam(("REPAIREQUIPMENTROLL")));
	                		((JComboBox)techBox.getComponent(EQUIPMENT)).setSelectedIndex(tech);
	                		((JSpinner)rollBox.getComponent(EQUIPMENT)).setValue(roll);
	                		setEquipmentCost();
	                    }
	                }
	                else if (cs.getType() == CriticalSlot.TYPE_SYSTEM && cs.getIndex() != Mech.SYSTEM_ENGINE) {
                		((JCheckBox)repairBox.getComponent(SYSTEMS)).setSelected(true);
                        tech = Integer.parseInt(mwclient.getConfigParam(("REPAIRSYSTEMSTECH")));
                        roll = Integer.parseInt(mwclient.getConfigParam(("REPAIRSYSTEMSROLL")));
                		((JComboBox)techBox.getComponent(SYSTEMS)).setSelectedIndex(tech);
                		((JSpinner)rollBox.getComponent(SYSTEMS)).setValue(roll);
                		setSystemCost();
	                }
	                else if ( UnitUtils.isEngineCrit(cs) ) {
                		((JCheckBox)repairBox.getComponent(ENGINES)).setSelected(true);
                        tech = Integer.parseInt(mwclient.getConfigParam(("REPAIRENGINESTECH")));
                        roll = Integer.parseInt(mwclient.getConfigParam(("REPAIRENGINESROLL")));
                		((JComboBox)techBox.getComponent(ENGINES)).setSelectedIndex(tech);
                		((JSpinner)rollBox.getComponent(ENGINES)).setValue(roll);
                		setEngineCost();
	                }
	            }
            }
    	}
    	setTotalCost();
    }
    
    /**
     * This method sets the cost field with the cost of the repair
     * based on the crit and the tech doing the job.
     *
     */
    public void setCost(int repairType){

        if ( repairType == ARMOR ){
            setArmorCost();
            return;
        }
            
        if ( repairType == INTERNAL ){
            setInternalCost();
            return;
        }
        
        if ( repairType == SYSTEMS ){
            setSystemCost();
            return;
        }
        
        if ( repairType == ENGINES ){
            setEngineCost();
            return;
        }
        
        if ( repairType == WEAPONS ){
            setWeaponCost();
            return;
        }
        
        if ( repairType == EQUIPMENT ){
            setEquipmentCost();
            return;
        }
        
    }
    
    public void keyTyped(KeyEvent arg0) {
    }

    public void keyPressed(KeyEvent arg0) {
    }

    public void keyReleased(KeyEvent arg0) {
        
        if ( arg0.getKeyCode() == KeyEvent.VK_ESCAPE ){
            super.dispose();
        }
    }
    
    public void stateChanged(ChangeEvent arg0) {
        
        int location = ARMOR;
        
        location = Integer.parseInt(((JSpinner)arg0.getSource()).getName());
  
        if ( ((JCheckBox)repairBox.getComponent(location)).isSelected() ){
            //System.err.println("stateChanged Location: "+location);
            setCost(location);
            setTotalCost();
        }
    }
    
    private void checkArmor(){
        //check to see if the checked the box
        if ( !((JCheckBox)repairBox.getComponent(ARMOR)).isSelected() )
            return;
        
        int techType = ((JComboBox)techBox.getComponent(ARMOR)).getSelectedIndex();
        String baseRoll = ((JSpinner)rollBox.getComponent(ARMOR)).getValue().toString();
        
        if ( isBulk() && techType != UnitUtils.TECH_PILOT ) {
	        mwclient.getConfig().setParam("REPAIRARMORTECH", Integer.toString(techType));
	        mwclient.getConfig().setParam("REPAIRARMORROLL", baseRoll);
        }

        for ( int location = 0; location < unit.locations(); location++ ){
        	if ( isSalvage() ){
	            if ( unit.getArmor(location) > 0 ){
	                String workOrder = unit.getExternalId()+"#"+location+"#"+UnitUtils.LOC_FRONT_ARMOR;
	                mwclient.getSMT().addWorkOrder(techType,workOrder);
	            }
	            if ( unit.hasRearArmor(location) && unit.getArmor(location,true) > 0 ){
	                String workOrder = unit.getExternalId()+"#"+(location)+"#"+UnitUtils.LOC_REAR_ARMOR;
	                mwclient.getSMT().addWorkOrder(techType,workOrder);
	            }
        	}
        	else{
	            if ( unit.getArmor(location) < unit.getOArmor(location) ){
	                String workOrder = unit.getExternalId()+"#"+location+"#"+UnitUtils.LOC_FRONT_ARMOR+"#"+baseRoll+"#999";
	                mwclient.getRMT().addWorkOrder(techType,workOrder);
	            }
	            if ( unit.hasRearArmor(location) && unit.getArmor(location,true) < unit.getOArmor(location,true) ){
	                String workOrder = unit.getExternalId()+"#"+(location+7)+"#"+UnitUtils.LOC_REAR_ARMOR+"#"+baseRoll+"#999";
	                mwclient.getRMT().addWorkOrder(techType,workOrder);
	            }
        	}
                
        }
        
    }

    private void checkInternal(){
        //check to see if the checked the box
        if ( !((JCheckBox)repairBox.getComponent(INTERNAL)).isSelected() )
            return;
        
        int techType = ((JComboBox)techBox.getComponent(INTERNAL)).getSelectedIndex();
        String baseRoll = ((JSpinner)rollBox.getComponent(INTERNAL)).getValue().toString();

        if ( isBulk() &&  techType != UnitUtils.TECH_PILOT ) {
        	mwclient.getConfig().setParam("REPAIRINTERNALTECH", Integer.toString(techType));
        	mwclient.getConfig().setParam("REPAIRINTERNALROLL", baseRoll);
        }
        
        for ( int location = 0; location < unit.locations(); location++ ){
        	
        	if ( isSalvage() ){
        		if ( unit.getInternal(location) > 0 ){
	                String workOrder = unit.getExternalId()+"#"+location+"#"+UnitUtils.LOC_INTERNAL_ARMOR+"#";
	                mwclient.getSMT().addWorkOrder(techType,workOrder);
        		}
        	}
        	else if ( unit.getInternal(location) < unit.getOInternal(location) ){
                String workOrder = unit.getExternalId()+"#"+location+"#"+UnitUtils.LOC_INTERNAL_ARMOR+"#"+baseRoll+"#999";
                mwclient.getRMT().addWorkOrder(techType,workOrder);
            }
        }
    }
    
    private void checkEngines(){
        //check to see if the checked the box
        if ( !((JCheckBox)repairBox.getComponent(ENGINES)).isSelected() )
            return;
    
        //No damaged engines no reason to keep going.
        if ( UnitUtils.getNumberOfDamagedEngineCrits(unit) < 1)
            return;
        
        int techType = ((JComboBox)techBox.getComponent(ENGINES)).getSelectedIndex();
        String baseRoll = ((JSpinner)rollBox.getComponent(ENGINES)).getValue().toString();
        
        if ( isBulk() && techType != UnitUtils.TECH_PILOT ) {
	        mwclient.getConfig().setParam("REPAIRENGINESTECH", Integer.toString(techType));
	        mwclient.getConfig().setParam("REPAIRENGINESROLL", baseRoll);
        }        
        for ( int location = UnitUtils.LOC_CT; location <= UnitUtils.LOC_LT;location++ ){
            for ( int slot = 0; slot < unit.locations(); slot++ ){
                CriticalSlot cs = unit.getCritical(location,slot);
                //make sure it a viable slot
                if ( cs == null )
                    continue;
                //check for engine slot
                if ( !UnitUtils.isEngineCrit(cs) )
                    continue;
                //check its damaged
                if ( isSalvage() ){
                	if ( !cs.isDamaged()){
	                    String workOrder = unit.getExternalId()+"#"+location+"#"+slot;
	                    mwclient.getSMT().addWorkOrder(techType,workOrder);
	                    return;
                	}
                }else{
                	if ( !cs.isDamaged() && !cs.isBreached() )
                		continue;

	                //ok we have a damaged engine slot lets queue up the repair and exit.
	                String workOrder = unit.getExternalId()+"#"+location+"#"+slot+"#"+baseRoll+"#999";
	                mwclient.getRMT().addWorkOrder(techType,workOrder);
	                return;
                }
            }
        }
    }
    
    private void checkSystems(){
        //check to see if the checked the box
        if ( !((JCheckBox)repairBox.getComponent(SYSTEMS)).isSelected() )
            return;
        
        int techType = ((JComboBox)techBox.getComponent(SYSTEMS)).getSelectedIndex();
        String baseRoll = ((JSpinner)rollBox.getComponent(SYSTEMS)).getValue().toString();
        if ( !isSalvage() && techType != UnitUtils.TECH_PILOT ) {
	        mwclient.getConfig().setParam("REPAIRSYSTEMSTECH", Integer.toString(techType));
	        mwclient.getConfig().setParam("REPAIRSYSTEMSROLL", baseRoll);
        }
        
        for ( int location = 0; location < unit.locations(); location++ ){
            for( int slot = 0; slot < unit.getNumberOfCriticals(location); slot++){
                CriticalSlot cs = unit.getCritical(location,slot);
                if ( cs == null )
                    continue;
                if ( isSalvage() ){
                        if ( !cs.isDamaged()
                        && cs.getType() == CriticalSlot.TYPE_SYSTEM 
                        && cs.getIndex() != Mech.SYSTEM_ENGINE){
	                        String workOrder = unit.getExternalId()+"#"+location+"#"+slot;
	                        mwclient.getSMT().addWorkOrder(techType,workOrder);
                        }
                }else{ 
	                if ( !cs.isBreached() && !cs.isDamaged() )
	                    continue;
	                if (cs.getType() == CriticalSlot.TYPE_SYSTEM && cs.getIndex() != Mech.SYSTEM_ENGINE) {
	                    String workOrder = unit.getExternalId()+"#"+location+"#"+slot+"#"+baseRoll+"#999";
	                    mwclient.getRMT().addWorkOrder(techType,workOrder);
	                }
                }
            }
        }
    }
    
    private void checkWeapons(){
        //check to see if the checked the box
        if ( !((JCheckBox)repairBox.getComponent(WEAPONS)).isSelected() )
            return;
        
        int techType = ((JComboBox)techBox.getComponent(WEAPONS)).getSelectedIndex();
        String baseRoll = ((JSpinner)rollBox.getComponent(WEAPONS)).getValue().toString();

        if ( !isSalvage() && techType != UnitUtils.TECH_PILOT ) {
	        mwclient.getConfig().setParam("REPAIRWEAPONSTECH", Integer.toString(techType));
	        mwclient.getConfig().setParam("REPAIRWEAPONSROLL", baseRoll);
        }
        
        for ( int location = 0; location < unit.locations(); location++ ){
            Mounted lastWeapon = null;
            for( int slot = 0; slot < unit.getNumberOfCriticals(location); slot++){
                CriticalSlot cs = unit.getCritical(location,slot);
                if ( cs == null )
                    continue;
                if ( !cs.isBreached() && !cs.isDamaged() )
                    continue;
                if (cs.getType() == CriticalSlot.TYPE_EQUIPMENT) {
                    Mounted mounted = unit.getEquipment(cs.getIndex());
                    //Only want to set a Tech to work on the Mounted object that is destroyed don't need to
                    //add multiple techs to a single weapon
                    if ( isSalvage() ){
                    		if ( mounted.getType() instanceof WeaponType 
                            && !mounted.isDestroyed() 
                            && !mounted.isMissing()
                            && !mounted.equals(lastWeapon)){
	                    		lastWeapon = mounted;
		                        String workOrder = unit.getExternalId()+"#"+location+"#"+slot;
		                        mwclient.getSMT().addWorkOrder(techType,workOrder);
                    		}
                    }else if ( mounted.getType() instanceof WeaponType 
                            && (mounted.isDestroyed() || mounted.isMissing()) 
                            && !mounted.equals(lastWeapon) ){
                        lastWeapon = mounted;
                        if ( isSalvage() ){
	                        String workOrder = unit.getExternalId()+"#"+location+"#"+slot;
	                        mwclient.getSMT().addWorkOrder(techType,workOrder);
                        }else{
                            String workOrder = unit.getExternalId()+"#"+location+"#"+slot+"#"+baseRoll+"#999";
                            mwclient.getRMT().addWorkOrder(techType,workOrder);
                        }
                    }
                }
            }
        }
    }
    
    private void checkEquipment(){
        //check to see if the checked the box
        if ( !((JCheckBox)repairBox.getComponent(EQUIPMENT)).isSelected() )
            return;
        
        int techType = ((JComboBox)techBox.getComponent(EQUIPMENT)).getSelectedIndex();
        String baseRoll = ((JSpinner)rollBox.getComponent(EQUIPMENT)).getValue().toString();

        if ( isBulk() && techType != UnitUtils.TECH_PILOT ) {
        	mwclient.getConfig().setParam("REPAIREQUIPMENTTECH", Integer.toString(techType));
	        mwclient.getConfig().setParam("REPAIREQUIPMENTROLL", baseRoll);
        }
        
        for ( int location = 0; location < unit.locations(); location++ ){
            Mounted lastEq = null;
            for( int slot = 0; slot < unit.getNumberOfCriticals(location); slot++){
                CriticalSlot cs = unit.getCritical(location,slot);
                if ( cs == null )
                    continue;
                if ( !cs.isBreached() && !cs.isDamaged() )
                    continue;
                
                if (cs.getType() == CriticalSlot.TYPE_EQUIPMENT) {
                    Mounted mounted = unit.getEquipment(cs.getIndex());
                    
                    //Only want to set a Tech to work on the Mounted object that is destroyed don't need to
                    //add multiple techs to a single piece of equipment
                    if ( isSalvage() ){
                    		if ( !(mounted.getType() instanceof WeaponType) 
                            && !mounted.isDestroyed() 
                            && !mounted.isMissing()
                            && !mounted.equals(lastEq)){
	                    		lastEq = mounted;
		                        String workOrder = unit.getExternalId()+"#"+location+"#"+slot;
		                        mwclient.getSMT().addWorkOrder(techType,workOrder);
                    		}
                    }else if ( !(mounted.getType() instanceof WeaponType) 
                            && (mounted.isDestroyed() || mounted.isMissing())
                            && !mounted.equals(lastEq)){
                        lastEq = mounted;
                        
                        String workOrder = unit.getExternalId()+"#"+location+"#"+slot+"#"+baseRoll+"#999";
                        mwclient.getRMT().addWorkOrder(techType,workOrder);
                    }
                }
            }
        }
    }
    
    private void setArmorCost(){

        int techType = ((JComboBox)techBox.getComponent(ARMOR)).getSelectedIndex();
        int baseRoll = Integer.parseInt(((JSpinner)rollBox.getComponent(ARMOR)).getValue().toString());
        double pointsToRepair = 0;
        double armorCost = CUnit.getArmorCost(unit,mwclient); 
        double techCost = 0;
        double techWorkMod = 0;
        double cost = 0;
        boolean clear = true;
        
        if ( techType != UnitUtils.TECH_PILOT ){ 
            techCost = Integer.parseInt(mwclient.getserverConfigs(UnitUtils.techDescription(techType)+"TechRepairCost"));
            techWorkMod = UnitUtils.getTechRoll(unit,0,UnitUtils.LOC_FRONT_ARMOR,techType,true,this.mwclient.getData().getHouseByName(mwclient.getPlayer().getHouse()).getTechLevel()) - baseRoll;
        }else
            techType = playerUnit.getPilot().getSkills().getPilotSkill(PilotSkill.AstechSkillID).getLevel();

        
        techWorkMod = Math.max(techWorkMod,0);
        
        for ( int location = 0; location < unit.locations(); location++ ){
        	
        	if ( isSalvage() ){
	            if ( unit.getArmor(location) > 0 ){
	                cost += techCost;
	                setWorkHours(ARMOR,location,0,true,clear);
	                clear = false;
	            }
	                
	            if ( unit.hasRearArmor(location) && unit.getArmor(location,true) > 0){
	                cost += techCost;
	                setWorkHours(ARMOR,location,0,true,clear);
	                clear = false;
	            }
        	}else{
	            if ( unit.getArmor(location) < unit.getOArmor(location) ){
	                pointsToRepair += unit.getOArmor(location) - unit.getArmor(location);
	                cost += armorCost*pointsToRepair;
	                cost += techCost*Math.abs(techWorkMod);
	                cost += techCost;
	                setWorkHours(ARMOR,location,0,true,clear);
	                clear = false;
	            }
	                
	            if ( unit.hasRearArmor(location) ){
	                pointsToRepair += unit.getOArmor(location,true) - unit.getArmor(location,true);
	                cost += armorCost*pointsToRepair;
	                cost += techCost*Math.abs(techWorkMod);
	                cost += techCost;
	                setWorkHours(ARMOR,location,0,true,clear);
	                clear = false;
	            }
        	}
        }
        
        //Base on what they assigned as the base roll we increase the payout so
        //that it covers the chances of failures. not the greatest but better then nothing.
        if ( !isSalvage() )
        	cost *= payOutIncreaseBasedOnRoll(baseRoll);
        cost = Math.max(0,cost);
        
        ((JLabel)costBox.getComponent(ARMOR)).setText(Integer.toString((int)cost));
    }
    
    private void setInternalCost(){

        int techType = ((JComboBox)techBox.getComponent(INTERNAL)).getSelectedIndex();
        int baseRoll = Integer.parseInt(((JSpinner)rollBox.getComponent(INTERNAL)).getValue().toString());
        double pointsToRepair = 0;
        double armorCost = CUnit.getStructureCost(unit,mwclient); 
        double techCost = 0;
        double techWorkMod = 0;
        double cost = 0;
        boolean clear = true;
        
        if ( techType != UnitUtils.TECH_PILOT ){ 
            techCost = Integer.parseInt(mwclient.getserverConfigs(UnitUtils.techDescription(techType)+"TechRepairCost"));
        }
        
        for ( int location = 0; location < unit.locations(); location++ ){
        	
        	if ( isSalvage() && unit.getInternal(location) > 0){
        		cost += techCost;
                setWorkHours(INTERNAL,location,UnitUtils.LOC_INTERNAL_ARMOR,true,clear);
                clear = false;
        	}else{
	            if ( unit.getInternal(location) < unit.getOInternal(location) ){
	                if ( techType != UnitUtils.TECH_PILOT ){ 
	                    techWorkMod = UnitUtils.getTechRoll(unit,location,UnitUtils.LOC_INTERNAL_ARMOR,techType,true,this.mwclient.getData().getHouseByName(mwclient.getPlayer().getHouse()).getTechLevel()) - baseRoll;
	                }
	                
	                techWorkMod = Math.max(techWorkMod,0);
	                pointsToRepair = unit.getOInternal(location) - unit.getInternal(location);
	                cost +=  armorCost*pointsToRepair;
	                cost += techCost*Math.abs(techWorkMod);
	                cost += techCost;
	                setWorkHours(INTERNAL,location,UnitUtils.LOC_INTERNAL_ARMOR,true,clear);
	                clear = false;
	
	            }
        	}
        }
        
        //Base on what they assigned as the base roll we increase the payout so
        //that it covers the chances of failures. not the greatest but better then nothing.
        if ( !isSalvage() )
        	cost *= payOutIncreaseBasedOnRoll(baseRoll);
        cost = Math.max(0,cost);
        
        ((JLabel)costBox.getComponent(INTERNAL)).setText(Integer.toString((int)cost));
    }
    
    private void setSystemCost(){

        int techType = ((JComboBox)techBox.getComponent(SYSTEMS)).getSelectedIndex();
        int baseRoll = Integer.parseInt(((JSpinner)rollBox.getComponent(SYSTEMS)).getValue().toString());
        double pointsToRepair = 0;
        double critCost = 0; 
        double techCost = 0;
        double techWorkMod = 0;
        double cost = 0;
        boolean clear = true;
        
        if ( techType != UnitUtils.TECH_PILOT ){ 
            techCost = Integer.parseInt(mwclient.getserverConfigs(UnitUtils.techDescription(techType)+"TechRepairCost"));
        }
        
        for ( int location = 0; location < unit.locations(); location++ ){
            for( int slot = 0; slot < unit.getNumberOfCriticals(location); slot++){
                CriticalSlot cs = unit.getCritical(location,slot);
                if ( cs == null )
                    continue;
            	if ( isSalvage() && !cs.isDamaged()){
        			cost += techCost*2;
        			clear = false;
        			continue;
            	}

                if ( !cs.isBreached() && !cs.isDamaged() )
                    continue;
                if (cs.getType() == CriticalSlot.TYPE_SYSTEM && cs.getIndex() != Mech.SYSTEM_ENGINE) {
                    if ( techType != UnitUtils.TECH_PILOT ){ 
                        techWorkMod = UnitUtils.getTechRoll(unit,location,slot,techType,true,this.mwclient.getData().getHouseByName(mwclient.getPlayer().getHouse()).getTechLevel()) - baseRoll;
                    }
                    
                    critCost = CUnit.getCritCost(unit,mwclient,cs);
                    techWorkMod = Math.max(techWorkMod,0);
                    pointsToRepair = UnitUtils.getNumberOfCrits(unit,cs);
                    critCost += techCost;
                    cost += critCost*pointsToRepair;
                    cost += techCost*Math.abs(techWorkMod);
                    cost += techCost;
                    setWorkHours(SYSTEMS,location,slot,false,clear);
                    clear = false;

                    //move the slot ahead if the Crit is more then 1 in size.
                    slot += pointsToRepair-1;
                }
            }
        }

        //Base on what they assigned as the base roll we increase the payout so
        //that it covers the chances of failures. not the greatest but better then nothing.
        if ( !isSalvage() )
        	cost *= payOutIncreaseBasedOnRoll(baseRoll);
        cost = Math.max(0,cost);
        
        ((JLabel)costBox.getComponent(SYSTEMS)).setText(Integer.toString((int)cost));
    }
    
    private void setWeaponCost(){

        int techType = ((JComboBox)techBox.getComponent(WEAPONS)).getSelectedIndex();
        int baseRoll = Integer.parseInt(((JSpinner)rollBox.getComponent(WEAPONS)).getValue().toString());
        double pointsToRepair = 0;
        double critCost = 0; 
        double techCost = 0;
        double techWorkMod = 0;
        double cost = 0;
        boolean clear = true;
        
        if ( techType != UnitUtils.TECH_PILOT ){ 
            techCost = Integer.parseInt(mwclient.getserverConfigs(UnitUtils.techDescription(techType)+"TechRepairCost"));
        }
        
        for ( int location = 0; location < unit.locations(); location++ ){
            for( int slot = 0; slot < unit.getNumberOfCriticals(location); slot++){
                CriticalSlot cs = unit.getCritical(location,slot);
                if ( cs == null )
                    continue;
            	if ( isSalvage() && !cs.isDamaged()){
        			cost += techCost*2;
        			clear = false;
        			continue;
            	}
                if ( !cs.isBreached() && !cs.isDamaged() )
                    continue;
                if (cs.getType() == CriticalSlot.TYPE_EQUIPMENT) {
                    Mounted mounted = unit.getEquipment(cs.getIndex());
                    
                    if ( mounted.getType() instanceof WeaponType ){
                        if ( techType != UnitUtils.TECH_PILOT ){ 
                            techWorkMod = UnitUtils.getTechRoll(unit,location,slot,techType,true,this.mwclient.getData().getHouseByName(mwclient.getPlayer().getHouse()).getTechLevel()) - baseRoll;
                        }
                        
                        critCost = CUnit.getCritCost(unit,mwclient,cs);
                        techWorkMod = Math.max(techWorkMod,0);
                        pointsToRepair = UnitUtils.getNumberOfCrits(unit,cs);
                        critCost += techCost;
                        cost += critCost*pointsToRepair;
                        cost += techCost*Math.abs(techWorkMod);
                        cost += techCost;
                        setWorkHours(WEAPONS,location,slot,false,clear);
                        clear = false;

                        //move the slot ahead if the Crit is more then 1 in size.
                        slot += pointsToRepair-1;
                    }
                }
            }
        }
        
        //Base on what they assigned as the base roll we increase the payout so
        //that it covers the chances of failures. not the greatest but better then nothing.
        if ( !isSalvage() )
        	cost *= payOutIncreaseBasedOnRoll(baseRoll);
        cost = Math.max(0,cost);
        
        ((JLabel)costBox.getComponent(WEAPONS)).setText(Integer.toString((int)cost));
    }
    
    private void setEquipmentCost(){

        int techType = ((JComboBox)techBox.getComponent(EQUIPMENT)).getSelectedIndex();
        int baseRoll = Integer.parseInt(((JSpinner)rollBox.getComponent(EQUIPMENT)).getValue().toString());
        double pointsToRepair = 0;
        double critCost = 0; 
        double techCost = 0;
        double techWorkMod = 0;
        double cost = 0;
        boolean clear = true;
        
        if ( techType != UnitUtils.TECH_PILOT ){ 
            techCost = Integer.parseInt(mwclient.getserverConfigs(UnitUtils.techDescription(techType)+"TechRepairCost"));
        }
        
        for ( int location = 0; location < unit.locations(); location++ ){
            for( int slot = 0; slot < unit.getNumberOfCriticals(location); slot++){
                CriticalSlot cs = unit.getCritical(location,slot);
                if ( cs == null )
                    continue;
            	if ( isSalvage() && !cs.isDamaged()){
        			cost += techCost*2;
        			clear = false;
        			continue;
            	}

                if ( !cs.isBreached() && !cs.isDamaged() )
                    continue;
                if (cs.getType() == CriticalSlot.TYPE_EQUIPMENT) {
                    Mounted mounted = unit.getEquipment(cs.getIndex());
                    
                    if ( !(mounted.getType() instanceof WeaponType) ){
                        if ( techType != UnitUtils.TECH_PILOT ){ 
                            techWorkMod = UnitUtils.getTechRoll(unit,location,slot,techType,true,this.mwclient.getData().getHouseByName(mwclient.getPlayer().getHouse()).getTechLevel()) - baseRoll;
                        }
                        
                        critCost = CUnit.getCritCost(unit,mwclient,cs);
                        techWorkMod = Math.max(techWorkMod,0);
                        pointsToRepair = UnitUtils.getNumberOfCrits(unit,cs);
                        critCost += techCost;
                        cost += critCost*pointsToRepair;
                        cost += techCost*Math.abs(techWorkMod);
                        cost += techCost;
                        setWorkHours(EQUIPMENT,location,slot,false,clear);
                        clear = false;
                        //move the slot ahead if the Crit is more then 1 in size.
                        slot += pointsToRepair-1;
                    }
                }
            }
        }
        
        //Base on what they assigned as the base roll we increase the payout so
        //that it covers the chances of failures. not the greatest but better then nothing.
        if ( !isSalvage() )
        	cost *= payOutIncreaseBasedOnRoll(baseRoll);
        cost = Math.max(0,cost);
        
        ((JLabel)costBox.getComponent(EQUIPMENT)).setText(Integer.toString((int)cost));
    }
    
    private void setEngineCost(){

        int techType = ((JComboBox)techBox.getComponent(ENGINES)).getSelectedIndex();
        int baseRoll = Integer.parseInt(((JSpinner)rollBox.getComponent(ENGINES)).getValue().toString());
        double pointsToRepair = 0;
        double critCost = 0; 
        double techCost = 0;
        double techWorkMod = 0;
        double cost = 0;
        boolean found = false;
        int location = 0, slot = 0;
        CriticalSlot cs = null;

        if ( techType != UnitUtils.TECH_PILOT ){ 
            techCost = Integer.parseInt(mwclient.getserverConfigs(UnitUtils.techDescription(techType)+"TechRepairCost"));
        }
        
            
        top_loop: for ( int x= UnitUtils.LOC_CT; x <= UnitUtils.LOC_LT; x++){
            for ( int y = 0; y < unit.getNumberOfCriticals(x); y++ ){
                cs = unit.getCritical(x,y);
                
                if ( cs == null )
                    continue;
                
                if ( !UnitUtils.isEngineCrit(cs) )
                    continue;
                
            	if ( isSalvage() ){
        			int totalCrits = UnitUtils.getNumberOfCrits(unit,cs) - UnitUtils.getNumberOfDamagedCrits(unit, slot, location, false);
        			int totalCost = (int)(totalCrits*techCost);
        			totalCost += techCost;
        			cost = Math.max(1,totalCost);
        			((JLabel)costBox.getComponent(ENGINES)).setText(Integer.toString((int)cost));
        			return;
            	}

                if ( !cs.isDamaged() && !cs.isBreached() )
                    continue;
                
                location = x;
                slot = y;
                found = true;
                break top_loop;
                
            }
        }
        
        if ( techType != UnitUtils.TECH_PILOT ){ 
            techWorkMod = UnitUtils.getTechRoll(unit,location,slot,techType,true,this.mwclient.getData().getHouseByName(mwclient.getPlayer().getHouse()).getTechLevel()) - baseRoll;
        }
        
        critCost = CUnit.getCritCost(unit,mwclient,cs);
        techWorkMod = Math.max(techWorkMod,0);
        pointsToRepair = UnitUtils.getNumberOfCrits(unit,cs);
        //System.err.println("critCost: "+critCost+" techWorkMod: "+techWorkMod+" pointsToRepair: "+pointsToRepair+" techCost: "+techCost);
        critCost += techCost;
        cost += critCost*pointsToRepair;
        cost += techCost*Math.abs(techWorkMod);
        cost += techCost;
        //System.err.println("Cost 1: "+cost);

        //Base on what they assigned as the base roll we increase the payout so
        //that it covers the chances of failures. not the greatest but better then nothing.
        cost *= payOutIncreaseBasedOnRoll(baseRoll);
        cost = Math.max(0,cost);
        //System.err.println("Cost 2: "+cost);
        if ( !found ){
            cost = 0;
        }
        if ( isSimple() ){
            ((JLabel)timeBox.getComponent(ENGINES)).setText("0");
            setWorkHours(ENGINES,UnitUtils.LOC_CT,0,false,true);
        }
        ((JLabel)costBox.getComponent(ENGINES)).setText(Integer.toString((int)cost));
    }
    
    private double payOutIncreaseBasedOnRoll(int roll){
        if (roll <= 2) {
            return 1.0;
        } else if (roll > 12) {
            return 36.0;
        }
        final double[] payout = { 1.0, 1.0, 1.0, 1.03, 1.09, 1.20, 1.38,
                1.72, 2.40, 3.60, 5.92, 12.0, 36.0 };
        return payout[roll];
    }
    
    private void setWorkHours(int type, int critLocation, int critSlot,
            boolean armor, boolean clear) {

        int techType = ((JComboBox) techBox.getComponent(type))
                .getSelectedIndex();
        int baseRoll = Integer.parseInt(((JSpinner) rollBox.getComponent(type))
                .getValue().toString());

        if (critLocation < 0 || critSlot < 0)
            return;

        int baseLine = Integer.parseInt(mwclient.getserverConfigs("TimeForEachRepairPoint"));

        if (!armor) {
            CriticalSlot cs = unit.getCritical(critLocation, critSlot);
            int totalCrits = UnitUtils.getNumberOfCrits(unit, cs);
            baseLine *= totalCrits;
        }

        int rolls = UnitUtils.getTechRoll(unit, critLocation, critSlot,
                techType, armor,this.mwclient.getData().getHouseByName(mwclient.getPlayer().getHouse()).getTechLevel()) - baseRoll;

        for ( int count = 0; count < rolls; count++)
            baseLine *= 2;

        baseLine = (int)(baseLine * payOutIncreaseBasedOnRoll(baseRoll));

        JLabel textField = (JLabel) timeBox.getComponent(type);
        if (!clear)
            baseLine += Integer.parseInt(textField.getText());
        textField.setText(Integer.toString(baseLine));

    }

    private void setTotalCost(){
        
        int cost = 0;
        int seconds = 0;
        int minutes = 0;
        int hours = 0;
        
        for ( int x = ARMOR; x <= ENGINES; x++ ){
            cost += Integer.parseInt(((JLabel)costBox.getComponent(x)).getText());
            if ( isSimple() )
                seconds += Integer.parseInt(((JLabel)timeBox.getComponent(x)).getText());
        }
        
        if ( isSimple() ){
            if ( seconds > 3600 ){
                hours = seconds/3600;
                seconds %= 3600;
            }
            
            if ( seconds > 60 ){
                minutes = seconds/60;
                seconds %= 60;
            }
            
            StringBuilder text = new StringBuilder();
            StringBuilder toolTip = new StringBuilder();
            
            if ( hours > 0){
                text.append(hours+"h ");
                toolTip.append(hours+" hours ");
            }
            if ( minutes > 0){
                text.append(minutes+"m ");
                toolTip.append(minutes+" minutes ");
            }
            text.append(seconds+"s");
            toolTip.append(seconds+" seconds");
            ((JLabel)timePanel.getComponent(0)).setText(text.toString());
            ((JLabel)timePanel.getComponent(0)).setToolTipText(toolTip.toString());
        }
        ((JLabel)totalPanel.getComponent(0)).setText(Integer.toString(cost));
    }
    
    private boolean isSimple(){
    	return repairType == BulkRepairDialog.TYPE_SIMPLE;
    }
    
    private boolean isBulk(){
    	return repairType == BulkRepairDialog.TYPE_BULK;
    }
    
    private boolean isSalvage(){
    	return repairType == BulkRepairDialog.TYPE_SALVAGE;
    }
}//end BulkRepairDialog.java
