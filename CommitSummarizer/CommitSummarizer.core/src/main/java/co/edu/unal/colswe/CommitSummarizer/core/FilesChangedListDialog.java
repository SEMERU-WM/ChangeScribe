package co.edu.unal.colswe.CommitSummarizer.core;

import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ListSelectionDialog;

import co.edu.unal.colswe.CommitSummarizer.core.git.ChangedFile;

public class FilesChangedListDialog extends ListSelectionDialog {
	private Text text;

	public FilesChangedListDialog(Shell shell, Set<ChangedFile> differences) {
		super(shell, differences,
				new ArrayContentProvider(),
				new LabelProvider(), "Changes");

		setTitle("Commit changes");
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		
		Label lblCommitDescription = new Label(area, SWT.NONE);
		lblCommitDescription.setText("Commit Description");
		
		text = new Text(area, SWT.BORDER | SWT.MULTI);
		GridData gd_text = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_text.heightHint = 60;
		text.setLayoutData(gd_text);
		
		Button button = new Button(area, SWT.RIGHT);
		button.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		button.setText("Create Message");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println("Construction");
			}
		});

		return area;
	}
	
	public List getSelectedFiles() {
		return this.getInitialElementSelections();
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected void okPressed() {
		super.okPressed();
	}

}
