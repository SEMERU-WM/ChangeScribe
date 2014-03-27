package co.edu.unal.colswe.CommitSummarizer.core.summarizer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.CompoundAssignment;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ForStatement;
import org.eclipse.jdt.internal.compiler.ast.ForeachStatement;
import org.eclipse.jdt.internal.compiler.ast.IfStatement;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.OperatorIds;
import org.eclipse.jdt.internal.compiler.ast.PrefixExpression;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.ThrowStatement;
import org.eclipse.jdt.internal.compiler.ast.TrueLiteral;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.RevisionSyntaxException;

import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller.Language;
import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.ChangeType;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType;
import ch.uzh.ifi.seal.changedistiller.model.entities.Delete;
import ch.uzh.ifi.seal.changedistiller.model.entities.Insert;
import ch.uzh.ifi.seal.changedistiller.model.entities.Move;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import ch.uzh.ifi.seal.changedistiller.model.entities.StructureEntityVersion;
import ch.uzh.ifi.seal.changedistiller.model.entities.Update;
import co.edu.unal.colswe.CommitSummarizer.core.git.ChangedFile;
import co.edu.unal.colswe.CommitSummarizer.core.textgenerator.phrase.NounPhrase;
import co.edu.unal.colswe.CommitSummarizer.core.textgenerator.phrase.Parameter;
import co.edu.unal.colswe.CommitSummarizer.core.textgenerator.phrase.ParameterPhrase;
import co.edu.unal.colswe.CommitSummarizer.core.textgenerator.phrase.VerbPhrase;
import co.edu.unal.colswe.CommitSummarizer.core.textgenerator.pos.POSTagger;
import co.edu.unal.colswe.CommitSummarizer.core.textgenerator.tokenizer.Tokenizer;
import co.edu.unal.colswe.CommitSummarizer.core.util.Utils;

@SuppressWarnings("restriction")
public class ModificationDescriptor {
	
	private List<SourceCodeChange> changes;
	private ChangedFile file;
	private Git git;
	
	public void extractDifferences(ChangedFile file, Git git) {
		FileDistiller distiller = ChangeDistiller.createFileDistiller(Language.JAVA); 
		try {
			compareModified(file, distiller, git);
		} catch(IllegalStateException ex) {
			ex.printStackTrace();
		}
		changes = distiller.getSourceCodeChanges();
	}
	
	public void extractDifferencesBetweenVersions(ChangedFile file, Git git, String olderID, String currentID) {
		FileDistiller distiller = ChangeDistiller.createFileDistiller(Language.JAVA);
		try {
			compareModifiedVersions(file, distiller, git, olderID, currentID);
		} catch(IllegalStateException ex) {
			ex.printStackTrace();
		}
		changes = distiller.getSourceCodeChanges();
	}
	
	public void extractModifiedMethods() {
		List<StructureEntityVersion> modifiedMethods = new ArrayList<StructureEntityVersion>();
		file.setModifiedMethods(modifiedMethods);
		if(changes != null) {
			for(SourceCodeChange change : changes) {
				if(change.getRootEntity() != null && (change.getRootEntity().getType().equals(JavaEntityType.METHOD) ||
						change.getRootEntity().getType().equals(JavaEntityType.METHOD_DECLARATION))) {
					if(!modifiedMethods.contains(change.getRootEntity())) {
						modifiedMethods.add(change.getRootEntity());
					}
    			} else if(change.getChangedEntity() != null && change.getChangedEntity().getType().equals(JavaEntityType.METHOD)) {
    				StructureEntityVersion entityVersion = new StructureEntityVersion(change.getChangedEntity().getType(), change.getChangedEntity().getUniqueName(), change.getChangedEntity().getModifiers(), change.getChangedEntity().getJavaStructureNode());
    				if(!modifiedMethods.contains(entityVersion)) {
    					modifiedMethods.add(entityVersion);
    				}
    			}
			}
		}
	}

