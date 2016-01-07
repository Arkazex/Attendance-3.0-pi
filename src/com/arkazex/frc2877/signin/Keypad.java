package com.arkazex.frc2877.signin;

import com.arkazex.lcd.LCDMode;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class Keypad {
	
	//GPIO pins
	public static GpioPinDigitalInput[] pins;

	//Input
	static String input = "";
	
	//GPIO listener
	public static GpioPinListenerDigital listener;
	
	public static void init() {
		//Notify
		Logger.log(Level.INFO, "Initializing Keypad...");
		Logger.log(Level.DEBUG, "Connecting to GPIO controller...");
		//Get the GPIO controller
		GpioController gpio = GpioFactory.getInstance();
		//Notify
		Logger.log(Level.DEBUG, "Allocating pins...");
		//Allocate the GPIO pins
		pins = new GpioPinDigitalInput[]{
			gpio.provisionDigitalInputPin(RaspiPin.GPIO_07,"*", PinPullResistance.PULL_DOWN),
			gpio.provisionDigitalInputPin(RaspiPin.GPIO_00,"7", PinPullResistance.PULL_DOWN),
			gpio.provisionDigitalInputPin(RaspiPin.GPIO_01,"4", PinPullResistance.PULL_DOWN),
			gpio.provisionDigitalInputPin(RaspiPin.GPIO_02,"1", PinPullResistance.PULL_DOWN),
			gpio.provisionDigitalInputPin(RaspiPin.GPIO_03,"0", PinPullResistance.PULL_DOWN),
			gpio.provisionDigitalInputPin(RaspiPin.GPIO_04,"8", PinPullResistance.PULL_DOWN),
			gpio.provisionDigitalInputPin(RaspiPin.GPIO_05,"5", PinPullResistance.PULL_DOWN),
			gpio.provisionDigitalInputPin(RaspiPin.GPIO_06,"2", PinPullResistance.PULL_DOWN),
			gpio.provisionDigitalInputPin(RaspiPin.GPIO_21,"#", PinPullResistance.PULL_DOWN),
			gpio.provisionDigitalInputPin(RaspiPin.GPIO_22,"9", PinPullResistance.PULL_DOWN),
			gpio.provisionDigitalInputPin(RaspiPin.GPIO_26,"6", PinPullResistance.PULL_DOWN),
			gpio.provisionDigitalInputPin(RaspiPin.GPIO_23,"3", PinPullResistance.PULL_DOWN)
		};
		//Notify
		Logger.log(Level.DEBUG, "Creating listener...");
		//Create the listener
		GpioPinListenerDigital listener = createListener();
		//Notify
		Logger.log(Level.DEBUG, "Registering listener...");
		//Register GPIO listeners
		for(GpioPinDigitalInput pin : pins) { pin.addListener(listener); }
		//Notify
		Logger.log(Level.OKAY, "Keypad ready!");
	}
	
	//Method for creating a GpioPinListener
	private static GpioPinListenerDigital createListener() {
		//Create and save the listener
		listener = new GpioPinListenerDigital() {
			@Override
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent arg0) {
				//Get the key
				String key = arg0.getPin().getName();
				//Check if this is a press event
				if(arg0.getState() == PinState.LOW) { return; }
				//Handle press
				handlePress(key);
			}
		};
		//Return reference
		return listener;
	}
	
	//Method for handling a press
	public static void handlePress(String key) {
		//Check if loading
		if(Display.mode == LCDMode.LOADING) {
			//Not allowed
			dbeep();
			//Delay
			delay(2);
			return;
		}
		//Start reset clock, or reset to 4 seconds from now
		Reset.time = System.currentTimeMillis() + 4000;
		//Beep
		Buzzer.beep();
		//Check the mode
		if(Display.mode != LCDMode.NUMINPUT) {
			//Change the display mode
			Display.mode = LCDMode.NUMINPUT;
			//Clear the input
			Display.clearl1();
			//Print the input message
			showInputMessage(); 
		}
		//Check if clear
		if(key.equals("*")) {
			//Clear the input
			clearInput();
			delay(2);
			return;
		}
		//Check if submit
		if(key.equals("#")) {
			//Notify
			Logger.log(Level.DEBUG, "Keypad input: " + input);
			//Check if debug code
			if(Debug.isDebugCode(input)) {
				//Trigger debug
				Debug.procDebugCode(input);
			} else {
				//Trigger users
				Users.trigger(input, TriggerSource.KPAD);
			}
			//Clear the input
			input = "";
			//Delay
			delay(2);
			//Finished
			return;
		}
		//Check the length
		if(input.length() > 3) {
			//Limit
			Buzzer.beep();
			delay(2);
			return;
		}
		//Print the character
		input += key;
		Display.lcd.position(0, 7 + input.length());
		Display.lcd.print(key);
		delay(2);
	}
	
	//Double beep
	private static void dbeep() {
		Buzzer.beep();
		try { Thread.sleep(25); } catch (InterruptedException e) {}
		Buzzer.beep();
	}
	
	//Show input message
	private static void showInputMessage() {
		//Clear the first line
		Display.clearl1();
		//Print the input text
		Display.lcd.print("    ID: ");
		//Clear the input dashes
		clearInput();
	}
	
	//Clears the input
	private static void clearInput() {
		//Clear the input string
		input = "";
		//Set the position
		Display.lcd.position(0, 8);
		//Print the dashes
		Display.lcd.print("----  ");
		//Set the position
		Display.lcd.position(0, 8);
	}
	
	//Delay
	private static void delay(int ms) {
		try { Thread.sleep(ms); } catch (InterruptedException e) {}
	}
}
