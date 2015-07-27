/* ----------------------------------------------------------------------------
 *  RendezVous.java
 *  ZCThread Library
 *
 *  (c) Copyright Zart Colwing. 2000, 2001.
 *  All rights reserved.
 * ------------------------------------------------------------------------- */

package zc.thread;


// ----------------------------------------------------------------------------
//		RendezVous - class
// ----------------------------------------------------------------------------
/**
 * <code>RendezVous</code>, sometime called <code>request-reply</code>, is a
 * message passing protocol used to support client-server interaction. Client
 * processes send request messages to a server process requesting the server to
 * perform some service. These request messages are queued to a FIFO list. The
 * server accepts requests from the FIFO list and upon completion of the
 * requested service sends a reply message to the client that make the request.
 * The client blocks waiting for the reply message. RendezVous involves
 * <i>many-to-one</i> communication in that many clients may request services
 * from a single server. The reply to a request is a <i>one-to-one</i>
 * communication from the server process to the client that requested the
 * service.
 *
Case 1: 
Conditions: 
Task A requests a rendezvous with Task B 
Task B is suspended waiting to accept a rendezvous 
Actions: 
Task A is suspended, while Task B executes the code for the rendezvous 
Case 2: 
Conditions: 
Task A requests a rendezvous with Task B 
Task B is not waiting to accept a rendezvous 
Actions: 
Task A is suspended, and the request for the rendezvous is placed on a queue 
Case 3: 
Conditions: 
Task B decides to accept rendezvous 
There are no queued rendezvous 
Actions: 
Task B is suspended, awaiting a rendezvous request 
Case 4: 
Conditions: 
Task B decides to accept rendezvous 
There are queued rendezvous 
Actions: 
Task B executes the code for one queued rendezvous request 
The suspended task associated with the queued rendezvous is unsuspended 
 *
 * @deprecated replaced by the SynchronousChannel class.
 * :NOTE: rehabilite this class instead of the SynchronousChannel.
 */
public class RendezVous {

	/** the array of participants */
	protected final Object[] fObjectArray;


	// ----------------------------------------------------------------------------
	//		RendezVous - constructor
	// ----------------------------------------------------------------------------
	public RendezVous() {
		fObjectArray = null;
	}

	// ----------------------------------------------------------------------------
	//		await
	// ----------------------------------------------------------------------------
	public synchronized Object await() throws InterruptedException {
		return null;
	}
}


















/*
 |
 | Rendezvous.java
 |
 | James Shin Young
 |
 | Copyright (c) 1996 by James Shin Young and the Regents
 | of the University of California.  All rights reserved.
 |
 | Permission to use, copy, modify, and distribute this software
 | and its documentation for NON-COMMERCIAL purposes and without
 | fee is hereby granted provided that this copyright notice
 | appears in all copies.
 |
 | Updated January 7, 1997
 |
 */

/**
 *	  @author James Shin Young
 */

public class Rendezvous {

///////////////////////////////////////////////////////////////////////////////
//* Variables */

	/* Private variables */

	// Array to store all the threads involved in the rendezvous.
	private Thread[] target;
	// Keeps track of the number of threads in target
	private int targetIndex;
	// Array to set the state of all the threads.
	private boolean[] targState;

///////////////////////////////////////////////////////////////////////////////
//* Constructors */

	public Rendezvous() {
		this(2);
	}

	public Rendezvous(int initSize) {
		target = new Thread[initSize];
		targState = new boolean[initSize];
		targetIndex = 0;
	}

	public Rendezvous(Thread[] initMembers) {
		target = initMembers;
		targState = new boolean[target.length];
		for (int i = 0; i < targstate.length; i++) {
			targState[i] = false;
		}
		targetIndex = target.length;
	}

///////////////////////////////////////////////////////////////////////////////
//* Public methods */
	
	/**
	 * Add a new thread to the rendezvous.
	 */
	public synchronized void add(Thread newTarget) {
		// Make sure this thread isn't in the rendezvous already.
		for (int i=0; i < targetIndex; i++) {
			if (target[i] == newTarget) // Whoops, break out of the method.
				return;
		}
		
		// If array is full..
		if (target.length == targetIndex) {
			// Make new arrays with more space.
			Thread[] newTarg = new Thread[2*targetIndex];
			boolean[] newState = new boolean[2*targetIndex];
			
			// Copy contents of old array to new array.
			System.arraycopy(target,0,newTarg,0,targetIndex);
			System.arraycopy(targState,0,newState,0,targetIndex);
			
			// Replace target w/ the new array.
			target = newTarg;
			targState = newState;
		}
		
		// Put new element in array.
		target[targetIndex] = newTarget;
		
		// Initialize state to false (hasn't reached the rendezvous point yet)
		targState[targetIndex] = false;
		targetIndex++;
	}
	 
	/**
	 * Add the currently executing thread to the rendezvous.
	 */
	public void add() {
		this.add(Thread.currentThread());
	}
	  
	/**
	 * Remove the currently executing thread from the rendezvous.
	 * Note that threads must remove themselves from the rendezvous;
	 * they may not be removed by other threads.
	 */
	public synchronized void remove() {
		this.remove(Thread.currentThread());
	}
	   
	/**
	 * Threads should call this when they've reached their rendezvous points 
	 */
	public synchronized void rendezvous() {
		// Mark off the appropriate entry in the array
		for (int i=0; i < targetIndex; i++) {
			if (target[i] == Thread.currentThread()) {
				targState[i] = true;
				
				if (checkStatus() == false) {
					try {
						wait();
					}
					catch (InterruptedException e) {
					}
				}
				break;
			}
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////
	//* Private methods */
	
	/* Check the status of all the threads in the rendezvous and wake them up when appropriate.
	 * Return true if all members have reached the rendezvous, false otherwise.
	 */
	private boolean checkStatus() {
		// Look for dead threads..
		// We don't do this anymore. Thus any thread in a rendezvous
		// *MUST* remove itself from any rendezvous' it is in before
		// it dies.
		/*
		for (int i=0; i < targetIndex; i++) {
			if (target[i].isAlive()== false) {
				// Remove entry..
				System.arraycopy(target,i+1,target,i,target.length-i-1);
				System.arraycopy(targState,i+1,targState,i,targState.length-i-1);
				targetIndex--;
				i--;
			}
		}
		*/
		
		boolean done = true;
		
		// See if all the entrys are true now..
		for (int i = 0; i < targetIndex; i++) {
			if (targState[i] == false) {
				done = false;
				break;
			}
		}
		if (done == false) {
			// If not all threads have reached rendezvous point yet..
			return(false);
		}
		else {
			// Reset the targState array.
			for (int i = 0; i < targetIndex; i++) {
				targState[i] = false;
			}
			
			// Wake everyone up.
			notifyAll();
			return(true);
		}
	}
	
	/**
	 * Remove a thread from the rendezvous.
	 */
	private void remove(Thread newTarget) {
		for (int i = 0; i < targetIndex; i++) {
			// Find the thread in the array.
			if (target[i] == newTarget) {
				// Remove the entry..
				System.arraycopy(target,i+1,target,i,target.length-i-1);
				System.arraycopy(targState,i+1,targState,i,targState.length-i-1);
				targetIndex--;
				checkStatus();
				break;
			}
		}
	}
}

// ----- THAT'S ALL FOLKS -----------------------------------------------------
