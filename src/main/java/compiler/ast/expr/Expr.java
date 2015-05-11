package compiler.ast.expr;

import compiler.ast.PCodeType;
import compiler.pcode.LabelGenerator;
import compiler.pcode.PCommand;
import compiler.pcode.SymbolTable;
import compiler.util.List;

/**
 * Code gen invariant: after code is executed, a single value is pushed into the stack with the value of the expression.
 **/
public interface Expr<A> {
    PCodeType type();
    int precedence();
    List<PCommand> evaluateExpr(SymbolTable symbolTable, LabelGenerator labelGenerator);
}
