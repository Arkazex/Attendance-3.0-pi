package com.arkazex.frc2877.signin;

import com.arkazex.lcd.LCDMode;

public class SignIn {
	
	//Base url
	public static final String baseURL = "https://script.google.com/macros/s/AKfycbwdcxHYRVmO6BcTvdOrBm2j65gkuljtdYM4pQdHewHjJ7QKAh16/exec";

	//Running
	public static boolean running = true;
	//Rebooted
	public static boolean rebooted = false;
	
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
		Display.init();
		//Initialize the Buzzer
		Buzzer.init();
		//Initialize the Reset timer
		Reset.init();
		//Initialize the Keypad
		Keypad.init();
		//Initialize the user list
		Users.init();
		//Initialize the clock
		Clock.init();
		//Initialize the RFID module
		Rfid.init();
		//Initialize the HID module
		HID.init();
		//Initialize the virtual keypad
		VirtKeypad.init();
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
}
