/*
 * MekWars - Copyright (C) 2004 
 * 
 * Original author jtighe (torren)
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
 * CustomUnitDialog.java
 *
 * Created on January 19, 2005
 */

/* 
 * Thanks to the MM guys for the majority of the code we needed for this.
 * 
 * Substantial changes where made to work with the MW code base but the base
 * can be found in megamek.common.CustomMechDialog.java in megamek 0.29.59
 */

package client.gui.dialog;


import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JDialog;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.BoxLayout;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import common.CampaignData;
import common.House;
import common.campaign.pilot.Pilot;
import common.campaign.pilot.skills.PilotSkill;
import common.util.UnitUtils;

import java.awt.Choice;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import megamek.client.Client;
import megamek.common.AmmoType;
import megamek.common.CriticalSlot;
import megamek.common.IOffBoardDirections;
import megamek.common.Mech;
import megamek.common.Infantry;
import megamek.common.MiscType;
import megamek.common.Protomech;
import megamek.common.Mounted;
import megamek.common.Entity;
import megamek.common.Tank;
import megamek.common.WeaponType;
import megamek.common.TechConstants;

import client.MWClient;
import common.util.SpringLayoutHelper;

/**
 * A dialog that a player can use to customize his mech before battle.  
 * Currently, only changing pilots is supported.
 *
 * @author  Ben
 * @version 
 */

public class CustomUnitDialog extends JDialog implements ActionListener{ 

    /**
     * 
     */
    private static final long serialVersionUID = 4035217132830883530L;
    private JLabel labAutoEject = new JLabel("Disable Autoeject", SwingConstants.TRAILING);
    private JCheckBox chAutoEject = new JCheckBox();
    private JLabel labOffBoard = new JLabel("Deploy Offboard", SwingConstants.TRAILING);
    private JCheckBox chOffBoard = new JCheckBox();
    private JLabel labOffBoardDistance = new JLabel("Offboard Distance (Hexes):", SwingConstants.TRAILING);
    private JTextField fldOffBoardDistance = new JTextField(4);
    private JPanel boxPanel;
	
    private JScrollPane scrollPane;
    
    private JPanel panButtons = new JPanel();
    private JButton butOkay = new JButton("Okay");
    private JButton butCancel = new JButton("Cancel");

    private Vector<MunitionChoicePanel> m_vMunitions = new Vector<MunitionChoicePanel>(1,1);
    private JPanel panMunitions = new JPanel();
    
    private Vector<MachineGunChoicePanel> m_vMachineGuns = new Vector<MachineGunChoicePanel>(1,1);
    private JPanel panMachineGuns = new JPanel();
        
    private JLabel labTargSys = new JLabel("Targeting System", SwingConstants.TRAILING);
    private Choice choTargSys = new Choice();

    private JPanel panEdgeSkills = new JPanel();
    private JCheckBox tacCB = new JCheckBox("TAC Rolls");
    private JCheckBox koCB = new JCheckBox("KO Rolls");
    private JCheckBox headHitsCB = new JCheckBox("Head Hit Rolls");
    private JCheckBox explosionsCB = new JCheckBox("Explosion Rolls");
    
    private Entity entity;
    private boolean okay = false;

    private MWClient mwclient;

    private boolean canDump = false;
    private Client mmClient =  new Client("temp","None",0);
    private Pilot pilot = null;
    private boolean usingCrits = false;
    
