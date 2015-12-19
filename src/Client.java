//--------------------------------------------------------------//
// MyClient.java 
//--------------------------------------------------------------//
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/*----------------------------------------------------------------
 * Program Process : MyClient
 -----------------------------------------------------------------
 * main()   				-- Enter Point
 * MyClient()     			-- Initialization
 * EstablishConnection() 	-- Establish Connection
 * class IncomingReader  	-- Recieve message (inner class)
 * actionPerformed()     	-- Action after pressing 
 ------------------------------------------------------------------
 */

public class Client extends JFrame implements ActionListener{  
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int PORT = 8888;
	// ****************    Variable     ***********************
	// Name & server's ip
	private String name,serverip;       
	private BufferedReader reader; // buffer for incoming stream (from server to user)      
	private PrintStream writer;    // buffer for outcoming stream (from user to server)
	
    //Socket : A socket is an endpoint for communication between two machines. 
	private Socket socket; 
	//Display panel
	private JTextArea incoming;
	private JTextField outgoing;
	
	private JLabel jlusername, jlserverip, state;   
	private JTextField jfusername;
	private JTextField jfserverip;
 
	private MenuBar mBar; 		 // placeholder for menus     
	private Menu mFile; 		 // File menu 
	private MenuItem mFileSave;  // Save item in File menu 

	//--------------------------------------------------------------//
	// Initialization
	//--------------------------------------------------------------//
	Client (){ 
		// Declaration and Definition of Application
		// Build a GUI by JFrame
		super("Group Chat room");
		serverip="";
		
		// Elements within usernameserveripPanel	
		jlusername = new JLabel("Name¡G");
		jfusername = new JTextField("ex. weikai",10);
		jlserverip = new JLabel("Server serverip¡G");
		jfserverip  = new JTextField("ex: 127.0.0.1",10);
		JButton setusernameserverip = new JButton("Connection");
		setusernameserverip.addActionListener(this);
		
		// add to usernameserveripPanel
		JPanel usernameserveripPanel  = new JPanel();  
		usernameserveripPanel.add(jlusername);
		usernameserveripPanel.add(jfusername);         
		usernameserveripPanel.add(jlserverip);
		usernameserveripPanel.add(jfserverip); 
		usernameserveripPanel.add(setusernameserverip);
		getContentPane().add(BorderLayout.NORTH,usernameserveripPanel);  // set usernameserveripPanel in the header 

		// Elements within mainPanel
		incoming = new JTextArea(15,50);
		outgoing = new JTextField(20);
		incoming.setLineWrap(true);     	// Sets the line-wrapping policy of the text area.
		incoming.setWrapStyleWord(true); 	// Sets the style of wrapping used if the text area is wrapping lines.
		incoming.setEditable(false); 		// Make incoming uneditable for read-only.
		JScrollPane qScroller = new JScrollPane(incoming); //add to JScrollPane so that let the incoming message can scrollable.
		qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);   // Scrolling direction : vertical  
		qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); // Scrolling direction : horizontal  
		//sendButton : activate connection 
		JButton sendButton = new JButton("Submit");
		sendButton.addActionListener(this); 
		
		// addd to mainPanel: the dialog Region
		JPanel mainPanel = new JPanel();       
		mainPanel.add(qScroller);
		mainPanel.add(outgoing);
		mainPanel.add(sendButton);
		getContentPane().add(BorderLayout.CENTER,mainPanel);  // let mainPanel in the middle
		
		// Elements within menu Bar & menu
		mBar = new MenuBar(); 
		mFile = new Menu("File");
		mFileSave = new MenuItem("Save");
		mFileSave.addActionListener(this); 
		mFile.add(mFileSave);  	// add Save item to File menu
		mBar.add(mFile); 		// add File menu to menu bar
		// MenuBar
		setMenuBar(mBar);
		// show stata in footer
		state = new JLabel("Please input username & server's ip.",10);
		getContentPane().add(BorderLayout.SOUTH,state);    
		setSize(600,450); // set GUI dimension 
		setVisible(true);
		// Exit 
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				System.out.println("Leave Chat Room");
				System.exit(0);
			}
		});
	}
	//--------------------------------------------------------------//
	// Main Enter Point
	//--------------------------------------------------------------//
	public static void main(String[] args){
		new Client();
	}
	//--------------------------------------------------------------//
	// Establish Connection
	//--------------------------------------------------------------//
	private void EstablishConnection(){
		try{
			// use socket to connect to the chat room's server which serverip is xxx.xxx.xxx.xxx, port:8888
			socket = new Socket(serverip,PORT);      
			// Build up input stream (from user to server) and connect with Socket object
			InputStreamReader streamReader = new InputStreamReader(socket.getInputStream());  
			// use BufferReader for buffering input stream
			reader = new BufferedReader(streamReader);    
			// extract output stream from server by Sucket
			writer = new PrintStream(socket.getOutputStream());
			// connect successfully
			state.setText("Connected Successfully"); 
			System.out.println("Connected Successfully");    
		}catch(IOException ex ){
			System.out.println("Fail to connect to : "+serverip);
		}
	}
	//--------------------------------------------------------------//
	// Recieve messages
	//--------------------------------------------------------------//
	public class IncomingReader implements Runnable{
		public void run(){
			// reader has been established after calling EstablishConnection()
			String message;
			try{
				while ((message = reader.readLine()) != null){
					incoming.append(message+'\n');
				}
			}catch(Exception ex ){
				ex.printStackTrace();
			}
		}
	} 
	//--------------------------------------------------------------//
	// Action after pressing
	//--------------------------------------------------------------//
	public void actionPerformed(ActionEvent e){
		String str = e.getActionCommand();   
		if(str.equals("Connection")){
			// acccess name & serverip
			name = jfusername.getText();
			serverip  = jfserverip.getText(); 
			// update state
			state.setText("Setting "+name+" : "+ serverip +"..."); 
			// Establish connection
			EstablishConnection();          
			// Create a thread for receiving input from server
			Thread readerThread = new Thread(new IncomingReader());  
			readerThread.start();  
		}else if(str.equals("Submit")){    
			// check both server ip & username are valid
			if((serverip!=null) && (outgoing.getText()!="")){
				try{
					// Submit messages typed in outgoing TextField
					writer.println((name+":"+outgoing.getText())); 
					writer.flush();         
				}catch(Exception ex ){
					System.out.println("Fail to send message...");
				}
				// Clear the outgoing after submitting.
				outgoing.setText("");        
			}
		}else if (str.equals("Save")){               
			try{             
				FileWriter f = new FileWriter("log.txt");     
				f.write(incoming.getText());      
				f.close();           
				state.setText("File Saved");
			}catch (IOException e2){
				state.setText("Fail to save files...");
			}              
		} 
	}
}

