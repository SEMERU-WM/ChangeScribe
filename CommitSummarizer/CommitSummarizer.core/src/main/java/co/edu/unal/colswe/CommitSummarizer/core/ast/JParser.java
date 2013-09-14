package co.edu.unal.colswe.CommitSummarizer.core.ast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jface.text.Document;

public class JParser
{
    private CompilationUnit unit;
    private List<ASTNode> elements;
    private Document document;
    private ASTRewrite astRewrite;
    private IMember member;
    
    public JParser(final ICompilationUnit unit) {
        super();
        final ASTParser parser = ASTParser.newParser(4);
        parser.setKind(8);
        parser.setSource(unit);
        parser.setResolveBindings(true);
        this.unit = (CompilationUnit)parser.createAST((IProgressMonitor)null);
        this.elements = new ArrayList<ASTNode>();
        try {
            this.document = new Document(unit.getSource());
        }
        catch (JavaModelException ex) {
            System.err.println("Ooops! An error occurred when printing the report.");
        }
    }
    
    public JParser(final File file) {
        super();
        final ASTParser parser = ASTParser.newParser(4);
        parser.setKind(8);
        
        IWorkspace workspace = ResourcesPlugin.getPlugin().getWorkspace();
        IWorkspaceRoot root = workspace.getRoot();
        
        
        parser.setSource(fileToString(file));
        parser.setResolveBindings(true);
        this.unit = (CompilationUnit)parser.createAST((IProgressMonitor)null);
        this.elements = new ArrayList<ASTNode>();
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
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return buffer.toString().toCharArray();
	}
    
    public JParser(final IMember member) {
        this(member.getCompilationUnit());
        this.member = member;
    }
    
    public void parse() {
        if (this.member != null) {
            if (this.member instanceof IType) {
                this.elements.add(this.unit.findDeclaringNode(((IType)this.member).getKey()));
            }
            else if (this.member instanceof IMethod) {
                final ASTParser parser = ASTParser.newParser(4);
                parser.setProject(((IMethod)this.member).getJavaProject());
                parser.setResolveBindings(true);
                final IBinding binding = parser.createBindings(new IJavaElement[] { (IMethod)this.member }, (IProgressMonitor)null)[0];
                if (binding instanceof IMethodBinding) {
                    final ASTNode method = this.unit.findDeclaringNode(((IMethodBinding)binding).getKey());
                    this.elements.add(method);
                }
            }
        }
        else {
            for (final Object o : this.unit.types()) {
                if (!(o instanceof TypeDeclaration)) {
                    continue;
                }
                this.elements.add((ASTNode)o);
            }
        }
    }
    
    public List<ASTNode> getElements() {
        return this.elements;
    }
    
    public CompilationUnit getCompilationUnit() {
        return this.unit;
    }
    
    public Document getDocument() {
        return this.document;
    }
    
    public ASTRewrite getAstRewrite() {
        if (this.astRewrite == null) {
            this.astRewrite = ASTRewrite.create(this.unit.getAST());
        }
        return this.astRewrite;
    }
}
