/**
 * MegaMek - Copyright (C) 2000-2002 Ben Mazur (bmazur@sev.org)
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

package client.gui.dialog;

//import java.awt.Dimension;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.SpringLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.ImageIcon;

import common.util.UnitUtils;

import megamek.common.AmmoType;
import megamek.common.CriticalSlot;
import megamek.common.Entity;
import megamek.common.EquipmentMode;
import megamek.common.EquipmentType;
import megamek.common.Infantry;
import megamek.common.Mech;
import megamek.common.MiscType;
import megamek.common.Mounted;
import megamek.common.Protomech;
import megamek.common.Tank;
import megamek.common.WeaponType;

import common.util.SpringLayoutHelper;
import client.gui.MechInfo;

/**
 * Displays the info for a mech.  This is also a sort
 * of interface for special movement and firing actions.
 */

public class MechDetailDisplay extends JTabbedPane {

	public GeneralPanel mPan;
    public ArmorPanel aPan;
    public WeaponPanel wPan;
    public SystemPanel sPan;

    /**
     * Creates and lays out a new mech display.
     */
    public MechDetailDisplay() {
        super();
        mPan = new GeneralPanel();
        add("General", mPan);
        aPan = new ArmorPanel();
        add("Armor", aPan);
        wPan = new WeaponPanel();
        add("Weapons", wPan);
        sPan = new SystemPanel();
        add("Systems", sPan);
    }

    //public void displayEntity(Entity en, int bv) {
    //	displayEntity(en,bv,null) ;
    //}
    
     /**
     * Displays the specified entity in the panel.
     */
    public void displayEntity(Entity en, int bv, ImageIcon currentCamo) {
        mPan.displayMech(en, bv, currentCamo);
        aPan.displayMech(en);
        wPan.displayMech(en);
        sPan.displayMech(en);
    }
}
/**
 * The general panel contains all the buttons, readouts
 * and gizmos relating to general things and moving around on the
 * battlefield.
 */

class GeneralPanel extends JPanel{
    
    public JPanel statusP, terrainP, moveP;
    public JLabel mechTypeL;
    public JLabel weightL, weightR, pilotL, pilotR, skillsL, skillsR;
    public JLabel mpL, mpR, heatL, heatR;
    public JLabel bvR, bvL;
    public JLabel cargoL, cargoR;
    public JCheckBox autoEjectCB;
    public Entity ent = null;
    public MechInfo unitPicture = null;
    
    public GeneralPanel() {
        super();
        
        try {
            //MechSummary ms = MechSummaryCache.getInstance().getMech("Error OMG-UR-FD");
            ent = UnitUtils.createOMG();//new MechFileParser(ms.getSourceFile(), ms.getEntryName()).getEntity();
        } catch (Exception e) {
        	System.out.println("ERROR UNIT MISSING!");
        }

        mechTypeL = new JLabel("<HTML><body>Me fail english!?<br>Thats unpossible!</body></HTML>", SwingConstants.CENTER);
        mechTypeL.setAlignmentX(Component.CENTER_ALIGNMENT);

        // status stuff
        weightL = new JLabel("Weight:", SwingConstants.RIGHT);
        pilotL = new JLabel("Pilot:", SwingConstants.RIGHT);
        skillsL = new JLabel("Gun/Pilot:", SwingConstants.RIGHT);
        
        weightR = new JLabel("?", SwingConstants.LEFT);
        pilotR = new JLabel("?", SwingConstants.LEFT);
        skillsR = new JLabel("?/?", SwingConstants.LEFT);
        
        // movement stuff
        mpL = new JLabel("Movement:", SwingConstants.RIGHT);
        heatL = new JLabel("Heat:", SwingConstants.RIGHT);

        mpR = new JLabel("8/12/0", SwingConstants.LEFT);
        heatR = new JLabel("2 (10 capacity)", SwingConstants.LEFT);

        autoEjectCB = new JCheckBox();
        autoEjectCB.setToolTipText("Check to enable autoeject");
        
        statusP = new JPanel();//status panel
        
        //new subpanel for status
        JPanel statusSpringP = new JPanel(new SpringLayout());
        
        //cargo stuff
        cargoL = new JLabel("Cargo:",SwingConstants.RIGHT);
        cargoR = new JLabel("9999",SwingConstants.LEFT);
        
        //bv stuff
        bvL = new JLabel("BV:", SwingConstants.RIGHT);
        bvR = new JLabel("9999", SwingConstants.LEFT);
        
        statusSpringP.add(weightL);
        statusSpringP.add(weightR);
        
        statusSpringP.add(skillsL);
        statusSpringP.add(skillsR);
        
        statusSpringP.add(mpL);
        statusSpringP.add(mpR);

        statusSpringP.add(heatL);
        statusSpringP.add(heatR);
        
        statusSpringP.add(bvL);
        statusSpringP.add(bvR);
        
        statusSpringP.add(cargoL);
        statusSpringP.add(cargoR);

        //set up the springs
        SpringLayoutHelper.setupSpringGrid(statusSpringP,6,2);
        statusP.setLayout(new BoxLayout(statusP, BoxLayout.Y_AXIS));
        statusP.add(statusSpringP);
        
        //layout main panel
        setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        add(mechTypeL);
        add(statusP);
    }

