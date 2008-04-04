/*
 * MekWars - Copyright (C) 2008 
 * 
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

/**
 * @author jtighe
 * 
 * Basic and advanced dialog for converting components into crits
 */

package client.gui.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import megamek.common.TechConstants;

import common.BMEquipment;
import common.util.ComponentToCritsConverter;

import client.MWClient;
import client.campaign.CUnit;

public final class ComponentConverterDialog implements ActionListener {

    private final static String okayCommand = "okay";
    private final static String cancelCommand = "cancel";
    private final static String selectorButtonCommand = "selectorbuttoncommand";
    private final static String windowName = "Component Crit Converter";

    protected JPanel mainPanel = new JPanel(); // main Panel for everything
    protected JPanel critPanel = new JPanel();
    protected JScrollPane scrollPane = new JScrollPane(); // the scrolly
                                                            // thingy

    private JTextField baseTextField = new JTextField(5);

    String[] units = { CUnit.getTypeClassDesc(CUnit.MEK), CUnit.getTypeClassDesc(CUnit.VEHICLE), CUnit.getTypeClassDesc(CUnit.INFANTRY), CUnit.getTypeClassDesc(CUnit.PROTOMEK), CUnit.getTypeClassDesc(CUnit.BATTLEARMOR) };
    String[] weight = { CUnit.getWeightClassDesc(CUnit.LIGHT), CUnit.getWeightClassDesc(CUnit.MEDIUM), CUnit.getWeightClassDesc(CUnit.HEAVY), CUnit.getWeightClassDesc(CUnit.ASSAULT) };
    protected JComboBox weightCombo;
    protected JComboBox typeCombo;

    private final JButton okayButton = new JButton("OK");
    private final JButton cancelButton = new JButton("Cancel");
    private final JButton modeButton = new JButton("Basic");
    private boolean isAdvanced = false;

    private JDialog dialog;
    private JOptionPane pane;

    MWClient mwclient = null;

    public ComponentConverterDialog(MWClient mwclient) {

        this.mwclient = mwclient;

        scrollPane.add(mainPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setViewportView(mainPanel);

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        okayButton.setActionCommand(okayCommand);
        okayButton.addActionListener(this);

        cancelButton.addActionListener(this);
        cancelButton.setActionCommand(cancelCommand);

        modeButton.addActionListener(this);
        modeButton.setActionCommand(selectorButtonCommand);

        // Set the user's options
        Object[] options = { okayButton, cancelButton, modeButton };

        // Create the pane containing the buttons
        pane = new JOptionPane(scrollPane, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null, options, null);

        // Create the main dialog and set the default button
        dialog = pane.createDialog(scrollPane, windowName);
        dialog.getRootPane().setDefaultButton(cancelButton);

        // Show the dialog and get the user's input
        dialog.setLocation(mwclient.getMainFrame().getLocation().x + 10, mwclient.getMainFrame().getLocation().y);
        dialog.setModal(true);
        dialog.pack();
        refresh();
        dialog.setVisible(true);

        if (pane.getValue() == okayButton) {

            for (int pos = mainPanel.getComponentCount() - 1; pos >= 0; pos--) {
                JPanel panel = (JPanel) mainPanel.getComponent(pos);
                findAndSaveConfigs(panel);
            }
            mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c getcomponentconversion");
        } else
            dialog.dispose();
    }

    /**
     * This method will tunnel through all of the panels of the config UI to
     * find any changed text fields or checkboxes. Then it will send the new
     * configs to the server.
     * 
     * @param panel
     */
    public void findAndSaveConfigs(JPanel panel) {
        String crit = null;
        String amount = null;
        int weight = 0;
        int type = 0;
        for (int fieldPos = panel.getComponentCount() - 1; fieldPos >= 0; fieldPos--) {

            Object field = panel.getComponent(fieldPos);

            // found another JPanel keep digging!
            if (field instanceof JPanel)
                findAndSaveConfigs((JPanel) field);
            else if (field instanceof JTextField) {
                JTextField textBox = (JTextField) field;

                if (textBox.getName().equals("amount")) {
                    amount = textBox.getText();
                } else {
                    crit = textBox.getName();
                }
            } else if (field instanceof JComboBox) {

                JComboBox combo = (JComboBox) field;
                if (combo.getName().equals("weight")) {
                    weight = combo.getSelectedIndex();
                } else {
                    type = combo.getSelectedIndex();
                }

            }
        }
        
        ComponentToCritsConverter converter = mwclient.getCampaign().getComponentConverter().get(crit);
        
        if ( converter == null || converter.getComponentUsedType() != type 
                || converter.getComponentUsedWeight() != weight 
                || converter.getMinCritLevel() != Integer.parseInt(amount) ){
            mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c Setcomponentconversion#" + crit+ "#" + weight + "#" + type + "#" + amount);
        }

    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals(okayCommand)) {
            pane.setValue(okayButton);
            dialog.dispose();
        } else if (command.equals(cancelCommand)) {
            pane.setValue(cancelButton);
            dialog.dispose();
        } else if (command.equals(selectorButtonCommand)) {
            refresh();
        }

    }

