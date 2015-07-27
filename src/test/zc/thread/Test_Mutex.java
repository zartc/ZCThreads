// ----------------------------------------------------------------------------
//  Test_Mutex.java
//	ZCThread Library
//
//	(c) Copyright Zart Colwing, 2002-2003. All rights reserved.
// ----------------------------------------------------------------------------

package zc.thread;

import junit.framework.*;
import junit.extensions.*;


// ----------------------------------------------------------------------------
//		Test_Mutex - class
// ----------------------------------------------------------------------------
public class Test_Mutex extends TestCase {

	// ----------------------------------------------------------------------------
	public static void main(String[] args) {
		junit.awtui.TestRunner.run(Test_Mutex.class);
	}

	// ----------------------------------------------------------------------------
	public static Test suite() {
		TestSuite theSuite;

		if(true) {
			// Let JUnit add all tests into the suite.
			theSuite = new TestSuite(Test_Mutex.class);
		}

		return theSuite;
	}

	// ----------------------------------------------------------------------------
	public Test_Mutex(String name) {
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
		final Mutex mutex = new Mutex();
		final Object syncObject = new Object();

		Thread getLockThread = new Thread("test_1$getLockThread") {
			public void run() {
				synchronized(syncObject) {
					try {
						// Acquire the Mutex
						mutex.acquire();

						// Notify/resume the main thread
						syncObject.notifyAll();
						syncObject.wait();
					}
					catch(InterruptedException e) {
						fail(this.getName() + " has been interrupted");
					}

					// Release the Mutex
					mutex.release();
				}
			}
		};

		synchronized(syncObject) {
			// Start the getLockThread
			getLockThread.start();

			// Suspend ourself to let the getLockThread acquire the Mutex
			syncObject.wait();

			// check that mutex.attempt() fail to acquire the lock.
			assertTrue(mutex.attempt(100) == false);

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

		// Make sure the Mutex was released
		assertTrue(mutex.attempt(100) == true);

		// Release the Mutex
		mutex.release();
	}

	// ----------------------------------------------------------------------------
	public void test_2() throws Exception {
		final Object syncObject = new Object();
		final Mutex mutex = new Mutex();

		Thread thread1 = new Thread() {
			public void run() {
				synchronized(syncObject) {
					try {
						mutex.acquire();

						// Notify/resume the main thread
						syncObject.notifyAll();
						syncObject.wait();

						// test that we can acquire multiple time
						mutex.acquire();

						syncObject.notifyAll();
					}
					catch(InterruptedException inException) {
						fail(this.getName() + " has been interrupted");
					}
				}
			}
		};

		synchronized(syncObject) {
			thread1.start();

			// Suspend ourself to let thread1 acquire the Mutex
			syncObject.wait();

			// Make sure the Mutex is acquired
			assertTrue(mutex.attempt(100) == false);

			// Try to release the mutex albeit we are not the owner.
			try {
				mutex.release();
				fail("Should have raised an IllegalMonitorStateException");
			}
			catch (IllegalMonitorStateException ex) {
				// OK
			}

			// Make sure the Mutex is still acquired
			assertTrue(mutex.attempt(100) == false);

			// Notify/resume the thread1
			syncObject.notifyAll();
			syncObject.wait();
		}


		// wait for thread to finish before returning
		thread1.join(100);
		// If it is stuck, unstuck it
		if(thread1.isAlive()) {
			thread1.interrupt();
			fail("getLockThread never released lock.");
		}

		// Make sure the Mutex was not released
		assertTrue(mutex.attempt(100) == false);
	}

	// ----------------------------------------------------------------------------
	public void test_3() throws Exception {
		final Mutex mutex = new Mutex();

		Thread thread1 = new Thread() {
			public void run() {
				try {
					mutex.acquire();
				}
				catch(InterruptedException inException) {
					fail(this.getName() + " has been interrupted");
				}
			}
		};

		thread1.start();
		thread1.join();

		// Make sure the Mutex is still acquired
		assertTrue(mutex.attempt(100) == false);
	}
}

// ----- THAT'S ALL FOLKS -----------------------------------------------------
