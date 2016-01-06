package com.arkazex.frc2877.signin;

import com.arkazex.frc2877.signin.util.Color;

public class Reset {

	//Time
	public static long time = Long.MAX_VALUE;
	
	//Init
	public static void init() {
		//Notify
		System.out.println("  Initializing Reset timer...");
		System.out.print("    Creating thread...");
		//Create the thread
		new Thread() {
			//Run method
			@Override
			public void run() {
				//For as long as active
				while(SignIn.running) {
					//Check the last active time
					if(System.currentTimeMillis() > time) {
						//Clear text
						Keypad.input = "";
						//Reset
						Display.ready();
						//Set the last input time to something far away
						time = Long.MAX_VALUE;
					}
					//Wait
					try { Thread.sleep(500); } catch (InterruptedException e) {}
				}
			}
		}.start();
		//Notify
		System.out.println(Color.GREEN + " Done." + Color.RESET);
		System.out.println("  Reset timer Initialization Complete.");
	}
}
