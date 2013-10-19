package co.edu.unal.colswe.CommitSummarizer.core.commitsignature;
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.browser.*;

public class Snippet270 {

static final int BROWSER_STYLE = SWT.NONE;

public static void main(String[] args) {
	Display display = new Display();
	Shell shell = new Shell(display);
	shell.setText("Main Window");
	shell.setLayout(new FillLayout());
	final Browser browser;
	try {
		browser = new Browser(shell, BROWSER_STYLE);
	} catch (SWTError e) {
		System.out.println("Could not instantiate Browser: " + e.getMessage());
		display.dispose();
		return;
	}
	initialize(display, browser);
	shell.open();
	browser.setUrl("http://www.eclipse.org");
	while (!shell.isDisposed()) {
		if (!display.readAndDispatch())
			display.sleep();
		}
		display.dispose();
	}

/* register WindowEvent listeners */
static void initialize(final Display display, Browser browser) {
	browser.addOpenWindowListener(new OpenWindowListener() {
		public void open(WindowEvent event) {
			if (!event.required) return;	/* only do it if necessary */
			Shell shell = new Shell(display);
			shell.setText("New Window");
			shell.setLayout(new FillLayout());
			Browser browser = new Browser(shell, BROWSER_STYLE);
			initialize(display, browser);
			event.browser = browser;
		}
	});
	browser.addVisibilityWindowListener(new VisibilityWindowListener() {
		public void hide(WindowEvent event) {
			Browser browser = (Browser)event.widget;
			Shell shell = browser.getShell();
			shell.setVisible(false);
		}
		public void show(WindowEvent event) {
			Browser browser = (Browser)event.widget;
			final Shell shell = browser.getShell();
			if (event.location != null) shell.setLocation(event.location);
			if (event.size != null) {
				Point size = event.size;
				shell.setSize(shell.computeSize(size.x, size.y));
			}
			shell.open();
		}
	});
	browser.addCloseWindowListener(new CloseWindowListener() {
		public void close(WindowEvent event) {
			Browser browser = (Browser)event.widget;
			Shell shell = browser.getShell();
			shell.close();
		}
	});
}
}