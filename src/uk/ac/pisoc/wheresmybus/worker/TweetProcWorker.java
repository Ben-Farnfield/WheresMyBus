package uk.ac.pisoc.wheresmybus.worker;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import uk.ac.pisoc.wheresmybus.json.AtCoCodeParser;
import uk.ac.pisoc.wheresmybus.json.BusAndStop;
import uk.ac.pisoc.wheresmybus.logger.Logger;
import uk.ac.pisoc.wheresmybus.model.Bus;
import uk.ac.pisoc.wheresmybus.model.HashtagTweet;

public class TweetProcWorker extends Worker {
	
	private static final String TAG = "TweetProcWorker";
	
	private final String STRIDE_USERNAME = "";
	
	private String busStopURL = "http://api.stride-project.com/"
			+ "transportapi/7c60e7f4-20ff-11e3-857c-fcfb53959281/"
			+ "bus/stops/near";
	private String busStopParamsFS = "lat=%s&lon=%s";
	
	private String busTimesUrlFS = "http://api.stride-project.com/"
			+ "transportapi/7c60e7f4-20ff-11e3-857c-fcfb53959281/"
			+ "bus/stop/%s/live";
	
	private String tweetFS = "%s here’s your bus!\n"
			+ "\nBus number  : %s\n"
			+ "Arrival time : %s\n\n"
			+ "[Data provided by Stride at %s]";
	
	private Stride stride = new Stride(STRIDE_USERNAME);
	private AtCoCodeParser atcocodeParser = new AtCoCodeParser();
	private BusAndStop busAndStopParser = new BusAndStop();
	private DateFormat df = new SimpleDateFormat("kk:mm:ss");

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
				
				String atcocode = findBusStop(tweet);
		        if (atcocode == null) {
		            throw new Exception(
		            		"No bus stop found for " + tweet.getUserName());
		        }

				Bus bus = findNextBus(tweet, atcocode);
		        if (bus == null) {
		            throw new Exception(
		            		"No next bus found for " + tweet.getUserName());
		        }

				sendUpdate(tweet, bus);
				
			} catch (InterruptedException e) {
				// do nothing
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println(e.getMessage());
			}
		}
	}
	
	/* finds the users closest bus stop */
	private String findBusStop(HashtagTweet tweet) {
		
		Logger.log(TAG, getName() 
				+ " finding bus stop for " + tweet.getUserName());
		
		String busStopParams;
		HttpURLConnection connection;
		try {
			busStopParams = String.format(busStopParamsFS, 
					URLEncoder.encode(tweet.getLat(), "UTF-8"), 
					URLEncoder.encode(tweet.getLon(), "UTF-8"));
			connection = stride.getHttpURLConnection(busStopURL, busStopParams);
			return atcocodeParser.parse(connection.getInputStream());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/* finds the next bus for the given bus stop */
	private Bus findNextBus(HashtagTweet tweet, String atcocode) {
		Logger.log(TAG, getName() 
				+ " finding bus times for " + tweet.getUserName());
		
		String busTimesURL;
		HttpURLConnection connection;
		try {
			busTimesURL = String.format(
					busTimesUrlFS, URLEncoder.encode(atcocode, "UTF-8"));
			connection = stride.getHttpURLConnection(busTimesURL);
			return busAndStopParser.parse(connection.getInputStream());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/* sends an @reply to the user */
	private void sendUpdate(HashtagTweet tweet, Bus bus) {
		String message = String.format(tweetFS, tweet.getUserName(), 
				bus.getNumber(), bus.getTime(), df.format(new Date()));
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
