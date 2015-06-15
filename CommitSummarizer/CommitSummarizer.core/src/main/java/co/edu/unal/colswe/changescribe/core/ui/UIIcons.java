package co.edu.unal.colswe.changescribe.core.ui;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;

import co.edu.unal.colswe.changescribe.core.Activator;

/**
 * Icons for the the Eclipse plugin. Mostly decorations.
 */
public class UIIcons {

	/** Decoration for resource in the index but not yet committed. */
	public final static ImageDescriptor OVR_STAGED;

	/** Decoration for resource added to index but not yet committed. */
	public final static ImageDescriptor OVR_STAGED_ADD;

	/** Decoration for resource removed from the index but not commit. */
	public final static ImageDescriptor OVR_STAGED_REMOVE;

	/** Decoration for resource that was removed and added with another name */
	public static final ImageDescriptor OVR_STAGED_RENAME;

	/** Decoration for resource not being tracked by Git */
	public final static ImageDescriptor OVR_UNTRACKED;

	/** Decoration for tracked resource with a merge conflict.  */
	public final static ImageDescriptor OVR_CONFLICT;

	/** Decoration for tracked resources that we want to ignore changes in. */
	public final static ImageDescriptor OVR_ASSUMEVALID;

	/** Decoration for tracked resources that are dirty. */
	public final static ImageDescriptor OVR_DIRTY;

	/** Decoration for warning **/
	public final static ImageDescriptor OVR_ERROR;

	/** Find icon */
	public final static ImageDescriptor ELCL16_FIND;
	/** Compare / View icon */
	public final static ImageDescriptor ELCL16_COMPARE_VIEW;
	/** Next arrow icon */
	public final static ImageDescriptor ELCL16_NEXT;
	/** Previous arrow icon */
	public final static ImageDescriptor ELCL16_PREVIOUS;
	/** Commit icon */
	public final static ImageDescriptor ELCL16_COMMIT;
	/** Comments icon */
	public final static ImageDescriptor ELCL16_COMMENTS;
	/** Author icon */
	public final static ImageDescriptor ELCL16_AUTHOR;
	/** Committer icon */
	public final static ImageDescriptor ELCL16_COMMITTER;
	/** Id icon */
	public final static ImageDescriptor ELCL16_ID;
	/** Delete icon */
	public final static ImageDescriptor ELCL16_DELETE;
	/** Add icon */
	public final static ImageDescriptor ELCL16_ADD;
	/** Trash icon */
	public final static ImageDescriptor ELCL16_TRASH;
	/** Clear icon */
	public final static ImageDescriptor ELCL16_CLEAR;
	/** Refresh icon */
	public final static ImageDescriptor ELCL16_REFRESH;
	/** Linked with icon */
	public final static ImageDescriptor ELCL16_SYNCED;
	/** Filter icon */
	public final static ImageDescriptor ELCL16_FILTER;

	/** Enabled, checked, checkbox image */
	public final static ImageDescriptor CHECKBOX_ENABLED_CHECKED;
	/** Enabled, unchecked, checkbox image */
	public final static ImageDescriptor CHECKBOX_ENABLED_UNCHECKED;
	/** Disabled, checked, checkbox image */
	public final static ImageDescriptor CHECKBOX_DISABLED_CHECKED;
	/** Disabled, unchecked, checkbox image */
	public final static ImageDescriptor CHECKBOX_DISABLED_UNCHECKED;
	/** Edit configuration */
	public final static ImageDescriptor EDITCONFIG;
	/** Create Patch Wizard banner */
	public final static ImageDescriptor WIZBAN_CREATE_PATCH;

	/** Import Wizard banner */
	public final static ImageDescriptor WIZBAN_IMPORT_REPO;

	/** Connect Wizard banner */
	public final static ImageDescriptor WIZBAN_CONNECT_REPO;

	/** History view, select all version in same project */
	public final static ImageDescriptor FILTERPROJECT;

	/** History view, select all version in same folder */
	public final static ImageDescriptor FILTERFOLDER;

	/** History view, select all version of resource */
	public final static ImageDescriptor FILTERRESOURCE;

	/** Import button */
	public final static ImageDescriptor FETCH;

