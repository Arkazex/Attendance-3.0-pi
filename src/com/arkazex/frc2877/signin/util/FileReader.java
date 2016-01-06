package com.arkazex.frc2877.signin.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileReader {

	//Buffer size
	private static int BUFFER_SIZE = 128;
	
	public static String readFile(File file) throws IOException {
		//Create the input stream
		InputStream in = new FileInputStream(file);
		//Create the output stream
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		//Create the buffer
		byte[] buffer = new byte[BUFFER_SIZE];
		//Create the read counter
		int rcount = 0;
		//Read
		while((rcount = in.read(buffer)) > 0) {
			out.write(buffer, 0, rcount);
		}
		//Close the reader
		in.close();
		//Return the file
		return new String(out.toByteArray());
	}
}
