package com.arkazex.frc2877.signin;

import com.arkazex.frc2877.signin.util.Color;
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
		System.out.println("  Initializing Buzzer...");
		System.out.print("    Retrieving GPIO controller...");
		//Get the GPIO controller
		GpioController gpio = GpioFactory.getInstance();
		//Notify
		System.out.println(Color.GREEN + " Done." + Color.RESET);
		System.out.print("    Allocating GPIO pin...");
		//Allocate the pin
		pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_25, "buzzer");
		//Notify
		System.out.println(Color.GREEN + " Done." + Color.RESET);
		System.out.println("  Buzzer Initialized.");
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
}
