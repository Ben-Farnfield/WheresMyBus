package uk.ac.pisoc.wheresmybus.server;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import uk.ac.pisoc.wheresmybus.logger.Logger;
import uk.ac.pisoc.wheresmybus.model.HashtagTweet;
import uk.ac.pisoc.wheresmybus.worker.TweetProcWorker;
import uk.ac.pisoc.wheresmybus.worker.TwitterSearchWorker;

public class WheresMyBusServer {
	
	private static final String TAG = "WheresMyBusServer";
	
	private BlockingQueue<HashtagTweet> bq;
	private Twitter                     twitter;
	private List<TweetProcWorker>       tweetProcWorkers;
	private TwitterSearchWorker         twitterSearchWorker;

	public static void main(String[] args) {
		
		int numThreads = Integer.parseInt(args[0]);
		int queueSize  = Integer.parseInt(args[1]);
		String query   = args[2];
		
		WheresMyBusServer server = 
				new WheresMyBusServer(numThreads, queueSize, query);
		
		server.start();
	}
	
	public WheresMyBusServer(int numThreads, int queueSize, String query) {
		
		bq = new ArrayBlockingQueue<>(queueSize);
		twitter = TwitterFactory.getSingleton();
		
		for (int i=0; i < numThreads; i++) {
			tweetProcWorkers.add(new TweetProcWorker(bq, twitter));
		}
		
		twitterSearchWorker = new TwitterSearchWorker(bq, twitter, query);
		
		Logger.log(TAG, "server created.");
	}
	
	public void start() {
		
		for (TweetProcWorker worker : tweetProcWorkers) {
			worker.start();
		}
		
		twitterSearchWorker.start();
		
		Logger.log(TAG, "server started.");
	}
}
