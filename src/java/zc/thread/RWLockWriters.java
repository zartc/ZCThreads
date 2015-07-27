// ----------------------------------------------------------------------------
//  RWLockWriters.java
//	ZCThread Library
//
//	(c) Copyright Zart Colwing, 2002-2003. All rights reserved.
// ----------------------------------------------------------------------------

package zc.thread;

import java.util.LinkedList;


// ----------------------------------------------------------------------------
//		RWLockWriters - class
// ----------------------------------------------------------------------------
/**
 * A RWLock that gives priority to <i>Writers</i>.
 * <p>
 * Read access is only granted when there is no <i>Writers</i> at all (active or waiting)
 * and write access is only granted when there is no active <i>Readers</i>
 * and no active <i>Writers</i>.
 * <p>
 * Pending write requests take precedence over all read requests, so any <i>Writer</i>
 * that is waiting while <i>Readers</i> are still executing will execute before any
 * subsequent <i>Readers</i> execute.
 * <p>
 * Writes are guaranteed to execute in the order that they were
 * requested, the oldest request is processed first.
 * @see "Tom Cargill paper on
 * <a href="http://www.profcon.com/cargill/jgf/9809/SpecificNotification.html">
 * Specific Notification for Java Thread Synchronization</a>"
 * @see "Concurrency - State Models & Java Programs, chap: 7.5, page: 144"
 * @see RWLockReaders
 */
public class RWLockWriters implements RWLock {

	/** Count the number of active Readers */
	private volatile int fReaderCount = 0;

	/**
	 * Refer to the Thread that currently has the write lock.
	 * null if there is currently no active Writer.
	 */
	private volatile SNL fActiveWriter = null;

	/**
	 * This list is used to release the Writers in the order received.
	 * The size of the list also serves as the "waiting writers" count.
	 * <p><b>Part of the Target Notification Pattern.</b>
	 */
	private final LinkedList fSNLQ = new LinkedList();


	/** Specific Notification Lock */
	private final static class SNL {
		Thread fThread;
		// ----------------------------------------------------------------------------
		//		SNL - constructor
		// ----------------------------------------------------------------------------
		SNL(Thread inThread) {
			fThread = inThread;
		}
	}

	// ----------------------------------------------------------------------------
	//		acquireRead
	// ----------------------------------------------------------------------------
	public void acquireRead() throws InterruptedException {
		if(Thread.interrupted()) {
			throw new InterruptedException();
		}

		synchronized(this) {
			// Block all incoming Readers while it is not safe to read.
			while(!safe_to_read()) {
				this.wait();
			}

			++fReaderCount;
		}
	}

	// ----------------------------------------------------------------------------
	//		acquireWrite
	// ----------------------------------------------------------------------------
	public void acquireWrite() throws InterruptedException {
		if(Thread.interrupted()) {
			throw new InterruptedException();
		}

		// :NOTE: encapsulate Thread.currentThread() inside a unique object
		// to make sure no one could inadvertently call wait() or notify().
		SNL theSNL = new SNL(Thread.currentThread());

		// :NOTE: it is mandatory to synchronize on the local theSNL object
		// in order to prevent a possible race condition at point P below.
		synchronized(theSNL) {
			synchronized(this) {
				if(safe_to_write() && fSNLQ.size() == 0) {
					fActiveWriter = theSNL;
					return;
				}

				fSNLQ.addLast(theSNL);
			}

			// point P:
			// If we where not synchronized on theSNL then a possible
			// race condition between this function and notify_writer_or_readers would
			// lead to a deadlock:
			// -Imagine that at point P the current Thread is scheduled out
			//  in favor to one of the active Reader, and than that Reader
			//  call doneRead().
			// -Imagine that this was the last active Reader and thus doneRead()
			//  calls notify_writer_or_readers() to wake up the waiting Writers.
			// -Imagine that our thread (the one that just happen to be scheduled out)
			//  was the unique Writer waiting on the list.
			// Then notify_writer_or_readers() would notify us before we had a chance to put
			// ourself in the wait state.
			// Then when we latter are scheduled back in, we will put ourself in the
			// wait state and fall in deep comma because there would be no one left
			// to wake us up (lost notification syndrome).

			theSNL.wait();
		}
	}

	// ----------------------------------------------------------------------------
	//		release
	// ----------------------------------------------------------------------------
	public void release() {
		synchronized(this) {
			if(fReaderCount > 0) {
				if((--fReaderCount) == 0) {
					// No more Readers, notify the next Writer in the list.
					notify_writer_or_readers();
				}
			}
			else if(fActiveWriter != null) {
				if(fActiveWriter.fThread != Thread.currentThread()) {
					throw new IllegalMonitorStateException("current thread not owner of the RWLock");
				}

				fActiveWriter = null;
				notify_writer_or_readers();
			}
		}
	}

	// ----------------------------------------------------------------------------
	//		notify_writer_or_readers
	// ----------------------------------------------------------------------------
	/**
	 * Notify the <i>Writer</i> that has been waiting the longest or if there is none,
	 * notify all waiting <i>Readers</i>.
	 * <p>
	 * Use the <b>Target Notification Pattern</b> to issue a notification to the
	 * oldest thread that needs to be awaken.
	 * <p>
	 * <b>Must imperatively be called from a synchronized block.</b>
	 */
	private void notify_writer_or_readers() {
		if(fSNLQ.size() > 0) {
			// Notify the next Writer in the list.
			Object theOldestSNL = fSNLQ.removeFirst();
			fActiveWriter = (SNL)theOldestSNL;
			synchronized(theOldestSNL) {
				theOldestSNL.notify();
			}
		}
		else {
			// Notify all waiting Readers.
			this.notifyAll();
		}
	}

	// ----------------------------------------------------------------------------
	//		safe_to_read
	// ----------------------------------------------------------------------------
	/**
	 * Return true when the conditions to accept a new <i>Reader</i> are met.
	 * <p>
	 * For this implementation the conditions are:
	 * <ul>
	 *	<li>There is no active <i>Writer</i>
	 *	<li>There is no waiting <i>Writer</i>
	 * </ul>
	 * <p>
	 * <b>Must imperatively be called from a synchronized block.</b>
	 *
	 */
	protected boolean safe_to_read() {
		return fActiveWriter == null && fSNLQ.size() == 0;
	}

	// ----------------------------------------------------------------------------
	//		safe_to_write
	// ----------------------------------------------------------------------------
	/**
	 * Returns true when the condition to accept a new <i>Writer</i> are met.
	 * <p>
	 * For this implementation the conditions are:
	 * <ul>
	 *	<li>There is no active <i>Writer</i>
	 *	<li>There is no active <i>Reader</i>
	 * </ul>
	 * <p>
	 * <b>Must imperatively be called from a synchronized block.</b>
	 */
	protected boolean safe_to_write() {
		return fActiveWriter == null && fReaderCount == 0;
	}
}

// ----- THAT'S ALL FOLKS -----------------------------------------------------
