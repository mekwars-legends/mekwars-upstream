/*
 * MekWars - Copyright (C) 2006 
 * 
 * Original author: nmorris (urgru@users.sourceforge.net)
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

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.sun.org.apache.xalan.internal.xsltc.compiler.Pattern;

import client.MWClient;

public final class RegisterNameDialog implements ActionListener {
	
	private final String okayCommand = "okay";
	private final String cancelCommand = "cancel";
	
	private final JTextField usernameField = new JTextField();
	private final JPasswordField passwordField1 = new JPasswordField();
	private final JPasswordField passwordField2 = new JPasswordField();
	private final JTextField emailField1 = new JTextField();
	private final JTextField emailField2 = new JTextField();
	
	private final JButton okayButton = new JButton("OK");
	private final JButton cancelButton = new JButton("Cancel");
	
	private JDialog dialog;
	private JOptionPane pane;
	
	public RegisterNameDialog(MWClient mwclient) {
		
		// Set the actions to generate
		okayButton.setActionCommand(okayCommand);
		cancelButton.setActionCommand(cancelCommand);
		
		// Set the listeners to this object
		usernameField.addActionListener(this);
		passwordField1.addActionListener(this);
		passwordField2.addActionListener(this);
		emailField1.addActionListener(this);
		emailField2.addActionListener(this);
		okayButton.addActionListener(this);
		cancelButton.addActionListener(this);
		
		//set tool tips (balloon help)
		usernameField.setToolTipText("Username to register");
		passwordField1.setToolTipText("Password to set.");
		passwordField2.setToolTipText("Confirm password.");
		emailField1.setToolTipText("Email address.");
		emailField2.setToolTipText("Confirm email address.");
		
		//Create the panel holding the labels and text fields
		JPanel fieldPanel = new JPanel(new GridLayout(5,2), false);
		fieldPanel.add(new JLabel("Username: ", SwingConstants.LEFT));
		fieldPanel.add(usernameField);
		fieldPanel.add(new JLabel("Password1: ", SwingConstants.LEFT));
		fieldPanel.add(passwordField1);
		fieldPanel.add(new JLabel("Password2: ", SwingConstants.LEFT));
		fieldPanel.add(passwordField2);
		if(Boolean.parseBoolean(mwclient.getserverConfigs("REQUIREEMAILFORREGISTRATION"))) {
			fieldPanel.add(new JLabel("Email: ", SwingConstants.LEFT));
			fieldPanel.add(emailField1);
			fieldPanel.add(new JLabel("Confirm Email: ", SwingConstants.LEFT));
			fieldPanel.add(emailField2);
		}
				
		
		JPanel messagePanel = new JPanel();
		messagePanel.add(new JLabel("<HTML><b><center>" +
				"Note: password will be stored<br>" +
				"and transmitted in plain text.</b></center></HTML>"));
		
		JPanel textPanel = new JPanel();
		textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
		textPanel.add(fieldPanel);
		textPanel.add(messagePanel);
		
		//Create the panel that will hold the entire UI
		JPanel mainPanel = new JPanel(false);
		
		//Set the user's options
		Object[] options = {okayButton, cancelButton};
		
		//Create the pane containing the buttons
		pane = new JOptionPane(textPanel, JOptionPane.PLAIN_MESSAGE,JOptionPane.DEFAULT_OPTION, null, options,passwordField1);
		
		//Create the main dialog and set the default button
		dialog = pane.createDialog(mainPanel,"Register Nickname");
		dialog.getRootPane().setDefaultButton(okayButton);
		
		//Fill username field with current name
		usernameField.setText(mwclient.getPlayer().getName());
		
		//Show the dialog and get the user's input
		dialog.setVisible(true);
		dialog.requestFocus();
		usernameField.requestFocus();
		
		if (pane.getValue() == okayButton) {
			
			String pass1 = String.valueOf(passwordField1.getPassword());
			String pass2 = String.valueOf(passwordField2.getPassword());
			
			boolean emailValid = false;
			boolean passwordValid = false;
			StringBuilder toUser = new StringBuilder();
			
			if (!pass1.equals(pass2)) 
				toUser.append("CH|CLIENT: Passwords did not match. Registration failed.<br>");
			else
				passwordValid = true;
			
			if (Boolean.parseBoolean(mwclient.getserverConfigs("REQUIREEMAILFORREGISTRATION"))) {
				if (emailField1.getText().equalsIgnoreCase(emailField2.getText()))
					if(emailIsValid(emailField1.getText().toUpperCase()))
						emailValid = true;
					else
						toUser.append("CH|CLIENT: " + emailField1.getText() + " is not a valid email address. Registration failed.<br>");
				else
					toUser.append("CH|CLIENT: Email fields did not match.  Registration failed.");
			} else
				emailValid = true;
			
			if(emailValid && passwordValid) {
				if(Boolean.parseBoolean(mwclient.getserverConfigs("REQUIREEMAILFORREGISTRATION"))) {
					mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "register " + usernameField.getText() + "," + emailField1.getText() + "," + String.valueOf(passwordField1.getPassword()));
				} else {
					mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "register " + usernameField.getText() + "," + String.valueOf(passwordField1.getPassword()));
				}
			} else {
						mwclient.doParseDataInput(toUser.toString());
				}
			}
		mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c setclientversion#" + mwclient.myUsername.trim()+ "#" + MWClient.CLIENT_VERSION);
		
		dialog.dispose();
	}
	
	public void actionPerformed(ActionEvent e) {
		
		String command = e.getActionCommand();
		if (command.equals(okayCommand)) {
			pane.setValue(okayButton);
			dialog.dispose();
		} else if (command.equals(cancelCommand)) {
			pane.setValue(cancelButton);
			dialog.dispose();
		}
	}
	
	private boolean emailIsValid(String email) {
		return email.matches("[A-Z0-9._%+-]+@([A-Z0-9.-]+.)+(?:[A-Z]{2}|com|org|net|gov|mil|biz|info|name|aero|biz|info|jobs|museum)");
		 
	}
}
