// ----------------------------------------------------------------------------
//  Barrier.java
//	ZCThread Library
//
//	(c) Copyright Zart Colwing, 2002-2003. All rights reserved.
// ----------------------------------------------------------------------------

package zc.thread;


// ----------------------------------------------------------------------------
//		Barrier - class
// ----------------------------------------------------------------------------
/**
 * Barriers serve as synchronization points for a group of threads that
 * must occasionally wait for each other. All threads must arrive at the
 * barrier before any of them are permitted to proceed past the barrier.
 * <ul>
 *   <li>A barrier is defined for a given group of <i>participants</i>
 *     i.e. the group of threads that must meet at the barrier point.
 *   <li>Participant can echange information at each synchronization point.
 *   <li>A barrier becomes <i>broken</i> if one or more threads leave
 *     a barrier point prematurely, generally due to interruption or timeout.
 * </ul>
 * <p>
 * If a Thread which is not a registered participant in the barrier attempts to
 * await at the barrier it is rejected with a IllegalMonitorStateException, the
 * Barrier is not broken by such a intruder attempt.
 */
class Barrier {

	/** the array of participants */
	protected final Thread[] fParticipants;
	/** the array of objects bring by each participant at synchronization points */
	protected final Object[] fObjectArray;

	protected int fLatestParticipant = 0;
	protected Barrier.Command fCommand = null;
	protected BrokenBarrierException fException = null;

	private int fSpinCounter = 0;


	// ----------------------------------------------------------------------------
	//		Command - inner interface
	// ----------------------------------------------------------------------------
	/**
	 * Interface for functions to run at synchronization points.
	 */
	public interface Command {
		// ----------------------------------------------------------------------------
		//		run
		// ----------------------------------------------------------------------------
		/**
		 * Perform some function on the objects presented at synchronization points.
		 * The objects array holds all presented items; one per thread.
		 * Its length is the number of participants.
		 * The array is ordered by arrival into the synchronization points.
		 * So, the last element (at objects[objects.length-1]) is guaranteed to have
		 * been presented by the thread performing this function. No identifying
		 * information is otherwise kept about which thread presented which item.
		 * If you need to trace origins, you will need to use an object type
		 * that includes identifying information.
		 * After return of this function, other threads are released, and each
		 * returns with the item with the same index as the one it presented.
		 */
		public void run(Object[] objects);
	}

	// ----------------------------------------------------------------------------
	//		Rotator - inner class
	// ----------------------------------------------------------------------------
	/**
	 * Rotates the array of result objects so that each thread gets the Object
	 * presented by the thread next to it.
	 * This Command is particularly adapted for the Resource-Exchanger pattern.
	 */
	public final static class Rotator implements Barrier.Command {
		// ----------------------------------------------------------------------------
		//		run
		// ----------------------------------------------------------------------------
		/**
		 * Rotate the array
		 */
		public void run(Object[] inObjects) {
			int theLastIndex = inObjects.length - 1;
			Object theFirstObject = inObjects[0];

			for(int i = 0; i < theLastIndex; ++i) {
				inObjects[i] = inObjects[i+1];
			}


			inObjects[theLastIndex] = theFirstObject;
		}
	}



	// ----------------------------------------------------------------------------
	//		Barrier - constructor
	// ----------------------------------------------------------------------------
	/**
	 * Construct a Barrier object for the given list of participants.
	 * No command will be run a synchronization points.
	 * <p><i>The participant list should contains at least two participants.</i>
	 */
	public Barrier(Thread[] inParticipants) {
		this(inParticipants, null);
	}

	// ----------------------------------------------------------------------------
	//		Barrier - constructor
	// ----------------------------------------------------------------------------
	/**
	 * Construct a Barrier object for the given list of participants.
	 * The given command will be run a synchronization points.
	 * <p><i>The participant list should contains at least two participants.</i>
	 */
	public Barrier(Thread[] inParticipants, Barrier.Command inCommand) {
		if(inParticipants == null) {
			throw new IllegalArgumentException("null participant list");
		}

		if(inParticipants.length < 2) {
			throw new IllegalArgumentException("not enough participant " + inParticipants.length);
		}

		fParticipants = inParticipants;
		fObjectArray = new Object[inParticipants.length];
		fCommand = inCommand;
	}

	// ----------------------------------------------------------------------------
	//		getCommand
	// ----------------------------------------------------------------------------
	public Barrier.Command getCommand() {
		return fCommand;
	}