    /**
     * updates fields for the specified mech
     */
    public void displayMech(Entity en, int bv, ImageIcon currentCamo) {
        
        ent = en;
        
        //reinit the unit picture
        unitPicture = new MechInfo(currentCamo);
        unitPicture.setUnit(en);
        
        String uName = en.getShortNameRaw();//Get Name without ID
        mechTypeL.setText("<HTML><br><b>" + uName + "</b><br></HTML>");

        weightR.setText(Integer.toString((int)en.getWeight()));
        pilotR.setText(en.crew.getDesc());
        if ( en instanceof Mech || en instanceof Tank 
                || (en instanceof Infantry && ((Infantry)en).isAntiMek()) ){
            skillsR.setText(en.crew.getGunnery()+"/"+en.crew.getPiloting());
        }
        else{
            skillsL.setText("Gunnery:");
            skillsR.setText(Integer.toString(en.crew.getGunnery()));
        }
        
        StringBuilder mp = new StringBuilder();
        try{
            mp.append(en.getWalkMP());
            mp.append('/');
            mp.append(en.getRunMP());
            mp.append('/');
            mp.append(en.getJumpMPWithTerrain());
        }catch (Exception ex){
            mp.append("0/0/0");
        }
        mpR.setText(mp.toString());

        int heatCap = en.getHeatCapacity();
        int heatCapWater = en.getHeatCapacityWithWater();
        String heatCapacityStr = Integer.toString(heatCap);

        if ( heatCap < heatCapWater ) {
          heatCapacityStr = heatCap + " [" + heatCapWater + "]";
        }

        heatR.setText(Integer.toString(en.heat) + " (" + heatCapacityStr + " capacity)");

        String capacity = en.getUnusedString();
        if (capacity != null && capacity.startsWith("Troops")) {
            capacity = capacity.substring(9);//strip "Troops - " from string
            cargoR.setText(capacity);
        } else {
        	cargoL.setText("");
        	cargoR.setText("");
        	cargoL.setVisible(false);
        	cargoR.setVisible(false);
        }

        if (bv == 0)
        	bvR.setText("N/A");
        else
        	bvR.setText(Integer.toString(bv));
        
        add(unitPicture);
        validate();
    }
}

/**
 * This panel contains the armor readout display.
 */

class ArmorPanel extends JPanel {
	public JLabel armorTotal, internalTotal;
    public JLabel locHL, internalHL, armorHL;
    public JLabel[] locL, internalL, armorL;

    public ArmorPanel() {
        super(new GridBagLayout());
        locHL = new JLabel("Location", SwingConstants.CENTER);
        internalHL = new JLabel("Internal", SwingConstants.CENTER);
        armorHL = new JLabel("Armor", SwingConstants.CENTER);
        armorTotal = new JLabel("Armor: 999/999", SwingConstants.CENTER);
        internalTotal = new JLabel("Internal total: ", SwingConstants.CENTER);
    }