    /** Creates new CustomMechDialog */
    public CustomUnitDialog(MWClient mwclient, Entity entity, Pilot pilot) {
        super(mwclient.getMainFrame());
        
        this.entity = entity;
        this.mwclient = mwclient;
        this.pilot = pilot;
        this.usingCrits = Boolean.parseBoolean(mwclient.getserverConfigs("UsePartsRepair"));
        
        this.mmClient.game.getOptions().loadOptions();
       	this.setTitle("Customize Unit");
        
        //refresh all ammo data
        this.loadAmmo();
        
        /*
         * Dialog Layout.
         * 
         * Generally speaking, dialog's content pane is a holder for a
         * ScrollPane, which itself wraps around a vertical BoxLayout which
         * holds 3 major sub-panels, and a flowpanel containing Okay/Cancel.
         * 
         * ScrollPane panels are as follows:
         *  - checkboxes and offboard
         *  - ammo loads
         *  - machinegun settings
         */
        this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        
        //scroll pane
        JPanel scrollPanel = new JPanel();
        scrollPanel.setLayout(new BoxLayout(scrollPanel, BoxLayout.Y_AXIS));
        scrollPane = new JScrollPane(scrollPanel);
        
        //add scroll pane and buttons to content pane
        this.getContentPane().add(scrollPane);
        this.getContentPane().add(panButtons);

        //button setup
        butOkay.addActionListener(this);
        butCancel.addActionListener(this);
        this.getRootPane().setDefaultButton(butOkay);
        panButtons.add(butOkay);
        panButtons.add(butCancel);
        /*
         * Build 1st major subpanel - checkboxes.
         */
        int boxRows = 0;
        boxPanel = new JPanel(new SpringLayout());
        
        //only show autoeject for mechs
        if (entity instanceof Mech) {
        	
        	//load mech and set ejection status from entity
        	Mech mech = (Mech)entity;
        	chAutoEject.setSelected(!mech.isAutoEject());
        	
        	//add autoeject label and cbox
        	boxPanel.add(labAutoEject);
        	boxPanel.add(chAutoEject);
        	
        	boxRows += 1;
        }
        
        if ( pilot.getSkills().has(PilotSkill.EdgeSkillID) ){
            setupEdgeSkills();
            boxPanel.add(new JLabel("Edge Selections",SwingConstants.TRAILING));
            boxPanel.add(panEdgeSkills);
            boxRows += 1;
        }
        
        if ( !(UnitUtils.hasTargettingComputer(entity) ) 
                && (mmClient.game.getOptions().booleanOption("allow_level_3_targsys")) 
                && ((entity instanceof Mech) || (entity instanceof Tank))
                && !entity.hasC3()
                && !entity.hasC3i()) {
            
            boxPanel.add(labTargSys);
            for ( int pos = 0; pos < MiscType.targSysNames.length; pos++ ){
                //No TC's or banned weapons systems allowed in the selection box.
                if ( pos == MiscType.T_TARGSYS_TARGCOMP 
                        || mwclient.getData().getBannedTargetingSystems().containsKey(pos) )
                    continue;
                //Else
                choTargSys.add(MiscType.getTargetSysName(pos));
            }
            boxPanel.add(choTargSys);
            choTargSys.select(MiscType.getTargetSysName(entity.getTargSysType()));
            boxRows += 1;
        }

  	//look for offboard weapons by looping through all weapons
        boolean eligibleForOffBoard = false;
        for (Mounted mounted : entity.getWeaponList()) {
            WeaponType wtype = (WeaponType)mounted.getType();
            if (wtype.hasFlag(WeaponType.F_ARTILLERY))
                eligibleForOffBoard = true;
        }
        
        //set up  the offboard box and text field if appropriate
        if (eligibleForOffBoard) {
        	
        	//checkbox for offboard
            boxPanel.add(labOffBoard);
            boxPanel.add(chOffBoard);
            chOffBoard.setSelected(entity.isOffBoard());
            
            //distance
            boxPanel.add(labOffBoardDistance);
            fldOffBoardDistance.setText(Integer.toString(entity.getOffBoardDistance()));
            boxPanel.add(fldOffBoardDistance);
            
            //rowcount
            boxRows += 2;
        }
        
        //layout the boxpanel. 2 columns. Counted the rows.
        SpringLayoutHelper.setupSpringGrid(boxPanel, 2);
        
        //add the boxPanel to the scroll panel. We know it has contents b/c all units can mount lights.
        scrollPanel.add(boxPanel);
        
        /*
         * Build second major subpanel - muntions;
         * however, only for non-inf units.
         */
        if (!(entity instanceof Infantry)) {
            setupMunitions();
            JPanel centeringPanel = new JPanel();
            centeringPanel.setLayout(new BoxLayout(centeringPanel, BoxLayout.Y_AXIS));
            centeringPanel.add(panMunitions);
            scrollPanel.add(centeringPanel);
            
            //hide the ammo cost in titlebar if no ammo to set
            if (panMunitions.getComponentCount() == 0)
            	 this.setTitle("Customize Unit");
            	
        }
        
        /*
         * Build the third major subpanel - burst MGs - for
         * Meks and Vehicles. No BA/Inf/Proto bursts!
         * 
         * Only doso if the server has enabled "maxtech_burst"
         */
        if ( mmClient.game.getOptions().booleanOption("tacops_burst") && !(entity instanceof Infantry) ) {
            setupMachineGuns();
            scrollPanel.add(panMachineGuns);
        }
        
        //add window listener which hides the window on close.
        addWindowListener(new WindowAdapter() {
        	@Override
			public void windowClosing(WindowEvent e) {setVisible(false);}
        });

        //scrollPane.setMinimumSize(new Dimension(150, 150));
        //scrollPane.setMaximumSize(new Dimension(780, 580));
        pack();
        
        setResizable(false);
        setLocationRelativeTo(null);
    }
    
