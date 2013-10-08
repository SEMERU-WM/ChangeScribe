package co.edu.unal.colswe.CommitSummarizer.core.listener;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import co.edu.unal.colswe.CommitSummarizer.core.FilesChangedListDialog;
import co.edu.unal.colswe.CommitSummarizer.core.summarizer.SummarizeChanges;

public class SummarizeChangeListener implements SelectionListener {

	private FilesChangedListDialog changedListDialog;
	
	public SummarizeChangeListener(FilesChangedListDialog changedListDialog) {
		super();
		this.changedListDialog = changedListDialog;
	}

	public void widgetSelected(SelectionEvent e) {
		if(changedListDialog.getSelectedFiles() != null && changedListDialog.getSelectedFiles().length > 0) {
			SummarizeChanges summarizer = new SummarizeChanges(changedListDialog.getGit());
			summarizer.setChangedListDialog(changedListDialog);
			
			summarizer.summarize(changedListDialog.getSelectedFiles());
			//changedListDialog.getText().setText(summarizer.getComment().toString());
			
		} else {
			MessageDialog.openInformation(changedListDialog.getShell(), "Information",
					"You do not select any change");
			
		}
		
		
	}

	public void widgetDefaultSelected(SelectionEvent e) {

	}

	public FilesChangedListDialog getChangedListDialog() {
		return changedListDialog;
	}

	public void setChangedListDialog(FilesChangedListDialog changedListDialog) {
		this.changedListDialog = changedListDialog;
	}
}