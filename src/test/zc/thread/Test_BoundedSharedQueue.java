// ----------------------------------------------------------------------------
//  Test_BoundedSharedQueue.java
//	ZCThread Library
//
//	(c) Copyright Zart Colwing, 2002-2003. All rights reserved.
// ----------------------------------------------------------------------------

package zc.thread;

import junit.extensions.*;
import junit.framework.*;


// ----------------------------------------------------------------------------
//		Test_BoundedSharedQueue - class
// ----------------------------------------------------------------------------
public class Test_BoundedSharedQueue extends TestCase {

	public final static int MAX_CAPACITY = 5;

	// ----------------------------------------------------------------------------
	public static void main(String[] args) {
		junit.awtui.TestRunner.run(Test_BoundedSharedQueue.class);
	}

	// ----------------------------------------------------------------------------
	public static Test suite() {
		TestSuite theSuite;

		if(true) {
			// Let JUnit add all tests into the suite.
			theSuite = new TestSuite(Test_BoundedSharedQueue.class);
		}

		return theSuite;
	}

	// ----------------------------------------------------------------------------
	public Test_BoundedSharedQueue(String name) {
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

	private void checkEmptyness(BoundedSharedQueue inBSQ) {
		// make sure the capacity doesn't evolve
		assertEquals(MAX_CAPACITY, inBSQ.capacity());
		// make sure that the queue properties are right
		assertEquals(0, inBSQ.size());
		assertTrue(inBSQ.isEmpty());
		assertTrue(!inBSQ.isFull());
	}

	// ----------------------------------------------------------------------------
	/**
	 * Test that the properties of the BoundedSharedQueue always bear correct values.
	 */
	public void test_1() throws Exception {
		BoundedSharedQueue theBSQ = new BoundedSharedQueue(MAX_CAPACITY);

		assertEquals(MAX_CAPACITY, theBSQ.capacity());
		// make sure that the queue properties are right
		assertTrue(theBSQ.isEmpty());
		assertTrue(!theBSQ.isFull());

		// Fill the Queue
		for(int i = 0; i < theBSQ.capacity(); ++i) {
			assertEquals(i, theBSQ.size());
			theBSQ.add(new Integer(i));
		}

		// make sure the capacity doesn't evolve
		assertEquals(MAX_CAPACITY, theBSQ.capacity());
		// make sure the size is right after the filling loop
		assertEquals(theBSQ.capacity(), theBSQ.size());
		// make sure that the queue properties are right
		assertTrue(!theBSQ.isEmpty());
		assertTrue(theBSQ.isFull());

		// Empty the queue
		for(int i = theBSQ.capacity(); i > 0 ; --i) {
			assertEquals(i, theBSQ.size());
			Object theObject = theBSQ.remove();
			assertEquals(theObject, new Integer(theBSQ.capacity()-i));
		}

		checkEmptyness(theBSQ);
	}

	// ----------------------------------------------------------------------------
	/**
	 * Test the BoundedSharedQueue.clear() function.
	 */
	public void test_2() throws Exception {
		BoundedSharedQueue theBSQ = new BoundedSharedQueue(MAX_CAPACITY);

		assertEquals(MAX_CAPACITY, theBSQ.capacity());
		assertTrue(theBSQ.isEmpty());
		assertTrue(!theBSQ.isFull());

		for(int i = 0; i < theBSQ.capacity(); ++i) {
			assertEquals(i, theBSQ.size());
			theBSQ.add(new Integer(i));
		}

		// clear the queue
		theBSQ.clear();

		checkEmptyness(theBSQ);
	}

	// ----------------------------------------------------------------------------
	/**
	 * Test that BoundedSharedQueue.get() retrieve element in the same order as
	 * they where put by BoundedSharedQueue.put().
	 */
	public void test_3() throws Exception {
		final BoundedSharedQueue theBSQ = new BoundedSharedQueue(MAX_CAPACITY);

		Thread theProducer = new Thread("Producer") {
			public void run() {
				try {
					for(int i = 0; i < MAX_CAPACITY * 1000; ++i) {
						theBSQ.add(new Integer(i));
//						if(theBSQ.isFull()) {
//							System.err.println("theProducer: queue is full " + theBSQ);
//						}
					}
				}
				catch(Exception ex) {
					fail(ex.getMessage());
				}
			}
		};

		Thread theConsumer = new Thread("Consumer") {
			public void run() {
				try {
					for(int i = 0; i < MAX_CAPACITY * 1000; ++i) {
						Integer theInteger = (Integer)theBSQ.remove();
						// Consume the Integers, make sure they arrive in the same
						// order they where produced
						assertEquals(i, theInteger.intValue());
//						if(theBSQ.isEmpty()) {
//							System.err.println(theInteger);
//							System.err.println("theConsumer: queue is empty ");
//						}
//						else {
//							System.err.println(theInteger);
//						}
					}
				}
				catch(Exception ex) {
					fail(ex.getMessage());
				}
			}
		};

		theProducer.start();
		theConsumer.start();

		theProducer.join();
		theConsumer.join();

		// Make sure avery and all Integers have been consumed
		checkEmptyness(theBSQ);
	}

	// ----------------------------------------------------------------------------
	/**
	 * Test that putting a null element throw a IllegalArgumentException.
	 */
	public void test_4() throws Exception {
		BoundedSharedQueue theBSQ = new BoundedSharedQueue(MAX_CAPACITY);

		try {
			theBSQ.add(null);  // null is not an accepted value
			fail("should throw an IllegalArgumentException");
		}
		catch(IllegalArgumentException ex) {
			// OK
		}
	}

	// ----------------------------------------------------------------------------
	/**
	 * test that constructing a BoundedSharedQueue whith a zero or less size throws
	 * a IllegalArgumentException.
	 */
	public void test_5() throws Exception {
		try {
			BoundedSharedQueue theBSQ = new BoundedSharedQueue(0);
			fail("should throw an IllegalArgumentException");
		}
		catch(IllegalArgumentException ex) {
			// OK
		}
	}
}

// ----- THAT'S ALL FOLKS -----------------------------------------------------
