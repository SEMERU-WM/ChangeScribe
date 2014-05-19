package co.edu.unal.colswe.changescribe.core.util;

/**
 * Preferences used by the EGit UI plug-in.
 * <p>
 * All plug-in preferences shall be referenced by a constant in this class.
 */
public class UIPreferences {
	/** */
	public final static String RESOURCEHISTORY_SHOW_RELATIVE_DATE = "resourcehistory_show_relative_date"; //$NON-NLS-1$
	/** */
	public final static String RESOURCEHISTORY_SHOW_EMAIL_ADDRESSES = "resourcehistory_show_email_addresses"; //$NON-NLS-1$
	/** */
	public final static String RESOURCEHISTORY_SHOW_NOTES = "resourcehistory_show_notes"; //$NON-NLS-1$
	/** */
	public final static String RESOURCEHISTORY_SHOW_COMMENT_WRAP = "resourcehistory_show_comment_wrap"; //$NON-NLS-1$
	/** */
	public static final String RESOURCEHISTORY_SHOW_COMMENT_FILL = "resourcehistory_fill_comment_paragraph"; //$NON-NLS-1$
	/** */
	public final static String RESOURCEHISTORY_SHOW_REV_DETAIL = "resourcehistory_show_rev_detail"; //$NON-NLS-1$
	/** */
	public final static String RESOURCEHISTORY_SHOW_REV_COMMENT = "resourcehistory_show_rev_comment"; //$NON-NLS-1$
	/** */
	public final static String RESOURCEHISTORY_GRAPH_SPLIT = "resourcehistory_graph_split"; //$NON-NLS-1$
	/** */
	public final static String RESOURCEHISTORY_REV_SPLIT = "resourcehistory_rev_split"; //$NON-NLS-1$
	/** */
	public final static String RESOURCEHISTORY_SHOW_TOOLTIPS = "resourcehistory_show_tooltips"; //$NON-NLS-1$
	/** */
	public final static String RESOURCEHISTORY_SHOW_FINDTOOLBAR = "resourcehistory_show_findtoolbar"; //$NON-NLS-1$
	/** */
	public final static String RESOURCEHISTORY_SHOW_ALL_BRANCHES = "resourcehistory_show_all_branches"; //$NON-NLS-1$
	/** */
	public final static String RESOURCEHISTORY_SHOW_ADDITIONAL_REFS = "resourcehistory_show_additionalrefs"; //$NON-NLS-1$
	/** */
	public static final String RESOURCEHISTORY_FOLLOW_RENAMES = "resourcehistory_follow_renames"; //$NON-NLS-1$
	/** */
	public final static String RESOURCEHISTORY_COMPARE_MODE = "resourcehistory_compare_mode"; //$NON-NLS-1$
	/** */
	public final static String FINDTOOLBAR_IGNORE_CASE = "findtoolbar_ignore_case"; //$NON-NLS-1$
	/** */
	public final static String FINDTOOLBAR_COMMIT_ID = "findtoolbar_commit_id"; //$NON-NLS-1$
	/** */
	public final static String FINDTOOLBAR_COMMENTS = "findtoolbar_comments"; //$NON-NLS-1$
	/** */
	public final static String FINDTOOLBAR_AUTHOR = "findtoolbar_author"; //$NON-NLS-1$
	/** */
	public final static String FINDTOOLBAR_COMMITTER = "findtoolbar_committer"; //$NON-NLS-1$
	/** */
	public final static String FINDTOOLBAR_FIND_IN = "findtoolbar_find_in"; //$NON-NLS-1$
	/** */
	public final static String COMMIT_DIALOG_HARD_WRAP_MESSAGE = "commit_dialog_hard_wrap_message"; //$NON-NLS-1$
	/** */
	public final static String COMMIT_DIALOG_SIGNED_OFF_BY = "commit_dialog_signed_off_by"; //$NON-NLS-1$
	/** */
	public final static String COMMIT_DIALOG_HISTORY_SIZE = "commit_dialog_history_size"; //$NON-NLS-1$
	/** */
	public final static String COMMIT_DIALOG_HISTORY_MESSAGES = "commit_dialog_history_messages"; //$NON-NLS-1$
	/** */
	public final static String COMMIT_DIALOG_INCLUDE_UNTRACKED = "commit_dialog_include_untracked"; //$NON-NLS-1$
	/** */
	public final static String CHECKOUT_PROJECT_RESTORE = "restore_projects_on_checkout"; //$NON-NLS-1$
	/** */
	public final static String THEME_CommitGraphNormalFont = "org.eclipse.egit.ui.CommitGraphNormalFont"; //$NON-NLS-1$
	/** */
	public final static String THEME_CommitGraphHighlightFont = "org.eclipse.egit.ui.CommitGraphHighlightFont"; //$NON-NLS-1$
	/** */
	public final static String THEME_CommitMessageFont = "org.eclipse.egit.ui.CommitMessageFont"; //$NON-NLS-1$
	/** */
	public final static String THEME_CommitMessageEditorFont = "org.eclipse.egit.ui.CommitMessageEditorFont"; //$NON-NLS-1$
	/** */
	public final static String THEME_UncommittedChangeForegroundColor = "org.eclipse.egit.ui.UncommittedChangeForegroundColor"; //$NON-NLS-1$
	/** */
	public final static String THEME_UncommittedChangeBackgroundColor = "org.eclipse.egit.ui.UncommittedChangeBackgroundColor"; //$NON-NLS-1$
	/** */
	public final static String THEME_UncommittedChangeFont = "org.eclipse.egit.ui.UncommittedChangeFont"; //$NON-NLS-1$
	/** */
	public final static String THEME_DiffHunkBackgroundColor = "org.eclipse.egit.ui.DiffHunkBackgroundColor"; //$NON-NLS-1$
	/** */
	public final static String THEME_DiffHunkForegroundColor = "org.eclipse.egit.ui.DiffHunkForegroundColor"; //$NON-NLS-1$
	/** */
	public final static String THEME_DiffHeadlineBackgroundColor = "org.eclipse.egit.ui.DiffHeadlineBackgroundColor"; //$NON-NLS-1$
	/** */
	public final static String THEME_DiffHeadlineForegroundColor = "org.eclipse.egit.ui.DiffHeadlineForegroundColor"; //$NON-NLS-1$
	/** */
	public final static String THEME_DiffHeadlineFont = "org.eclipse.egit.ui.DiffHeadlineFont"; //$NON-NLS-1$
	/** */
	public final static String THEME_DiffAddBackgroundColor = "org.eclipse.egit.ui.DiffAddBackgroundColor"; //$NON-NLS-1$
	/** */
	public final static String THEME_DiffAddForegroundColor = "org.eclipse.egit.ui.DiffAddForegroundColor"; //$NON-NLS-1$
	/** */
	public final static String THEME_DiffRemoveBackgroundColor = "org.eclipse.egit.ui.DiffRemoveBackgroundColor"; //$NON-NLS-1$
	/** */
	public final static String THEME_DiffRemoveForegroundColor = "org.eclipse.egit.ui.DiffRemoveForegroundColor"; //$NON-NLS-1$
	/** */
	public final static String THEME_IgnoredResourceForegroundColor = "org.eclipse.egit.ui.IgnoredResourceForegroundColor"; //$NON-NLS-1$
	/** */
	public final static String THEME_IgnoredResourceBackgroundColor = "org.eclipse.egit.ui.IgnoredResourceBackgroundColor"; //$NON-NLS-1$
	/** */
	public final static String THEME_IgnoredResourceFont = "org.eclipse.egit.ui.IgnoredResourceFont"; //$NON-NLS-1$
	/** */
	public final static String DECORATOR_RECOMPUTE_ANCESTORS = "decorator_recompute_ancestors"; //$NON-NLS-1$
	/** */
	public final static String DECORATOR_FILETEXT_DECORATION = "decorator_filetext_decoration"; //$NON-NLS-1$
	/** */
	public final static String DECORATOR_FOLDERTEXT_DECORATION = "decorator_foldertext_decoration"; //$NON-NLS-1$
	/** */
	public final static String DECORATOR_PROJECTTEXT_DECORATION = "decorator_projecttext_decoration"; //$NON-NLS-1$
	/** */
	public final static String DECORATOR_SHOW_TRACKED_ICON = "decorator_show_tracked_icon"; //$NON-NLS-1$
	/** */
	public final static String DECORATOR_SHOW_UNTRACKED_ICON = "decorator_show_untracked_icon"; //$NON-NLS-1$
	/** */
	public final static String DECORATOR_SHOW_STAGED_ICON = "decorator_show_staged_icon"; //$NON-NLS-1$
	/** */
	public final static String DECORATOR_SHOW_CONFLICTS_ICON = "decorator_show_conflicts_icon"; //$NON-NLS-1$
	/** */
	public final static String DECORATOR_SHOW_ASSUME_VALID_ICON = "decorator_show_assume_valid_icon"; //$NON-NLS-1$
	/** */
	public final static String DECORATOR_SHOW_DIRTY_ICON = "decorator_show_dirty_icon"; //$NON-NLS-1$
	/** */
	public final static String SYNC_VIEW_CHANGESET_LABEL_FORMAT = "sync_view_changeset_pattern"; //$NON-NLS-1$
	/** */
	public final static String SYNC_VIEW_ALWAYS_SHOW_CHANGESET_MODEL = "sync_view_show_changeset_model"; //$NON-NLS-1$
	/** */
	public final static String SYNC_VIEW_LAST_SELECTED_MODEL = "sync_view_last_selected_model"; //$NON-NLS-1$
	/** */
	public static final String SYNC_VIEW_FETCH_BEFORE_LAUNCH = "sync_view_fetch_before_launch"; //$NON-NLS-1$
	/** */
	public final static String DATE_FORMAT = "date_format"; //$NON-NLS-1$
	/** */
	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";  //$NON-NLS-1$
	/** */
	public static final String DEFAULT_CHANGESET_FORMAT = "[{author}] ({date}) {short_message}"; //$NON-NLS-1$
	/** */
	public static final String REFESH_ON_INDEX_CHANGE = "refesh_on_index_change"; //$NON-NLS-1$
	/** */
	public static final String REFESH_ONLY_WHEN_ACTIVE = "refesh_only_when_active"; //$NON-NLS-1$
	/** */
	public static final String REMOTE_CONNECTION_TIMEOUT = "remote_connection_timeout"; //$NON-NLS-1$

