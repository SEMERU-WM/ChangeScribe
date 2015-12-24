package co.edu.unal.colswe.changescribe.core.ui.describeversion;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jgit.api.Git;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import changescribe.core.preferences.PreferenceConstants;
import co.edu.unal.colswe.changescribe.core.Activator;
import co.edu.unal.colswe.changescribe.core.Constants;
import co.edu.unal.colswe.changescribe.core.Messages;
import co.edu.unal.colswe.changescribe.core.commitsignature.SignatureCanvas;
import co.edu.unal.colswe.changescribe.core.editor.JavaViewer;
import co.edu.unal.colswe.changescribe.core.git.ChangedFile;
import co.edu.unal.colswe.changescribe.core.stereotype.taxonomy.MethodStereotype;
import co.edu.unal.colswe.changescribe.core.ui.CachedCheckboxTreeViewer;
import co.edu.unal.colswe.changescribe.core.ui.IDialog;

public class GenerateMessagePage extends WizardPage implements IDialog {

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
	private TreeMap<MethodStereotype, Integer> signatureMap;
	private SignatureCanvas signatureCanvas;
	private Text olderVersionText;
	private Text newerVersionText;
	private SashForm sashForm;
	private Composite messageAndPersonArea;
	private String commitCurrentID = Constants.EMPTY_STRING;
	private String commitPreviousID = Constants.EMPTY_STRING;
	private Shell shellTmp;

	public GenerateMessagePage(Shell shell, HashSet<Object> hashSet, Git git) {
		
		super("Generate Commit Message");
		
		shellTmp = shell;
		this.git = git;
		this.setSelection(selection);
		
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
		
		setTitle("Generate Commit Message");
		setDescription("Generate Commit Message for selected version");
	}

	public void refreshView() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				MessageDialog.openInformation(getShell(), Messages.INFORMATION, Messages.FilesChangedListDialog_CloseDialogWindow);
			}});
	}
	
	@Override
	public void createControl(Composite parent) {
		setControl(sashForm);
	}

	@Override
	public IJavaProject getSelection() {
		return this.selection;
	}
	
	public void setSelection(IJavaProject selection) {
		this.selection = selection;
	}

	@Override
	public void setSignatureMap(TreeMap<MethodStereotype, Integer> signatureMap) {
		this.signatureMap = signatureMap;		
	}

	@Override
	public JavaViewer getEditor() {
		return this.editor;
	}

	@Override
	public void updateSignatureCanvas() {
		if(signatureMap != null) {
			signatureCanvas.setSignatureMap(signatureMap);
			signatureCanvas.redraw();
		}
	}

	@Override
	public void updateMessage() {
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
	}
	
	private boolean isCommitWithoutFilesAllowed() {
		if (filesViewer.getCheckedElements().length > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	
	
}
