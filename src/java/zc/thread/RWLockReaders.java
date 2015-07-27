// ----------------------------------------------------------------------------
//  RWLockReaders.java
//	ZCThread Library
//
//	(c) Copyright Zart Colwing, 2002-2003. All rights reserved.
// ----------------------------------------------------------------------------

package zc.thread;


// ----------------------------------------------------------------------------
//		RWLockReaders - class
// ----------------------------------------------------------------------------
/**
 * A RWLock that gives priority to <i>Readers</i>.
 * <p>
 * Read access is only granted when there is no active <i>Writers</i>
 * and write access is only granted when there is no active <i>Readers</i>
 * and no active <i>Writers</i>.
 * <p>
 * On a heavily loaded system it is possible that the number of <i>Readers</i>
 * never drop to zero and consequently the RWLockReaders denies access to waiting <i>Writers</i>.
 *
 * @see "Concurrency - State Models & Java Programs, chap: 7.5, page: 144"
 * @see RWLockWriters
 */
public class RWLockReaders implements RWLock {

	protected volatile int fReaderCount = 0;
	protected volatile boolean fWritingFlag = false;


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

		synchronized(this) {
			while(!safe_to_write()) {
				this.wait();
			}

			fWritingFlag = true;
		}
	}

	// ----------------------------------------------------------------------------
	//		release
	// ----------------------------------------------------------------------------
	public void release() {
		synchronized(this) {
			if(fWritingFlag == true) {
				fWritingFlag = false;
				this.notifyAll();
			}
			else if(fReaderCount > 0) {
				if((--fReaderCount) == 0) {
					this.notify();
				}
			}
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
	 * </ul>
	 * <p>
	 * <b>Must imperatively be called from a synchronized block.</b>
	 *
	 */
	protected boolean safe_to_read() {
		return fWritingFlag == false;
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
		return fWritingFlag == false && fReaderCount == 0;
	}
}

// ----- THAT'S ALL FOLKS -----------------------------------------------------
