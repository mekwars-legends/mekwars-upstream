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

package client.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.Arrays;
import java.util.Comparator;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.LineBorder;
import javax.swing.SpringLayout;
import javax.swing.BoxLayout;

import megamek.common.Entity;

import client.MWClient;
import client.campaign.CCampaign;
import client.campaign.CPlayer;
import client.campaign.CUnit;
import client.gui.dialog.MechDetailDisplay;

import common.House;
import common.Unit;
import common.UnitFactory;
import common.util.SpringLayoutHelper;
import common.util.UnitUtils;


/**
 * SHouse Status Panel
 */
@SuppressWarnings({"unchecked","serial"})
public class CHSPanel extends JPanel {
	
	MWClient mwclient;
	CPlayer thePlayer;
	CCampaign theCampaign;
	JEditorPane mainPane = new JEditorPane();
	JScrollPane scrollPane = new JScrollPane();
	MyHTMLEditorKit kit = new MyHTMLEditorKit();
	GridBagConstraints gridBagConstraints;
	
	private JPanel pnlBtns = new JPanel();
	private JPanel hsButtonSpringPanel = new JPanel();
	
	private JButton buyNewButton = new JButton();
	private JButton buyUsedButton = new JButton();
	private JLabel lblInfo = new JLabel();
	private BuyPopupListener myPopup = null;
	
	//Needed to internally store SHouse Status
	private String HouseName;
	//hastable of Hashtables
	private TreeMap<String,String> componentsInfo;
	private TreeMap<String,TreeMap<String,String>> factoriesInfo;
	private TreeMap<String,Vector<HSMek>> unitsInfo;
	
	public CHSPanel(MWClient client) {
		
		setLayout(new GridBagLayout());
		this.mwclient = client;
		theCampaign = mwclient.getCampaign();
		thePlayer = theCampaign.getPlayer();
		myPopup = new BuyPopupListener();
		
		mainPane.setEditorKit(kit);
		mainPane.setEditable(false);
		mainPane.addHyperlinkListener(new MMNetHyperLinkListener(mwclient,this));
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setViewportBorder(new LineBorder(new Color(0, 0, 0)));
		scrollPane.setViewportView(mainPane);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		this.add(scrollPane,gridBagConstraints);
		
		//set up the button row
		pnlBtns.setLayout(new BoxLayout(pnlBtns, BoxLayout.Y_AXIS));
		hsButtonSpringPanel = new JPanel(new SpringLayout());
		
		//button to buy new units
		buyNewButton.setText("Buy New");
		buyNewButton.addActionListener(new ActionListener()
				{public void actionPerformed(ActionEvent evt)
				{buyNewButtonActionPerformed(evt);}});
		buyNewButton.addMouseListener(new MouseAdapter()
				{@Override public void mousePressed(MouseEvent evt)
				{buyNewUnitMouseEvent(evt);}});
		hsButtonSpringPanel.add(buyNewButton);
		
		//button to buy used units
		buyUsedButton.setText("Buy Used");
		buyUsedButton.addActionListener(new ActionListener()
				{public void actionPerformed(ActionEvent evt)
				{buyUsedButtonActionPerformed(evt);}});
		buyUsedButton.addMouseListener(new MouseAdapter()
				{@Override public void mousePressed(MouseEvent evt)
				{buyUsedUnitMouseEvent(evt);}});
		hsButtonSpringPanel.add(buyUsedButton);
		
		SpringLayoutHelper.setupSpringGrid(hsButtonSpringPanel, 1, 3);
		
		//set up lblInfo, which is used when rolling over factories
		lblInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblInfo.setSize(0,0);
		
		pnlBtns.add(lblInfo);
		pnlBtns.add(hsButtonSpringPanel);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 0.0;
		//gridBagConstraints.insets = new Insets(0, 0, 0, 0);
		this.add(pnlBtns,gridBagConstraints);
		
		//make information holders
		componentsInfo = new TreeMap<String,String>();
		factoriesInfo = new TreeMap<String,TreeMap<String,String>>();
		unitsInfo = new TreeMap<String,Vector<HSMek>>();
	}
	
	/**
	 * Set faction name. Called in response to HS|FN| command.
	 */
	public void setFactionName(String name) {
		HouseName = name;
	}
	
	/**
	 * Clear all faction data.
	 */
	public void clearHouseStatusData() {
		componentsInfo.clear();
		factoriesInfo.clear();
		unitsInfo.clear();
	}
	
	/** 
	 * Add a unit to the units' hash. Called from HS.java
	 * when client receives HS|AU|data command.
	 */
	public void addFactionUnit(String unitData) {
		
		StringTokenizer tokenizer = new StringTokenizer(unitData,"$");
		
		String weight = tokenizer.nextToken();
		String type = tokenizer.nextToken();
		
		HSMek currHSUnit = new HSMek(mwclient, tokenizer);//reads rest of tokens
		
		//if there isn't a vector for this type + weight combo already, create one
		Vector<HSMek> weightAndTypeVec = unitsInfo.get(weight + "$" + type);
		if (weightAndTypeVec == null) {
			weightAndTypeVec = new Vector<HSMek>(1,1);
			unitsInfo.put(weight + "$" + type, weightAndTypeVec);
		}
		
		//add the unit to the vector
		weightAndTypeVec.add(currHSUnit);
	}
	
