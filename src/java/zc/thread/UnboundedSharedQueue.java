// ----------------------------------------------------------------------------
//  UnboundedSharedQueue.java
//	ZCThread Library
//
//	(c) Copyright Zart Colwing, 2002-2003. All rights reserved.
// ----------------------------------------------------------------------------

package zc.thread;

import java.util.*;


// ----------------------------------------------------------------------------
//		UnboundedSharedQueue - class
// ----------------------------------------------------------------------------
/**
 * A SharedQueue that have no maximum capacity.
 *
 * @deprecated Conceptually an UnboundedSharedQueue should be, well, unbounded,
 * but in real world  an unbounded queue that grow continually will certainly
 * leads to disasters. Thus UnboundedSharedQueue are simulated with a
 * {@link BoundedSharedQueue} of a sufficiently large capacity.
 */
public class UnboundedSharedQueue extends AbstractSharedQueue {

	protected final LinkedList fList = new LinkedList();


	// ----------------------------------------------------------------------------
	//		UnboundedSharedQueue - constructor
	// ----------------------------------------------------------------------------
	/**
	 * Construct a SharedQueue.
	 * <p>
	 * By providing a large but reasonable
	 * value for inMaxCapacity we can prevent the queue to grow out of control.
	 * @param inMaxCapacity the maximum allowable capacity for the queue.
	 * @exception IllegalArgumentException if inMaxCapacity argument is less than one.
	 */
	public UnboundedSharedQueue(int inMaxCapacity) {
		super(inMaxCapacity);
	}

	// ----------------------------------------------------------------------------
	//		UnboundedSharedQueue - constructor
	// ----------------------------------------------------------------------------
	/**
	 * Construct an UnboundedSharedQueue.
	 */
	public UnboundedSharedQueue() {
		super(1024 * 8);
	}

	// ----------------------------------------------------------------------------
	//		finalize
	// ----------------------------------------------------------------------------
	public void finalize() throws Throwable {
		// Do not call the clear member function as it notify threads.
		fList.clear();
		super.finalize();
	}

	// ----------------------------------------------------------------------------
	//		clear
	// ----------------------------------------------------------------------------
	/**
	 * Removes all the objects from this SharedQueue.
	 * All waiting threads will be notified.
	 * <p>
	 * The SharedQueue will be empty after this call returns.
	 */
	public synchronized void clear() {
		fList.clear();
		// Notify threads that are waiting for a free slot.
		this.notifyAll();
	}

	// ----------------------------------------------------------------------------
	//		size
	// ----------------------------------------------------------------------------
	public int size() {
		return fList.size();
	}

	// ----------------------------------------------------------------------------
	//		enqueue
	// ----------------------------------------------------------------------------
	protected void enqueue(Object inObject) {
		fList.addLast(inObject);
	}

	// ----------------------------------------------------------------------------
	//		dequeue
	// ----------------------------------------------------------------------------
	protected Object dequeue() {
		return fList.removeFirst();
	}
}

// ----- THAT'S ALL FOLKS -----------------------------------------------------
