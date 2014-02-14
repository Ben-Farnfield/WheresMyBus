package uk.ac.pisoc.wheresmybus.model;

import twitter4j.Status;
import uk.ac.pisoc.wheresmybus.logger.Logger;

public class HashtagTweet {
	
	private static final String TAG = "HashtagTweet";
	
	private String  userName;
	private long    replyToStatusId;
	private double  lon;
	private double  lat;

	public HashtagTweet(Status tweet) {
		userName        = "@" + tweet.getUser().getScreenName();
		replyToStatusId = tweet.getInReplyToStatusId();
		lon             = tweet.getGeoLocation().getLongitude();
		lat             = tweet.getGeoLocation().getLatitude();
		
		Logger.log(TAG, "HashtagTweet created.");
	}
	
	public String getLon() { return "" + lon; }
	
	public String getLat() { return "" + lat; }

	public String getUserName() { return userName; }

	public long getReplyToStatusId() { return replyToStatusId; }

	@Override
	public String toString() {
		return "HashtagTweet [userName=" + userName + ", replyToStatusId="
				+ replyToStatusId + ", lon=" + lon + ", lat=" + lat + "]";
	}
}
