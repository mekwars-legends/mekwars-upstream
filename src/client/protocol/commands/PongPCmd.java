package client.protocol.commands;

import java.util.StringTokenizer;

import client.MWClient;

/**
 * Pong command
 */

public class PongPCmd extends CProtCommand {

	public PongPCmd(MWClient mwclient) {
		super(mwclient);
		name = "pong";
	}

	// execute command
	@Override
	public boolean execute(String input) {

		StringTokenizer ST = new StringTokenizer(input, delimiter);
		if (check(ST.nextToken()) && ST.hasMoreTokens()) {
			input = decompose(input);
			echo(input);
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
		if (sender.equals("server")) {return;}
		float time = (float)(System.currentTimeMillis() - Long.parseLong(ST.nextToken())) / 1000;
		mwclient.systemMessage("Ping reply from " + sender + ": " + time + " s");
	}

}