package org.shared;

public class Request extends Message{
	
	private int param;
	
	public Request(String client, String func) {
		super(client, func);
		this.param = -1;
	}
	
	public Request(String client, String func, int param) {
		super(client, func);
		this.param = param;
	}
	
	/**
	 * Get method for params
	 * @return the parameters contained in this 
	 */
	public int getParameter() {
		return param;
	}
	
	/**
	 * Returns a string which displays the data of the
	 * Request.  
	 */
	public String toString() {
		String s1 = super.toString();
		return s1 + "Parameter: " + String.valueOf(param) + "\n";
	}
}
