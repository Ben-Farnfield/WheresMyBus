package uk.ac.pisoc.wheresmybus.json;

import java.io.IOException;
import java.io.InputStream;

import uk.ac.pisoc.wheresmybus.logger.Logger;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class AtcocodeParser {

    private static final String TAG = "AtcocodeParser";

    private JsonFactory jsonFactory = new JsonFactory();

    public String parse( InputStream in ) throws IOException {

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

            Logger.log( TAG, "no atcocode found." );
            throw new IOException();

        } finally {
            if ( jp != null ) jp.close();
            if ( in != null ) in.close();
        }
    }
}
