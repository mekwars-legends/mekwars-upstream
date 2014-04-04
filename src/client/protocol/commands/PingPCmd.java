package client.protocol.commands;

import java.util.StringTokenizer;

import client.MWClient;

import common.CampaignData;

/**
 * Ping command
 */
public class PingPCmd extends CProtCommand {

	public PingPCmd(MWClient mwclient) {
		super(mwclient);
		name = "ping";
	}

	// execute command
	@Override
	public boolean execute(String input) {

		StringTokenizer ST = new StringTokenizer(input, delimiter);
		if (check(ST.nextToken()) && ST.hasMoreTokens()) {
			input = decompose(input);
			ST = new StringTokenizer(input, delimiter);
			String sender = ST.nextToken();
			String stamp = ST.nextToken();

			CampaignData.mwlog.infoLog("Received server ping.");

			Connector.send(prefix + "pong" + delimiter + sender + delimiter + stamp);
			if (!sender.equals("server")) {echo(input);}
			else {mwclient.setLastPing(System.currentTimeMillis() / 1000);}
			return true;
		}
		//else
		return false; 
	}

	// echo command in GUI
	@Override
	protected void echo(String input) {

		StringTokenizer ST = new StringTokenizer(input, delimiter);
		String sender = ST.nextToken();
		mwclient.systemMessage("Ping request from " + sender); 
	}

}