	/**
	 * When reading this preference, use
	 * {@link UIUtils#getDefaultRepositoryDir()} instead (for variable
	 * substitution).
	 */
	public static final String DEFAULT_REPO_DIR = "default_repository_dir"; //$NON-NLS-1$

	/** */
	public static final String MERGE_MODE = "merge_mode"; //$NON-NLS-1$
	/** */
	public static final String SHOW_REBASE_CONFIRM = "show_rebase_confirm"; //$NON-NLS-1$
	/** */
	public static final String SHOW_INITIAL_CONFIG_DIALOG = "show_initial_config_dialog"; //$NON-NLS-1$
	/** */
	public static final String SHOW_HOME_DIR_WARNING = "show_home_drive_warning"; //$NON-NLS-1$
	/** */
	public static final String SHOW_GIT_PREFIX_WARNING = "show_git_prefix_warning"; //$NON-NLS-1$
	/** */
	public static final String SHOW_DETACHED_HEAD_WARNING = "show_detached_head_warning"; //$NON-NLS-1$
	/** */
	public static final String TREE_COMPARE_SHOW_EQUALS = "CompareTreeView_ShowEquals"; //$NON-NLS-1$
	/** */
	public static final String HISTORY_MAX_NUM_COMMITS = "HistoryView_MaxNumberOfCommmits"; //$NON-NLS-1$
	/** */
	public static final String HISTORY_MAX_TAG_LENGTH = "HistoryView_MaxTagLength"; //$NON-NLS-1$
	/** */
	public static final String HISTORY_MAX_BRANCH_LENGTH = "HistoryView_MaxBranchLength"; //$NON-NLS-1$
	/** */
	public static final String HISTORY_SHOW_TAG_SEQUENCE = "HistoryView_ShowTagSequence"; //$NON-NLS-1$
	/** */
	public static final String STAGING_VIEW_SHOW_NEW_COMMITS = "StagingView_ShowNewCommits"; //$NON-NLS-1$
	/** */
	public static final String STAGING_VIEW_COLUMN_LAYOUT = "StagingView_ColumnLayout"; //$NON-NLS-1$
	/** */
	public static final String STAGING_VIEW_SYNC_SELECTION = "StagingView_SyncWithSelection"; //$NON-NLS-1$
	/** */
	public static final String STAGING_VIEW_FILENAME_MODE = "StagingView_FileNameMode"; //$NON-NLS-1$
	/** */
	public static final String STAGING_VIEW_PRESENTATION = "StagingView_Presentation"; //$NON-NLS-1$
	/** */
	public static final String PAGE_COMMIT_PREFERENCES = "org.eclipse.egit.ui.internal.preferences.CommitDialogPreferencePage"; //$NON-NLS-1$
	
