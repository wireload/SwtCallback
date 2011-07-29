package net.wireload.swtcallback;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

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
 * A simple demo of the SwtCallback library.
 */
public class Demo {
	private Shell shell;
	private Display display;

	public static void main(String[] args) {
		Demo demo = new Demo();
		demo.open();
	}

	private void open() {
		shell.open();
		shell.setFocus();

		while (!shell.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();
	}

	/**
	 * Initialize a new demo.
	 */
	public Demo() {
		display = Display.getDefault();
		shell = new Shell(display);
		shell.setText("SwtCallback Demo");
		shell.setSize(200, 70);
		Callback.add(shell, SWT.Close, this, "quit");

		FormLayout layout = new FormLayout();
		shell.setLayout(layout);

		Button myButton = new Button(shell, SWT.NONE);
		FormData data = new FormData();
		myButton.setText("Click Me");
		data.left = new FormAttachment(0);
		data.right = new FormAttachment(100);
		myButton.setLayoutData(data);
		Callback.add(myButton, SWT.Selection, this, "myButtonSelected");
	}

	/**
	 * Called when myButton is selected.
	 */
	@SuppressWarnings("unused")
	private void myButtonSelected() {
		shell.dispose();
	}

	/**
	 * Called when the window receives a close event.
	 */
	@SuppressWarnings("unused")
	private void quit() {
		shell.dispose();
	}
}
