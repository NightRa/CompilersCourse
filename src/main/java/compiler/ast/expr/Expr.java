package compiler.ast.expr;

import compiler.ast.Type;
import compiler.pcode.PCommand;
import compiler.pcode.SymbolTable;
import compiler.util.List;

import java.util.Map;

/**
 * Code gen invariant: after code is executed, a single value is pushed into the stack with the value of the expression.
 **/
public abstract class Expr<A> {
    // Type as in the program, can be variable.
    public abstract Type rawType(Map<String, Type> typeTable);

    // Resolved type - can't be a variable.
    public Type.PType type(Map<String, Type> typeTable) {
        return Type.resolveIdentifier(typeTable, rawType(typeTable));
    }


    public abstract int precedence();
    public abstract List<PCommand> evaluateExpr(SymbolTable symbolTable, Map<String, Type> typeTable);
    public abstract A eval();
    public abstract boolean equals(Object obj);
}
