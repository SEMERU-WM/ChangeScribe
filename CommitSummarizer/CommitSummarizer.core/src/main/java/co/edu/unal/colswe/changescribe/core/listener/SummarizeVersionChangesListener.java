package co.edu.unal.colswe.changescribe.core.listener;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import changescribe.core.preferences.PreferenceConstants;
import co.edu.unal.colswe.changescribe.core.Activator;
import co.edu.unal.colswe.changescribe.core.Messages;
import co.edu.unal.colswe.changescribe.core.summarizer.SummarizeChanges;
import co.edu.unal.colswe.changescribe.core.ui.DescribeVersionsDialog;
import co.edu.unal.colswe.changescribe.core.ui.IDialog;

public class SummarizeVersionChangesListener implements SelectionListener {

	private IDialog changedListDialog;
	
	public SummarizeVersionChangesListener(IDialog changedListDialog) {
		super();
		this.changedListDialog = changedListDialog;
	}

	public void widgetSelected(SelectionEvent e) {
		if(changedListDialog.getSelectedFiles() != null && changedListDialog.getSelectedFiles().length > 0) {
			IPreferenceStore store = Activator.getDefault().getPreferenceStore();

			boolean filtering = store.getBoolean(PreferenceConstants.P_FILTER_COMMIT_MESSAGE);
			double factor = store.getDouble(PreferenceConstants.P_FILTER_FACTOR);
			
			SummarizeChanges summarizer = new SummarizeChanges(changedListDialog.getGit(), filtering, factor,
					changedListDialog.getOlderVersionText().getText(), changedListDialog.getNewerVersionText().getText());
			summarizer.setChangedListDialog(changedListDialog);
			
			summarizer.summarize(changedListDialog.getSelectedFiles());
		} else {
			MessageDialog.openInformation(changedListDialog.getShell(), Messages.INFORMATION,
					Messages.FilesChangedListDialog_EmptySelection);
			
		}
	}

	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public DescribeVersionsDialog getChangedListDialog() {
		return (DescribeVersionsDialog) changedListDialog;
	}

	public void setChangedListDialog(DescribeVersionsDialog changedListDialog) {
		this.changedListDialog = changedListDialog;
	}
}