    /**
     * updates fields for the specified mech
     */
    public void displayMech(Entity en) {
        removeAll();

        locL = new JLabel[en.locations()];
        internalL = new JLabel[en.locations()];
        armorL = new JLabel[en.locations()];

        // initialize
        for(int i = 0; i < en.locations(); i++) {
            locL[i] = new JLabel("Center Torso Rear", SwingConstants.LEFT);
            internalL[i] = new JLabel("99", SwingConstants.CENTER);
            armorL[i] = new JLabel("999", SwingConstants.CENTER);
        }

        // layout main panel
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(2, 2, 2, 2);

        c.weightx = 1.0;    c.weighty = 1.0;
        
        c.gridwidth = GridBagConstraints.REMAINDER;
        if (en instanceof Mech)
        	armorTotal.setText("Armor: "+en.getTotalArmor()+"/"+(((en.getTotalInternal()-3)*2)+9));
        else
        	armorTotal.setText("Armor: "+en.getTotalArmor());
        add(armorTotal, c);
        
        internalTotal.setText("Internal: "+en.getTotalInternal());
        add(internalTotal, c);
        
        c.gridwidth = 1;
        add(locHL, c);
        add(armorHL, c);

        c.gridwidth = GridBagConstraints.REMAINDER;
        add(internalHL, c);

        for(int i = 0; i < en.locations(); i++) {
            c.gridwidth = 1;
            add(locL[i], c);
            add(armorL[i], c);

            c.gridwidth = GridBagConstraints.REMAINDER;
            add(internalL[i], c);
        }
        // update armor panel
        for(int i = 0; i < en.locations(); i++) {
            locL[i].setText(en.getLocationName(i));
            if ( en.getInternal(i) >= 99){
                UnitUtils.removeArmorRepair(en,UnitUtils.LOC_INTERNAL_ARMOR,i);
                internalL[i].setText(en.getInternalString(i));
                UnitUtils.setArmorRepair(en,UnitUtils.LOC_INTERNAL_ARMOR,i);
            }else
                internalL[i].setText(en.getInternalString(i));
            if ( en.getArmor(i) >= 99 ){
                UnitUtils.removeArmorRepair(en,UnitUtils.LOC_FRONT_ARMOR,i);
                if ( en.hasRearArmor(i) && en.getArmor(i,true) >= 99){
                    UnitUtils.removeArmorRepair(en,UnitUtils.LOC_REAR_ARMOR,i);
                }
                armorL[i].setText(en.getArmorString(i) + (en.hasRearArmor(i) ? " (" + en.getArmorString(i, true) + ")" : ""));
                UnitUtils.setArmorRepair(en,UnitUtils.LOC_FRONT_ARMOR,i);
                if ( en.hasRearArmor(i) && en.getArmor(i,true) != en.getOArmor(i,true) )
                    UnitUtils.setArmorRepair(en,UnitUtils.LOC_REAR_ARMOR,i);
            }
            else
                armorL[i].setText(en.getArmorString(i) + (en.hasRearArmor(i) ? " (" + en.getArmorString(i, true) + ")" : ""));
        }
        validate();
    }

}

/**
 * This class contains the all the gizmos for firing the
 * mech's weapons.
 */

class WeaponPanel extends JPanel implements ListSelectionListener {
    public JList weaponList;
    public JComboBox ammoList;
    public JPanel displayP, rangeP, targetP;

    public JLabel wAmmo, wNameL, wHeatL, wDamL, wMinL, wShortL, wMedL, wLongL;
    public JLabel wNameR, wHeatR, wDamR, wMinR, wShortR, wMedR, wLongR;

    public JLabel wTargetL, wRangeL, wToHitL;
    public JLabel wTargetR, wRangeR, wToHitR;

    // I need to keep a pointer to the weapon list of the
    // currently selected mech.
    private Vector ammo;
    private Entity entity;