	public void describe(int i, int j, StringBuilder desc) {
		StringBuilder localDescription = new StringBuilder("");
		if(changes != null) {
			if(changes != null && changes.size() > 0) {
				desc.append((i - 1) + "." + j + ". " + " Modifications to " + file.getName() + ":  \n\n");
			} 
			int k = 1;
		    for(SourceCodeChange change : changes) {
		    	StringBuilder descTmp = new StringBuilder("");
		    	if(change instanceof Update) {
		    		Update update = (Update) change;
		    		describeUpdate(descTmp, change, update);
		    		
		    	} else if(change instanceof Insert) {
		    		Insert insert = (Insert) change;
		    		describeInsert(descTmp, insert); 
	    			
	    		} else if(change instanceof Delete) {
	    			Delete delete = (Delete) change;
	    			describeDelete(descTmp, delete);
	    		} else if(change instanceof Move) {
	    		} 
		    	
		    	if(!descTmp.toString().equals("") && (change instanceof Update || change instanceof Insert || change instanceof Delete)) {
			    	
			    	if(!localDescription.toString().toLowerCase().contains(descTmp.toString().toLowerCase())) {
			    		desc.append("\t\t");
				    	desc.append((i - 1) + "." + j + "." + k + ". ");
			    		desc.append(descTmp.toString());
			    		localDescription.append(descTmp.toString());
			    		
			    		if(!descTmp.toString().equals("") && (change instanceof Update || change instanceof Insert || change instanceof Delete)) {
				    		desc.append("\n");
				    		k++;
				    	}
			    	}
		    	}
		    }
		    if(!localDescription.toString().equals("")) {
		    	desc.append("\n");
		    }
		}
	}

