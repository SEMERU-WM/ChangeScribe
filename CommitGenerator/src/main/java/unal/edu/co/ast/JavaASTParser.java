package unal.edu.co.ast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.TypeDeclaration;


public class JavaASTParser {
	
	private List<ASTNode> nodes;
	private CompilationUnit compilationUnit;
	


	
	public JavaASTParser(File file) {
		super();
		ASTParser parser = ASTParser.newParser(4);
		parser.setKind(8);
		parser.setSource(fileToString(file));
		parser.setResolveBindings(true);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		this.compilationUnit = (CompilationUnit)parser.createAST(null);
		
	}

	public static CompilationUnit parseFile(File file) {
		ASTParser ast = ASTParser.newParser(AST.JLS4);
		ast.setResolveBindings(true);
		ast.setBindingsRecovery(true);
		ast.setKind(ASTParser.K_COMPILATION_UNIT);
		ast.setSource(fileToString(file));
		
		Hashtable<String, String> options = JavaCore.getDefaultOptions();
	    options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_6);
	    ast.setCompilerOptions(options);
		
		CompilationUnit cu = (CompilationUnit) ast.createAST(null);
		
		IProblem[] problems = cu.getProblems();
	    if (problems != null && problems.length > 0) {
	    	System.out.println("Got {} problems compiling the source file: " + problems.length);
	        for (IProblem problem : problems) {
	        	System.out.println("Got {} problems compiling the source file: " + problem);
	        }
	    }
		
	    return cu;
	}
	
	public void parse() {
		this.nodes = new ArrayList<ASTNode>();
        
        for (final Object o : this.compilationUnit.types()) {
            if (!(o instanceof TypeDeclaration)) {
                continue;
            } 
            this.nodes.add((ASTNode)o);
        }
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

	public List<ASTNode> getNodes() {
		return nodes;
	}

	public void setNodes(List<ASTNode> nodes) {
		this.nodes = nodes;
	}

	public CompilationUnit getCompilationUnit() {
		return compilationUnit;
	}

	public void setCompilationUnit(CompilationUnit compilationUnit) {
		this.compilationUnit = compilationUnit;
	}

}
