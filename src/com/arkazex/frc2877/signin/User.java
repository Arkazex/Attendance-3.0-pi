package com.arkazex.frc2877.signin;

import org.json.JSONObject;

public class User {

	//Data
	public JSONObject data;
	
	//User object
	public User(JSONObject data) {
		//Save the data
		this.data = data;
	}
}
