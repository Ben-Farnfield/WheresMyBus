package uk.ac.pisoc.wheresmybus.json;

import java.io.IOException;
import java.io.InputStream;

import uk.ac.pisoc.wheresmybus.logger.Logger;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class AtcocodeParser {

    private static final String TAG = "AtCoCodeParser";

    public String parse( InputStream in ) throws IOException {

        JsonFactory jsonFactory = new JsonFactory();
        JsonParser jp = jsonFactory.createParser( in );

        if ( jp.nextToken() != JsonToken.START_OBJECT ) {
            Logger.log( TAG, "JSON doesn't begin with start object." );
            throw new IOException();
        }

        jp.nextToken();

        while ( jp.nextToken() != JsonToken.END_OBJECT ) {
            String fieldName = jp.getCurrentName();
            jp.nextToken();
            if ( "atcocode".equals( fieldName )) {
                return jp.getText();
            }
        }
        Logger.log( TAG, "No atcocode found." );
        throw new IOException();
    }
}
