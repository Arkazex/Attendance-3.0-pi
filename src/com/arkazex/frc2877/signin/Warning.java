package com.arkazex.frc2877.signin;

public class Warning {

	//Active warning
	private static boolean active = false;
	
	//Shows the warning alert
	public static void show() {
		//Make active
		active = true;
		//Create the warning thread
		new Thread() {
			//Method
			@Override
			public void run() {
				//Wile warning is active
				while(active) {
					//Move to upper left corner and display mark
					Display.lcd.position(0, 0);
					Display.lcd.print("!");
					//Wait
					try { Thread.sleep(1000); } catch (InterruptedException e) {}
					//Move to upper left corner and display mark
					Display.lcd.position(0, 0);
					Display.lcd.print(" ");
					//Wait
					try { Thread.sleep(1750); } catch (InterruptedException e) {}
				}
			}
		}.start();
	}
	
	//Clears the warning alert
	public static void clear() {
		active = false;
	}
}
