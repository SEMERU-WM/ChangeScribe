package co.edu.unal.colswe.changescribe.core;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "co.edu.unal.colswe.changescribe.core.messages"; //$NON-NLS-1$
	public static String INFORMATION;
	public static String InformationDialog_Help;
	public static String SignatureCanvas_Amount;
	public static String SignatureCanvas_Stereotype;
	public static String SignatureCanvas_StereotypeDescription;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
