package com.arkazex.frc2877.signin;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class Buzzer {

	//GPIO pin
	private static GpioPinDigitalOutput pin;
	
	public static void init() {
		//Notify
		Logger.log(Level.INFO, "Initializing Buzzer...");
		Logger.log(Level.DEBUG, "Connecting to GPIO controller...");
		//Get the GPIO controller
		GpioController gpio = GpioFactory.getInstance();
		//Notify
		Logger.log(Level.DEBUG, "Assigning Pin...");
		//Allocate the pin
		pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_25, "buzzer");
		//Notify
		Logger.log(Level.OKAY, "Buzzer ready!");
	}
	
	//Beep method
	public static void beep() {
		beep(50);
	}
	
	//Beep for specific duration
	public static void beep(int duration) {
		//Turn the pin on
		pin.setState(PinState.HIGH);
		//Delay
		try { Thread.sleep(duration); } catch (InterruptedException e) {}
		//Turn the pin off
		pin.setState(PinState.LOW);
	}
	
	//Fatal error beep
	public static void fatalbeep() {
		//Run in new thread
		new Thread() {
			@Override
			//Thread method
			public void run() {
				//Beep 3 times
				for(int i = 0; i < 3; i++) {
					//Long beep
					beep(500);
					//Long pause
					try { Thread.sleep(500); } catch (InterruptedException e) {}
				}
			}
		}.start();
	}
}
