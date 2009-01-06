package client.protocol;

public interface IClient
{
    /**
     * The delimiter.  A tab character.
     */
    public static final String DELIMITER = "\t";

    /** if you understand this you are a 1.1-compliant client.
     *  Following DEFLATED + DELIMITER is the number of bytes in the undeflated text.
     *  This will be a maximum of 29999, so you don't have to buffer more than that.
     *  com.carnageblender.chat.net gives an example implemenation.
     */
    public static final String DEFLATED = "/deflated";

    // called when there's a system message to show
    public void systemMessage(String message);

    // called when there's an error message to show
    public void errorMessage(String message);

    // called when there's server input to process
    public void processIncoming(String incoming);

    // called when connection is lost
    public void connectionLost();

    // called when connection is established
    public void connectionEstablished();
}