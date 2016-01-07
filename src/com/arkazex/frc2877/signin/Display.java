package com.arkazex.frc2877.signin;

import com.arkazex.lcd.LCD;
import com.arkazex.lcd.LCDMode;

public class Display {

	//LCD object
	public static LCD lcd;
	//LCD mode
	public static LCDMode mode;
	//Ready message
	public static final String defaultReadyMessage = "Ready";
	public static String readyMessage = defaultReadyMessage;
	
	//Initialization method
	public static void init() {
		//Notify
		Logger.log(Level.INFO, "Initializing Display..");
		//Try/catch
		try {
			//Get the LCD object
			lcd = new LCD(0x27);
			//Initialize the LCD
			if(!SignIn.rebooted) {
				lcd.init();
			}
			//Display the loading text
			printCenter("Loading...");
			lcd.position(1,0);
			printCenter("Please Wait...");
		} catch(Exception e) {
			//Fatal error
			Logger.log(Level.ERROR, "Failed to initialize display: " + e.getMessage());
			Logger.handleCrash(e);
			System.exit(0);
		}
		//Notify
		Logger.log(Level.OKAY, "Display ready!");
	}
	
	//Method for clearing line 1
	public static void clearl1() {
		//Set the lcd position
		lcd.position(0, 0);
		//Write over with spaces
		lcd.print("                ");
		//Reset the position
		lcd.position(0, 0);
	}
	
	//Method for going to ready mode
	public static void ready() {
		//Set the mode
		mode = LCDMode.IDLE;
		//Set the text
		clearl1();
		printCenter(readyMessage);
	}
	
	//Method for printing center
	public static void printCenter(String message) {
		//Get the message length
		int mlength = message.length();
		//Check warning
		if(mlength > 16) {
			//Warning
			Logger.log(Level.WARN, "Message \"" + message + "\" is too big for screen");
		}
		//Calculate the length difference
		int diff = 16 - mlength;
		//Calculate padding
		int padding = diff / 2;
		//Move to center
		lcd.position(0, 0);
		//Print the padding
		for(int i = 0; i < padding; i++) {
			lcd.print(" ");
		}
		//Print the message
		lcd.print(message);
	}
}
