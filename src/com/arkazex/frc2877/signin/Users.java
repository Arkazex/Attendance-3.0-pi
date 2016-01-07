package com.arkazex.frc2877.signin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.arkazex.frc2877.signin.util.UrlFetcher;
import com.arkazex.lcd.LCDMode;

public class Users {
	
	//Maps
	public static HashMap<String,User> uidmap = new HashMap<String,User>();
	public static HashMap<String,User> cidmap = new HashMap<String,User>();

	public static void init() {
		//Notify
		Logger.log(Level.INFO, "Initializing User List...");
		//Update the user list
		update();
		//Done
		Logger.log(Level.OKAY, "User List ready!");
	}
	
	//Update method
	static void update() {
		//User data JSON object
		JSONObject udata = null;
		//Try/catch
		try {
			//Notify
			Logger.log(Level.DEBUG, "Updating user list...");
			//Get the query URL
			String url = SignIn.baseURL + "?cmd=getUsers";
			//Execute the command, and get the response
			String rawjson = UrlFetcher.fetch(url);
			//Notify
			Logger.log(Level.DEBUG, "Parsing user list...");
			//Parse the data
			udata = new JSONObject(rawjson);
		} catch(Exception e) {
			//Log error
			Logger.log(Level.ERROR, "Failed to retrieve user list");
			//Print error to screen
			Display.lcd.position(0, 0);
			Display.lcd.print("User List error!");
			//Beep the error
			Buzzer.fatalbeep();
			//Restart in 5 seconds
			SignIn.restart(5);
		}
		//Notify
		Logger.log(Level.DEBUG, "Processing user list...");
		//Get the users
		JSONArray users = udata.getJSONArray("data");
		//Process the users
		for(int i = 0; i < users.length(); i++) {
			//Get the user
			JSONObject userinfo = users.getJSONObject(i);
			//Create the user
			User user = new User(userinfo);
			//Check for duplicate UID
			if(uidmap.containsKey(userinfo.getString("uid"))) {
				//Duplicate UID
				Logger.log(Level.WARN, "Duplicate UID \"" + userinfo.getString("uid") + "\"");
			}
			//Check for duplicate CID
			if(cidmap.containsKey(userinfo.getString("cid"))) {
				//Duplicate CID
				Logger.log(Level.WARN, "Duplicate CID \"" + userinfo.getString("cid") + "\"");
			}
			//Save the info
			uidmap.put(userinfo.get("uid") + "", user);
			cidmap.put(userinfo.get("cid") + "", user);
		}
		//Notify
		Logger.log(Level.OKAY, "User list ready!");
	}
	
	//Trigger sign-in
	public static void trigger(String id, TriggerSource source) {
		//Freeze
		Display.mode = LCDMode.LOADING;
		//Get the user
		User user = getuser(id, source);
		//Check if user exists
		if(user == null) {
			//Notify
			Logger.log(Level.WARN, "Invalid ID \"" + id + "\" from " + source.name());
			//Clear the first line of the display
			Display.clearl1();
			//Print the error message
			Display.lcd.print("   INVALID ID");
			//Set the reset clock
			Reset.time = System.currentTimeMillis() + 1500;
			//Alert tone
			Buzzer.beep(500);
		} else {
			//Trigger the event
			sendTrigger(id, source);
			//Get the users current status
			boolean in = user.data.getString("status").equals("in");
			//Notify
			Logger.log(Level.INFO, user.data.getString("fname") + " " + user.data.getString("lname") +
					" has signed " + (in ? "in" : "out"));
			//Display name
			Display.clearl1();
			//Print the name
			Display.lcd.print((in ? "Hello " : "Goodbye ") + user.data.getString("fname"));
			//Set the reset clock
			Reset.time = System.currentTimeMillis() + 1500;
			//Toggle user status
			user.data.put("status", user.data.get("status").equals("in") ? "out" : "in");
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
	public static void sendTrigger(String id, TriggerSource ts) {
		//Trigger in another thread
		new Thread() {
			@Override
			public void run() {
				//Append the arguments
				String url = SignIn.baseURL + "?cmd=trigger&id=" + id;
				//Get user
				User user = Users.getuser(id, ts);
				//Request
				try {
					//Get the response
					String resp = UrlFetcher.fetch(url);
					//Parse the response
					JSONObject json = new JSONObject(resp);
					//Check detail
					user.data.put("status", json.getString("detail").equals("opened") ? "in" : "out");
				} catch (IOException e) {
					//Notify
					Logger.log(Level.WARN, "Failed to trigger user: " + e.getMessage());
					//Try to save to record
					try {
						//Save entry to file
						File record = new File("offline_record.txt");
						//Open in write mode
						PrintWriter out = new PrintWriter(new FileWriter(record, true));
						//Append
						out.println(System.currentTimeMillis() + ", " + user.data.getString("fname") +
								user.data.getString("lname"));
						//Flush and close
						out.flush();
						out.close();
					} catch(Exception ex) {
						//Notify
						Logger.log(Level.ERROR, "Failed to save entry to offline record!");
					}
				}
			}
		}.start();
	}
}