    private void setupMunitions() {
        
    	int munitionsRows = 0;
        panMunitions.setLayout(new SpringLayout());
        MunitionChoicePanel mcp = null;//replaced repeatedly w/i while loop

        //int row = 0;
        int location = -1;//also repeatedly replaced
        
        /*
         * Loop through all ammo?
         */
        Iterator<Mounted> e = entity.getAmmo().iterator();
        while (e.hasNext()) {
        	
            Mounted m = e.next();
            AmmoType at = (AmmoType)m.getType();
            
            Vector<AmmoType> vTypes = new Vector<AmmoType>(1,1);
            Vector<AmmoType> vAllTypes = AmmoType.getMunitionsFor(at.getAmmoType());
            location++;
            
            canDump = mmClient.game.getOptions().booleanOption("lobby_ammo_dump");
            
            if (vAllTypes == null) 
                continue;
            
            //Remove for now Torren and lets see how this works. appears to cause issues with single tech weapons
            //i.e. HGR's and LGR's
            /*if (vAllTypes.size() < 2 && !canDump )
                continue;*/
            
            for (int x = 0, n = vAllTypes.size(); x < n; x++) {
                AmmoType atCheck = vAllTypes.elementAt(x);
                boolean bTechMatch = TechConstants.isLegal(entity.getTechLevel(), atCheck.getTechLevel());//(entity.getTechLevel() == atCheck.getTechLevel());
                
                String munition = Long.toString(atCheck.getMunitionType());
                House faction = mwclient.getData().getHouseByName(mwclient.getPlayer().getHouse());
                
                //CampaignData.mwlog.errLog("Ammo: "+atCheck.getInternalName()+" MType: "+atCheck.getMunitionType());
                //check banned ammo
                if ( mwclient.getData().getServerBannedAmmo().containsKey(munition)
                        || faction.getBannedAmmo().containsKey(munition) 
                        || (mwclient.getAmmoCost(atCheck.getInternalName()) < 0 && !usingCrits))
                    continue;
                
                //CampaignData.mwlog.errLog("2.Ammo: "+atCheck.getInternalName()+" MType: "+atCheck.getMunitionType());
                
                //System.err.println(atCheck.getName()+"/"+atCheck.getInternalName());
                if ( usingCrits && 
                		mwclient.getPlayer().getPartsCache().getPartsCritCount(atCheck.getInternalName()) < 1 &&
                		!ammoAlreadyLoaded(atCheck))
                	continue;
                //CampaignData.mwlog.errLog("3.Ammo: "+atCheck.getInternalName()+" MType: "+atCheck.getMunitionType());

                // allow all lvl2 IS units to use level 1 ammo
                // lvl1 IS units don't need to be allowed to use lvl1 ammo,
                // because there is no special lvl1 ammo, therefore it doesn't
                // need to show up in this display.
                if (!bTechMatch && entity.getTechLevel() == TechConstants.T_IS_ADVANCED &&
                    atCheck.getTechLevel() <= TechConstants.T_IS_TW_NON_BOX) {
                    bTechMatch = true;
                }
                
                // if is_eq_limits is unchecked allow L1 units to use L2 munitions
                if (!mmClient.game.getOptions().booleanOption("is_eq_limits")
                    && entity.getTechLevel() <= TechConstants.T_IS_TW_NON_BOX
                    && atCheck.getTechLevel() == TechConstants.T_IS_ADVANCED) {
                    bTechMatch = true;
                }
                
                // Possibly allow level 3 ammos, possibly not.
                if (mmClient.game.getOptions().booleanOption("allow_level_3_ammo")) {
                    if (!mmClient.game.getOptions().booleanOption("is_eq_limits")) {
                        if (entity.getTechLevel() == TechConstants.T_CLAN_ADVANCED
                                && atCheck.getTechLevel() == TechConstants.T_CLAN_EXPERIMENTAL) {
                            bTechMatch = true;
                        }
                        if (((entity.getTechLevel() <= TechConstants.T_IS_TW_NON_BOX) || (entity.getTechLevel() == TechConstants.T_IS_ADVANCED))
                                && (atCheck.getTechLevel() == TechConstants.T_IS_EXPERIMENTAL)) {
                            bTechMatch = true;
                        }
                    }
                } else if ((atCheck.getTechLevel() == TechConstants.T_IS_EXPERIMENTAL) || (atCheck.getTechLevel() == TechConstants.T_CLAN_EXPERIMENTAL)) {
                    bTechMatch = false;
                }
                
		        //allow mixed Tech Mechs to use both IS and Clan Ammo
                if (entity.isMixedTech()) {
                   bTechMatch = true;                       
                }
                
                // If clan_ignore_eq_limits is unchecked,
                // do NOT allow Clans to use IS-only ammo.
                // N.B. play bit-shifting games to allow "incendiary"
                //      to be combined to other munition types.
                long muniType = atCheck.getMunitionType();
                muniType &= ~AmmoType.M_INCENDIARY_LRM;
                if ( !mmClient.game.getOptions().booleanOption("clan_ignore_eq_limits")
                     && entity.isClan()
                     && ( muniType == AmmoType.M_SEMIGUIDED ||
                          muniType == AmmoType.M_THUNDER_AUGMENTED ||
                          muniType == AmmoType.M_THUNDER_INFERNO   ||
                          muniType == AmmoType.M_THUNDER_VIBRABOMB ||
                          muniType == AmmoType.M_THUNDER_ACTIVE ||
                          muniType == AmmoType.M_INFERNO_IV ||
                          muniType == AmmoType.M_VIBRABOMB_IV)) {
                    bTechMatch = false;
		        }
		        
                if (!mmClient.game.getOptions().booleanOption("minefields") &&
                	AmmoType.canDeliverMinefield(atCheck) ) {
                    continue;
                }
                //CampaignData.mwlog.errLog("4.Ammo: "+atCheck.getInternalName()+" MType: "+atCheck.getMunitionType());

                // Only Protos can use Proto-specific ammo
                if ( atCheck.hasFlag(AmmoType.F_PROTOMECH) &&
                     !(entity instanceof Protomech) ) {
                    continue;
                }
                //CampaignData.mwlog.errLog("5.Ammo: "+atCheck.getInternalName()+" MType: "+atCheck.getMunitionType());

                // When dealing with machine guns, Protos can only
                //  use proto-specific machine gun ammo
                if ( entity instanceof Protomech &&
                     atCheck.hasFlag(AmmoType.F_MG) &&
                     !atCheck.hasFlag(AmmoType.F_PROTOMECH) ) {
                    continue;
                }
                //CampaignData.mwlog.errLog("6.Ammo: "+atCheck.getInternalName()+" MType: "+atCheck.getMunitionType());

                // BattleArmor ammo can't be selected at all.
                // All other ammo types need to match on rack size and tech.
                if ( bTechMatch &&
                     atCheck.getRackSize() == at.getRackSize() &&
                     !atCheck.hasFlag(AmmoType.F_BATTLEARMOR) &&
                     atCheck.getTonnage(entity) == at.getTonnage(entity)) {
                    vTypes.addElement(atCheck);
                }
            }
         
            //Protomechs need special choice panels.
            if (entity instanceof Protomech)
                mcp = new ProtomechMunitionChoicePanel(m, vTypes,location);
            else
                mcp = new MunitionChoicePanel(m, vTypes, location);
            
            //get a location name
            int loc;
            if (m.getLocation() == Entity.LOC_NONE){// oneshot weapons don't have a location of their own
                Mounted linkedBy = m.getLinkedBy();
                loc = linkedBy.getLocation();
            } else {
                loc = m.getLocation();
            }
            
            //add location label
            panMunitions.add(new JLabel(entity.getLocationAbbr(loc) + ":", SwingConstants.TRAILING));
           
            panMunitions.add(mcp);
            m_vMunitions.addElement(mcp);
            
            //increment the rowcount
            munitionsRows++;
            
        }//end while(ammo remains in enumeration)
        
        /*
         * setup the spring grid. If there are > 10 combo
         * boxes in play, split into two columns.
         */
        if (munitionsRows > 10)
        	SpringLayoutHelper.setupSpringGrid(panMunitions, 4);
        else 
        	SpringLayoutHelper.setupSpringGrid(panMunitions, 2);
    }
    
