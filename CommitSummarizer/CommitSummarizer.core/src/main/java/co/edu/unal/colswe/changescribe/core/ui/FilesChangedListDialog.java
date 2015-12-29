package co.edu.unal.colswe.changescribe.core.ui;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DecoratingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.UnmergedPathsException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;

import changescribe.core.preferences.PreferenceConstants;
import co.edu.unal.colswe.changescribe.core.Activator;
import co.edu.unal.colswe.changescribe.core.Constants;
import co.edu.unal.colswe.changescribe.core.Messages;
import co.edu.unal.colswe.changescribe.core.commitsignature.InformationDialog;
import co.edu.unal.colswe.changescribe.core.commitsignature.SignatureCanvas;
import co.edu.unal.colswe.changescribe.core.decorator.ProblemLabelDecorator;
import co.edu.unal.colswe.changescribe.core.editor.JavaViewer;
import co.edu.unal.colswe.changescribe.core.git.ChangedFile;
import co.edu.unal.colswe.changescribe.core.listener.SummarizeChangeListener;
import co.edu.unal.colswe.changescribe.core.stereotype.taxonomy.MethodStereotype;
import co.edu.unal.colswe.changescribe.core.util.UIPreferences;
import co.edu.unal.colswe.changescribe.core.util.UIUtils;

public class FilesChangedListDialog extends TitleAreaDialog implements IDialog {
	public static final int COMMIT_AND_PUSH_ID = 30;
	private static final String DIALOG_SETTINGS_SECTION_NAME = Activator.getDefault() + ".COMMIT_DIALOG_SECTION"; //$NON-NLS-1$
	private StyledText text;
	private Git git;
	private IJavaProject selection;
	private JavaViewer editor;
	private ListSelectionDialog listSelectionDialog;
	private FormToolkit toolkit;
	private Section filesSection;
	private CachedCheckboxTreeViewer filesViewer;
	private Set<ChangedFile> items;
	private Button commitButton;
	private Button commitAndPushButton;
	private TreeMap<MethodStereotype, Integer> signatureMap;
	private SignatureCanvas signatureCanvas;
	private boolean isPushRequested;
	private Text authorText;
	private Text committerText;
	private String committer = null;
	private String author = null;
	private SashForm sashForm;
	private Composite messageAndPersonArea;

	public FilesChangedListDialog(Shell shell, Set<ChangedFile> differences, Git git, IJavaProject selection) {
		super(shell);
		this.items = differences;
		listSelectionDialog = new ListSelectionDialog(shell, differences,
				new ArrayContentProvider(),
				new LabelProvider(), Messages.FilesChangedListDialog_Changes);
		this.git = git;
		this.setSelection(selection);
		this.setHelpAvailable(false);
		setAuthor(Constants.ANONYMOUS); 
		setCommitter(Constants.ANONYMOUS); 
		
		Activator.getDefault().getPreferenceStore().addPropertyChangeListener(new IPropertyChangeListener() {
		    @Override
		    public void propertyChange(PropertyChangeEvent event) {
		      if (event.getProperty().equals(PreferenceConstants.P_COMMIT_SIGNATURE_ACTIVE)) {
		        if(getShell() != null) {
		        	getShell().redraw();
		        	getShell().layout();
		        	 refreshView();
		        }
		      }
		    }
		  }); 
	}
	