	// ----------------------------------------------------------------------------
	//		setCommand
	// ----------------------------------------------------------------------------
	/**
	 * Set the command to execute when the last participant reach the Barrier.
	 * The Command is executed exactly once, by the last Participant Thread
	 * that reach the Barrier. The Command is not executed if the Barrier is broken.
	 * @param inCommand the command to execute. If null, no command will be executed.
	 */
	public synchronized void setCommand(Barrier.Command inCommand) {
		fCommand = inCommand;
	}

	// ----------------------------------------------------------------------------
	//		getParticipantCount
	// ----------------------------------------------------------------------------
	/**
	 * Return the number of participants that must meet at the Barrier.
	 * The number of participants is always at least 1.
	 * @return an int.
	 */
	public int getParticipantCount() {
		return fParticipants.length;
	}

	// ----------------------------------------------------------------------------
	//		getLateArrivalCount
	// ----------------------------------------------------------------------------
	/**
	 * Get the number of participant that did not yet reached the Barrier.
	 * <p><i>NOTE: when the last of these late arrival will reach the Barrier,
	 * then the Barrier will be notified.</i>
	 * @return an int.
	 */
	public synchronized int getLateArrivalCount() {
		return getParticipantCount() - fLatestParticipant;
	}

	// ----------------------------------------------------------------------------
	//		isBroken
	// ----------------------------------------------------------------------------
	/**
	 * Returns true if the Barrier has been compromised by threads leaving the
	 * Barrier early (normally due to interruption or timeout).
	 * <p><i>NOTE: Barrier methods throw BrokenBarrierException upon detection of breakage.</i>
	 */
	public synchronized boolean isBroken() {
		return fException != null;
	}

	// ----------------------------------------------------------------------------
	//		getSpinCounter
	// ----------------------------------------------------------------------------
	/**
	 * Return the number of time the Barrier has been reached.
	 */
	public int getSpinCounter() {
		return fSpinCounter;
	}


	// ----------------------------------------------------------------------------
	//		await
	// ----------------------------------------------------------------------------
	/**
	 * Enter Barrier and wait for the other participants to enter the Barrier at their turn.
	 * @exception InterruptedException if this thread is interrupted while waiting.
	 * @exception BrokenBarrierException if the Barrier is broken while this thread is waiting.
	 * @exception RuntimeException throw at an unregistered thread that attempt to
	 * penetrate the Barrier. Only the faulty thread receive this exception and the Barrier
	 * is not broken by such an illegal attempt.
	 **/
	public synchronized void await() throws InterruptedException, BrokenBarrierException {
		// Check that the calling Thread is a registered participant
		if(is_registered_participant(Thread.currentThread()) == false) {
			// Unregistered Threads are rejected with a RuntimeException
			// but the Barrier itself is not considered broken by such an attempt.
			throw new RuntimeException("not a registered participant");
		}

		// Make sure the Thread is not interrupted
		if(Thread.interrupted()) {
			release();
			throw new InterruptedException();
		}

		// Check the Barrier before waiting.
		if(isBroken()) {
			throw fException;
		}

		enter_waiting();

		// Check again after waiting.
		if(isBroken()) {
			throw fException;
		}
	}

	// ----------------------------------------------------------------------------
	//		await
	// ----------------------------------------------------------------------------
	/**
	 * Enter Barrier and wait for the other participants to enter the Barrier at their turn.
	 * @return the Object affected to this Thread by the Command executed at the synchronization point.
	 * @exception InterruptedException if this thread is interrupted while waiting.
	 * @exception BrokenBarrierException if the Barrier is broken while this thread is waiting.
	 * @exception RuntimeException throw at an unregistered thread that attempt to
	 * penetrate the Barrier. Only the faulty thread receive this exception and the Barrier
	 * is not broken by such an illegal attempt.
	 */
	public synchronized Object await(Object inObject) throws InterruptedException, BrokenBarrierException {
		// Check-in the given Object
		int theCheckInIndex = fLatestParticipant;
		fObjectArray[theCheckInIndex] = inObject;

		this.await();

		Object theResult = fObjectArray[theCheckInIndex];
		fObjectArray[theCheckInIndex] = null;

		return theResult;
	}

