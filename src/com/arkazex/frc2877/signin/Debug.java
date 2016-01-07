package com.arkazex.frc2877.signin;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import org.json.JSONObject;

import com.arkazex.frc2877.signin.util.UrlFetcher;
import com.arkazex.lcd.LCDMode;

public class Debug {

	//Debug codes
	private static final String ipcode = "7372"; //Show IP address
	private static final String macode = "7371"; //Show MAC address
	private static final String utcode = "7373"; //Update the user table
	private static final String srcode = "7374"; //Soft reboot code
	private static final String hrcode = "7375"; //Hard reboot code
	private static final String excode = "7376"; //Exit Application
	private static final String endall = "7377"; //Close all open things
	
	//Debug mode enabled
	public static boolean enabled = false;
	
	public static boolean isDebugCode(String code) {
		//Enable code
		if(code.equals("7777")) {
			//Special case
			return true;
		}
		//Test
		boolean debug = 
				code.equals(ipcode) || //Show IP
				code.equals(macode) || //Show MAC
				code.equals(utcode) || //Update user table
				code.equals(srcode) || //Soft-reboot
				code.equals(hrcode) || //Hard-reboot
				code.equals(excode) || //Terminate
				code.equals(endall);   //Sign-out all remaining users
		//Notify
		if(debug) {
			//Check if debug codes are enabled
			if(!enabled) {
				System.out.println("Debug codes are not enabled!");
				return false;
			}
			//This is a debug code
			System.out.println(code + " is a debug code");
			return true;
		}
		//Not a debug code
		return false;
	}
	
	//Process a debug code
	public static void procDebugCode(String code) {
		//Check for enable code
		if(code.equals("7777")) {
			//Toggle mode
			enabled = !enabled;
			//Notify
			System.out.println("Debug mode " +
					(enabled ? "enabled" : "disabled"));
			//Write to screen
			Display.clearl1();
			Display.printCenter("Debug mode " +
					(enabled ? "enabled" : "disabled"));
			//Set reset
			Display.mode = LCDMode.DEBUG;
			Reset.time = System.currentTimeMillis() + 2000;
		}
		//Check if enabled
		if(!enabled) {
			System.out.println("Debug codes are not enabled!");
			return;
		}
		//Set the mode
		Display.mode = LCDMode.DEBUG;
		//Process
		switch(code) {
			case(ipcode): showIP(); break;
			case(macode): showMA(); break;
			case(utcode): updateUT(); break;
			case(srcode): sr(); break;
			case(hrcode): hr(); break;
			case(excode): ex(); break;
			case(endall): ea(); break;
		}
	}
	
	//Soft reboot
	private static void sr() {
		//Soft reboot
		Display.clearl1();
		Display.printCenter("Restarting...");
		System.out.println("Restarting...");
		System.exit(1);
	}
	//Hard reboot
	private static void hr() {
		//Hard reboot
		Display.clearl1();
		Display.printCenter("Rebooting...");
		System.out.println("Rebooting...");
		System.exit(8);
	}
	//Exit
	private static void ex() {
		//Exit application
		Display.clearl1();
		Display.printCenter("-TERMINATED-");
		System.out.println("Application Terminated");
		System.exit(9);
	}
	
	public static void showIP() {
		//Try/catch block
		try {
			//Distance the reset
			Reset.time = Long.MAX_VALUE;
			//Get the interface
			NetworkInterface i = getInterface();
			//Get the addresses
			Enumeration<InetAddress> addresses = i.getInetAddresses();
			//Check each
			while(addresses.hasMoreElements()) {
				//Get the address
				InetAddress addr = addresses.nextElement();
				//Check
				if(!addr.getHostAddress().equals("127.0.0.1")) {
					//That's it
					Display.clearl1();
					Display.printCenter(addr.getHostAddress());
				}
			}
			//Show the address
		} catch (Exception e) {
			//Failed to load
			Display.clearl1();
			Display.printCenter("IP Error");
			//Clear in 4 seconds
			Reset.time = System.currentTimeMillis() + 4000;
		}
	}
	public static void showMA() {
		//Try/catch block
		try {
			//Get the interface
			NetworkInterface net = getInterface();
			//Get the mac address
			byte[] mac = net.getHardwareAddress();
			//Create a string builder
			StringBuilder sb = new StringBuilder();
			//Build the MAC address
			for (int i = 0; i < mac.length; i++) {
				sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "" : ""));		
			}
			//Print
			Display.clearl1();
			Display.printCenter(sb.toString());
			//Distance the reset time
			Reset.time = Long.MAX_VALUE;
		} catch(Exception e) {
			//Failed to load
			Display.clearl1();
			Display.printCenter("MAC Error");
			//Clear in 4 seconds
			Reset.time = System.currentTimeMillis() + 4000;
		}
	}
	private static NetworkInterface getInterface() {
		//Try/catch
		try {
			//Get the interfaces
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			//Iterate through the interfaces
			while(interfaces.hasMoreElements()) {
				//Get the interface
				NetworkInterface i = interfaces.nextElement();
				//Check the addresses
				Enumeration<InetAddress> addresses = i.getInetAddresses();
				//Iterate through the addresses
				while(addresses.hasMoreElements()) {
					//Check
					if(!addresses.nextElement().getHostAddress().equals("127.0.0.1")) {
						//Found it!
						return i;
					}
				}
			}
			//Failure
			return null;
		} catch(Exception e) {
			//Failure
			return null;
		}
	}
	
	private static void ea() {
		//Close all open events
		System.out.println("Signing out all users...");
		Display.mode = LCDMode.LOADING;
		Display.clearl1();
		Display.printCenter("Signing out...");
		Reset.time = System.currentTimeMillis() + 10000;
		//Get the URL
		String url = SignIn.baseURL + "?cmd=closeAll";
		//Request
		try {
			//Get result
			String result = UrlFetcher.fetch(url);
			//Parse result
			JSONObject usr = new JSONObject(result);
			//Print result
			System.out.println("Signed out " + usr.getInt("closed") + " users");
			Display.clearl1();
			Display.printCenter("Done (" + usr.getInt("closed") + " Users)");
			//Finished
			Reset.time = System.currentTimeMillis() + 2000;
		} catch (IOException e) {}
	}
	
	private static void updateUT() {
		//Set reset time
		Reset.time = Long.MAX_VALUE;
		//Notify
		System.out.println("DEBUG: Updating user table.");
		//Set status
		Display.mode = LCDMode.LOADING;
		Display.clearl1();
		Display.printCenter("Updating Table..");
		//Update table
		Users.update();
		//Finished
		Display.ready();
		//Notify
		System.out.println("DEBUG: User table updated.");
	}
}