	/**
	 * Remove a unit from the units' hash+vector sets. Called from
	 * HS.java when client receives HS|RU| command.
	 */
	public void removeFactionUnit(String unitData) {
		
		StringTokenizer tokenizer = new StringTokenizer(unitData,"$");
		
		String weight = tokenizer.nextToken();
		String type = tokenizer.nextToken();
		int unitID = Integer.valueOf(tokenizer.nextToken());
		
		Vector<HSMek> weightAndTypeVec = unitsInfo.get(weight + "$" + type);
		
		//if weight and type are null, there is no way to remove the unit.
		if (weightAndTypeVec == null)
			return;
		
		//iterate through all units of this wieght&type. remove matching id.
		Iterator i = weightAndTypeVec.iterator();
		while (i.hasNext()) {
			HSMek currHSMek = (HSMek)i.next();
			if (currHSMek.getUnitID() == unitID) {
				i.remove();
				return;
			}
		}
	}
	
	/**
	 * Change the component display for a given weight & type combo. Called
	 * from HS.java when client receives HS|CC| command. Because components
	 * are so simple, change is always used and there are no adds/removes.
	 */
	public void changeFactionComponents(String componentData) {
		
		StringTokenizer tokenizer = new StringTokenizer(componentData,"$");
		
		String weight = tokenizer.nextToken();
		String type = tokenizer.nextToken();
		
		String currentPP = tokenizer.nextToken();
		String prodUnits = tokenizer.nextToken();
		
		//remove old value, if any, and insert new value
		componentsInfo.remove(weight + "$" + type);
		componentsInfo.put(weight + "$" + type, currentPP + "$" + prodUnits);
	}
	
	/**
	 * Add a factory. Called from HS.java when HS|AF| command received.
	 */
	public void addFactionFactory(String factoryData) {
		
		StringTokenizer tokenizer = new StringTokenizer(factoryData,"$");
		
		//read factory data
		int weight = Integer.valueOf(tokenizer.nextToken());
		int type = Integer.valueOf(tokenizer.nextToken());
		
		String founder = tokenizer.nextToken();
		String planet = tokenizer.nextToken();
		String factoryName = tokenizer.nextToken();
		
		int timeToRefresh = Integer.valueOf(tokenizer.nextToken());
		int accessLevel = Integer.parseInt(tokenizer.nextToken());
		
		/*
		 * Check for multiproduction and add to all appropriate factory categories. Overly
		 * complex, and makes me want to punch the person who RFE'ed multifacs in the face
		 * 
		 * :-(
		 */
		if (this.canProduce(Unit.MEK, type))
			this.addFactoryHelper(weight,new Integer(Unit.MEK),timeToRefresh,founder,planet,factoryName,accessLevel);
		if (canProduce(Unit.VEHICLE, type))
			this.addFactoryHelper(weight,new Integer(Unit.VEHICLE),timeToRefresh,founder,planet,factoryName,accessLevel);
		if (canProduce(Unit.INFANTRY, type))
			this.addFactoryHelper(weight,new Integer(Unit.INFANTRY),timeToRefresh,founder,planet,factoryName,accessLevel);
		if (canProduce(Unit.PROTOMEK, type))
			this.addFactoryHelper(weight,new Integer(Unit.PROTOMEK),timeToRefresh,founder,planet,factoryName,accessLevel);
		if (canProduce(Unit.BATTLEARMOR, type))
			this.addFactoryHelper(weight,new Integer(Unit.BATTLEARMOR),timeToRefresh,founder,planet,factoryName,accessLevel);
	}
	
	/**
	 * Remove a factory from house status. Used when client receives
	 * a HS|RF| command. Usually after a world changes hands.
	 * 
	 * Format: HS|RF|weight$metatype$planet$name|
	 */
	public void removeFactionFactory(String factoryData) {
		
		StringTokenizer tokenizer = new StringTokenizer(factoryData,"$");
		
		int weight = Integer.valueOf(tokenizer.nextToken());
		int type = Integer.valueOf(tokenizer.nextToken());
	
		String planet = tokenizer.nextToken();
		String factoryName = tokenizer.nextToken();
		
		/*
		 * Check for multiproduction and remove from all appropriate factory categories. Overly
		 * complex, and makes me want to punch the person who RFE'ed multifacs in the face :-(
		 */
		if (this.canProduce(Unit.MEK, type))
			this.removeFactoryHelper(weight,Unit.MEK,planet,factoryName);
		if (canProduce(Unit.VEHICLE, type))
			this.removeFactoryHelper(weight,Unit.VEHICLE,planet,factoryName);
		if (canProduce(Unit.INFANTRY, type))
			this.removeFactoryHelper(weight,Unit.INFANTRY,planet,factoryName);
		if (canProduce(Unit.PROTOMEK, type))
			this.removeFactoryHelper(weight,Unit.PROTOMEK,planet,factoryName);
		if (canProduce(Unit.BATTLEARMOR, type))
			this.removeFactoryHelper(weight,Unit.BATTLEARMOR,planet,factoryName);
	}
	
