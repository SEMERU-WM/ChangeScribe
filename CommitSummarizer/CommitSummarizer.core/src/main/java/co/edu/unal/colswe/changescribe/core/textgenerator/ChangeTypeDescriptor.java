package co.edu.unal.colswe.changescribe.core.textgenerator;

import co.edu.unal.colswe.changescribe.core.Constants;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.ChangeType;

public class ChangeTypeDescriptor {

	public ChangeTypeDescriptor() {
		
	}
	
	public String generateDescription(ChangeType type) {
		return type.name().toLowerCase().replace(Constants.UNDERSCORE, Constants.SPACE);
	}

}
