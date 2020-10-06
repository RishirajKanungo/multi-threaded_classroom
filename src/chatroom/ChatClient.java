package chatroom;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * RISHIRAJ KANUNGO
 */
public class ChatClient extends ChatWindow {

	// Inner class used for networking
	private Communicator comm;
	boolean connected;

	// GUI Objects
	private JTextField serverTxt;
	private JTextField nameTxt;
	private JButton connectB;
	private JTextField messageTxt;
	private JButton sendB;

	public ChatClient(){
		super();
		this.setTitle("Chat Client");
		printMsg("Chat Client Started.");

		// GUI elements at top of window
		// Need a Panel to store several buttons/text fields
		serverTxt = new JTextField("localhost");	//server text field
		serverTxt.setColumns(15);
		nameTxt = new JTextField("Name");	//name text field
		nameTxt.setColumns(10);
		connectB = new JButton("Connect");	//button to connect on GUI
		JPanel topPanel = new JPanel();
		topPanel.add(serverTxt);	//adding the server label
		topPanel.add(nameTxt);	//adding the name label
		topPanel.add(connectB);	//adding button to the GUI
		contentPane.add(topPanel, BorderLayout.NORTH);	//where to add the panel in the GUI

		// GUI elements and panel at bottom of window
		messageTxt = new JTextField("");
		messageTxt.setColumns(40);
		sendB = new JButton("Send");
		JPanel botPanel = new JPanel();
		botPanel.add(messageTxt);
		botPanel.add(sendB);
		contentPane.add(botPanel, BorderLayout.SOUTH);

		// Resize window to fit all GUI components
		this.pack();

		// Setup the communicator so it will handle the connect button
		Communicator comm = new Communicator();
		connectB.addActionListener(comm);
		sendB.addActionListener(comm);

	}

	/** This inner class handles communication with the server. */
	class Communicator implements ActionListener, Runnable{
		//socket number
		private Socket socket;
		//writing object
		private PrintWriter writer;
		//reading object
		private BufferedReader reader;
		//port number
		private int port = 2113;
		public String name;

		//override the function from runnable
		@Override
		public void run(){
			//always read into the client what the msg you sent is
			while(true){
				try{
					readMsg();
				}catch(IOException e){
					printMsg("\n ERROR:" + e.getLocalizedMessage() + "\n");
				}
			}
		}

		//action listener for the client
		@Override
		public void actionPerformed(ActionEvent actionEvent) {
			//when the connect button pressed
			if(actionEvent.getActionCommand().compareTo("Connect") == 0) {
				connect();
			}
			//when the send button is pressed
			else if(actionEvent.getActionCommand().compareTo("Send") == 0) {
				sendMsg(messageTxt.getText());
			}
		}

		/** Connect to the remote server and setup input/output streams. */
		public void connect(){
			try {
				//creating socket object
				socket = new Socket(serverTxt.getText(), port);
				//Returns the address to which the socket is connected into serverIP
				InetAddress serverIP = socket.getInetAddress();
				//if it is able to get serverIP
				printMsg("Connection made to " + serverIP);
				//initialize writer and reader objects for the client
				writer = new PrintWriter(socket.getOutputStream(), true);
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				connected = true;

				//send out the message to the server
				sendMsg("Hello server");
				//threading for the client
				Thread newThread = new Thread(this);
				newThread.start();

			}
			catch(IOException e) {
				//if it is not able to connect
				printMsg("\nERROR:" + e.getLocalizedMessage() + "\n");
			}
		}
		/** Receive and display a message */
		public void readMsg() throws IOException {
			String s = reader.readLine();
			printMsg(s);
		}
		/** Send a string */
		public void sendMsg(String s){
			name = nameTxt.getText();

			writer.println(name + ": " + s);
			//printMsg(name + ": " + s);

			//check to see if the user changes their name
			if((s.charAt(0) == '/') && s.contains("/name ")){
				int newNameIndex = s.indexOf(' ') + 1;
				writer.println(name + " changed username to " + s.substring(newNameIndex));
				name = s.substring(newNameIndex);
				nameTxt.setText(name); 
			}
			messageTxt.setText("");
		}
	}


	public static void main(String args[]){
		new ChatClient();
	}

}
