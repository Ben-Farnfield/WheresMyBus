package uk.ac.pisoc.wheresmybus.logger;

import java.util.Date;

public class Logger {

	public static void log(String tag, String msg) {
		
		System.out.println("[" + tag + "] [" + new Date() + "] " + msg);
	}
}
