// ----------------------------------------------------------------------------
//  ZCThread Library
//
//  (c) Copyright Pascal Jacob, 2002. All rights reserved.
// ----------------------------------------------------------------------------

package zc.thread;


/**
 * A reentrant mutual exclusion lock. The owning thread can recursively <code>acquire</code> the same mutex
 * multiple times, but there must be a matching <code>release</code> for every <code>acquire</code> before
 * some other thread can acquire the mutex at its turn. Mutex can be useful in constructions that cannot be
 * expressed using java synchronized blocks because the acquire/release pairs do not occur in the same method
 * or code block.
 * 
 * @see Semaphore
 * @see FIFOSemaphore
 */
public class Mutex implements Lock {

	/**
	 * The thread that currently own this mutex, or null if this mutex is up (not owned by anyone).
	 */
	protected volatile Thread fOwner = null;

	/**
	 * Count how many times the owner thread recursively acquired this Mutex. This mutex will not be released
	 * until this counter reach zero.
	 */
	protected volatile int fCount = 0;


	/**
	 * Acquire this mutex.
	 * <p>
	 * If this mutex is already owned by another thread then the calling thread will block until this mutex is
	 * released or the, then blocked, thread is interrupted.
	 * <p>
	 * It is legal for a thread to re-acquire the same mutex. The mutex will not be released until as many
	 * <code>release</code> as <code>acquire</code> have occurred.
	 * 
	 * @exception <code>InterruptedException</code> {@inheritDoc}
	 */
	public void acquire() throws InterruptedException {
		if(Thread.interrupted()) {
			throw new InterruptedException();
		}

		synchronized(this) {
			while(fOwner != null && fOwner != Thread.currentThread()) {
				this.wait();
			}

			fOwner = Thread.currentThread();
			++fCount;
		}
	}

	/**
	 * Try to acquire this mutex without blocking.
	 * 
	 * @param msecs the number of milliseconds to wait for this mutex before giving up.
	 * @return true if this mutex has been acquired within the given time.
	 * @exception <code>InterruptedException</code> {@inheritDoc}
	 */
	public boolean attempt(long msecs) throws InterruptedException {
		if(Thread.interrupted()) {
			throw new InterruptedException();
		}

		synchronized(this) {
			// This mutex is not yet owned by anyone or already owned
			// by the current thread itself, acquire it immediately.
			if(fOwner == null || fOwner == Thread.currentThread()) {
				fOwner = Thread.currentThread();
				++fCount;
				return true;
			}

			long t0 = System.currentTimeMillis();

			while(msecs > 0) {
				this.wait(msecs);

				if(fOwner == null) {
					fOwner = Thread.currentThread();
					++fCount;
					return true;
				}

				msecs -= (System.currentTimeMillis() - t0);
			}

			return false;
		}
	}

	/**
	 * Release this mutex.
	 * <p>
	 * If this mutex has been acquired multiple time by the owner thread then it will not be released until as
	 * many <code>release</code> as <code>acquire</code> have occurred.
	 * 
	 * @exception <code>IllegalMonitorStateException</code> if the current thread is not the owner of this
	 * mutex. It is an error for a thread to call <code>release</code> on a mutex not owned by the thread.
	 */
	public void release() throws IllegalMonitorStateException {
		if(Thread.currentThread() != fOwner) {
			throw new IllegalMonitorStateException("current thread not owner of the mutex");
		}

		synchronized(this) {
			if((--fCount) == 0) {
				fOwner = null;
				this.notifyAll();
			}
		}
	}

	/**
	 * Return the number of times the owner thread recursively acquired this Mutex or zero if there is no
	 * current owner.
	 * 
	 * @return an int.
	 */
	public int getCount() {
		return fCount;
	}

	/**
	 * Return the thread that owns this mutex, or <code>null</code> if there is no current owner.
	 * 
	 * @return a Thread object.
	 */
	public Thread getOwner() {
		return fOwner;
	}
}

// ----- THAT'S ALL FOLKS -----
