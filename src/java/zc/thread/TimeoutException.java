// ----------------------------------------------------------------------------
//  TimeoutException.java
//	ZCThread Library
//
//	(c) Copyright Zart Colwing, 2002-2003. All rights reserved.
// ----------------------------------------------------------------------------

package zc.thread;


// ----------------------------------------------------------------------------
//		TimeoutException - class
// ----------------------------------------------------------------------------
/**
 * Thrown by synchronization classes that report timeouts via exceptions.
 * The exception is treated as a form (subclass) of InterruptedException.
 * This both simplifies handling, and conceptually reflects the fact that
 * timed-out operations are artificially interrupted by timers.
 */
public class TimeoutException extends InterruptedException {

	/**
	 * The approximate time that the operation lasted before
	 * this timeout exception was thrown.
	 */
	protected final long fDuration;


	// ----------------------------------------------------------------------------
	//		TimeoutException - constructor
	// ----------------------------------------------------------------------------
	/**
	 * Constructs a TimeoutException with no duration value.
	 */
	public TimeoutException() {
		fDuration = 0L;
	}

	// ----------------------------------------------------------------------------
	//		TimeoutException - constructor
	// ----------------------------------------------------------------------------
	/**
	 * Constructs a TimeoutException with given duration value.
	 */
	public TimeoutException(long inDuration) {
		fDuration = inDuration;
	}

	// ----------------------------------------------------------------------------
	//		TimeoutException - constructor
	// ----------------------------------------------------------------------------
	/**
	 * Constructs a TimeoutException with the
	 * specified duration value and detail message.
	 */
	public TimeoutException(long inDuration, String message) {
		super(message);
		fDuration = inDuration;
	}

	// ----------------------------------------------------------------------------
	//		getDuration
	// ----------------------------------------------------------------------------
	public long getDuration() {
		return fDuration;
	}
}

// ----- THAT'S ALL FOLKS -----------------------------------------------------
