package org.shared;

public class Response extends Message {
	
	private Object data;
	
	public Response(String client, String func) {
		super(client, func);
		data = null;
	}
	
	public Response(String client, String func, Object data) {
		super(client, func);
		this.data = data;
	}
	
	/**
	 * Get the value of the data
	 */
	public Object getData() {
		return data;
	}
}
