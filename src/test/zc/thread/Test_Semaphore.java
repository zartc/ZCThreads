// ----------------------------------------------------------------------------
//  Test_Semaphore.java
//	ZCThread Library
//
//	(c) Copyright Zart Colwing, 2002-2003. All rights reserved.
// ----------------------------------------------------------------------------

package zc.thread;

import junit.framework.*;
import junit.extensions.*;


// ----------------------------------------------------------------------------
//		Test_Semaphore - class
// ----------------------------------------------------------------------------
public class Test_Semaphore extends TestCase {

	// ----------------------------------------------------------------------------
	public static void main(String[] args) {
		junit.awtui.TestRunner.run(Test_Semaphore.class);
	}

	// ----------------------------------------------------------------------------
	public static Test suite() {
		TestSuite theSuite;

		if(true) {
			// Let JUnit add all tests into the suite.
			theSuite = new TestSuite(Test_Semaphore.class);
		}

		return theSuite;
	}

	// ----------------------------------------------------------------------------
	public Test_Semaphore(String name) {
		super(name);
	}

	// ----------------------------------------------------------------------------
	protected void setUp() throws Exception {
		super.setUp();
	}

	// ----------------------------------------------------------------------------
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	// ----- the tests -----

	// ----------------------------------------------------------------------------
	public void test_1() throws Exception {
		final Semaphore sema = new Semaphore(1);
		final Object syncObject = new Object();

		Thread getLockThread = new Thread("test_sema1$getLockThread") {
			public void run() {
				synchronized(syncObject) {
					try {
						// Acquire the semaphore
						assertEquals(1, sema.value());
						sema.acquire();
						assertEquals(0, sema.value());

						// Notify/resume the main thread
						syncObject.notifyAll();

						// Suspend ourself
						syncObject.wait();
					}
					catch(InterruptedException e) {
					}

					// Make sure we still hold the semaphore
					assertEquals(0, sema.value());

					// Release the semaphore
					sema.release();
					assertEquals(1, sema.value());
				}
			}
		};

		synchronized(syncObject) {
			// Start the getLockThread
			getLockThread.start();

			try {
				// Suspend ourself to let the getLockThread acquire the semaphore
				syncObject.wait();
			}
			catch(InterruptedException e) {
			}

			// Assert that the getLockThread did acquire the semaphore
			assertEquals(0, sema.value());

			// check that sema.attempt() fail to acquire the lock.
			assertTrue(sema.attempt(100) == false);

			// resume the getLockThread
			syncObject.notifyAll();
		}

		// wait for thread to finish before returning
		getLockThread.join(100);
		// If it is stuck, unstuck it
		if(getLockThread.isAlive()) {
			getLockThread.interrupt();
			fail("getLockThread never released lock.");
		}

		// Make sure the semaphore was released
		assertEquals(1, sema.value());
	}

	// ----------------------------------------------------------------------------
	public void test_2() throws Exception {
		final Semaphore sema = new Semaphore(1);

		Thread thread1 = new Thread() {
			public void run() {
				try {
					sema.acquire();
					assertEquals(0, sema.value());
				}
				catch(InterruptedException inException) {
				}
			}
		};

		thread1.start();
		thread1.join();

		// If in the future Sun provide a way to be notified
		// when a Thread died then the Semaphore could be "magically"
		// released upond its owner Thread death.
		// For the time being, Assert that the semaphore is not released
		assertEquals(0, sema.value());
	}
}

// ----- THAT'S ALL FOLKS -----------------------------------------------------
