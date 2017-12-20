package org.shared;

import java.io.Serializable;

public class Message implements Serializable{
	private String client;
	private String func;
	
	public Message() {}
	
	/**Initializes a Message
	 * @param client Name of the client this Message concerns
	 * @param func   Name of the function this Message concerns
	 */
	public Message(String client, String func) {
		this.client = client;
		this.func = func;
	}
	
	// Get functions
	
	/**
	 * Returns the String which identifies a client.
	 */
	public String getClient() {
		return new String(client);
	}
	
	/**
	 * Returns the name of a function
	 */
	public String getFunction() {
		return new String(func);
	}
	
	/**toString method
	 */
	@Override
	public String toString() {
		String response = "Client: ";
		response = response + client + "\n" 
				+ "Function: " + func + "\n";
		return response;
	}
}