    public void refresh() {
        mainPanel.removeAll();

        if (!isAdvanced) {
            scrollPane.setSize(300, 40);
            scrollPane.setPreferredSize(scrollPane.getSize());
            scrollPane.setMinimumSize(scrollPane.getSize());
            dialog.setSize(400, 140);
            dialog.setPreferredSize(dialog.getSize());
            dialog.setMinimumSize(dialog.getSize());

            ComponentToCritsConverter converter = mwclient.getCampaign().getComponentConverter().get("All");
            
            if ( converter == null ) {
                converter = new ComponentToCritsConverter();
                converter.setCritName("All");
                converter.setComponentUsedType(CUnit.MEK);
                converter.setComponentUsedWeight(CUnit.LIGHT);
                converter.setMinCritLevel(100);
            }
            
            critPanel = new JPanel();
            baseTextField = new JTextField(5);
            baseTextField.setEditable(false);
            baseTextField.setName(converter.getCritName());
            baseTextField.setText(converter.getCritName());
            critPanel.add(baseTextField);

            weightCombo = new JComboBox(weight);
            weightCombo.setName("weight");
            weightCombo.setSelectedIndex(converter.getComponentUsedWeight());
            critPanel.add(weightCombo);

            typeCombo = new JComboBox(units);
            typeCombo.setName("type");
            typeCombo.setSelectedIndex(converter.getComponentUsedType());
            critPanel.add(typeCombo);

            baseTextField = new JTextField(5);
            baseTextField.setName("amount");
            baseTextField.setText(Integer.toString(converter.getMinCritLevel()));
            critPanel.add(baseTextField);

            mainPanel.add(critPanel);
            modeButton.setText("Advanced");
            isAdvanced = !isAdvanced;
        } else {
            for (BMEquipment eq : mwclient.getCampaign().getBlackMarketParts().values()) {

                if ( (Boolean.parseBoolean(mwclient.getserverConfigs("AllowCrossOverTech")) 
                        || mwclient.getPlayer().getHouseFightingFor().getTechLevel() == TechConstants.T_ALL 
                        || eq.getTechLevel() == TechConstants.T_ALL
                        || mwclient.getPlayer().getHouseFightingFor().getTechLevel() >= eq.getTechLevel()) 
                        && eq.getCost() > 0 ) {

                    ComponentToCritsConverter converter = mwclient.getCampaign().getComponentConverter().get(eq.getEquipmentInternalName());
                    
                    if ( converter == null ) {
                        converter = new ComponentToCritsConverter();
                        converter.setCritName(eq.getEquipmentInternalName());
                        converter.setComponentUsedType(CUnit.MEK);
                        converter.setComponentUsedWeight(CUnit.LIGHT);
                        converter.setMinCritLevel(100);
                    }

                    critPanel = new JPanel();
                    baseTextField = new JTextField(25);
                    baseTextField.setEditable(false);
                    baseTextField.setName(eq.getEquipmentInternalName());
                    baseTextField.setText(eq.getEquipmentName());
                    critPanel.add(baseTextField);

                    weightCombo = new JComboBox(weight);
                    weightCombo.setName("weight");
                    weightCombo.setSelectedIndex(converter.getComponentUsedWeight());
                    critPanel.add(weightCombo);

                    typeCombo = new JComboBox(units);
                    typeCombo.setSelectedIndex(converter.getComponentUsedType());
                    typeCombo.setName("type");
                    critPanel.add(typeCombo);

                    baseTextField = new JTextField(5);
                    baseTextField.setName("amount");
                    baseTextField.setText(Integer.toString(converter.getMinCritLevel()));
                    critPanel.add(baseTextField);

                    mainPanel.add(critPanel);
                }
            }
            scrollPane.setSize(400, 400);
            scrollPane.setPreferredSize(scrollPane.getSize());
            scrollPane.setMinimumSize(scrollPane.getSize());
            dialog.setSize(500, 500);
            dialog.setPreferredSize(dialog.getSize());
            dialog.setMinimumSize(dialog.getSize());
            isAdvanced = !isAdvanced;
            modeButton.setText("Basic");
        }
    }
}