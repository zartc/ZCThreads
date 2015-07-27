// ----------------------------------------------------------------------------
//  ZCThread Library
//
//  (c) Copyright Pascal Jacob, 2002. All rights reserved.
// ----------------------------------------------------------------------------

package zc.thread;

/**
 * Main interface for Semaphore, Mutex and CondVar.
 * <p>
 * Most Locks are intended to be used primarily (although not exclusively) in before/after constructions such
 * as:
 * 
 * <pre>
 * class X {
 * 	public void doSomething(Lock lock) {
 *         try {
 *             // acquire the Lock before the body
 *             lock.acquire();
 *   
 *             try {
 *                 // ... method body
 *             }
 *             finally {
 *                 // release the Lock after the body
 *                 lock.release()
 *             }
 *         }
 *         catch(InterruptedException ex) {
 *             // ... evasive action
 *         }
 *     }
 * 
 * 	public void doSomethingElse(Lock lock) {
 *         try {
 *             // try the condition for 10 ms
 *             if(lock.attempt(10)) {
 *                 try {
 *                     // ... method body
 *                 }
 *                 finally {
 *                     lock.release()
 *                 }
 *             }
 *         }
 *         catch (InterruptedException ex) {
 *             // ... evasive action
 *         }
 *     }
 * }
 * </pre>
 * 
 * @see Mutex
 * @see Semaphore
 * @see java.util.concurrent.Semaphore
 * @see FIFOSemaphore
 */
public interface Lock {
	/**
	 * Acquires this lock, blocking until it is available, or the thread is interrupted.
	 * 
	 * @exception <code>InterruptedException</code> if the calling thread is interrupted while waiting. On
	 * failure, you can be sure that this <code>Lock</code> has not been acquired, and that no corresponding
	 * <code>release</code> should be performed. Conversely, a normal return guarantees that the
	 * <code>acquire</code> was successful.
	 */
	public void acquire() throws InterruptedException;

	/**
	 * Acquires this lock, if it becomes available within the given waiting time.
	 * 
	 * @param msecs the number of milliseconds to wait for the lock before giving up. if msecs is <= 0 then no
	 * wait will occurs and <code>attempt</code> will return immediately.
	 * @return <code>true</code> if the Lock has been acquired within the given time, <code>false</code>
	 * otherwise.
	 * @exception <code>InterruptedException</code> if the calling thread is interrupted while waiting. On
	 * failure, you can be sure that this <code>Lock</code> has not been acquired, and that no corresponding
	 * <code>release</code> should be performed. Conversely, a normal return guarantees that the
	 * <code>acquire</code> was successful.
	 */
	public boolean attempt(long msecs) throws InterruptedException;

	/**
	 * Release this lock, potentially re-enable one blocked thread. If any threads are blocked trying to
	 * acquire this lock, then one is selected and given the lock that was just released. That thread is
	 * re-enabled for thread scheduling purposes.
	 * <p>
	 * <code>release</code> do not raise exceptions, thus it can be used in `finally` clauses without
	 * requiring extra try/catch blocks.
	 */
	public void release();
}

// ----- THAT'S ALL FOLKS -----
