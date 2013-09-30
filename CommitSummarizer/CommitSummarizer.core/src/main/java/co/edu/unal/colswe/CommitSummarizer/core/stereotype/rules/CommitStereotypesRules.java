package co.edu.unal.colswe.CommitSummarizer.core.stereotype.rules;

import java.util.HashMap;
import java.util.List;

import co.edu.unal.colswe.CommitSummarizer.core.stereotype.stereotyped.StereotypedMethod;
import co.edu.unal.colswe.CommitSummarizer.core.stereotype.taxonomy.CommitStereotype;
import co.edu.unal.colswe.CommitSummarizer.core.stereotype.taxonomy.MethodStereotype;

public class CommitStereotypesRules {
	
	public CommitStereotype checkStructureModifier(List<StereotypedMethod> methods, HashMap<MethodStereotype, Integer> signatureMap) {
		CommitStereotype commitStereotype = null;
		/*for(StereotypedMethod stereotype : methods) {
			if(stereotype.isGet() || stereotype.isSet()) {
				commitStereotype = CommitStereotype.STRUCTURE_MODIFIER;
			} else if(!stereotype.isConstructor()) {
				commitStereotype = null;
				break;
			}
		}*/
		int get = (signatureMap.get(MethodStereotype.GET) != null) ?  signatureMap.get(MethodStereotype.GET) : 0;
		int set = (signatureMap.get(MethodStereotype.SET) != null) ?  signatureMap.get(MethodStereotype.SET) : 0;
		int sum = get + set;
		if(sum > 0 && methods.size() - (sum) > 0) {
			commitStereotype = CommitStereotype.STRUCTURE_MODIFIER;
		}
		return commitStereotype;
	}

	public CommitStereotype checkStateAccessModifier(List<StereotypedMethod> methods, HashMap<MethodStereotype, Integer> signatureMap) {
		CommitStereotype commitStereotype = null;
		/*int counter = 0;
		for(StereotypedMethod stereotype : methods) {
			if(stereotype.isAccessor()) {
				counter++;
			} 
		}
		System.out.println("Count accessors: " + counter + " > (2/3)*" + methods.size());
		if(counter > (methods.size()*(2/3))) {
			commitStereotype = CommitStereotype.STATE_ACCESS_MODIFIER;
		}*/
		int get = (signatureMap.get(MethodStereotype.GET) != null) ?  signatureMap.get(MethodStereotype.GET) : 0;
		int predicate = (signatureMap.get(MethodStereotype.PREDICATE) != null) ?  signatureMap.get(MethodStereotype.PREDICATE) : 0;
		int property = (signatureMap.get(MethodStereotype.PROPERTY) != null) ?  signatureMap.get(MethodStereotype.PROPERTY) : 0;
		int voidAccessor = (signatureMap.get(MethodStereotype.VOID_ACCESSOR) != null) ?  signatureMap.get(MethodStereotype.VOID_ACCESSOR) : 0;
		
		int accessors = get + predicate + property + voidAccessor;
		if(accessors > (2/3) * methods.size()) {
			commitStereotype = CommitStereotype.STATE_ACCESS_MODIFIER;
		}
		return commitStereotype;
	}
	
	public CommitStereotype checkStateUpdateModifier(List<StereotypedMethod> methods, HashMap<MethodStereotype, Integer> signatureMap) {
		CommitStereotype commitStereotype = null;
		/*int counter = 0;
		for(StereotypedMethod stereotype : methods) {
			if(stereotype.isMutator()) {
				counter++;
			} 
		}
		
		System.out.println("Count accessors: " + counter + " > (2/3)*" + methods.size());
		if(counter > (methods.size()*(2/3))) {
			commitStereotype = CommitStereotype.STATE_UPDATE_MODIFIER;
		}*/
		int set = (signatureMap.get(MethodStereotype.SET) != null) ?  signatureMap.get(MethodStereotype.SET) : 0;
		int nonVoidCommand = (signatureMap.get(MethodStereotype.NON_VOID_COMMAND) != null) ?  signatureMap.get(MethodStereotype.NON_VOID_COMMAND) : 0;
		int command = (signatureMap.get(MethodStereotype.COMMAND) != null) ?  signatureMap.get(MethodStereotype.COMMAND) : 0;
		int mutators = set + command + nonVoidCommand;
		if(mutators > (2/3) * methods.size()) {
			commitStereotype = CommitStereotype.STATE_UPDATE_MODIFIER;
		}
		return commitStereotype;
	}
	
