package co.edu.unal.colswe.CommitSummarizer.core.summarizer;

import org.eclipse.jdt.core.ICompilationUnit;

import co.edu.unal.colswe.CommitSummarizer.core.stereotype.stereotyped.StereotypedCommit;
import co.edu.unal.colswe.CommitSummarizer.core.stereotype.taxonomy.CommitStereotype;
import co.edu.unal.colswe.CommitSummarizer.core.stereotype.taxonomy.MethodStereotype;

public class CommitStereotypeDescriptor {
	
	public static String describe(ICompilationUnit cu, StereotypedCommit stereotypedCommit) {
		StringBuilder description = new StringBuilder();
		
		if(stereotypedCommit.getStereotypes().get(0) == CommitStereotype.STRUCTURE_MODIFIER) {
			description.append("This is a structure modifier commit.\n");
			description.append("Commit is composed by setter and getter methods.\n" );
		} else if (stereotypedCommit.getStereotypes().get(0) == CommitStereotype.STATE_ACCESS_MODIFIER) {
			description.append("This is a state access modifier commit.\n");
			description.append("Commit is composed by accessor methods.\n" );
		} else if (stereotypedCommit.getStereotypes().get(0) == CommitStereotype.STATE_UPDATE_MODIFIER) {
			description.append("This is a state update modifier commit.\n");
			description.append("Commit is composed by mutator methods.\n" );
		} else if (stereotypedCommit.getStereotypes().get(0) == CommitStereotype.BEHAVIOR_MODIFIER) {
			description.append("This is a behavior modifier commit.\n");
			description.append("Commit is composed by command and non-void-command methods.\n" );
		} else if (stereotypedCommit.getStereotypes().get(0) == CommitStereotype.OBJECT_CREATION_MODIFIER) {
			description.append("This is an object creation modifier commit.\n");
			description.append("Commit is composed by factory, constructor, copy constructor and destructor methods.\n" );
		} else if (stereotypedCommit.getStereotypes().get(0) == CommitStereotype.RELATIONSHIP_MODIFIER) {
			description.append("This is a relationship modifier commit.\n");
			description.append("Commit is composed mainly by collaborators and low number of controller methods.\n" );
		} else if (stereotypedCommit.getStereotypes().get(0) == CommitStereotype.CONTROL_MODIFIER) {
			description.append("This is a control modifier commit.\n");
			description.append("Commit is composed mainly by controller, factory, constructor, copy-constructor and destructor methods.\n" );
		} else if (stereotypedCommit.getStereotypes().get(0) == CommitStereotype.LARGE_MODIFIER) {
			description.append("This is a large modifier commit.\n");
			description.append("Commit is composed by " + stereotypedCommit.getMethods().size() + "\n" );
			int constructor = (stereotypedCommit.getSignatureMap().get(MethodStereotype.CONSTRUCTOR) != null) ? stereotypedCommit.getSignatureMap().get(MethodStereotype.CONSTRUCTOR) : 0;
			int copyConstructor = (stereotypedCommit.getSignatureMap().get(MethodStereotype.COPY_CONSTRUCTOR) != null) ? stereotypedCommit.getSignatureMap().get(MethodStereotype.COPY_CONSTRUCTOR) : 0;
			int destructor = (stereotypedCommit.getSignatureMap().get(MethodStereotype.DESTRUCTOR) != null) ? stereotypedCommit.getSignatureMap().get(MethodStereotype.DESTRUCTOR) : 0;
			int factory = (stereotypedCommit.getSignatureMap().get(MethodStereotype.FACTORY) != null) ? stereotypedCommit.getSignatureMap().get(MethodStereotype.FACTORY) : 0;
			int creational = factory + destructor + copyConstructor + constructor;
			int controller = (stereotypedCommit.getSignatureMap().get(MethodStereotype.CONTROLLER) != null) ? stereotypedCommit.getSignatureMap().get(MethodStereotype.CONTROLLER) : 0;
			if(creational > 0) {
				description.append(", " + creational + " methods are creational category\n" );
			}
			if(creational > 0) {
				description.append(", and " + controller + " methods are controller.\n" );
			}
			description.append("Commit is composed by " + stereotypedCommit.getMethods().size() + ".\n" );
		} else if (stereotypedCommit.getStereotypes().get(0) == CommitStereotype.LAZY_MODIFIER) {
			int get = (stereotypedCommit.getSignatureMap().get(MethodStereotype.GET) != null) ? stereotypedCommit.getSignatureMap().get(MethodStereotype.GET) : 0;
			int set = (stereotypedCommit.getSignatureMap().get(MethodStereotype.SET) != null) ? stereotypedCommit.getSignatureMap().get(MethodStereotype.SET) : 0;
			int getSet = get + set;
			
			description.append("This is a lazy modifier commit.\n");
			description.append("Commit is composed by " + getSet + " get and set methods, and other degenerate methods.\n" );
		} else if (stereotypedCommit.getStereotypes().get(0) == CommitStereotype.DEGENERATE_MODIFIER) {
			int empty = (stereotypedCommit.getSignatureMap().get(MethodStereotype.EMPTY) != null) ? stereotypedCommit.getSignatureMap().get(MethodStereotype.EMPTY) : 0;
			int incidental = (stereotypedCommit.getSignatureMap().get(MethodStereotype.INCIDENTAL) != null) ? stereotypedCommit.getSignatureMap().get(MethodStereotype.INCIDENTAL) : 0;
			int abs = (stereotypedCommit.getSignatureMap().get(MethodStereotype.ABSTRACT) != null) ? stereotypedCommit.getSignatureMap().get(MethodStereotype.ABSTRACT) : 0;
			int degenerate = incidental + empty + abs;
			description.append("This is a degenerate modifier commit.\n");
			description.append("Commit is composed by " + degenerate + " degenerate methods (empty, incidental, and abstract methods), and other degenerate methods\n" );
		} else if (stereotypedCommit.getStereotypes().get(0) == CommitStereotype.SMALL_MODIFIER) {
			description.append("This is a small modifier commit.\n");
			description.append("Commit is composed by only " + stereotypedCommit.getMethods().size() + " methods.\n" );
		}
		
		return description.toString();
	}

}
