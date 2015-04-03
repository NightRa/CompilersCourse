package compilers.ast.atom;

import compilers.ast.PCodeType;
import compilers.ast.expr.Expr;

public abstract class Atom<A> extends Expr<A> {
    public abstract PCodeType type();
}