	// ----------------------------------------------------------------------------
	//		release
	// ----------------------------------------------------------------------------
	/**
	 * Release all participants at the Barrier.
	 * All participants - waiting and late - will receive a BrokenBarrierException
	 * indicating that the Barrier synchronization has been broken.
	 */
	public synchronized void release() {
		release("Barrier released");
	}

	// ----------------------------------------------------------------------------
	//		release
	// ----------------------------------------------------------------------------
	/**
	 * Release all participants at the Barrier.
	 * All participants - those waiting and those still late - will receive a
	 * BrokenBarrierException indicating that the Barrier synchronization has been broken.
	 * @param inMessage the message bear by the generated BrokenBarrierException.
	 */
	public synchronized void release(String inMessage) {
		fException = new BrokenBarrierException(inMessage);
		this.notifyAll();
	}



	// ----------------------------------------------------------------------------
	//		enter_waiting
	// ----------------------------------------------------------------------------
	/**
	 * Put a participant Thread into the waiting state.
	 * The participant Thread will goes out of the waiting state once the last
	 * participant reach the Barrier or the Barrier is released.
	 */
	protected void enter_waiting() throws InterruptedException, BrokenBarrierException {
		int localIndex = ++ fLatestParticipant;

		try {
			if(fLatestParticipant == 1 && fSpinCounter > 0) {
				int localSpiner = fSpinCounter;
				// The first arrived participant has the duty of periodically
				// check the participant list to detect died Thread.
				while(localSpiner == fSpinCounter) {
					check_participants();
					// wait a quantum of time before re-checking participant list.
					this.wait(get_quantum());
				}
			}
			else if(fLatestParticipant == getParticipantCount()) {
				// The last arrived participant has the duty of executing the
				// Barrier command then to notify all the other participants.
				execute_command();
				++ fSpinCounter;

				// Reset fLatestParticipant to makes the Barrier cyclical.
				fLatestParticipant = 0;
				this.notifyAll();
			}
			else {
				// Wait for other participants to enter the Barrier.
				this.wait();
			}
		}
		catch(InterruptedException inException) {
			release();
			throw inException;
		}
	}

	// ----------------------------------------------------------------------------
	//		get_quantum
	// ----------------------------------------------------------------------------
	/**
	 * Get the quantum of time between two consecutives checks of the participant list.
	 * @return a long.
	 */
	protected long get_quantum() {
		return 2 * 1000L;
	}

	// ----------------------------------------------------------------------------
	//		execute_command
	// ----------------------------------------------------------------------------
	/**
	 * Execute the Barrier command when the last participant reach the Barrier.
	 * This function also provide a safety net around the user provided command
	 * that prevent unexpected and uncatched exceptions to bring SNAFU.
	 */
	protected void execute_command() {
		if(fCommand != null) {
			try {
				fCommand.run(fObjectArray);
			}
			catch(Throwable inException) {
				// Normally the Barrier.Command is not supposed to throw any Exception
				// but in case it does, we set a safety net that catch all possible throwable
				// exception and set the broken flag accordingly.
				release("Barrier command failed with a " + inException.getMessage());
			}
		}
	}

	// ----------------------------------------------------------------------------
	//		is_registered_participant
	// ----------------------------------------------------------------------------
	/**
	 * Check that a given Thread is a registered participant of this Barrier.
	 * @return true if the Thread is a registered participant false otherwise.
	 */
	protected boolean is_registered_participant(Thread inAttemptingThread) {
		for(int i = 0; i < fParticipants.length; ++i) {
			if(inAttemptingThread.equals(fParticipants[i])) {
				// Ok the attempting Thread is a registered participant.
				return true;
			}
		}

		return false;
	}

	// ----------------------------------------------------------------------------
	//		check_participants
	// ----------------------------------------------------------------------------
	/**
	 * Check that no participant have died since the last time we checked.
	 * Since a dead Thread is unlikely to reach the Barrier we need to check
	 * periodically for this situation in order to prevent starvation lock.
	 * (i.e. the other participants will wait indefinitely for the dead
	 * participant to reach the Barrier).
	 * If the function detect that a participant died, it release the Barrier.
	 */
	protected void check_participants() {
		for(int i = 0; i < fParticipants.length; ++i) {
			if(fParticipants[i].isAlive() == false) {
				// A participant Thread just died: the Barrier is broken.
				release();
			}
		}
	}
}

// ----- THAT'S ALL FOLKS -----------------------------------------------------
