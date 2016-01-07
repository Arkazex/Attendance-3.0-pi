package com.arkazex.frc2877.signin;

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
		Logger.log(Level.INFO, "Initializing RFID module...");
		Logger.log(Level.DEBUG, "Creating Serial Connection...");
		//Create the serial connection
		Serial serial = SerialFactory.createInstance();
		//Notify
		Logger.log(Level.DEBUG, "Opening Serial Connection...");
		//Open the serial connection
		serial.open(Serial.DEFAULT_COM_PORT, 115200);
		//Notify
		Logger.log(Level.DEBUG, "Creating Serial Listener...");
		//Create serial listener
		SerialDataListener listener = new SerialDataListener() {
			@Override
			public void dataReceived(SerialDataEvent event) {
				//Beep
				Buzzer.beep();
				//Notify
				Logger.log(Level.DEBUG, "Serial Input: " + event.getData());
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
		Logger.log(Level.DEBUG, "Registering Serial Listener...");
		//Register the listener
		serial.addListener(listener);
		//Notify
		Logger.log(Level.OKAY, "RFID module ready!");
	}
}
