// ----------------------------------------------------------------------------
//  BrokenBarrierException.java
//	ZCThread Library
//
//	(c) Copyright Zart Colwing, 2002-2003. All rights reserved.
// ----------------------------------------------------------------------------

package zc.thread;


// ----------------------------------------------------------------------------
//		BrokenBarrierException - class
// ----------------------------------------------------------------------------
/**
 * Thrown by Barriers when their integrity has been compromized.
 */
public class BrokenBarrierException extends Exception {

	// ----------------------------------------------------------------------------
	//		BrokenBarrierException - constructor
	// ----------------------------------------------------------------------------
	/**
	 * Constructs a BrokenBarrierException.
	 */
	public BrokenBarrierException() {
		super();
	}

	// ----------------------------------------------------------------------------
	//		BrokenBarrierException - constructor
	// ----------------------------------------------------------------------------
	/**
	 * Constructs a BrokenBarrierException with the specified detail message.
	 */
	public BrokenBarrierException(String message) {
		super(message);
	}
}

// ----- THAT'S ALL FOLKS -----------------------------------------------------
