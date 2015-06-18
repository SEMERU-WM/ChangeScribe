package co.edu.unal.colswe.changescribe.core.summarizer;

import org.eclipse.jdt.core.ICompilationUnit;

import co.edu.unal.colswe.changescribe.core.stereotype.stereotyped.StereotypedCommit;
import co.edu.unal.colswe.changescribe.core.stereotype.taxonomy.CommitStereotype;

public class CommitStereotypeDescriptor {
	
	public static String describe(ICompilationUnit cu, StereotypedCommit stereotypedCommit) {
		StringBuilder description = new StringBuilder();
		
		if(stereotypedCommit.getStereotypes().get(0) == CommitStereotype.STRUCTURE_MODIFIER) {
			description.append("This is a structure modifier commit: ");
			description.append("this change set is composed only of setter and getter methods, and " );
			description.append("these methods perform simple access and modifications to the data. " );
		} else if (stereotypedCommit.getStereotypes().get(0) == CommitStereotype.STATE_ACCESS_MODIFIER) {
			description.append("This is a state access modifier commit: ");
			description.append("this change set is composed only of accessor methods, and " );
			description.append("these methods provide a client with information, but the data members are not modified. ");
		} else if (stereotypedCommit.getStereotypes().get(0) == CommitStereotype.STATE_UPDATE_MODIFIER) {
			description.append("This is a state update modifier commit: ");
			description.append("this change set is composed only of mutator methods, and " );
			description.append("these methods provide changes related to updates of an object's state. ");
		} else if (stereotypedCommit.getStereotypes().get(0) == CommitStereotype.BEHAVIOR_MODIFIER) {
			description.append("This is a behavior modifier commit: ");
			description.append("this change set is composed of command and non-void-command methods, and " );
			description.append("these methods execute complex internal behavioral changes within an object. ");
		} else if (stereotypedCommit.getStereotypes().get(0) == CommitStereotype.OBJECT_CREATION_MODIFIER) {
			description.append("This is an object creation modifier commit: ");
			description.append("this change set is composed of factory, constructor, copy constructor and destructor methods, and " );
			description.append("these methods allow the creation of objects. ");
		} else if (stereotypedCommit.getStereotypes().get(0) == CommitStereotype.RELATIONSHIP_MODIFIER) {
			description.append("This is a relationship modifier commit: ");
			description.append("this change set is composed mainly of collaborators and low number of controller methods, and " );
			description.append("these methods implement generalization, dependency and association performing calls on parameters or local variable objects. ");
		} else if (stereotypedCommit.getStereotypes().get(0) == CommitStereotype.CONTROL_MODIFIER) {
			description.append("This is a control modifier commit: ");
			description.append("this change set is composed mainly of controller, factory, constructor, copy-constructor and destructor methods, and " );
			description.append("these methods modify the external behavior of the participating classes.  ");
		} else if (stereotypedCommit.getStereotypes().get(0) == CommitStereotype.LARGE_MODIFIER) {
			description.append("This is a large modifier commit: ");
			description.append("this is a commit with many methods and combines multiple roles. " );
		} else if (stereotypedCommit.getStereotypes().get(0) == CommitStereotype.LAZY_MODIFIER) {
			description.append("This is a lazy modifier commit: ");
			description.append("this change set is composed of getter and setter methods mainly, and a low percentage of other methods. " );
			description.append("These methods denote new or planned feature that is not yet completed. ");
		} else if (stereotypedCommit.getStereotypes().get(0) == CommitStereotype.DEGENERATE_MODIFIER) {
			description.append("This is a degenerate modifier commit: ");
			description.append("this change set is composed of empty, incidental, and abstract methods. " );
			description.append("These methods indicate that a new feature is planned. " );
		} else if (stereotypedCommit.getStereotypes().get(0) == CommitStereotype.SMALL_MODIFIER) {
			description.append("This is a small modifier commit that does not change the system significantly.");
		}
		
		return description.toString();
	}
}
