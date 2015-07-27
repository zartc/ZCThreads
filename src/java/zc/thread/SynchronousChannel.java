// ----------------------------------------------------------------------------
//  SynchronousChannel.java
//	ZCThread Library
//
//	(c) Copyright Zart Colwing, 2002-2003. All rights reserved.
// ----------------------------------------------------------------------------

package zc.thread;


// ----------------------------------------------------------------------------
//		SynchronousChannel - class
// ----------------------------------------------------------------------------
/**
 * Implements a synchronous message passing channel.
 * <p>
 * A SynchronousChannel, also known as a <i>Rendez-Vous</i>, connects two and only two
 * threads; A single thread, the <i>Sender</i>, can <code>send</code> to the channel
 * and a single thread, the <i>Receiver</i>, can <code>receive</code> from the channel;
 * Communication is said to be <b>one-to-one</b>.
 * <p>
 * The first process, whether sender or receiver, to perform a channel operation is
 * blocked until its partner perform the complementary operation. After a communication
 * occurs, sender and receiver can proceed independently. This form of message
 * passing is termed synchronous, since the sender and receiver must be exactly
 * synchronized for communication to occur.
 * <p>
 * Another way of thinking of synchronous communication is that it implements
 * a distributed assignment in which the sender's expression is assigned to the
 * receiver's local variable.
 * <p>
 * <b>Keep in mind</b> that the ownership of the message object is transferred from
 * the sender to the receiver. Consequently the sender should not access a message
 * object after it has been sent through a Channel. If the sender need to reference
 * a message object in the future it should copy it before sending it.
 *
 * @see Port
 */
public class SynchronousChannel implements Channel {

	protected volatile Object fMessage = null;


	// ----------------------------------------------------------------------------
	//		send
	// ----------------------------------------------------------------------------
	/**
	 * Send a message through this Channel. The Sender is blocked
	 * until the message is received on the other side of this Channel.
	 * @param inMessage the message to send (null is not allowed).
	 * @exception InterruptedException if the calling thread is interrupted while waiting.
	 * @exception IllegalArgumentException if inMessage is null.
	 */
	public void send(Object inMessage) throws InterruptedException {
		if(Thread.interrupted()) {
			throw new InterruptedException();
		}
		if(inMessage == null) {
			throw new IllegalArgumentException("attempt to send a null message");
		}

		synchronized(this) {
			while(fMessage != null) {
				this.wait();
			}

			fMessage = inMessage;

			this.notifyAll();
		}
	}

	// ----------------------------------------------------------------------------
	//		receive
	// ----------------------------------------------------------------------------
	/**
	 * Wait for a message from this Channel. The Receiver is blocked until
	 * a message is sent from the other side of this Channel.
	 * @return an Object the message received (null object is not possible).
	 * @exception InterruptedException if the calling thread is interrupted while waiting.
	 */
	public Object receive() throws InterruptedException {
		if(Thread.interrupted()) {
			throw new InterruptedException();
		}

		synchronized(this) {
			while(fMessage == null) {
				this.wait();
			}

			Object theMessage = fMessage;
			fMessage = null;

			this.notifyAll();

			return theMessage;
		}
	}
}

// ----- THAT'S ALL FOLKS -----------------------------------------------------
