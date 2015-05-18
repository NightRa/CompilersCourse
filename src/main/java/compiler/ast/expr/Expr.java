package compiler.ast.expr;

import compiler.ast.PCodeType;
import compiler.pcode.PCommand;
import compiler.pcode.SymbolTable;
import compiler.util.List;

import java.util.Map;

/**
 * Code gen invariant: after code is executed, a single value is pushed into the stack with the value of the expression.
 **/
public abstract class Expr<A> {
    public abstract PCodeType rawType(Map<String, PCodeType> typeTable);
    public PCodeType type(Map<String, PCodeType> typeTable) {
        return PCodeType.resolveIdentifier(typeTable, rawType(typeTable));
    }


    public abstract int precedence();
    public abstract List<PCommand> evaluateExpr(SymbolTable symbolTable, Map<String, PCodeType> typeTable);
    public abstract A eval();
    public abstract boolean equals(Object obj);
}