	/**
	 * Change a factory's information. Used to update refresh times.
	 * Format: HS|CF|weight$metatype$name$planet$timetorefresh|
	 */
	public void changeFactionFactory(String factoryData) {
		
		StringTokenizer tokenizer = new StringTokenizer(factoryData,"$");
		
		int weight = Integer.valueOf(tokenizer.nextToken());
		int type = Integer.valueOf(tokenizer.nextToken());
	
		String planet = tokenizer.nextToken();
		String factoryName = tokenizer.nextToken();
		
		int timeToRefresh = Integer.valueOf(tokenizer.nextToken());
		
		int accessLevel = Integer.parseInt(tokenizer.nextToken());
		/*
		 * Check for multiproduction and update in all appropriate factory categories. Overly
		 * complex, and makes me want to punch the person who RFE'ed multifacs in the face :-(
		 */
		if (this.canProduce(Unit.MEK, type))
			this.changeFactoryHelper(weight,Unit.MEK,planet,factoryName,timeToRefresh,accessLevel);
		if (canProduce(Unit.VEHICLE, type))
			this.changeFactoryHelper(weight,Unit.VEHICLE,planet,factoryName,timeToRefresh,accessLevel);
		if (canProduce(Unit.INFANTRY, type))
			this.changeFactoryHelper(weight,Unit.INFANTRY,planet,factoryName,timeToRefresh,accessLevel);
		if (canProduce(Unit.PROTOMEK, type))
			this.changeFactoryHelper(weight,Unit.PROTOMEK,planet,factoryName,timeToRefresh,accessLevel);
		if (canProduce(Unit.BATTLEARMOR, type))
			this.changeFactoryHelper(weight,Unit.BATTLEARMOR,planet,factoryName,timeToRefresh,accessLevel);
	}
	
	/**
	 * Private method called only from addFactionFactory. Abstracts out some repetetive
	 * code that checks for factory vectors and creates missing listings.
	 */
	private void addFactoryHelper(int weight, int type, int timeToRefresh, String founder, String planet, String factoryName, int accessLevel) {
		
		//if there isn't a vector for this type + weight combo already, create one
		TreeMap<String,String> weightAndTypeMap = factoriesInfo.get(weight + "$" + type);
		if (weightAndTypeMap == null) {
			weightAndTypeMap = new TreeMap<String,String>();
			factoriesInfo.put(weight + "$" + type, weightAndTypeMap);
		}
		
		/*
		 * Add the factory to the map. Note that we use a map
		 * so the factories appear in alpha order, by world.
		 */
		weightAndTypeMap.put(planet + "$" + factoryName, founder + "$" + planet + "$" + factoryName + "$" + timeToRefresh + "$" + accessLevel);
	}
	
	/**
	 * Helper that abstracts out some repetetive checks from removeFactionFactory.
	 */
	private void removeFactoryHelper(int weight,int type, String planet, String factoryName) {
		
		TreeMap<String,String> weightAndTypeMap = factoriesInfo.get(weight + "$" + type);
		
		//if weight and type map is null, there is no way to remove the factory.
		if (weightAndTypeMap == null)
			return;
		
		//iterate through all facs of this wieght&type. remove matching names.
		Iterator i = weightAndTypeMap.keySet().iterator();
		while (i.hasNext()) {
			String currName = (String)i.next();
			if (currName.equals(planet + "$" + factoryName))
				i.remove();
		}
	}
	
	/**
	 * Helper that abstracts out some repetetive checks from checkFactionFactory.
	 */
	private void changeFactoryHelper(int weight,int type, String planet, String factoryName, int timeToRefresh, int accessLevel) {
		
		TreeMap<String,String> weightAndTypeMap = factoriesInfo.get(weight + "$" + type);
		
		//if weight and type map is null, there is no way to change the factory.
		if (weightAndTypeMap == null) {
			MWClient.mwClientLog.clientErrLog("Error updating factory: null treemap at weight & type.");
			return;
		}
		
		//no factory with matching name on planet. return.
		String oldFactoryInfo = weightAndTypeMap.get(planet + "$" + factoryName);
		if (oldFactoryInfo == null) {
			MWClient.mwClientLog.clientErrLog("Error updating factory: null oldFactory.");
			return;
		}
		
		//get the founder, which wasn't transferred.
		StringTokenizer tokenizer = new StringTokenizer(oldFactoryInfo,"$");
		String founder = tokenizer.nextToken();
		
		//overwrite the old entry
		weightAndTypeMap.put(planet + "$" + factoryName, founder + "$" + planet + "$" + factoryName + "$" + timeToRefresh + "$" + accessLevel);
	}
	
	/**
	 * Helper used to determine which unit types a multi-fac can produce.
	 */
	private boolean canProduce(int type_id, int productionCapabilities) {
		
		//Exception 0 = everything;
		if (productionCapabilities == UnitFactory.BUILDALL)
			return true;
		
		int test = productionCapabilities;
		if (test - UnitFactory.BUILDBATTLEARMOR >= 0){
			test -= UnitFactory.BUILDBATTLEARMOR;
			if (type_id == Unit.BATTLEARMOR)
				return true;
		}
		
		if (test - UnitFactory.BUILDPROTOMECHS >= 0){
			test -= UnitFactory.BUILDPROTOMECHS;
			if (type_id == Unit.PROTOMEK)
				return true;
		}
		
		if (test - UnitFactory.BUILDINFANTRY >= 0) {
			test -= UnitFactory.BUILDINFANTRY;
			if (type_id == Unit.INFANTRY)
				return true;
		}
		
		if (test - UnitFactory.BUILDVEHICLES >= 0) {
			test -= UnitFactory.BUILDVEHICLES;
			if (type_id == Unit.VEHICLE)
				return true;
		}
		
		if (test - UnitFactory.BUILDMEK >= 0) {
			if (type_id == Unit.MEK)
				return true;
		}
		
		return false;
	}
	
