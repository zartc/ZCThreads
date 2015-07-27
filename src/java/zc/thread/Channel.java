// ----------------------------------------------------------------------------
//  Channel.java
//	ZCThread Library
//
//	(c) Copyright Zart Colwing, 2002-2003. All rights reserved.
// ----------------------------------------------------------------------------

package zc.thread;


// ----------------------------------------------------------------------------
//		Channel - interface
// ----------------------------------------------------------------------------
/**
 * Interface for message passing conduit between two threads.
 */
public interface Channel {

	// ----------------------------------------------------------------------------
	//		send
	// ----------------------------------------------------------------------------
	/**
	 * Send the message to the other side of this Channel.
	 * @param inMessage the message to send (null is not allowed).
	 * @throws InterruptedException if the calling thread is interrupted while waiting.
	 * @throws IllegalArgumentException if inMessage is null.
	 */
	public void send(Object inMessage) throws InterruptedException;

	// ----------------------------------------------------------------------------
	//		receive
	// ----------------------------------------------------------------------------
	/**
	 * Wait for a message from the other side of this Channel.
	 * The Receiver is blocked until a message is sent from the other side of this Channel.
	 * @return an Object the message received (null object is not possible).
	 * @exception InterruptedException if the calling thread is interrupted while waiting.
	 */
	public Object receive() throws InterruptedException;

	// ----------------------------------------------------------------------------
	//		close
	// ----------------------------------------------------------------------------
	/** @todo Add close semantic to the channel.($P=4$)
	 * The Channel may be closed by the Sender or the Receiver.
	 * When the Channel is closed the Sender receive a ClosedChannelException
	 * while the Receiver continue to receive available object in the Channel
	 * until the Channel is empty, at which time the Receiver will also receive
	 * a ClosedChannelException.
	 */
//	public void close();
}

// ----- THAT'S ALL FOLKS -----------------------------------------------------
