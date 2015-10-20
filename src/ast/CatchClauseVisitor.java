package ast;

/**
 * Created by wayne on 15/10/16.
 */
public interface CatchClauseVisitor {
    Object forCatchClause(CatchClause catchClause, IdentifierExpression param, BlockStatement body);
}
