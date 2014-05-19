package co.edu.unal.colswe.changescribe.core.stereotype.rules;

import java.util.TreeMap;
import java.util.List;

import co.edu.unal.colswe.changescribe.core.stereotype.stereotyped.StereotypedMethod;
import co.edu.unal.colswe.changescribe.core.stereotype.taxonomy.CommitStereotype;
import co.edu.unal.colswe.changescribe.core.stereotype.taxonomy.MethodStereotype;

public class CommitStereotypesRules {
	
	public CommitStereotype checkStructureModifier(List<StereotypedMethod> methods, TreeMap<MethodStereotype, Integer> signatureMap) {
		CommitStereotype commitStereotype = null;
		
		int get = (signatureMap.get(MethodStereotype.GET) != null) ? signatureMap.get(MethodStereotype.GET) : 0;
		int set = (signatureMap.get(MethodStereotype.SET) != null) ? signatureMap.get(MethodStereotype.SET) : 0;
		int sum = get + set;
		
		if(sum > 0 && methods.size() - (sum) > 0) {
			commitStereotype = CommitStereotype.STRUCTURE_MODIFIER;
		}
		return commitStereotype;
	}

	public CommitStereotype checkStateAccessModifier(List<StereotypedMethod> methods, TreeMap<MethodStereotype, Integer> signatureMap) {
		CommitStereotype commitStereotype = null;
		
		int get = (signatureMap.get(MethodStereotype.GET) != null) ? signatureMap.get(MethodStereotype.GET) : 0;
		int predicate = (signatureMap.get(MethodStereotype.PREDICATE) != null) ? signatureMap.get(MethodStereotype.PREDICATE) : 0;
		int property = (signatureMap.get(MethodStereotype.PROPERTY) != null) ? signatureMap.get(MethodStereotype.PROPERTY) : 0;
		int voidAccessor = (signatureMap.get(MethodStereotype.VOID_ACCESSOR) != null) ?  signatureMap.get(MethodStereotype.VOID_ACCESSOR) : 0;
		int accessors = get + predicate + property + voidAccessor;
		
		if(accessors > (2/3) * methods.size()) {
			commitStereotype = CommitStereotype.STATE_ACCESS_MODIFIER;
		}
		return commitStereotype;
	}
	
	public CommitStereotype checkStateUpdateModifier(List<StereotypedMethod> methods, TreeMap<MethodStereotype, Integer> signatureMap) {
		CommitStereotype commitStereotype = null;
		
		int set = (signatureMap.get(MethodStereotype.SET) != null) ?  signatureMap.get(MethodStereotype.SET) : 0;
		int nonVoidCommand = (signatureMap.get(MethodStereotype.NON_VOID_COMMAND) != null) ? signatureMap.get(MethodStereotype.NON_VOID_COMMAND) : 0;
		int command = (signatureMap.get(MethodStereotype.COMMAND) != null) ? signatureMap.get(MethodStereotype.COMMAND) : 0;
		int mutators = set + command + nonVoidCommand;
		
		if(mutators > (2/3) * methods.size()) {
			commitStereotype = CommitStereotype.STATE_UPDATE_MODIFIER;
		}
		return commitStereotype;
	}
	
	public CommitStereotype checkBehaviorModifier(List<StereotypedMethod> methods, TreeMap<MethodStereotype, Integer> signatureMap) {
		CommitStereotype commitStereotype = null;
		
		int command = (signatureMap.get(MethodStereotype.COMMAND) != null) ? signatureMap.get(MethodStereotype.COMMAND) : 0;
		int nonVoidCommand = (signatureMap.get(MethodStereotype.NON_VOID_COMMAND) != null) ? signatureMap.get(MethodStereotype.NON_VOID_COMMAND) : 0;
		int behavioral = command + Math.abs(nonVoidCommand - command);
		
		if(behavioral > (2/3) * methods.size()) {
			commitStereotype = CommitStereotype.BEHAVIOR_MODIFIER;
		}
		return commitStereotype;
	}
	
	public CommitStereotype checkObjectCreationModifier(List<StereotypedMethod> methods, TreeMap<MethodStereotype, Integer> signatureMap) {
		CommitStereotype commitStereotype = null;
		
		int constructor = (signatureMap.get(MethodStereotype.CONSTRUCTOR) != null) ? signatureMap.get(MethodStereotype.CONSTRUCTOR) : 0;
		int copyConstructor = (signatureMap.get(MethodStereotype.COPY_CONSTRUCTOR) != null) ? signatureMap.get(MethodStereotype.COPY_CONSTRUCTOR) : 0;
		int destructor = (signatureMap.get(MethodStereotype.DESTRUCTOR) != null) ? signatureMap.get(MethodStereotype.DESTRUCTOR) : 0;
		int factory = (signatureMap.get(MethodStereotype.FACTORY) != null) ? signatureMap.get(MethodStereotype.FACTORY) : 0;
		int creational = factory + destructor + copyConstructor + constructor;
		
		if(creational > (2/3) * methods.size()) {
			commitStereotype = CommitStereotype.OBJECT_CREATION_MODIFIER;
		}
		return commitStereotype;
	}
	
