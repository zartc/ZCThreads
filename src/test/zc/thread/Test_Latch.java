// ----------------------------------------------------------------------------
//  Test_Latch.java
//	ZCThread Library
//
//	(c) Copyright Zart Colwing, 2002-2003. All rights reserved.
// ----------------------------------------------------------------------------

package zc.thread;

import junit.extensions.*;
import junit.framework.*;


// ----------------------------------------------------------------------------
//		Test_Latch - class
// ----------------------------------------------------------------------------
public class Test_Latch extends TestCase {

	public final static int MAX_CAPACITY = 5;

	// ----------------------------------------------------------------------------
	public static void main(String[] args) {
		junit.awtui.TestRunner.run(Test_Latch.class);
	}

	// ----------------------------------------------------------------------------
	public static Test suite() {
		TestSuite theSuite;

		if(true) {
			// Let JUnit add all tests into the suite.
			theSuite = new TestSuite(Test_Latch.class);
		}

		return theSuite;
	}

	// ----------------------------------------------------------------------------
	public Test_Latch(String name) {
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
	/**
	 * Test that Latch.get() retrieve element in the same order as
	 * they where put by Latch.put().
	 */
	public void test_1() throws Exception {
		final Latch theLatch = new Latch();

		Thread racer1 = new Thread("racer1") {
			public void run() {
				try {
					assertEquals(false, theLatch.hasFired());
					theLatch.await();
					assertEquals(true, theLatch.hasFired());
				}
				catch(Exception ex) {
					fail(ex.getMessage());
				}
			}
		};

		Thread racer2 = new Thread("racer2") {
			public void run() {
				try {
					assertEquals(false, theLatch.hasFired());
					theLatch.await();
					assertEquals(true, theLatch.hasFired());
				}
				catch(Exception ex) {
					fail(ex.getMessage());
				}
			}
		};

		// Start the racers, they will immediately
		// wait for theLatch to be set.
		racer1.start();
		racer2.start();

		theLatch.fire();

		// wait for thread to finish before returning
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
		final Latch theLatch = new Latch();

		assertEquals(false, theLatch.hasFired());
		theLatch.fire();
		assertEquals(true, theLatch.hasFired());
		theLatch.reset();
		// can not reset a Latch
		assertEquals(true, theLatch.hasFired());
	}

	// ----------------------------------------------------------------------------
	public void test_3() throws Exception {
		// no Lacth(Boolean) constructor
//		final Latch theLatch = new Latch(true);
//
//		assertEquals(true, theLatch.value());
//		theLatch.set();
//		assertEquals(true, theLatch.value());
//		theLatch.reset();
//		// can not reset a Latch
//		assertEquals(true, theLatch.value());
	}

	// ----------------------------------------------------------------------------
	public void test_4() throws Exception {
		final Latch theLatch = new Latch();

		assertEquals(false, theLatch.await(100));
		theLatch.fire();
		assertEquals(true, theLatch.await(100));
		theLatch.reset();
		// can not reset a Latch
		assertEquals(true, theLatch.await(100));
	}
}

// ----- THAT'S ALL FOLKS -----------------------------------------------------