	public static void describeDelete(StringBuilder desc, Delete delete) {
		if(delete.getChangeType() == ChangeType.STATEMENT_DELETE) {
			String statementType = delete.getChangedEntity().getType().name().toLowerCase().replace("statement", "").replace("_", " ");
			desc.append("Remove ");
			desc.append(statementType);
			if(delete.getChangedEntity().getAstNode() != null && delete.getChangedEntity().getAstNode() instanceof LocalDeclaration) {
				LocalDeclaration localDec = (LocalDeclaration) delete.getChangedEntity().getAstNode();
				NounPhrase phrase = new NounPhrase(Tokenizer.split(new String(localDec.name)));
				phrase.generate();
				desc.append(" to " + phrase.toString() + " at " + delete.getRootEntity().getJavaStructureNode().getName() + " method");
			} else if(delete.getChangedEntity().getAstNode() != null && delete.getChangedEntity().getAstNode() instanceof ForeachStatement) {
				ForeachStatement forDec = (ForeachStatement) delete.getChangedEntity().getAstNode();
				NounPhrase phrase = null;
				if(forDec.collection instanceof MessageSend) {
					phrase = new NounPhrase(Tokenizer.split(((MessageSend)forDec.collection).receiver.toString()));
				} else {
					phrase = new NounPhrase(Tokenizer.split((forDec.collection).toString().toString()));
				}
				phrase.generate();
				desc.append(" loop for " + phrase.toString() + " collection at " + delete.getRootEntity().getJavaStructureNode().getName() + " method");
			} else if(delete.getChangedEntity().getAstNode() != null && delete.getChangedEntity().getAstNode() instanceof MessageSend) {
				MessageSend messageSend = (MessageSend) delete.getChangedEntity().getAstNode();
				NounPhrase phrase = new NounPhrase(Tokenizer.split(new String(messageSend.selector)));
				phrase.generate();
				desc.append(" to " + phrase.toString());
				if(messageSend.arguments != null && messageSend.arguments.length > 0) {
					if(messageSend.arguments[0] instanceof SingleNameReference) {
						phrase = new NounPhrase(Tokenizer.split(new String(((SingleNameReference)messageSend.arguments[0]).token)));
					} else if(messageSend.arguments[0] instanceof TrueLiteral) {
						phrase = new NounPhrase(Tokenizer.split(new String(((TrueLiteral)messageSend.arguments[0]).toString())));
					}
					phrase.generate();
				}
				if(!desc.toString().endsWith(phrase.toString())) {
					desc.append(" " + phrase.toString());
				}
				desc.append(" at " + delete.getRootEntity().getJavaStructureNode().getName() + " method");
			} else if(delete.getChangedEntity().getAstNode() != null && delete.getChangedEntity().getAstNode() instanceof CompoundAssignment) {
				CompoundAssignment statement = (CompoundAssignment) delete.getChangedEntity().getAstNode();
				if(delete.getChangedEntity().getAstNode() instanceof PrefixExpression) {
					PrefixExpression prefixExpression = (PrefixExpression) delete.getChangedEntity().getAstNode();
					
					if(OperatorIds.PLUS == prefixExpression.operator) {
						desc.append(" to increment ");
					} else if(OperatorIds.MINUS == prefixExpression.operator) {
						desc.append(" to decrement ");
					}
					
					desc.append(" to " + prefixExpression.lhs.toString());
				} else {
					desc.append(" to " + statement.lhs.toString());
				}
				desc.append(" at " + delete.getRootEntity().getJavaStructureNode().getName() + " " + delete.getRootEntity().getJavaStructureNode().getType().name().toLowerCase());
			} else if(delete.getChangedEntity().getAstNode() != null && delete.getChangedEntity().getAstNode() instanceof ReturnStatement) {
				desc.append(" statement ");
				desc.append(" at " + delete.getRootEntity().getJavaStructureNode().getName() + " " + delete.getRootEntity().getJavaStructureNode().getType().name().toLowerCase());
			} else if(delete.getChangedEntity().getAstNode() != null && delete.getChangedEntity().getAstNode() instanceof IfStatement) {
				desc.append(" statement ");
				if(!delete.getRootEntity().getJavaStructureNode().getName().equals("")) {
					desc.append(" at " + delete.getRootEntity().getJavaStructureNode().getName() + " " + delete.getRootEntity().getJavaStructureNode().getType().name().toLowerCase());
				}
			} else if(delete.getChangedEntity().getAstNode() != null && delete.getChangedEntity().getAstNode() instanceof Assignment) {
				desc.append(" statement of " + ((Assignment)delete.getChangedEntity().getAstNode()).lhs);
				desc.append(" at " + delete.getRootEntity().getJavaStructureNode().getName() + " " + delete.getRootEntity().getJavaStructureNode().getType().name().toLowerCase());
			} else if(delete.getChangedEntity().getAstNode() != null && delete.getChangedEntity().getAstNode() instanceof ForStatement) {
				desc.append(" loop with " + ((ForStatement)delete.getChangedEntity().getAstNode()).condition + " condition");
				desc.append(" at " + delete.getRootEntity().getJavaStructureNode().getName() + " " + delete.getRootEntity().getJavaStructureNode().getType().name().toLowerCase());
			} else if(delete.getChangedEntity().getAstNode() != null && delete.getChangedEntity().getAstNode() instanceof ThrowStatement) {
				desc.append(" statement of " + ((AllocationExpression)((ThrowStatement)delete.getChangedEntity().getAstNode()).exception).type + " exception");
			}
		} else if(delete.getChangeType() == ChangeType.PARENT_CLASS_DELETE) {
			desc.append(StringUtils.capitalize("Remove parent class ") + delete.getChangedEntity().getUniqueName());
		} else if(delete.getChangeType() == ChangeType.PARENT_INTERFACE_DELETE) {
			desc.append(StringUtils.capitalize("Remove parent interface ") + delete.getChangedEntity().getUniqueName());
		} else if(delete.getChangeType() == ChangeType.ADDING_METHOD_OVERRIDABILITY || delete.getChangeType() == ChangeType.ADDING_ATTRIBUTE_MODIFIABILITY) {
			desc.append(StringUtils.capitalize("Remove final modifier of ") + delete.getRootEntity().getJavaStructureNode().getName().toString() + " " + delete.getRootEntity().getJavaStructureNode().getType().name().toString().toLowerCase());
		} else if(delete.getChangeType() == ChangeType.ALTERNATIVE_PART_DELETE) {
			desc.append(StringUtils.capitalize("Remove else part of ") + delete.getChangedEntity().getUniqueName() + " condition ");
		} else if(delete.getChangeType() == ChangeType.COMMENT_DELETE || delete.getChangeType() == ChangeType.DOC_DELETE) {
			String type = delete.getChangedEntity().getType().name().toLowerCase().replace("_", " ");
			String entityType = delete.getRootEntity().getJavaStructureNode().getType().name().toLowerCase();
			desc.append("Remove " + type +" at " + delete.getRootEntity().getJavaStructureNode().getName() + " " + entityType);
		} else if(delete.getChangeType() == ChangeType.REMOVED_FUNCTIONALITY) {
			String className = delete.getParentEntity().getName();
			String functionality = delete.getChangedEntity().getName().substring(0, delete.getChangedEntity().getName().indexOf("("));
			VerbPhrase phrase = new VerbPhrase(POSTagger.tag(Tokenizer.split(functionality)), className, null, false);
			phrase.generate();
			desc.append("Remove funtionality to " + phrase.toString());
		} else if(delete.getChangeType() == ChangeType.REMOVED_OBJECT_STATE) {
			desc.append("Remove object state " + delete.getChangedEntity().getName().substring(0, delete.getChangedEntity().getName().indexOf(":")));
		} else if(delete.getChangeType() == ChangeType.PARAMETER_DELETE) {
			if(delete.getChangedEntity().getAstNode() instanceof Argument) { 
				Argument arg = (Argument) delete.getChangedEntity().getAstNode(); 
				Parameter parameter = new Parameter(arg.type.toString(), new String(arg.name));
				ParameterPhrase phrase = new ParameterPhrase(parameter);
				phrase.generate();
				desc.append("Remove parameter " + phrase.toString() + " at " + delete.getRootEntity().getJavaStructureNode().getName() + " " + delete.getRootEntity().getJavaStructureNode().getType().name().toLowerCase());
			}
		}  
	}

