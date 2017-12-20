package org.server;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;

import org.shared.Request;

public class Worker implements Runnable{
	
	private static int next_id = 0;
	private int id;
	static int QUEUE_SIZE = 10;
	private BlockingQueue<Request> q;
	private boolean working;
	
	/**
	 * Constructor
	 * @param req The request this worker will handle
	 */
	public Worker() {
		q = new ArrayBlockingQueue<Request>(QUEUE_SIZE);
		working = true;
		id = next_id;
		next_id++;
	}
	
	/**
	 * run function for use as a thread
	 * Calls the worker_handle_request function to handle the
	 * request
	 */
	public void run() {
		
		// Loop through and accept requests until told to 
		// stop by receiving an End request
		while(working) {
			try {
				// Get something from the queue, block if there is nothing
				Request req = q.take();
				
				// check for end request
				if(req.getFunction().equals("End")) {
					break;
				}
				
				// execute the function
				do_function(req);
			}catch(Exception e) { e.printStackTrace(); }	
		}
	}
	
	/**
	 * Handles a request that has been sent to a worker by
	 * adding it to this worker's queue of tasks
	 * @param req The request to be handled
	 * @return true if the request was added to the queue,
	 * false if the queue was full
	 */
	public boolean worker_handle_request(Request req) {
		return q.offer(req);
	}
	
	/**
	 * Simulates an execution of the request
	 * @param req The request to be executed
	 */
	private void do_function(Request req) {
		
		// First determine the cost
		long cost;
		if(req.getFunction().equals("tellmenow")) {
			cost = 5;
		}else if(req.getFunction().equals("418Oracle")){
			cost = 200;
		}else if(req.getFunction().equals("countPrimes")) {
			cost = 10 * req.getParameter();
		}else {
			cost = 0;
		}
		
		// sleep using the cost as the duration
		try {
			Thread.sleep(cost);
		} catch (InterruptedException e) { }
		
		// Determine the current workload percentage by dividing the current size
		// of the queue by the maximum size and multiplying by 100
		int workload = (int)(100*((float)q.size()/(float)QUEUE_SIZE));
		
		// Print client,function,workload%
		System.out.println(String.valueOf(id) + "," + req.getClient() + "," + req.getFunction() + "," + workload + "%");
	}
	
	/**
	 * Causes a Worker to stop accepting requests.  Only the task that
	 * is currently being worked on will be completed, but other items
	 * in the queue will be lost.
	 */
	public void stop_worker() {
		working = false;
	}
	
	/**
	 * Says in the form of a percentage how busy the worker is.
	 * @return 100*(n/m) where n is the number of requests in the
	 * Worker's queue and m is the number of tasks the queue can
	 * hold.  This number is rounded to the nearest integer.
	 */
	public int get_workload() {
		return (int)(100*((float)q.size()/(float)QUEUE_SIZE));
	}
	
}
