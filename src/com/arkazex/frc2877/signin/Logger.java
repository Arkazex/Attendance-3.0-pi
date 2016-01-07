package com.arkazex.frc2877.signin;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

	//Log level
	private static final Level minlevel = Level.DEBUG;
	
	//Timestamp format
	private static SimpleDateFormat tsformat = new SimpleDateFormat("hh:mm:ss");
	
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
		return tsformat.format(new Date());
	}
	
	//Method for saving an exception
	public static void handleCrash(Exception e) {
		//Try/catch
		try {
			//Create the crash folder
			File cfolder = new File("crash");
			//Make sure folder exists
			cfolder.mkdir();
			//Create the log file
			File clog = new File(cfolder, "crash_" + System.nanoTime() + ".log");
			//Get the writer
			PrintWriter pw = new PrintWriter(new FileWriter(clog));
			//Write
			e.printStackTrace(pw);
			//Flush and close
			pw.flush();
			pw.close();
			//Notify
			Logger.log(Level.INFO, "Crash log saved to " + clog.getAbsolutePath());
		} catch(Exception failure) {
			//Oh the irony
			Logger.log(Level.ERROR, "Failed to save crash to disk: " + failure.getMessage());
		}
	}
}