	/** Import button */
	public final static ImageDescriptor PULL;

	/** Export button */
	public final static ImageDescriptor PUSH;

	/** Collapse all button */
	public final static ImageDescriptor COLLAPSEALL;

	/** Repository tree node */
	public final static ImageDescriptor REPOSITORY;

	/** Gerrit Repository tree node */
	public final static ImageDescriptor REPOSITORY_GERRIT;

	/** New Repository button */
	public final static ImageDescriptor NEW_REPOSITORY;

	/** Create Repository button */
	public final static ImageDescriptor CREATE_REPOSITORY;

	/** Remote Repository tree node */
	public final static ImageDescriptor REMOTE_REPOSITORY;

	/** Reset */
	public final static ImageDescriptor RESET;

	/** Remote Repository tree node */
	public final static ImageDescriptor REMOTE_SPEC;

	/** Branches tree node */
	public final static ImageDescriptor BRANCHES;

	/** Checked-out decorator for branch */
	public final static ImageDescriptor OVR_CHECKEDOUT;

	/** Tags icon */
	public final static ImageDescriptor TAGS;

	/** Tag icon */
	public final static ImageDescriptor TAG;

	/** Create Tag icon */
	public final static ImageDescriptor CREATE_TAG;

	/** Branch icon */
	public final static ImageDescriptor BRANCH;

	/** Create Branch icon */
	public final static ImageDescriptor CREATE_BRANCH;

	/** Clone Icon */
	public final static ImageDescriptor CLONEGIT;

	/** Changeset Icon */
	public final static ImageDescriptor CHANGESET;

	/** Gerrit Icon */
	public final static ImageDescriptor GERRIT;

	/** Expand all icon */
	public final static ImageDescriptor EXPAND_ALL;

	/** Checkout icon */
	public final static ImageDescriptor CHECKOUT;

	/** Signed Off By icon */
	public final static ImageDescriptor SIGNED_OFF;

	/** Check all icon */
	public final static ImageDescriptor CHECK_ALL;

	/** Uncheck all icon */
	public final static ImageDescriptor UNCHECK_ALL;

	/** Amend commit icon */
	public final static ImageDescriptor AMEND_COMMIT;

	/** Untracked file icon */
	public final static ImageDescriptor UNTRACKED_FILE;

	/** Commit note icon */
	public final static ImageDescriptor NOTE;

	/** Show Annotation icon */
	public final static ImageDescriptor ANNOTATE;

	/** Commit icon */
	public final static ImageDescriptor COMMIT;

	/** Rebase icon */
	public final static ImageDescriptor REBASE;

	/** Rebase continue icon */
	public final static ImageDescriptor REBASE_CONTINUE;

	/** Rebase skip icon */
	public final static ImageDescriptor REBASE_SKIP;

	/** Rebase abort icon */
	public final static ImageDescriptor REBASE_ABORT;

	/** Merge icon */
	public final static ImageDescriptor MERGE;

	/** Annotated tag icon */
	public final static ImageDescriptor TAG_ANNOTATED;

	/** Submodules icon */
	public final static ImageDescriptor SUBMODULES;

	/** Clean icon */
	public final static ImageDescriptor CLEAN;

	/** Stash icon */
	public final static ImageDescriptor STASH;

	/** Search commit icon */
	public final static ImageDescriptor SEARCH_COMMIT;

	/** Hierarchy layout icon */
	public final static ImageDescriptor HIERARCHY;

	/** Flat presentation icon */
	public final static ImageDescriptor FLAT;

	/** Compact tree presentation icon */
	public final static ImageDescriptor COMPACT;
	
	public static final ImageDescriptor HELP;
	
	public static final ImageDescriptor EXTRACT_DIFFERENCES;

	/** base URL */
	public final static URL base;