    public WeaponPanel() {
        super(new GridBagLayout());

        // weapon list
        weaponList = new JList();
        weaponList.setVisibleRowCount(4);
        weaponList.addListSelectionListener(this);

        // ammo choice panel
        wAmmo = new JLabel("Ammo", SwingConstants.LEFT);
        ammoList = new JComboBox();
        ammoList.setEditable(false);

        JPanel ammoP = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(1, 1, 1, 1);
        c.gridwidth = 1;
        c.weightx = 0.0;    c.weighty = 0.0;
        c.fill = GridBagConstraints.NONE;
        ammoP.add(wAmmo, c);

        c.gridwidth = 3;
        c.gridx = 1;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        ammoP.add(ammoList, c);

        // weapon display panel
        wNameL = new JLabel("Name", SwingConstants.LEFT);
        wHeatL = new JLabel("Heat", SwingConstants.CENTER);
        wDamL = new JLabel("Damage", SwingConstants.CENTER);
        wNameR = new JLabel("", SwingConstants.LEFT);
        wHeatR = new JLabel("--", SwingConstants.CENTER);
        wDamR = new JLabel("--", SwingConstants.CENTER);

        displayP = new JPanel(new GridBagLayout());

        c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(1, 1, 1, 1);

        c.gridwidth = 1;
        c.weightx = 1.0;    c.weighty = 0.0;
        displayP.add(wNameL, c);

        c.weightx = 0.0;    c.weighty = 0.0;
        displayP.add(wHeatL, c);

        c.gridwidth = GridBagConstraints.REMAINDER;
        displayP.add(wDamL, c);

        c.gridwidth = 1;
        c.weightx = 1.0;    c.weighty = 0.0;
        displayP.add(wNameR, c);

        c.weightx = 0.0;    c.weighty = 0.0;
        displayP.add(wHeatR, c);

        c.gridwidth = GridBagConstraints.REMAINDER;
        displayP.add(wDamR, c);


        // range panel
        wMinL = new JLabel("Min", SwingConstants.CENTER);
        wShortL = new JLabel("Short", SwingConstants.CENTER);
        wMedL = new JLabel("Med", SwingConstants.CENTER);
        wLongL = new JLabel("Long", SwingConstants.CENTER);
        wMinR = new JLabel("---", SwingConstants.CENTER);
        wShortR = new JLabel("---", SwingConstants.CENTER);
        wMedR = new JLabel("---", SwingConstants.CENTER);
        wLongR = new JLabel("---", SwingConstants.CENTER);

        rangeP = new JPanel(new GridLayout(2, 4));
        rangeP.add(wMinL);
        rangeP.add(wShortL);
        rangeP.add(wMedL);
        rangeP.add(wLongL);
        rangeP.add(wMinR);
        rangeP.add(wShortR);
        rangeP.add(wMedR);
        rangeP.add(wLongR);

        // target panel
        wTargetL = new JLabel("Target:");
        wRangeL = new JLabel("Range:");
        wToHitL = new JLabel("To Hit:");

        wTargetR = new JLabel("---");
        wRangeR = new JLabel("---");
        wToHitR = new JLabel("---");

        targetP = new JPanel(new GridLayout(3, 2));
        targetP.add(wTargetL);
        targetP.add(wTargetR);
        targetP.add(wRangeL);
        targetP.add(wRangeR);
        targetP.add(wToHitL);
        targetP.add(wToHitR);

        // layout main panel
        c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(1, 1, 1, 1);

        c.weightx = 1.0;    c.weighty = 1.0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        add(new JScrollPane(weaponList), c);

        c.weightx = 1.0;    c.weighty = 0.0;
        add(ammoP, c);

        c.weightx = 1.0;    c.weighty = 0.0;
        add(displayP, c);
        add(rangeP, c);

        c.weightx = 1.0;    c.weighty = 0.0;
        add(targetP, c);
    }

    /**
     * updates fields for the specified mech
     *
     * fix the ammo when it's added
     */
    public void displayMech(Entity en) {
        entity = en;

        // update weapon list
        weaponList.removeAll();
        ammoList.removeAllItems();
        ammoList.setEnabled(false);
        ArrayList data = new ArrayList();
        for(Mounted mounted : entity.getWeaponList()) {
            WeaponType wtype = (WeaponType)mounted.getType();
            String wn = mounted.getDesc()
                        + " [" + entity.getLocationAbbr(mounted.getLocation()) + "]";
            // determine shots left & total shots left
            if (wtype.getAmmoType() != AmmoType.T_NA) {
                int shotsLeft = 0;
                if (mounted.getLinked() != null && !mounted.getLinked().isDumping()) {
                    shotsLeft = mounted.getLinked().getShotsLeft();
                }

                EquipmentType typeUsed = mounted.getLinked() == null ? null : mounted.getLinked().getType();
                int totalShotsLeft = entity.getTotalAmmoOfType(typeUsed);

                wn += " (" + shotsLeft + "/" + totalShotsLeft + ")";
                // Fire Mode - lots of things have variable modes
                if (wtype.hasModes()) {
                    wn += " " + mounted.curMode().getDisplayableName();
                }
            }
            data.add(wn);
        }
        weaponList.setListData(data.toArray());
    }