    private void setupEdgeSkills(){
        
        panEdgeSkills.setLayout(new SpringLayout());
        
        tacCB.setSelected(pilot.getTac());
        koCB.setSelected(pilot.getKO());
        explosionsCB.setSelected(pilot.getExplosion());
        headHitsCB.setSelected(pilot.getHeadHit());
        
        panEdgeSkills.add(tacCB);
        panEdgeSkills.add(koCB);
        panEdgeSkills.add(explosionsCB);
        panEdgeSkills.add(headHitsCB);
        
        SpringLayoutHelper.setupSpringGrid(panEdgeSkills,4);
    }
    
    private void setupMachineGuns() {
        
        int mgRows = 0;
        panMachineGuns.setLayout(new SpringLayout());

        for (int location = Mech.LOC_HEAD; location <= Mech.LOC_LLEG; location++) {
            for (int slot = 0; slot < entity.getNumberOfCriticals(location); slot++) {
                CriticalSlot crit = entity.getCritical(location, slot);

                if (crit == null || crit.getType() != CriticalSlot.TYPE_EQUIPMENT)
                    continue;

                Mounted m = entity.getEquipment(crit.getIndex());

                if (m == null || !(m.getType() instanceof WeaponType))
                    continue;

                WeaponType wt = (WeaponType) m.getType();
                if (!wt.hasFlag(WeaponType.F_MG))
                    continue;

                // Protomechs need special choice panels.
                MachineGunChoicePanel mgcp = new MachineGunChoicePanel(m, location, slot);
                panMachineGuns.add(mgcp);
                m_vMachineGuns.addElement(mgcp);

                mgRows++;
            }
        }
        
        /*
         * setup the spring grid. If there are >6 combo boxes in play, split
         * into two columns.
         */
        if (mgRows >= 6) {
        	SpringLayoutHelper.setupSpringGrid(panMachineGuns,2);
        } else 
        	SpringLayoutHelper.setupSpringGrid(panMachineGuns,1);
    }

