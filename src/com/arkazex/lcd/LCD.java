package com.arkazex.lcd;

import java.io.IOException;
import java.util.HashMap;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

public class LCD {
	
	//Ready
	private boolean ready = true;

	//Device
	private I2CDevice device;
	
	//Variables
	public boolean bl = true;
	public boolean rw = false;
	public boolean rs = false;
	
	//Characters
	private static HashMap<Character, String[]> cmap = new HashMap<Character, String[]>();
	
	//Constructor
	public LCD(int address) throws IOException {
		//Get the device
		I2CBus i2cBus = I2CFactory.getInstance(1);
		device = i2cBus.getDevice(address);
	}
	
	//Initialize method
	public void init() {
		//Check ready
		while(!ready){try{Thread.sleep(1);}catch(InterruptedException e){}}
		//Not ready
		ready = false;
		
		//Set the modes
		rw = false;
		rs = false;
		
		//Wait for startup
		delay(20);
		//Write the initialization data
		write("0011");
		delay(5);
		write("0011");
		delay(5);
		write("0011");
		delay(5);
		write("0010");
		
		//Wait for that to sink in
		delay(50);
		//Write the setup data
		write("0010");
		write("1000");  //Set the number of lines, and display parameters
		delay(5);
		write("0000");
		write("1000");  //Display off, cursor off, blink off
		delay(5);
		write("0000");
		write("0001");  //Clear screen and return the cursor to home
		delay(5);
		write("0000");
		write("0110");  //Increment cursor to the right, don't shift screen
		delay(5);
		write("0000");
		write("1100");  //Turn the display back on
		delay(5);
		
		//Display should be ready
		ready = true;
	}
	
	//Delete method
	public void clear() {
		//Check ready
		while(!ready){try{Thread.sleep(1);}catch(InterruptedException e){}}
		//Not ready
		ready = false;
		//Set the mode to command
		rs = false;
		//Write the command
		write("0000");
		write("0001");
		delay(1);
		//Ready now
		ready = true;
	}
	
	//Home method
	public void home() {
		//Check ready
		while(!ready){try{Thread.sleep(1);}catch(InterruptedException e){}}
		//Not ready
		ready = false;
		//Set the mode to command
		rs = false;
		//Write the command
		write("0000");
		write("0010");
		delay(1);
		//Ready now
		ready = true;
	}
	
	//Go to position
	public void position(int row, int col) {
		//Check ready
		while(!ready){try{Thread.sleep(1);}catch(InterruptedException e){}}
		//Not ready
		ready = false;
		//Set the mode to command
		rs = false;
		//Row offsets
		int[] roffsets = {0, 64};
		//Calculate the DRAM address
		int addr = roffsets[row] + col;
		//Convert to binary
		String rbin = Integer.toBinaryString(addr);
		//Pad the string
		String address = String.format("%07d", Integer.parseInt(rbin));
		//Write the data
		write("1" + address.substring(0, 3));
		write(address.substring(3));
		delay(1);
		//Ready now
		ready = true;
	}
	
	//Write text method
	public void print(String message) {
		//Check ready
		while(!ready){try{Thread.sleep(1);}catch(InterruptedException e){}}
		//Not ready
		ready = false;
		//Set to data mode
		rs = true;
		//Get characters
		char[] characters = message.toCharArray();
		//Print each character
		for(char c : characters) {
			//Get the binary data
			String[] bdata = cmap.get(c);
			//Check if specified
			if(bdata == null) {
				//Invalid character
				System.out.println("Warning: Character \"" + c + "\" can not be displayed");
				continue;
			}
			//Write the two nibbles
			write(bdata[0]);
			write(bdata[1]);
			delay(1);
		}
		//Delay
		delay(1);
		//Ready again
		ready = true;
	}
	
	//Is ready method
	public boolean isReady() {
		return ready;
	}
	
	//Write method
	private void write(String fourbits) {
		
		//Write first
		writeb(fourbits + ((bl) ? 1 : 0) + "0" + ((rw) ? 1 : 0) + ((rs) ? 1 : 0));
		//Write with enable pin high
		writeb(fourbits + ((bl) ? 1 : 0) + "1" + ((rw) ? 1 : 0) + ((rs) ? 1 : 0));
		//Write with enable pin low
		writeb(fourbits + ((bl) ? 1 : 0) + "0" + ((rw) ? 1 : 0) + ((rs) ? 1 : 0));
		
	}
	
