package server.campaign.commands.admin;

import java.util.StringTokenizer;

import common.Unit;
import server.campaign.CampaignMain;
import server.campaign.SPlayer;
import server.campaign.commands.Command;
import server.campaign.pilot.SPilot;
import server.MWChatServer.auth.IAuthenticator;

/**
 * @author Torren (Jason Tighe)
 */
public class RemovePilotCommand implements Command {
	
	int accessLevel = IAuthenticator.ADMIN;
	String syntax = "Player Name#Type/ALL#weight/ALL#Position[Not used if ALL is selected]";
	public int getExecutionLevel(){return accessLevel;}
	public void setExecutionLevel(int i) {accessLevel = i;}
	public String getSyntax() { return syntax;}
	
	public void process(StringTokenizer command,String Username) {
		
		if (accessLevel != 0) {
			int userLevel = CampaignMain.cm.getServer().getUserLevel(Username);
			if(userLevel < getExecutionLevel()) {
				CampaignMain.cm.toUser("AM:Insufficient access level for command. Level: " + userLevel + ". Required: " + accessLevel + ".",Username,true);
				return;
			}
		}
		
        String player = "";
        String type = "";
        String weight = "";
        String position = "";

        try{
            player = command.nextToken();
            type = command.nextToken();
            weight = command.nextToken();
            position = command.nextToken();
        }catch (Exception ex){
            CampaignMain.cm.toUser("Syntanx RemovePilot#Player#Type/ALL#weight/ALL#Position[Not used if ALL is selected].",Username);
            return;
        }
        SPlayer p =  CampaignMain.cm.getPlayer(player);
        
        if ( type.equalsIgnoreCase("all") ){
           if ( weight.equalsIgnoreCase("all") ){
                   p.getPersonalPilotQueue().flushQueue();
                   if(CampaignMain.cm.isUsingMySQL()) {
                	   CampaignMain.cm.MySQL.deletePlayerPilots(p.getDBId());
                   }
           }
           else{
               p.getPersonalPilotQueue().getPilotQueue(Unit.MEK,Unit.getWeightIDForName(weight)).clear();
               p.getPersonalPilotQueue().getPilotQueue(Unit.PROTOMEK,Unit.getWeightIDForName(weight)).clear();
               if(CampaignMain.cm.isUsingMySQL()) {
            	   CampaignMain.cm.MySQL.deletePlayerPilots(p.getDBId(), Unit.MEK, Unit.getWeightIDForName(weight));
            	   CampaignMain.cm.MySQL.deletePlayerPilots(p.getDBId(), Unit.PROTOMEK, Unit.getWeightIDForName(weight));
               }
           }
        }else if (weight.equalsIgnoreCase("all") ){
            for ( int weightClass = 0; weightClass <= Unit.ASSAULT; weightClass++ ){
                p.getPersonalPilotQueue().getPilotQueue(Unit.getTypeIDForName(type),weightClass).clear();
                if(CampaignMain.cm.isUsingMySQL()) {
                	CampaignMain.cm.MySQL.deletePlayerPilots(p.getDBId(), Unit.getTypeIDForName(type), weightClass);
                }
            }
        }//Ok so lets try a position
        else{
            if ( position.equalsIgnoreCase("all") ){
            	if(CampaignMain.cm.isUsingMySQL()) {
            		int numpilots = p.getPersonalPilotQueue().getPilotQueue(Unit.getTypeIDForName(type), Unit.getWeightIDForName(weight)).size();
            		for(int x = 0; x < numpilots; x++)
            			CampaignMain.cm.MySQL.deletePilot(((SPilot)p.getPersonalPilotQueue().getPilotQueue(Unit.getTypeIDForName(type), Unit.getWeightIDForName(weight)).get(x)).getPilotId());
            	}
                p.getPersonalPilotQueue().getPilotQueue(Unit.getTypeIDForName(type),Unit.getWeightIDForName(weight)).clear();
            }
            else if ( position.indexOf("-") > 0){
                int end = Integer.parseInt(position.substring(0,position.indexOf("-")));
                int start = Integer.parseInt(position.substring(position.indexOf("-")+1));
                //search backwards through the queue so you stay ahead of the shrinkinage.
                for (int pos = start ;pos >= end; pos--){
                	if(CampaignMain.cm.isUsingMySQL()) {
                		CampaignMain.cm.MySQL.deletePilot(((SPilot)p.getPersonalPilotQueue().getPilotQueue(Unit.getTypeIDForName(type), Unit.getWeightIDForName(weight)).get(pos)).getPilotId());
                	}
                    p.getPersonalPilotQueue().getPilot(Unit.getTypeIDForName(type),Unit.getWeightIDForName(weight),pos);
                }
            }
            else {
               	if(CampaignMain.cm.isUsingMySQL()) {
            		CampaignMain.cm.MySQL.deletePilot(((SPilot)p.getPersonalPilotQueue().getPilotQueue(Unit.getTypeIDForName(type), Unit.getWeightIDForName(weight)).get(Integer.parseInt(position))).getPilotId());
            	}   
                p.getPersonalPilotQueue().getPilot(Unit.getTypeIDForName(type),Unit.getWeightIDForName(weight),Integer.parseInt(position));
            }
        }

        CampaignMain.cm.toUser("PL|PPQ|"+p.getPersonalPilotQueue().toString(true),player,false);
		CampaignMain.cm.doSendModMail("NOTE",Username+" has removed pilots from "+player+"'s PPQ");
        CampaignMain.cm.toUser(Username+" has removed pilots from your PPQ",player);

		
	}
}

