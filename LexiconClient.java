/**
 * @author Sunny Kamleshbhai Shah
 * Student ID: 1001358145
 */ 


package lab1;

import java.io.*;
import java.net.*;
import java.util.concurrent.TimeUnit;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

public class LexiconClient extends Application {
    
	// JavaFX GUI variable used to load the GUI on the application.
	private Stage primaryStage;
	
	// JavaFX GUI Layout (ClientGUI.fxml) is binded to this layout variable.
	private GridPane rootLayout;
	
	// This variable stores the input that client want to be searched for.
	private String input;
	
	// This is a static variable that stores the name of the server to be connected.
	private static final String host = "localhost";

	// This is a static variable that stores the port number of the server to be connected.
	private static final int port = 7478;
	
	// Socket variable that is used to establish connection to the server
	private Socket client;
	
	// Variable that will help send the messages that are entered by the client to the server.
	private PrintWriter os;
	
	// Variable that will help receive the messages that are sent by the server to the client.
	private BufferedReader serverResponse;
	
	// Variable that holds the client number of the current client sent by the server and displays
	// in the application
	private String clientNumber;
	
	// Flag to keep track of socket connections, and are set/reset according to the connection status
	private static Boolean socketFlag = false;
	
	
	//private static Boolean flag = false;
	
