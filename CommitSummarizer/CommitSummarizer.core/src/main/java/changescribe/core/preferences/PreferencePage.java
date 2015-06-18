package changescribe.core.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.ScaleFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import co.edu.unal.colswe.changescribe.core.Activator;
import co.edu.unal.colswe.changescribe.core.Constants;
import co.edu.unal.colswe.changescribe.core.Messages;

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
		setDescription(Messages.PreferencePage_SectionTitle);
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
		BooleanFieldEditor commitSignatureActive = new BooleanFieldEditor(
				PreferenceConstants.P_COMMIT_SIGNATURE_ACTIVE,
				Messages.PreferencePage_ViewCommitSignature,
				getFieldEditorParent());
		addField(commitSignatureActive);
		
		BooleanFieldEditor filterCk = new BooleanFieldEditor(
				PreferenceConstants.P_FILTER_COMMIT_MESSAGE,
				Messages.PreferencePage_FilterCommitMessage,
				getFieldEditorParent());
		filterCk.setPropertyChangeListener(new IPropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		addField(filterCk);
		
		final ScaleFieldEditor scaleFieldEditor = new ScaleFieldEditor(PreferenceConstants.P_FILTER_FACTOR, Messages.PreferencePage_FilterFactor, getFieldEditorParent());
		scaleFieldEditor.setMinimum(Constants.SIGNATURE_MIN_VALUE);
		scaleFieldEditor.setMaximum(Constants.SIGNATURE_MAX_VALUE);
		scaleFieldEditor.setPageIncrement(Constants.SIGNATURE_INCREMENT);
		final StringFieldEditor fieldEditor = new StringFieldEditor(PreferenceConstants.P_FILTER_FACTOR, Constants.EMPTY_STRING, getFieldEditorParent());
		fieldEditor.setEnabled(false, getFieldEditorParent());
		scaleFieldEditor.getScaleControl().addListener(SWT.Selection,
				new Listener() {
					public void handleEvent(Event event) {
						int perspectiveValue = scaleFieldEditor.getScaleControl().getSelection() + scaleFieldEditor.getMinimum();
						fieldEditor.setStringValue(perspectiveValue + Constants.SPACE + Constants.PERCENTAJE);
					}
		});
		
		fieldEditor.setStringValue(Activator.getDefault().getPreferenceStore().getDouble(PreferenceConstants.P_FILTER_FACTOR) + Constants.SPACE + Constants.PERCENTAJE);
		
		addField(scaleFieldEditor);
		addField(new StringFieldEditor(PreferenceConstants.P_AUTHOR, Messages.PreferencePage_Author, getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.P_COMMITER, Messages.PreferencePage_Commiter, getFieldEditorParent()));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
	
}