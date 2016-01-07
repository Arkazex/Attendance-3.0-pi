package com.arkazex.frc2877.signin;

import java.io.OutputStream;
import java.net.Socket;

import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

public class HID {

	//Client writer
	private static OutputStream hidOut;
	private static Socket hidClient;
	private static boolean busy = false;
	private static boolean connected = false;
	//Socket
	private static SSLServerSocket server;
	
	//Initialization method
	public static void init() {
		//Notify
		Logger.log(Level.INFO, "Initializing HID server...");
		//Start server
		startServer();
		//Notify
		Logger.log(Level.OKAY, "HID server ready!");
	}
	
	//Send a message to connected HID device
	public static void hidSend(String message) {
		//Catch any errors
		try {
			//Check if connected
			if(hidOut == null) {
				//No client connected D:
				Logger.log(Level.WARN, "HID invoked, no client connected");
				return;
			}
			//Write
			hidOut.write(0x02);
			hidOut.write(hexStringToByteArray(message));
		} catch(Exception e) {
			//Failure
			Logger.log(Level.WARN, "HID error: " + e.getMessage());
		}
	}
	
	//Check if HID is enabled
	public static boolean enabled() {
		return hidOut != null;
	}
	
	//Starts the server
	private static void startServer() {
		//Catch any errors that occur here
		try {
			//Create a server socket
			server = createSecureSocket();
			//Create the listener thread
			new Thread() {
				@Override
				public void run() {
					//Delay start
					try { Thread.sleep(2000); } catch(Exception e) {}
					//Loop
					while(true) {
						//Catch any errors
						try {
							//Server loop
							serverLoop();
						} catch(Exception e) {
							//Warning
							Logger.log(Level.WARN, "HID socket listen error: " + 
									e.getMessage());
						}
					}
				}
			}.start();
		} catch(Exception e) {
			//Startup error
			Logger.log(Level.ERROR, "Failed to start HID module: " + e.getMessage());
			Logger.handleCrash(e);
			Logger.log(Level.INFO, "Application will continue...");
		}
	}
	
	//Server loop
	private static void serverLoop() throws Exception{
		//Check if client is connected
		if(hidOut == null && !busy) {
			//Busy
			busy = true;
			//Notify
			//System.out.println("Awaiting HID client...");
			//No client is connected - accept connection
			SSLSocket client = (SSLSocket) server.accept();
			//Require auth
			//client.setNeedClientAuth(true);
			//Enable ciphers
			client.setEnabledCipherSuites(client.getEnabledCipherSuites());
			//Add a handshake listener
			client.addHandshakeCompletedListener(new HandshakeCompletedListener() {
				@Override
				public void handshakeCompleted(HandshakeCompletedEvent event) {
					handleClient(event);
				}
			});
			//Start handshake
			client.startHandshake();
		} else {
			//Check client status
			try {
				//System.out.print("Checking HID client status...");
				if(!connected) {
					//System.out.println(Color.YELLOW + " Connecting..." +
					//		Color.RESET);
					return;
				}
				//Send a ping to the client
				hidOut.write(0x01);
				//System.out.println(Color.GREEN + " Connected!" + Color.RESET);
				Thread.sleep(1000);
			} catch(Exception e) {
				//System.out.println(Color.RED + " Not Connected!" + Color.RESET);
				//Reset variables
				hidClient = null; hidOut = null; busy = false;
				connected = false;
				//Reset display
				Display.readyMessage = Display.defaultReadyMessage;
				Display.ready();
				//Notify
				Logger.log(Level.INFO, "HID client has disconnected");
			}
		}
	}
	
	//Method for handling a client who just completed the handshake process
	private static void handleClient(HandshakeCompletedEvent event) {
		//Notify
		Logger.log(Level.INFO, "HID client connected from " + 
				event.getSocket().getInetAddress().toString() + ":" +
				event.getSocket().getPort());
		//Create output
		hidClient = event.getSocket();
		//Display
		Display.readyMessage = "HID Mode Active";
		Display.ready();
		//Try to beep a tune
		try {
			Buzzer.beep(); Thread.sleep(20); Buzzer.beep();
		} catch(Exception e) {}
		//Try to get the output stream
		try {
			hidOut = hidClient.getOutputStream();
		} catch (Exception e) {
			//Something went wrong
			Logger.log(Level.WARN, "HID client error: " + e.getMessage());
		}
		//Connected
		connected = true;
	}
	
	//Method for creating a socket
	private static SSLServerSocket createSecureSocket() throws Exception {
		//Generate a generic server
		SSLServerSocket server = (SSLServerSocket)
				SSLServerSocketFactory.getDefault().createServerSocket(2877);
		//Enable ciphers
		server.setEnabledCipherSuites(server.getSupportedCipherSuites());
		//Return the socket
		return server;
	}
	
	//Converts a string to bytes
	//From http://stackoverflow.com/a/140861
	private static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
}
