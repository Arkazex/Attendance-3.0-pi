package com.arkazex.frc2877.signin;

public class Reset {

	//Time
	public static long time = Long.MAX_VALUE;
	
	//Init
	public static void init() {
		//Notify
		Logger.log(Level.INFO, "Initializing Reset timer...");
		Logger.log(Level.DEBUG, "Creating thread...");
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
		Logger.log(Level.OKAY, "Reset timer ready!");
	}
}
