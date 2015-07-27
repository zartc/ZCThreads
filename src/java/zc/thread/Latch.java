// ----------------------------------------------------------------------------
//  Latch.java
//	ZCThread Library
//
//	(c) Copyright Zart Colwing, 2002-2003. All rights reserved.
// ----------------------------------------------------------------------------

package zc.thread;


// ----------------------------------------------------------------------------
//		Latch - class
// ----------------------------------------------------------------------------
/**
 * A latch is a specialized <code>CondVar</code> that once set cannot be reset ever.
 * <p>
 * <b>Sample usage.</b><br>Here are a set of classes that use
 * a <code>Latch</code> as a start signal for a group of worker threads that
 * are created and started beforehand, and then later enabled.
 * <pre>
 *  class Worker implements Runnable {
 *      private final Latch startSignal;
 *
 *      Worker(Latch l) {
 *          // This worker is synchronized
 *          // on the given Latch.
 *          startSignal = l;
 *      }
 *      public void run() {
 *          // Wait for the startSignal to be set
 *          // before proceeding.
 *          startSignal.await();
 *          { ... }
 *      }
 *  }
 *
 *  class Driver {
 *      void main() {
 *          Latch go = new Latch();
 *
 *          for(int i = 0 ; i < N ; ++i) {  // make threads
 *              new Thread(new Worker(go)).start();
 *          }
 *
 *          doSomethingElse();              // don't let run yet
 *          go.set();                       // let all threads proceed
 *      }
 *  }
 * </pre>
 */
public class Latch extends CondVar {

	// ----------------------------------------------------------------------------
	//		reset
	// ----------------------------------------------------------------------------
	/**
	 * Do nothing, a Latch cannot be reset.
	 */
	public synchronized void reset() {
		// Do nothing.
	}
}

// ----- THAT'S ALL FOLKS -----------------------------------------------------
