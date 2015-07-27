// ----------------------------------------------------------------------------
//  FIFOSemaphore.java
//	ZCThread Library
//
//	(c) Copyright Zart Colwing, 2002-2003. All rights reserved.
// ----------------------------------------------------------------------------

package zc.thread;

import java.util.LinkedList;


// ----------------------------------------------------------------------------
//		FIFOSemaphore - class
// ----------------------------------------------------------------------------
/**
 * A semaphore where waiting thread are woke up in the order they came in - that is
 * in FIFO order.
 * @author Zart Colwing
 * @version 1.3
 * @see Semaphore
 * @see Mutex
 * @see "Tom Cargill paper on
 * <a href="http://www.profcon.com/cargill/jgf/9809/SpecificNotification.html">
 * Specific Notification for Java Thread Synchronization</a>"
 */
public class FIFOSemaphore implements Lock {

	protected volatile int fValue;

	/** Specific Notification Lock Queue */
	private final LinkedList fSNLQ = new LinkedList();


	// ----------------------------------------------------------------------------
	//		FIFOSemaphore - constructor
	// ----------------------------------------------------------------------------
	/**
	 * Construct a FIFOSemaphore with the given number of permits.
	 * @param inValue the initial number of permit available. If the number
	 * is negative then that number of <code>release</code> call must be made
	 * before the first <code>acquire</code> pass.
	 */
	public FIFOSemaphore(int inValue) {
		fValue = inValue;
	}

	// ----------------------------------------------------------------------------
	//		acquire
	// ----------------------------------------------------------------------------
	public void acquire() throws InterruptedException {
		if(Thread.interrupted()) {
			throw new InterruptedException();
		}

		Object theSNL = new Object();	// SNL => Specific Notification Lock

		synchronized(theSNL) {
			synchronized(this) {
				if(fValue > 0 && fSNLQ.size() == 0) {
					--fValue;
					return;
				}

				fSNLQ.addLast(theSNL);
			}

			theSNL.wait();

			/*
			1. There is certainly a large delay between the time ``theOldestSNL''
			is notified in release and the time it regain the processor.
			2. When ``theOldestSNL'' regain the processor it is not synchronized
			on ``this'' thus it is possible for another Thread to enter acquire()
			and to synchronize on ``this'' just overtaking ``theOldestSNL''.
			3. Because fValue is probably > 0 (it has been incremented in
			release() just before we notify `theOldestSNL'), the overtaker
			will even succeed at the if(fValue > 0 && ...) test above.
			Hum, sounds like a race condition scenario !
			Fortunately release() did not remove `theOldestSNL' from the SNLQueue.
			This special attention protect us against the race condition
			because no other Thread can pass while the queue is not empty.
			*/

			synchronized(this) {
				--fValue;
				fSNLQ.remove(theSNL);
			}
		}
	}

	// ----------------------------------------------------------------------------
	//		attempt
	// ----------------------------------------------------------------------------
	public boolean attempt(long msecs) throws InterruptedException {
		if(Thread.interrupted()) {
			throw new InterruptedException();
		}

		Object theSNL = new Object();

		synchronized(theSNL) {
			synchronized(this) {
				if(fValue > 0 && fSNLQ.size() == 0) {
					--fValue;
					return true;
				}

				fSNLQ.addLast(theSNL);
			}

			theSNL.wait(msecs);

			/*
			We are protected against the race condition because
			`theOldestSNL' is still in the SNL queue thus no other Thread
			can pass until the queue is empty.
			*/

			synchronized(this) {
				boolean success = false;

				if(fValue > 0 && (fSNLQ.getFirst() == theSNL)) {
					--fValue;
					success = true;
				}

				fSNLQ.remove(theSNL);

				return success;
			}
		}
	}

	// ----------------------------------------------------------------------------
	//		release
	// ----------------------------------------------------------------------------
	public void release() {
		synchronized(this) {
			if((++fValue) > 0 && fSNLQ.size() > 0) {

				// We do not remove theOldestSNL from the queue so that
				// it serves as a guard against a possible race condition
				// in acquire(). See the discussion inside acquire().

				Object theOldestSNL = fSNLQ.getFirst();

				synchronized(theOldestSNL) {
					theOldestSNL.notify();
				}
			}
		}
	}
}

// ----- THAT'S ALL FOLKS -----------------------------------------------------