	public void updateDisplay() {
		
		//Returns the Private Status for Members only
		StringBuilder result = new StringBuilder("<BODY  TEXT=\"" + mwclient.getConfigParam("CHATFONTCOLOR") + "\" BGCOLOR=\""+mwclient.getConfigParam("BACKGROUNDCOLOR")+"\">");
		boolean usingAdvanceRepairs = mwclient.isUsingAdvanceRepairs();
        int playerAccessLevel = mwclient.getPlayer().getSubFactionAccess();
		result.append("<TABLE Border=\"1\"><TR><TH>"+ this.HouseName +"</TH><TH>Light</TH><TH>Medium</TH><TH>Heavy</TH><TH>Assault</TH></TR>");
		for (int type_id = 0; type_id < Unit.TOTALTYPES; type_id++) {
		    
			//hide unit types that aren't in use on the server
			String useIt = "Use" + Unit.getTypeClassDesc(type_id);

			result.append("<TR><TD VALIGN=MIDDLE><b>" + Unit.getTypeClassDesc(type_id) + "</b></TD>");
			for (int weight = 0; weight < 4; weight++) {
									
				String buyNew = "CanBuyNew"+CUnit.getWeightClassDesc(weight)+CUnit.getTypeClassDesc(type_id);

				String Comps = componentsInfo.get(weight + "$" + type_id);
				StringTokenizer ST = new StringTokenizer(Comps,"$");
				int comps = Integer.parseInt(ST.nextToken());
				if (comps > 0 || (factoriesInfo.get(weight + "$" + type_id) != null)) {
					
					result.append("<TD>" + "<img src=\"data/images/miniticks.gif\">:" + comps);
					result.append("<img src=\"data/images/units.gif\">:" + ST.nextToken() + "<br>");
										
					//Needed because of the binary coding.
					int typetocheck = type_id;
					
					TreeMap<String,String> facs = factoriesInfo.get(weight + "$" + typetocheck);
					if (facs != null && Boolean.parseBoolean(thePlayer.getSubFaction().getConfig(buyNew)) && Boolean.parseBoolean(mwclient.getserverConfigs(useIt))) {
						
						boolean hasOpen = false;
						int minrefresh = Integer.MAX_VALUE;
						
						for (String Fac : facs.values()) {
							
							ST = new StringTokenizer(Fac,"$");
							String founder = ST.nextToken();
							String planet = ST.nextToken();
							String factoryName = ST.nextToken();
							int refreshTime = Integer.parseInt(ST.nextToken());
							int accessLevel = Integer.parseInt(ST.nextToken());
							
							if ( accessLevel > playerAccessLevel ) {
								hasOpen = true;
								continue;
							}
							
							if (refreshTime == 0) {
								
								House foundH = mwclient.getData().getHouseByName(founder);
								int cbillCost = CUnit.getPriceForUnit(mwclient, weight, type_id, foundH);
								int fluCost = CUnit.getInfluenceForUnit(mwclient, weight, type_id, foundH);
								int ppCost = CUnit.getPPForUnit(mwclient, weight, type_id, foundH);
								
								if (!mwclient.getPlayer().getMyHouse().getName().equalsIgnoreCase(foundH.getName())) {
									cbillCost = Math.round(cbillCost * Float.parseFloat(mwclient.getserverConfigs("NonOriginalCBillMultiplier")));
									fluCost = Math.round(fluCost * Float.parseFloat(mwclient.getserverConfigs("NonOriginalInfluenceMultiplier")));
									ppCost = Math.round(ppCost * Float.parseFloat(mwclient.getserverConfigs("NonOriginalComponentMultiplier")));
								}
								
								String costString = "(Cost: " + mwclient.moneyOrFluMessage(true, true, cbillCost, false) + ", " + mwclient.moneyOrFluMessage(false, true, fluCost, false) + ", " + ppCost + " Components)";
								
								result.append("<a href=\"MEKWARS/c request#" + weight + "#" + type_id + "#" + planet + "#"+ factoryName +"\"><img border=\"0\" alt=\"Click to buy a " + founder + " " + Unit.getTypeClassDesc(type_id) + " from " + factoryName + " on " + planet + ". " + costString + "\" src=\"data/images/open" + founder + ".gif\"></a>");
								hasOpen = true;
								
							} else {
								result.append("<a href=\"MEKWARS/c request#" + weight + "#" + type_id + "#" + planet + "#"+ factoryName +"\"<img border=\"0\" alt=\"" + factoryName + " on " + planet + " built by " + founder + " (Refresh Time: " + refreshTime + ")\" src=\"data/images/closed" + founder + ".gif\"></a>");
								if (refreshTime < minrefresh)
									minrefresh = refreshTime;
							}
						}
						
						if (!hasOpen)
							result.append("<img src=\"data/images/clock.gif\">:"+ minrefresh);
					}
					else
						result.append("<img src=\"data/images/absent.gif\">");
				}
				else
					result.append("<TD> </TD>");
				
					result.append("</TD>");
			}
			result.append("</TR>");
		}
		result.append("</TABLE>");
		
		//Bays
		for (int type = 0; type < Unit.TOTALTYPES; type++) {
		    
			//is not using units of the type, skip the listings
		    String useIt = "Use" + Unit.getTypeClassDesc(type);
		    if (!Boolean.parseBoolean(mwclient.getserverConfigs(useIt)))
		        continue;
			
		    
		    //if the house has any units at all, add a Bays: title.
			boolean hasUnits = false;
			for (int weight = 0; weight < 4; weight++) {
				if (this.unitsInfo.get(weight+"$"+type) != null)
					hasUnits = true;
			}
			if (hasUnits)
				result.append("<b>" + Unit.getTypeClassDesc(type) + " Bays</b><br>");
			
			//fill out bays
			for (int weight = 0; weight < 4; weight++) {
				
				String buyUsed = "CanBuyUsed"+CUnit.getWeightClassDesc(weight)+CUnit.getTypeClassDesc(type);
				if ( !Boolean.parseBoolean(thePlayer.getSubFaction().getConfig(buyUsed)) )
					continue;
				
				if (this.unitsInfo.get(weight+"$"+type) != null && ((Vector)this.unitsInfo.get(weight+"$"+type)).size() > 0 ) {
					House foundH = mwclient.getData().getHouseByName(mwclient.getPlayer().getMyHouse().getName());
					int cbillCost = Math.round(CUnit.getPriceForUnit(mwclient, weight, type, foundH) * foundH.getUsedMekBayMultiplier());
					int fluCost = Math.round(CUnit.getInfluenceForUnit(mwclient, weight, type, foundH) * foundH.getUsedMekBayMultiplier());
					result.append("<a href=\"MEKWARS/c requestdonated#" + weight + "#" + type + "\"><img border=\"0\" alt=\"Request one of the Units from this bay (Cost: " + mwclient.moneyOrFluMessage(true, true, cbillCost, false) + ", " + mwclient.moneyOrFluMessage(false, true, fluCost, false) + ")\" src=\"data/images/cart.gif\"></a> " + Unit.getWeightClassDesc(weight) + ": ");
					Vector<HSMek> v = this.unitsInfo.get(weight + "$" + type);
					HSMek[] entities = new HSMek[v.size()];
					for (int i = 0; i < v.size();i++)
						entities[i] = v.elementAt(i);
					
					//alpha sort
					Arrays.sort(entities, new Comparator() {
						public int compare(Object obj1, Object obj2) {
							
							HSMek a = (HSMek)obj1;
							HSMek b = (HSMek)obj2;
							
							//if the names are the same, check for damage
							if (a.getName().compareTo(b.getName()) == 0) {
								
								String damA = a.getBattleDamage();
								String damB = b.getBattleDamage();
								
								return damA.compareTo(damB);
							}
							//else
							return a.getName().compareTo(b.getName());
						}
					});
					
					//group identical units together and add to result
					for (int j = 0; j < entities.length; j++) {
						
						StringBuilder unitString = new StringBuilder();
						
						HSMek m = entities[j];
						int num = 1;
						
						while (j < entities.length - 1 
								&& m.getName().equalsIgnoreCase(entities[j+1].getName()) 
								&& (m.getBattleDamage().equalsIgnoreCase(entities[j+1].getBattleDamage())) ) {
							j++;
							num++;
						}
						
						//change color depending on level of damage, if using AS
                        if (usingAdvanceRepairs){
                            Entity e = m.getEntity();
                            UnitUtils.applyBattleDamage(e,m.getBattleDamage());
                            if (!UnitUtils.canStartUp(e))
                                unitString.append("<font color=\"BLUE\">");
                            else if (UnitUtils.hasCriticalDamage(e))
                                unitString.append("<font color=\"red\">");
                            else if (UnitUtils.hasArmorDamage(e))
                                unitString.append("<font color=\"yellow\">");
                            else
                                unitString.append("<font>");
                          }
                        
						if (m.getType().equalsIgnoreCase("mek") || m.getType().equalsIgnoreCase("vehicle"))
						    unitString.append("<a href=\"MEKINFO" + m.getMekFile() + "#" + m.getBV() + "#" + m.getEntity().getCrew().getGunnery() + "#" + m.getEntity().getCrew().getPiloting() + "#" + m.getBattleDamage() + "\">"+ m.getName());
						else
							unitString.append("<a href=\"MEKINFO" + m.getMekFile() + "#" + m.getBV() + "#" + m.getEntity().getCrew().getGunnery() + "#" + m.getEntity().getCrew().getPiloting() + "#" + m.getBattleDamage() + "\">"+ m.getName());
                        						
						//frontload the dupe indicator, to reduce confusion with mono-skill units 
						if (num > 1)
							unitString.insert(0,num + " x ");
						
						unitString.append("</a>");
                        if (usingAdvanceRepairs)
                            unitString.append("</font>");
						if (j < (entities.length -1))
							unitString.append(", ");
						else
							unitString.append("<br>");
						
						//add this unit's string to the overall result
						result.append(unitString.toString());
					}
				}
			}
		}
		
		result.append("</BODY>");
		mainPane.setText("");
		mainPane.setText(result.toString());
		mainPane.repaint();
	}
	
