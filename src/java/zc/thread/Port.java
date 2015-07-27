// ----------------------------------------------------------------------------
//  Port.java
//	ZCThread Library
//
//	(c) Copyright Zart Colwing, 2002-2003. All rights reserved.
// ----------------------------------------------------------------------------

package zc.thread;


// ----------------------------------------------------------------------------
//		Port - class
// ----------------------------------------------------------------------------
/**
 * Implements an asynchronous message passing channel.
 *
 * Many processes, the <i>Senders</i>, may send to a Port but only a single process,
 * the <i>Receiver</i>, may receive messages from it.
 * Communication is said to be <b>many-to-one</b>.
 * <p>
 * In asynchronous message passing the <code>send</code> operation does not block,
 * as in the synchronous scheme. Messages which have been sent but not received are
 * held in a message queue. <i>Senders</i> add messages to the tail of the queue and
 * a single <i>Receiver</i> removes messages from the head.
 * <p>
 * A Port is (conceptually) an unbounded FIFO queue of messages. This form of
 * communication is termed asynchronous since the <i>Senders</i> proceeds independently
 * of the <i>Receiver</i>. Synchronization only occurs when the <i>Receiver</i>
 * waits for a <i>Sender</i> if the queue of messages at the Port is empty.
 * <p>
 * If send operations can occurs more frequently than receive, then there
 * is (conceptually again) no upper bound on the length of the
 * queue required and consequently no upper bound on the amount of store required
 * to buffer messages. In real world however memory constraints impose a upper limit 
 * on the size of real queues, consequently a Port is based on an {@link BoundedSharedQueue}
 * retrofited to implements the {@link Channel} interface
 * <p>
 * <b>Keep in mind</b> that the ownership of the message object is transferred from
 * the sender to the receiver. Consequently the sender should not access a message
 * object after it has been sent through a Channel. If the sender need to reference
 * a message object in the future it should copy it before sending it.
 *
 * @see SynchronousChannel
 */
public class Port extends BoundedSharedQueue implements Channel {

	// ----------------------------------------------------------------------------
	//		Port - constructor
	// ----------------------------------------------------------------------------
	/**
	 * @param inMaxCapacity the maximum allowable capacity for the
	 * underlying {@link BoundedSharedQueue}.
	 */
	public Port(int inMaxCapacity) {
		super(inMaxCapacity);
	}

	// ----------------------------------------------------------------------------
	//		send
	// ----------------------------------------------------------------------------
	public void send(Object inObject) throws InterruptedException {
		super.add(inObject);
	}

	// ----------------------------------------------------------------------------
	//		receive
	// ----------------------------------------------------------------------------
	public Object receive() throws InterruptedException {
		return super.remove();
	}
}

// ----- THAT'S ALL FOLKS -----------------------------------------------------
