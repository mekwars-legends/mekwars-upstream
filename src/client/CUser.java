/*
 * MekWars - Copyright (C) 2004 
 * 
 * Derived from MegaMekNET (http://www.sourceforge.net/projects/megameknet)
 * Original author Helge Richter (McWizard)
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

package client;

import java.util.StringTokenizer;
import java.awt.Color;

import common.House;
import common.util.StringUtils;

/*
  Class for User objects held in userlist
 */

public class CUser implements Comparable {

	protected String Name;
	protected String Addon;
	protected int Userlevel = 0;
	protected String PlayerHouse;
	protected String Fluff;
	protected int Exp;
	protected float Rating;
	protected int Status;
	protected String HTMLColor;
	protected Color RGBColor;
	protected String Country;
	protected boolean LoggedIn = false;
	protected boolean isMerc = false;
	protected boolean isInvis = false;

	/**
	 * Empty CUser.
	 */
	public CUser() {
		Name = "";
		Addon = "";
		PlayerHouse = "";
		Fluff = "";
		Exp = 0;
		Rating = 0;
		Status = MWClient.STATUS_LOGGEDOUT;
		HTMLColor = "black";
		RGBColor = Color.black;
		Country = "";
		isMerc = false;
		isInvis = false;
	}

	/**
	 * New CUser w/ data. Called NU|MWClientInfo.toString()|NEW/NONE command.
	 */
	public CUser(String data) {

		StringTokenizer ST = null;

		Addon = "";
		PlayerHouse = "";
		Fluff = "";
		Exp = 0;
		Rating = 0;
		Status = MWClient.STATUS_LOGGEDOUT;
		RGBColor = Color.black;

		ST = new StringTokenizer(data,"~");
		try {
			Name = ST.nextToken();
			HTMLColor = ST.nextToken();
			Country = ST.nextToken();
			Userlevel = Integer.parseInt(ST.nextToken());
			isInvis = Boolean.parseBoolean(ST.nextToken());
		} catch (Exception ex) {
			MWClient.mwClientLog.clientErrLog("Error in deserializing user");
		}
	}


	public void setName(String tname) {Name = tname;}
	public String getName() {return Name;}
	public void setAddon(String taddon) {Addon = taddon;}
	public String getAddon() {return Addon;}
	public void setCountry(String tcountry) {Country = tcountry;}
	public String getCountry() {return Country;}
	public void setColor(String tcolor) {HTMLColor = tcolor;}
	public String getColor() {return HTMLColor;}
	public void setUserlevel(int tlevel) {Userlevel = tlevel;}
	public int getUserlevel() {return Userlevel;}
	public void setFluff(String tfluff) {Fluff = tfluff;}
	public String getFluff() {return Fluff;}
	public String getHouse() {return PlayerHouse;}
	public void setExp(int texp) {Exp = texp;}
	public int getExp() {return Exp;}
	public void setRating(float trating) {Rating = trating;}
	public float getRating() {return Rating;}

	public boolean isInvis(){return isInvis;}

	public void setMercStatus(boolean merc){isMerc = merc;}
	public boolean isMerc(){return this.isMerc;}


	public Color getRGBColor() {return RGBColor;}

	public void setCampaignData(MWClient mwclient, String data) {
		StringTokenizer ST = new StringTokenizer(data, "#");
		try {

			Exp = Integer.parseInt(ST.nextToken());
			Rating = Float.parseFloat(ST.nextToken());
			setStatus(Integer.parseInt(ST.nextToken()));

			if (ST.hasMoreTokens())
				Fluff = ST.nextToken();
			if (Fluff.equals(" ") || Fluff.equals("0"))
				Fluff = "";

			if (ST.hasMoreTokens())
				PlayerHouse = ST.nextToken();
			if (ST.hasMoreElements())
				isMerc = Boolean.parseBoolean(ST.nextToken());

			//Abbreviation and Color from House (sed to be sent as part of player update)
			House playerH = mwclient.getData().getHouseByName(PlayerHouse);
			Addon = playerH.getAbbreviation();

			RGBColor = Color.black;
			RGBColor = StringUtils.html2Color(playerH.getHousePlayerColor());
		} catch (Exception ex) {
			MWClient.mwClientLog.clientErrLog(ex);
		}
	}

	public void clearCampaignData()
	{
		Addon = "";
		PlayerHouse = "";
		Fluff = "";
		Exp = 0;
		Rating = 0;
		Status = MWClient.STATUS_LOGGEDOUT;
		RGBColor = Color.black;
	}

	public void setStatus(int status)
	{
		Status = status;
		if (Status == MWClient.STATUS_LOGGEDOUT)
		{
			LoggedIn = false;
			clearCampaignData();
		}
		else
		{
			if (Status == MWClient.STATUS_RESERVE || Status == MWClient.STATUS_ACTIVE ||
					Status == MWClient.STATUS_FIGHTING)
			{LoggedIn = true;}
		}
	}

	public int getStatus() {return Status;}

	public boolean isLoggedIn() {return LoggedIn;}

	public String getShortInfo() {
		String info;
		info = "<html><body>" + getName();
		if (Userlevel >= 100 && Userlevel < 200) {info += " (Moderator)";}
		if (Userlevel >= 200) {info += " (Admin)";}
		if (!Country.equals("unknown")) {info += " (" + getCountry() + ")";}
		info += "</body></html>";
		return info;
	}

	public String getInfo(boolean removeImages) {

		String info;
		info = "<html><body>" + Name;
		if (!Addon.equals("") && LoggedIn) {info += " [" + Addon + "]";}
		if (Userlevel >= 100 && Userlevel < 200) {info += " (Moderator)";}
		if (Userlevel >= 200) {info += " (Admin)";}
		if (!Country.equals("unknown")) {info += " (" + getCountry() + ")";}
		if (LoggedIn) {
			info += "<br>Exp: " + Exp;

			//only show the rating if its real. will be 0.0 if server is hiding ELOs.
			if (Rating >= 1) {info += " Rating: " + Rating;}

			if (!PlayerHouse.equals("")) {info += "<br>Fights for " + PlayerHouse;}
			if (!Fluff.equals("")) {

				//if the user wants to, remove any img tags in fluff
				String toShow = "";
				if (removeImages) {

					int start = Fluff.indexOf("<img");
					int finish = -1;

					if (start != -1)
						finish = Fluff.indexOf(">",start);

					if (start != -1 && finish != -1) {
						String firstHalf = Fluff.substring(0,start);
						String secondHalf = Fluff.substring(finish + 1, Fluff.length());

						toShow = firstHalf + "(img blocked)" + secondHalf;
					}

					info += "<br>" + toShow;
				} 

				//otherwise, just display the fluff
				else {
					info += "<br>" + Fluff;
				}  
			}

		}
		info += "</body></html>";
		return info;
	}

	/**
	 * Comparable, for PlayerNameDialog.
	 * Don't use elsewhere =)
	 */
	public int compareTo(Object o) {
		if (!(o instanceof CUser))
			return 0;

		CUser u = (CUser)o;
		return this.getName().compareTo(u.getName());
	}

}