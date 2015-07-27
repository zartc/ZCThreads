// ----------------------------------------------------------------------------
//  Test_CondVar.java
//	ZCThread Library
//
//	(c) Copyright Zart Colwing, 2002-2003. All rights reserved.
// ----------------------------------------------------------------------------

package zc.thread;

import junit.extensions.*;
import junit.framework.*;


// ----------------------------------------------------------------------------
//		Test_CondVar - class
// ----------------------------------------------------------------------------
public class Test_CondVar extends TestCase {

	public final static int MAX_CAPACITY = 5;

	// ----------------------------------------------------------------------------
	public static void main(String[] args) {
		junit.awtui.TestRunner.run(Test_CondVar.class);
	}

	// ----------------------------------------------------------------------------
	public static Test suite() {
		TestSuite theSuite;

		if(true) {
			// Let JUnit add all tests into the suite.
			theSuite = new TestSuite(Test_CondVar.class);
		}

		return theSuite;
	}

	// ----------------------------------------------------------------------------
	public Test_CondVar(String name) {
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
		final CondVar theCondVar = new CondVar();
		final int WAIT_TIME = 200;

		class Racer implements Runnable {
			public void run() {
				try {
					assertEquals(false, theCondVar.hasFired());

					long startTime = System.currentTimeMillis();
					theCondVar.await();
					// Make sure the Tread await at least WAIT_TIME ms.
					assertTrue((System.currentTimeMillis()-startTime) > WAIT_TIME);

					assertEquals(true, theCondVar.hasFired());
				}
				catch(Exception ex) {
					fail(ex.getMessage());
				}
			}
		}

		Thread racer1 = new Thread(new Racer(), "Racer1");
		Thread racer2 = new Thread(new Racer(), "Racer2");

		// Start the racers, they will immediately
		// wait for theCondVar to be set.
		racer1.start();
		racer2.start();

		// Wait a little bit before setting the condVar.
		Thread.sleep(WAIT_TIME);
		theCondVar.fire();

		// wait for threads to finish before returning
		racer1.join(100);
		// If it is stuck, unstuck it
		if(racer1.isAlive()) {
			racer1.interrupt();
			fail("racer1 never resumed.");
		}

		racer2.join(100);
		// If it is stuck, unstuck it
		if(racer2.isAlive()) {
			racer2.interrupt();
			fail("racer2 never resumed.");
		}
	}

	// ----------------------------------------------------------------------------
	public void test_2() throws Exception {
		final CondVar theCondVar = new CondVar();

		assertEquals(false, theCondVar.hasFired());
		theCondVar.fire();
		assertEquals(true, theCondVar.hasFired());
		theCondVar.reset();
		assertEquals(false, theCondVar.hasFired());
	}

	// ----------------------------------------------------------------------------
	public void test_3() throws Exception {
		final CondVar theCondVar = new CondVar(true);

		assertEquals(true, theCondVar.hasFired());
		theCondVar.fire();
		assertEquals(true, theCondVar.hasFired());
		theCondVar.reset();
		assertEquals(false, theCondVar.hasFired());
	}

	// ----------------------------------------------------------------------------
	public void test_4() throws Exception {
		final CondVar theCondVar = new CondVar();

		assertEquals(false, theCondVar.await(100));
		theCondVar.fire();
		assertEquals(true, theCondVar.await(100));
		theCondVar.reset();
		assertEquals(false, theCondVar.await(100));
	}

	// ----------------------------------------------------------------------------
	public void test_5() throws Exception {
		class lockout {
			CondVar theCondVar = new CondVar();
			void foo() throws InterruptedException {
				synchronized(this) {
					theCondVar.await();
				}
			}
			void bar() {
				synchronized(this) {
					theCondVar.fire();
				}
			}
		}

		final lockout theLockout = new lockout();

		Thread racer1 = new Thread("racer1") {
			public void run() {
				try {
					theLockout.foo();
				}
				catch(Exception ex) {
					fail(ex.getMessage());
				}
			}
		};

		racer1.start();

		// Deadlock here because of the nested monitor.
//		theLockout.bar();
	}
}

// ----- THAT'S ALL FOLKS -----------------------------------------------------
