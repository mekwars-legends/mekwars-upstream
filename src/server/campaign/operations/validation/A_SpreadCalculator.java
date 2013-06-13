package server.campaign.operations.validation;

import common.Unit;

/*
 * Calculate the spread.  Intended to allow for multiple spread
 * types (such as BV and tonnage).  
 * 
 * Subclasses should implement 2 static methods:
 * 
 * public static int calcMax(SArmy a, boolean countSupport, boolean countInfantry, boolean countVehicles, boolean countAero, boolean countProtos, boolean ignorePilot);
 * 
 * and
 * 
 * public static int calcMax(SArmy a, boolean countSupport, boolean countInfantry, boolean countVehicles, boolean countAero, boolean countProtos, boolean ignorePilot);
 * 
 * However, because of the static keyword in those, I cannot define them in either
 * the abstract class or the interface
 * 
 */
public abstract class A_SpreadCalculator implements ISpreadCalculator {
	
	protected static boolean countUnit(Unit u, boolean countSupport, boolean countInfantry, boolean countVehicles, boolean countAero, boolean countProtos) {
		int type = u.getType();
		if (u.isSupportUnit() && !countSupport) {
			return false;
		} 
		if (type == Unit.AERO && !countAero){
			return false;
		}
		if (type == Unit.VEHICLE && !countVehicles) {
			return false;
		}
		if (type == Unit.PROTOMEK && !countProtos) {
			return false;
		}
		if ((type == Unit.INFANTRY || type == Unit.BATTLEARMOR) && !countInfantry) {
			return false;
		}
		return true;
	}
}