	public CommitStereotype checkRelationshipModifier(List<StereotypedMethod> methods, TreeMap<MethodStereotype, Integer> signatureMap) {
		CommitStereotype commitStereotype = null;

		int collaborator = (signatureMap.get(MethodStereotype.COLLABORATOR) != null) ?  signatureMap.get(MethodStereotype.COLLABORATOR) : 0;
		int controller = (signatureMap.get(MethodStereotype.CONTROLLER) != null) ? signatureMap.get(MethodStereotype.CONTROLLER) : 0;
		int factory = (signatureMap.get(MethodStereotype.FACTORY) != null) ? signatureMap.get(MethodStereotype.FACTORY) : 0;
		int nonCollaborator = methods.size() - collaborator;
		
		if((collaborator > nonCollaborator) && 
				(factory < (1/2) * methods.size()) && 
				(controller < (1/3) * methods.size())) {
			commitStereotype = CommitStereotype.RELATIONSHIP_MODIFIER;
		}
		
		return commitStereotype;
	}
	
	public CommitStereotype checkControlModifier(List<StereotypedMethod> methods, TreeMap<MethodStereotype, Integer> signatureMap) {
		CommitStereotype commitStereotype = null;
		
		int controller = (signatureMap.get(MethodStereotype.CONTROLLER) != null) ? signatureMap.get(MethodStereotype.CONTROLLER) : 0;
		int factory = (signatureMap.get(MethodStereotype.FACTORY) != null) ? signatureMap.get(MethodStereotype.FACTORY) : 0;
		int control = controller + factory;
		
		if(control > (2/3) * methods.size() && controller > 0) {
			commitStereotype = CommitStereotype.CONTROL_MODIFIER;
		}
		return commitStereotype;
	}
	
	
	public CommitStereotype checkLargeModifier(List<StereotypedMethod> methods, TreeMap<MethodStereotype, Integer> signatureMap) {
		CommitStereotype commitStereotype = null;
		
		int set = (signatureMap.get(MethodStereotype.SET) != null) ?  signatureMap.get(MethodStereotype.SET) : 0;
		int nonVoidCommand = (signatureMap.get(MethodStereotype.NON_VOID_COMMAND) != null) ? signatureMap.get(MethodStereotype.NON_VOID_COMMAND) : 0;
		int command = (signatureMap.get(MethodStereotype.COMMAND) != null) ?  signatureMap.get(MethodStereotype.COMMAND) : 0;
		int mutators = set + command + nonVoidCommand;
		int get = (signatureMap.get(MethodStereotype.GET) != null) ? signatureMap.get(MethodStereotype.GET) : 0;
		int predicate = (signatureMap.get(MethodStereotype.PREDICATE) != null) ? signatureMap.get(MethodStereotype.PREDICATE) : 0;
		int property = (signatureMap.get(MethodStereotype.PROPERTY) != null) ? signatureMap.get(MethodStereotype.PROPERTY) : 0;
		int voidAccessor = (signatureMap.get(MethodStereotype.VOID_ACCESSOR) != null) ? signatureMap.get(MethodStereotype.VOID_ACCESSOR) : 0;
		int factory = (signatureMap.get(MethodStereotype.FACTORY) != null) ? signatureMap.get(MethodStereotype.FACTORY) : 0;
		int controller = (signatureMap.get(MethodStereotype.CONTROLLER) != null) ? signatureMap.get(MethodStereotype.CONTROLLER) : 0;
		int accessors = get + predicate + property + voidAccessor;
		
		if(accessors + mutators > (1/5) * methods.size() && 
				(factory > (1/10) * methods.size() || controller > (1/10) * methods.size()) &&
				(accessors <=  (1/2) * methods.size() || mutators <= (1/2) * methods.size()) &&
				(factory > 0 || controller > 0) ) {
			commitStereotype = CommitStereotype.LARGE_MODIFIER;
		}
		
		return commitStereotype;
	}
	
	public CommitStereotype checkLazyModifier(List<StereotypedMethod> methods, TreeMap<MethodStereotype, Integer> signatureMap) {
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
	
	public CommitStereotype checkDegenerateModifier(List<StereotypedMethod> methods, TreeMap<MethodStereotype, Integer> signatureMap) {
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
	
	public CommitStereotype checkSmallModifier(List<StereotypedMethod> methods, TreeMap<MethodStereotype, Integer> signatureMap) {
		CommitStereotype commitStereotype = null;
		
		if(methods.size() < 3) {
			commitStereotype = CommitStereotype.SMALL_MODIFIER;
		}
		return commitStereotype;
	}

}
