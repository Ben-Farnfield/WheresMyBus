package uk.ac.pisoc.wheresmybus.worker;

import java.util.concurrent.BlockingQueue;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import uk.ac.pisoc.stride.Stride;
import uk.ac.pisoc.wheresmybus.logger.Logger;
import uk.ac.pisoc.wheresmybus.model.HashtagTweet;

public class TweetProcWorker extends Worker {
	
	private static final String TAG = "TweetProcWorker";
	
	private final String STRIDE_USERNAME = "";
	
	private String nearbyStop = "http://api.stride-project.com/"
			+ "transportapi/7c60e7f4-20ff-11e3-857c-fcfb53959281/"
			+ "bus/stops/near?lon=%s&lat=%s";
	
	private String busDeparture = "http://api.stride-project.com/"
			+ "transportapi/7c60e7f4-20ff-11e3-857c-fcfb53959281/"
			+ "bus/stop/%s/live";
	
	private Stride stride = new Stride(STRIDE_USERNAME);

	public TweetProcWorker(BlockingQueue<HashtagTweet> bq, Twitter twitter, 
			String threadName) {
		
		super(bq, twitter, threadName);
	}
	
	@Override
	public void run() {
		super.run();
		
		for (;;) {
			try {
				Logger.log(TAG, getName() + " waiting for job.");
				HashtagTweet tweet = bq.take();
				Logger.log(TAG, getName() + " starting job ...");
				
				// Nearby Stops
				String nearbyStopURL = getNearbyStopURL(tweet);
				
				String atcocode = "someCode"; // This will come from Stride
				
				// Live Bus Departures
				String liveBusDepartURL = getLiveBusDepartURL(atcocode);
				
				// Stride data stuff here
				
				
				// Create message to be sent to user
				String message = String.format("%s Have a random number: %s",
						                       tweet.getUserName(),
						                       Math.random()*1000);
				
				// Send @ reply to user
				sendUpdate(tweet, message);
				
			} catch (InterruptedException e) {}
		}
	}
	
	private String getNearbyStopURL(HashtagTweet tweet) {
		return String.format("http://api.stride-project.com/transportapi/"
				+ "7c60e7f4-20ff-11e3-857c-fcfb53959281/bus/stops/near?"
				+ "lon=%s&lat=%s",tweet.getLon(), tweet.getLat());
	}
	
	private String getLiveBusDepartURL(String atcocode) {
		return String.format("http://api.stride-project.com/transportapi/"
				+ "7c60e7f4-20ff-11e3-857c-fcfb53959281/bus/stop/%s/live",
				atcocode);
	}
	
	private void sendUpdate(HashtagTweet tweet, String message) {
		StatusUpdate statusUpdate = new StatusUpdate(message);
		statusUpdate.setInReplyToStatusId(tweet.getReplyToStatusId());
		try {
			twitter.updateStatus(statusUpdate);
			Logger.log(TAG, "message sent to: " + tweet.getUserName());
		} catch (TwitterException e) {
			e.printStackTrace();
			Logger.log(TAG, "Update failed: " + e.getMessage());
		}
	}
}