    /**
     * displays the selected item from the list in the weapon
     * display panel.
     */
    public void displaySelected() {
        // short circuit if not selected
        if(weaponList.getSelectedIndex() == -1) {
            ammoList.removeAllItems();
            ammoList.setEnabled(false);
            wNameR.setText("");
            wHeatR.setText("--");
            wDamR.setText("--");
            wMinR.setText("---");
            wShortR.setText("---");
            wMedR.setText("---");
            wLongR.setText("---");
            return;
        }
        Mounted mounted = entity.getWeaponList().get(weaponList.getSelectedIndex());
        WeaponType wtype = (WeaponType)mounted.getType();
        // update weapon display
        wNameR.setText(mounted.getDesc());
        wHeatR.setText(wtype.getHeat() + "");
        if(wtype.getDamage() == WeaponType.DAMAGE_MISSILE) {
            wDamR.setText("Missile");
        } else if(wtype.getDamage() == WeaponType.DAMAGE_VARIABLE) {
            wDamR.setText("Variable");
        } else {
            wDamR.setText(new Integer(wtype.getDamage()).toString());
        }

        // update range
        if(wtype.getMinimumRange() > 0) {
            wMinR.setText(Integer.toString(wtype.getMinimumRange()));
        } else {
            wMinR.setText("---");
        }
        if(wtype.getShortRange() > 1) {
            wShortR.setText("1 - " + wtype.getShortRange());
        } else {
            wShortR.setText("" + wtype.getShortRange());
        }
        if(wtype.getMediumRange() - wtype.getShortRange() > 1) {
            wMedR.setText((wtype.getShortRange() + 1) + " - " + wtype.getMediumRange());
        } else {
            wMedR.setText("" + wtype.getMediumRange());
        }
        if(wtype.getLongRange() - wtype.getMediumRange() > 1) {
            wLongR.setText((wtype.getMediumRange() + 1) + " - " + wtype.getLongRange());
        } else {
            wLongR.setText("" + wtype.getLongRange());
        }

        // update ammo selector
        ammoList.removeAllItems();
        if (wtype.getAmmoType() == AmmoType.T_NA)
            ammoList.setEnabled(false);
        else {
            ammoList.setEnabled(true);
            ammo = new Vector(1,1);
            int nCur = -1;
            int i = 0;
            for (Mounted mountedAmmo : entity.getAmmo()) {
                AmmoType atype = (AmmoType)mountedAmmo.getType();
                if (mountedAmmo.isDestroyed() || mountedAmmo.getShotsLeft() <= 0 || mountedAmmo.isDumping()) {
                    continue;
                }
                if (atype.getAmmoType() == wtype.getAmmoType() && atype.getRackSize() == wtype.getRackSize()) {
                    ammo.addElement(mountedAmmo);
                    if (mounted.getLinked() == mountedAmmo) {
                        nCur = i;
                    }
                    i++;
                }
            }
            for (int x = 0, n = ammo.size(); x < n; x++) {
            	String s = formatAmmo((Mounted)ammo.elementAt(x));
            	if (s.length() > 0)
            		ammoList.addItem(s);
            }
            if (nCur == -1) {
                ammoList.setEnabled(false);
            }
            else {
                ammoList.setSelectedIndex(nCur);
            }
        }
    }

    private String formatAmmo(Mounted m)
    {
        StringBuilder sb = new StringBuilder(64);
        int ammoIndex = m.getDesc().indexOf("Ammo");
        sb.append("[").append(entity.getLocationAbbr(m.getLocation())).append("] ");
        if (ammoIndex == -1) {
            sb.append(m.getDesc());
        } else {
            sb.append(m.getDesc().substring(0, ammoIndex));
            sb.append(m.getDesc().substring(ammoIndex + 4));
        }
        return sb.toString();
    }

	public void valueChanged(ListSelectionEvent ev) {
		displaySelected();
	}
}

/**
 * This class shows the critical hits and systems for a mech
 */

class SystemPanel extends JPanel implements ListSelectionListener {
    private static Object SYSTEM = new Object();

    public JLabel locLabel;
    public JLabel slotLabel;
    public JList slotList;
    public JList locList;

    private Vector equipment = new Vector(16,1);

    public JLabel modeLabel;
    public JComboBox modeList;

    Entity en;