	@SuppressWarnings("static-access")
	public static void describeInsert(StringBuilder desc, Insert insert) {
		
		String fType = insert.getChangedEntity().getType().name().toLowerCase().replace("_", " ");
		//String article = PhraseUtils.getIndefiniteArticle(fType).concat(" ");
		
		fType = "Add " + fType;
		
		if(insert.getChangeType() == ChangeType.ADDITIONAL_FUNCTIONALITY) {
			String className = insert.getParentEntity().getName();
			String functionality = insert.getChangedEntity().getName().substring(0, insert.getChangedEntity().getName().indexOf("("));
			VerbPhrase phrase = new VerbPhrase(POSTagger.tag(Tokenizer.split(functionality)), className, null, false);
			phrase.generate();
			if(insert.getChangedEntity().getAstNode() != null && !(insert.getChangedEntity().getAstNode() instanceof ConstructorDeclaration)) {
				desc.append("Add an additional functionality to " + phrase.toString());
			} else {
				desc.append("Add a constructor method");
			}
		} else if(insert.getChangeType() == ChangeType.ADDITIONAL_OBJECT_STATE) {
			desc.append("Add object state " + insert.getChangedEntity().getName().substring(0, insert.getChangedEntity().getName().indexOf(":")) + "");
		} else if(insert.getChangeType() == ChangeType.INCREASING_ACCESSIBILITY_CHANGE) {
			desc.append("Increasing accessibility change " + insert.getChangedEntity().toString().substring(insert.getChangedEntity().toString().indexOf(":") + 1) + "");
		} else if(insert.getChangeType() == ChangeType.COMMENT_INSERT || insert.getChangeType() == ChangeType.DOC_INSERT) {
			String entityType = insert.getRootEntity().getJavaStructureNode().getType().name().toLowerCase();
			desc.append(StringUtils.capitalize(fType) +" at " + insert.getRootEntity().getJavaStructureNode().getName() + " " + entityType);
		} else if(insert.getChangeType() == ChangeType.PARENT_CLASS_INSERT) {
			desc.append(StringUtils.capitalize("Add parent class ") + insert.getChangedEntity().getUniqueName());
		} else if(insert.getChangeType() == ChangeType.PARENT_INTERFACE_INSERT) {
			desc.append(StringUtils.capitalize("Add parent interface ") + insert.getChangedEntity().getUniqueName());
		} else if(insert.getChangeType() == ChangeType.REMOVING_METHOD_OVERRIDABILITY || insert.getChangeType() == ChangeType.REMOVING_ATTRIBUTE_MODIFIABILITY) {
			desc.append(StringUtils.capitalize("Add final modifier to ") + insert.getRootEntity().getJavaStructureNode().getName().toString() + " " + insert.getRootEntity().getJavaStructureNode().getType().name().toString().toLowerCase());
		} else if(insert.getChangeType() == ChangeType.ALTERNATIVE_PART_INSERT) {
			desc.append(StringUtils.capitalize("Add else part of ") + insert.getChangedEntity().getUniqueName() + " condition ");
		} else if(insert.getChangedEntity().getType() == JavaEntityType.METHOD_INVOCATION) {
			MessageSend methodC = (MessageSend) insert.getChangedEntity().getAstNode();
			String referencedObject = "";
			String object = "";
			if(methodC.receiver.toString().equals("")) {
				referencedObject = " to local method ";
			} else {
				referencedObject = " to method ";
				object = " of " + methodC.receiver.toString() + " object ";
			}
			
			desc.append(StringUtils.capitalize(fType) + referencedObject + new String(methodC.selector) + object + " at " + insert.getRootEntity().getJavaStructureNode().getName() + " method");
		} else if (insert.getChangeType() == ChangeType.PARAMETER_INSERT) {
			if(insert.getChangedEntity().getAstNode() instanceof Argument) { 
				Argument arg = (Argument) insert.getChangedEntity().getAstNode(); 
				Parameter parameter = new Parameter(arg.type.toString(), new String(arg.name));
				ParameterPhrase phrase = new ParameterPhrase(parameter);
				phrase.generate();
				desc.append("Add parameter " + phrase.toString() + " at " + insert.getRootEntity().getJavaStructureNode().getName() + " " + insert.getRootEntity().getJavaStructureNode().getType().name().toLowerCase());
			}
		} else if(insert.getChangeType() == ChangeType.STATEMENT_INSERT)  {
			desc.append(StringUtils.capitalize(fType) + " ");
			
			if(insert.getChangedEntity().getAstNode() instanceof PrefixExpression) {
				PrefixExpression prefixExpression = (PrefixExpression) insert.getChangedEntity().getAstNode();
				
				if(prefixExpression.PLUS == prefixExpression.operator) {
					desc.append(" to increment ");
				} else if(prefixExpression.MINUS == prefixExpression.operator) {
					desc.append(" to decrement ");
				}
				
				desc.append(" " + prefixExpression.lhs.toString());
				
			} else if(insert.getChangedEntity().getAstNode() != null && insert.getChangedEntity().getAstNode() instanceof CompoundAssignment) {
				CompoundAssignment statement = (CompoundAssignment) insert.getChangedEntity().getAstNode();
				desc.append(" " + statement.lhs.toString() + " variable to " + statement.expression.toString()+ " at " + insert.getRootEntity().getJavaStructureNode().getName() + " " + insert.getRootEntity().getJavaStructureNode().getType().name().toLowerCase());
			} else if(insert.getChangedEntity().getAstNode() != null && insert.getChangedEntity().getAstNode() instanceof Assignment) {
				Assignment statement = (Assignment) insert.getChangedEntity().getAstNode();
				desc.append(" to " + statement.lhs.toString() + " at " + insert.getRootEntity().getJavaStructureNode().getName() + " " + insert.getRootEntity().getJavaStructureNode().getType().name().toLowerCase());
			} else {
				fType = insert.getChangeType().name().toLowerCase().replace("_", " ");
				desc.append(" at " + insert.getRootEntity().getJavaStructureNode().getName() + " method");
			}
		}
	}