	static {
		base = init();
		OVR_STAGED = map("ovr/staged.gif"); //$NON-NLS-1$
		OVR_STAGED_ADD = map("ovr/staged_added.gif"); //$NON-NLS-1$
		OVR_STAGED_REMOVE = map("ovr/staged_removed.gif"); //$NON-NLS-1$
		OVR_STAGED_RENAME = map("ovr/staged_renamed.gif"); //$NON-NLS-1$
		OVR_UNTRACKED = map("ovr/untracked.gif"); //$NON-NLS-1$
		OVR_CONFLICT = map("ovr/conflict.gif"); //$NON-NLS-1$
		OVR_ASSUMEVALID = map("ovr/assume_valid.gif"); //$NON-NLS-1$
		OVR_DIRTY = map("ovr/dirty.gif"); //$NON-NLS-1$
		ELCL16_FIND = map("elcl16/find.gif"); //$NON-NLS-1$
		ELCL16_COMPARE_VIEW = map("elcl16/compare_view.gif"); //$NON-NLS-1$
		ELCL16_NEXT = map("elcl16/next.gif"); //$NON-NLS-1$
		ELCL16_PREVIOUS = map("elcl16/previous.gif"); //$NON-NLS-1$
		WIZBAN_CREATE_PATCH = map("wizban/createpatch_wizban.png"); //$NON-NLS-1$
		WIZBAN_IMPORT_REPO = map("wizban/import_wiz.png"); //$NON-NLS-1$
		WIZBAN_CONNECT_REPO = map("wizban/newconnect_wizban.png"); //$NON-NLS-1$
		EDITCONFIG = map("obj16/editconfig.gif"); //$NON-NLS-1$
		ELCL16_COMMIT = map("elcl16/commit.gif"); //$NON-NLS-1$
		ELCL16_COMMENTS = map("elcl16/comment.gif"); //$NON-NLS-1$
		ELCL16_AUTHOR = map("elcl16/author.gif"); //$NON-NLS-1$
		ELCL16_COMMITTER = map("elcl16/committer.gif"); //$NON-NLS-1$
		ELCL16_DELETE = map("elcl16/delete.gif"); //$NON-NLS-1$
		ELCL16_ADD = map("elcl16/add.gif"); //$NON-NLS-1$
		ELCL16_TRASH = map("elcl16/trash.gif"); //$NON-NLS-1$
		ELCL16_CLEAR = map("elcl16/clear.gif"); //$NON-NLS-1$
		ELCL16_REFRESH = map("elcl16/refresh.gif"); //$NON-NLS-1$
		ELCL16_SYNCED = map("elcl16/synced.gif"); //$NON-NLS-1$
		ELCL16_FILTER = map("elcl16/filter_ps.gif"); //$NON-NLS-1$
		ELCL16_ID = map("elcl16/sha1.gif"); //$NON-NLS-1$
		CHECKBOX_ENABLED_CHECKED = map("checkboxes/enabled_checked.gif"); //$NON-NLS-1$
		CHECKBOX_ENABLED_UNCHECKED = map("checkboxes/enabled_unchecked.gif"); //$NON-NLS-1$
		CHECKBOX_DISABLED_CHECKED = map("checkboxes/disabled_checked.gif"); //$NON-NLS-1$
		CHECKBOX_DISABLED_UNCHECKED = map("checkboxes/disabled_unchecked.gif"); //$NON-NLS-1$
		FILTERRESOURCE = map("elcl16/filterresource.gif"); //$NON-NLS-1$
		FILTERPROJECT = map("elcl16/filterproject.gif"); //$NON-NLS-1$
		FILTERFOLDER = map("elcl16/filterfolder.gif"); //$NON-NLS-1$
		FETCH = map("obj16/fetch.gif"); //$NON-NLS-1$
		PUSH = map("obj16/push.gif"); //$NON-NLS-1$
		PULL = map("obj16/pull.gif"); //$NON-NLS-1$
		REPOSITORY = map("obj16/repository_rep.gif"); //$NON-NLS-1$
		REPOSITORY_GERRIT = map("obj16/repository_gerrit.gif"); //$NON-NLS-1$
		NEW_REPOSITORY = map("etool16/newlocation_wiz.gif"); //$NON-NLS-1$
		REMOTE_REPOSITORY = map("obj16/remote_entry_tbl.gif"); //$NON-NLS-1$
		REMOTE_SPEC = map("obj16/remotespec.gif"); //$NON-NLS-1$
		BRANCHES = map("obj16/branches_obj.gif"); //$NON-NLS-1$
		OVR_CHECKEDOUT = map("ovr/checkedout_ov.gif"); //$NON-NLS-1$
		TAGS = map("obj16/tags.gif"); //$NON-NLS-1$
		TAG = map("obj16/version_rep.gif"); //$NON-NLS-1$
		CREATE_TAG = map("obj16/new_tag_obj.gif"); //$NON-NLS-1$
		BRANCH = map("obj16/branch_obj.gif"); //$NON-NLS-1$
		CREATE_BRANCH = map("obj16/new_branch_obj.gif"); //$NON-NLS-1$
		COLLAPSEALL = map("elcl16/collapseall.gif"); //$NON-NLS-1$
		CLONEGIT = map("obj16/cloneGit.gif"); //$NON-NLS-1$
		RESET = map("obj16/reset.gif"); //$NON-NLS-1$
		CHANGESET = map("obj16/changelog_obj.gif"); //$NON-NLS-1$
		GERRIT = map("obj16/gerrit_obj.gif"); //$NON-NLS-1$
		EXPAND_ALL = map("wizban/expandall.gif"); //$NON-NLS-1$
		CHECKOUT = map("obj16/checkout.gif"); //$NON-NLS-1$
		SIGNED_OFF = map("obj16/signed-off.png"); //$NON-NLS-1$
		CHECK_ALL = map("obj16/check_all.gif"); //$NON-NLS-1$
		UNCHECK_ALL = map("obj16/uncheck_all.gif"); //$NON-NLS-1$
		AMEND_COMMIT = map("obj16/commit_amend.gif"); //$NON-NLS-1$
		UNTRACKED_FILE = map("obj16/untracked_file.gif"); //$NON-NLS-1$
		NOTE = map("obj16/note.png"); //$NON-NLS-1$
		ANNOTATE = map("etool16/annotate.gif"); //$NON-NLS-1$
		COMMIT = map("obj16/commit.gif"); //$NON-NLS-1$
		REBASE = map("obj16/rebase.gif"); //$NON-NLS-1$
		REBASE_CONTINUE = map("elcl16/continue.gif"); //$NON-NLS-1$
		REBASE_SKIP = map("elcl16/skip.gif"); //$NON-NLS-1$
		REBASE_ABORT = map("elcl16/progress_stop.gif"); //$NON-NLS-1$
		OVR_ERROR = map("ovr/error.png"); //$NON-NLS-1$
		MERGE = map("obj16/merge.gif"); //$NON-NLS-1$
		TAG_ANNOTATED = map("obj16/annotated-tag.gif"); //$NON-NLS-1$
		CREATE_REPOSITORY = map("etool16/createRepository.gif"); //$NON-NLS-1$
		SUBMODULES = map("obj16/submodules.gif"); //$NON-NLS-1$
		CLEAN = map("obj16/clean_obj.gif"); //$NON-NLS-1$
		STASH = map("obj16/stash.png"); //$NON-NLS-1$
		SEARCH_COMMIT = map("obj16/search-commit.gif"); //$NON-NLS-1$
		HIERARCHY = map("elcl16/hierarchicalLayout.gif"); //$NON-NLS-1$
		FLAT = map("elcl16/flatLayout.gif"); //$NON-NLS-1$
		COMPACT = map("elcl16/compactLayout.gif"); //$NON-NLS-1$
		HELP = map("etool16/help.gif"); //$NON-NLS-1$
		EXTRACT_DIFFERENCES = map("etool16/diff.gif"); //$NON-NLS-1$
	}

	private static ImageDescriptor map(final String icon) {
		if (base != null)
			try {
				return ImageDescriptor.createFromURL(new URL(base, icon));
			} catch (MalformedURLException mux) {
				mux.printStackTrace();
			}
		return ImageDescriptor.getMissingImageDescriptor();
	}

	private static URL init() {
		try {
			return new URL(Activator.getDefault().getBundle().getEntry("/"), //$NON-NLS-1$
					"icons/"); //$NON-NLS-1$
		} catch (MalformedURLException mux) {
			mux.printStackTrace();
			return null;
		}
	}
}