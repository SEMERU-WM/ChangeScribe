package co.edu.unal.colswe.changescribe.core.textgenerator.phrase.util;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class CompilationUtils {

	public CompilationUtils() {
		// TODO Auto-generated constructor stub
	}
	
	public static String getPackageNameFromStringClass(String removedFile) {
		final ASTParser parser = ASTParser.newParser(4);
		parser.setResolveBindings(true);
		parser.setKind(8);
		
		parser.setSource(removedFile.toCharArray());
		
		CompilationUnit unit = (CompilationUnit)parser.createAST((IProgressMonitor)null);
		return unit.getPackage().getName().toString();
	}
	
	public static String getCUType(ICompilationUnit type) {
		String fileType = "";
		try {
			if(type.getPrimary().findPrimaryType().isInterface()) {
				fileType = "interface";
			} else if(type.getPrimary().findPrimaryType().isClass()) {
				fileType = "class";
			} else if(type.getPrimary().findPrimaryType().isEnum()) {
				fileType = "enum";
			}
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fileType;
	}
	
	

}