	public static void describeUpdate(StringBuilder desc,
			SourceCodeChange change, Update update) {
		String fType = "Modify " + update.getChangedEntity().getType().name().toLowerCase().replace("_", " ");
		if(fType.equals("Modify variable declaration statement")) {
			fType = "Modify variable declaration ";
		}
		if(update.getChangeType() == ChangeType.STATEMENT_UPDATE) {
			
			if(update.getChangedEntity().getType() == JavaEntityType.METHOD_INVOCATION) {
				desc.append(StringUtils.capitalize(fType));
				MessageSend methodC = (MessageSend) update.getChangedEntity().getAstNode();
				MessageSend methodN = (MessageSend) update.getNewEntity().getAstNode();
				
				if(!methodC.receiver.toString().equals(methodN.receiver.toString())) {
					String receiverA = (!methodC.receiver.toString().equals("")) ? new String(methodC.receiver.toString()) : new String(methodC.selector);
					//String receiverB = (!methodN.receiver.toString().equals("")) ? new String(methodN.receiver.toString()) : new String(methodN.selector);
					desc.append(" " + receiverA + " at " + update.getParentEntity().getName() + " method");
				} else if(!(new String(methodC.selector)).equals((new String(methodN.selector)))) {
					desc.append(new String(methodC.selector) + " at " + update.getParentEntity().getName() + " method");
				} else if(!methodC.arguments.equals(methodN.arguments)) {
					String name = !(new String(methodC.selector)).equals("") ? (new String(methodC.selector)) : methodC.receiver.toString();
					String methodName = update.getRootEntity().getUniqueName().substring(update.getRootEntity().getUniqueName().lastIndexOf(".") + 1, update.getRootEntity().getUniqueName().length());
					desc.replace(desc.lastIndexOf(fType), desc.lastIndexOf(fType) + fType.length(), "");
					desc.insert(0, "Modify parameters calling " + name + " method at " + methodName + " method");
				}
			} else if(update.getChangedEntity().getType() == JavaEntityType.ASSIGNMENT) {
				desc.append(StringUtils.capitalize(fType) + " ");
				Assignment asC = (Assignment) update.getChangedEntity().getAstNode();
				Assignment asN = (Assignment) update.getNewEntity().getAstNode();
				
				if(asC.lhs != asN.lhs) {
					desc.append(" of " + new String(asC.lhs.toString()) + " type");
					if(!update.getParentEntity().getName().equals("")) {
						desc.append(" at " + update.getParentEntity().getName() + " method");
					}
				} else if(asC.expression != asN.expression) {
					desc.append(" of " + new String(asC.expression.toString()) + " to " + new String(asN.expression.toString()));
					if(!update.getParentEntity().getName().equals("")) {
						desc.append(" at " + update.getParentEntity().getName() + " method");
					}
					
				}
			} else if(update.getChangedEntity().getAstNode() instanceof PrefixExpression) {
				desc.append(StringUtils.capitalize(fType));
				PrefixExpression prefixExpression = (PrefixExpression) update.getChangedEntity().getAstNode();
				if(OperatorIds.PLUS == prefixExpression.operator) {
					desc.append(" increment ");
				} else if(OperatorIds.MINUS == prefixExpression.operator) {
					desc.append(" decrement ");
				}
				desc.append(" " + prefixExpression.lhs.toString());
				//desc.append(" was added ");
			} else if(update.getChangedEntity().getAstNode() != null && update.getChangedEntity().getAstNode() instanceof ReturnStatement) {
				String beforeName = update.getChangedEntity().getName().replace(";", "");
				String afterName = update.getNewEntity().getUniqueName().replace(";", "");
				desc.append(StringUtils.capitalize(fType) + " " + beforeName + " with " + afterName);
				desc.append(" at " + update.getRootEntity().getJavaStructureNode().getName() + " " + update.getRootEntity().getJavaStructureNode().getType().name().toLowerCase());
			} else {
				String name = "";
				if(update.getChangedEntity().getAstNode() instanceof LocalDeclaration) {
					name = new String(((LocalDeclaration) update.getChangedEntity().getAstNode()).name);
				} else {
					name = update.getChangedEntity().getName();
				}
				desc.append(StringUtils.capitalize(fType) + " " + name);
				if(!update.getParentEntity().getName().equals("")) {
					desc.append(" at " + update.getParentEntity().getName()  + " method");
				}
			}
		} else if(update.getChangeType() == ChangeType.METHOD_RENAMING) {
			desc.append("Rename " + update.getChangedEntity().getName().substring(0, update.getChangedEntity().getName().indexOf("(")) + " method " + " with " + update.getNewEntity().getName().substring(0, update.getNewEntity().getName().indexOf("(")));
		} else if(update.getChangeType() == ChangeType.ATTRIBUTE_RENAMING) {
			desc.append("Rename " + update.getChangedEntity().getName().substring(0, update.getChangedEntity().getName().indexOf(":")).trim() + " object attribute " + " with " + update.getNewEntity().getName().substring(0, update.getNewEntity().getName().indexOf(":")).trim());
		} else if(update.getChangeType() == ChangeType.ATTRIBUTE_TYPE_CHANGE) {
			if(update.getChangedEntity().getAstNode() != null && update.getChangedEntity().getJavaStructureNode() != null) { 
				if(update.getChangedEntity().getJavaStructureNode().getASTNode() instanceof FieldDeclaration) {
			
					FieldDeclaration field = (FieldDeclaration) update.getChangedEntity().getJavaStructureNode().getASTNode();
					desc.append("Change attribute type of " + new String(field.name) + " with " + update.getNewEntity().getAstNode().toString());
				} else if(update.getChangedEntity().getJavaStructureNode().getASTNode() instanceof TrueLiteral) {
					System.out.println("hola");
				}
			} else {
				String name = (!update.getChangedEntity().getName().equals("")) ? update.getChangedEntity().getName() : update.getChangedEntity().getAstNode().toString();
				desc.append("Change attribute type " + name + " with " + update.getNewEntity().getAstNode().toString());
			}
		} else if(update.getChangeType() == ChangeType.CONDITION_EXPRESSION_CHANGE) {
			desc.append("Modify conditional expression " + update.getChangedEntity().getName().substring(1, update.getChangedEntity().getName().length() - 1) + " with " + update.getNewEntity().getUniqueName() + " at " + update.getParentEntity().getName() + " method");
		} else if(update.getChangeType() == ChangeType.INCREASING_ACCESSIBILITY_CHANGE) {
			String forValue = (update.getRootEntity().getJavaStructureNode().getName().indexOf(":") > - 1) ? update.getRootEntity().getJavaStructureNode().getName().substring(0, update.getRootEntity().getJavaStructureNode().getName().indexOf(":") - 1) : update.getRootEntity().getJavaStructureNode().getName(); 
			desc.append("Increase accessibility of " + update.getChangedEntity().getUniqueName() + " to " + update.getNewEntity().getUniqueName() + " for " + forValue + " " + update.getRootEntity().getType().name().toLowerCase());
		} else if(update.getChangeType() == ChangeType.PARENT_CLASS_CHANGE) {
			desc.append("Modify the " + "parent class " + update.getChangedEntity().getUniqueName() + " with " + update.getNewEntity().getUniqueName());
		} else if(update.getChangeType() == ChangeType.PARENT_INTERFACE_CHANGE) {
			desc.append("Modify the " + "parent interface " + update.getChangedEntity().getUniqueName() + " with " + update.getNewEntity().getUniqueName());
		} else if(update.getChangeType() == ChangeType.DECREASING_ACCESSIBILITY_CHANGE) {
			desc.append("Decrease accessibility of " + update.getChangedEntity().getUniqueName() + " to " + update.getNewEntity().getUniqueName() + " for " + update.getRootEntity().getJavaStructureNode().getName() + " " + update.getRootEntity().getType().name().toLowerCase());
		} else if(update.getChangeType() == ChangeType.COMMENT_UPDATE || update.getChangeType() == ChangeType.DOC_UPDATE) {
			String entityType = update.getRootEntity().getJavaStructureNode().getType().name().toLowerCase();
			desc.append(fType +" at " + update.getRootEntity().getJavaStructureNode().getName() + " " + entityType);
		} else if(update.getChangeType() == ChangeType.PARAMETER_TYPE_CHANGE) {
			desc.append("Type's " + update.getChangedEntity().getUniqueName().substring(0, update.getChangedEntity().getUniqueName().indexOf(":") - 1).trim() + " paramater change of " + update.getChangedEntity().getUniqueName().substring(update.getChangedEntity().getUniqueName().indexOf(":") + 1, update.getChangedEntity().getUniqueName().length()).trim()  + " to " + update.getNewEntity().getUniqueName().substring(update.getNewEntity().getUniqueName().indexOf(":") + 1, update.getNewEntity().getUniqueName().length()).trim() + " for " + update.getRootEntity().getJavaStructureNode().getName() + " " + update.getRootEntity().getType().name().toLowerCase());
		} else if(update.getChangeType() == ChangeType.RETURN_TYPE_CHANGE) {
			desc.append(fType + " " + update.getChangedEntity().getUniqueName().substring(update.getChangedEntity().getUniqueName().indexOf(":") + 1).trim() + " with " + update.getNewEntity().getUniqueName().substring(update.getNewEntity().getUniqueName().indexOf(":") + 1, update.getNewEntity().getUniqueName().length()).trim()  + " for " + update.getRootEntity().getJavaStructureNode().getName() + " " + update.getRootEntity().getType().name().toLowerCase());
		}
		
	}
	
