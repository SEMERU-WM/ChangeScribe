package co.edu.unal.colswe.CommitSummarizer.core;

import java.util.Arrays;
import java.util.Set;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jgit.api.Git;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ListSelectionDialog;

import co.edu.unal.colswe.CommitSummarizer.core.git.ChangedFile;
import co.edu.unal.colswe.CommitSummarizer.core.listener.SummarizeChangeListener;

public class FilesChangedListDialog extends ListSelectionDialog {
	private Text text;
	private Git git;

	public FilesChangedListDialog(Shell shell, Set<ChangedFile> differences, Git git) {
		super(shell, differences,
				new ArrayContentProvider(),
				new LabelProvider(), "Changes");
		this.git = git;
		setTitle("Commit changes");
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		
		Label lblCommitDescription = new Label(area, SWT.NONE);
		lblCommitDescription.setText("Commit Description");
		
		setText(new Text(area, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL));
		GridData gd_text = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_text.widthHint = 608;
		gd_text.heightHint = 114;
		getText().setLayoutData(gd_text);
		
		Button button = new Button(area, SWT.RIGHT);
		button.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		button.setText("Create Message");
		button.addSelectionListener(new SummarizeChangeListener(this));
		

		return area;
	}
	
	public ChangedFile[] getSelectedFiles() {
		return Arrays.copyOf(this.getViewer().getCheckedElements(), this.getViewer().getCheckedElements().length, ChangedFile[].class);
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected void okPressed() {
		super.okPressed();
	}

	public Git getGit() {
		return git;
	}

	public void setGit(Git git) {
		this.git = git;
	}

	public Text getText() {
		return text;
	}

	public void setText(Text text) {
		this.text = text;
	}

}
