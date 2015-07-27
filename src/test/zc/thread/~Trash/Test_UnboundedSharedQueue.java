/* ----------------------------------------------------------------------------
 *  Test_UnboundedSharedQueue.java
 *  ZCThread Library
 *
 *  (c) Copyright Zart Colwing. 2000, 2001.
 *  All rights reserved.
 * ------------------------------------------------------------------------- */

package zc.thread;

import junit.extensions.*;
import junit.framework.*;


// ----------------------------------------------------------------------------
//		Test_UnboundedSharedQueue - class
// ----------------------------------------------------------------------------
public class Test_UnboundedSharedQueue extends TestCase {

	public final static int MAX_CAPACITY = 5;

	// ----------------------------------------------------------------------------
	public static void main(String[] args) {
		junit.awtui.TestRunner.run(Test_UnboundedSharedQueue.class);
	}

	// ----------------------------------------------------------------------------
	public static Test suite() {
		TestSuite theSuite;

		if(true) {
			// Let JUnit add all tests into the suite.
			theSuite = new TestSuite(Test_UnboundedSharedQueue.class);
		}

		return theSuite;
	}

	// ----------------------------------------------------------------------------
	public Test_UnboundedSharedQueue(String name) {
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

	private void checkEmptyness(UnboundedSharedQueue inBSQ) {
		// make sure the capacity doesn't evolve
		assertEquals(MAX_CAPACITY, inBSQ.capacity());
		// make sure that the queue properties are right
		assertEquals(0, inBSQ.size());
		assertTrue(inBSQ.isEmpty());
		assertTrue(!inBSQ.isFull());
	}

	// ----------------------------------------------------------------------------
	/**
	 * Test that the properties of the UnboundedSharedQueue always bear correct values.
	 */
	public void test_1() throws Exception {
		UnboundedSharedQueue theUBSQ = new UnboundedSharedQueue(MAX_CAPACITY);

		assertEquals(MAX_CAPACITY, theUBSQ.capacity());
		// make sure that the queue properties are right
		assertTrue(theUBSQ.isEmpty());
		assertTrue(!theUBSQ.isFull());

		// Fill the Queue
		for(int i = 0; i < theUBSQ.capacity(); ++i) {
			assertEquals(i, theUBSQ.size());
			theUBSQ.add(new Integer(i));
		}

		// make sure the capacity doesn't evolve
		assertEquals(MAX_CAPACITY, theUBSQ.capacity());
		// make sure the size is right after the filling loop
		assertEquals(theUBSQ.capacity(), theUBSQ.size());
		// make sure that the queue properties are right
		assertTrue(!theUBSQ.isEmpty());
		assertTrue(theUBSQ.isFull());

		// Empty the queue
		for(int i = theUBSQ.capacity(); i > 0 ; --i) {
			assertEquals(i, theUBSQ.size());
			Object theObject = theUBSQ.remove();
			assertEquals(theObject, new Integer(theUBSQ.capacity()-i));
		}

		checkEmptyness(theUBSQ);
	}

	// ----------------------------------------------------------------------------
	/**
	 * Test the UnboundedSharedQueue.clear() function.
	 */
	public void test_2() throws Exception {
		UnboundedSharedQueue theUBSQ = new UnboundedSharedQueue(MAX_CAPACITY);

		assertEquals(MAX_CAPACITY, theUBSQ.capacity());
		assertTrue(theUBSQ.isEmpty());
		assertTrue(!theUBSQ.isFull());

		for(int i = 0; i < theUBSQ.capacity(); ++i) {
			assertEquals(i, theUBSQ.size());
			theUBSQ.add(new Integer(i));
		}

		// clear the queue
		theUBSQ.clear();

		checkEmptyness(theUBSQ);
	}

	// ----------------------------------------------------------------------------
	/**
	 * Test that UnboundedSharedQueue.get() retrieve element in the same order as
	 * they where put by UnboundedSharedQueue.put().
	 */
	public void test_3() throws Exception {
		final UnboundedSharedQueue theUBSQ = new UnboundedSharedQueue(MAX_CAPACITY);

		Thread theProducer = new Thread("Producer") {
			public void run() {
				try {
					for(int i = 0; i < MAX_CAPACITY * 1000; ++i) {
						theUBSQ.add(new Integer(i));
//						if(theUBSQ.isFull()) {
//							System.err.println("theProducer: queue is full " + theUBSQ);
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
						Integer theInteger = (Integer)theUBSQ.remove();
						// Consume the Integers, make sure they arrive in the same
						// order they where produced
						assertEquals(i, theInteger.intValue());
//						if(theUBSQ.isEmpty()) {
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
		checkEmptyness(theUBSQ);
	}

	// ----------------------------------------------------------------------------
	/**
	 * Test that putting a null element throw a IllegalArgumentException.
	 */
	public void test_4() throws Exception {
		UnboundedSharedQueue theUBSQ = new UnboundedSharedQueue(MAX_CAPACITY);

		try {
			theUBSQ.add(null);  // null is not an accepted value
			fail("should throw an IllegalArgumentException");
		}
		catch(IllegalArgumentException ex) {
			// OK
		}
	}

	// ----------------------------------------------------------------------------
	/**
	 * test that constructing a UnboundedSharedQueue whith a zero or less size throws
	 * a IllegalArgumentException.
	 */
	public void test_5() throws Exception {
		try {
			UnboundedSharedQueue theUBSQ = new UnboundedSharedQueue(0);
			fail("should throw an IllegalArgumentException");
		}
		catch(IllegalArgumentException ex) {
			// OK
		}
	}
}

// ----- THAT'S ALL FOLKS -----------------------------------------------------
