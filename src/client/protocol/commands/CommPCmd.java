package client.protocol.commands;

import java.util.StringTokenizer;

import client.MWClient;
import client.protocol.TransportCodec;

/**
 * Comm command
 */

public class CommPCmd extends CProtCommand
{
	public CommPCmd(MWClient mwclient) 
	{
		super(mwclient);
		name = "comm";
	}

	// execute command
	@Override
	public boolean execute(String input) {
		
		StringTokenizer ST = new StringTokenizer(input, delimiter);
		if (check(ST.nextToken()) && ST.hasMoreTokens()) {
			input = TransportCodec.unescape(ST.nextToken());
			if (!mwclient.isDedicated()) {mwclient.doParseDataInput(input);}
			else {mwclient.parseDedDataInput(input);}
			return true;
		}
		
		//else
		return false; 
	}

	// echo command in GUI
	@Override
	protected void echo(String input) {}
}