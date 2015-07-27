// ----------------------------------------------------------------------------
//  AbstractSharedQueue.java
//	ZCThread Library
//
//	(c) Copyright Zart Colwing, 2002-2003. All rights reserved.
// ----------------------------------------------------------------------------

package zc.thread;


// ----------------------------------------------------------------------------
//		AbstractSharedQueue - abstract class
// ----------------------------------------------------------------------------
/**
 * Base implementation for derived SharedQueue classes.
 */
public abstract class AbstractSharedQueue implements SharedQueue {

	protected final int fMaxCapacity;


	// ----------------------------------------------------------------------------
	//		AbstractSharedQueue - constructor
	// ----------------------------------------------------------------------------
	/**
	 * Construct a AbstractSharedQueue of capacity <code>inMaxCapacity</code>
	 * @param inMaxCapacity
	 * @throws IllegalArgumentException
	 */
	public AbstractSharedQueue(int inMaxCapacity) {
		if(inMaxCapacity < 1) {
			throw new IllegalArgumentException("illegal capacity " + inMaxCapacity);
		}

		fMaxCapacity = inMaxCapacity;
	}

	// ----------------------------------------------------------------------------
	//		add
	// ----------------------------------------------------------------------------
	public void add(Object inObject) throws InterruptedException {
		if(Thread.interrupted()) {
			throw new InterruptedException();
		}
		if(inObject == null) {
			throw new IllegalArgumentException("attempt to enqueue a null object");
		}

		synchronized(this) {
			while(isFull()) {
				// The queue is full, wait until a Consumer
				// remove an object thus making space available.
				// If we are interrupted, the exception is propagated to the caller.
				this.wait();
			}

			// Once we grab the lock AND the queue is not full we enqueue this object.
			enqueue(inObject);

			// Notify the threads that are waiting for available object.
			this.notifyAll();
		}
	}

	// ----------------------------------------------------------------------------
	//		remove
	// ----------------------------------------------------------------------------
	public Object remove() throws InterruptedException {
		if(Thread.interrupted()) {
			throw new InterruptedException();
		}

		synchronized(this) {
			while(isEmpty()) {
				// The queue is empty, wait until a producer add an object to the queue.
				// If we are interrupted, the exception is propagated to the caller.
				this.wait();
			}

			// Dequeue the oldest object.
			Object theObject = dequeue();

			// Notify the threads that are waiting for a free slot.
			this.notifyAll();

			return theObject;
		}
	}

	// ----------------------------------------------------------------------------
	//		isEmpty
	// ----------------------------------------------------------------------------
	public boolean isEmpty() {
		return size() == 0;
	}

	// ----------------------------------------------------------------------------
	//		isFull
	// ----------------------------------------------------------------------------
	public boolean isFull() {
		return size() >= capacity();
	}

	// ----------------------------------------------------------------------------
	//		capacity
	// ----------------------------------------------------------------------------
	public int capacity() {
		return fMaxCapacity;
	}

	// ----------------------------------------------------------------------------
	//		size
	// ----------------------------------------------------------------------------
	public abstract int size();

	// ----------------------------------------------------------------------------
	//		enqueue
	// ----------------------------------------------------------------------------
	/**
	 * Put an object into the next free slot and adjust the cursors.
	 * <p>
	 * <b>Must imperatively be called from a synchronized block.</b>
	 * @param inObject the object to put in the SharedQueue. null object are not allowed.
	 */
	protected abstract void enqueue(Object inObject);

	// ----------------------------------------------------------------------------
	//		dequeue
	// ----------------------------------------------------------------------------
	/**
	 * Remove the oldest object from the queue and adjust the cursors.
	 * <p>
	 * <b>Must imperatively be called from a synchronized block.</b>
	 * @return The oldest Object in the queue.
	 */
	protected abstract Object dequeue();
}

// ----- THAT'S ALL FOLKS -----------------------------------------------------
