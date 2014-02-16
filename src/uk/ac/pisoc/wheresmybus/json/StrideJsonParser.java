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
     */
    public String parseAtcocode( InputStream in ) throws JsonParseException {

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

        } catch ( IOException e ) {
            throw new JsonParseException( TAG, e.getMessage() );
        } finally {
            try {
                if ( jp != null ) jp.close();
                if ( in != null ) in.close();
            } catch ( IOException e ) {}
        }
    }

    /**
     *
     * @param in
     * @return
     * @throws JsonParseException
     */
    public Bus parseBusTimes( InputStream in ) throws JsonParseException {

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

        } catch ( IOException e ) {
            throw new JsonParseException( TAG, e.getMessage() );
        } finally {
            try {
                if ( jp != null ) jp.close();
                if ( in != null ) in.close();
            } catch ( IOException e ) {}
        }
    }
}
