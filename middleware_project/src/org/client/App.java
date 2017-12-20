package org.client;

import java.io.FileNotFoundException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.server.Master;
import org.shared.Request;
import org.client.Parser;

/**
 * Simulates the running of the server.
 */
public class App 
{
    public static void main( String[] args ) {
    	
    	// Start the master thread
    	Thread master = new Thread(new Master(10));
    	master.start();
    	
    	ObjectOutputStream output;
    	
    	// Start parsing
    	Parser parser;
    	try {
    		parser = new Parser("input/input2.csv");
    	}catch(FileNotFoundException e) {
    		e.printStackTrace();
    		return;
    	}
    	
    	// Open a socket to connect to the master thread
    	while(true) {
    		Socket socket;
    		try {
    			socket = new Socket("localhost", 5371);
    			output = new ObjectOutputStream(socket.getOutputStream());
    		}catch(Exception e) {
    			e.printStackTrace(System.err);
    			return;
    		}

    		// Send requests to server

        	Request current = parser.getRequest();
        	
        	if(current == null) { 
        		
        		// If there is nothing left in the file, tell the server stop
        		// and close the socket
        		Request req = new Request("0", "End", -1);
        		try {
        			output.writeObject(req);
        			socket.close();
        		}catch(Exception e) { e.printStackTrace(System.err); }
        		
        		// break out of the loop
        		break; 
        	}
        	try{ 
        		output.writeObject(current); 
        	}catch(Exception e) { e.printStackTrace(System.err); }
        
        
        	// Close the socket
        	try {
        		socket.close();
        	}catch(Exception e) {
        		e.printStackTrace(System.err);
        	}
    	}
    }
}
