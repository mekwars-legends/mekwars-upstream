/*
 * MekWars - Copyright (C) 2011
 * 
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 */

/**
 * @author Spork
 * @author jtighe
 */

package admin.dialog.serverConfigDialogs;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import common.util.SpringLayoutHelper;
import client.MWClient;

public class InfluencePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5359808432287239311L;

    private JTextField baseTextField = new JTextField(5);
    
    public InfluencePanel(MWClient mwclient) {
		super();
		/*
         * INFLUENCE PANEL CONSTRUCTION Influence panel, where admins set influence gain controls (bv limits, etc) and action costs (bm bid, attack, and so on). Use nested layouts. A Box containing a Flow, which in turn contains two Springs
         */
        JPanel influenceBoxPanel = new JPanel();
        JPanel influenceFlowPanel = new JPanel();
        JPanel influenceSpring1 = new JPanel(new SpringLayout());// 7 items
        JPanel influenceSpring2 = new JPanel(new SpringLayout());// 7 items
        influenceBoxPanel.setLayout(new BoxLayout(influenceBoxPanel, BoxLayout.Y_AXIS));
        influenceBoxPanel.add(influenceFlowPanel);
        influenceFlowPanel.add(influenceSpring1);
        influenceFlowPanel.add(influenceSpring2);

        // load spring1 first
        baseTextField = new JTextField(5);
        influenceSpring1.add(new JLabel("Max Player " + mwclient.moneyOrFluMessage(false, false, -1) + ":", SwingConstants.TRAILING));
        baseTextField.setToolTipText(mwclient.moneyOrFluMessage(false, false, -1) + " ceiling");
        baseTextField.setName("InfluenceCeiling");
        influenceSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
        influenceSpring1.add(new JLabel("Min Time for " + mwclient.moneyOrFluMessage(false, true, -1) + ":", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Minimum active time to receive flu @ check.");
        baseTextField.setName("InfluenceTimeMin");
        influenceSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
        influenceSpring1.add(new JLabel("Floor Penalty:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount removed from TotalArmies when an Army abutts the MinBV");
        baseTextField.setName("FloorPenalty");
        influenceSpring1.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(influenceSpring1, 2);

        // then set up spring2
        baseTextField = new JTextField(5);
        influenceSpring2.add(new JLabel("Ceiling Penalty:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount removed from TotalArmies when an Army abutts the MaxBV");
        baseTextField.setName("CeilingPenalty");
        influenceSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        influenceSpring2.add(new JLabel("Overlap Penalty:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount removed from TotalArmies when 2 armies overlap");
        baseTextField.setName("OverlapPenalty");
        influenceSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        influenceSpring2.add(new JLabel(mwclient.moneyOrFluMessage(false, true, -1) + " Per Army:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base amount of " + mwclient.moneyOrFluMessage(false, false, -1) + " given for each army");
        baseTextField.setName("BaseInfluence");
        influenceSpring2.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(influenceSpring2, 2);

        // springs are it for now. if CBoxes come later, stick them in the box
        // =)
        add(influenceBoxPanel);
	}
	
}
