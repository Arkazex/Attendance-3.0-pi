package com.arkazex.frc2877.signin.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FileWriter {

	public static void writeFile(File file, String data) throws IOException {
		//Create the writer
		OutputStream out = new FileOutputStream(file);
		//Write the data
		out.write(data.getBytes());
		//Close the writer
		out.flush();
		out.close();
	}
}
