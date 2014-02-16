package uk.ac.pisoc.wheresmybus.json;

import java.io.IOException;
import java.io.InputStream;

import uk.ac.pisoc.wheresmybus.logger.Logger;
import uk.ac.pisoc.wheresmybus.model.Bus;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class BusTimeParser {

    private static final String TAG = "BusAndStop";

    public Bus parse( InputStream in ) throws IOException {

        JsonFactory jf = new JsonFactory();
        JsonParser jp = jf.createParser( in );

        Bus bus = new Bus();

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
            Logger.log( TAG, "bus times not found." );
            throw new IOException();
        }
        return bus;
    }
}
