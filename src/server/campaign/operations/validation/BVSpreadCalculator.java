package server.campaign.operations.validation;

import common.Unit;

import server.campaign.SArmy;
import server.campaign.SUnit;

public class BVSpreadCalculator extends A_SpreadCalculator implements ISpreadCalculator {
	
	public static int calcMax(SArmy a, boolean countSupport, boolean countInfantry, boolean countVehicles, boolean countAero, boolean countProtos, boolean ignorePilot) {
		int max = 0;
		
		for(Unit u : a.getUnits()) {
			if (countUnit(u, countSupport, countInfantry, countVehicles, countAero, countProtos)) {
				if (ignorePilot) {
					max = Math.max(max, ((SUnit) u).getBaseBV());
				} else {
					max = Math.max(max,  ((SUnit) u).getBVForMatch());
				}
			}
		}
		return max;
	}
	
	public static int calcMin(SArmy a, boolean countSupport, boolean countInfantry, boolean countVehicles, boolean countAero, boolean countProtos, boolean ignorePilot ) {
		int min = 99999;
			
		for(Unit u : a.getUnits()) {
			if (countUnit(u, countSupport, countInfantry, countVehicles, countAero, countProtos)) {
				if (ignorePilot) {
					min = Math.min(min, ((SUnit) u).getBaseBV());
				} else {
					min = Math.min(min,  ((SUnit) u).getBVForMatch());
				}
			}
		}
		return min;
	}
}