    /*
     * In truth, this could be broken down into a method which
     * returned a JComboBox and the ammo-dumping CBox could be
     * handled elsewhere; however, the Panel extension is carried
     * over from the original MegaMek code-path and works well
     * enough for our purposes. @urgru 7/30/05
     */
    class MunitionChoicePanel extends JPanel {
        /**
         * 
         */
        private static final long serialVersionUID = -5861067242226106955L;
        private Vector<AmmoType> m_vTypes;
        private JComboBox m_choice;
        private Mounted m_mounted;
        private int location = 0;
        
        protected JCheckBox chDump = new JCheckBox();
        protected JCheckBox chHotLoad = new JCheckBox();
        
        public MunitionChoicePanel(Mounted m, Vector<AmmoType> vTypes, int location) {
            
        	//save params
            m_vTypes = vTypes;
            m_mounted = m;
            this.location = location;
            
            //setup panel
            AmmoType curType = (AmmoType)m.getType();
            m_choice = new JComboBox();
            Enumeration<AmmoType> e = m_vTypes.elements();
            
            for (int x = 0; e.hasMoreElements(); x++) {
            	AmmoType at = e.nextElement();
                m_choice.setMaximumSize(new Dimension(5,5));
                int cost = Integer.MAX_VALUE;
                int shotsLeft = m.getShotsLeft();
                if ( !curType.getInternalName().equalsIgnoreCase(at.getInternalName()) )
                	shotsLeft = 0;

                double ammoCost = 0;
                try{
                    ammoCost = mwclient.getAmmoCost(at.getInternalName());
                }catch (Exception ex){
                    CampaignData.mwlog.errLog("error finding cost for: "+at.getName());
                    CampaignData.mwlog.errLog(ex);
                }
                if ( m.getLocation() == Entity.LOC_NONE ){
                    if ( usingCrits )
                    	m_choice.addItem(at.getName()+" ("+shotsLeft+"/1/"+mwclient.getPlayer().getPartsCache().getPartsCritCount(at.getInternalName())+")");
                    else
                        m_choice.addItem(at.getName()+" ("+shotsLeft+"/1) "+mwclient.moneyOrFluMessage(true,true,(int)ammoCost));
                }
                else {
                    int refillShots = at.getShots();
                    if ( !curType.getInternalName().equalsIgnoreCase(at.getInternalName()) )
                    	shotsLeft = 0;
                    
                    //No reason to continue if there are not shots to refill.
                    if ( shotsLeft == refillShots )
                    	cost = 0;
                    else{
                    	refillShots -= shotsLeft;
                    	cost = (int)Math.ceil(ammoCost*(double)refillShots);
                    }

                    //CampaignData.mwlog.errLog("Cost: "+cost+" string: "+mwclient.moneyOrFluMessage(true,true,cost));
                	if ( usingCrits )
                		m_choice.addItem(at.getName()+" ("+shotsLeft+"/"+refillShots+"/"+mwclient.getPlayer().getPartsCache().getPartsCritCount(at.getInternalName())+")");
                	else
                        m_choice.addItem(at.getName()+" ("+shotsLeft+"/"+refillShots+") "+mwclient.moneyOrFluMessage(true,true,cost));

                }
            	if (at.getInternalName().equalsIgnoreCase(curType.getInternalName()))
            		m_choice.setSelectedIndex(x);
            }
            
            add(m_choice);
            
            //set up the dump checkbox, if dumping is allowed
            if (canDump) {
                if (m.getShotsLeft() == 0)
                    chDump.setSelected(true);
                chDump.setText("Dump");
                add(chDump);
            }
            if ( mmClient.game.getOptions().booleanOption("tacops_hotload") 
                    && ((entity instanceof Mech) || (entity instanceof Tank))
                    && ((AmmoType) m.getType()).hasFlag(AmmoType.F_HOTLOAD) ){
                chHotLoad.setSelected(m.isHotLoaded());
                chHotLoad.setText("Hot-Load");
                add(chHotLoad);
            }else{
            	chHotLoad.setEnabled(false);
                chHotLoad.setText("Hot-Load");
                add(chHotLoad);
            }
            
        }

