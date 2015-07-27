// ----------------------------------------------------------------------------
//  TupleSpace.java
//	ZCThread Library
//
//	(c) Copyright Zart Colwing, 2002-2003. All rights reserved.
// ----------------------------------------------------------------------------

package zc.thread;

import java.util.*;

// ----------------------------------------------------------------------------
//		TupleSpace - class
// ----------------------------------------------------------------------------
public class TupleSpace {

	private HashMap fSpace;

	// ----------------------------------------------------------------------------
	//		TupleSpace - constructor
	// ----------------------------------------------------------------------------
	public TupleSpace() {
	}

	// ----------------------------------------------------------------------------
	//		out
	// ----------------------------------------------------------------------------
	public synchronized void out(String tag, Object data) {
		ArrayList theArray = (ArrayList)fSpace.get(tag);

		// If the tag was not found in the tuple space, we add it.
		if(theArray == null) {
			theArray = new ArrayList();
			fSpace.put(tag, theArray);
		}

		theArray.add(data);
		this.notifyAll();
	}

	// ----------------------------------------------------------------------------
	//		get
	// ----------------------------------------------------------------------------
	private Object get(String tag, boolean remove) {
		ArrayList theArray = (ArrayList)fSpace.get(tag);

		if(theArray == null || theArray.size() == 0) {
			return null;
		}

		return (remove) ? theArray.remove(0) : theArray.get(0);
	}

	// ----------------------------------------------------------------------------
	//		in
	// ----------------------------------------------------------------------------
	public synchronized Object in(String tag) throws InterruptedException {
		Object theObject;
		while((theObject = get(tag, /*remove*/ true)) == null) {
			this.wait();
		}
		return theObject;
	}

	// ----------------------------------------------------------------------------
	//		rd
	// ----------------------------------------------------------------------------
	public synchronized Object rd(String tag) throws InterruptedException {
		Object theObject;
		while((theObject = get(tag, /*remove*/ false)) == null) {
			this.wait();
		}
		return theObject;
	}
}

// ----- THAT'S ALL FOLKS -----------------------------------------------------