	public void setInfoText(String s) {
		lblInfo.setText(s);
		if (s == null) {
			this.hsButtonSpringPanel.setVisible(true);
			lblInfo.setVisible(false);
		} else if (s.equals("")) {
			this.hsButtonSpringPanel.setVisible(true);
			this.lblInfo.setVisible(false);
		} else {
			
			//only set the info label's dimension once. has to be done here to ensure
			//that the label and panel dimensions match exactly.
			if (lblInfo.getWidth() == 0 && lblInfo.getHeight() ==0) {
				Dimension newDim = new Dimension();
				newDim.setSize(lblInfo.getPreferredSize().getWidth(), hsButtonSpringPanel.getSize().getHeight());
				lblInfo.setMinimumSize(newDim);
			}
			
			this.hsButtonSpringPanel.setVisible(false);
			this.lblInfo.setVisible(true);
		}
	}
	
	public void showInfoWindow(String MekFile, int BV, int Gunnery, int Piloting, String battleDamage)
	{
        Entity UnitEntity = null;
		CUnit embeddedUnit = new CUnit();
		embeddedUnit.setUnitFilename(MekFile);
		embeddedUnit.createEntity();
		UnitEntity = embeddedUnit.getEntity();
		
		JFrame InfoWindow = new JFrame();
		MechDetailDisplay MechDetailInfo = new MechDetailDisplay();
        UnitEntity.loadAllWeapons();
        UnitEntity.setCrew(new megamek.common.Pilot("", Gunnery, Piloting));
        if ( battleDamage.trim().length() > 1 )
            UnitUtils.applyBattleDamage(UnitEntity,battleDamage,false);
		MechDetailInfo.displayEntity(UnitEntity, BV, mwclient.getConfig().getImage("CAMO"));
		InfoWindow.getContentPane().add(MechDetailInfo);
		InfoWindow.setSize(220,400);
		InfoWindow.setResizable(false);
		InfoWindow.setTitle(UnitEntity.getModel());
		InfoWindow.setLocationRelativeTo(mwclient.getMainFrame());
		InfoWindow.setVisible(true);
	}
	
