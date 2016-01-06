package com.arkazex.frc2877.signin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.arkazex.frc2877.signin.util.Color;
import com.arkazex.frc2877.signin.util.FileReader;
import com.arkazex.frc2877.signin.util.FileWriter;
import com.arkazex.frc2877.signin.util.UrlFetcher;
import com.arkazex.lcd.LCDMode;

public class Users {
	
	//Maps
	public static HashMap<String,String[]> uidmap = new HashMap<String,String[]>();
	public static HashMap<String,String[]> cidmap = new HashMap<String,String[]>();

	public static void init() {
		//Notify
		System.out.println("  Initializing User List...");
		//Update the user list
		update();
		//Done
		System.out.println("  User List Initialized.");
	}
	
	//Update method
	static void update() {
		//User data json
		JSONObject udata;
		//Try/catch
		try {
			//Notify
			System.out.print("    Retrieving user list...");
			//Check if skip
			if(Keypad.pins[0].isHigh()) {
				//Skip re-download
				System.out.print(Color.YELLOW + " Skipping..." + Color.RESET);
				//Load from file
				udata = loadFromFile();
			} else {
				//Apply the parameters
				String url = SignIn.baseURL + "?cmd=getUsers";
				//Get the user
				String rawjson = UrlFetcher.fetch(url);
				//Parse the data
				udata = new JSONObject(rawjson);
				//Save the data
				FileWriter.writeFile(new File("udat.json"), rawjson);
			}
		} catch(Exception e) {
			//Error, trigger warning mode
			Warning.show();
			//Load the data from file
			udata = loadFromFile();
		}
		//Notify
		System.out.println(Color.GREEN + " Done." + Color.RESET);
		System.out.print("    Processing user list...");
		//Get the users
		JSONArray users = udata.getJSONArray("data");
		//Process the users
		for(int i = 0; i < users.length(); i++) {
			//Get the user
			JSONObject user = users.getJSONObject(i);
			//Get the user info as an array
			String[] info = new String[]{
					user.get("uid") + "",
					user.get("cid") + "",
					user.getString("fname"),
					user.getString("lname")};
			//Save the info
			uidmap.put(user.get("uid") + "", info);
			cidmap.put(user.get("cid") + "", info);
		}
		System.out.println(Color.GREEN + " Done." + Color.RESET);
	}
	//Load from file
	private static JSONObject loadFromFile() {
		//Load the user file
		String users = null;
		try {
			users = FileReader.readFile(new File("udat.json"));
		} catch (IOException e1) {
			//Fatal error
			System.out.println();
			System.out.println("FATAL ERROR: No user table");
			Display.lcd.clear();
			Display.lcd.print("  FATAL ERROR");
			Display.lcd.position(1, 0);
			Display.lcd.print(" NO USER TABLE");
			System.exit(0);
		}
		//Process the object
		return new JSONObject(users);
	}
	
	//Trigger sign-in
	public static void trigger(String id, TriggerSource source) {
		//Freeze
		Display.mode = LCDMode.LOADING;
		//Get the user
		String[] user = getuser(id, source);
		//Check if user exists
		if(user == null) {
			//Clear the first line of the display
			Display.clearl1();
			//Print the error message
			System.out.println("[" + source.name() + "] Invalid ID " + id);
			Display.lcd.print("   INVALID ID");
			//Set the reset clock
			Reset.time = System.currentTimeMillis() + 1500;
			//Alert tone
			Buzzer.beep(500);
		} else {
			//Trigger the tap
			sendTrigger(id);
			//Display name
			Display.clearl1();
			//Print the name
			System.out.println("[" + source.name() + "] Trigger " + id + " " + user[2]);
			Display.lcd.print("Hello " + user[2]);
			//Set the reset clock
			Reset.time = System.currentTimeMillis() + 1500;
		}
	}
	
	//Get a user
	private static String[] getuser(String id, TriggerSource ts) {
		//Check source
		if(ts == TriggerSource.KPAD) {
			//Return user from user ID map
			return uidmap.get(id);
		} else {
			//Return user from card id map
			return cidmap.get(id);
		}
	}
	
	//Trigger a sign-in event
	public static void sendTrigger(String id) {
		//Trigger in another thread
		new Thread() {
			@Override
			public void run() {
				//Append the arguments
				String url = SignIn.baseURL + "?cmd=trigger&id=" + id;
				//Request
				try { System.out.println(UrlFetcher.fetch(url)); } catch (IOException e) {
					
				}
			}
		}.start();
	}
}
