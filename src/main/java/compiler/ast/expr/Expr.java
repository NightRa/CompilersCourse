package compiler.ast.expr;

import compiler.pcode.PCodeGenable;
import compiler.ast.PCodeType;

/**
 * Code gen invariant: after code is executed, a single value is pushed into the stack with the value of the expression.
 **/
public abstract class Expr<A> implements PCodeGenable {
    public abstract PCodeType type();
    public abstract int precedence();
}
