package co.edu.unal.colswe.changescribe.core.listener;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import co.edu.unal.colswe.changescribe.core.DescribeVersionsDialog;
import co.edu.unal.colswe.changescribe.core.summarizer.SummarizeChangesTMP;

public class SummarizeVersionChangesListener implements SelectionListener {

	private DescribeVersionsDialog changedListDialog;
	
	public SummarizeVersionChangesListener(DescribeVersionsDialog changedListDialog) {
		super();
		this.changedListDialog = changedListDialog;
	}

	public void widgetSelected(SelectionEvent e) {
		if(changedListDialog.getSelectedFiles() != null && changedListDialog.getSelectedFiles().length > 0) {
			SummarizeChangesTMP summarizer = new SummarizeChangesTMP(changedListDialog.getGit());
			summarizer.setChangedListDialog(changedListDialog);
			
			summarizer.summarize(changedListDialog.getSelectedFiles());
		} else {
			MessageDialog.openInformation(changedListDialog.getShell(), "Information",
					"You do not select any change");
			
		}
	}

	public void widgetDefaultSelected(SelectionEvent e) {

	}

	public DescribeVersionsDialog getChangedListDialog() {
		return changedListDialog;
	}

	public void setChangedListDialog(DescribeVersionsDialog changedListDialog) {
		this.changedListDialog = changedListDialog;
	}
}