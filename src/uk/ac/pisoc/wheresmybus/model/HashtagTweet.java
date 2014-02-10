package uk.ac.pisoc.wheresmybus.model;

import twitter4j.Status;
import uk.ac.pisoc.wheresmybus.logger.Logger;

public class HashtagTweet {
	
	private static final String TAG = "HashtagTweet";
	
	private String  userName;
	private long    replyToUserId;
	private double  lon;
	private double  lat;

	public HashtagTweet(Status tweet) {
		userName      = "@" + tweet.getUser().getName();
		replyToUserId = tweet.getInReplyToUserId();
		lon           = tweet.getGeoLocation().getLongitude();
		lat           = tweet.getGeoLocation().getLatitude();
		
		Logger.log(TAG, "hashtag tweet created.");
	}
	
	public String getLonLat() {
		return lon + ", " + lat;
	}

	public String getUserName() {
		return userName;
	}

	public long getReplyToUserId() {
		return replyToUserId;
	}

	@Override
	public String toString() {
		return "HashtagTweet [userName=" + userName + ", replyToUserId="
				+ replyToUserId + ", lon=" + lon + ", lat=" + lat + "]";
	}
}
