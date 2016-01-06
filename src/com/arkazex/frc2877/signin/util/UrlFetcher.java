package com.arkazex.frc2877.signin.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UrlFetcher {
	
	//User agent
	private static String USER_AGENT = "SignIn Computer";
	private static int BUFFER_SIZE = 256;

	public static String fetch(String url) throws IOException {
		//Create the URL object
		URL address = new URL(url);
		//Create the connection
		HttpURLConnection con = (HttpURLConnection) address.openConnection();
		//Set the user agent
		con.setRequestProperty("User-Agent", USER_AGENT);
		//Send the request
		con.getResponseCode();
		//Read the data
		InputStream in = con.getInputStream();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[BUFFER_SIZE];
		int bcount = 0;
		while((bcount = in.read(buffer)) > 0) {
			out.write(buffer, 0, bcount);
		}
		//Return the data
		return new String(out.toByteArray());
	}
}
