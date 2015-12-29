package co.edu.unal.colswe.changescribe.core.ui;

import java.util.TreeMap;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jgit.api.Git;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import co.edu.unal.colswe.changescribe.core.editor.JavaViewer;
import co.edu.unal.colswe.changescribe.core.git.ChangedFile;
import co.edu.unal.colswe.changescribe.core.stereotype.taxonomy.MethodStereotype;

public interface IDialog {
	public IJavaProject getSelection();
	public void setSignatureMap(TreeMap<MethodStereotype, Integer> signatureMap);
	public JavaViewer getEditor();
	public void updateSignatureCanvas();
	public void updateMessage();
	public ChangedFile[] getSelectedFiles();
	public Git getGit();
	public Text getOlderVersionText();
	public Text getNewerVersionText();
	public Shell getShell();
}
