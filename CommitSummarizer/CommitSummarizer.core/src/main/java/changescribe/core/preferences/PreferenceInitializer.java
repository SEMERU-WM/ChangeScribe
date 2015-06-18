package changescribe.core.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import co.edu.unal.colswe.changescribe.core.Activator;
import co.edu.unal.colswe.changescribe.core.Constants;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.P_FILTER_COMMIT_MESSAGE, Boolean.TRUE);
		store.setDefault(PreferenceConstants.P_FILTER_FACTOR, Constants.FILTER_FACTOR_DEFAULT);
		store.setDefault(PreferenceConstants.P_COMMIT_SIGNATURE_ACTIVE, Boolean.TRUE);
	}

}
