package co.edu.unal.colswe.changescribe.core.stereotype.stereotyped;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import co.edu.unal.colswe.changescribe.core.stereotype.rules.CommitStereotypesRules;
import co.edu.unal.colswe.changescribe.core.stereotype.taxonomy.CommitStereotype;
import co.edu.unal.colswe.changescribe.core.stereotype.taxonomy.MethodStereotype;

public class StereotypedCommit {
	
	private List<StereotypedMethod> methods;
	private TreeMap<MethodStereotype, Integer> signatureMap = new TreeMap<MethodStereotype, Integer>();
	private CommitStereotype primaryStereotype;
	private CommitStereotype secondaryStereotype;
	
	public StereotypedCommit(List<StereotypedMethod> methods) {
		super();
		this.methods = methods;
	}

	public void buildSignature() {
		signatureMap = new TreeMap<MethodStereotype, Integer>();
		for(Object object: methods) {
			if(object instanceof StereotypedMethod) {
				StereotypedMethod method = (StereotypedMethod) object;
				Integer value = null;
				if(!getSignatureMap().containsKey(method.getStereotypes().get(0))) {
					getSignatureMap().put((MethodStereotype) method.getStereotypes().get(0), 1);
				} else {
					value = getSignatureMap().get(method.getStereotypes().get(0));
					getSignatureMap().put((MethodStereotype) method.getStereotypes().get(0), value + 1);
				}
			}
		}
		System.out.println("signatures: " + getSignatureMap().toString());
	}
	
	public CommitStereotype findStereotypes() {
		CommitStereotypesRules rules = new CommitStereotypesRules();
		primaryStereotype = null;
		
		//small modifier
		primaryStereotype = rules.checkSmallModifier(methods, signatureMap);
		if(primaryStereotype != null) {
			return primaryStereotype;
		}
		
		//large modifier
		primaryStereotype = rules.checkLargeModifier(methods, signatureMap);
		if(primaryStereotype != null) {
			return primaryStereotype;
		}
		
		//degenarate modifier
		primaryStereotype = rules.checkDegenerateModifier(methods, signatureMap);
		if(primaryStereotype != null) {
			return primaryStereotype;
		}
		
		//lazy modifier
		primaryStereotype = rules.checkLazyModifier(methods, signatureMap);
		if(primaryStereotype != null) {
			return primaryStereotype;
		}
		
		//control modifier
		primaryStereotype = rules.checkControlModifier(methods, signatureMap);
		if(primaryStereotype != null) {
			return primaryStereotype;
		}
		
		//relationship modifier
		primaryStereotype = rules.checkRelationshipModifier(methods, signatureMap);
		if(primaryStereotype != null) {
			secondaryStereotype = rules.checkBehaviorModifier(methods, signatureMap);
			if(secondaryStereotype != null) {
				CommitStereotype tmp = primaryStereotype;
				primaryStereotype = secondaryStereotype;
				secondaryStereotype = tmp;
			} else {
				secondaryStereotype = rules.checkStateAccessModifier(methods, signatureMap);
				if(secondaryStereotype != null) {
					CommitStereotype tmp = primaryStereotype;
					primaryStereotype = secondaryStereotype;
					secondaryStereotype = tmp;
				}
			}
			return primaryStereotype;
		}
		
		//state update modifier
		primaryStereotype = rules.checkStateUpdateModifier(methods, signatureMap);
		if(primaryStereotype != null) {
			secondaryStereotype = rules.checkBehaviorModifier(methods, signatureMap);
			if(secondaryStereotype != null) {
				CommitStereotype tmp = primaryStereotype;
				primaryStereotype = secondaryStereotype;
				secondaryStereotype = tmp;
			}
			return primaryStereotype;
		}
		
		//state access modifier
		primaryStereotype = rules.checkStateAccessModifier(methods, signatureMap);
		if(primaryStereotype != null) {
			return primaryStereotype;
		}
		
		//structure modifier
		primaryStereotype = rules.checkStructureModifier(methods, signatureMap);
		if(primaryStereotype != null) {
			return primaryStereotype;
		}
		
		//object creation modifier
		primaryStereotype = rules.checkObjectCreationModifier(methods, signatureMap);
		if(primaryStereotype != null) {
			return primaryStereotype;
		}
		
		return null;
	}
	
	public List<CommitStereotype> getStereotypes() {
		List<CommitStereotype> stereotypes = new ArrayList<CommitStereotype>();
		
		if(primaryStereotype !=  null) {
			stereotypes.add(primaryStereotype);
		}
		
		if(secondaryStereotype !=  null) {
			stereotypes.add(secondaryStereotype);
		}
		
		return stereotypes;
	}
	
	public TreeMap<MethodStereotype, Integer> getSignatureMap() {
		return signatureMap;
	}

	public void setSignatureMap(TreeMap<MethodStereotype, Integer> signatureMap) {
		this.signatureMap = signatureMap;
	}

	public List<StereotypedMethod> getMethods() {
		return methods;
	}

	public void setMethods(List<StereotypedMethod> methods) {
		this.methods = methods;
	}
}
