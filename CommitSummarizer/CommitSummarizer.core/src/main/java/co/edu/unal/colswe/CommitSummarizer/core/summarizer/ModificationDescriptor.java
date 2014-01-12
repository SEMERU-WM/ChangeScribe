package co.edu.unal.colswe.CommitSummarizer.core.summarizer;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.CompoundAssignment;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ForeachStatement;
import org.eclipse.jdt.internal.compiler.ast.IfStatement;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.OperatorIds;
import org.eclipse.jdt.internal.compiler.ast.PrefixExpression;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
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
import ch.uzh.ifi.seal.changedistiller.model.entities.Update;
import co.edu.unal.colswe.CommitSummarizer.core.git.ChangedFile;
import co.edu.unal.colswe.CommitSummarizer.core.textgenerator.phrase.NounPhrase;
import co.edu.unal.colswe.CommitSummarizer.core.textgenerator.phrase.Parameter;
import co.edu.unal.colswe.CommitSummarizer.core.textgenerator.phrase.ParameterPhrase;
import co.edu.unal.colswe.CommitSummarizer.core.textgenerator.tokenizer.Tokenizer;
import co.edu.unal.colswe.CommitSummarizer.core.util.Utils;

@SuppressWarnings("restriction")
public class ModificationDescriptor {

