/*
 * MekWars - Copyright (C) 2005
 * 
 * Original author - nmorris (urgru@users.sourceforge.net)
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 */
package admin;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;

import client.MWClient;
import client.CUser;

public class ModeratorUserlistPopupMenu extends JMenu implements ActionListener {
	
	/**
     * 
     */
    private static final long serialVersionUID = 9145688971748962135L;
    //variables
	private MWClient mwclient;
	private int userLevel = 0;
	private String userName = "";
    
	public ModeratorUserlistPopupMenu () {
		super("Player Status");
	}
	
	public void createMenu(MWClient client, CUser user) {
		
		//save things
		this.mwclient = client;
		userLevel = this.mwclient.getUser(this.mwclient.getUsername()).getUserlevel();
        userName = user.getName();
        
		//format
		JMenuItem item;
		
		item = new JMenuItem("Ignore");
		item.setActionCommand("MMUTE|"+userName);
		item.addActionListener(this);
		this.add(item);
		this.addSeparator();
		item = new JMenuItem("Check");
		item.setActionCommand("CKU|"+userName);
		item.addActionListener(this);
		if ( userLevel >= mwclient.getData().getAccessLevel("Check") )
	        this.add(item);
		item = new JMenuItem("Deactivate");
		item.setActionCommand("DAU|"+userName);
		item.addActionListener(this);
		if ( userLevel >= mwclient.getData().getAccessLevel("ModDeactivate") )
	        this.add(item);
		item = new JMenuItem("Kick");
		item.setActionCommand("KK|"+userName);
		item.addActionListener(this);
		this.add(item);
		item = new JMenuItem("Unlock Armies");
		item.setActionCommand("UUA|"+userName);
		item.addActionListener(this);
		if ( userLevel >= mwclient.getData().getAccessLevel("UnlockLances") )
	        this.add(item);
        
        JMenu grantMenu = new JMenu();
        grantMenu.setText("Grant");
        
        item = new JMenuItem("Exp");
        item.setActionCommand("GE|"+userName);
        item.addActionListener(this);
        if (userLevel >= mwclient.getData().getAccessLevel("GrantEXP"))
            grantMenu.add(item);
        item = new JMenuItem(client.getserverConfigs("FluLongName"));
        item.setActionCommand("GI|"+userName);
        item.addActionListener(this);
        if (userLevel >= mwclient.getData().getAccessLevel("GrantInfluence"))
            grantMenu.add(item);
        item = new JMenuItem(client.getserverConfigs("MoneyLongName"));
        item.setActionCommand("GM|"+userName);
        item.addActionListener(this);
        if (userLevel >= mwclient.getData().getAccessLevel("GrantMoney"))
            grantMenu.add(item);
        item = new JMenuItem("Reward Points");
        item.setActionCommand("GRP|"+userName);
        item.addActionListener(this);
        if (userLevel >= mwclient.getData().getAccessLevel("GrantReward"))
            grantMenu.add(item);
        if (grantMenu.getItemCount() > 0)
            this.add(grantMenu);

        
	}
	
	public void actionPerformed(ActionEvent actionEvent) {
		
		//command helpers
		String s = actionEvent.getActionCommand();
		StringTokenizer st = new StringTokenizer(s,"|");
		String command = st.nextToken();
		String userName = ""; 
		
		//mod commands
		if (command.equals("KK") && st.hasMoreElements()) {
			
			userName = st.nextToken();
			{
				
				mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "kick " + userName);
			}
		}
		if (command.equals("MMUTE") && st.hasMoreElements()) {
			
			userName = st.nextToken();
			{
				
				mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "ignore " + userName);
			}
		}
		if (command.equals("CKU") && st.hasMoreElements()) {
			
			userName = st.nextToken();
			{
				
				mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c check#" + userName);
			}
		}
		if (command.equals("DAU") && st.hasMoreElements()) {
			
			userName = st.nextToken();
			{
				
				mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c moddeactivate#" + userName);
			}
		}
		if (command.equals("UUA") && st.hasMoreElements()) {
			
			userName = st.nextToken();
			{
				
				mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c unlocklances#" + userName);
			}
		}
		
        if (command.equals("GI") && st.hasMoreElements()) {
            
            userName = st.nextToken();
            {
                

                String exp = JOptionPane.showInputDialog(null, mwclient
                        .moneyOrFluMessage(false, true,-1)
                        + " Amount,- to remove");
                if (exp == null || exp.length() == 0)
                    return;

                mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c grantinfluence#" + userName
                        + "#" + exp);            
                }
        }
        if (command.equals("GM") && st.hasMoreElements()) {
            
            userName = st.nextToken();
            {
                
                String exp = JOptionPane.showInputDialog(null, mwclient
                        .moneyOrFluMessage(true, true,-1)
                        + " Amount,- to remove");
                if (exp == null || exp.length() == 0)
                    return;

                mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c grantmoney#" + userName
                        + "#" + exp);
            }
        }
        if (command.equals("GE") && st.hasMoreElements()) {
            
            userName = st.nextToken();
            {
                
                String exp = JOptionPane
                .showInputDialog(null, "Exp Amount,- to remove");
                if (exp == null || exp.length() == 0)
                    return;

                mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c grantexp#" + userName + "#"
                + exp);
            }
        }
        if (command.equals("GRP") && st.hasMoreElements()) {
            
            userName = st.nextToken();
            {
                
                String exp = JOptionPane.showInputDialog(null,
                "Reward Amount,- to remove");
                if (exp == null || exp.length() == 0)
                    return;

                mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c grantreward#" + userName
                + "#" + exp);
            }
        }
	}
	
}//end ModeratorPopupMenu class



