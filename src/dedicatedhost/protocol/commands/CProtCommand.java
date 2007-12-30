package dedicatedhost.protocol.commands;

import dedicatedhost.MWDedHost;
import dedicatedhost.protocol.CConnector;

/**
 * Abstract class for protocol Commands
 */

public abstract class CProtCommand implements IProtCommand
{
  String name = "";
  String prefix = "";
  String delimiter = "";
  MWDedHost dedHost;
  CConnector Connector;

  public CProtCommand(MWDedHost  client)
  {
	dedHost = client;
    Connector = dedHost.getConnector();
    prefix = MWDedHost.PROTOCOL_PREFIX;
    delimiter = MWDedHost.PROTOCOL_DELIMITER;
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