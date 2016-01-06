package com.arkazex.frc2877.signin;

import com.arkazex.frc2877.signin.util.Color;
import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataListener;
import com.pi4j.io.serial.SerialFactory;

public class Rfid {
	
	//HID mode (Sends codes to a computer instead of processing)
	public static boolean hid = false;

	//Initialization method
	public static void init() {
		//Notify
		System.out.println("  Initializing RFID module...");
		System.out.print("    Creating Serial Connection...");
		//Create the serial connection
		Serial serial = SerialFactory.createInstance();
		//Notify
		System.out.println(Color.GREEN + " Done." + Color.RESET);
		System.out.print("    Opening Serial Connection...");
		//Open the serial connection
		serial.open(Serial.DEFAULT_COM_PORT, 115200);
		//Notify
		System.out.println(Color.GREEN + " Done." + Color.RESET);
		System.out.print("    Creating serial listener...");
		//Create serial listener
		SerialDataListener listener = new SerialDataListener() {
			@Override
			public void dataReceived(SerialDataEvent event) {
				Buzzer.beep();
				System.out.println("    Serial: " + event.getData());
				//Check mode
				if(HID.enabled()) {
					//HID mode, send command to connected device
					HID.hidSend(event.getData());
				} else {
					//Standard mode, send user trigger as normal
					Users.trigger(event.getData(), TriggerSource.RFID);
				}
			}
		};
		//Notify
		System.out.println(Color.GREEN + " Done." + Color.RESET);
		System.out.print("    Registering serial listener...");
		//Register the listener
		serial.addListener(listener);
		//Notify
		System.out.println(Color.GREEN + " Done." + Color.RESET);
		System.out.println("  RFID Module Initialization Complete.");
	}
}
