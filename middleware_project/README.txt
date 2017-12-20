Middleware Final Project
========================

Output
======

The format for a row of output in my program is:

worker id,client id,function,percent workload

There is also output for each tick.  Since the number of workers is changing throughout
the lifetime of the program, I made it so that every tick would show how many active
workers there are.

If a server refuses a request it will output a line with the form:

Refused client id,function

The output goes to System.out

Overview
========

The program's major classes are Parser, App, Master, Worker, Message, and Request.  I 
defined a Response class but did not end up using it.  I decided to use a thread based
approach.  App runs the simulation.  It will create a Master, and start a Master thread.
The master accepts requests from the client through sockets.  After the Master thread has
started, App makes a Parser to read from an input file, and opens a socket to connect to 
the server in the Master thread.  The client gets a request from the Parser, and sends it
through an OutputObjectStream to the Master.

When a Master is initiated it starts out with a list of Workers, and the number of Workers
it starts is given as an argument to its constructor.  In the constructor, it constructs
the Workers and starts worker threads.  It puts each Worker it creates into the list.
Its run method loops and accepts sockets.  Each time it accepts a socket it opens an
InputObjectStream and reads a Request from it.  It then will pick a Worker to process the
request.

For a Worker thread the run function simply is constantly looping; taking requests from
its queue and simulating their execution.  It uses a blocking queue, so if the queue is
empty it just waits until a request is put into it and then continues.  To simulate a 
request it will read the function and parameter to determine the cost and then use 
Thread.sleep(cost).  After that it prints output about its current state.  The way to add
tasks to the Worker's queue is through worker_handle_request(Request req) which returns
true if the task was added to the workers queue, and false if the Worker is busy.

Load Balancing
==============

Load balancing happens when the Master is picking which worker to assign a task.  If I
simply looped through starting from the first worker and going to the last, the first
worker would have to become busy before any other workers would get any tasks.  In this
program I did load balancing by picking the worker randomly.  It will pick a random Worker
from the list of workers and try to add the task to the queue.  If that worker is busy, it
randomly picks another one.  The number of attempts it will give like this is two times 
the number of active workers.  If the task still hasn't been assigned after that it will
loop through from first to last, to make sure that every worker has been checked.  If the
task still hasn't been assigned, that means that all of the workers are busy, so it has to
refuse the task.

Elasticity
==========

Elasticity was done by having a tick regularly.  I made a static variable in Master called
TICK which says after how many requests a tick is done.  I set it to 10, meaning that
after every 10 requests there will be a tick.  The other static variable is THRESHOLD
which I will explain below.  handle_tick() will first loop through all of the workers
asking for their workload percentage.  It sums these up and keeps track of which workers
are at 0%.  It uses the sum to find the average workload percentage.  If that percentage
is higher than the THRESHOLD, it will add another worker.  If it is not above that
threshold, it will check to see if it should remove workers.  If there are at least two
workers which are at 0%, it will kill half of the workers that are at 0%.  For example,
if there were 4 workers at 0% it would kill two of them.  I found that this worked quite
well.  When the number of workers was statically set to 10, I would still get a decent
number of requests rejected.  However, with TICK=10 and THRESHOLD=70 I had no requests
rejected, and I could see the tick output showing the number of workers changing nicely.
