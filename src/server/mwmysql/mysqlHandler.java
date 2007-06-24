package server.mwmysql;

import common.CampaignData;

import server.mwmysql.MWmysql;
import server.mwmysql.planetHandler;
import server.mwmysql.factoryHandler;
import server.campaign.SUnit;
import server.campaign.SUnitFactory;
import server.campaign.SPlanet;
import server.campaign.pilot.SPilot;

public class mysqlHandler{
  private MWmysql MySQLCon = null;
  private planetHandler ph = null;
  private factoryHandler fh = null;
  private PilotHandler pih = null;
  private UnitHandler uh = null;

  public void closeMySQL(){
	  MySQLCon.close();
  }

  public void deleteFactory(int FactoryID){
    fh.deleteFactory(FactoryID);
  }

  public void deletePlanetFactories(String planetName){
    fh.deletePlanetFactories(planetName);
  }

  public void saveFactory(SUnitFactory factory){
    fh.saveFactory(factory);
  }

  public void loadFactories(SPlanet planet){
    fh.loadFactories(planet);
  }
  
  public void savePlanet(SPlanet planet) {
	  ph.savePlanet(planet);
  }
  
  public int countPlanets() {
	  return ph.countPlanets();
  }
  
  public void loadPlanets(CampaignData data) {
	  ph.loadPlanets(data);
  }
  
  public void deletePlanet(int PlanetID) {
	  ph.deletePlanet(PlanetID);
  }
  
  public void savePilot(SPilot p) {
	  pih.savePilot(p);
  }
  
  public void linkPilotToUnit(int pilotID, int unitID) {
	  pih.linkPilotToUnit(pilotID, unitID);
  }
  
  public void unlinkPilotFromUnit(int pilotID){
	  pih.unlinkPilotFromUnit(pilotID);
  }

  public void saveUnit(SUnit u) {
	  uh.saveUnit(u);
  }
  
  public mysqlHandler(){
    this.MySQLCon = new MWmysql();
    this.ph = new planetHandler(MySQLCon.con);
    this.fh = new factoryHandler(MySQLCon.con);
    this.pih = new PilotHandler(MySQLCon.con);
    this.uh = new UnitHandler(MySQLCon.con);
  }
}
