package com.arkazex.frc2877.signin;

import java.io.OutputStream;
import java.net.Socket;

import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import com.arkazex.frc2877.signin.util.Color;

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
		System.out.println("  Initializing HID server...");
		System.out.print("    Starting server...");
		//Start server
		startServer();
		//Notify
		System.out.println(Color.GREEN + " Done." + Color.RESET);
		System.out.println("  HID server initialization complete.");
	}
	
	//Send a message to connected HID device
	public static void hidSend(String message) {
		//Catch any errors
		try {
			//Check if connected
			if(hidOut == null) {
				System.out.println("HID client not connected");
				return;
			}
			//Write
			hidOut.write(0x02);
			hidOut.write(hexStringToByteArray(message));
		} catch(Exception e) {
			//Failure
			System.out.println("Fatal HID error: " + e.getMessage());
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
							System.out.println(Color.YELLOW + "Notice: " +
									Color.RESET + "HID Socket listen error: " +
									e.getMessage() + " (Nonfatal)");
						}
					}
				}
			}.start();
		} catch(Exception e) {
			System.out.print(Color.RED + " ERROR" + Color.RESET);
			System.out.println(e.getMessage());
			e.printStackTrace();
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
				System.out.println(Color.BLUE + "NOTICE: " + Color.RESET +
						"HID client has disconnected");
			}
		}
	}
	
	//Method for handling a client who just completed the handshake process
	private static void handleClient(HandshakeCompletedEvent event) {
		//Notify
		System.out.println(Color.BLUE + "NOTICE: " + Color.RESET +
				"HID Client connected from " + event.getSocket().toString());
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
			System.out.println("HID client exception: " + e.getMessage());
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
		
		/*//Key password and file
		char[] keyPass = "L1g3rb0ts2877".toCharArray();
		InputStream keyStream = HID.class.getResourceAsStream(
				"/com/arkazex/frc2877/signin/ssl.key");
		
		//Load the key store
		KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
		keyStore.load(keyStream, keyPass);
		//Initialize the key manager factory
		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(
				KeyManagerFactory.getDefaultAlgorithm());
		keyManagerFactory.init(keyStore, keyPass);
		//Get the key managers
		KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();
		//Initialize the SSL context
		SSLContext sslContext = SSLContext.getDefault();
		sslContext.init(keyManagers, null, new SecureRandom());
		//Get the socket factory
		SSLServerSocketFactory factory = sslContext.getServerSocketFactory();
		
		//Get the socket
		return (SSLServerSocket) factory.createServerSocket(2877); */
	}
	
	//Converts a string to bytes
	//From http://stackoverflow.com/a/140861
	private static byte[] hexStringToByteArray(String s) {
		System.out.println("Sending code: " + s);
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
}
