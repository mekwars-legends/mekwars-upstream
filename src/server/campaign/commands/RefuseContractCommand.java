/*
 * MekWars - Copyright (C) 2004 
 * 
 * Derived from MegaMekNET (http://www.sourceforge.net/projects/megameknet)
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

package server.campaign.commands;

import java.util.StringTokenizer;
import server.campaign.CampaignMain;
import server.campaign.SPlayer;
import server.campaign.mercenaries.ContractInfo;

public class RefuseContractCommand implements Command {
	
	int accessLevel = 0;
	public int getExecutionLevel(){return accessLevel;}
	public void setExecutionLevel(int i) {accessLevel = i;}
	
	public void process(StringTokenizer command,String Username) {
		
		if (accessLevel != 0) {
			int userLevel = CampaignMain.cm.getServer().getUserLevel(Username);
			if(userLevel < getExecutionLevel()) {
				CampaignMain.cm.toUser("Insufficient access level for command. Level: " + userLevel + ". Required: " + accessLevel + ".",Username,true);
				return;
			}
		}
		
		boolean contractCancelled = false;
		String receivingPlayerName = "";
		boolean offeringPlayerFound = false;
		String offeringPlayerName = (String)command.nextElement();
		SPlayer offeringPlayer = CampaignMain.cm.getPlayer(offeringPlayerName);
		for (int i = 0; i < CampaignMain.cm.getUnresolvedContracts().size(); i++) {
			ContractInfo info = (ContractInfo)(CampaignMain.cm.getUnresolvedContracts().get(i));
			if (info.getOfferingPlayer() == offeringPlayer) {
				offeringPlayerFound = true;
				//MMServ.mmlog.mainLog("CANCEL: Offering player found set to true");
				//if contract belong to offering player, check to see if it is for this player.
				receivingPlayerName = (info.getPlayerName());
				if (CampaignMain.cm.getPlayer(receivingPlayerName) == CampaignMain.cm.getPlayer(Username)) {//player can kill contract offer
					CampaignMain.cm.getUnresolvedContracts().remove(i);
					contractCancelled = true;
					CampaignMain.cm.toUser("You refused the contract offered by " + offeringPlayerName,Username,true);
					CampaignMain.cm.toUser(Username + " refused your contract offer",offeringPlayerName,true);
				}//end if (contract is offered to player attempting to cancel)
			}//end if(contract offered by proper player)
		}//end for loop
		if (contractCancelled == false) {
			if (offeringPlayerFound == false) {//not found
				CampaignMain.cm.toUser("This player has no outstanding contracts",Username,true);
			}
			else {//contract is for someone else
				CampaignMain.cm.toUser("This player has not offered you a contract.",Username,true);
			}
		}//end if contract not cancelled.
	}//end process
}