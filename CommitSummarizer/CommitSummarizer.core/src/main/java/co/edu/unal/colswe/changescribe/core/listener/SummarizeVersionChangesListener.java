package co.edu.unal.colswe.changescribe.core.listener;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import changescribe.core.preferences.PreferenceConstants;
import co.edu.unal.colswe.changescribe.core.Activator;
import co.edu.unal.colswe.changescribe.core.summarizer.SummarizeChanges;
import co.edu.unal.colswe.changescribe.core.ui.DescribeVersionsDialog;

public class SummarizeVersionChangesListener implements SelectionListener {

	private DescribeVersionsDialog changedListDialog;
	
	public SummarizeVersionChangesListener(DescribeVersionsDialog changedListDialog) {
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