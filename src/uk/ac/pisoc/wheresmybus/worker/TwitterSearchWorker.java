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
	
	private List<Status> tweetCache;
	
	private Query        twitterQuery;
	private QueryResult  queryResult;
		
	public TwitterSearchWorker(
			BlockingQueue<HashtagTweet> bq, Twitter twitter, String query) {
		
		super(bq, twitter);
		
		tweetCache = new ArrayList<>(); //TODO purge old tweets
		twitterQuery = new Query(query);
	}
	
	@Override
	public void run() {
		super.run();
		
		// Load the tweetCache with old tweets
		Logger.log(TAG, "searching for old tweets ...");
		List<Status> tweets = search();
		for (Status tweet : tweets) {
			if (tweet.getGeoLocation() != null) {
				tweetCache.add(tweet);
			}
		}
		Logger.log(TAG, "tweetCache loaded with old tweets.");

		// Search for new tweets
		for (;;) {
			Logger.log(TAG, "searching twitter ...");
			
			try {
				tweets = search();
				
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

			} catch (InterruptedException e) {
				// do nothing
			}
		}
	}
	
	private List<Status> search() {
		try {
			queryResult = twitter.search(twitterQuery);
		} catch (TwitterException e) {
			e.printStackTrace();
			Logger.log(TAG, "search failed : " + e.getMessage());
		}
		return queryResult.getTweets();
	}
}
