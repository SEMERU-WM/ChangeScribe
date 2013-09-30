package co.edu.unal.colswe.CommitSummarizer.core.stereotype.stereotyped;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.edu.unal.colswe.CommitSummarizer.core.stereotype.rules.CommitStereotypesRules;
import co.edu.unal.colswe.CommitSummarizer.core.stereotype.taxonomy.CommitStereotype;

public class StereotypedCommit {
	
	private List<StereotypedMethod> methods;
	private HashMap<StereotypedMethod, Integer> signatureMap = new HashMap<StereotypedMethod, Integer>();
	private CommitStereotype primaryStereotype;
	private CommitStereotype secondaryStereotype;
	
	public StereotypedCommit(List<StereotypedMethod> methods) {
		super();
		this.methods = methods;
	}

	public void buildSignature() {
		signatureMap = new HashMap<StereotypedMethod, Integer>();
		for(StereotypedMethod method: methods) {
			//Integer value = null;
			if(!getSignatureMap().containsKey(method.getStereotypes().get(0))) {
				//getSignatureMap().put((StereotypedMethod) method.getStereotypes().get(0), 1);
			} else {
				//value = getSignatureMap().get(method.getStereotypes().get(0));
				//getSignatureMap().put((StereotypedMethod) method.getStereotypes().get(0), value + 1);
			}
		}
		System.out.println("signatures: " + getSignatureMap().toString());
	}
	
	public CommitStereotype findStereotypes() {
		CommitStereotypesRules rules = new CommitStereotypesRules();
		primaryStereotype = null;
		
		//control modifier
		primaryStereotype = rules.checkControlModifier(methods);
		if(primaryStereotype != null) {
			return primaryStereotype;
		}
		
		//relationship modifier
		primaryStereotype = rules.checkRelationshipModifier(methods);
		if(primaryStereotype != null) {
			secondaryStereotype = rules.checkBehaviorModifier(methods);
			if(secondaryStereotype != null) {
				CommitStereotype tmp = primaryStereotype;
				primaryStereotype = secondaryStereotype;
				secondaryStereotype = tmp;
			} else {
				secondaryStereotype = rules.checkStateAccessModifier(methods);
				if(secondaryStereotype != null) {
					CommitStereotype tmp = primaryStereotype;
					primaryStereotype = secondaryStereotype;
					secondaryStereotype = tmp;
				}
			}
			return primaryStereotype;
		}
		
		//state update modifier
		primaryStereotype = rules.checkStateUpdateModifier(methods);
		if(primaryStereotype != null) {
			secondaryStereotype = rules.checkBehaviorModifier(methods);
			if(secondaryStereotype != null) {
				CommitStereotype tmp = primaryStereotype;
				primaryStereotype = secondaryStereotype;
				secondaryStereotype = tmp;
			}
			return primaryStereotype;
		}
		
		//state access modifier
		primaryStereotype = rules.checkStateAccessModifier(methods);
		if(primaryStereotype != null) {
			return primaryStereotype;
		}
		
		//structure modifier
		primaryStereotype = rules.checkStructureModifier(methods);
		if(primaryStereotype != null) {
			return primaryStereotype;
		}
		
		//object creation modifier
		primaryStereotype = rules.checkObjectCreationModifier(methods);
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
	
	public HashMap<StereotypedMethod, Integer> getSignatureMap() {
		return signatureMap;
	}

	public void setSignatureMap(HashMap<StereotypedMethod, Integer> frequencymap) {
		this.signatureMap = frequencymap;
	}

	public List<StereotypedMethod> getMethods() {
		return methods;
	}

	public void setMethods(List<StereotypedMethod> methods) {
		this.methods = methods;
	}


}
