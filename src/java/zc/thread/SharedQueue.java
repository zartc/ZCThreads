// ----------------------------------------------------------------------------
//  SharedQueue.java
//	ZCThread Library
//
//	(c) Copyright Zart Colwing, 2002-2003. All rights reserved.
// ----------------------------------------------------------------------------

package zc.thread;


// ----------------------------------------------------------------------------
//		SharedQueue - interface
// ----------------------------------------------------------------------------
/**
 * A fully synchronized object container aimed for inter-thread communication.
 * SharedQueue are frequently used in concurrent systems to smooth out
 * information transfer rates between the producers of data and the consumers
 * of that data.
 * <p>
 * <i>This container is often known as a <b>Buffer</b> or a <b>BoundedBuffer</b>,
 * but the <b>SharedQueue</b> name emphasize the FIFO nature of the container and
 * the fact that it is shared between multiple threads.</i>
 *
 */
public interface SharedQueue {

	// ----------------------------------------------------------------------------
	//		clear
	// ----------------------------------------------------------------------------
	/**
	 * Removes all the objects from this SharedQueue.
	 * <p>
	 * The SharedQueue will be empty after this call returns.
	 */
	public void clear();

	// ----------------------------------------------------------------------------
	//		add
	// ----------------------------------------------------------------------------
	/**
	 * Add an object into this SharedQueue and notify waiting threads.
	 * May block the calling thread if the queue has reach its maximum capacity.
	 * @param inObject the object to enqueue.
	 * @exception InterruptedException if the calling thread is interrupted while waiting.
	 * @exception IllegalArgumentException if inObject is null.
	 */
	public void add(Object inObject) throws InterruptedException;

	// ----------------------------------------------------------------------------
	//		remove
	// ----------------------------------------------------------------------------
	/**
	 * Remove and return the "next" available object from this SharedQueue and notify waiting threads.
	 * May block the calling thread if the queue is empty.
	 * @return the "next" object in the queue.
	 * @exception InterruptedException if the calling thread is interrupted while waiting.
	 */
	public Object remove() throws InterruptedException;

	// ----------------------------------------------------------------------------
	//		capacity
	// ----------------------------------------------------------------------------
	/**
	 * Return the maximum number of object that can be held in this SharedQueue,
	 * that is the capacity beyond which {@link #add} will block.
	 * @return an int > zero.
	 */
	public int capacity();

	// ----------------------------------------------------------------------------
	//		size
	// ----------------------------------------------------------------------------
	/**
	 * Returns the number of objects currently enqueued in this SharedQueue.
	 * <p>
	 * <b>NOTE:</b> This is only a snapshot value, that may have changed before returning.
	 *
	 * @return an int >= zero.
	 */
	public int size();

	// ----------------------------------------------------------------------------
	//		isEmpty
	// ----------------------------------------------------------------------------
	/**
	 * Tests if this SharedQueue is currently empty.
	 * <p>
	 * <b>NOTE:</b> This is only a snapshot value, that may have changed before returning.
	 *
	 * @return  <code>true</code> if and only if this SharedQueue has
	 *          no objects, that is, its size is zero;
	 *          <code>false</code> otherwise.
	 */
	public boolean isEmpty();

	// ----------------------------------------------------------------------------
	//		isFull
	// ----------------------------------------------------------------------------
	/**
	 * Tests if this SharedQueue is currently full.
	 * <p>
	 * <b>NOTE:</b> This is only a snapshot value, that may have changed before returning.
	 *
	 * @return  <code>true</code> if and only if this SharedQueue has
	 *          no free slots, that is, no more objects can be put into it;
	 *          <code>false</code> otherwise.
	 */
	public boolean isFull();

}

// ----- THAT'S ALL FOLKS -----------------------------------------------------
