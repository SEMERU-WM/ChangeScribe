package co.edu.unal.colswe.changescribe.core.util;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IWorkbenchWindow;

import co.edu.unal.colswe.changescribe.core.Messages;
import co.edu.unal.colswe.changescribe.core.ui.UIIcons;

public class UIUtils {
	public static void showInformationWindow(final IWorkbenchWindow window, final String title, final String message) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				MessageDialog.openInformation(window.getShell(), title, message);
		}});
	}
	
	/**
	 * Create a help button with the help icon on it.
	 * No action is associated with the button
	 * @param container parent container
	 * @return created button
	 */
	public static ToolItem createHelpButton(ToolBar filesToolbar) {
		ToolItem help = new ToolItem(filesToolbar,SWT.PUSH);
	    Image img = UIIcons.HELP.createImage(); 
	    help.setImage(img);
	    help.setToolTipText(Messages.UIUtils_Help);
	    return help;
	}
}
