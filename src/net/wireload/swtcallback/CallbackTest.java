package net.wireload.swtcallback;

import junit.framework.TestCase;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class CallbackTest extends TestCase {
	boolean gotEvent = false;
	boolean gotRun = false;

	private class MySuperclass {
		Button eventButton;
		boolean superCalled = false;
		boolean childCalled = false;
		
		MySuperclass() {
			Shell testShell = new Shell();
			eventButton = new Button(testShell, SWT.NONE);
			Callback.add(eventButton, SWT.Selection, this, "callbackTestEventClicked");
		}
		
		void callbackTestEventClicked() {
			superCalled = true;
		}
	}
	
	private class MySubclass extends MySuperclass {
		MySubclass() {
			super();
		}
		
		void callbackTestEventClicked() {
			childCalled = true;
		}
	}

	
	/**
	 * This test just makes sure normal SWT events work fine.
	 */
	public final void testNormalListener() {
		Shell testShell = new Shell();
		Button eventButton = new Button(testShell, SWT.NONE);
		
		gotEvent = false;

		eventButton.notifyListeners(SWT.Selection, new Event());
		assertFalse(gotEvent);
		
		Listener directListener = new Listener() {
		    public void handleEvent(Event event) {
		        gotEvent = true;
		    }
		};
		
		eventButton.addListener(SWT.Selection, directListener);
		eventButton.notifyListeners(SWT.Selection, new Event());
		assertTrue(gotEvent);
		
		eventButton.removeListener(SWT.Selection, directListener);
		gotEvent = false;
		eventButton.notifyListeners(SWT.Selection, new Event());
		assertFalse(gotEvent);
	}

	
	/**
	 * This test just makes sure that if a super class registers
	 * a listener and a subclass overrides the listener method,
	 * the subclass listener gets called for subclass instances.
	 */
	public final void testSubclassListener() {
		MySuperclass a = new MySuperclass();
		MySubclass b = new MySubclass();
		
		assertFalse(a.superCalled);
		assertFalse(a.childCalled);
		assertFalse(b.superCalled);
		assertFalse(b.childCalled);

		a.eventButton.notifyListeners(SWT.Selection, new Event());
		b.eventButton.notifyListeners(SWT.Selection, new Event());

		assertFalse(b.superCalled);
		assertTrue(b.childCalled);
		assertTrue(a.superCalled);
		assertFalse(a.childCalled);
	}
	
	/**
	 * Test method for {@link net.wireload.Callback(...)}.
	 */
	public final void testCallback() {
		Shell testShell = new Shell();
		Button eventButton = new Button(testShell, SWT.NONE);
		
		gotEvent = false;
		
		eventButton.notifyListeners(SWT.Selection, new Event());
		assertFalse(gotEvent);
		
		eventButton.addListener(SWT.Selection, new Callback(this, "callbackTestEventClicked"));
		assertFalse(gotEvent);

		eventButton.notifyListeners(SWT.Selection, new Event());
		assertTrue(gotEvent);

	}
	
	/**
	 * Test method for {@link net.wireload.Callback.add(...)}.
	 */
	public final void testShortCallback() {
		Shell testShell = new Shell();
		Button eventButton = new Button(testShell, SWT.NONE);
		
		gotEvent = false;
		
		eventButton.notifyListeners(SWT.Selection, new Event());
		assertFalse(gotEvent);
		
		Callback.add(eventButton, SWT.Selection, this, "callbackTestEventClicked");
		assertFalse(gotEvent);

		eventButton.notifyListeners(SWT.Selection, new Event());
		assertTrue(gotEvent);
	}
	
	/**
	 * Test method for {@link net.wireload.Callback(...)}.
	 */
	public final void testRunnableCallback() {
		// Test Callback as a Runnable.
		gotRun = false;
		
		Callback runner = new Callback(this, "callbackRunnableExecuted");
		// Make sure just instantiating the callback doesn't cause method execution.
		assertFalse(gotRun);
		
		Display.getDefault().syncExec(runner);
		assertTrue(gotRun);
	}

	/**
	 * Test callback method.
	 */
	@SuppressWarnings("unused")
	private void callbackTestEventClicked() {
		gotEvent = true;
	}
	
	/**
	 * Test callback method.
	 */
	@SuppressWarnings("unused")
	private void callbackRunnableExecuted() {
		gotRun = true;
	}
}
