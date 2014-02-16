package uk.ac.pisoc.wheresmybus.worker;

import java.util.concurrent.BlockingQueue;

import twitter4j.Twitter;
import uk.ac.pisoc.wheresmybus.model.HashtagTweet;

public abstract class Worker extends Thread {

	protected BlockingQueue<HashtagTweet> bq;
	protected Twitter twitter;

	public Worker( BlockingQueue<HashtagTweet> bq,
			       Twitter twitter,
			       String threadName )
	{
		super( threadName );
		this.bq = bq;
		this.twitter = twitter;
	}
}
