package uk.ac.pisoc.wheresmybus.json;

import java.io.IOException;
import java.io.InputStream;

import uk.ac.pisoc.wheresmybus.logger.Logger;
import uk.ac.pisoc.wheresmybus.model.Bus;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class BusAndStop {
	
	private static final String TAG = "BusAndStop";

    public Bus parse(InputStream in) throws IOException {

		JsonFactory jf = new JsonFactory();
		JsonParser jp = jf.createParser(in);
		
		while (jp.nextToken() != JsonToken.END_OBJECT) {
			String fieldName = jp.getCurrentName();
			if ("line".equalsIgnoreCase(fieldName)) {
				jp.nextToken();
				Bus bus = new Bus();
				bus.setNumber(jp.getText());
				bus.setTime(getTime(jp));
				Logger.log(TAG, "found bus times.");
				return bus;
			}
		}
		return null;
	}
	
	private static String getTime(JsonParser jp) 
			throws JsonParseException, IOException {
		while (jp.nextToken() != JsonToken.END_OBJECT) {
			String fieldName = jp.getCurrentName();
			if ("aimed_departure_time".equalsIgnoreCase(fieldName)) {
				jp.nextToken();
				return jp.getText();
			}
		}
		return null;
	}
}