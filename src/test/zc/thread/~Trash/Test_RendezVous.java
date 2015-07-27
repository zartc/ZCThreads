/* ----------------------------------------------------------------------------
 *  Test_RendezVous.java
 *  ZCThread Library
 *
 *  (c) Copyright Zart Colwing. 2000, 2001.
 *  All rights reserved.
 * ------------------------------------------------------------------------- */

package zc.thread;

import java.util.Random;

import junit.framework.*;
import junit.extensions.*;


// ----------------------------------------------------------------------------
//		Test_RendezVous - class
// ----------------------------------------------------------------------------
public class Test_RendezVous extends TestCase {

	Random fRandom = new Random();
	Thread[] fParticipants;
	RendezVous fRDV;

	// ----------------------------------------------------------------------------
	public static void main(String[] args) {
		junit.awtui.TestRunner.run(Test_RendezVous.class);
	}

	// ----------------------------------------------------------------------------
	public static Test suite() {
		TestSuite theSuite;

		if(true) {
			// Let JUnit add all tests into the suite.
			theSuite = new TestSuite(Test_RendezVous.class);
		}

		return theSuite;
	}

	// ----------------------------------------------------------------------------
	public Test_RendezVous(String name) {
		super(name);
	}

	// ----------------------------------------------------------------------------
	protected void setUp() throws Exception {
		super.setUp();

		fParticipants = new Thread[3];
		fRDV = new RendezVous(fParticipants);

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
		RendezVous fRDV;
		int fID;

		public WThread(RendezVous inRDV, int inID) {
			super("T" + inID);
			fRDV = inRDV;
			fID = inID;
		}

		public void run() {
			Object theObject = String.valueOf(fID);
			for(int i = 0; i < 500; ++i) {
				try {
					Object theNewObject = fRDV.await(theObject);
					// assert that the object we received is not the same than
					// the one we presented to the RendezVous. We can not make
					// any other assumption because the object we will receive
					// depend upon the thread arrival position at the RendezVous.
					// Also if two participant present the same object it is not
					// unlikely that a given participant receive the same object
					// as the one it presented.
					assertTrue(theObject.equals(theNewObject) == false);
				}
				catch(InterruptedException inException) {
					fail(inException.getMessage());
				}
				catch(BrokenBarrierException inException) {
					fail(inException.getMessage());
				}
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
	}

	// ----------------------------------------------------------------------------
	public void test_2() throws Exception {
		for(int i = 0; i < 3; ++i) {
			fParticipants[i].start();
		}

		try {
			fRDV.await();
			fail("Should receive an NoSuchMethodError");
			// because rendezVous.await(void) is an illegal metho to call.
		}
		catch(NoSuchMethodError ex) {
			// OK
		}

		try {
			Object theObject = fRDV.await(new Integer(0));
			fail("Should receive an RuntimeException");
			// because the cuurent thread is not a registered
			// participant of the RDV.
		}
		catch(RuntimeException ex) {
			// OK
		}

		for(int i = 0; i < 3; ++i) {
			fParticipants[i].join();
		}
	}
	// ----------------------------------------------------------------------------
	public void xtest_3() throws Exception {
		// make sure we can not call RendezVous.setCommand(new Runnable() {});
		// As Barrier and RDV doesn't use the same kind of Command object.
	}
}

// ----- THAT'S ALL FOLKS -----------------------------------------------------
