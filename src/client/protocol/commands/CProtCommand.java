package client.protocol.commands;

import client.MWClient;
import client.protocol.CConnector;

/**
 * Abstract class for protocol Commands
 */

public abstract class CProtCommand implements IProtCommand
{
  String name = "";
  String prefix = "";
  String delimiter = "";
  MWClient mwclient;
  CConnector Connector;

  public CProtCommand(MWClient client)
  {
    mwclient = client;
    Connector = mwclient.getConnector();
    prefix = MWClient.PROTOCOL_PREFIX;
    delimiter = MWClient.PROTOCOL_DELIMITER;
  }

  public boolean check(String tname)
  {
    if (tname.startsWith(prefix)) {tname = tname.substring(prefix.length());}
    return(name.equals(tname));
  }

  // execute command
  public boolean execute(String input) {return true;}

  // echo command in GUI
  protected void echo(String input) {}

  // remove prefix and name/alias from input
  protected String decompose(String input)
  {
    if (input.startsWith(prefix)) {input = input.substring(prefix.length()).trim();}
    if (input.startsWith(name)) {input = input.substring(name.length()).trim();}
    return input;
  }

  public String getName() {return name;}

}