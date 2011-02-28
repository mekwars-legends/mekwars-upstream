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

package admin.dialog.serverConfigDialogs;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import common.util.SpringLayoutHelper;

/**
 * @author jtighe
 * @author Spork
 */
public class DBPanel extends JPanel {

	private static final long serialVersionUID = -1527400045914887851L;
	private JCheckBox BaseCheckBox = new JCheckBox();
	
	public DBPanel() {
		super();
        /*
         * Database Configuration Panel Construction
         */
        JPanel dbSpring = new JPanel(new SpringLayout());

        BaseCheckBox = new JCheckBox("Store Unit/Pilot Histories");

        BaseCheckBox.setToolTipText("Stores Unit and Pilot histories in the database");
        BaseCheckBox.setName("StoreUnitHistoryInDatabase");
        dbSpring.add(BaseCheckBox);

        SpringLayoutHelper.setupSpringGrid(dbSpring, 2);
        add(dbSpring);
	}

}