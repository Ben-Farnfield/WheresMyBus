package uk.ac.pisoc.wheresmybus.worker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import uk.ac.pisoc.wheresmybus.logger.Logger;
import uk.ac.pisoc.wheresmybus.model.HashtagTweet;

public class TwitterSearchWorker extends Worker {
	
	private static final String TAG = "TwitterSearchWorker";
	
	public static final String QUERY = "#wheresmybus";
	public static final String TEST_QUERY  = "#somerandomtag6";
	
	private List<Status> tweetCache;
	
	private Query twitterQuery;
	private QueryResult queryResult;
		
	public TwitterSearchWorker(
			BlockingQueue<HashtagTweet> bq, Twitter twitter, String query) {
		
		super(bq, twitter);
		
		tweetCache = new ArrayList<>();
		twitterQuery = new Query(query);
	}
	
	@Override
	public void run() {
		super.run();
		
		for (;;) {
			
			Logger.log(TAG, "searching twitter ...");
			
			try {
				
				queryResult = twitter.search(twitterQuery);		
				List<Status> tweets = queryResult.getTweets();
				
				for (Status tweet : tweets) {
					// Check geolocation data has been included.
					if (tweet.getGeoLocation() != null) {
						// Check we've not seen this tweet before.
						if (! tweetCache.contains(tweet)) {
							
							tweetCache.add(tweet);
							bq.put(new HashtagTweet(tweet));
							Logger.log(TAG, "found tweet from: @" 
							                + tweet.getUser().getName());
						}
					}
				}
				
				sleep(8000l); // Wait 8 seconds (5 is the min we could set).
				
			} catch (TwitterException e) {
				e.printStackTrace();
				Logger.log(TAG, "Search failed : " + e.getMessage());
				System.exit(-1);
			} catch (InterruptedException e) {
				// do nothing
			}
		}
	}
}
