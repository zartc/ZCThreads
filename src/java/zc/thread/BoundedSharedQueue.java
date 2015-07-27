// ----------------------------------------------------------------------------
//  BoundedSharedQueue.java
//	ZCThread Library
//
//	(c) Copyright Zart Colwing, 2002-2003. All rights reserved.
// ----------------------------------------------------------------------------

package zc.thread;


// ----------------------------------------------------------------------------
//		BoundedSharedQueue - class
// ----------------------------------------------------------------------------
/**
 * A SharedQueue that have a maximum capacity.
 * <p>
 * NOTE: Despite this class name, there is no UnboundedSharedQueue class in the
 * package. This is so because "unbounded" is rarely a possibility in limited computer
 * memory. UnboundedSharedQueue can be simulated by constructing a BoundedSharedQueue
 * with a large capacity.
 * <p>
 * The algorithm avoids contention between puts and gets when the queue is not empty.
 * Normally a put and a get can proceed simultaneously, although it does not allow
 * multiple concurrent puts or gets.
 * <p>
 * <code>{@link #add}</code> request will block when the maximum capacity is reached.
 *
 * @see UnboundedSharedQueue
 */
public class BoundedSharedQueue extends AbstractSharedQueue {

	public final static int LARGE_CAPACITY = 8 * 1024;
	
	protected final Object[] fQueue;
	private volatile int fObjectCount = 0;
	private volatile int fPutCursor = 0, fGetCursor = 0;


	// ----------------------------------------------------------------------------
	//		BoundedSharedQueue - constructor
	// ----------------------------------------------------------------------------
	/**
	 * Construct a SharedQueue with the given maximum capacity.
	 * @param inMaxCapacity the maximum number of object that this SharedQueue can
	 * contain before blocking.
	 * @exception IllegalArgumentException if inCapacity is less than one.
	 */
	public BoundedSharedQueue(int inMaxCapacity) {
		super(inMaxCapacity);
		fQueue = new Object[inMaxCapacity];
	}

	// ----------------------------------------------------------------------------
	//		UnboundedSharedQueue - constructor
	// ----------------------------------------------------------------------------
	/**
	 * Construct an SharedQueue with a sufficiently large capacity so that to
	 * conceptually simulate an UnboundedSharedQueue.<p>
	 * The maximum capacity of this SharedQueue will be 8192 objects.
	 */
	public BoundedSharedQueue() {
		this(LARGE_CAPACITY);
	}
	
	// ----------------------------------------------------------------------------
	//		finalize
	// ----------------------------------------------------------------------------
	public void finalize() throws Throwable {
		// Do not call the clear member function as it notify threads.
		for(int i = 0; i < fQueue.length; ++i) {
			fQueue[i] = null;
		}
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
		for(int i = 0; i < fQueue.length; ++i) {
			fQueue[i] = null;
		}

		fObjectCount = fPutCursor = fGetCursor = 0;

		// Notify threads that are waiting for a free slot.
		this.notifyAll();
	}

	// ----------------------------------------------------------------------------
	//		size
	// ----------------------------------------------------------------------------
	public int size() {
		return fObjectCount;
	}

	// ----------------------------------------------------------------------------
	//		enqueue
	// ----------------------------------------------------------------------------
	protected void enqueue(Object inObject) {
		fQueue[fPutCursor] = inObject;
		fPutCursor = (fPutCursor + 1) % capacity();
		++fObjectCount;
	}

	// ----------------------------------------------------------------------------
	//		dequeue
	// ----------------------------------------------------------------------------
	protected Object dequeue() {
		Object theObject = fQueue[fGetCursor];
		fQueue[fGetCursor] = null;	// For the garbage collector.
		fGetCursor = (fGetCursor + 1) % capacity();
		--fObjectCount;

		return theObject;
	}


	// ----------------------------------------------------------------------------
	//		toString
	// ----------------------------------------------------------------------------
	public synchronized String toString() {
		StringBuffer theResult = new StringBuffer("BoundedSharedQueue(");

		theResult.append("p=").append(fPutCursor);
		theResult.append(",g=").append(fGetCursor);
		theResult.append(",#=").append(fObjectCount);
		theResult.append(")[");

		if(fQueue.length > 0) {
			theResult.append(fQueue[0]);

			for(int i = 1; i < fQueue.length; ++i) {
				theResult.append(";");
				theResult.append(fQueue[i]);
			}
		}

		theResult.append("]");

		return theResult.toString();
	}
}

// ----- THAT'S ALL FOLKS -----------------------------------------------------
