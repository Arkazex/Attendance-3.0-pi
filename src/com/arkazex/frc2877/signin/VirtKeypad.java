package com.arkazex.frc2877.signin;

public class VirtKeypad {

	//Client writer
	/*private static OutputStream vkout;
	private static InputStream vkin;
	private static Socket vkclient;
	private static boolean busy = false;
	private static boolean connected = false;
	//Socket
	private static SSLServerSocket server;
	*/
	//Initialization method
	public static void init() {
		//Notify
		Logger.log(Level.INFO, "Skipping virtual keypad server...");
		/*
		System.out.println("  Initializing virtual keypad server...");
		System.out.print("    Starting server...");
		//Start server
		startServer();
		//Notify
		System.out.println(Color.GREEN + " Done." + Color.RESET);
		System.out.println("  Virtual keypad server initialization complete.");
		*/
	}
	
	//Check if Virtual Keypad is enabled
	public static boolean enabled() {
		return false;
	}
	/*
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
					//Loop
					while(true) {
						//Catch any errors
						try {
							//Server loop
							serverLoop();
						} catch(Exception e) {
							
							//Warning
							System.out.println(Color.YELLOW + "Notice: " +
									Color.RESET + "Virtual Keypad Socket listen error: " +
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
		if(vkout == null && !busy) {
			//Busy
			busy = true;
			//Notify
			//System.out.println("Awaiting Virtual Keypad client...");
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
				//System.out.print("Checking Virtual Keypad client status...");
				if(!connected) {
					//System.out.println(Color.YELLOW + " Connecting..." +
					//		Color.RESET);
					return;
				}
				//Send a ping to the client
				vkout.write(0x01);
				//System.out.println(Color.GREEN + " Connected!" + Color.RESET);
				Thread.sleep(1000);
			} catch(Exception e) {
				//System.out.println(Color.RED + " Not Connected!" + Color.RESET);
				//Reset variables
				vkclient = null; vkout = null; busy = false;
				connected = false; vkin = null;
				//Reset display
				Display.readyMessage = Display.defaultReadyMessage;
				Display.ready();
				//Notify
				System.out.println(Color.BLUE + "NOTICE: " + Color.RESET +
						"Virtual Keypad client has disconnected");
			}
		}
	}
	
	//Method for handling a client who just completed the handshake process
	private static void handleClient(HandshakeCompletedEvent event) {
		//Notify
		System.out.println(Color.BLUE + "NOTICE: " + Color.RESET +
				"Virtual Keypad Client connected from " + event.getSocket().toString());
		//Create output
		vkclient = event.getSocket();
		//Create input
		try {
			vkin = vkclient.getInputStream();
		} catch (IOException e1) {}
		//Display
		Display.ready();
		//Try to beep a tune
		try {
			Buzzer.beep(); Thread.sleep(20); Buzzer.beep();
		} catch(Exception e) {}
		//Try to get the output stream
		try {
			vkout = vkclient.getOutputStream();
		} catch (Exception e) {
			//Something went wrong
			System.out.println("Virtual Keypad client exception: " + e.getMessage());
		}
		//Connected
		connected = true;
		//Loop
		loop();
	}
	
	//Method for reading commands
	private static void loop() {
		//Loop
		while(vkin != null) {
			//Read
			try {
				int b = vkin.read();
				//Process
				switch(b) {
					case(0x10): Keypad.handlePress("0"); break;
					case(0x11): Keypad.handlePress("1"); break;
					case(0x12): Keypad.handlePress("2"); break;
					case(0x13): Keypad.handlePress("3"); break;
					case(0x14): Keypad.handlePress("4"); break;
					case(0x15): Keypad.handlePress("5"); break;
					case(0x16): Keypad.handlePress("6"); break;
					case(0x17): Keypad.handlePress("7"); break;
					case(0x18): Keypad.handlePress("8"); break;
					case(0x19): Keypad.handlePress("9"); break;
					case(0x1A): Keypad.handlePress("*"); break;
					case(0x1B): Keypad.handlePress("#"); break;
					case(0x01): break; //Ping
				}
			} catch(Exception e) {
				//Disconnected
				System.out.println("Virtual Keypad client error " + e.getMessage());
			}
		}
		//End
		System.out.println("VK client loop terminated");
	}
	
	//Method for creating a socket
	private static SSLServerSocket createSecureSocket() throws Exception {
		//Generate a generic server
		SSLServerSocket server = (SSLServerSocket)
				SSLServerSocketFactory.getDefault().createServerSocket(7782);
		//Enable ciphers
		server.setEnabledCipherSuites(server.getSupportedCipherSuites());
		//Return the socket
		return server;
	}
	*/
}
