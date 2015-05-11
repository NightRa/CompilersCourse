package compiler.ast.expr;

import compiler.ast.PCodeType;
import compiler.pcode.LabelGenerator;
import compiler.pcode.PCommand;
import compiler.pcode.SymbolTable;
import compiler.util.List;

/**
 * Code gen invariant: after code is executed, a single value is pushed into the stack with the value of the expression.
 **/
public abstract class Expr<A> {
    public abstract PCodeType type();
    public abstract int precedence();
    public abstract List<PCommand> evaluateExpr(SymbolTable symbolTable, LabelGenerator labelGenerator);
}
