package server.campaign.commands.admin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import server.MWServ;
import server.MWChatServer.auth.IAuthenticator;
import server.campaign.CampaignMain;
import server.campaign.commands.Command;


public class AdminRequestBuildTableCommand implements Command {

	/*
	 * This command allows an Admin to upload a single build table
	 * from a directory on their local machine.  The directory structure
	 * on the local machine must match that on the server - i.e, ./buildtables/rare
	 * ./buildtables/reward and ./buildtables/standard.  The replacement build
	 * table is put into place and a backup of the original build table is created.
	 */
	
	int accessLevel = IAuthenticator.ADMIN;
	String syntax = "list,get[#folder#filename]";
	public int getExecutionLevel(){return accessLevel;}
	public void setExecutionLevel(int i) {accessLevel = i;}
	public String getSyntax() { return syntax;}
	
	public void process(StringTokenizer command,String Username) {

		//access level check
		int userLevel = CampaignMain.cm.getServer().getUserLevel(Username);
		if(userLevel < getExecutionLevel()) {
			CampaignMain.cm.toUser("Insufficient access level for command. Level: " + userLevel + ". Required: " + accessLevel + ".",Username,true);
			return;
		}
		String subcommand = command.nextToken();
		if(subcommand.equalsIgnoreCase("list")) {
			StringBuilder toReturn = new StringBuilder();
			String folderDelimiter="?";
			String fileDelimiter="*";
			String[] folderList = {"standard","rare","reward"};

			for (int i = 0; i < folderList.length; i++) {
				File currF = new File("./data/buildtables/" + folderList[i]);
				toReturn.append(folderList[i]);
				toReturn.append(folderDelimiter);
				if (!currF.exists() || !currF.isDirectory())
					continue;
				File fileNames[] = currF.listFiles();
				
				for (File currFile : fileNames) {
					if(currFile.getName().endsWith("txt")) { 
						toReturn.append(currFile.getName());
						toReturn.append(fileDelimiter);
					}
				}
				toReturn.append(folderDelimiter);
			}
			CampaignMain.cm.toUser("BT|LS|"+ toReturn.toString(), Username, false);
			MWServ.mwlog.dbLog("Sending: " + toReturn.toString());				
			
			return;
		} else if (subcommand.equalsIgnoreCase("get")) {
			StringBuilder toReturn = new StringBuilder();
			String folder = "";
			String table = "";
			if(command.hasMoreTokens())
				folder = command.nextToken();
			if(command.hasMoreTokens())
				table = command.nextToken();
			if(folder.length() == 0 || table.length() == 0) {
				CampaignMain.cm.toUser("Bad Build Table Request: " + (folder.length()==0 ? "Empty folder name" : "Empty file name"), Username, true);
				return;
			}
			File file = new File("./data/buildtables/" + folder + "/" + table);
			if(!file.exists()) {
				CampaignMain.cm.toUser("Bad Build Table Request: " + folder + "/" + table + " does not exist.", Username, true);
				return;
			}
			// The request is good, so send it.
			try {
				FileInputStream in = new FileInputStream(file);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				try {
					while(br.ready()) {
						toReturn.append("|" + br.readLine());
					}
					br.close();
					in.close();
				} catch (IOException ex) {
					
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			CampaignMain.cm.toUser("BT|BT|" + folder + "|" + table + toReturn.toString(), Username, false);
			
		}
	}
}