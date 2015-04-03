package compiler.ast.expr;

import compiler.ast.PCodeType;

public abstract class Expr<A> {
    public abstract PCodeType type();
    public abstract int precedence();
}
