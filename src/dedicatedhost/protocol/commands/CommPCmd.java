package dedicatedhost.protocol.commands;

import java.util.StringTokenizer;

import dedicatedhost.MWDedHost;
import dedicatedhost.protocol.TransportCodec;

/**
 * Comm command
 */

public class CommPCmd extends CProtCommand
{
	public CommPCmd(MWDedHost dedHost) 
	{
		super(dedHost);
		name = "comm";
	}

	// execute command
	@Override
	public boolean execute(String input) {
		
		StringTokenizer ST = new StringTokenizer(input, delimiter);
		if (check(ST.nextToken()) && ST.hasMoreTokens()) {
			input = TransportCodec.unescape(ST.nextToken());
			dedHost.parseDedDataInput(input);
			return true;
		}
		
		//else
		return false; 
	}

	// echo command in GUI
	@Override
	protected void echo(String input) {}
}