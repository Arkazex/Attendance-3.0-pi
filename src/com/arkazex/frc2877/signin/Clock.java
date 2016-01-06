package com.arkazex.frc2877.signin;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.arkazex.frc2877.signin.util.Color;
import com.arkazex.lcd.LCDMode;

public class Clock {

	//Initialization method
	public static void init() {
		//Notify
		System.out.print("  Initializing Clock...");
		//Create the clock thread
		new Thread() {
			//Run method
			@Override
			public void run() {
				//Wait for the LCD to enter idle mode
				while(Display.mode != LCDMode.IDLE) { try {Thread.sleep(100); } catch(Exception e) {}}
				//For as long as the system is running
				while(SignIn.running) {
					//Update the clock
					update();
					//Wait for 1 minute
					try { Thread.sleep(15000); } catch (InterruptedException e) {}
				}
			}
		}.start();
		//Notify
		System.out.println(Color.GREEN + " Done." + Color.RESET);
	}
	
	//Update method
	public static void update() {
		//Create a date format
		SimpleDateFormat df = new SimpleDateFormat("MM/dd   hh:mm a");
		//Process the format
		String date = df.format(new Date());
		//Set the LCD position
		Display.lcd.position(1, 0);
		//Print the date
		Display.lcd.print(date);
	}
}
