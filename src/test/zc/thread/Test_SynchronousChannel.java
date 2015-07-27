// ----------------------------------------------------------------------------
//  Test_SynchronousChannel.java
//	ZCThread Library
//
//	(c) Copyright Zart Colwing, 2002-2003. All rights reserved.
// ----------------------------------------------------------------------------

package zc.thread;

import junit.extensions.*;
import junit.framework.*;


// ----------------------------------------------------------------------------
//		Test_SynchronousChannel - class
// ----------------------------------------------------------------------------
public class Test_SynchronousChannel extends TestCase {

	public final static int MAX_CAPACITY = 5;

	// ----------------------------------------------------------------------------
	public static void main(String[] args) {
		junit.awtui.TestRunner.run(Test_SynchronousChannel.class);
	}

	// ----------------------------------------------------------------------------
	public static Test suite() {
		TestSuite theSuite;

		if(true) {
			// Let JUnit add all tests into the suite.
			theSuite = new TestSuite(Test_SynchronousChannel.class);
		}

		return theSuite;
	}

	// ----------------------------------------------------------------------------
	public Test_SynchronousChannel(String name) {
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
		final SynchronousChannel theSynchronousChannel = new SynchronousChannel();

		Thread theProducer = new Thread("Producer") {
			public void run() {
				try {
					for (int i = 0; i < 1000; i++) {
						theSynchronousChannel.send(new Integer(i));
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
					for (int i = 0; i < 1000; i++) {
						assertEquals(new Integer(i), theSynchronousChannel.receive());
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
	}
}

// ----- THAT'S ALL FOLKS -----------------------------------------------------
