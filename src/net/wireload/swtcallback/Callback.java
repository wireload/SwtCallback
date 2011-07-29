package net.wireload.swtcallback;

import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;

/*
 * Copyright (c) 2007 WireLoad Inc.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. Neither the name of WireLoad Inc. nor the names
 * of its contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * A Callback instance calls a specified method of a specified object when a
 * particular event occurs. It can be used as an SWT Listener, or as a Runnable.
 * When used as an SWT event handler, a Callback may be used as follows:
 *
 * <pre><code>
 * Button eventButton = new Button(myShell, SWT.NONE);
 * eventButton.addSelectionListener(new Callback(this, "doEventButtonClicked"));
 * </code></pre>
 *
 * When used as a runnable, a Callback may be used for anything that takes a
 * Runnable argument when you actually would much rather specify a method. This
 * may be used both in SWT and elsewhere. The following example is for a timer
 * in SWT:
 *
 * <pre><code>
 * display.timerExec(200, new Callback(this, "refresh"));
 * </code></pre>
 *
 * The use as an event handler is inspired by Buoy by Peter Eastman.
 *
 * @author Alexander Ljungberg
 * @version 0.3
 */
public class Callback implements Listener, Runnable {
	private final boolean DEBUG = false;

	/**
	 * The object to handle the events.
	 */
	private final Object target;

	/**
	 * The method of the object to handle the events.
	 */
	private final Method callbackMethod;

	/**
	 * Whether the callBackMethod takes an Event argument or not.
	 */
	private boolean eventArgument;

	/**
	 * Define a new callback listener. The callback listener will call the named
	 * method of the given object instance whenever it receives an event.
	 *
	 * @param callbackInstance
	 *            the object which should have the method called
	 * @param methodName
	 *            the method to call upon an event
	 */
	@SuppressWarnings("unchecked")
	public Callback(Object callbackInstance, String methodName) {
		if (callbackInstance == null)
			throw new IllegalArgumentException(
					"callbackInstance can't be null.");
		if (methodName == null || methodName.length() == 0)
			throw new IllegalArgumentException("Illegal methodName specified.");

		this.target = callbackInstance;

		// Search the class and its super classes for the given method.
		Class targetClass = target.getClass();
		while (targetClass != null) {
			for (Method methodCandidate : targetClass.getDeclaredMethods()) {
				if (methodCandidate.getName().equals(methodName)) {
					callbackMethod = methodCandidate;

					Class parameters[] = methodCandidate.getParameterTypes();

					eventArgument = (parameters.length > 0 && parameters[0].isAssignableFrom(Event.class));
					return;
				}
			}

			// Nothing here. Lets check the super class, if any.
			targetClass = targetClass.getSuperclass();
		}

		// If we get here we didn't find anything.
		throw new IllegalArgumentException("The specified callback method ("
				+ target.getClass().getName() + "." + methodName
				+ ") couldn't be found.");
	}

	/**
	 * <p>
	 * Shorthand method for adding a new callback event handler to an SWT
	 * widget. Notice that this callback can never be removed. It will be
	 * garbage collected as normal when the widget is disposed off.
	 * </p>
	 *
	 * <p>
	 * Example usage:
	 * </p>
	 *
	 * <pre><code>
	 * Button eventButton = new Button(myShell, SWT.NONE);
	 * Callback.add(eventButton, SWT.Selection, this, &quot;myCallback&quot;);
	 * </code></pre>
	 *
	 * @param widget
	 *            the widget to add a callback event handler for
	 * @param event
	 *            the event that the callback should handle
	 * @param callbackInstance
	 *            the object which should have the method called
	 * @param methodName
	 *            the method to call upon an event
	 */
	public static void add(Widget widget, int event, Object callbackInstance,
			String methodName) {
		widget.addListener(event, new Callback(callbackInstance, methodName));
	}

	/**
	 * Handle an event by calling the appropriate callback method.
	 */
	public void handleEvent(Event event) {
		boolean overrideAccess = false;
		if (!callbackMethod.isAccessible()) {
			callbackMethod.setAccessible(true);
			overrideAccess = true;
		}

		try {
			if (eventArgument) {
				if (DEBUG)
					System.out.println("Calling " + target.getClass().getName()
							+ "." + callbackMethod.getName() + "(event)");
				callbackMethod.invoke(target, new Object[] { event });
			} else {
				if (DEBUG)
					System.out.println("Calling " + target.getClass().getName()
							+ "." + callbackMethod.getName() + "()");
				callbackMethod.invoke(target, new Object[0]);
			}
		} catch (InvocationTargetException e) {
			// Try to get some debugging info out there.
			e.printStackTrace(System.err);
			Throwable cause = e.getCause();
			if (cause == null)
				cause = e;
			else
				cause.printStackTrace(System.err);

			UncaughtExceptionHandler exceptionHandler = Thread.currentThread().getUncaughtExceptionHandler();
			if (exceptionHandler != null)
				exceptionHandler.uncaughtException(Thread.currentThread(), e);
		} catch (Exception e) {
			throw new AssertionError("Unable to invoke callback method ("
					+ target.getClass().getName() + "."
					+ callbackMethod.getName() + ".): " + e);
		} finally {
			if (overrideAccess)
				callbackMethod.setAccessible(false);
		}
	}

	/**
	 * Handle a request to run by calling the appropriate callback method.
	 */
	public void run() {
		boolean overrideAccess = false;
		if (!callbackMethod.isAccessible()) {
			callbackMethod.setAccessible(true);
			overrideAccess = true;
		}

		try {
			if (eventArgument) {
				throw new IllegalStateException("The specified callback method takes an event and can't be used " +
						"as a Runnable.");
			} else {
				if (DEBUG)
					System.out.println("Calling " + target.getClass().getName()
							+ "." + callbackMethod.getName() + "()");
				callbackMethod.invoke(target, new Object[0]);
			}
		} catch (InvocationTargetException e) {
			// Try to get some debugging info out there.
			e.printStackTrace(System.err);
			Throwable cause = e.getCause();
			if (cause == null)
				cause = e;
			else
				cause.printStackTrace(System.err);

			UncaughtExceptionHandler exceptionHandler = Thread.currentThread().getUncaughtExceptionHandler();
			if (exceptionHandler != null)
				exceptionHandler.uncaughtException(Thread.currentThread(), e);
		} catch (Exception e) {
			throw new AssertionError("Unable to invoke callback method ("
					+ target.getClass().getName() + "."
					+ callbackMethod.getName() + ".): " + e);
		} finally {
			if (overrideAccess)
				callbackMethod.setAccessible(false);
		}
	}

	@Override
	public String toString() {
		return "[Callback " + target.getClass().getName() + "." + callbackMethod.getName() + "]";
	}
}
