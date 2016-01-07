package com.arkazex.frc2877.signin;

import com.arkazex.lcd.LCDMode;

public class SignIn {
	
	//Base url
	public static final String baseURL = "https://script.google.com/macros/s/AKfycbwdcxHYRVmO6BcTvdOrBm2j65gkuljtdYM4pQdHewHjJ7QKAh16/exec";

	//Running
	public static boolean running = true;
	//Rebooted
	public static boolean rebooted = false;
	
	//Number of modules
	private static final int MODCOUNT = 9;
	
	//Main method
	public static void main(String[] args) {
		//Check the arguments
		if(args.length > 0) {
			if(args[0].equals("rb")) {
				rebooted = true;
			}
		};
		//Notify
		System.out.println("Initializing V3...");
		//Initialize the LCD
		Display.init();	status(1);
		//Initialize the Buzzer
		Buzzer.init();	status(2);
		//Initialize the Reset timer
		Reset.init();	status(3);
		//Initialize the Keypad
		Keypad.init();	status(4);
		//Initialize the user list
		Users.init();	status(5);
		//Initialize the clock
		Clock.init();	status(6);
		//Initialize the RFID module
		Rfid.init();	status(7);
		//Initialize the HID module
		HID.init();		status(8);
		//Initialize the virtual keypad
		VirtKeypad.init();	status(9);
		
		//Start the clock
		Display.mode = LCDMode.IDLE;
		//Notify
		System.out.println("Initialization Complete.");
		Display.ready();
		initbeep();
		System.out.println();
	}
	
	//Initialization beep
	private static void initbeep() {
		Buzzer.beep();
		try { Thread.sleep(100); } catch (InterruptedException e) {}
		Buzzer.beep();
	}
	
	//Shows a loading status indicator
	private static void status(int modnum) {
		//Set the label
		Display.clearl1();
		Display.lcd.print("Loading... (" + modnum + "/" + MODCOUNT + ")");
	}
}
