package uk.ac.pisoc.wheresmybus.json;


@SuppressWarnings("serial")
public class JsonParseException extends Exception {

    private static final String TAG = "JsonParseException";

    private String message;

    public JsonParseException() {}

    public JsonParseException( String tag, String message ) {
        this.message = "[" + tag + "] [" + TAG + "] " + message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
