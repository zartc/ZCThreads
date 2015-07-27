// ----------------------------------------------------------------------------
//  Future.java
//	ZCThread Library
//
//	(c) Copyright Zart Colwing, 2002-2003. All rights reserved.
// ----------------------------------------------------------------------------

package zc.thread;


// ----------------------------------------------------------------------------
//		Future - class
// ----------------------------------------------------------------------------
/**
 * A Future represents the results of an asynchronous computation.
 * Futures maintain a single value serving as the result of an operation.
 * The result cannot be accessed until the computation has completed.
 */
public class Future {

	protected volatile Object fObject;
	protected Latch fReady = new Latch();


	// ----------------------------------------------------------------------------
	//		get
	// ----------------------------------------------------------------------------
	/**
	 * Obtains the value of the Future.
	 * <br>block (possibly forever) until the value is made available.
	 * @return an Object the value of the Future.
	 * @exception InterruptedException if the calling thread is interrupted while waiting.
	 */
	public Object get() throws InterruptedException {
		fReady.await();
		return fObject;
	}

	// ----------------------------------------------------------------------------
	//		get
	// ----------------------------------------------------------------------------
	/**
	 * Obtains the value of the Future.
	 * <br>block for at most msecs milliseconds waiting for the value to be made
	 * available. If the value is not available after msec milliseconds a
	 * TimeoutException is thrown.
	 * @param msecs the number of milliseconds to wait before giving up.
	 * @return an Object the value of the Future.
	 * @exception TimeoutException if not ready after msecs
	 */
	public Object get(long msecs) throws InterruptedException, TimeoutException {
		if(fReady.await(msecs) == false) {
			throw new TimeoutException(msecs);
		}
		return fObject;
	}

	// ----------------------------------------------------------------------------
	//		set
	// ----------------------------------------------------------------------------
	/**
	 * Set the value of the Future and make it available to waiting Threads.
	 */
	public void set(Object inObject) {
		if(fReady.hasFired() == false) {
			synchronized(fReady) {
				if(fReady.hasFired() == false) {
					fObject = inObject;
					fReady.fire();
				}
			}
		}
	}

	// ----------------------------------------------------------------------------
	//		isSet
	// ----------------------------------------------------------------------------
	/**
	 * Returns true if and only if the future value is ready.
	 * @return a boolean
	 */
	public boolean isSet() {
		return fReady.hasFired();
	}
}

// ----- THAT'S ALL FOLKS -----------------------------------------------------
