package com.arkazex.frc2877.signin;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

	//Log level
	private static final Level minlevel = Level.DEBUG;
	
	//Simple date format
	private static SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
	
	//Method for logging
	public static void log(Level level, String message) {
		//Check the level
		if(level.value >= minlevel.value) {
			//Show the message
			String msg = "[" + getTime() + "][" + level.getName() + "] " + message;
			System.out.println(msg);
		}
	}
	
	//Method for formatting the time
	private static String getTime() {
		return sdf.format(new Date());
	}
}
