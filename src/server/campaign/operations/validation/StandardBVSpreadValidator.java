package server.campaign.operations.validation;

public class StandardBVSpreadValidator extends BVSpreadValidator implements ISpreadValidator {
	
	public StandardBVSpreadValidator(int min, int max) {
		setMaxAllowed(max);
		setMinAllowed(min);
	}
	
	

}
