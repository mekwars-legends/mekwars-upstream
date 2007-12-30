package dedicatedhost.protocol.commands;

import java.util.StringTokenizer;

import dedicatedhost.MWDedHost;

/**
 * AckSignon command
 */

public class AckSignonPCmd extends CProtCommand {

	public AckSignonPCmd(MWDedHost dedHost) {
		super(dedHost);
		name = "ack_signon";
	}

	// execute command
	@Override
	public boolean execute(String input) {
		StringTokenizer ST = new StringTokenizer(input, delimiter);
		if (check(ST.nextToken()) && ST.hasMoreTokens()) {
			input = decompose(input);
			ST = new StringTokenizer(input, delimiter);
			dedHost.setUsername(ST.nextToken());
			echo(input);
			if (dedHost.isDedicated()) {
				
				try {Thread.sleep(5000);}
				catch (Exception ex) {
					ex.printStackTrace();
				}
				
				try {
					dedHost.startHost(true,false,false);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			
			return true;
		}
		//else
		return false; 
	}

	// echo command in GUI
	@Override
	protected void echo(String input) {
	}

}