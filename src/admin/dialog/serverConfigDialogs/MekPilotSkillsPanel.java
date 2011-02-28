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

import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import client.MWClient;

import common.util.SpringLayoutHelper;

/**
 * @author jtighe
 * @author Spork
 */
public class MekPilotSkillsPanel extends JPanel {

	private static final long serialVersionUID = -1357270507979833189L;
	private JTextField baseTextField = new JTextField(5);
	
	public MekPilotSkillsPanel(MWClient mwclient) {
		super();
		/*
         * Mek Pilot Skills Panel
         */
		Dimension fieldSize = new Dimension(5, 10);

        JPanel mekPilotSkillsSpring = new JPanel(new SpringLayout());

        mekPilotSkillsSpring.add(new JLabel(" ", SwingConstants.TRAILING));
        mekPilotSkillsSpring.add(new JLabel("Mek", SwingConstants.TRAILING));
        mekPilotSkillsSpring.add(new JLabel("Pilot Skills", SwingConstants.TRAILING));
        mekPilotSkillsSpring.add(new JLabel(" ", SwingConstants.TRAILING));

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        mekPilotSkillsSpring.add(new JLabel("DM", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain dodge maneuver. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain dodge maneuver</body></html>");
        }
        baseTextField.setName("chanceforDMforMek");
        mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        mekPilotSkillsSpring.add(new JLabel("MS", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain melee specialist. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain melee specialist</body></html>");
        }
        baseTextField.setName("chanceforMSforMek");
        mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        mekPilotSkillsSpring.add(new JLabel("PR", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain pain resistance. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain pain resistance</body></html>");
        }
        baseTextField.setName("chanceforPRforMek");
        mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        mekPilotSkillsSpring.add(new JLabel("SV", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain survivalist. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain survivalist</body></html>");
        }
        baseTextField.setName("chanceforSVforMek");
        mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        mekPilotSkillsSpring.add(new JLabel("IM", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain iron man. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain iron man</body></html>");
        }
        baseTextField.setName("chanceforIMforMek");
        mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        mekPilotSkillsSpring.add(new JLabel("MA", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain maneuvering ace. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain maneuvering ace</body></html>");
        }
        baseTextField.setName("chanceforMAforMek");
        mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        mekPilotSkillsSpring.add(new JLabel("NAP", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain Natural Aptitude Piloting. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain Natural Aptitude Piloting</body></html>");
        }
        baseTextField.setName("chanceforNAPforMek");
        mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        mekPilotSkillsSpring.add(new JLabel("NAG", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain Natural Aptitude Gunnery. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain Natural Aptitude Gunnery</body></html>");
        }
        baseTextField.setName("chanceforNAGforMek");
        mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        mekPilotSkillsSpring.add(new JLabel("AT", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain Astech. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain Astech</body></html>");
        }
        baseTextField.setName("chanceforATforMek");
        mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        mekPilotSkillsSpring.add(new JLabel("TG", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain tactical genius. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain tactical genius</body></html>");
        }
        baseTextField.setName("chanceforTGforMek");
        mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        mekPilotSkillsSpring.add(new JLabel("WS", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain weapon specialist. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain weapon specialist</body></html>");
        }
        baseTextField.setName("chanceforWSforMek");
        mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        mekPilotSkillsSpring.add(new JLabel("G/B", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain gunnery/Ballistic. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain gunnery/Ballistic</body></html>");
        }
        baseTextField.setName("chanceforGBforMek");
        mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        mekPilotSkillsSpring.add(new JLabel("G/L", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain gunnery/laser. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain gunnery/laser</body></html>");
        }
        baseTextField.setName("chanceforGLforMek");
        mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        mekPilotSkillsSpring.add(new JLabel("G/M", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain gunnery/missile. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain gunnery/missile</body></html>");
        }
        baseTextField.setName("chanceforGMforMek");
        mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        mekPilotSkillsSpring.add(new JLabel("Trait", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain a trait. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain a trait</body></html>");
        }
        baseTextField.setName("chanceforTNforMek");
        mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        mekPilotSkillsSpring.add(new JLabel("EI", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain Enhanced Interface. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain Enhanced Interface</body></html>");
        }
        baseTextField.setName("chanceforEIforMek");
        mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        mekPilotSkillsSpring.add(new JLabel("GT", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain gifted. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain gifted</body></html>");
        }
        baseTextField.setName("chanceforGTforMek");
        mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        mekPilotSkillsSpring.add(new JLabel("QS", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain Quick Study. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain Quick Study</body></html>");
        }
        baseTextField.setName("chanceforQSforMek");
        mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        mekPilotSkillsSpring.add(new JLabel("MT", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain the Med Tech skill. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain the Med Tech skill</body></html>");
        }
        baseTextField.setName("chanceforMTforMek");
        mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        mekPilotSkillsSpring.add(new JLabel("Edge", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain the Edge skill. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain the Edge skill</body></html>");
        }
        baseTextField.setName("chanceforEDforMek");
        mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        mekPilotSkillsSpring.add(new JLabel("VDNI", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain the VDNI skill. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain the VDNI skill</body></html>");
        }
        baseTextField.setName("chanceforVDNIforMek");
        mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        mekPilotSkillsSpring.add(new JLabel("BVDNI", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain the buffered VDNI skill. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain the buffered VDNI skill</body></html>");
        }
        baseTextField.setName("chanceforBVDNIforMek");
        mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(3);
        baseTextField.setMaximumSize(fieldSize);
        baseTextField.setPreferredSize(fieldSize);
        mekPilotSkillsSpring.add(new JLabel("PS", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Set cost for a pilot to gain the Pain Shunt skill. Zero to disable</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain the Pain Shunt skill</body></html>");
        }
        baseTextField.setName("chanceforPSforMek");
        mekPilotSkillsSpring.add(baseTextField);

        SpringLayoutHelper.setupSpringGrid(mekPilotSkillsSpring, 4);

        add(mekPilotSkillsSpring);
	}

}