        /*  Yes this to load the ammo
         Save Weapon Type from at.getAmmoType() call weapon type
         save weapon position with at.getMunitionType() call ammo type
         Load
         at.getMunitionsFor(ammoType) returns vector
         ammo_vector.elementAt(MunitionType);
         */
        
        public void applyChoice() {
            int n = m_choice.getSelectedIndex();
            
            if ( n < 0 )
            	return;
            AmmoType at = (AmmoType)m_vTypes.elementAt(n);
            //m_mounted.changeAmmoType(at);
            
            int totalShots = at.getShots();
            boolean hotloaded = false;

            if ( chHotLoad != null )
                hotloaded = chHotLoad.isSelected();
            
            if (chDump.isSelected()){
                m_mounted.setShotsLeft(0);
                totalShots = 0;
            }
            else if ( m_mounted.getLocation() == Entity.LOC_NONE )
                totalShots = 1;

            //m_mounted.setShotsLeft(totalShots);
            mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c setunitammo#"+entity.getExternalId()+"#"+this.location+"#"+at.getAmmoType()+"#"+at.getInternalName()+"#"+totalShots+"#"+hotloaded);
        }

        @Override
		public void setEnabled(boolean enabled) {
            m_choice.setEnabled(enabled);
        }

        /**
         * Get the number of shots in the mount.
         *
         * @return      the <code>int</code> number of shots in the mount.
         */
        /* package */ int getShotsLeft() {
            return m_mounted.getShotsLeft();
        }