    //BUY MENU METHODS AND LISTENERS
	private void buyNewButtonActionPerformed(ActionEvent e) {}//do nothing on action
	private void buyUsedButtonActionPerformed(ActionEvent e) {}//do nothing
	
	//make popup on press or release of New button
	private void buyNewUnitMouseEvent(MouseEvent e) {
		JPopupMenu buy = createBuyNewPopupMenu();
		buy.show(e.getComponent(), e.getX(), e.getY());
	}
	
	private void buyUsedUnitMouseEvent(MouseEvent e) {
		JPopupMenu buy = createBuyUsedPopupMenu();
		buy.show(e.getComponent(), e.getX(), e.getY());
	}
	
	class BuyPopupListener extends MouseAdapter implements ActionListener {
		
		public void actionPerformed(ActionEvent actionEvent) {
			String s = actionEvent.getActionCommand();
			StringTokenizer st = new StringTokenizer(s,"|");
			String command = st.nextToken();
			
			if (command.equalsIgnoreCase("BUY")) {
				mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c request#"+st.nextToken()+"#"+st.nextToken());
				//(Client.getMainFrame().getMainPanel().getCommPanel()).
				//removeHttpLinksFromEditorPane(CCommPanel.CHANNEL_MISC);
			} else if (command.equalsIgnoreCase("BUYU")) {
				mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c requestdonated#"+st.nextToken()+"#"+st.nextToken());
				//(Client.getMainFrame().getMainPanel().getCommPanel()).
				//removeHttpLinksFromEditorPane(CCommPanel.CHANNEL_MISC);
			} else if (command.equalsIgnoreCase("BUYP")) {
				mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c buypilotsfromhouse#"+st.nextToken()+"#"+st.nextToken());
				//(Client.getMainFrame().getMainPanel().getCommPanel()).
				//removeHttpLinksFromEditorPane(CCommPanel.CHANNEL_MISC);
			}
		}
	}
	
