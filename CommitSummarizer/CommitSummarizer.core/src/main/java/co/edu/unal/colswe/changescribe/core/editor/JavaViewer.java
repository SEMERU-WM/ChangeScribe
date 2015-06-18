package co.edu.unal.colswe.changescribe.core.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class JavaViewer {
	private Shell shell;
	private StyledText text;
	private Composite composite;
	private JavaLineStyler lineStyler = new JavaLineStyler();

	public void createStyledText() {
		setText(new StyledText(composite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL
				| SWT.H_SCROLL | SWT.WRAP));
		getText().addLineStyleListener(this.lineStyler);
		getText().setEditable(true);
		Color bg = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
		getText().setBackground(bg);
	}

	public Shell getShell() {
		return shell;
	}

	public void setShell(Shell shell) {
		this.shell = shell;
	}

	public StyledText getText() {
		return text;
	}

	public void setText(StyledText text) {
		this.text = text;
	}

	public Composite getComposite() {
		return composite;
	}

	public void setComposite(Composite composite) {
		this.composite = composite;
	}
}