	// @FXML variables, are the layout variables. @FXML is used to bind the variables with the same name
	// in the layout. And the declaration shows what those variable are exactly for.
	@FXML
	Button connectHost;
	@FXML
	Button disconnectHost;
	@FXML
	Button quit;
	@FXML
	Label clientID;
	@FXML
	Label outputArea;
	@FXML
	TextField inputText;
	
	
	/* This method does the main work of connecting to the server.
	 * If the server is alive, it will display the connection message and read the
	 * initial messages from server and update the GUI with the content.
	 * Whereas, if the server is not running, it will display the corresponding message
	 * and shutdown/quit the client application.
	 * 
	 * This method gets invoked automatically in a new thread, by the 
	 * conectHostListener() function
	 */
	public void connectHost() {
		System.out.println("Attempting Connection!\nHost: '" + host + "'\nPort: " + port);
		
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
				
				// Sets the alignment of the output region to the top left
				outputArea.setAlignment(Pos.TOP_LEFT);
				
				// Sets the text of the output area as specified
				outputArea.setText("Attempting Connection!\n\nHost: '" + host + "'\nPort: " + port);
			}
		});
		
		try {
			/* Tries to create a connection to server.
			 * This is a operation which proceeds only if connection
			 * is established or throws an exception 
			 */
			client = new Socket(host,port);
			socketFlag = true;
			
			/*
			 * Block below is the catch block for the try block above
			 * and is used to display/take suitable measures when
			 * connection fails while trying to create it
			 */
		} catch(IOException e) {

			
			/* This try-catch block is used to delay the execution of the
			 * program. This is done so as to keep the GUI updates in synchronization
			 * with the server and client responses.
			 * 
			 * Same goes for these as above. You will find these quite often in the program 
			 * and their sole purpose is as stated above.
			 */
			try {
				TimeUnit.SECONDS.sleep(2);
			} catch(InterruptedException ie) {
				ie.printStackTrace();
			}
			
			System.out.println("\nHost Connection Failed!\nHost is DOWN!\n");		
			
			/* Perform the same action as before. If something different, it will be mentioned
			 * in that particular block of code.
			 * The GUI variable also perform the same functions and will be mentioned where different. 
			 */
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					outputArea.setTextFill(Paint.valueOf("#FF0000"));
					outputArea.setText("Host Connection Failed!\nHost is DOWN!");
				}
			});

			// Perform the same action as before.
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch(InterruptedException ie) {
				ie.printStackTrace();
			}
			
			/* Perform the same action as before. If something different, it will be mentioned
			 * in that particular block of code.
			 * The GUI variable also perform the same functions and will be mentioned where different. 
			 */
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					outputArea.setTextFill(Paint.valueOf("#000000"));
					outputArea.setAlignment(Pos.CENTER);
					outputArea.setText("Closing Application...");
				}
			});
			
			// Perform the same action as before.
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch(InterruptedException ie) {
				ie.printStackTrace();
			}
			
			// This command is used to terminate and close the application,
			// after all the messages have been displayed to the client.
			System.exit(0);
		}
		// END OF CATCH BLOCK--------------------------------------------------------------------------------------------------------->
		
		/* This block checks the value of socketFlag and is executed in only the case it is true.
		 * 
		 */
		if(socketFlag.equals(true)) {
			
			try {
				TimeUnit.MILLISECONDS.sleep(800);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
			
			System.out.println("\n\nHost Connection Established!\nLoading Interface......");
			/* Perform the same action as before. If something different, it will be mentioned
			 * in that particular block of code.
			 * The GUI variable also perform the same functions and will be mentioned where different. 
			 */
			Platform.runLater(new Runnable() {
					@Override
					public void run() {
						outputArea.setText(outputArea.getText() + "\n\nHost Connection Established!\n\nLoading Interface......");
					}
			});
			
			try{
				// This command opens up new input stream, which is then used to listen
				// to the responses from the server
				// throws an exception is the stream is interrupted
				serverResponse = new BufferedReader(new InputStreamReader(client.getInputStream()));
				
				// Perform the same action as before.
				try {
					TimeUnit.SECONDS.sleep(2);
				} catch(InterruptedException ie) {
					ie.printStackTrace();
				}
				
				// Reads the client number sent by the users and later updates the 
				// GUI with the same number assigned.
				clientNumber = serverResponse.readLine();
				
				// Clears and updates the GUI output area.
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						outputArea.setTextFill(Paint.valueOf("#000000"));
						outputArea.setText("");
						outputArea.setWrapText(true);
					}
				});
				
				// Stores the few initial messages in the string array msg[]
				String[] msg = new String[4];
				msg[0] = serverResponse.readLine();
				System.out.println(msg[0]);
				msg[1] = serverResponse.readLine();
				System.out.println(msg[1]);
				msg[2] = serverResponse.readLine();
				System.out.println(msg[2]);
				msg[3] = serverResponse.readLine();
				System.out.println(msg[3]);
				
				// Output the stored initial messages from the server on the GUI
				for(int i=0;i<msg.length;i++) {
					String message;
					if(i == 2) {
						message = msg[i] + "\n";
						
						// This updates the GUI title & the program header with the
						// client id assigned to the client
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								// This get the application layout control, which is used to set
								// title of the application
								Stage primary = getPrimaryStage();
								primary.setTitle("Client " + clientNumber + " Process");
								clientID.setText("Client ID: " + clientNumber);
							}
						});
					}  else {
						message = msg[i];
					}
					
					// Updates the GUI message by messages as the for loop advances
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							outputArea.setText(outputArea.getText() + message + "\n");
						}
					});
					
					// Gives the times to update the GUI synchronously, by making the
					// loop sleep for 1 sec.
					try {
						TimeUnit.SECONDS.sleep(1);
					} catch(InterruptedException ie) {
						ie.printStackTrace();
					}
				}
					
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						// Enables the disconnect host button.
						disconnectHost.setDisable(false);
						// Enables the input text field.
						inputText.setDisable(false);
					}
				});
			} catch (IOException e) {
				e.printStackTrace();	
			}
		}	
	}
	
	
	/* This listener is binded to the connect host button in the FXML itself.
	 * So, as soon as connect host button is clicked this method will be invoked and
	 * will simply in sequence initiate a thread to connect to the server.
	 */
	public void connectHostListener() {
		
		// Disables to connect host button, so as to prevent user,
		// from sending multiple connect requests, when process is
		// executing
		connectHost.setDisable(true);
		
		// Sets the text color of the output to Green
		outputArea.setTextFill(Paint.valueOf("#00FF00"));
		
		
		/* Java in-built implementation interface, which implements the run method and
		 * starts the connectHost() function on a new thread when invoked.
		 */
		Runnable task = new Runnable() {
			@Override
			public void run() {
				connectHost();
			}
		};
		
		// Thread that is used to invoke the Runnable implementation
		// and start the thread
		Thread connectHost = new Thread(task);
		connectHost.setDaemon(true);
		connectHost.start();
	}
	
	
	/* This method does the main work of disconnecting from the server.
	 * Doing so, will not quit the application. Application will still be
	 * running and give client a chance to create a new connection to server.
	 * 
	 * This method gets invoked automatically in a new thread, by the 
	 * disconectHostListener() function
	 */
	public void disconnectHost() {
		try {
			System.out.println("Disconnecting Host...!");
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					outputArea.setAlignment(Pos.CENTER);
					outputArea.setText("Disconnecting Host...!");
				}
			});
			
			// Closes the connection to the server and resets the flag
			client.close();
			socketFlag = false;	
		
			try {
				TimeUnit.SECONDS.sleep(2);
			} catch(InterruptedException ie) {
				ie.printStackTrace();
			}
			
			System.out.println("Host Disconnected!");
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					outputArea.setAlignment(Pos.CENTER);
					Stage stage = getPrimaryStage();
					// Resets the application title
					stage.setTitle("Client Process");
					// Resets the client label
					clientID.setText("");
					outputArea.setText("Host Disconnected!");
					// Disables the input text field
					inputText.setDisable(true);
					// Enables the connect host button
					connectHost.setDisable(false);
				}
			});
			/*
			 * Catch block runs in the event, when there is a exception
			 * closing the socket and will makes the following updates to the
			 * GUI.
			 */
		} catch(IOException e) {
			//e.printStackTrace();
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					outputArea.setText("Host can't be disconnected");
					// re-enables the disconnect button and the input field 
					inputText.setDisable(false);
					disconnectHost.setDisable(false);
					// disables the connection button
					connectHost.setDisable(true);
				}
			});
		}
	}
	
	/* This listener is binded to the disconnect host button in the FXML itself.
	 * So, as soon as disconnect host button is clicked this method will be invoked and
	 * will simply in sequence initiate a thread to disconnect to the server.
	 */
	public void disconnectHostListener() {
		// Disables the disconnect host button as soon as invoked.
		// So, to prevent multiples disconnect request from the client
		disconnectHost.setDisable(true);
		
		/* Java in-built implementation interface, which implements the run method and
		 * starts the disconnectHost() function on a new thread when invoked.
		 */
		Runnable task = new Runnable() {
			@Override
			public void run() {
				disconnectHost();
			}
		};
		
		// Thread that is used to invoke the Runnable implementation
		// and start the thread
		Thread disconnectHost = new Thread(task);
		disconnectHost.setDaemon(true);
		disconnectHost.start();
	}
	
	// Simply will terminate the application and QUITTTT!
	public void quitListener() {
		System.exit(0);
	}
	
	/* Sends clients messages to the server and waits 
	 * for a response from the server.
	 * If server shutdowns abnormally or normally, proper actions
	 * are taken, to maintain the consistency of the application
	 */
	public void processData() {
		
		try {
			// Opens up a output stream, that will send messages to the server
			os = new PrintWriter(client.getOutputStream(),true);
			
			// Opens up a input stream that will receive messages from the server
			serverResponse = new BufferedReader(new InputStreamReader(client.getInputStream()));
			
			/*
			 * If an exception is caught while opening I/O Streams, then the
			 * application will initiate a host disconnect procedure, which ensures
			 * proper disconnection from the host.
			 */
		} catch(IOException e) {
			e.printStackTrace();
			socketFlag = false;
			disconnectHostListener();
		}
		
		/* 
		 * Executed only when no exception is caught while opening I/O Streams and also
		 * verifies the connection again.
		 */
		if(socketFlag.equals(true) && !client.isInputShutdown() && !client.isOutputShutdown()) {
			try {
				
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						outputArea.setText("Please Wait! Procesing Data....");
					}
				});
				
				try{
					TimeUnit.SECONDS.sleep(1);
				} catch(InterruptedException ie) {
					ie.printStackTrace();
				}
				
				// Displays the client input on the screen
				System.out.println("\nYou: " + input);
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						outputArea.setText(outputArea.getText() + "\n\n\nYou:\t'" + input + "'");
					}
				});
					
					// Sends the client input to the server
					os.println(input);
					
					// This is a blocking operation and will not move forward until
					// a response from server is received
					String response = serverResponse.readLine();
					
					// Checks if the server response is socket down, if it is
					// will initiate GUI updates to connect to the server
					if(response.equals("socketdown!")) {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								// Update the GUI to show text in center with red color
								outputArea.setAlignment(Pos.CENTER);
								outputArea.setTextFill(Paint.valueOf("#FF0000"));;
								outputArea.setText("Host Connection Lost");
								// Update the application's main title
								getPrimaryStage().setTitle("Client Process");
								// clears the client ID display to blank
								clientID.setText("");
								// Clears and disables the input text field
								inputText.clear();
								inputText.setDisable(true);
								// Disables the disconnect host button
								disconnectHost.setDisable(true);
								// Enables the connect host button
								connectHost.setDisable(false);
							}
						});
						/*
						 * If connection is still alive, then it checks for the 
						 * server response is not 'Exiting!', if response is indeed the same
						 * application will update GUI and display the message
						 */
					} else if(!response.equals("Exiting!")) {
						
						System.out.println("Server: " + response);
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								outputArea.setText(outputArea.getText() +"\n\nServer: "+ response);
								inputText.setDisable(false);
								inputText.requestFocus();
							}
						});
						
						/*
						 * When a client sends a message quit or exit
						 * server responds with message 'Exiting!' and 
						 * closes the connection.
						 * The client on intercepting this message will then
						 * initiate the disconnection procedure
						 */
					} else if(response.equals("Exiting!")) {
							
						try{
							TimeUnit.SECONDS.sleep(1);
						} catch(InterruptedException ie) {
							ie.printStackTrace();
						}
						
						System.out.println("Server: " + response);
						
						disconnectHostListener();
					}
				
			} catch(IOException e) {
				Platform.runLater(new Runnable() {
					
					@Override
					public void run() {
						outputArea.setText("Connection Lost!");
					}
				});
			}
		}
		
	}
	
	/* This method is set to listen for key presses in the layout file
	 * and is invoked any time a key is pressed.
	 * This function stores the client input and then invokes process data
	 * function that sends and receives the server response.
	 */
	public void getInputText() {
		inputText.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				
				// The block gets activated only when client presses enter and the client input/output streams
				// are still open i.e. the socket is open
				if(event.getCode() == KeyCode.ENTER && !client.isInputShutdown() && !client.isOutputShutdown()) {

						// stores the client input into the variable 'input'
						input = inputText.getText();
						// clears the input text field
						inputText.clear();
						// disable text field, to prevent multiple simultaneous requests
						inputText.setDisable(true);

						// Creates a new thread, which will invoke processData method
						// when the thread starts.
						Runnable task = new Runnable() {
							@Override
							public void run() {
								if(!client.isClosed()) {
									processData();
								}
							}
						};
						
						// Initializes the runnable thread and starts it
						Thread processData = new Thread(task);
						processData.setDaemon(false);
						processData.start();
				}
			}
		});
	}
	
	// Return the primary stage of the application, that is running the GUI and controls it
	public Stage getPrimaryStage() {
        return primaryStage;
    }
	
	/* (non-Javadoc) @see javafx.application.Application#start(javafx.stage.Stage)
	 * 
	 *  JavaFX implementation method that sets and initializes the primary stage,
	 *  that is to be displayed when the application starts up 
	 */
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Client Process");
		
		initRootLayout();
	}

	/* Invoked by the start method of the JavaFX process.
	 * Loads the FXML file and sets the LexiconClient class as it's 
	 * controller.
	 * Initializes the rootLayout.
	 * Loads the rootLayout on to the primary stage
	 * and displays that scene on the application
	 */
	public void initRootLayout() {
		try {
			// FXML loader variable, that will be used to load GUI elements
			FXMLLoader loader = new FXMLLoader();
			// Sets the location, where to find the Layout file for GUI 
			loader.setLocation(getClass().getResource("ClientView.fxml"));
			// Sets the LexiconClient class as the GUI Controller
			loader.setController(this);
			// Initializes the root layout, as the default layout variable
			// where the layout is actually binded, to be accessed programmatically.
			rootLayout = (GridPane) loader.load();
			
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
			
			// Initial state of application. Input text field and 
			// disconnect host button, both are disabled. So, the flow of
			// of application is never disrupted, and always performs as expected
			inputText.setDisable(true);
			disconnectHost.setDisable(true);
			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	/* Launches the main application with the launch command, 
	 * when the program starts running
	 */
	public static void main(String[] args) {
		launch(args);
	}
}