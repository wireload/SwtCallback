SwtCallback
===========

Brevity is the essence of readability and leads to code which sets clear expectations for what should happen. SwtCallback was written to make SWT code, or anything using Runnables, more readable by eliminating repetitive anonymous classes. For instance instead of:

    void initWidgets() {
        /* ... */

        // Prime the timer.
        display.timerExec(INITIAL_DELAY, new Runnable() {
            public void run() {
                refresh();
            }
        });
    }

    void refresh() {
        /* ... update stuff ... */

        // Set the timer again.
        display.timerExec(TIMER_DELAY, new Runnable() {
            public void run() {
                refresh();
            }
        });
    }

You can do:

    void initWidgets() {
        /* ... */

        // Prime the timer.
        display.timerExec(INITIAL_DELAY, new Callback(this, "refresh"));
    }

    void refresh() {
        /* ... update stuff ... */

        // Set the timer again.
        display.timerExec(TIMER_DELAY, new Callback(this, "refresh"));
    }

For usage with SWT, callbacks transform this:

    Button eventButton = new Button(myShell, SWT.NONE);
    eventButton.addSelectionListener(new Listener() {
        public void handleEvent(Event event) {
            // Do something
        }
    });

Into this:

    Button eventButton = new Button(myShell, SWT.NONE);
    eventButton.addSelectionListener(new Callback(this, "doEventButtonClicked"));

## Building ##

You need to have an appropriate `SWT.jar` file in a folder named `lib/`. Then use the included ant
script to compile the jar file.

    ant jar

## Usage ##

Put `SwtCallback.jar` in the class path of your program. See `Demo.java` for a complete example.

## License ##

Free to use and modify under the terms of the BSD open source license.