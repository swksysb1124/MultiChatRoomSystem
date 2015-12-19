import java.io.*;
import java.net.*;
import java.util.*;


public class Server {
	private static final int PORT = 8888;
	Vector<PrintStream> output;//output
	//--------------------------------------------------------------//
	// Main Enter Point
	//--------------------------------------------------------------//
	public static void main (String args[]){
		new Server().go();     
	}
	 //--------------------------------------------------------------//
	 // Establish connection
	 //--------------------------------------------------------------//
	public void go() {
		output = new Vector<PrintStream>();          
		try{
			// Create a ServerSocket object and set port as 8888;
			@SuppressWarnings("resource")
			System.out.println("start the server...");
			/*
			 * ServerSocket 
			 * This class implements server sockets. A server socket waits for requests 
			 * to come in over the network. It performs some operation based on that 
			 * request, and then possibly returns a result to the requester. 
			 */
			ServerSocket serverSock = new ServerSocket(PORT);  
			while(true){
				System.out.println("waiting for connection from client...");
				Socket cSocket = serverSock.accept(); // Listens for a connection to be made to this socket and accepts it.   
				// Connect stream flow from clients
				PrintStream writer = new PrintStream(cSocket.getOutputStream());  
				System.out.println(writer); 
				output.add(writer);         
				//Create a thread by the socket transmited from a client
				Thread t = new Thread(new Process(cSocket));
				t.start(); // run thread
				
				// Show connections from current on-line clients       
				System.out.println("There is/are "
					+ (Thread.activeCount()-1)
					+ " connections to "
					+ cSocket.getLocalSocketAddress());  
			} 
		}catch(Exception ex){
			System.out.println("Fail to connection with clients");
		}
	 }
	 //--------------------------------------------------------------//
	 // Process
	 //--------------------------------------------------------------//
	 public class Process implements Runnable{   

		 BufferedReader reader;    
		 Socket socket;            
		 //----------------------------------------------------------//
		 // connect input stream from socket 
		 //----------------------------------------------------------//
		 public Process(Socket cSocket){
			 try{
				 socket = cSocket;
				 // access input stream from socket
				 InputStreamReader isReader = new InputStreamReader(socket.getInputStream()); 
				 reader = new BufferedReader(isReader);
			 }catch(Exception ex){
				 System.out.println("Connection from client fails");
			 } 
		 }
		 //--------------------------------------------------------------//
		 // run thread
		 //--------------------------------------------------------------//
		 public void run(){
			String message;
			try{
				//read message
				while ((message = reader.readLine())!=null){   
					System.out.println("recieved " + message);
					tellApiece(message);
				}
			}catch(Exception ex){
				 System.out.println("a client has leaved");
			}
		 }
		 //--------------------------------------------------------------//
		 // notify everyone in the chat room
		 //--------------------------------------------------------------//
		 public void tellApiece(String message){
			 //use Iterator to run throught the messages recorded    
			 Iterator<PrintStream> it = output.iterator(); 

			 while(it.hasNext()){          
				 try{

					 PrintStream writer = (PrintStream) it.next();  
					 //print out
					 writer.println(message); 
					 //flush the writer buffer
					 writer.flush();           
				 }
				 catch(Exception ex){
					 System.out.println("Fail to connect");
				 }
			 }
		 }
	 } 
}