	private static void compareModified(ChangedFile file, FileDistiller distiller,Git git) {
		File previousType = null;
		File currentType = null;
		
		try {
			previousType = Utils.getFileContentOfLastCommit(file.getPath(), git.getRepository());
			currentType = new File(file.getAbsolutePath());
			distiller.extractClassifiedSourceCodeChanges(previousType, currentType);
			
		} catch (RevisionSyntaxException e) {
			e.printStackTrace();
		} catch (AmbiguousObjectException e) {
			e.printStackTrace();
		} catch (IncorrectObjectTypeException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private static void compareModifiedVersions(ChangedFile file, FileDistiller distiller, Git git, String olderID, String currentID) {
		File previousType = null;
		File currentType = null;
		
		try {
			previousType = Utils.getFileContentOfCommitID(file.getPath(), git.getRepository(), olderID);
			currentType = Utils.getFileContentOfCommitID(file.getPath(), git.getRepository(), currentID);
			distiller.extractClassifiedSourceCodeChanges(previousType, currentType);
			
		} catch (RevisionSyntaxException e) {
			e.printStackTrace();
		} catch (AmbiguousObjectException e) {
			e.printStackTrace();
		} catch (IncorrectObjectTypeException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public List<SourceCodeChange> getChanges() {
		return changes;
	}

	public void setChanges(List<SourceCodeChange> changes) {
		this.changes = changes;
	}

	public ChangedFile getFile() {
		return file;
	}

	public void setFile(ChangedFile file) {
		this.file = file;
	}

	public Git getGit() {
		return git;
	}

	public void setGit(Git git) {
		this.git = git;
	}

}
