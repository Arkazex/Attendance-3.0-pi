package com.arkazex.frc2877.signin;

import com.arkazex.frc2877.signin.util.Color;

//Custom levels for logging because I don't like the existing ones
public enum Level {

	//Levels
	DEBUG(0,Color.WHITE),	//Test messages
	INFO(1,Color.BLUE),		//Detail messages
	WARN(2,Color.YELLOW),	//Warnings (nonfatal)
	ERROR(3,Color.RED),		//Errors (fatal)
	OKAY(4,Color.GREEN);	//Something good happened
	
	//Level constants
	public final int value;		//Value
	public final String color;	//ANSI escape
	
	//Constructor
	Level(int value, String color) {
		//Save values
		this.value = value;  this.color = color;
	}
	
	//Method for getting the name
	public String getName() {
		//Return the name
		return color + name() + Color.RESET;
	}
}
