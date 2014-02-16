package uk.ac.pisoc.wheresmybus.json;

import java.io.IOException;
import java.io.InputStream;

import uk.ac.pisoc.wheresmybus.model.Bus;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class StrideJsonParser {

    private static final String TAG = "StrideJsonParser";

    private JsonFactory jsonFactory = new JsonFactory();

    /**
     *
     * @param in
     * @return
     * @throws JsonParseException
     * @throws IOException
     */
    public String parseAtcocode( InputStream in )
            throws JsonParseException, IOException {

        JsonParser jp = null;

        try {
            jp = jsonFactory.createParser( in );

            while ( jp.nextToken() != JsonToken.END_OBJECT ) {
                String fieldName = jp.getCurrentName();
                if ( "atcocode".equals( fieldName )) {
                    jp.nextToken();
                    return jp.getText();
                }
            }

            throw new JsonParseException( TAG, "no atcocode found." );

        } finally {
            if ( jp != null ) jp.close();
            if ( in != null ) in.close();
        }
    }

    /**
     *
     * @param in
     * @return
     * @throws JsonParseException
     * @throws IOException
     */
    public Bus parseBusTimes( InputStream in )
            throws JsonParseException, IOException {

        Bus bus = new Bus();
        JsonParser jp = null;

        try {
            jp = jsonFactory.createParser( in );

            while ( jp.nextToken() != JsonToken.END_OBJECT ) {
                String fieldName = jp.getCurrentName();
                if ( "line".equals( fieldName )) {
                    jp.nextToken();
                    bus.setNumber( jp.getText() );
                } else if ( "aimed_departure_time".equals( fieldName )) {
                    jp.nextToken();
                    bus.setTime( jp.getText() );
                }
            }

            if ( bus.getNumber() == null || bus.getTime() == null ) {
                throw new JsonParseException( TAG, "bus times not found." );
            }

            return bus;

        } finally {
            if ( jp != null ) jp.close();
            if ( in != null ) in.close();
        }
    }
}
