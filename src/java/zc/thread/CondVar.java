// ----------------------------------------------------------------------------
//  CondVar.java
//	ZCThread Library
//
//	(c) Copyright Zart Colwing, 2002-2003. All rights reserved.
// ----------------------------------------------------------------------------

package zc.thread;


// ----------------------------------------------------------------------------
//		CondVar - class
// ----------------------------------------------------------------------------
/**
 * Implements a simple "condition variable." The notion
 * is that a thread awaits for some condition to become true.
 * If the condition is false, then no wait occurs.<p>
 * <p>
 * <b>Be very careful of nested-monitor-lockout here:</b>
 * <pre>
 *  class lockout {
 *      CondVar theCondVar = new CondVar();
 *      void synchronized foo() throws InterruptedException {
 *          // <b>nested monitor here</b>
 *          theCondVar.await();
 *      }
 *      void synchronized bar() {
 *          theCondVar.set();
 *      }
 *  }
 *
 *  final lockout theLockout = new lockout();
 *
 *  Thread racer1 = new Thread("racer1") {
 *      public void run() {
 *          try {
 *              theLockout.foo();
 *          }
 *          catch(Exception ex) {
 *              fail(ex.getMessage());
 *          }
 *      }
 *  };
 *
 *  racer1.start();
 *
 *  // <b>Deadlock because the Thread "racer1" hold the lockout's monitor !</b>
 *  theLockout.bar();
 * </pre>
 * The solution is either to release the lockout's monitor before waiting on
 * the CondVar:
 * <pre>
 *      void foo() throws InterruptedException {
 *          synchronized(this) {
 *              // some code...
 *          }
 *          // <b>Moved theCondVar.await() outside the monitor</b>
 *          theCondVar.await();
 *      }
 * </pre>
 * or to not synchronizing the <code>Lockout.bar()</code> function.
 * <pre>
 *      // <b>Removed the synchronized statement</b>
 *      void bar() {
 *          theCondVar.set();
 *      }
 * </pre>
 */
public class CondVar {

	/** the CondVar state. Default to false */
	private volatile boolean fState;


	// ----------------------------------------------------------------------------
	//		CondVar - constructor
	// ----------------------------------------------------------------------------
	/**
	 * Create a new condition variable initially set to false.
	 */
	public CondVar() {
		fState = false;
	}

	// ----------------------------------------------------------------------------
	//		CondVar - constructor
	// ----------------------------------------------------------------------------
	/**
	 * Create a new condition variable in a known state.
	 */
	public CondVar(boolean inState) {
		fState = inState;
	}

	// ----------------------------------------------------------------------------
	//		set
	// ----------------------------------------------------------------------------
	/**
	 * Inform waiting Threads that the condition turns true.
	 * All waiting Threads are notified.
	 */
	public synchronized void fire() {
		fState = true;
		// Notify all threads that are waiting on this monitor.
		this.notifyAll();
	}

	// ----------------------------------------------------------------------------
	//		reset
	// ----------------------------------------------------------------------------
	/**
	 * Reset the condition to <code>false</code>.
	 * No Threads are notified.
	 */
	public synchronized void reset() {
		fState = false;
	}

	// ----------------------------------------------------------------------------
	//		await
	// ----------------------------------------------------------------------------
	/**
	 * Wait (potentially forever) for the condition to turns true.
	 * If the condition is already true, then no wait occurs.
	 * @exception InterruptedException if the calling thread is interrupted while waiting.
	 */
	public void await() throws InterruptedException {
		if(Thread.interrupted()) {
			throw new InterruptedException();
		}

		if(fState == true) {
			return;
		}

		synchronized(this) {
			while(fState != true) {
				this.wait();
			}
		}
	}

	// ----------------------------------------------------------------------------
	//		await
	// ----------------------------------------------------------------------------
	/**
	 * Wait at most msecs milliseconds for the condition to turns true.
	 * @param msecs the number of milliseconds to wait before giving up.
	 * @return true if the condition became true within the given time.
	 * @exception InterruptedException if the calling thread is interrupted while waiting.
	 */
	public boolean await(long msecs) throws InterruptedException {
		if(Thread.interrupted()) {
			throw new InterruptedException();
		}

		if(fState == true) {
			return true;
		}

		synchronized(this) {
			long t0 = System.currentTimeMillis();

			while(msecs > 0) {
				this.wait(msecs);

				if(fState == true) {
					return true;
				}

				msecs -= (System.currentTimeMillis() - t0);
			}

			return false;
		}
	}

	// ----------------------------------------------------------------------------
	//		hasFired
	// ----------------------------------------------------------------------------
	/**
	 * Returns true if and only if the condition is set.
	 * @return a boolean.
	 */
	public boolean hasFired() {
		return fState;
	}
}

// ----- THAT'S ALL FOLKS -----------------------------------------------------
