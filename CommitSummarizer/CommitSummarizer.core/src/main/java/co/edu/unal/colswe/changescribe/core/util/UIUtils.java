package co.edu.unal.colswe.changescribe.core.util;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;

public class UIUtils {
	public static void showInformationWindow(final IWorkbenchWindow window, final String title, final String message) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				MessageDialog.openInformation(window.getShell(), title, message);
		}});
	}
}
