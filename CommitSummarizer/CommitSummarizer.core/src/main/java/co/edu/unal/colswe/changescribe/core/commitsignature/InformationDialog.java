package co.edu.unal.colswe.changescribe.core.commitsignature;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;

import co.edu.unal.colswe.changescribe.core.Constants;
import co.edu.unal.colswe.changescribe.core.Messages;
import co.edu.unal.colswe.changescribe.core.ProjectInformation;

public class InformationDialog extends TitleAreaDialog {
	
	private static final int MIN_HEIGHT = 400;
	private FormToolkit toolkit;

	public InformationDialog(Shell shell) {
		super(shell);
		this.setHelpAvailable(false);
	}
	
	 @Override
	protected Control createDialogArea(Composite parent) {
		Browser browser = null;
		Composite container = (Composite) super.createDialogArea(parent);
		
		setTitle(Messages.INFORMATION);
		parent.getShell().setText(Messages.InformationDialog_Help);
		
		container = toolkit.createComposite(container);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(container);
		toolkit.paintBordersFor(container);
		GridLayoutFactory.swtDefaults().applyTo(container);

		Point size = container.getSize();
		container.setLayoutData(GridDataFactory.fillDefaults().grab(true, true)
				.hint(size).minSize(size.x, InformationDialog.MIN_HEIGHT)
				.align(SWT.FILL, SWT.FILL).create());

		try {
			browser = new Browser(container, SWT.NONE);
		} catch (SWTError e) {
			browser = new Browser(container, SWT.MOZILLA);
		}
		if (browser != null) {
			browser.setUrl(ProjectInformation
					.getAbsoluteURL(Constants.HELP_RESOURCES_DIR)); 
			toolkit.adapt(browser, true, true);
			browser.setLayoutData(GridDataFactory.fillDefaults()
					.grab(true, true).create());
		}

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