	private JPopupMenu createBuyNewPopupMenu() {
		JMenu tmenu;
		JPopupMenu buy = new JPopupMenu();
		JMenuItem menuItem = null;
		
		tmenu = new JMenu("Mek");
		buy.add(tmenu);
		menuItem = new JMenuItem("Light Mek");
		menuItem.setActionCommand("BUY|LIGHT|" + Unit.MEK);
		menuItem.addActionListener(this.myPopup);
		tmenu.add(menuItem);
		menuItem = new JMenuItem("Medium Mek");
		menuItem.setActionCommand("BUY|MEDIUM|" + Unit.MEK);
		menuItem.addActionListener(this.myPopup);
		tmenu.add(menuItem);
		menuItem = new JMenuItem("Heavy Mek");
		menuItem.setActionCommand("BUY|HEAVY|" + Unit.MEK);
		menuItem.addActionListener(this.myPopup);
		tmenu.add(menuItem);
		menuItem = new JMenuItem("Assault Mek");
		menuItem.setActionCommand("BUY|ASSAULT|" + Unit.MEK);
		menuItem.addActionListener(this.myPopup);
		tmenu.add(menuItem);
		
		if ( Boolean.parseBoolean(mwclient.getserverConfigs("UseVehicle"))){
			tmenu = new JMenu("Vehicle");
			buy.add(tmenu);
			menuItem = new JMenuItem("Light Vehicle");
			menuItem.setActionCommand("BUY|LIGHT|" + Unit.VEHICLE);
			menuItem.addActionListener(this.myPopup);
			tmenu.add(menuItem);
			menuItem = new JMenuItem("Medium Vehicle");
			menuItem.setActionCommand("BUY|MEDIUM|" + Unit.VEHICLE);
			menuItem.addActionListener(this.myPopup);
			tmenu.add(menuItem);
			menuItem = new JMenuItem("Heavy Vehicle");
			menuItem.setActionCommand("BUY|HEAVY|" + Unit.VEHICLE);
			menuItem.addActionListener(this.myPopup);
			tmenu.add(menuItem);
			menuItem = new JMenuItem("Assault Vehicle");
			menuItem.setActionCommand("BUY|ASSAULT|" + Unit.VEHICLE);
			menuItem.addActionListener(this.myPopup);
			tmenu.add(menuItem);
		}
		
		if ( Boolean.parseBoolean(mwclient.getserverConfigs("UseInfantry"))){
			tmenu = new JMenu("Infantry");
			buy.add(tmenu);
			menuItem = new JMenuItem("Light Infantry");
			menuItem.setActionCommand("BUY|LIGHT|" + Unit.INFANTRY);
			menuItem.addActionListener(this.myPopup);
			tmenu.add(menuItem);
			menuItem = new JMenuItem("Medium Infantry");
			menuItem.setActionCommand("BUY|MEDIUM|" + Unit.INFANTRY);
			menuItem.addActionListener(this.myPopup);
			tmenu.add(menuItem);
			menuItem = new JMenuItem("Heavy Infantry");
			menuItem.setActionCommand("BUY|HEAVY|" + Unit.INFANTRY);
			menuItem.addActionListener(this.myPopup);
			tmenu.add(menuItem);
			menuItem = new JMenuItem("Assault Infantry");
			menuItem.setActionCommand("BUY|ASSAULT|" + Unit.INFANTRY);
			menuItem.addActionListener(this.myPopup);
			tmenu.add(menuItem);
		}

		if ( Boolean.parseBoolean(mwclient.getserverConfigs("UseProtoMek"))){
			tmenu = new JMenu("ProtoMek");
			buy.add(tmenu);
			menuItem = new JMenuItem("Light ProtoMek");
			menuItem.setActionCommand("BUY|LIGHT|" + Unit.PROTOMEK);
			menuItem.addActionListener(this.myPopup);
			tmenu.add(menuItem);
			menuItem = new JMenuItem("Medium ProtoMek");
			menuItem.setActionCommand("BUY|MEDIUM|" + Unit.PROTOMEK);
			menuItem.addActionListener(this.myPopup);
			tmenu.add(menuItem);
			menuItem = new JMenuItem("Heavy ProtoMek");
			menuItem.setActionCommand("BUY|HEAVY|" + Unit.PROTOMEK);
			menuItem.addActionListener(this.myPopup);
			tmenu.add(menuItem);
			menuItem = new JMenuItem("Assault ProtoMek");
			menuItem.setActionCommand("BUY|ASSAULT|" + Unit.PROTOMEK);
			menuItem.addActionListener(this.myPopup);
			tmenu.add(menuItem);
		}
		
		if ( Boolean.parseBoolean(mwclient.getserverConfigs("UseBattleArmor"))){
		    tmenu = new JMenu("Battle Armor");
			buy.add(tmenu);
			menuItem = new JMenuItem("Light Battle Armor");
			menuItem.setActionCommand("BUY|LIGHT|" + Unit.BATTLEARMOR);
			menuItem.addActionListener(this.myPopup);
			tmenu.add(menuItem);
			menuItem = new JMenuItem("Medium Battle Armor");
			menuItem.setActionCommand("BUY|MEDIUM|" + Unit.BATTLEARMOR);
			menuItem.addActionListener(this.myPopup);
			tmenu.add(menuItem);
			menuItem = new JMenuItem("Heavy Battle Armor");
			menuItem.setActionCommand("BUY|HEAVY|" + Unit.BATTLEARMOR);
			menuItem.addActionListener(this.myPopup);
			tmenu.add(menuItem);
			menuItem = new JMenuItem("Assault Battle Armor");
			menuItem.setActionCommand("BUY|ASSAULT|" + Unit.BATTLEARMOR);
			menuItem.addActionListener(this.myPopup);
			tmenu.add(menuItem);
		}
		
		if (Boolean.parseBoolean(mwclient.getserverConfigs("AllowPersonalPilotQueues"))) {
			tmenu = new JMenu("Pilots");
			buy.add(tmenu);
			JMenu smenu = new JMenu("Mek");
			menuItem = new JMenuItem("Light Pilot");
			menuItem.setActionCommand("BUYP|" + Unit.MEK+"|" + Unit.LIGHT);
			menuItem.addActionListener(this.myPopup);
			smenu.add(menuItem);
			menuItem = new JMenuItem("Medium Pilot");
			menuItem.setActionCommand("BUYP|"+Unit.MEK+"|" + Unit.MEDIUM);
			menuItem.addActionListener(this.myPopup);
			smenu.add(menuItem);
			menuItem = new JMenuItem("Heavy Pilot");
			menuItem.setActionCommand("BUYP|"+Unit.MEK+"|" + Unit.HEAVY);
			menuItem.addActionListener(this.myPopup);
			smenu.add(menuItem);
			menuItem = new JMenuItem("Assault Pilot");
			menuItem.setActionCommand("BUYP|"+Unit.MEK+"|" + Unit.ASSAULT);
			menuItem.addActionListener(this.myPopup);
			smenu.add(menuItem);
			tmenu.add(smenu);
			
			if (Boolean.parseBoolean(mwclient.getserverConfigs("UseProtoMek"))){
				smenu = new JMenu("Proto");
				menuItem = new JMenuItem("Light Pilot");
				menuItem.setActionCommand("BUYP|"+Unit.PROTOMEK+"|" + Unit.LIGHT);
				menuItem.addActionListener(this.myPopup);
				smenu.add(menuItem);
				menuItem = new JMenuItem("Medium Pilot");
				menuItem.setActionCommand("BUYP|"+Unit.PROTOMEK+"|" + Unit.MEDIUM);
				menuItem.addActionListener(this.myPopup);
				smenu.add(menuItem);
				menuItem = new JMenuItem("Heavy Pilot");
				menuItem.setActionCommand("BUYP|"+Unit.PROTOMEK+"|" + Unit.HEAVY);
				menuItem.addActionListener(this.myPopup);
				smenu.add(menuItem);
				menuItem = new JMenuItem("Assault Pilot");
				menuItem.setActionCommand("BUYP|"+Unit.PROTOMEK+"|" + Unit.ASSAULT);
				menuItem.addActionListener(this.myPopup);
				smenu.add(menuItem);
				tmenu.add(smenu);
			}
		}

		return buy;
	}
	
