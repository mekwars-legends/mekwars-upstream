package server.campaign.operations.validation;

import server.campaign.SArmy;
import server.campaign.operations.Operation;

public interface ISpreadValidator {
	public static int SPREADTYPE_BV = 0;
	public static int SPREADTYPE_TONS = 1;
	
	public static int ERROR_UNKNOWN = -1;
	public static int ERROR_NONE = 0;
	public static int ERROR_SPREAD_TOO_LARGE = 1;
	public static int ERROR_SPREAD_TOO_SMALL = 2;
	
	public void setSpreadType(int type);
	
	public int getSpreadType();
	
	public boolean validate(SArmy a, Operation o);
	public int getError();
}
