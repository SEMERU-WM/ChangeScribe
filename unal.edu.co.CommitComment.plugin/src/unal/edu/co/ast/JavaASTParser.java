package unal.edu.co.ast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.internal.compiler.batch.CompilationUnit;


public class JavaASTParser {


	
	public static CompilationUnit parseFile(File file) {
		ASTParser ast = ASTParser.newParser(AST.JLS4);
		ast.setSource(fileToString(file));
		
	    CompilationUnit cu = new CompilationUnit(fileToString(file), file.getName(), "");

	    return cu;
	}
	
	@SuppressWarnings("resource")
	private static char[] fileToString(final File file) {
		BufferedReader in = null;
		final StringBuffer buffer = new StringBuffer();
		try {
			in = new BufferedReader(new FileReader(file));
			String line = null;
			while (null != (line = in.readLine())) {
			     buffer.append(line).append("\n");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return buffer.toString().toCharArray();
	}

}
