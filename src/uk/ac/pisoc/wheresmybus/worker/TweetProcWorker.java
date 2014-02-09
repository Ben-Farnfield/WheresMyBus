package uk.ac.pisoc.wheresmybus.worker;

import java.util.concurrent.BlockingQueue;

import twitter4j.Twitter;
import uk.ac.pisoc.wheresmybus.model.HashtagTweet;

public class TweetProcWorker extends Worker {

	public TweetProcWorker(BlockingQueue<HashtagTweet> bq, Twitter twitter) {
		super(bq, twitter);
	}
	
	@Override
	public void run() {
		super.run();
	}
}
