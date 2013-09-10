package unal.edu.co.textgenerator;

import ch.uzh.ifi.seal.changedistiller.model.classifiers.ChangeType;

public class ChangeTypeDescriptor {
	
	

	public ChangeTypeDescriptor() {
		
	}
	
	public String generateDescription(ChangeType type) {
		return type.name().toLowerCase().replace("_", " ");
	}

}
