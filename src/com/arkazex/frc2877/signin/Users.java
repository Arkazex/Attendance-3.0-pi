package com.arkazex.frc2877.signin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.arkazex.frc2877.signin.util.Color;
import com.arkazex.frc2877.signin.util.FileReader;
import com.arkazex.frc2877.signin.util.UrlFetcher;
import com.arkazex.lcd.LCDMode;

public class Users {
	
	//Maps
	public static HashMap<String,User> uidmap = new HashMap<String,User>();
	public static HashMap<String,User> cidmap = new HashMap<String,User>();

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
				//Get the file
				File udcache = new File("udat.json");
				//Get output
				FileOutputStream out = new FileOutputStream(udcache);
				//Save the data
				out.write(rawjson.getBytes());
				//Flush and close output
				out.flush(); out.close();
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
			JSONObject userinfo = users.getJSONObject(i);
			//Create the user
			User user = new User(userinfo);
			//Save the info
			uidmap.put(userinfo.get("uid") + "", user);
			cidmap.put(userinfo.get("cid") + "", user);
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
		User user = getuser(id, source);
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
			System.out.println("[" + source.name() + "] Trigger " + id + " " + user.data.getString("fname"));
			Display.lcd.print("Hello " + user.data.getString("fname"));
			//Set the reset clock
			Reset.time = System.currentTimeMillis() + 1500;
		}
	}
	
	//Get a user
	private static User getuser(String id, TriggerSource ts) {
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