	public static void describe(ChangedFile file, Git git, int i, int j, StringBuilder desc) {
		FileDistiller distiller = ChangeDistiller.createFileDistiller(Language.JAVA); 
		try {
			compareModified(file, distiller, git);
		} catch(IllegalStateException ex) {
			ex.printStackTrace();
			desc.append((i - 1) + "." + j + ". " + " Rename the " + file.getName() + " file:  \n\n");
		}
		List<SourceCodeChange> changes = distiller.getSourceCodeChanges();
		if(changes != null) {
			if(changes != null && changes.size() > 0) {
				desc.append((i - 1) + "." + j + ". " + " Modifications to " + file.getName() + ":  \n\n");
			} 
			int k = 1;
		    for(SourceCodeChange change : changes) {
		    	desc.append("\t\t");
		    	desc.append((i - 1) + "." + j + "." + k + ". ");
		    	if(change instanceof Update) {
		    		Update update = (Update) change;
		    		describeUpdate(desc, change, update);
		    		
		    	} else if(change instanceof Insert) {
		    		Insert insert = (Insert) change;
		    		describeInsert(desc, insert); 
	    			
	    		} else if(change instanceof Delete) {
	    			Delete delete = (Delete) change;
	    			describeDelete(desc, delete);
	    		} else if(change instanceof Move) {
	    			
	    		} 
		    	desc.append("\n");
		    	k++;
		    }
		    desc.append("\n");
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
				NounPhrase phrase = new NounPhrase(Tokenizer.split(((MessageSend)forDec.collection).receiver.toString()));
				phrase.generate();
				desc.append(" loop for " + phrase.toString() + " collection at " + delete.getRootEntity().getJavaStructureNode().getName() + " method");
			} else if(delete.getChangedEntity().getAstNode() != null && delete.getChangedEntity().getAstNode() instanceof MessageSend) {
				MessageSend messageSend = (MessageSend) delete.getChangedEntity().getAstNode();
				NounPhrase phrase = new NounPhrase(Tokenizer.split(new String(messageSend.selector)));
				phrase.generate();
				desc.append(" to " + phrase.toString());
				if(messageSend.arguments != null && messageSend.arguments.length > 0) {
					phrase = new NounPhrase(Tokenizer.split(new String(((SingleNameReference)messageSend.arguments[0]).token)));
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
				desc.append(" at " + delete.getRootEntity().getJavaStructureNode().getName() + " " + delete.getRootEntity().getJavaStructureNode().getType().name().toLowerCase());
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
			desc.append("Remove funtionality to " + delete.getChangedEntity().getName().substring(0, delete.getChangedEntity().getName().indexOf("(")));
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
			desc.append("Add an additional functionality to " + insert.getChangedEntity().getName().substring(0, insert.getChangedEntity().getName().indexOf("(")));
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
				
				if(methodC.receiver != methodN.receiver) {
					desc.append(new String(methodC.receiver.toString()) + " to " + new String(methodN.receiver.toString()) + " at " + update.getParentEntity().getName() + " method");
				} else if(methodC.selector != methodN.selector) {
					desc.append(new String(methodC.selector.toString()) + " to " + new String(methodN.selector.toString()) + " at " + update.getParentEntity().getName() + " method");
				}
			} else if(update.getChangedEntity().getType() == JavaEntityType.ASSIGNMENT) {
				desc.append(StringUtils.capitalize(fType));
				Assignment asC = (Assignment) update.getChangedEntity().getAstNode();
				Assignment asN = (Assignment) update.getNewEntity().getAstNode();
				
				if(asC.lhs != asN.lhs) {
					desc.append(new String(asC.lhs.toString()) + " to " + new String(asN.lhs.toString()) + " at " + update.getParentEntity().getName() + " method");
				} else if(asC.expression != asN.expression) {
					desc.append(new String(asC.expression.toString()) + " to " + new String(asN.expression.toString()) + " at " + update.getParentEntity().getName() + " method");
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
			} else {
				desc.append(StringUtils.capitalize(fType) + " " + update.getChangedEntity().getName() + " by " + update.getNewEntity().getUniqueName() + " at " + update.getParentEntity().getName()  + " method");
			}
		} else if(update.getChangeType() == ChangeType.METHOD_RENAMING) {
			desc.append("Rename " + update.getChangedEntity().getName().substring(0, update.getChangedEntity().getName().indexOf("(")) + " method " + " with " + update.getNewEntity().getName().substring(0, update.getNewEntity().getName().indexOf("(")));
		} else if(update.getChangeType() == ChangeType.ATTRIBUTE_RENAMING) {
			desc.append("Rename " + update.getChangedEntity().getName().substring(0, update.getChangedEntity().getName().indexOf(":")).trim() + " attribute " + " with " + update.getNewEntity().getName().substring(0, update.getNewEntity().getName().indexOf(":")).trim());
		} else if(update.getChangeType() == ChangeType.ATTRIBUTE_TYPE_CHANGE) {
			if(update.getChangedEntity().getAstNode() != null && update.getChangedEntity().getJavaStructureNode().getASTNode() instanceof FieldDeclaration) {
				FieldDeclaration field = (FieldDeclaration) update.getChangedEntity().getJavaStructureNode().getASTNode();
				desc.append("Change attribute type of " + new String(field.name) + " with " + update.getNewEntity().getAstNode().toString());
			} else {
				desc.append("Change attribute type " + update.getChangedEntity().getName() + " with " + update.getNewEntity().getAstNode().toString());
			}
		} else if(update.getChangeType() == ChangeType.CONDITION_EXPRESSION_CHANGE) {
			desc.append("Modify conditional expression " + update.getChangedEntity().getName().substring(1, update.getChangedEntity().getName().length() - 1) + " with " + update.getNewEntity().getUniqueName() + " at " + update.getParentEntity().getName() + " method");
		} else if(update.getChangeType() == ChangeType.INCREASING_ACCESSIBILITY_CHANGE) {
			desc.append("Increase accessibility of " + update.getChangedEntity().getUniqueName() + " to " + update.getNewEntity().getUniqueName() + " for " + update.getRootEntity().getJavaStructureNode().getName().substring(0, update.getRootEntity().getJavaStructureNode().getName().indexOf(":") - 1) + " " + update.getRootEntity().getType().name().toLowerCase());
		} else if(update.getChangeType() == ChangeType.PARENT_CLASS_CHANGE) {
			desc.append("Modify the " + "parent class " + update.getChangedEntity().getUniqueName() + " with " + update.getNewEntity().getUniqueName());
		} else if(update.getChangeType() == ChangeType.PARENT_INTERFACE_CHANGE) {
			desc.append("Modify the " + "parent interface " + update.getChangedEntity().getUniqueName() + " with " + update.getNewEntity().getUniqueName());
		} else if(update.getChangeType() == ChangeType.DECREASING_ACCESSIBILITY_CHANGE) {
			desc.append("Decrease accessibility of " + update.getChangedEntity().getUniqueName() + " to " + update.getNewEntity().getUniqueName() + " for " + update.getRootEntity().getJavaStructureNode().getName() + " " + update.getRootEntity().getType().name().toLowerCase());
		} else if(update.getChangeType() == ChangeType.COMMENT_UPDATE || update.getChangeType() == ChangeType.DOC_UPDATE) {
			String entityType = update.getRootEntity().getJavaStructureNode().getType().name().toLowerCase();
			desc.append(fType +" at " + update.getRootEntity().getJavaStructureNode().getName() + " " + entityType);
		}
		
	}
	
	public static void compareModified(ChangedFile file, FileDistiller distiller,Git git) {
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

}
