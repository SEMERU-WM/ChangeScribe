package co.edu.unal.colswe.changescribe.core.ui;

import java.util.TreeMap;

import org.eclipse.jdt.core.IJavaProject;

import co.edu.unal.colswe.changescribe.core.editor.JavaViewer;
import co.edu.unal.colswe.changescribe.core.stereotype.taxonomy.MethodStereotype;

public interface IDialog {
	public IJavaProject getSelection();
	public void setSignatureMap(TreeMap<MethodStereotype, Integer> signatureMap);
	public JavaViewer getEditor();
	public void updateSignatureCanvas();
	public void updateMessage();
}
