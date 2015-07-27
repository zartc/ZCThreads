// ----------------------------------------------------------------------------
//  Test_Barrier.java
//	ZCThread Library
//
//	(c) Copyright Zart Colwing, 2002-2003. All rights reserved.
// ----------------------------------------------------------------------------

package zc.thread;

import java.util.Random;

import junit.extensions.*;
import junit.framework.*;


// ----------------------------------------------------------------------------
//		Test_Barrier - class
// ----------------------------------------------------------------------------
public class Test_Barrier extends TestCase {

	private final static int NB_ITERATION = 500;

	Random fRandom = new Random();
	Thread[] fParticipants;
	Barrier fRDV;

	// ----------------------------------------------------------------------------
	public static void main(String[] args) {
		junit.awtui.TestRunner.run(Test_Barrier.class);
	}

	// ----------------------------------------------------------------------------
	public static Test suite() {
		TestSuite theSuite;

		if(true) {
			// Let JUnit add all tests into the suite.
			theSuite = new TestSuite(Test_Barrier.class);
		}

		return theSuite;
	}

	// ----------------------------------------------------------------------------
	public Test_Barrier(String name) {
		super(name);
	}

	// ----------------------------------------------------------------------------
	protected void setUp() throws Exception {
		super.setUp();

		fParticipants = new Thread[3];
		fRDV = new Barrier(fParticipants);

		for(int i = 0; i < 3; ++i) {
			fParticipants[i] = new WThread(fRDV, i);
		}
	}

	// ----------------------------------------------------------------------------
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	// ----- the tests -----

	class WThread extends Thread {
		Barrier fBarrier;
		int fID;

		public WThread(Barrier inBarrier, int inID) {
			super("T" + inID);
			fBarrier = inBarrier;
			fID = inID;
		}

		public void run() {
			try {
				for(int i = 0; i < NB_ITERATION; ++i) {
					fBarrier.await();
					// Sleep a variable time so that the thread arrival order
					// at the next barrier will varie.
//						Thread.sleep(fRandom.nextInt(100));
//						System.out.println(i + " : " + getName());
				}
			}
			catch(InterruptedException inException) {
				System.out.println(inException.getMessage());
			}
			catch(BrokenBarrierException inException) {
				System.out.println(inException.getMessage());
			}
		}
	}

	// ----------------------------------------------------------------------------
	public void test_1() throws Exception {
		for(int i = 0; i < 3; ++i) {
			fParticipants[i].start();
		}

		for(int i = 0; i < 3; ++i) {
			fParticipants[i].join();
		}

		assertEquals(NB_ITERATION, fRDV.getSpinCounter());
	}

	// ----------------------------------------------------------------------------
	public void test_2() throws Exception {
		// Test that adding a non registered Thread will throw an Exception
		for(int i = 0; i < 3; ++i) {
			fParticipants[i].start();
		}

		try {
			fRDV.await();
			fail("Should receive a RuntimeException");
		}
		catch(RuntimeException inException) {
			// OK
		}

		for(int i = 0; i < 3; ++i) {
			fParticipants[i].join();
		}

		// The barrier should not be broken by the intruder attempt.
		assertEquals(NB_ITERATION, fRDV.getSpinCounter());
	}

	// ----------------------------------------------------------------------------
	public void test_3() throws Exception {
		// Test that failing in the barrier user function will break the barrier
		fRDV.setCommand(new Barrier.Command() {
			public void run(Object[] inObjects) {
				throw new RuntimeException("expected exception");
			}
		});

		for(int i = 0; i < 3; ++i) {
			fParticipants[i].start();
		}
		for(int i = 0; i < 3; ++i) {
			fParticipants[i].join();
		}

		assertEquals(1, fRDV.getSpinCounter());
	}

	// ----------------------------------------------------------------------------
	public void xtest_4() throws Exception {
		// 2 threads
		// on lance le premier qui fait Barrier.wait()
		// avant de lancer le second, le main thread interrupt le premier (qui
		// est waiting)
		// on lance le second
		// est-ce qu'il reoit immediatement une BrokenException ou bien est-ce que
		// la barrier command est executer malgrŽ tout? (ca depend du Thread.schedulling!)
	}
}

// ----- THAT'S ALL FOLKS -----------------------------------------------------
