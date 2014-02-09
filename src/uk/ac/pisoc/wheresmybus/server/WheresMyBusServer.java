package uk.ac.pisoc.wheresmybus.server;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import uk.ac.pisoc.wheresmybus.model.HashtagTweet;
import uk.ac.pisoc.wheresmybus.worker.TweetProcWorker;
import uk.ac.pisoc.wheresmybus.worker.TwitterSearchWorker;

public class WheresMyBusServer {
	
	private BlockingQueue<HashtagTweet> bq;
	private Twitter twitter;
	private List<TweetProcWorker> tweetProcWorkers;
	private TwitterSearchWorker twitterSearchWorker;

	public static void main(String[] args) {
		
		int numThreads = Integer.parseInt(args[0]);
		int queueSize  = Integer.parseInt(args[1]);
		
		WheresMyBusServer wheresMyBus = 
				new WheresMyBusServer(numThreads, queueSize);
		
		wheresMyBus.start();
	}
	
	public WheresMyBusServer(int numThreads, int queueSize) {
		
		bq = new ArrayBlockingQueue<>(queueSize);
		twitter = TwitterFactory.getSingleton();
		
		for (int i=0; i < numThreads; i++) {
			tweetProcWorkers.add(new TweetProcWorker(bq, twitter));
		}
		
		twitterSearchWorker = new TwitterSearchWorker(bq, twitter);
	}
	
	public void start() {
		
		for (TweetProcWorker worker : tweetProcWorkers) {
			worker.start();
		}
		
		twitterSearchWorker.start();
	}
}
