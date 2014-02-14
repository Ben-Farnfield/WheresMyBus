package uk.ac.pisoc.wheresmybus.json;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class AtCoCodeParser {

    public String parse(InputStream in) throws IOException {

        JsonFactory jsonFactory = new JsonFactory();
        JsonParser jp = jsonFactory.createParser(in);

        if (jp.nextToken() != JsonToken.START_OBJECT) {
            throw new IOException("Expected data to start with an object");
        }

        jp.nextToken();

        while (jp.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = jp.getCurrentName();
            jp.nextToken();
            if (fieldName.equals("atcocode")) {
                return jp.getText();
            }
        }
        return null;
    }
}