	private JPopupMenu createBuyUsedPopupMenu() {
		JMenu tmenu;
		JPopupMenu buy = new JPopupMenu();
		JMenuItem menuItem = null;
		
		tmenu = new JMenu("Mek");
		buy.add(tmenu);
		menuItem = new JMenuItem("Light Mek");
		menuItem.setActionCommand("BUYU|LIGHT|" + Unit.MEK);
		menuItem.addActionListener(this.myPopup);
		tmenu.add(menuItem);
		menuItem = new JMenuItem("Medium Mek");
		menuItem.setActionCommand("BUYU|MEDIUM|" + Unit.MEK);
		menuItem.addActionListener(this.myPopup);
		tmenu.add(menuItem);
		menuItem = new JMenuItem("Heavy Mek");
		menuItem.setActionCommand("BUYU|HEAVY|" + Unit.MEK);
		menuItem.addActionListener(this.myPopup);
		tmenu.add(menuItem);
		menuItem = new JMenuItem("Assault Mek");
		menuItem.setActionCommand("BUYU|ASSAULT|" + Unit.MEK);
		menuItem.addActionListener(this.myPopup);
		tmenu.add(menuItem);
				
		if (Boolean.parseBoolean(mwclient.getserverConfigs("UseVehicle"))){
		    tmenu = new JMenu("Vehicle");
			buy.add(tmenu);
			menuItem = new JMenuItem("Light Vehicle");
			menuItem.setActionCommand("BUYU|LIGHT|" + Unit.VEHICLE);
			menuItem.addActionListener(this.myPopup);
			tmenu.add(menuItem);
			menuItem = new JMenuItem("Medium Vehicle");
			menuItem.setActionCommand("BUYU|MEDIUM|" + Unit.VEHICLE);
			menuItem.addActionListener(this.myPopup);
			tmenu.add(menuItem);
			menuItem = new JMenuItem("Heavy Vehicle");
			menuItem.setActionCommand("BUYU|HEAVY|" + Unit.VEHICLE);
			menuItem.addActionListener(this.myPopup);
			tmenu.add(menuItem);
			menuItem = new JMenuItem("Assault Vehicle");
			menuItem.setActionCommand("BUYU|ASSAULT|" + Unit.VEHICLE);
			menuItem.addActionListener(this.myPopup);
			tmenu.add(menuItem);
		}
		if (Boolean.parseBoolean(mwclient.getserverConfigs("UseInfantry"))){
			tmenu = new JMenu("Infantry");
			buy.add(tmenu);
			menuItem = new JMenuItem("Light Infantry");
			menuItem.setActionCommand("BUYU|LIGHT|" + Unit.INFANTRY);
			menuItem.addActionListener(this.myPopup);
			tmenu.add(menuItem);
			menuItem = new JMenuItem("Medium Infantry");
			menuItem.setActionCommand("BUYU|MEDIUM|" + Unit.INFANTRY);
			menuItem.addActionListener(this.myPopup);
			tmenu.add(menuItem);
			menuItem = new JMenuItem("Heavy Infantry");
			menuItem.setActionCommand("BUYU|HEAVY|" + Unit.INFANTRY);
			menuItem.addActionListener(this.myPopup);
			tmenu.add(menuItem);
			menuItem = new JMenuItem("Assault Infantry");
			menuItem.setActionCommand("BUYU|ASSAULT|" + Unit.INFANTRY);
			menuItem.addActionListener(this.myPopup);
			tmenu.add(menuItem);
		}
		
		if ( Boolean.parseBoolean(mwclient.getserverConfigs("UseProtoMek")) ){
			tmenu = new JMenu("ProtoMek");
			buy.add(tmenu);
			menuItem = new JMenuItem("Light ProtoMek");
			menuItem.setActionCommand("BUYU|LIGHT|" + Unit.PROTOMEK);
			menuItem.addActionListener(this.myPopup);
			tmenu.add(menuItem);
			menuItem = new JMenuItem("Medium Infantry");
			menuItem.setActionCommand("BUYU|MEDIUM|" + Unit.PROTOMEK);
			menuItem.addActionListener(this.myPopup);
			tmenu.add(menuItem);
			menuItem = new JMenuItem("Heavy Infantry");
			menuItem.setActionCommand("BUYU|HEAVY|" + Unit.PROTOMEK);
			menuItem.addActionListener(this.myPopup);
			tmenu.add(menuItem);
			menuItem = new JMenuItem("Assault Infantry");
			menuItem.setActionCommand("BUYU|ASSAULT|" + Unit.PROTOMEK);
			menuItem.addActionListener(this.myPopup);
			tmenu.add(menuItem);
		}
		
		if ( Boolean.parseBoolean(mwclient.getserverConfigs("UseBattleArmor")) ){
			tmenu = new JMenu("Battle Armor");
			buy.add(tmenu);
			menuItem = new JMenuItem("Light Battle Armor");
			menuItem.setActionCommand("BUYU|LIGHT|" + Unit.BATTLEARMOR);
			menuItem.addActionListener(this.myPopup);
			tmenu.add(menuItem);
			menuItem = new JMenuItem("Medium Battle Armor");
			menuItem.setActionCommand("BUYU|MEDIUM|" + Unit.BATTLEARMOR);
			menuItem.addActionListener(this.myPopup);
			tmenu.add(menuItem);
			menuItem = new JMenuItem("Heavy Battle Armor");
			menuItem.setActionCommand("BUYU|HEAVY|" + Unit.BATTLEARMOR);
			menuItem.addActionListener(this.myPopup);
			tmenu.add(menuItem);
			menuItem = new JMenuItem("Assault Battle Armor");
			menuItem.setActionCommand("BUYU|ASSAULT|" + Unit.BATTLEARMOR);
			menuItem.addActionListener(this.myPopup);
			tmenu.add(menuItem);
		}

		return buy;
	}
	
}