	public static final String PAGE_COMMIT_PREFERENCES_SUMMARY = "changescribe.core.preferences.PreferencePage";
	/** */
	public static final String BLAME_IGNORE_WHITESPACE = "Blame_IgnoreWhitespace"; //$NON-NLS-1$
	/** */
	public static final String CLONE_WIZARD_STORE_SECURESTORE = "CloneWizard_StoreInSecureStore"; //$NON-NLS-1$
	/** */
	public static final String CLONE_WIZARD_IMPORT_PROJECTS = "CloneWizard_ImportProjects"; //$NON-NLS-1$
	/** */
	public static final String CLONE_WIZARD_SHOW_DETAILED_FAILURE_DIALOG = "CloneWizard_ShowDetailedFailureDialog"; //$NON-NLS-1$

	/**
	 * Converts a persisted String separated with commas to an integer array
	 *
	 * @param value
	 *            the String value
	 * @param cnt
	 *            number of entries in the returned array
	 * @return the preference values for the array.
	 */
	public static int[] stringToIntArray(final String value, final int cnt) {
		final int[] r = new int[cnt];
		if (value != null) {
			final String[] e = value.split(","); //$NON-NLS-1$
			for (int i = 0; i < Math.min(e.length, r.length); i++)
				r[i] = Integer.parseInt(e[i].trim());
		}
		return r;
	}

	/**
	 * Converts an integer array into a String separated by commas
	 *
	 * @param data
	 *            integers to store
	 * @return the String
	 */
	public static String intArrayToString(final int[] data) {
		final StringBuilder s = new StringBuilder();
		for (int i = 0; i < data.length; i++) {
			if (i > 0)
				s.append(',');
			s.append(data[i]);
		}
		return s.toString();
	}
}
