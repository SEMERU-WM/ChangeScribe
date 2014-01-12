package commitsummarizer.core.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import co.edu.unal.colswe.CommitSummarizer.core.Activator;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Setting preferences of Commit Summarize plugin");
		
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
		BooleanFieldEditor filterCk = new BooleanFieldEditor(
				PreferenceConstants.P_FILTER_COMMIT_MESSAGE,
				"&Filter commit message",
				getFieldEditorParent());
		addField(filterCk);
		
		final StringFieldEditor fieldEditor = new StringFieldEditor(PreferenceConstants.P_FILTER_FACTOR, "&Filter factor:", getFieldEditorParent());
		
		addField(fieldEditor);
		addField(new StringFieldEditor(PreferenceConstants.P_AUTHOR, "&Author:", getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.P_COMMITER, "&Commiter:", getFieldEditorParent()));
		
		/*filterCk.setPropertyChangeListener(new IPropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if(fieldEditor != null) {
					fieldEditor.setEnabled((boolean) event.getNewValue(), getFieldEditorParent());
				}
			}
		});*/

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
	
}