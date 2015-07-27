// ----------------------------------------------------------------------------
//  RWLock.java
//	ZCThread Library
//
//	(c) Copyright Zart Colwing, 2002-2003. All rights reserved.
// ----------------------------------------------------------------------------

package zc.thread;


// ----------------------------------------------------------------------------
//		RWLock - interface
// ----------------------------------------------------------------------------
/**
 * A RWLock accepts any number of concurrent <i>Readers</i> when there is no
 * <i>Writer</i> and permits only a single <i>Writer</i> when there is no <i>Readers</i>.
 * <p>
 * A RWLock is concerned with access to a shared resource by two kinds of threads.
 * <i>Readers</i> execute transactions that examine the resource while <i>Writers</i> both examine
 * and update the resource. For the resource to be updated correctly, <i>Writers</i> must
 * have exclusive access to the resource while they are updating it. If no <i>Writer</i>
 * is accessing the resource, any number of <i>Readers</i> may concurrently access it.
 * <p>
 * A RWLock is used to prevents reads from occurring while writes are in progress
 * and vice versa, and also to prevents multiple writes from happening simultaneously.
 * Multiple read operations can run in parallel however.
 * <p>
 * In case of contention a RWLock can give priority to <i>Readers</i>
 * or to the <i>Writer</i> depending on the implementation.
 */
public interface RWLock {

	// ----------------------------------------------------------------------------
	//		acquireRead
	// ----------------------------------------------------------------------------
	/**
	 * Acquire a read lock. Blocks until a read operation can be performed safely.
	 * <p>
	 * This call must be followed by a call to <code>release</code> as soon as
	 * the read operation completes.
	 * @exception InterruptedException if the calling thread is interrupted while waiting.
	 */
	public void acquireRead() throws InterruptedException;

	// ----------------------------------------------------------------------------
	//		acquireWrite
	// ----------------------------------------------------------------------------
	/**
	 * Acquire the write lock. Blocks until the write operation can be performed safely.
	 * <p>
	 * This call must be followed by a call to <code>release</code> as soon as
	 * the write operation completes.
	 * @exception InterruptedException if the calling thread is interrupted while waiting.
	 */
	public void acquireWrite() throws InterruptedException;

	// ----------------------------------------------------------------------------
	//		release
	// ----------------------------------------------------------------------------
	/**
	 * Release the lock. You must call this method as soon as you're done reading or writing.
	 */
	public void release();
}

// ----- THAT'S ALL FOLKS -----------------------------------------------------
