package uk.ac.pisoc.wheresmybus.worker;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import uk.ac.pisoc.stride.Stride;
import uk.ac.pisoc.wheresmybus.json.JsonParseException;
import uk.ac.pisoc.wheresmybus.json.StrideJsonParser;
import uk.ac.pisoc.wheresmybus.logger.Logger;
import uk.ac.pisoc.wheresmybus.model.Bus;
import uk.ac.pisoc.wheresmybus.model.HashtagTweet;

public class TweetProcWorker extends Worker {

    private static final String TAG = "TweetProcWorker";

    private final String STRIDE_USERNAME =
            ""; // TODO read from file

    private Stride stride = new Stride( STRIDE_USERNAME );

    private StrideJsonParser strideJsonParser = new StrideJsonParser();

    /* Stride URL's */

    private final String transportapiURL = "http://api.stride-project.com/"
            + "transportapi/7c60e7f4-20ff-11e3-857c-fcfb53959281/bus/";

    private String busStopURL = transportapiURL + "stops/near";
    private String busStopParamsFS = "lat=%s&lon=%s";

    private String busTimesFS = transportapiURL + "stop/%s/live";

    /* Tweet body */

    private String tweetFS = "%s here’s your bus!\n"
                           + "\n"
                           + "Bus number  : %s\n"
                           + "Arrival time  : %s\n"
                           + "\n"
                           + "[Data provided by Stride at %s]";

    private DateFormat df = new SimpleDateFormat( "kk:mm:ss" );

    /**
     *
     * @param bq			BlockingQueue holding jobs for this worker.
     * @param twitter		Twitter object used to send reply to user.
     * @param threadName	String containing the user selected name for this
     * 						worker thread.
     */
    public TweetProcWorker( BlockingQueue<HashtagTweet> bq,
                            Twitter twitter,
                            String threadName )
    {
        super( bq, twitter, threadName );
    }

    /**
     *
     */
    @Override
    public void run() {
        super.run();

        for ( ;; ) {
            try {
                Logger.log( TAG, getName() + " ready for job." );
                HashtagTweet tweet = bq.take();
                Logger.log( TAG, getName() + " started job." );

                String atcocode =
                        findBusStop( tweet.getLat(), tweet.getLon() );
                Logger.log( TAG, getName() + " found closest bus stop to "
                                           + tweet.getUserName() );

                Bus bus = findNextBus( atcocode );
                Logger.log( TAG, getName() + " found bus times for "
                                           + tweet.getUserName() );

                sendUpdate( tweet, bus );
                Logger.log( TAG, getName() + " sent message to "
                                           + tweet.getUserName() );
            } catch ( IOException | JsonParseException | TwitterException e ) {
                Logger.log( TAG, e.getMessage() );
                // TODO send apology tweet to user.
            } catch ( InterruptedException e ) {}
        }
    }

    /* finds the users closest bus stop */
    private String findBusStop( String lat, String lon )
            throws JsonParseException, IOException {

        String busStopParams = String.format( busStopParamsFS,
                URLEncoder.encode( lat, "UTF-8" ),
                URLEncoder.encode( lon, "UTF-8" ));

        HttpURLConnection con =
                stride.getHttpURLConnection( busStopURL, busStopParams );

        return strideJsonParser.parseAtcocode( con.getInputStream() );
    }

    /* finds the next bus for the given bus stop */
    private Bus findNextBus( String atcocode )
            throws JsonParseException, IOException {

        String busTimesURL = String.format( busTimesFS,
                URLEncoder.encode( atcocode, "UTF-8" ));

        HttpURLConnection con =
                stride.getHttpURLConnection( busTimesURL );

        return strideJsonParser.parseBusTimes( con.getInputStream() );
    }

    /* sends an @reply to the user */
    private void sendUpdate( HashtagTweet tweet, Bus bus )
            throws TwitterException {

        String message = String.format( tweetFS, tweet.getUserName(),
                bus.getNumber(), bus.getTime(), df.format( new Date() ));

        StatusUpdate statusUpdate = new StatusUpdate( message );
        statusUpdate.setInReplyToStatusId( tweet.getReplyToStatusId() );
        twitter.updateStatus( statusUpdate );
    }
}