	public CommitStereotype checkBehaviorModifier(List<StereotypedMethod> methods, HashMap<MethodStereotype, Integer> signatureMap) {
		CommitStereotype commitStereotype = null;
		/*int counter = 0;
		for(StereotypedMethod stereotype : methods) {
			System.out.println(stereotype.getQualifiedName() + " stereotype: " + stereotype.getStereotypes());
			if(stereotype.isCommand() || stereotype.isNonVoidCommand()) {
				counter++;
			} 
		}
		System.out.println("Count behavior: " + ((counter * 100) / methods.size()));
		if(((counter * 100) / methods.size()) >= 75) {
			commitStereotype = CommitStereotype.BEHAVIOR_MODIFIER;
		}*/
		int command = (signatureMap.get(MethodStereotype.COMMAND) != null) ?  signatureMap.get(MethodStereotype.COMMAND) : 0;
		int nonVoidCommand = (signatureMap.get(MethodStereotype.NON_VOID_COMMAND) != null) ?  signatureMap.get(MethodStereotype.NON_VOID_COMMAND) : 0;
		int behavioral = command + Math.abs(nonVoidCommand - command);
		if(behavioral > (2/3) * methods.size()) {
			commitStereotype = CommitStereotype.BEHAVIOR_MODIFIER;
		}
		return commitStereotype;
	}
	
	public CommitStereotype checkObjectCreationModifier(List<StereotypedMethod> methods, HashMap<MethodStereotype, Integer> signatureMap) {
		CommitStereotype commitStereotype = null;
		/*int counter = 0;
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
		*/
		int factory = (signatureMap.get(MethodStereotype.FACTORY) != null) ?  signatureMap.get(MethodStereotype.FACTORY) : 0;
		if(factory > (2/3) * methods.size()) {
			commitStereotype = CommitStereotype.OBJECT_CREATION_MODIFIER;
		}
		return commitStereotype;
	}
	
	public CommitStereotype checkRelationshipModifier(List<StereotypedMethod> methods, HashMap<MethodStereotype, Integer> signatureMap) {
		CommitStereotype commitStereotype = null;
		/*int counter = 0;
		for(StereotypedMethod stereotype : methods) {
			System.out.println(stereotype.getQualifiedName() + " stereotype: " + stereotype.getStereotypes());
			if(stereotype.isCollaborational()) {
				counter++;
			} 
		}
		System.out.println("Count collaborational: " + ((counter * 100) / methods.size()));
		if(((counter * 100) / methods.size()) >= 75) {
			commitStereotype = CommitStereotype.RELATIONSHIP_MODIFIER;
		}*/
		int collaborator = (signatureMap.get(MethodStereotype.COLLABORATOR) != null) ?  signatureMap.get(MethodStereotype.COLLABORATOR) : 0;
		int controller = (signatureMap.get(MethodStereotype.CONTROLLER) != null) ?  signatureMap.get(MethodStereotype.CONTROLLER) : 0;
		int factory = (signatureMap.get(MethodStereotype.FACTORY) != null) ?  signatureMap.get(MethodStereotype.FACTORY) : 0;
		int nonCollaborator = methods.size() - collaborator;
		if((collaborator > nonCollaborator) && 
				(factory < (1/2) * methods.size()) && 
				(controller < (1/3) * methods.size())) {
			commitStereotype = CommitStereotype.RELATIONSHIP_MODIFIER;
		}
		
		return commitStereotype;
	}
	
