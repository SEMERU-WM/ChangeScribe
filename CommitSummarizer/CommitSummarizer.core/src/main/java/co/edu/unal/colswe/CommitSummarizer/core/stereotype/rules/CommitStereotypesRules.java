package co.edu.unal.colswe.CommitSummarizer.core.stereotype.rules;

import java.util.List;

import co.edu.unal.colswe.CommitSummarizer.core.stereotype.stereotyped.StereotypedMethod;
import co.edu.unal.colswe.CommitSummarizer.core.stereotype.taxonomy.CommitStereotype;

public class CommitStereotypesRules {
	
	public CommitStereotype checkStructureModifier(List<StereotypedMethod> methods) {
		CommitStereotype commitStereotype = null;
		for(StereotypedMethod stereotype : methods) {
			if(stereotype.isGet() || stereotype.isSet()) {
				commitStereotype = CommitStereotype.STRUCTURE_MODIFIER;
			} else if(!stereotype.isConstructor()) {
				commitStereotype = null;
				break;
			}
		}
		return commitStereotype;
	}

	public CommitStereotype checkStateAccessModifier(List<StereotypedMethod> methods) {
		CommitStereotype commitStereotype = null;
		int counter = 0;
		for(StereotypedMethod stereotype : methods) {
			System.out.println(stereotype.getQualifiedName() + " stereotype: " + stereotype.getStereotypes());
			if(stereotype.isAccessor()) {
				counter++;
			} 
		}
		System.out.println("Count accessors: " + ((counter * 100) / methods.size()));
		if(((counter * 100) / methods.size()) >= 75) {
			commitStereotype = CommitStereotype.STATE_ACCESS_MODIFIER;
		}
		return commitStereotype;
	}
	
	public CommitStereotype checkStateUpdateModifier(List<StereotypedMethod> methods) {
		CommitStereotype commitStereotype = null;
		int counter = 0;
		for(StereotypedMethod stereotype : methods) {
			System.out.println(stereotype.getQualifiedName() + " stereotype: " + stereotype.getStereotypes());
			if(stereotype.isMutator()) {
				counter++;
			} 
		}
		System.out.println("Count mutator: " + ((counter * 100) / methods.size()));
		if(((counter * 100) / methods.size()) >= 75) {
			commitStereotype = CommitStereotype.STATE_UPDATE_MODIFIER;
		}
		return commitStereotype;
	}
	
	public CommitStereotype checkBehaviorModifier(List<StereotypedMethod> methods) {
		CommitStereotype commitStereotype = null;
		int counter = 0;
		for(StereotypedMethod stereotype : methods) {
			System.out.println(stereotype.getQualifiedName() + " stereotype: " + stereotype.getStereotypes());
			if(stereotype.isCommand() || stereotype.isNonVoidCommand()) {
				counter++;
			} 
		}
		System.out.println("Count behavior: " + ((counter * 100) / methods.size()));
		if(((counter * 100) / methods.size()) >= 75) {
			commitStereotype = CommitStereotype.BEHAVIOR_MODIFIER;
		}
		return commitStereotype;
	}
	
	public CommitStereotype checkObjectCreationModifier(List<StereotypedMethod> methods) {
		CommitStereotype commitStereotype = null;
		int counter = 0;
		for(StereotypedMethod stereotype : methods) {
			System.out.println(stereotype.getQualifiedName() + " stereotype: " + stereotype.getStereotypes());
			if(stereotype.isFactory()) {
				counter++;
			} 
		}
		System.out.println("Count factory: " + ((counter * 100) / methods.size()));
		if(((counter * 100) / methods.size()) >= 75) {
			commitStereotype = CommitStereotype.OBJECT_CREATION_MODIFIER;
		}
		return commitStereotype;
	}
	
	public CommitStereotype checkRelationshipModifier(List<StereotypedMethod> methods) {
		CommitStereotype commitStereotype = null;
		int counter = 0;
		for(StereotypedMethod stereotype : methods) {
			System.out.println(stereotype.getQualifiedName() + " stereotype: " + stereotype.getStereotypes());
			if(stereotype.isCollaborational()) {
				counter++;
			} 
		}
		System.out.println("Count collaborational: " + ((counter * 100) / methods.size()));
		if(((counter * 100) / methods.size()) >= 75) {
			commitStereotype = CommitStereotype.RELATIONSHIP_MODIFIER;
		}
		return commitStereotype;
	}
	
	public CommitStereotype checkControlModifier(List<StereotypedMethod> methods) {
		CommitStereotype commitStereotype = null;
		int counter = 0;
		for(StereotypedMethod stereotype : methods) {
			System.out.println(stereotype.getQualifiedName() + " stereotype: " + stereotype.getStereotypes());
			if(stereotype.isController()) {
				counter++;
			} 
		}
		System.out.println("Count controller: " + ((counter * 100) / methods.size()));
		if(((counter * 100) / methods.size()) >= 75) {
			commitStereotype = CommitStereotype.CONTROL_MODIFIER;
		}
		return commitStereotype;
	}
	
	public CommitStereotype checkLazyModifier(List<StereotypedMethod> methods) {
		return null;
	}
	
	public CommitStereotype checkDegenerateModifier(List<StereotypedMethod> methods) {
		return null;
	}
	
	public CommitStereotype checkSmallModifier(List<StereotypedMethod> methods) {
		return null;
	}

}
