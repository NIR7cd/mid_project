package org.server;

import java.io.EOFException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

import org.shared.Request;

public class Master implements Runnable{
	
	private static int TICK = 10;
	private static float THRESHOLD = (float)70.0;
	
	private ArrayList<Worker> workers;
	
	public Master(int numWorkers) {
		
		// Create a list of workers
		workers = new ArrayList<Worker>();
		for(int i = 0; i < numWorkers; ++i) {
			Worker worker = new Worker();
			
			// Start the thread that works on tasks in the queue
			Thread t1 = new Thread(worker);
			t1.start();
			
			// Add the worker to the list
			workers.add(worker);
		}
	}
	
	/** Run method required for Runnable
	 * The thread opens a server socket and repeatedly accepts requests
	 * from clients.  It will open an input stream and read an object which
	 * it tries to convert to a Request.  It sends the request to a worker,
	 * 
	 */
	public void run() {
		try {
			// Open server socket
			ServerSocket servsocket = new ServerSocket(5371, 5);
			
			int clock = 0;
			
			// Loop and accept requests
			while(true) {
				
				// Elasticity: handle_tick will change the number of workers available
				// reducing if there are many inactive workers, or increasing if most of
				// the workers are considered very busy
				clock++;
				if(clock == TICK) {
					clock = 0;
					handle_tick();
				}
				
				// Open socket and get the input stream from that socket
				Socket socket = servsocket.accept();
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
				
				Request req;
				
				// Read a request from the input stream
				try {
					req = (Request)in.readObject();
					
					// If it is an end, stop the server
					if(req.getFunction().equals("End")) {
						socket.close();
						break;
					}
					
					// Send the reqest to the worker
					handle_client_request(req);
					
				}catch(EOFException e) { break; }
				
				// close the socket
				socket.close();
			}
			
			// close the server socket
			servsocket.close();
			
		}catch(Exception e) {
			e.printStackTrace(System.err);
		}
	}
	
	/**Handles a client request by dispatching a worker
	 * to work on the request
	 * @param req    The request to be executed
	 * @throws InvalidArgumentException if the request is invalid
	 */
	public void handle_client_request(Request req) {
		
		Random random = new Random();
		
		boolean inProgress = false;
		
		// Load balancing:  Randomly pick the worker that will work on this task.
		// This way the Workers that are earlier in the queue won't always fill up first.
		// I will randomly pick workers 2*n times where n is the number of workers.  If
		// All of the ones I pick are busy I will loop through the whole array just to make
		// sure I haven't missed any that aren't busy
		for(int i = 0; i < 2*workers.size(); ++i) {
			
			// Get a random worker
			int j = random.nextInt(workers.size());
			
			inProgress = workers.get(j).worker_handle_request(req);
			if(inProgress) { break; }
		}
		
		// If inProgress is still false, that means all of the workers
		// that were checked were busy
		if(!inProgress) {
			// Loop through all of the workers.  Some of the workers might not have
			// been checked and may still be accepting jobs
			for(int i = 0; i < workers.size(); ++i) {
				inProgress = workers.get(i).worker_handle_request(req);
				if(inProgress) { break; }
			}
			
			// If inProgress is still false, that means all of the workers are busy
			System.out.println("Refused " + req.getClient() + "," + req.getFunction());
		}
	}
	
	/**
	 * Changes the number of workers available to the master.  If there are
	 * at least two inactive nodes it removes half of the inactive nodes.  If
	 * the average workload is greater than the statically defined THRESHOLD it adds a worker.
	 */
	private void handle_tick() {
		int sum = 0;
		ArrayList<Worker> inactive = new ArrayList<Worker>();
		for(Worker worker : workers) {
			int i = worker.get_workload();
			sum += i;
			if(i == 0) { inactive.add(worker); }
		}
		
		// Check the average workload
		if((float)sum/(float)workers.size() >= THRESHOLD) {
			Worker worker = new Worker();
			Thread t1 = new Thread(worker);
			t1.start();
			workers.add(worker);
		}else if(inactive.size() > 1) {
			for(int i = 0; i < inactive.size()/2; ++i) {
				inactive.get(i).stop_worker();
				workers.remove(inactive.get(i));
			}
		}
		
		System.out.println("===============Tick=================");
		System.out.println("Number of workers: " + String.valueOf(workers.size()));
		System.out.println("===============Tick=================");
	}
	
}
