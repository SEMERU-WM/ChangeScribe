package co.edu.unal.colswe.changescribe.core.textgenerator;

import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;

public class ChangeDescriptor {

	public ChangeDescriptor() {
		// TODO Auto-generated constructor stub
	}
	
	public String generateChangeDescription(SourceCodeChange change) {
		ChangeTypeDescriptor typeDescriptor = new ChangeTypeDescriptor();
		StringBuilder template = new StringBuilder();
		
		template.append(typeDescriptor.generateDescription(change.getChangeType()));
		template.append(artifactName(change));
		template.append(rootNodeName(change));
		//TODO describe each change according to granularity level
		
		return template.toString();
	}
	
	public String rootNodeName(SourceCodeChange change) {
		return " in " + change.getRootEntity().getUniqueName();
	}
	
	public String artifactName(SourceCodeChange change) {
		return " " + change.getChangedEntity().getUniqueName();
	}
}
