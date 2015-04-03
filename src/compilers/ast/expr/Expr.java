package compilers.ast.expr;

import compilers.ast.PCodeType;

public abstract class Expr<A> {
    public abstract PCodeType type();
}
