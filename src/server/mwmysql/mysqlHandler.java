package server.mwmysql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import server.MMServ;
import server.campaign.CampaignMain;
import server.campaign.SHouse;
import server.campaign.SPlanet;
import server.campaign.SPlayer;
import server.campaign.pilot.SPilot;

import common.CampaignData;
import common.Unit;

public class mysqlHandler{
  private MWmysql MySQLCon = null;
  private planetHandler ph = null;
  private factoryHandler fh = null;
  private PilotHandler pih = null;
  private UnitHandler uh = null;
  private FactionHandler fah = null;
  private PlayerHandler plh = null;
  private PhpBBConnector phpBBCon = null;

  private final int currentDBVersion = 1;
  
  public void closeMySQL(){
	  MySQLCon.close();
	  if(CampaignMain.cm.isSynchingBB())
		  phpBBCon.close();
  }
  
  public void backupDB() {
	  MySQLCon.backupDB();
  }
  
  public Connection getCon() {
	return MySQLCon.con;  
  }
  
  public Connection getBBCon() {
	  return phpBBCon.con;
  }
  
  public void deleteFactory(int FactoryID){
    fh.deleteFactory(FactoryID);
  }

  public void deletePlanetFactories(String planetName){
    fh.deletePlanetFactories(planetName);
  }

  public void loadFactories(SPlanet planet){
    fh.loadFactories(planet);
  }
  
  public int countPlanets() {
	  return ph.countPlanets();
  }
  
  public void loadPlanets(CampaignData data) {
	  ph.loadPlanets(data);
  }
  
  public void saveInfluences(SPlanet planet) {
	  ph.saveInfluences(planet);
  }
  
  public void saveEnvironments(SPlanet planet) {
	  ph.saveEnvironments(planet);
  }
  
  public void savePlanetFlags(SPlanet planet) {
	  ph.savePlanetFlags(planet);
  }
  
  public void deletePlanet(int PlanetID) {
	  ph.deletePlanet(PlanetID);
  }
  
  public void loadFactionPilots(SHouse h) {
	  try {
		  ResultSet rs;
		  Statement stmt = MySQLCon.con.createStatement();

		  for (int x = Unit.MEK; x < Unit.MAXBUILD; x++) {
			  h.getPilotQueues().setFactionID(h.getDBId());
			  rs = stmt.executeQuery("SELECT pilotID from pilots WHERE factionID = " + h.getId() + " AND pilotType= " + x);
			  while(rs.next()) {
				  SPilot p = pih.loadPilot(rs.getInt("pilotID"));
				  h.getPilotQueues().loadPilot(x, p);
			  }			  
		  }

	  } catch (SQLException e) {
		  MMServ.mmlog.dbLog("SQL Error in mysqlHandler.loadFactionPilots: " + e.getMessage());
	  }
  }
  
  public void deleteFactionPilots(int factionID) {
	  pih.deleteFactionPilots(factionID);
  }
  
  public void deleteFactionPilots(int factionID, int type) {
	  pih.deleteFactionPilots(factionID, type);
  }
  
  public void deletePlayerPilots(int playerID) {
	  pih.deletePlayerPilots(playerID);
  }
  
  public void deletePlayerPilots(int playerID, int unitType, int unitWeight) {
	  pih.deletePlayerPilots(playerID, unitType, unitWeight);
  }
  
  public void deletePilot(int pilotID) {
	  pih.deletePilot(pilotID);
  }
  
  public SPilot loadUnitPilot(int unitID) {
	  return pih.loadUnitPilot(unitID);
  }
  
  public SPilot loadPilot(int pilotID) {
	  return pih.loadPilot(pilotID);
  }
  
  public void linkPilotToUnit(int pilotID, int unitID) {
	  pih.linkPilotToUnit(pilotID, unitID);
  }
  
  public void linkPilotToFaction(int pilotID, int factionID) {
	  pih.linkPilotToFaction(pilotID, factionID);
  }
  
  public void linkPilotToPlayer(int pilotID, int playerID) {
	  pih.linkPilotToPlayer(pilotID, playerID);
  }
  
  public void unlinkUnit(int unitID) {
	  uh.unlinkUnit(unitID);
  }
  
  public void linkUnitToPlayer(int unitID, int playerID) {
	  uh.linkUnitToPlayer(unitID, playerID);
  }
  
  public void linkUnitToFaction(int unitID, int factionID){
	  uh.linkUnitToFaction(unitID, factionID);
  }
  
  public void deleteUnit(int unitID) {
	  uh.deleteUnit(unitID);
  }
  
  public void loadFactions(CampaignData data) {
	  fah.loadFactions(data);
  }

  public int countFactions() {
	  return fah.countFactions();
  }
  
  public int countPlayers() {
	  return plh.countPlayers();
  }
  
  public int getPlayerIDByName(String name) {
	  return plh.getPlayerIDByName(name);
  }
  
  public void setPlayerPassword(int ID, String password) {
	  plh.setPassword(ID, password);
  }
  
  public void setPlayerAccess(int ID, int level) {
	  plh.setPlayerAccess(ID, level);
  }
  
  public boolean matchPassword(String playerName, String pass) {
	  return plh.matchPassword(playerName, pass);
  }
  
  public boolean playerExists(String name) {
	  return plh.playerExists(name);
  }
  
  public void deletePlayer(SPlayer p) {
	  plh.deletePlayer(p);
  }
  
  public void purgeStalePlayers(long days) {
	  plh.purgeStalePlayers(days);
  }
  
  public int getDBVersion() {
	  Statement stmt;
	  ResultSet rs;
	  try {
		  stmt = getCon().createStatement();
		  rs = stmt.executeQuery("SELECT config_value from config WHERE config_key = 'mekwars_database_version'");
		  if(rs.next()) {
			  return rs.getInt("config_value");
		  }
		  return 0;
	  } catch (SQLException e) {
		  MMServ.mmlog.dbLog("SQL Error in mysqlHandler.getDBVersion: " + e.getMessage());
		  return 0;
	  }
  }
  
  private boolean databaseIsUpToDate() {
	  if(getDBVersion() == currentDBVersion){
		  MMServ.mmlog.dbLog("Database up to date");
		  return true;
	  }
	  MMServ.mmlog.dbLog("Database is out of date!  Please update.");
	  return false;
  }
  
  public void checkAndUpdateDB() {
	  if(databaseIsUpToDate())
		  return;
	  MMServ.mmlog.dbLog("Database out of date");
	  MMServ.mmlog.mainLog("Database out of date.  Shutting down to avoid data corruption.");
	  System.exit(0);

/*

	  int dbVersion = getDBVersion();
	  
	  MMServ.mmlog.dbLog("Updating Database from version " + dbVersion + " to " + currentDBVersion);
	  while (dbVersion != currentDBVersion) {
		  int targetVersion = dbVersion + 1;
		  MMServ.mmlog.dbLog("Starting update: " + dbVersion + " to " + targetVersion);

		  dbVersion = targetVersion;
		  
	  }
*/
  }
  
  public mysqlHandler(){
    this.MySQLCon = new MWmysql();
    if(CampaignMain.cm.isSynchingBB()) {
    	this.phpBBCon = new PhpBBConnector();
    }
    this.ph = new planetHandler(MySQLCon.con);
    this.fh = new factoryHandler(MySQLCon.con);
    this.pih = new PilotHandler(MySQLCon.con);
    this.uh = new UnitHandler(MySQLCon.con);
    this.fah = new FactionHandler(MySQLCon.con);
    this.plh = new PlayerHandler(MySQLCon.con);
    this.checkAndUpdateDB();
  }
}
