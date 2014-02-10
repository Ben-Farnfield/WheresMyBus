package uk.ac.pisoc.wheresmybus.worker;

import java.util.concurrent.BlockingQueue;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import uk.ac.pisoc.wheresmybus.logger.Logger;
import uk.ac.pisoc.wheresmybus.model.HashtagTweet;

public class TweetProcWorker extends Worker {
	
	private static final String TAG = "TweetProcWorker";

	public TweetProcWorker(BlockingQueue<HashtagTweet> bq, Twitter twitter) {
		super(bq, twitter);
	}
	
	@Override
	public void run() {
		super.run();
		
		for (;;) {
			try {
				HashtagTweet tweet = bq.take();			
				
				// Stride data stuff here
			
				
				// Create message to be sent to user
				String message = String.format("%s heres your random number %s", tweet.getUserName(), Math.random()*1000);
				
				// Send @ reply to user
				StatusUpdate statusUpdate = new StatusUpdate(message);
				statusUpdate.setInReplyToStatusId(tweet.getReplyToStatusId());
				twitter.updateStatus(statusUpdate);
				
				Logger.log(TAG, "message sent to: " + tweet.getUserName());
				
			} catch (InterruptedException e) {
				// do nothing
			} catch (TwitterException e) {
				e.printStackTrace();
				Logger.log(TAG, "Update failed: " + e.getMessage());
			}
		}
	}
}
