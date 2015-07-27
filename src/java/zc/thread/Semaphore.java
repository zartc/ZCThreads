// ----------------------------------------------------------------------------
//  Semaphore.java
//	ZCThread Library
//
//	(c) Copyright Zart Colwing, 2002-2003. All rights reserved.
// ----------------------------------------------------------------------------

package zc.thread;


// ----------------------------------------------------------------------------
//		Semaphore - class
// ----------------------------------------------------------------------------
/**
 * Implements the <b>Dijkstra</b> counting semaphore.
 * <p>
 * Conceptually, a Semaphore maintains a set of permits.
 * Each <code>acquire</code> blocks if necessary until a permit is available,
 * and then takes it. Each <code>release</code> release a permit.
 * <p>
 * <b>Important:</b> A Semaphore is not released when the thread that acquire it is interrupted;
 * it is the responsibility of the interrupted thread to recover properly from a
 * InterruptedException and to release all its acquired Semaphore.<p>
 * <b>example:</b>
 * <pre>
 *  Semaphore theSemaphore = new Semaphore();
 *  try {
 *      theSemaphore.acquire();
 *
 *      // We did acquire the semaphore and, from now on,
 *      // it is our responsibility to make sure we release it.
 *
 *      try {
 *          // do something guarded by the semaphore
 *      }
 *      finally {
 *          theSemaphore.release();	// release the semaphore
 *      }
 *  }
 *  catch(InterruptedException e) {
 *      // The thread was interrupted before it
 *      // could acquire the semaphore, nothing to do.
 *  }
 * </pre>
 * @see Mutex
 * @see FIFOSemaphore
 * @see "Concurrency - State Models & Java Programs, chap: 5.2, page: 87"
 */
public class Semaphore implements Lock {

	protected volatile int fValue;


	// ----------------------------------------------------------------------------
	//		Semaphore - constructor
	// ----------------------------------------------------------------------------
	/**
	 * Construct a Semaphore with the given number of permits.
	 * @param inValue the initial number of permit available. If the number
	 * is negative then that number of <code>release</code> call must be made
	 * before the first <code>acquire</code> pass.
	 */
	public Semaphore(int inValue) {
		fValue = inValue;
	}

	// ----------------------------------------------------------------------------
	//		acquire
	// ----------------------------------------------------------------------------
	/**
	 * {@inheritDoc}
	 * <p>
	 * If the number of available permits is less than or equal to zero then the
	 * calling thread will block until enough permits are made available or the
	 * thread is interrupted.
	 * @exception InterruptedException {@inheritDoc}
	 */
	public void acquire() throws InterruptedException {
		if(Thread.interrupted()) {
			throw new InterruptedException();
		}

		synchronized(this) {
			while(fValue <= 0) {
				this.wait();
			}

			--fValue;
		}
	}

	// ----------------------------------------------------------------------------
	//		attempt
	// ----------------------------------------------------------------------------
	/**
	 * {@inheritDoc}
	 */
	public boolean attempt(long msecs) throws InterruptedException {
		if(Thread.interrupted()) {
			throw new InterruptedException();
		}

		synchronized(this) {
			if(fValue > 0) {
				--fValue;
				return true;
			}

			long t0 = System.currentTimeMillis();

			while(msecs > 0) {
				this.wait(msecs);

				if(fValue > 0) {
					--fValue;
					return true;
				}

				msecs -= (System.currentTimeMillis() - t0);
			}

			return false;
		}
	}

	// ----------------------------------------------------------------------------
	//		release
	// ----------------------------------------------------------------------------
	/**
	 * Releases a permit, increasing the number of available permits by one.
	 * If any threads are blocking trying to acquire a permit, then one is selected
	 * and given the permit that was just released.
	 * That thread is re-enabled for thread scheduling purposes.
	 * <p>
	 * There is no requirement that a thread that releases a permit must
	 * have acquired that permit by calling acquire().
	 * Correct usage of a semaphore is established by programming convention
	 * in the application.
	 * <p>
	 * <b>NOTE:</b> it is possible to release a semaphore more time than it
	 * was acquired, this is not considered an error, in that case, the same
	 * amount of acquire will pass without being bloqued.
	 * <p>
	 * <code>release</code> do not raise exceptions, thus it can be used in `finally'
	 * clauses without requiring extra try/catch blocks.
	 */
	public void release() {
		synchronized(this) {
			if((++fValue) > 0) {
				this.notify();
			}
		}
	}

	// ----------------------------------------------------------------------------
	//		release
	// ----------------------------------------------------------------------------
	/**
	 * Release the semaphore n time.
	 * Equivalent in effect to, but more efficient than:
	 * <pre>
	 *  for(int i = 0 ; i < n ; ++i)
	 *      release();
	 * </pre>
	 * @exception IllegalArgumentException if n is negative.
	 */
	public void release(int n) {
		if(n < 0) {
			throw new IllegalArgumentException("Negative argument " + n);
		}

		synchronized(this) {
			if((fValue += n) > 0) {
				for(int i = 0; i < fValue; ++i) {
					this.notify();
				}
			}
		}
	}

	// ----------------------------------------------------------------------------
	//		value
	// ----------------------------------------------------------------------------
	/**
	 * Returns the number of available permits.
	 * @return an int, the number of time <code>acquire</code> can be called before the
	 * Semaphore will block.
	 */
	public int value() {
		return fValue;
	}
}

// ----- THAT'S ALL FOLKS -----------------------------------------------------