        /**
         * Set the number of shots in the mount.
         *
         * @param shots the <code>int</code> number of shots for the mount.
         */
        /* package */ void setShotsLeft( int shots ) {
            m_mounted.setShotsLeft( shots );
        }
    }

    class MachineGunChoicePanel extends JPanel {
    	
        /**
         * 
         */
        private static final long serialVersionUID = -2207765894385312209L;
        private Mounted m_mounted;
        private int location = 0;
        private int slot = 0;
        protected JCheckBox chBurst = new JCheckBox();
        
        public MachineGunChoicePanel(Mounted m, int location, int slot) {
            
        	//store params
        	m_mounted = m;
        	this.location = location;
        	this.slot = slot;
        	
            //restore previous setting
            chBurst.setSelected(m_mounted.isRapidfire());
            
            //setup
            chBurst.setText("Rapid Fire MG (" + entity.getLocationAbbr(location) + ")");
            add(chBurst);
        }

        /*  Yes this to load the ammo
         Save Weapon Type from at.getAmmoType() call weapon type
         save weapon position with at.getMunitionType() call ammo type
         Load
         at.getMunitionsFor(ammoType) returns vector
         ammo_vector.elementAt(MunitionType);
         */
        
        public void applyChoice() {
            if ( m_mounted.isRapidfire() != chBurst.isSelected() )
                mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c setunitburst#"+entity.getExternalId()+"#"+this.location+"#"+this.slot+"#"+chBurst.isSelected());
        }

        @Override
		public void setEnabled(boolean enabled) {
            chBurst.setEnabled(enabled);
        }
    }

    class HotLoadChoicePanel extends JPanel {
        
        /**
         * 
         */
        private static final long serialVersionUID = -4801226845131401403L;
        private Mounted m_mounted;
        private int location = 0;
        protected JCheckBox chHotLoad = new JCheckBox();
        
        public HotLoadChoicePanel(Mounted m, int location) {
            
            //store params
            m_mounted = m;
            this.location = location;
            
            //mount hodler int
            int loc;
            loc = m.getLocation();
            
            //restore previous setting
            chHotLoad.setSelected(m_mounted.isHotLoaded());
            
            //setup
            chHotLoad.setText("Hot-Load "+m_mounted.getName()+" (" + entity.getLocationAbbr(loc) + ")");
            add(chHotLoad);
        }

        /*  Yes this to load the ammo
         Save Weapon Type from at.getAmmoType() call weapon type
         save weapon position with at.getMunitionType() call ammo type
         Load
         at.getMunitionsFor(ammoType) returns vector
         ammo_vector.elementAt(MunitionType);
         */
        
        public void applyChoice() {
            if ( m_mounted.isHotLoaded() != chHotLoad.isSelected() )
                mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c setunithotload#"+entity.getExternalId()+"#"+this.location+"#"+chHotLoad.isSelected());
        }

        @Override
		public void setEnabled(boolean enabled) {
            chHotLoad.setEnabled(enabled);
        }
    }

    /**
     * When a Protomech selects ammo, you need to adjust the shots on the
     * unit for the weight of the selected munition.
     */
    class ProtomechMunitionChoicePanel extends MunitionChoicePanel {
    	/**
         * 
         */
        private static final long serialVersionUID = 3984045407240841489L;
        private final float m_origShotsLeft;
    	private final AmmoType m_origAmmo;
    	
    	public ProtomechMunitionChoicePanel(Mounted m, Vector<AmmoType> vTypes, int row) {
    		super( m, vTypes, row );
    		m_origAmmo = (AmmoType) m.getType();
    		m_origShotsLeft = m.getShotsLeft();
    	}
    	