	//Write binary method
	private void writeb(String eightbits) {
		//Calculate the byte
		byte value = (byte) Integer.parseInt(eightbits, 2);
		//Write
		try {
			device.write(value);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	//Delay method
	private void delay(int length) {
		try { Thread.sleep(length); } catch(Exception e) {}
	}
	
	//Populate the character map
	static {
		cmap.put(' ', new String[]{"0010","0000"});
		cmap.put('!', new String[]{"0010","0001"});
		cmap.put('"', new String[]{"0010","0010"});
		cmap.put('#', new String[]{"0010","0011"});
		cmap.put('$', new String[]{"0010","0100"});
		cmap.put('%', new String[]{"0010","0101"});
		cmap.put('&', new String[]{"0010","0110"});
		cmap.put('\'', new String[]{"0010","0111"});
		cmap.put('(', new String[]{"0010","1000"});
		cmap.put(')', new String[]{"0010","1001"});
		cmap.put('*', new String[]{"0010","1010"});
		cmap.put('+', new String[]{"0010","1011"});
		cmap.put(',', new String[]{"0010","1100"});
		cmap.put('-', new String[]{"0010","1101"});
		cmap.put('.', new String[]{"0010","1110"});
		cmap.put('/', new String[]{"0010","1111"});
		
		cmap.put('0', new String[]{"0011","0000"});
		cmap.put('1', new String[]{"0011","0001"});
		cmap.put('2', new String[]{"0011","0010"});
		cmap.put('3', new String[]{"0011","0011"});
		cmap.put('4', new String[]{"0011","0100"});
		cmap.put('5', new String[]{"0011","0101"});
		cmap.put('6', new String[]{"0011","0110"});
		cmap.put('7', new String[]{"0011","0111"});
		cmap.put('8', new String[]{"0011","1000"});
		cmap.put('9', new String[]{"0011","1001"});
		cmap.put(':', new String[]{"0011","1010"});
		cmap.put(';', new String[]{"0011","1011"});
		cmap.put('<', new String[]{"0011","1100"});
		cmap.put('=', new String[]{"0011","1101"});
		cmap.put('>', new String[]{"0011","1110"});
		cmap.put('?', new String[]{"0011","1111"});
		
		cmap.put('@', new String[]{"0100","0000"});
		cmap.put('A', new String[]{"0100","0001"});
		cmap.put('B', new String[]{"0100","0010"});
		cmap.put('C', new String[]{"0100","0011"});
		cmap.put('D', new String[]{"0100","0100"});
		cmap.put('E', new String[]{"0100","0101"});
		cmap.put('F', new String[]{"0100","0110"});
		cmap.put('G', new String[]{"0100","0111"});
		cmap.put('H', new String[]{"0100","1000"});
		cmap.put('I', new String[]{"0100","1001"});
		cmap.put('J', new String[]{"0100","1010"});
		cmap.put('K', new String[]{"0100","1011"});
		cmap.put('L', new String[]{"0100","1100"});
		cmap.put('M', new String[]{"0100","1101"});
		cmap.put('N', new String[]{"0100","1110"});
		cmap.put('O', new String[]{"0100","1111"});
		
		cmap.put('P', new String[]{"0101","0000"});
		cmap.put('Q', new String[]{"0101","0001"});
		cmap.put('R', new String[]{"0101","0010"});
		cmap.put('S', new String[]{"0101","0011"});
		cmap.put('T', new String[]{"0101","0100"});
		cmap.put('U', new String[]{"0101","0101"});
		cmap.put('V', new String[]{"0101","0110"});
		cmap.put('W', new String[]{"0101","0111"});
		cmap.put('X', new String[]{"0101","1000"});
		cmap.put('Y', new String[]{"0101","1001"});
		cmap.put('Z', new String[]{"0101","1010"});
		cmap.put('[', new String[]{"0101","1011"});
		cmap.put('¥', new String[]{"0101","1100"});
		cmap.put(']', new String[]{"0101","1101"});
		cmap.put('^', new String[]{"0101","1110"});
		cmap.put('_', new String[]{"0101","1111"});
		
		cmap.put('`', new String[]{"0110","0000"});
		cmap.put('a', new String[]{"0110","0001"});
		cmap.put('b', new String[]{"0110","0010"});
		cmap.put('c', new String[]{"0110","0011"});
		cmap.put('d', new String[]{"0110","0100"});
		cmap.put('e', new String[]{"0110","0101"});
		cmap.put('f', new String[]{"0110","0110"});
		cmap.put('g', new String[]{"0110","0111"});
		cmap.put('h', new String[]{"0110","1000"});
		cmap.put('i', new String[]{"0110","1001"});
		cmap.put('j', new String[]{"0110","1010"});
		cmap.put('k', new String[]{"0110","1011"});
		cmap.put('l', new String[]{"0110","1100"});
		cmap.put('m', new String[]{"0110","1101"});
		cmap.put('n', new String[]{"0110","1110"});
		cmap.put('o', new String[]{"0110","1111"});
		
		cmap.put('p', new String[]{"0111","0000"});
		cmap.put('q', new String[]{"0111","0001"});
		cmap.put('r', new String[]{"0111","0010"});
		cmap.put('s', new String[]{"0111","0011"});
		cmap.put('t', new String[]{"0111","0100"});
		cmap.put('u', new String[]{"0111","0101"});
		cmap.put('v', new String[]{"0111","0110"});
		cmap.put('w', new String[]{"0111","0111"});
		cmap.put('x', new String[]{"0111","1000"});
		cmap.put('y', new String[]{"0111","1001"});
		cmap.put('z', new String[]{"0111","1010"});
		cmap.put('{', new String[]{"0111","1011"});
		cmap.put('|', new String[]{"0111","1100"});
		cmap.put('}', new String[]{"0111","1101"});
		//cmap.put('', new String[]{"0111","1110"}); //Arrows removed due to document encoding issue
		//cmap.put('', new String[]{"0111","1111"});
	}
}
