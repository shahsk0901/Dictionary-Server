/**
 * @author Sunny Kamleshbhai Shah
 * Student ID: 1001358145
 */ 

package lab1;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LexiconServer extends Application {
	
	//JavaFX GUI primary stage, where the layout will be loaded
	private Stage primaryStage;
	// JavaFX root layout, where the FXML is binded to.
	private GridPane rootLayout;
	// JavaFX FXML that will act as the loader, which sets controlling class for FXML
	// and tells JVM where to find FXML.
	private FXMLLoader loader;
	
	
	/* @FXML are the variable defined in FXML file, with the exact
	 * same name as stated below, act act as a variable binding between 
	 * FXML and  java class LexiconServer
	 */
	
	// Starts the server, when clicked
	@FXML
	Button startServer;

	// Shuts the server down, when clicked
	@FXML
	Button killServer;
	
	// Quits the application when clicked
	@FXML
	Button quit;
	
	// Output area that will display incoming client requests.
	@FXML
	public Label serverOutput;
	
	// This is a static variable that stores the port number of the server to be created.
	private static final int PORT = 7478;
	
	// Initializes the client id, and keep a count of connections created in a particular 
	// session.
	private static int clientID = 1;
	
	// Socket variable that is used to create server connection and listen for client and accept them
	private ServerSocket server;
	
	// Flag to keep track of socket connections, and are set/reset according to the connection status
	public static Boolean serverFlag = false;
	
	// Setter method to set content to output area on server GUI, in a non-controller class
	public void setLabel(String message) {
		serverOutput.setText(message);
	}

	// Getter method to get access to output area on server GUI, in a non-controller class
	public String getLabel() {
		return this.serverOutput.getText();
	}
	
	/* This method does the main work of starting the server.
	 * If the server is alive, it will display the connection message and wait
	 * for clients to join and then send them initial greeting or instructions
	 * Whereas, if the server start fails, it will display the corresponding message
	 * and shutdown/quit the client application.
	 * 
	 * This method gets invoked automatically in a new thread, by the 
	 * startServerListener() function
	 */
	public void startServer() {
		
		System.out.println("Attempting to start server on port: " + PORT);
		
		/* This is the basic method that is used to call the JavaFX Application Thread.
		 * Any updates to the GUI should be done within this thread. Or, there are chances
		 * that GUI goes into the blocking state and freezes up.
		 * 
		 * You will find these method calls, quite often in the program and their sole purpose
		 * is as mentioned above.
		 */
		Platform.runLater(new Runnable() {	
			@Override
			public void run() {
				serverOutput.setWrapText(true);
				serverOutput.setText("Attempting to start server on port: " + PORT);
			}
		});
		
		
		/*
		 * This try-catch block tries to start the server on
		 * the port mentioned. Will throw an exception if server is already
		 * running and take the actions accordingly
		 */
		try {
			
			// command that will initiate the server creation.
			// and sets the flag to true if server is created
			server = new ServerSocket(PORT);
			serverFlag = true;
			
			/* This and all other similar try-catch block is used to delay the execution of the
			 * program. This is done so as to keep the GUI updates in synchronization
			 * with the server and client responses.
			 * 
			 * Same goes for these as above. You will find these quite often in the program 
			 * and their sole purpose is as stated above.
			 */
			try {
				TimeUnit.SECONDS.sleep(2);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
			
			System.out.println("Server Started: " + server);
			System.out.println("Waiting for connection......");
			
			// Display messages on the GUI, just as above, that prints on the console
			Platform.runLater(new Runnable() {	
				@Override
				public void run() {
					killServer.setDisable(false);
					serverOutput.setText(serverOutput.getText() + "\nServer Started: " + server);
					serverOutput.setText(serverOutput.getText() + "\nWaiting for connection......");	
				}
			});
				
			// resets the client id count, every time a new server connection is started
			clientID = 1;
			
			while(serverFlag.equals(true)) {
				// creates a new client process on a separate thread and starts the thread as soon as the
				// connection to the client is accepted by the server
				try {
					// this is a blocking operation, where server will always listen for new connections
					// until the socket is open
					// Creates a new class object and starts it on it's own different thread
					new ClientProcessThread(server.accept(),clientID,(LexiconServer) loader.getController()).start();
				} catch(IOException e) {
					// Exception thrown in the case, when server can no longer accept new connections
					// and resets the server flag accordingly
					System.out.println("Server Socket Closed Exception! Cannot accept new connections");
					serverFlag = false;
				}
				// increment the client count, every time a new connection is accepted by server
				clientID++;
			}
			/* The catch block below is executed in the case when
			 * there is an exception, while starting the server, i.e. when the 
			 * server is already running and a new server creation request
			 * occurs, address busy exception will be raised
			 * 
			 */
		} catch(IOException e) {
				
			// Displays the exception message on the server GUI and then 
			// update the server flag accordingly
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					serverOutput.setAlignment(Pos.CENTER);
					serverOutput.setText("\nUnable to start server!" + "\nPort " + PORT + ": Address Busy..");
				}
			});
			System.out.println("\nUnable to start server!");
			System.out.println("Port " + PORT + ": Address Busy..");
			serverFlag = false;
			//e.printStackTrace();
		}
	}
	
	/* This listener is binded to the start server button in the FXML itself.
	 * So, as soon as start server button is clicked this method will be invoked and
	 * will simply in sequence initiate a thread to start to the server.
	 */
	public void startServerListener() {
		
		// Disables to start server button, so as to prevent,
		// from sending multiple start requests, when process is
		// still executing
		startServer.setDisable(true);
		
		/* Java in-built implementation interface, which implements the run method and
		 * starts the startServer() function on a new thread when invoked.
		 */
		Runnable task = new Runnable() {
			@Override
			public void run() {
				startServer();
			}
		};
		
		// Thread that is used to invoke the Runnable implementation
		// and start the thread
		Thread startServer = new Thread(task);
		startServer.setDaemon(false);
		startServer.start();
	}
	
	/* This method does the main work of shutting the server.
	 * Doing so, will not quit the application. Application will still be
	 * running and give a chance to start a new server instance.
	 * 
	 * This method gets invoked automatically in a new thread, by the 
	 * killServerListener() function
	 */
	public void killServer() {
		try {
			// closes the server and resets the serverFlag
			server.close();
			serverFlag = false;
			
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch(InterruptedException ie) {
				ie.printStackTrace();
			}
			
			System.out.println("\n\nServer killed! Press 'Start Server' to fire up the server");
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					serverOutput.setText("Server killed! Press 'Start Server' to fire up the server");
					// enables the start server button
					startServer.setDisable(false);
				}
			});
			
			/* Exception thrown in the event that the server was not able to
			 * successfully close or shutdown
			 */
		} catch(IOException e) {
			e.printStackTrace();
			
			serverFlag = true;
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					// enable the kill server button
					killServer.setDisable(false);
				}
			});
			
		}
	}
	
	
	/* This listener is binded to the kill server button in the FXML itself.
	 * So, as soon as kill server button is clicked this method will be invoked and
	 * will simply in sequence initiate a function to kill to the server.
	 */
	public void killServerListener() {
		
		// Disables to kill server button, so as to prevent,
		// from sending multiple kill requests, when process is
		// still executing
		killServer.setDisable(true);
		
		/* Java in-built implementation interface, which implements the run method and
		 * starts the killServer() function on a new thread when invoked.
		 */
		Runnable task = new Runnable() {
			@Override
			public void run() {
				killServer();
			}
		};
		
		// Thread that is used to invoke the Runnable implementation
		// and start the thread
		Thread killServer = new Thread(task);
		killServer.setDaemon(false);
		killServer.start();
	}
	
	/*
	 * It is invoked by quitListener.
	 * This function will simply quit the application.
	 * It will first check if server is running or not.
	 * If running, it will close the server properly and exit.
	 * if not running, will just display exit message and close the
	 * application
	 */
	public void quit() {
		serverOutput.setAlignment(Pos.CENTER);

		// will initiate shutdown, only if the server flag is set
		// and take action to kill server and proceed with 
		// quitting the application
		if(serverFlag.equals(true)) {
			
			System.out.println("Server Shutdown: Initiated!");
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					serverOutput.setText("Server Shutdown: Initiated!");
				}
			});
			
			try {
				// command which actually closes the server
				server.close();
				
				try {
					TimeUnit.SECONDS.sleep(1);
				} catch(InterruptedException ie) {
					ie.printStackTrace();
				}
				
				System.out.println("Server Shutdown: Successful!");
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						serverOutput.setText("Server Shutdown: Successful");
					}
				});
				
				try {
					TimeUnit.SECONDS.sleep(1);
				} catch(InterruptedException ie) {
					ie.printStackTrace();
				}
			/*
			 * Exception will be raised, when there's an exception in
			 * closing the server.
			 */
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		System.out.println("Closing application!");
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				serverOutput.setText("Closing application!\n       GOODBYE");
			}
		});
	
		try {
			TimeUnit.SECONDS.sleep(2);
		} catch(InterruptedException ie) {
			ie.printStackTrace();
		}
		
		// command that actually terminates and closes the application
		System.exit(0);
	}
	
	/* This listener is binded to the quit button in the FXML itself.
	 * So, as soon as quit button is clicked this method will be invoked and
	 * will simply in sequence initiate a function to quit application.
	 */
	public void quitListener() {

		/* Java in-built implementation interface, which implements the run method and
		 * starts the killServer() function on a new thread when invoked.
		 */
		Runnable task = new Runnable() {
			@Override
			public void run() {
				quit();
			}
		};
		
		// Thread that is used to invoke the Runnable implementation
		// and start the thread
		Thread quit = new Thread(task);
		quit.setDaemon(false);
		quit.start();
	}
	
	/* (non-Javadoc) @see javafx.application.Application#start(javafx.stage.Stage)
	 * 
	 *  JavaFX implementation method that sets and initializes the primary stage,
	 *  that is to be displayed when the application starts up 
	 */
	@Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Server Process");

        initRootLayout();
    }
	    
	/* Invoked by the start method of the JavaFX process.
	 * Loads the FXML file and sets the LexiconServer class as it's 
	 * controller.
	 * Initializes the rootLayout.
	 * Loads the rootLayout on to the primary stage
	 * and displays that scene on the application
	 */
	public void initRootLayout() {
        try {
			// FXML loader variable, that will be used to load GUI elements
            loader = new FXMLLoader();
			// Sets the location, where to find the Layout file for GUI
            loader.setLocation(LexiconServer.class.getResource("ServerView.fxml"));
			// Sets the LexiconClient class as the GUI Controller
            loader.setController(this);
			// Initializes the root layout, as the default layout variable
			// where the layout is actually binded, to be accessed programmatically.
            rootLayout = (GridPane) loader.load();
        	
            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
            
            // Initial state of application. 
            // kill server button, is disabled. So, the flow of
            // of application is never disrupted, and always performs as expected
            killServer.setDisable(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	// Return the primary stage of the application, that is running the GUI and controls it
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
	/* Launches the main application with the launch command, 
	 * when the program starts running
	 */
    public static void main(String[] args) {
        launch(args);
    }
}
//--------------------------------------------------END OF CONTROLLER CLASS------------------------------------------------------------>

//---------------------------------------------------CLIENT THREAD CLASS-------------------------------------------------------------->

/* ClientProcessThread class to facilitate the individual client 
 * requests.
 * A new thread is created every time a client is accepted by
 * the server, and runs on it's separate thread, leading to the 
 * MULTITHREADING behavior of client processes.
 */

class ClientProcessThread extends Thread {

	// Variables that gets the controller access, which will be used to print messages on GUI
	private LexiconServer ui;
	// Variable that gets the socket connection information, and bind that information to this thread
	private Socket client;
	// Variable that will issue id number to the client and send them messages on that ID
	private int clientID;
	
	// Constructor method of the class ClientProcessThread and is invoked and passed the
	// parameters in the LexiconServer class, when a new client connection is accepted
	ClientProcessThread(Socket client, int clientID,LexiconServer ui) {
		this.client = client;
		this.clientID = clientID;
		this.ui = ui;
	}
	
	/* This method checks the client input against the data present in
	 * the lexicon file and returns the message if the client input
	 * is found in the lexicon file or return error message.
	 * 
	 * Invoke parameter is the lexicon that is requested by the client
	 */ 
	public static String checkLexicon(String checkWord) {
		
		// if blank input from client, returns the very moment,
		// it gets invoked, without further checking for lexicons
		if(checkWord.length() == 0) {
			return "No input! Enter a word";
		}
		
		// container variable that will hold message and will be returned to calling function
		String returnMessage = null;
		try (
				// Opens up readers to access the file and read file contents
				// from the location specified
				FileReader fr = new FileReader(new File("src/lab1/lexicon.txt"));
				BufferedReader br = new BufferedReader(fr);
		){
			// List that is used to hold the lexicons temoratily,
			// until the response message is generated
			ArrayList<String> lexicons =  new ArrayList<String>();
			String add;
			// loop that will scan file lie-by-kine and lexicons to the array list
			while((add = br.readLine()) != null) {
				lexicons.add(add);
			}
			
			// flag variable set when the client lexicon requested
			// is found in the lexicons file
			int flag = 0;
			
			// loops through the array list to look for the 
			// requested requested
			for(int i=0;i<lexicons.size();i++) {
				if(lexicons.get(i).equals(checkWord)) {
					flag = 1;
					break;
				}
			}
			
			// if flag is set it will send the message, lexicon found
			// else will send an error message
			if(flag == 1) {
				 returnMessage = "\"<FOUND>\"";
			} else if (flag == 0) {
				returnMessage = "\"<ERROR!>\" NOT FOUND";
			}
			
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		// returns the response to the caller
		return returnMessage;
	}
	
	/* This method is invoked automatically as soon as the thread starts.
	 * It is the main process that executes methods to send responses and
	 * receives client request and maintain server connection 
	 * status with the current client
	 */
	@Override
	public void run() {
		System.out.println("\nClient " + clientID + " connected");
		// gets the name of thread, the current client is running on
		String currentThread = Thread.currentThread().getName();
		System.out.println("Running on: " + currentThread);
		
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				ui.setLabel("\nClient " + clientID + " connected");
				ui.setLabel(ui.getLabel() + "\nRunning on: " + currentThread);
			}
		});
		
		try {
			// opens up IO Streams to the client
			PrintWriter os = new PrintWriter(client.getOutputStream(),true);
			BufferedReader is = new BufferedReader(new InputStreamReader(client.getInputStream()));
			
			// sends the client ID as the first message
			os.println(clientID);
			
			// sends the remaining initial messages/instructions to the client
			// when the client connects
			os.println("To quit the program at anytime, just type 'exit' or 'quit' or Press 'Quit'");
			os.print("\n");
			os.flush();
			os.println("Your client ID: " + clientID);
			os.println("Enter the word in the 'text field' below to be searched");
			
			// block runs until the thread is interrupted and server is still running
			// or client sends the terminating triggers 'quit' or 'exit'
			// it as a blocking state loop.
			while(!Thread.currentThread().isInterrupted() && LexiconServer.serverFlag == true) {
				// this is a blocking operation and the server will wait
				// for the client request, no matter what
				String message = is.readLine();
				
				// block gets executed only if the message is not null or 'exit' or 'quit'
				if(!message.equals("exit") && !message.equals("quit") && message != null) {
					
					// will actively check for server status
					// if, server is down, will send a socketdown!
					// connection closing trigger and break the loop
					if(LexiconServer.serverFlag == false) {
						os.println("socketdown!");
						break;
					}
					
					// prints the client id and their request on the GUI and console
					System.out.println("Client " + clientID + ": " + message);
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							ui.setLabel("Incoming Request\n");
							ui.setLabel(ui.getLabel() + "Client " + clientID + ": " + message);
						}
					});
					
					// calls the lexicon checker method, which will look for the lexicon requested
					// and give a return message after parsing the file
					String result = checkLexicon(message);
					
					try {
						TimeUnit.MILLISECONDS.sleep(1200);
					} catch(Exception e) {
						e.printStackTrace();
					}
					
					// display on server GUI, what the response is being sent to the client
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							ui.setLabel(ui.getLabel() + "\n\nServer response to Client" + clientID + ": " + result);
						}
					});
					
					// sends the message to client, which will be then intercepted and read by the client
					os.println(result);
					
					/*
					 * This block gets executed in the event, the sends the message
					 * 'exit' or 'quit'
					 */
				} else {
					// sends the trigger message to the client, to start the 
					// disconnect sequence
					os.println("Exiting!");
					
					System.out.println("\nClient Exiting!");
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							ui.setLabel("\nClient Exiting!");
						}
					});
					break;
				}
			}
			
			// checks for aliveness of the client and closes the connection, only
			// if the client is still active
			if(LexiconServer.serverFlag.equals(true) && client.isConnected()) {
				
				// command that actually closes the connection to the client 
				client.close();
				
				try {
					TimeUnit.SECONDS.sleep(1);
				} catch(Exception e) {
					e.printStackTrace();
				}
				
				// Displays the client disconnection message along with the particular client, when the client exits
				System.out.println("Client " + clientID + ": Disconnected");
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						ui.setLabel(ui.getLabel() + "\nClient " + clientID + ": Disconnected");
					}
				});
			}
			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}


//---------------------------------------CODE BELOW TO BE IGNORED---------------------------------------------------------------------->

/*
 *try (
 * 		ServerSocket server = new ServerSocket(port);
		//System.out.println("Server Initiated");
		Socket client = server.accept();
		PrintWriter os = new PrintWriter(client.getOutputStream(),true);
		BufferedReader is = new BufferedReader(new InputStreamReader(client.getInputStream()));
){
	System.out.println("Server running on port: " + port + "\n\n");
	os.println("Connected to server on port: " + server.getLocalSocketAddress());
	String message;
	while(true) {
			while((message = is.readLine()) != null) {
				if(message != "") {
					System.out.println("Client: " + message);
					String result = checkLexicon(message);
					os.println(result);
				}
			}
	System.out.println("\n\tClosing connection and exiting...");
	client.close();
	break;
	}
} catch (IOException e) {
	e.printStackTrace();
	System.out.println("Server initialization error!");
	System.exit(1);
}*/

//------------------------------------------------------------------------------------------------------------------------------------->