    	/**
    	 * All ammo must be applied in ratios to the starting load.
    	 */
    	@Override
		public void applyChoice() {
    		super.applyChoice();
    		
    		// Calculate the number of shots for the new ammo.
    		// N.B. Some special ammos are twice as heavy as normal
    		// so they have half the number of shots (rounded down).
    		setShotsLeft( Math.round( getShotsLeft() * m_origShotsLeft / m_origAmmo.getShots() ) );
    		if (chDump.isSelected())
    			setShotsLeft(0);
    	}
    }
    
    /* public void disableMunitionEditing() {
     for (int i = 0; i < m_vMunitions.size(); i++) {
     ((MunitionChoicePanel)m_vMunitions.elementAt(i)).setEnabled(false);
     }
     }*/
    
    public boolean isOkay() {
    	return okay;
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() != butCancel) {
            //get values
            //String name = fldName.getText();
            int offBoardDistance;
            boolean autoEject = chAutoEject.isSelected();
            
            if (chOffBoard.isSelected()){
                try {
                    offBoardDistance = Integer.parseInt(fldOffBoardDistance.getText());
                } catch (NumberFormatException e) {
                	mwclient.showInfoWindow("Please enter valid numbers for off board distance.");
                    return;
                }
                if (offBoardDistance < 17) {
                	mwclient.showInfoWindow("Offboard units need to be at least one mapsheet (17 hexes) away.");
                    return;
                }
                entity.setOffBoard(offBoardDistance, IOffBoardDirections.NORTH );
            }
            else {
                entity.setOffBoard( 0, Entity.NONE );
            }

            // change entity
           if (entity instanceof Mech) {
                Mech mech = (Mech)entity;
                if ( mech.isAutoEject() == autoEject )
                {
                    mech.setAutoEject(!autoEject);
                    mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c setautoeject#"+mech.getExternalId()+"#"+!autoEject);
                 }
                if ( pilot.getSkills().has(PilotSkill.EdgeSkillID) ){
                    mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c setedgeSkills#"+mech.getExternalId()+"#"+tacCB.isSelected()+"#"+koCB.isSelected()+"#"+headHitsCB.isSelected()+"#"+explosionsCB.isSelected());

                }
          }
           
           if ((entity instanceof Mech) || (entity instanceof Tank)) {
               // Update the entity's targetting system type.
               if (!(UnitUtils.hasTargettingComputer(entity)) && (mmClient.game.getOptions().booleanOption("allow_level_3_targsys"))) {
                   
                   int targSysIndex = MiscType.T_TARGSYS_STANDARD;
                   if (choTargSys.getSelectedItem() != null)
                       targSysIndex = MiscType.getTargetSysType(choTargSys.getSelectedItem());
                   if ( entity.getTargSysType() != targSysIndex){
                       if (targSysIndex >= 0)
                           entity.setTargSysType(targSysIndex);
                       else {
                           System.err.println("Illegal targetting system index: "+targSysIndex);
                           entity.setTargSysType(MiscType.T_TARGSYS_STANDARD);
                       }
                       mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c settargetsystemtype#"+entity.getExternalId()+"#"+entity.getTargSysType());
                   }
               }
           }           
          
            okay = true;

	        for (Enumeration<MunitionChoicePanel> e = m_vMunitions.elements(); e.hasMoreElements(); ) {
	            e.nextElement().applyChoice();
	        }
	        
	        
	        for (Enumeration<MachineGunChoicePanel> e = m_vMachineGuns.elements(); e.hasMoreElements(); ) {
	            e.nextElement().applyChoice();
	        }

        }
        
        this.setVisible(false);
    }
    
    private void loadAmmo(){
        mwclient.loadBannedAmmo();
        mwclient.loadBanTargeting();
    }

    private boolean ammoAlreadyLoaded(AmmoType ammo){
    	
    	for ( Mounted mounted : entity.getAmmo() ){
    		AmmoType currAmmo = (AmmoType)mounted.getType();
    		
    		if ( currAmmo.equals(ammo) )
    			return true;
    	}
    	
    	return false;
    }
}
