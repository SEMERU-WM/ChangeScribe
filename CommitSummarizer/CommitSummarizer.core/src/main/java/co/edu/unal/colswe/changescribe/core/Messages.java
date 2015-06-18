package co.edu.unal.colswe.changescribe.core;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "co.edu.unal.colswe.changescribe.core.messages"; //$NON-NLS-1$
	
	public static String DescribeVersionsDialog_ComputeChanges;

	public static String DescribeVersionsDialog_EmptyNewerCommit;

	public static String DescribeVersionsDialog_EmptyOlderCommit;

	public static String DescribeVersionsDialog_NewerCommitId;

	public static String DescribeVersionsDialog_OlderCommitId;

	public static String FilesChangedListDialog_SelectAll;

	public static String FilesChangedListDialog_DeselectAll;

	public static String FilesChangedListDialog_ChangedFileListTitle;

	public static String FilesChangedListDialog_EmptyMessage;

	public static String FilesChangedListDialog_EmptySelection;

	public static String FilesChangedListDialog_FontType;

	public static String FilesChangedListDialog_Changes;

	public static String FilesChangedListDialog_CloseDialogWindow;

	public static String FilesChangedListDialog_Commit;

	public static String FilesChangedListDialog_CommitChanges;

	public static String FilesChangedListDialog_CommitMessage;

	public static String FilesChangedListDialog_CommitPush;

	public static String FilesChangedListDialog_ConfigureLink;

	public static String FilesChangedListDialog_DescribeChanges;

	public static String FilesChangedListDialog_EmptyAuthor;

	public static String FilesChangedListDialog_EmptyCommiter;

	public static String FilesChangedListDialog_Error;

	public static String FilesChangedListDialog_Path;

	public static String FilesChangedListDialog_Status;

	public static String HandlerUtil_RepositoryNotFound;

	public static String INFORMATION;
	
	public static String InformationDialog_Help;
	
	public static String SignatureCanvas_Amount;
	
	public static String SignatureCanvas_Stereotype;
	
	public static String SignatureCanvas_StereotypeDescription;
	
	public static String NO_FILES_SELECTED_TITLE;
	
	public static String NO_FILES_SELECTED_MESSAGE;
	
	public static String PreferencePage_Author;

	public static String PreferencePage_Commiter;

	public static String PreferencePage_FilterCommitMessage;

	public static String PreferencePage_FilterFactor;

	public static String PreferencePage_SectionTitle;

	public static String PreferencePage_ViewCommitSignature;

	public static String UIUtils_Help;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
