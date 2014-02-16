package uk.ac.pisoc.wheresmybus.worker;

import java.util.List;
import java.util.concurrent.BlockingQueue;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import uk.ac.pisoc.wheresmybus.cache.StatusIdCache;
import uk.ac.pisoc.wheresmybus.logger.Logger;
import uk.ac.pisoc.wheresmybus.model.HashtagTweet;

public class TwitterSearchWorker extends Worker {

    private static final String TAG = "TwitterSearchWorker";

    private final int CACHE_SIZE = 300;
    private StatusIdCache statusIdCache = new StatusIdCache( CACHE_SIZE );

    private Query twitterQuery;
    private QueryResult queryResult;

    public TwitterSearchWorker( BlockingQueue<HashtagTweet> bq,
                                Twitter twitter,
                                String threadName,
                                String query )
    {
        super( bq, twitter, threadName );
        twitterQuery = new Query( query );
        twitterQuery.setCount( 100 ); // max number of results
    }

    @Override
    public void run() {
        super.run();

        primeStatusIdCache();

        for ( ;; ) {
            try {
                List<Status> tweets = searchTwitter();

                for ( Status tweet : tweets ) {
                    if ( tweet.getGeoLocation() != null
                         && !statusIdCache.contains( tweet.getId() )) {

                        Logger.log( TAG, "found tweet from: @"
                                + tweet.getUser().getScreenName() );

                        statusIdCache.add( tweet.getId() );
                        bq.put( new HashtagTweet( tweet ));
                    }
                }
                sleep( 8000l ); // Wait 8 seconds
            } catch ( InterruptedException e ) { }
        }
    }

    private void primeStatusIdCache() {
        List<Status> tweets = searchTwitter();
        for ( Status tweet : tweets ) {
            if ( tweet.getGeoLocation() != null ) {
                statusIdCache.add( tweet.getId() );
            }
        }
        Logger.log( TAG, "cache primed with old tweets." );
    }

    private List<Status> searchTwitter() {
        try {
        	Logger.log( TAG, "searching twitter ..." );
            queryResult = twitter.search( twitterQuery );
        } catch ( TwitterException e ) {
            e.printStackTrace();
            Logger.log( TAG, "search failed : " + e.getMessage() );
        }
        return queryResult.getTweets();
    }
}