    public SystemPanel() {
        super();

        locLabel = new JLabel("Location", SwingConstants.CENTER);
        slotLabel = new JLabel("Slot", SwingConstants.CENTER);

        locList = new JList();
        locList.setVisibleRowCount(8);
        locList.addListSelectionListener(this);
        slotList = new JList();
        slotList.setVisibleRowCount(12);
        slotList.addListSelectionListener(this);

        modeLabel = new JLabel("Mode", SwingConstants.LEFT);
        modeLabel.setEnabled(false);
        modeList = new JComboBox();
        modeList.setEnabled(false);

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        setLayout(gridbag);

        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(1, 1, 1, 1);

        c.weightx = 0.5;    c.weighty = 0.0;
        c.gridwidth = 1;
        add(locLabel, c);

        c.weightx = 1.0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        add(slotLabel, c);

        c.weightx = 0.5;    c.weighty = 1.0;
        c.gridwidth = 1;
        c.gridheight = GridBagConstraints.REMAINDER;
        add(new JScrollPane(locList), c);

        c.gridwidth = GridBagConstraints.REMAINDER;
        c.gridheight = 2;
        c.gridx = 1;
        c.weightx = 1.0;
        c.weighty = 1.0;
        add(new JScrollPane(slotList), c);

        JPanel p = new JPanel(new GridBagLayout());
        c.gridx = 1;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.gridheight = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        add(p, c);

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 0.0;
        c.weighty = 1.0;
        p.add(modeLabel,c);
        c.gridx = 1;
        c.weightx = 1.0;
        p.add(modeList);
    }

    public Mounted getSelectedEquipment() {
        int n = slotList.getSelectedIndex();
        if (n == -1)
            return null;
        Object o = equipment.elementAt(n);
        if (o == SYSTEM)
            return null;
        return (Mounted)o;
    }

    /**
     * updates fields for the specified mech
     */
    public void displayMech(Entity ent) {
        
    	this.en = ent;

        locList.removeAll();
        ArrayList data = new ArrayList();
        for(int i = 0; i < en.locations(); i++) {
            if(en.getNumberOfCriticals(i) > 0) {
                data.add(en.getLocationName(i));
            }
        }
        locList.setListData(data.toArray());
        locList.setSelectedIndex(0);
        displaySlots();
    }

    public void displaySlots() {
        int loc = locList.getSelectedIndex();
        slotList.removeAll();
        equipment = new Vector(16,1);
        ArrayList data = new ArrayList();
        for (int i = 0; i < en.getNumberOfCriticals(loc); i++) {
            final CriticalSlot cs = en.getCritical(loc, i);
            StringBuilder sb = new StringBuilder(32);
            if(cs == null) {
                sb.append("---");
                equipment.addElement(SYSTEM);
            } else {
                switch(cs.getType()) {
                case CriticalSlot.TYPE_SYSTEM :
                    sb.append(cs.isDestroyed() ? "*" : "");
                    // Protomechs have different systme names.
                    if ( en instanceof Protomech ) {
                        sb.append(Protomech.systemNames[cs.getIndex()]);
                    } else {
                        sb.append(Mech.systemNames[cs.getIndex()]);
                    }
                    equipment.addElement(SYSTEM);
                    break;
                case CriticalSlot.TYPE_EQUIPMENT :
                    Mounted m = en.getEquipment(cs.getIndex());
                    sb.append(cs.isDestroyed() ? "*" : "").append(m.getDesc());
                    if (m.getType().hasModes())
                        sb.append(" (").append(m.curMode().getDisplayableName()).append(")");
                    equipment.addElement(m);
                    break;
                }
            }
            data.add(sb.toString());
        }
        if (en instanceof Tank) {
            if (en.hasTargComp()) {
                Iterator equip = en.getEquipment().iterator();
                while (equip.hasNext()) {
                    Mounted m = (Mounted)equip.next();
                    if (m.getType() instanceof MiscType && 
                        m.getType().hasFlag(MiscType.F_TARGCOMP) ) {
                        StringBuilder sb = new StringBuilder(32);
                        sb.append(m.isDestroyed() ? "*" : "").append(m.isBreached() ? "x" : "").append(m.getDesc()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                        if (m.getType().hasModes()) {
                            sb.append(" (").append(m.curMode().getDisplayableName()).append(")"); //$NON-NLS-1$ //$NON-NLS-2$
                        }
                        data.add(sb.toString());                                                
                    }                    
                }
            }
        }
        slotList.setListData(data.toArray());
    }

	public void valueChanged(ListSelectionEvent ev) {
        if(ev.getSource() == locList)
            displaySlots();
        else if (ev.getSource() == slotList) {
            Mounted m = getSelectedEquipment();
            if (m != null && m.getType().hasModes()) {
                modeLabel.setEnabled(true);
                modeList.setEnabled(true);
                modeList.removeAllItems();
                for (Enumeration e = m.getType().getModes(); e.hasMoreElements();) {
                    EquipmentMode em = (EquipmentMode) e.nextElement();
                    modeList.addItem(em.getDisplayableName());
                }
            } else {
                modeLabel.setEnabled(false);
                modeList.setEnabled(false);
            }
        }
    }
}