	public CommitStereotype checkControlModifier(List<StereotypedMethod> methods, HashMap<MethodStereotype, Integer> signatureMap) {
		CommitStereotype commitStereotype = null;
		/*int counter = 0;
		for(StereotypedMethod stereotype : methods) {
			System.out.println(stereotype.getQualifiedName() + " stereotype: " + stereotype.getStereotypes());
			if(stereotype.isController()) {
				counter++;
			} 
		}
		System.out.println("Count controller: " + ((counter * 100) / methods.size()));
		if(((counter * 100) / methods.size()) >= 75) {
			commitStereotype = CommitStereotype.CONTROL_MODIFIER;
		}*/
		int controller = (signatureMap.get(MethodStereotype.CONTROLLER) != null) ?  signatureMap.get(MethodStereotype.CONTROLLER) : 0;
		int factory = (signatureMap.get(MethodStereotype.FACTORY) != null) ?  signatureMap.get(MethodStereotype.FACTORY) : 0;
		int control = controller + factory;
		if(control > (2/3) * methods.size() && controller > 0) {
			commitStereotype = CommitStereotype.CONTROL_MODIFIER;
		}
		return commitStereotype;
	}
	
	
	public CommitStereotype checkLargeModifier(List<StereotypedMethod> methods, HashMap<MethodStereotype, Integer> signatureMap) {
		CommitStereotype commitStereotype = null;
		
		int set = (signatureMap.get(MethodStereotype.SET) != null) ?  signatureMap.get(MethodStereotype.SET) : 0;
		int nonVoidCommand = (signatureMap.get(MethodStereotype.NON_VOID_COMMAND) != null) ?  signatureMap.get(MethodStereotype.NON_VOID_COMMAND) : 0;
		int command = (signatureMap.get(MethodStereotype.COMMAND) != null) ?  signatureMap.get(MethodStereotype.COMMAND) : 0;
		int mutators = set + command + nonVoidCommand;
		
		int get = (signatureMap.get(MethodStereotype.GET) != null) ?  signatureMap.get(MethodStereotype.GET) : 0;
		int predicate = (signatureMap.get(MethodStereotype.PREDICATE) != null) ?  signatureMap.get(MethodStereotype.PREDICATE) : 0;
		int property = (signatureMap.get(MethodStereotype.PROPERTY) != null) ?  signatureMap.get(MethodStereotype.PROPERTY) : 0;
		int voidAccessor = (signatureMap.get(MethodStereotype.VOID_ACCESSOR) != null) ?  signatureMap.get(MethodStereotype.VOID_ACCESSOR) : 0;
		int factory = (signatureMap.get(MethodStereotype.FACTORY) != null) ?  signatureMap.get(MethodStereotype.FACTORY) : 0;
		int controller = (signatureMap.get(MethodStereotype.CONTROLLER) != null) ?  signatureMap.get(MethodStereotype.CONTROLLER) : 0;
		
		int accessors = get + predicate + property + voidAccessor;
		
		if(accessors + mutators > (1/5) * methods.size() && 
				(factory > (1/10) * methods.size() || controller >  (1/10) * methods.size()) &&
				(accessors <=  (1/2) * methods.size() || mutators <=  (1/2) * methods.size()) &&
				(factory > 0 || controller > 0) ) {
			commitStereotype = CommitStereotype.LARGE_MODIFIER;
		}
		
		return commitStereotype;
	}
	
	public CommitStereotype checkLazyModifier(List<StereotypedMethod> methods, HashMap<MethodStereotype, Integer> signatureMap) {
		CommitStereotype commitStereotype = null;
		int get = (signatureMap.get(MethodStereotype.GET) != null) ?  signatureMap.get(MethodStereotype.GET) : 0;
		int set = (signatureMap.get(MethodStereotype.SET) != null) ?  signatureMap.get(MethodStereotype.SET) : 0;
		int empty = (signatureMap.get(MethodStereotype.EMPTY) != null) ? signatureMap.get(MethodStereotype.EMPTY) : 0;
		int incidental = (signatureMap.get(MethodStereotype.INCIDENTAL) != null) ? signatureMap.get(MethodStereotype.INCIDENTAL) : 0;
		int abs = (signatureMap.get(MethodStereotype.ABSTRACT) != null) ? signatureMap.get(MethodStereotype.ABSTRACT) : 0;
		int getSet = get + set;
		int degenerate = incidental + empty + abs;
		int degenerational = methods.size() - (getSet - Math.abs(degenerate));
		if(getSet > 0 && 
				degenerational <= (1/3) * methods.size() && 
				Math.abs(degenerate) > (1/3) * methods.size()) {
			commitStereotype = CommitStereotype.LAZY_MODIFIER;
		}
		return commitStereotype;
	}
	
	public CommitStereotype checkDegenerateModifier(List<StereotypedMethod> methods, HashMap<MethodStereotype, Integer> signatureMap) {
		CommitStereotype commitStereotype = null;
		int empty = (signatureMap.get(MethodStereotype.EMPTY) != null) ? signatureMap.get(MethodStereotype.EMPTY) : 0;
		int incidental = (signatureMap.get(MethodStereotype.INCIDENTAL) != null) ? signatureMap.get(MethodStereotype.INCIDENTAL) : 0;
		int abs = (signatureMap.get(MethodStereotype.ABSTRACT) != null) ? signatureMap.get(MethodStereotype.ABSTRACT) : 0;
		int degenerate = incidental + empty + abs;
		if(Math.abs(degenerate) > 1) {
			commitStereotype = CommitStereotype.DEGENERATE_MODIFIER;
		}
		return commitStereotype;
	}
	
	public CommitStereotype checkSmallModifier(List<StereotypedMethod> methods, HashMap<MethodStereotype, Integer> signatureMap) {
		CommitStereotype commitStereotype = null;
		if(methods.size() < 3) {
			commitStereotype = CommitStereotype.SMALL_MODIFIER;
		}
		return commitStereotype;
	}

}
