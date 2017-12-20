package org.client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.shared.Request;

import java.io.FileNotFoundException;

public class Parser {
	
	private BufferedReader reader;
	
	/**Initializes a parser object to read from the
	 * file filename
	 * @param filename The file to be read from
	 * @throws FileNotFoundException if filename is not
	 * not valid
	 */
	public Parser(String filename) throws FileNotFoundException{
		this.reader = new BufferedReader(new FileReader(filename));
	}
	
	/**Parses the next line in the input file and returns the
	 * next request.
	 * @return Either a Request or null if either the end of the file
	 * is reached or the file was of invalid format
	 */
	public Request getRequest() {
		
		String[] params;
		
		// Read a line and split on comma
		try {
			String line = reader.readLine();
			// Could be end of file
			if(line == null) { return null; }
			params = line.split(",",3);
		}catch(IOException e) {
			System.err.println(e);
			return null;
		}
		
		Request result = new Request(params[0], params[1], Integer.parseInt(params[2]));
		return result;
	}
}
