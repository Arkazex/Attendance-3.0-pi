package com.arkazex.frc2877.signin;

import com.arkazex.frc2877.signin.util.Color;
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
		System.out.print("  Initializing Display...");
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
			System.out.println();
			System.out.println(Color.RED + "Encountered fatal error while initializing display" + Color.RESET);
			e.printStackTrace();
			System.exit(0);
		}
		//Notify
		System.out.println(Color.GREEN + " Done." + Color.RESET);
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
			System.out.println("Warning: Message \"" + message + "\" is too big for screen.");
			System.out.println("\tSome characters may have been lost.");
		}
		//Calculate the length difference
		int diff = 16 - mlength;
		//Calculate padding
		int padding = diff / 2;
		//Print the padding
		for(int i = 0; i < padding; i++) {
			lcd.print(" ");
		}
		//Print the message
		lcd.print(message);
	}
}
