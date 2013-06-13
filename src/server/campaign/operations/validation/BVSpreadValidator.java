package server.campaign.operations.validation;

import server.campaign.SArmy;
import server.campaign.operations.Operation;

public abstract class BVSpreadValidator implements ISpreadValidator {
	protected int maxAllowedSpread = 0;
	protected int minAllowedSpread = 99999;
	protected int maxActualSpread = 0;
	protected int minActualSpread = 99999;
	
	protected int validatorType;
	protected int error = ISpreadValidator.ERROR_UNKNOWN;
	
	
	public void setMaxActual(SArmy a, Operation o) {
		maxActualSpread = BVSpreadCalculator.calcMax(a, o.getBooleanValue("CountSupportUnitsForSpread"), o.getBooleanValue("CountInfForSpread"), o.getBooleanValue("CountVehsForSpread"), o.getBooleanValue("CountAerosForSpread"), o.getBooleanValue("CountProtosForSpread"), o.getBooleanValue("IgnorePilotsForBVSpread"));
	}
	
	public void setMinActual(SArmy a, Operation o) {
		minActualSpread = BVSpreadCalculator.calcMin(a, o.getBooleanValue("CountSupportUnitsForSpread"), o.getBooleanValue("CountInfForSpread"), o.getBooleanValue("CountVehsForSpread"), o.getBooleanValue("CountAerosForSpread"), o.getBooleanValue("CountProtosForSpread"), o.getBooleanValue("IgnorePilotsForBVSpread"));
	}
	
	public void setMaxAllowed(int max) {
		maxAllowedSpread = max;
	}
	
	public void setMinAllowed(int min) {
		minAllowedSpread = min;
	}
	
	public int getMaxAllowed() {
		return maxAllowedSpread;
	}
	
	public int getMinAllowed() {
		return minAllowedSpread;
	}
	
	public int getMinActual() {
		return minActualSpread;
	}
	
	public int getMaxActual() {
		return maxActualSpread;
	}
	
	public int getSpreadType() {
		return validatorType;
	}
	
	public void setSpreadType(int type) {
		validatorType = type;
	}
	
	protected void setError(int e) {
		error = e;
	}
	
	protected void calcError() {
		if(getMaxActual() > getMaxAllowed()) {
			setError(ISpreadValidator.ERROR_SPREAD_TOO_LARGE);
		} else if (getMinActual() < getMinAllowed()) {
			setError(ISpreadValidator.ERROR_SPREAD_TOO_SMALL);
		} else {
			setError(ISpreadValidator.ERROR_UNKNOWN);
		}
	}
	
	public int getError() {
		return error;
	}
	
	public boolean validate(SArmy a, Operation o) {
		setMinActual(a, o);
		setMaxActual(a, o);
		
		calcError();
		if(getError() == ISpreadValidator.ERROR_NONE) {
			return true;
		} else {
			return false;
		}
	}

}
