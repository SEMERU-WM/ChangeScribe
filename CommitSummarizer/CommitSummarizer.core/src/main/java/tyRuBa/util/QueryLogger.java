/*
 * Created on Jul 15, 2004
 */
package tyRuBa.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import tyRuBa.engine.RBCompoundTerm;
import tyRuBa.engine.RBConjunction;
import tyRuBa.engine.RBCountAll;
import tyRuBa.engine.RBDisjunction;
import tyRuBa.engine.RBExistsQuantifier;
import tyRuBa.engine.RBExpression;
import tyRuBa.engine.RBFindAll;
import tyRuBa.engine.RBIgnoredVariable;
import tyRuBa.engine.RBModeSwitchExpression;
import tyRuBa.engine.RBNotFilter;
import tyRuBa.engine.RBPair;
import tyRuBa.engine.RBPredicateExpression;
import tyRuBa.engine.RBQuoted;
import tyRuBa.engine.RBTemplateVar;
import tyRuBa.engine.RBTestFilter;
import tyRuBa.engine.RBTuple;
import tyRuBa.engine.RBUniqueQuantifier;
import tyRuBa.engine.RBVariable;
import tyRuBa.engine.visitor.ExpressionVisitor;
import tyRuBa.engine.visitor.TermVisitor;

public abstract class QueryLogger {
	
    public QueryLogger() {}
    
    public abstract void close(); 
    
    public abstract void logQuery(RBExpression query);
}
