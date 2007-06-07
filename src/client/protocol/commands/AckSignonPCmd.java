package client.protocol.commands;

import java.util.StringTokenizer;

import client.MWClient;

/**
 * AckSignon command
 */

public class AckSignonPCmd extends CProtCommand {

	public AckSignonPCmd(MWClient mwclient) {
		super(mwclient);
		name = "ack_signon";
	}

	// execute command
	@Override
	public boolean execute(String input) {
		StringTokenizer ST = new StringTokenizer(input, delimiter);
		if (check(ST.nextToken()) && ST.hasMoreTokens()) {
			input = decompose(input);
			ST = new StringTokenizer(input, delimiter);
			mwclient.setUsername(ST.nextToken());
			echo(input);
			if (mwclient.isDedicated()) {
				
				try {Thread.sleep(5000);}
				catch (Exception ex) {MWClient.mwClientLog.clientErrLog(ex);}
				
				try {
					mwclient.startHost(true,false,false);
				} catch (Exception ex) {
					MWClient.mwClientLog.clientErrLog("AckSignonPCmd: Error attempting to start host on signon.");
					MWClient.mwClientLog.clientErrLog(ex);
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
		MWClient.mwClientLog.clientOutputLog("Signon acknowledged");
		MWClient.mwClientLog.clientErrLog("Signon acknowledged");
	}

}