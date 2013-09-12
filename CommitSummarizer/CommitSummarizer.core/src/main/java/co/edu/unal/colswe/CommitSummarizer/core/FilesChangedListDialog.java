package co.edu.unal.colswe.CommitSummarizer.core;

import java.util.Set;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListSelectionDialog;

import co.edu.unal.colswe.CommitSummarizer.core.git.ChangedFile;

public class FilesChangedListDialog extends ListSelectionDialog {

	public FilesChangedListDialog(Shell shell, Set<ChangedFile> differences) {
		super(shell, differences,
				new ArrayContentProvider(),
				new LabelProvider(), "Changes");

		setTitle("Commit changes");
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout(2, false);
		container.setLayoutData(new GridData(SWT.CHECK, SWT.CHECK, true, true));
		container.setLayout(layout);

		return area;
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
