package co.edu.unal.colswe.CommitSummarizer.core.commitsignature;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class InformationDialog extends TitleAreaDialog {
	
	private FormToolkit toolkit;

	public InformationDialog(Shell shell) {
		super(shell);
	}
	
	
	 @Override
	 protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		parent.getShell().setText("Help information");
		
		container = toolkit.createComposite(container);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(container);
		toolkit.paintBordersFor(container);
		GridLayoutFactory.swtDefaults().applyTo(container);
		
		String html = "<HTML><HEAD><TITLE>HTML Test</TITLE></HEAD><BODY>";
		for (int i = 0; i < 100; i++) html += "<P>This is line "+i+"</P>";
		html += "</BODY></HTML>";
	    Browser browser = new Browser(container, SWT.NONE);
	    browser.setText(html);
	    
	    toolkit.adapt(browser, true, true);
	    browser.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
	    
		return container;
	}
	 
	@Override
	protected Control createContents(Composite parent) {
		toolkit = new FormToolkit(parent.getDisplay());
		parent.addDisposeListener(new DisposeListener() {

			public void widgetDisposed(DisposeEvent e) {
				toolkit.dispose();
			}
		});
		return super.createContents(parent);
	}

}