	public void refreshView() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				MessageDialog.openInformation(getShell(), Messages.INFORMATION, Messages.FilesChangedListDialog_CloseDialogWindow);
			}});
	}
	
	static class CommitFileContentProvider extends BaseWorkbenchContentProvider {
		@SuppressWarnings("rawtypes")
		@Override
		public Object[] getElements(Object element) {
			if (element instanceof Object[])
				return (Object[]) element;
			if (element instanceof Collection)
				return ((Collection) element).toArray();
			return new Object[0];
		}

		public Object[] getChildren(Object parentElement) {
			return new Object[0];
		}

		public Object getParent(Object element) {
			return null;
		}

		public boolean hasChildren(Object element) {
			return false;
		}
	}
	
	static class CommitPathLabelProvider extends ColumnLabelProvider {

		public String getText(Object obj) {
			return ((ChangedFile) obj).getPath();
		}

		public String getToolTipText(Object element) {
			return ((ChangedFile) element).getPath();
		}

	}

	static class CommitStatusLabelProvider extends BaseLabelProvider implements
			IStyledLabelProvider {

		private Image DEFAULT = PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_OBJ_FILE);

		private ResourceManager resourceManager = new LocalResourceManager(
				JFaceResources.getResources());

		private Image getEditorImage(ChangedFile item) {
			Image image = DEFAULT;
			String name = new Path(item.getPath()).lastSegment();
			if (name != null) {
				ImageDescriptor descriptor = PlatformUI.getWorkbench().getEditorRegistry().getImageDescriptor(name);
				image = (Image) this.resourceManager.get(descriptor);
			}
			return image;
		}

		private Image getDecoratedImage(Image base, ImageDescriptor decorator) {
			DecorationOverlayIcon decorated = new DecorationOverlayIcon(base,
					decorator, IDecoration.BOTTOM_RIGHT);
			return (Image) this.resourceManager.get(decorated);
		}

		public StyledString getStyledText(Object element) {
			return new StyledString();
		}

		public Image getImage(Object element) {
			ChangedFile item = (ChangedFile) element;
			ImageDescriptor decorator = null;
			//Image other = null;
			Image finalOther = null;
			switch (item.getTypeChange()) {
				case UNTRACKED:
					decorator = UIIcons.OVR_UNTRACKED;
					break;
				case ADDED:
				case ADDED_INDEX_DIFF:
					decorator = UIIcons.OVR_STAGED_ADD;
					break;
				case REMOVED:
				case REMOVED_NOT_STAGED:
				case REMOVED_UNTRACKED:
					decorator = UIIcons.OVR_STAGED_REMOVE;
					break;
				default:
					break;
			}
			
			if(decorator != null) {
				finalOther = getDecoratedImage(getEditorImage(item), decorator);
			} else if(finalOther == null) {
				finalOther = getEditorImage(item);
			}
			return finalOther;
		}

		@Override
		public void dispose() {
			resourceManager.dispose();
			super.dispose();
		}
	}

	@Override
	protected Control createContents(Composite parent) {
		toolkit = new FormToolkit(parent.getDisplay());
		parent.addDisposeListener(new DisposeListener() {

			public void widgetDisposed(DisposeEvent e) {
				toolkit.dispose();
			}
		});
		return super.createContents(parent);
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		toolkit.adapt(parent, false, false);
		commitAndPushButton = createButton(parent, COMMIT_AND_PUSH_ID,
				Messages.FilesChangedListDialog_CommitPush, false);
		commitButton = createButton(parent, IDialogConstants.OK_ID,
				Messages.FilesChangedListDialog_Commit, true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
		updateMessage();
	}
	
	@Override
	protected IDialogSettings getDialogBoundsSettings() {
		IDialogSettings settings = Activator.getDefault().getDialogSettings();
		IDialogSettings section = settings.getSection(DIALOG_SETTINGS_SECTION_NAME);
		if (section == null)
			section = settings.addNewSection(DIALOG_SETTINGS_SECTION_NAME);
		return section;
	}
	
	protected void buttonPressed(int buttonId) {
		if (IDialogConstants.OK_ID == buttonId)
			okPressed();
		else if (COMMIT_AND_PUSH_ID == buttonId) {
			setPushRequested(true);
			okPressed();
		} else if (IDialogConstants.CANCEL_ID == buttonId)
			cancelPressed();
	}
	
	/**
	 * Add message drop down toolbar item
	 *
	 * @param parent
	 * @return toolbar
	 */
	protected ToolBar addMessageDropDown(Composite parent) {
		final ToolBar dropDownBar = new ToolBar(parent, SWT.FLAT | SWT.RIGHT);
		final ToolItem dropDownItem = new ToolItem(dropDownBar, SWT.PUSH);
		dropDownItem.setImage(PlatformUI.getWorkbench().getSharedImages()
				.getImage("IMG_LCL_RENDERED_VIEW_MENU")); //$NON-NLS-1$
		final Menu menu = new Menu(dropDownBar);
		dropDownItem.addDisposeListener(new DisposeListener() {

			public void widgetDisposed(DisposeEvent e) {
				menu.dispose();
			}
		});
		MenuItem preferencesItem = new MenuItem(menu, SWT.PUSH);
		preferencesItem.setText(Messages.FilesChangedListDialog_ConfigureLink);
		preferencesItem.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				String[] pages = new String[] { UIPreferences.PAGE_COMMIT_PREFERENCES_SUMMARY };
				Activator.getDefault().getDialogSettings();
				PreferencesUtil.createPreferenceDialogOn(getShell(), pages[0],
						pages, null).open();
			}

		});
		dropDownItem.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				Rectangle b = dropDownItem.getBounds();
				Point p = dropDownItem.getParent().toDisplay(
						new Point(b.x, b.y + b.height));
				menu.setLocation(p.x, p.y);
				menu.setVisible(true);
			}

		});
		return dropDownBar;
	}
	
	@Override
	protected void okPressed() {
		if (!isCommitWithoutFilesAllowed()) {
			MessageDialog.openWarning(getShell(), Messages.NO_FILES_SELECTED_TITLE, Messages.NO_FILES_SELECTED_MESSAGE); //$NON-NLS-1$
			return;
		} else if(!validateCommit().equals(Constants.EMPTY_STRING)) {
			MessageDialog.openWarning(getShell(), Messages.FilesChangedListDialog_Error, validateCommit());
			return;
		}

		ArrayList<String> selectedFiles = new ArrayList<String>(); 
		Object[] checkedElements = filesViewer.getCheckedElements(); 
		selectedFiles.clear(); 
		for (Object obj : checkedElements)
			selectedFiles.add(((ChangedFile) obj).getPath());

		try {
			for(String fileTmp : selectedFiles) {
				AddCommand add = git.add();
				add.addFilepattern(fileTmp).call();
			}
			
			CommitCommand commit = git.commit();
			commit.setAuthor(getAuthor(), getAuthor());
			commit.setCommitter(getCommitter(), Constants.EMPTY_STRING);
			commit.setMessage(editor.getText().getText());
			commit.call();
			
			if(isPushRequested) {
				PushCommand push = git.push();
				push.call();
			}
		} catch (NoHeadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoMessageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnmergedPathsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConcurrentRefUpdateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WrongRepositoryStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		super.okPressed();
	}
	
	protected String validateCommit() {
		String authorValue = author;
		String committerValue = committer;
		if (authorValue.length() == 0) {
			return Messages.FilesChangedListDialog_EmptyAuthor;
		}
		if (committerValue.length() == 0) {
			return Messages.FilesChangedListDialog_EmptyCommiter;
		}
		return Constants.EMPTY_STRING;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		parent.getShell().setText(Messages.FilesChangedListDialog_CommitChanges);

		container = toolkit.createComposite(container);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(container);
		toolkit.paintBordersFor(container);
		GridLayoutFactory.swtDefaults().applyTo(container);

		sashForm = new SashForm(container, SWT.VERTICAL | SWT.FILL);
		toolkit.adapt(sashForm, true, true);
		sashForm.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		
		createMessageAndPersonArea(sashForm);
		
		filesSection = createFileSection(sashForm);
		
		sashForm.setWeights(new int[] { 50, 50 });
		applyDialogFont(container);
		container.pack();
		setTitle(Messages.FilesChangedListDialog_CommitChanges);
		setMessage(Messages.FilesChangedListDialog_CommitMessage, IMessageProvider.INFORMATION);

		filesViewer.addCheckStateListener(new ICheckStateListener() {

			public void checkStateChanged(CheckStateChangedEvent event) {
				updateMessage();
			}
		});

		updateFileSectionText();
		return container;
	}
	
	private Composite createMessageAndPersonArea(Composite container) {
		messageAndPersonArea = toolkit.createComposite(container);
		GridDataFactory.fillDefaults().grab(true, true)
				.applyTo(messageAndPersonArea);
		GridLayoutFactory.swtDefaults().margins(0, 0).spacing(0, 0)
				.applyTo(messageAndPersonArea);

		Section messageSection = toolkit.createSection(messageAndPersonArea, ExpandableComposite.TITLE_BAR | ExpandableComposite.CLIENT_INDENT);
		messageSection.setText(Messages.FilesChangedListDialog_CommitMessage);
		Composite messageArea = toolkit.createComposite(messageSection);
		GridLayoutFactory.fillDefaults().spacing(0, 0).extendedMargins(2, 2, 2, 2).applyTo(messageArea);
		toolkit.paintBordersFor(messageArea);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(messageSection);
		GridLayoutFactory.swtDefaults().applyTo(messageSection);

		Composite headerArea = new Composite(messageSection, SWT.NONE);
		GridLayoutFactory.fillDefaults().spacing(0, 0).numColumns(2).applyTo(headerArea);

		ToolBar messageToolbar = new ToolBar(headerArea, SWT.FLAT | SWT.HORIZONTAL);
		GridDataFactory.fillDefaults().align(SWT.END, SWT.FILL).grab(true, false).applyTo(messageToolbar);
		addMessageDropDown(headerArea);
		
		messageSection.setTextClient(headerArea);
		
		Point size = createJavaSourceCodeViewer(container, messageSection,
				messageArea);
		
		createSignatureCanvas(size);
		
		return messageAndPersonArea;
	}

	public void createSignatureCanvas(Point size) {
		boolean visible = Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.P_COMMIT_SIGNATURE_ACTIVE);
		
		signatureCanvas = new SignatureCanvas(signatureMap, messageAndPersonArea, getShell());
		signatureCanvas.createContents();
		if(visible) {
			signatureCanvas.getCanvas().setLayoutData(GridDataFactory.fillDefaults()
					.grab(true, true).hint(size).minSize(size.x, 90)
					.align(SWT.FILL, SWT.FILL).create());
		} else {
			signatureCanvas.getCanvas().setLayoutData(GridDataFactory.fillDefaults()
					.grab(true, true).hint(size).minSize(size.x, 0)
					.align(SWT.FILL, SWT.FILL).create());
		}
		signatureCanvas.getCanvas().setVisible(visible);
	}

	public Point createJavaSourceCodeViewer(Composite container,
			Section messageSection, Composite messageArea) {
		JavaViewer viewer = new JavaViewer();
		viewer.setShell(getShell());
		viewer.setComposite(messageAndPersonArea);
		viewer.createStyledText();
		int minHeight = 114;
		Point size = container.getSize();
		viewer.getText().setLayoutData(GridDataFactory.fillDefaults()
				.grab(true, true).hint(size).minSize(size.x - 30, minHeight)
				.align(SWT.FILL, SWT.FILL).create());
		setEditor(viewer);
		messageSection.setClient(messageArea);
		return size;
	}
	
	public void updateSignatureCanvas() {
		if(signatureMap != null) {
			signatureCanvas.setSignatureMap(signatureMap);
			signatureCanvas.redraw();
		}
	}
	
	private Section createFileSection(Composite container) {
		Section filesSection = toolkit.createSection(container,
				ExpandableComposite.TITLE_BAR
						| ExpandableComposite.CLIENT_INDENT);
		
		GridDataFactory.fillDefaults().grab(true, false).applyTo(filesSection);
		Composite filesArea = toolkit.createComposite(filesSection);
		filesSection.setClient(filesArea);
		toolkit.paintBordersFor(filesArea);
		GridLayoutFactory.fillDefaults().extendedMargins(2, 2, 2, 2).applyTo(filesArea);

		ToolBar filesToolbar = new ToolBar(filesSection, SWT.FLAT);
		filesSection.setTextClient(filesToolbar);

		PatternFilter patternFilter = new PatternFilter() {
			@Override
			protected boolean isLeafMatch(Viewer viewer, Object element) {
				if(element instanceof ChangedFile) {
					ChangedFile commitItem = (ChangedFile) element;
					return wordMatches(commitItem.getPath());
				}
				return super.isLeafMatch(viewer, element);
			}
		};
		patternFilter.setIncludeLeadingWildcard(true);
		FilteredCheckboxTree resourcesTreeComposite = new FilteredCheckboxTree(filesArea, toolkit, SWT.FULL_SELECTION, patternFilter);
		Tree resourcesTree = resourcesTreeComposite.getViewer().getTree();
		resourcesTree.setData(FormToolkit.KEY_DRAW_BORDER,FormToolkit.TREE_BORDER);
		resourcesTreeComposite.setLayoutData(GridDataFactory.fillDefaults().hint(600, 230).grab(true, true).create());
		resourcesTree.setHeaderVisible(true);
		
		TreeColumn statCol = new TreeColumn(resourcesTree, SWT.LEFT);
		statCol.setText(Messages.FilesChangedListDialog_Status);
		statCol.setWidth(150);

		TreeColumn resourceCol = new TreeColumn(resourcesTree, SWT.LEFT);
		resourceCol.setText(Messages.FilesChangedListDialog_Path);
		resourceCol.setWidth(415);

		filesViewer = resourcesTreeComposite.getCheckboxTreeViewer();
		new TreeViewerColumn(filesViewer, statCol).setLabelProvider(createStatusLabelProvider());
		new TreeViewerColumn(filesViewer, resourceCol).setLabelProvider(new CommitPathLabelProvider());
		ColumnViewerToolTipSupport.enableFor(filesViewer);
		filesViewer.setContentProvider(new CommitFileContentProvider());
		filesViewer.setUseHashlookup(true);
		if(items != null) {
			filesViewer.setInput(items.toArray());
		}
		
		filesViewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				updateFileSectionText();
			}
		});
		
		ToolItem help = UIUtils.createHelpButton(filesToolbar);
		help.addSelectionListener(new SelectionAdapter() {
		    @Override
		    public void widgetSelected(SelectionEvent e) {
		    	InformationDialog dialog = new InformationDialog(getShell());
		    	dialog.create();
		    	dialog.open();
		    }
		});
		
		ToolItem describeChangesItem = new ToolItem(filesToolbar, SWT.PUSH);
		Image describeImage = UIIcons.ANNOTATE.createImage();
		describeChangesItem.setImage(describeImage);
		describeChangesItem.setToolTipText(Messages.FilesChangedListDialog_DescribeChanges);
		describeChangesItem.addSelectionListener(new SummarizeChangeListener(this));

		ToolItem checkAllItem = new ToolItem(filesToolbar, SWT.PUSH);
		Image checkImage = UIIcons.CHECK_ALL.createImage();
		checkAllItem.setImage(checkImage);
		checkAllItem.setToolTipText(Messages.FilesChangedListDialog_SelectAll);
		checkAllItem.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				filesViewer.setAllChecked(true);
				updateFileSectionText();
				updateMessage();
			}

		});

		ToolItem uncheckAllItem = new ToolItem(filesToolbar, SWT.PUSH);
		Image uncheckImage = UIIcons.UNCHECK_ALL.createImage();
		uncheckAllItem.setImage(uncheckImage);
		uncheckAllItem.setToolTipText(Messages.FilesChangedListDialog_DeselectAll);
		uncheckAllItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				filesViewer.setAllChecked(false);
				updateFileSectionText();
				updateMessage();
			}
		});
		statCol.pack();
		resourceCol.pack();
		return filesSection;
	}
	
	private static IPreferenceStore getPreferenceStore() {
		return Activator.getDefault().getPreferenceStore();
	}
	
	private void updateFileSectionText() {
		filesSection.setText(MessageFormat.format(Messages.FilesChangedListDialog_ChangedFileListTitle,
				Integer.valueOf(filesViewer.getCheckedElements().length),
				Integer.valueOf(filesViewer.getTree().getItemCount())));
	}
	
	private static CellLabelProvider createStatusLabelProvider() {
		CommitStatusLabelProvider baseProvider = new CommitStatusLabelProvider();
		ProblemLabelDecorator decorator = new ProblemLabelDecorator(null);
		return new DecoratingStyledCellLabelProvider(baseProvider, decorator, null) {
			@Override
			public String getToolTipText(Object element) {
				return ((ChangedFile) element).getChangeType();
			}
		};
	}
	
	public void updateMessage() {
		if (commitButton == null)
			// Not yet fully initialized.
			return;

		String message = null;
		int type = IMessageProvider.NONE;

		String commitMsg = getEditor().getText().getText().toString();
		if (commitMsg == null || commitMsg.trim().length() == 0) {
			message = Messages.FilesChangedListDialog_EmptyMessage;
			type = IMessageProvider.INFORMATION;
		} else if (!isCommitWithoutFilesAllowed()) {
			message = Messages.FilesChangedListDialog_EmptySelection;
			type = IMessageProvider.INFORMATION;
		}

		setMessage(message, type);
		boolean commitEnabled = type == IMessageProvider.WARNING
				|| type == IMessageProvider.NONE;
		commitButton.setEnabled(commitEnabled);
		commitAndPushButton.setEnabled(commitEnabled);
	}
	
	private boolean isCommitWithoutFilesAllowed() {
		if (filesViewer.getCheckedElements().length > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public ChangedFile[] getSelectedFiles() {
		return Arrays.copyOf(filesViewer.getCheckedElements(), filesViewer.getCheckedElements().length, ChangedFile[].class);
	}
	
	public void initStyles() {
		Color orange, blue;
		orange = new Color(getShell().getDisplay(), 255, 127, 0);
		blue = getShell().getDisplay().getSystemColor(SWT.COLOR_BLUE);
		
		Font font = new Font(getShell().getDisplay(), Messages.FilesChangedListDialog_FontType, 10, SWT.NORMAL);
		getText().setFont(font);

		StyleRange range1 = new StyleRange(0, 4, orange, null);
		range1.fontStyle = SWT.BOLD;
		getText().setStyleRange(range1);

		StyleRange range2 = new StyleRange(5, 2, blue, null);
		range2.background = getShell().getDisplay().getSystemColor(SWT.COLOR_YELLOW);

		getText().setStyleRange(range2);
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	public Git getGit() {
		return git;
	}

	public void setGit(Git git) {
		this.git = git;
	}

	public StyledText getText() {
		return text;
	}

	public void setText(StyledText text) {
		this.text = text;
	}

	public IJavaProject getSelection() {
		return selection;
	}

	public void setSelection(IJavaProject selection) {
		this.selection = selection;
	}

	public JavaViewer getEditor() {
		return editor;
	}

	public void setEditor(JavaViewer editor) {
		this.editor = editor;
	}

	public ListSelectionDialog getListSelectionDialog() {
		return listSelectionDialog;
	}

	public void setListSelectionDialog(ListSelectionDialog listSelectionDialog) {
		this.listSelectionDialog = listSelectionDialog;
	}

	public TreeMap<MethodStereotype, Integer> getSignatureMap() {
		return signatureMap;
	}

	public void setSignatureMap(TreeMap<MethodStereotype, Integer> signatureMap) {
		this.signatureMap = signatureMap;
	}

	public boolean isPushRequested() {
		return isPushRequested;
	}

	public void setPushRequested(boolean isPushRequested) {
		this.isPushRequested = isPushRequested;
	}

	public Text getAuthorText() {
		return authorText;
	}

	public void setAuthorText(Text authorText) {
		this.authorText = authorText;
	}

	public Text getCommitterText() {
		return committerText;
	}

	public void setCommitterText(Text committerText) {
		this.committerText = committerText;
	}

	public String getCommitter() {
		return committer;
	}

	public void setCommitter(String committer) {
		IDialogSettings settings = Activator.getDefault().getDialogSettings();
		if (settings != null) {
			this.committer = getPreferenceStore().getString(PreferenceConstants.P_COMMITER);
		} else {
			this.committer = committer;
		}
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		IDialogSettings settings = Activator.getDefault().getDialogSettings();
		if (settings != null) {
			this.author = getPreferenceStore().getString(PreferenceConstants.P_AUTHOR);
		} else {
			this.author = author;
		}
	}

	@Override
	public Text getOlderVersionText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Text getNewerVersionText() {
		// TODO Auto-generated method stub
		return null;
	}
}
