package uk.ac.pisoc.wheresmybus.net;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.apache.commons.codec.binary.Base64;

public class StrideConnection {

    private final String ENCODING = "UTF-8";
    private final String PASSWORD = " ";
    private String authString;

    /**
     * Constructs a new Stride object ready to produce HttpURLConnections.
     *
     * @param username	String representing your Stride user name.
     */
    public StrideConnection( String username ) {
        authString = getEncodedAuthString( username );
    }

    /**
     * Initialises, configures and returns an HttpURLConnection ready for
     * you to open an input stream. [without parameters]
     *
     * @param strideURL String representing Stride URL (without parameters).
     * @return			Returns a HttpURLConnection configured for Stride.
     * @throws MalformedURLException
     * @throws IOException
     */
    public HttpURLConnection getHttpURLConnection( String strideURL )
            throws MalformedURLException, IOException {

        HttpURLConnection connection = ( HttpURLConnection )
                new URL( strideURL ).openConnection();

        return configConnection( connection );
    }

    /**
     * Initialises, configures and returns an HttpURLConnection ready for
     * you to open an input stream. [with parameters]
     *
     * @param strideURL	String representing Stride URL (without parameters).
     * @param strideParams String representing query parameters.
     * @return			Returns a HttpURLConnection configured for Stride.
     * @throws MalformedURLException
     * @throws IOException
     */
    public HttpURLConnection getHttpURLConnection( String strideURL,
            String strideParams ) throws MalformedURLException, IOException {

        HttpURLConnection connection = ( HttpURLConnection )
                new URL( strideURL + "?" + strideParams ).openConnection();

        return configConnection( connection );
    }

    /* Configure connection object for authorised GET request */
    private HttpURLConnection configConnection(
            HttpURLConnection connection ) throws ProtocolException {

        connection.setRequestProperty( "Authorization", "Basic " + authString );
        connection.setRequestProperty( "Accept-Charset", ENCODING );
        connection.setRequestMethod( "GET" );
        connection.setDoInput( true );
        return connection;
    }

    /* Base64 encode authorisation string "userName:PASSWORD" */
    private String getEncodedAuthString( String username ) {
        String authString = username + ":" + PASSWORD;
        byte[] authStringBytes = Base64.encodeBase64( authString.getBytes() );
        return new String( authStringBytes );
    }
}
